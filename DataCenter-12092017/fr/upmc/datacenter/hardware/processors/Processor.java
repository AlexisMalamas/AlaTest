package fr.upmc.datacenter.hardware.processors ;

//Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import java.util.HashMap ;
import java.util.HashSet ;
import java.util.Map ;
import java.util.Set ;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import fr.upmc.components.AbstractComponent ;
import fr.upmc.components.ComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorServicesNotificationConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorIntrospectionInboundPort ;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementInboundPort ;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesInboundPort ;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorStaticStateDataInboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllingI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>Processor</code> defines components that simulate processors
 * with a fixed number of cores capable of executing tasks and which can change
 * the frequencies of its cores.
 *
 * <p><strong>Description</strong></p>
 * 
 * Each core provide some computation power that depends upon its current
 * frequency.  All cores need not run at the same frequency, but a maximum
 * gap between any two core frequencies is imposed.  A processor has a default
 * frequency imposed to its cores when powering up.
 * 
 * Processors offer services, i.e. the capability to run tasks on their cores
 * through the port <code>ProcessorServicesInboundPort</code implementing the
 * interface <code>ProcessorServicesI</code> defining a method
 * <code>executeTaskOnCore</code> to execute a given task on a core of the
 * processor.
 * 
 * Running a task is a simulation. When a task is started on a core, the
 * duration of the task is computed in real time and the core object uses
 * the scheduled thread pool of the processor component to schedule the
 * execution of the method <code>endCurrentTask</code> on the core after that
 * duration. If the frequency of a non idle core is done during the execution
 * of a task, the scheduled <code>endCurrentTask</code> execution is cancelled,
 * a remaining duration is computed, and a new <code>endCurrentTask</code>
 * execution is scheduled accordingly.
 * 
 * Processors can also be introspected through a port
 * <code>ProcessorIntrospectionInboundPort</code> implementing the interface
 * <code>ProcessorIntrospectionI</code> that allows clients to know what are
 * the cores of the processor, their current frequencies and their admissible
 * frequencies.
 * 
 * Finally, processors can be managed through a port
 * <code>ProcessorManagementInboundPort</code> implementing the interface
 * <code>ProcessorManagementI</code> defining a method
 * <code>setCoreFrequency</code> to modify the frequency of a core.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		processorURI != null
 * invariant		admissibleFrequencies != null && forall i in admissibleFrequencies, i > 0
 * invariant		processingPower != null && forall i in processingPower.values(), i > 0
 * invariant		admissibleFrequencies.contains(defaultFrequency)
 * invariant		maxFrequencyGap >= 0 && forall i in possibleFrequencies, maxFrequencyGap <= i
 * invariant		numberOfCores > 0
 * invariant		servicesInboundPortURI != null
 * invariant		introspectionInboundPortURI != null
 * invariant		managementInboundPortURI != null
 * invariant		processorStaticStateDataInboundPortURI != null
 * invariant		processorDynamicStateDataInboundPortURI != null
 * </pre>
 * 
 * <p>Created on : January 15, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				Processor
extends		AbstractComponent
implements	PushModeControllingI
{
	public static boolean	DEBUG = false ;

	public static enum		ProcessorPortTypes {
		SERVICES, INTROSPECTION, MANAGEMENT, STATIC_STATE, DYNAMIC_STATE
	}

	// ------------------------------------------------------------------------
	// Component internal state
	// ------------------------------------------------------------------------

	protected String					processorURI ;
	/** Array of core objects.												*/
	protected final Core[]				cores ;
	/** Processor services inbound port.									*/
	protected ProcessorServicesInboundPort
										servicesInboundPort ;
	/** Port offering introspection upon the properties of the processor.	*/
	protected ProcessorIntrospectionInboundPort
										introspectionInboundPort ;
	/** Port offering management (actuation) services of the processor.		*/
	protected ProcessorManagementInboundPort
										processorManagementInboundPort ;
	protected ProcessorDynamicStateDataInboundPort
										processorDynamicStateDataInboundPort ;
	protected ProcessorStaticStateDataInboundPort
										processorStaticStateDataInboundPort ;
	/** Possible frequencies in MHz.										*/
	protected final Set<Integer>		admissibleFrequencies ;
	/** Default frequency of the cores, when powering up the processor.		*/
	protected final int					defaultFrequency ;
	/** Maximum gap between the current frequencies of the cores.			*/
	protected final int					maxFrequencyGap ;
	/** Map providing the processing power of the cores in number of
	 *  instructions that they can execute at each of their admissible
	 *  frequencies in MHz.													*/
	protected final HashMap<Integer,Integer>
										processingPower ;
	protected ScheduledFuture<?>		pushingFuture ;
	protected Map<TaskI,String>			notificationInboundPortURIs ;

	// ------------------------------------------------------------------------
	// Component constructor
	// ------------------------------------------------------------------------

	/**
	 * create a processor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	processorURI != null
	 * pre	admissibleFrequencies != null && forall i in admissibleFrequencies, i > 0
	 * pre	processingPower != null && forall i in processingPower.values(), i > 0
	 * pre	admissibleFrequencies.contains(defaultFrequency)
	 * pre	maxFrequencyGap >= 0 && forall i in possibleFrequencies, maxFrequencyGap <= i
	 * pre	numberOfCores > 0
	 * pre	servicesInboundPortURI != null
	 * pre	introspectionInboundPortURI != null
	 * pre	managementInboundPortURI != null
	 * pre	processorStaticStateDataInboundPortURI != null
	 * pre	processorDynamicStateDataInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param processorURI					URI of the processor component.
	 * @param admissibleFrequencies			admissible frequencies for cores.
	 * @param processingPower				Mips for the different admissible frequencies.
	 * @param defaultFrequency				default frequency at which the cores run.
	 * @param maxFrequencyGap				max frequency gap among cores of the same processor.
	 * @param numberOfCores					number of cores of the processor.
	 * @param servicesInboundPortURI		URI of the service inbound port of the processor.
	 * @param introspectionInboundPortURI	URI of the introspection inbound port of the processor.
	 * @param managementInboundPortURI		URI of the management inbound port of the processor.
	 * @param processorStaticStateDataInboundPortURI	URI of the static state notification inbound port of the processor.
	 * @param processorDynamicStateDataInboundPortURI	URI of the dynamic state notification inbound port of the processor.
	 * @throws Exception
	 */
	public				Processor(
		String processorURI,
		Set<Integer> admissibleFrequencies,
		Map<Integer, Integer> processingPower,
		int defaultFrequency,
		int maxFrequencyGap,
		int numberOfCores,
		String servicesInboundPortURI,
		String introspectionInboundPortURI,
		String managementInboundPortURI,
		String processorStaticStateDataInboundPortURI,
		String processorDynamicStateDataInboundPortURI
		) throws Exception
	{
		// The normal thread pool is used to process component services, while
		// the scheduled one is used to schedule the pushes of dynamic state
		// when requested.
		super(1, 1) ;

		// Preconditions
		boolean allPositive = true ;
		for(int f : admissibleFrequencies) {
			allPositive = allPositive && (f > 0) ;
		}
		assert	allPositive ;
		assert	admissibleFrequencies.contains(defaultFrequency) ;
		int max = -1 ;
		for(int f : admissibleFrequencies) {
			if (max < f) {
				max = f ;
			}
		}
		assert	maxFrequencyGap <= max ;
		assert	processingPower.keySet().containsAll(admissibleFrequencies) ;
		assert	numberOfCores > 0 ;

		this.processorURI = processorURI ;
		this.admissibleFrequencies =
							new HashSet<Integer>(admissibleFrequencies.size()) ;
		for(int f : admissibleFrequencies) {
			this.admissibleFrequencies.add(f) ;
		}
		this.processingPower =
			new HashMap<Integer,Integer>(this.admissibleFrequencies.size()) ;
		for(int f : processingPower.keySet()) {
			this.processingPower.put(f, processingPower.get(f)) ;
		}
		this.defaultFrequency = defaultFrequency ;
		this.maxFrequencyGap = maxFrequencyGap ;

		this.cores = new Core[numberOfCores] ;
		for(int i = 0 ; i < numberOfCores ; i++) {
			this.cores[i] =
				new Core(this, i, admissibleFrequencies, processingPower,
						 defaultFrequency) ;
		}

		this.addOfferedInterface(ProcessorServicesI.class) ;
		this.servicesInboundPort =
			new ProcessorServicesInboundPort(servicesInboundPortURI, this) ;
		this.addPort(this.servicesInboundPort) ;
		this.servicesInboundPort.publishPort() ;

		this.addOfferedInterface(ProcessorIntrospectionI.class) ;
		this.introspectionInboundPort =
			new ProcessorIntrospectionInboundPort(introspectionInboundPortURI,
												  this) ;
		this.addPort(this.introspectionInboundPort) ;
		this.introspectionInboundPort.publishPort() ;

		this.addOfferedInterface(ProcessorManagementI.class) ;
		this.processorManagementInboundPort =
				new ProcessorManagementInboundPort(managementInboundPortURI,
												   this) ;
		this.addPort(this.processorManagementInboundPort) ;
		this.processorManagementInboundPort.publishPort() ;

		this.addOfferedInterface(DataOfferedI.PullI.class) ;
		this.addRequiredInterface(DataOfferedI.PushI.class) ;
		this.processorStaticStateDataInboundPort =
			new ProcessorStaticStateDataInboundPort(
								processorStaticStateDataInboundPortURI, this) ;
		this.addPort(this.processorStaticStateDataInboundPort) ;
		this.processorStaticStateDataInboundPort.publishPort() ;

		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class) ;
		this.processorDynamicStateDataInboundPort =
			new ProcessorDynamicStateDataInboundPort(
								processorDynamicStateDataInboundPortURI, this) ;
		this.addPort(this.processorDynamicStateDataInboundPort) ;
		this.processorDynamicStateDataInboundPort.publishPort() ;

		this.pushingFuture = null ;
		this.notificationInboundPortURIs = new HashMap<TaskI,String>() ;
	}

	// ------------------------------------------------------------------------
	// Component life-cycle
	// ------------------------------------------------------------------------

	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			if (this.processorStaticStateDataInboundPort.connected()) {
				this.processorStaticStateDataInboundPort.doDisconnection() ;
			}
			if (this.processorDynamicStateDataInboundPort.connected()) {
				this.processorDynamicStateDataInboundPort.doDisconnection() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(
								"processor ports disconnection error.", e) ;
		}

		super.shutdown();
	}

	// ------------------------------------------------------------------------
	// Component introspection services (ProcessorIntrospectionI)
	// ------------------------------------------------------------------------

	/**
	 * return the number of cores in this processor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return > 0
	 * </pre>
	 *
	 * @return	the number of cores in this processor.
	 * @throws Exception
	 */
	public int			getNumberOfCores()
	throws Exception
	{
		return this.cores.length ;
	}

	/**
	 * returns the default frequency for the cores of this processor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return > 0
	 * </pre>
	 *
	 * @return	the default frequency for the cores of this processor.
	 * @throws Exception
	 */
	public int			getDefaultFrequency()
	throws Exception
	{
		return this.defaultFrequency ;
	}

	/**
	 * return the maximum gap tolerated between any two cores of this processor
	 * at any time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return >= 0
	 * </pre>
	 *
	 * @return	the maximum gap tolerated between any two cores of this processor at any time.
	 * @throws Exception
	 */
	public int			getMaxFrequencyGap()
	throws Exception
	{
		return this.maxFrequencyGap ;
	}

	/**
	 * return true if coreNo is a valid number for a core on this processor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo		number of a core to be tested.
	 * @return				true if coreNo is a valid number for a core.
	 * @throws Exception
	 */
	public boolean		isValidCoreNo(int coreNo)
	throws Exception
	{
		return coreNo >= 0 && coreNo < this.cores.length ;
	}

	/**
	 * return true if frequency is admissible for cores on this processor, and
	 * false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param frequency		frequency to be tested.
	 * @return				true if frequency is admissible for cores.
	 * @throws Exception
	 */
	public boolean		isAdmissibleFrequency(int frequency)
	throws Exception
	{
		return this.admissibleFrequencies.contains(frequency) ;
	}

	/**
	 * return true if frequency is currently a possible choice for the given
	 * core, and false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isValidCoreNo(coreNo) && this.isAdmissibleFrequency(frequency)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo		number of the core to be tested.
	 * @param frequency		frequency to be tested.
	 * @return				true if frequency is possible on coreNo.
	 * @throws Exception
	 */
	public boolean		isCurrentlyPossibleFrequencyForCore(
		int coreNo,
		int frequency
		) throws Exception
	{
		assert	this.isValidCoreNo(coreNo) ;
		assert	this.isAdmissibleFrequency(frequency) ;

		boolean ret = true ;
		for(int i = 0 ; i < this.cores.length ; i++) {
			if (i != coreNo &&
					Math.abs(frequency - this.cores[i].getCurrentFrequency())
													> this.maxFrequencyGap) {
				ret = false ;
			}
		}
		return ret ;
	}

	// ------------------------------------------------------------------------
	// Component introspection services (ProcessorStaticStateDataI)
	// ------------------------------------------------------------------------

	/**
	 * return the static state of the processor as an instance of
	 * <code>ProcessorStaticStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the static state of the processor.
	 * @throws Exception
	 */
	public ProcessorStaticStateI	getStaticState() throws Exception
	{
		return new ProcessorStaticState(this.getNumberOfCores(),
										this.defaultFrequency,
										this.maxFrequencyGap,
										this.admissibleFrequencies,
										this.processingPower) ;
	}

	/**
	 * get the static state and push it through the static state data inbound
	 * port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void						sendStaticState() throws Exception
	{
		if (this.processorStaticStateDataInboundPort.connected()) {
			ProcessorStaticStateI pss = this.getStaticState() ;
			this.processorStaticStateDataInboundPort.send(pss) ;
		}
	}

	// ------------------------------------------------------------------------
	// Component introspection services (ProcessorDynamicStateDataI)
	// ------------------------------------------------------------------------

	/**
	 * return the dynamic state of the processor as an instance of
	 * <code>ProcessorDynamicStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the dynamic state of the processor.
	 * @throws Exception
	 */
	public ProcessorDynamicStateI	getDynamicState() throws Exception
	{
		int numberOfCores = this.getNumberOfCores() ;
		boolean[] coresIdleStatus = new boolean[numberOfCores] ;
		int[] currentCoreFrequencies = new int[numberOfCores] ;
		for (int i = 0 ; i < numberOfCores ; i++) {
			coresIdleStatus[i] = this.cores[i].isIdle() ;
			currentCoreFrequencies[i] = this.cores[i].getCurrentFrequency() ;
		}
		return new ProcessorDynamicState(coresIdleStatus,
										 currentCoreFrequencies) ;
	}

	/**
	 * get once the dynamic state and push it through the dynamic state data
	 * inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			sendDynamicState() throws Exception
	{
		ProcessorDynamicStateI pds = this.getDynamicState() ;
		this.processorDynamicStateDataInboundPort.send(pds) ;
	}

	/**
	 * get the dynamic state and push it through the dynamic state data
	 * inbound port, and repeat the process for
	 * <code>numberOfRemainingPushes - 1 </code> times.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	interval > 0 && numberOfRemainingPushes > 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param interval					delay between pushes.
	 * @param numberOfRemainingPushes	number of pushes remaining to be done.
	 * @throws Exception
	 */
	public void			sendDynamicState(
		final int interval,
		int numberOfRemainingPushes
		) throws Exception
	{
		if (Processor.DEBUG) {
			this.logMessage("Processor>>sendDynamicState(" + interval +
							", " + numberOfRemainingPushes + ")") ;
		}

		assert	interval > 0 && numberOfRemainingPushes > 0 ;

		this.sendDynamicState() ;
		final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1 ;
		if (fNumberOfRemainingPushes > 0) {
			final Processor p = this ;
			this.pushingFuture =
					this.scheduleTask(
							new ComponentI.ComponentTask() {
								@Override
								public void run() {
									try {
										p.sendDynamicState(
												interval,
												fNumberOfRemainingPushes) ;
									} catch (Exception e) {
										throw new RuntimeException(e) ;
									}
								}
							},
							TimeManagement.acceleratedDelay(interval),
							TimeUnit.MILLISECONDS) ;
		}
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void			startUnlimitedPushing(int interval) throws Exception
	{
		// first, send the static state if the corresponding port is connected
		this.sendStaticState() ;

		// schedule the first dynamic data push
		final Processor p = this ;
		this.pushingFuture =
			this.scheduleTaskAtFixedRate(
					new ComponentI.ComponentTask() {
						@Override
						public void run() {
							try {
								p.sendDynamicState() ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int, int)
	 */
	@Override
	public void			startLimitedPushing(final int interval, final int n)
	throws Exception
	{
		assert	n > 0 ;

		if (Processor.DEBUG) {
			this.logMessage("startLimitedPushing with interval " + interval +
							" ms for " + n + " times.") ;
		}

		// first, send the static state if the corresponding port is connected
		this.sendStaticState() ;

		// schedule the first dynamic data push
		final Processor p = this ;
		this.pushingFuture =
			this.scheduleTask(
					new ComponentI.ComponentTask() {
						@Override
						public void run() {
							try {
								p.sendDynamicState(interval, n) ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;

	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void			stopPushing() throws Exception
	{
		// cancel the next scheduled push, if any.
		if (this.pushingFuture != null &&
						!(this.pushingFuture.isCancelled() ||
											this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false) ;
		}
	}

	// ------------------------------------------------------------------------
	// Component services (ProcessorServicesI)
	// ------------------------------------------------------------------------

	/**
	 * execute a task on a given core.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	task != null
	 * pre	this.isValidCoreNo(coreNo) && this.cores[coreNo].isIdle()
	 * post	!this.cores[coreNo].isIdle()
	 * </pre>
	 *
	 * @param task			task to be executed.
	 * @param coreNo		core number on which the task is to be executed.
	 * @throws Exception
	 */
	public void			executeTaskOnCore(TaskI task, int coreNo)
	throws Exception
	{
		assert	this.isValidCoreNo(coreNo) ;
		assert	this.cores[coreNo].isIdle() ;

		if (Processor.DEBUG) {
			this.logMessage("processor execute task on core " + coreNo) ;
		}
		this.cores[coreNo].startTask(task) ;
	}
	/**
	 * execute a task on a given core and notify the end of the task through
	 * a port which URI is given.
	 * 
	 * The port which URI is given must implement the interface
	 * <code>ProcessorServicesNotificationI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	task != null && notificationPortURI != null
	 * pre	this.isValidCoreNo(coreNo) && this.cores[coreNo].isIdle()
	 * post	!this.cores[coreNo].isIdle()
	 * </pre>
	 *
	 * @param task			task to be executed.
	 * @param coreNo		core number on which the task is to be executed.
	 * @param notificationInboundPortURI URI of the inbound port to notify.
	 * @throws Exception
	 */
	public void			executeTaskOnCoreAndNotify(
		TaskI task,
		int coreNo,
		String notificationInboundPortURI
		) throws Exception
	{
		assert	task != null ;
		assert	this.isValidCoreNo(coreNo) ;
		assert	this.cores[coreNo].isIdle() ;

		if (Processor.DEBUG) {
			this.logMessage("processor execute task on core " + coreNo +
							" with notification port URI " +
												notificationInboundPortURI) ;
		}
		this.notificationInboundPortURIs.put(task, notificationInboundPortURI) ;
		this.cores[coreNo].startTask(task) ;
	}

	/**
	 * process an end of task event triggered by a core.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t				task which just ended.
	 * @throws Exception
	 */
	public void			endOfTask(final TaskI t)
	throws Exception
	{
		assert	t != null ;

		String notificationInboundPortURI =
									this.notificationInboundPortURIs.remove(t) ;
		if (notificationInboundPortURI != null) {
			try {
				ProcessorServicesNotificationOutboundPort p =
						new ProcessorServicesNotificationOutboundPort(this) ;
				p.publishPort() ;
				p.doConnection(
					notificationInboundPortURI,
					ProcessorServicesNotificationConnector.class.
													getCanonicalName()) ;
				p.notifyEndOfTask(t) ;
				p.doDisconnection() ;
				p.unpublishPort() ;
			} catch (Exception e) {
				this.logMessage("Processor " + this.processorURI +
					" could not create, publish, connect, disconnect or" +
					" unpublish notification outbound port in endTask (" +
					e.getMessage() + ").") ;
				throw e ;
			}
		}
	}

	// ------------------------------------------------------------------------
	// Component management services (ProcessorManagementI)
	// ------------------------------------------------------------------------

	/**
	 * set a new frequency for a given core on this processor; exceptions are
	 * raised if the required frequency is not admissible for this processor
	 * or not currently possible for the given core.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isValidCoreNo(coreNo)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo		number of the core to be modified.
	 * @param frequency		new frequency for the given core.
	 * @throws UnavailableFrequencyException
	 * @throws UnacceptableFrequencyException
	 * @throws Exception
	 */
	public void			setCoreFrequency(int coreNo, int frequency)
	throws	UnavailableFrequencyException,
			UnacceptableFrequencyException,
			Exception
	{
		assert	this.isValidCoreNo(coreNo) ;

		if (Processor.DEBUG) {
			this.logMessage("Processor>>setCoreFrequency(" + 
											coreNo + ", " + frequency + ")") ;
		}

		if (!this.isAdmissibleFrequency(frequency)) {
			throw new UnavailableFrequencyException(frequency) ;
		}
		if (!this.isCurrentlyPossibleFrequencyForCore(coreNo, frequency)) {
			throw new UnacceptableFrequencyException(frequency) ;
		}

		this.cores[coreNo].setFrequency(frequency) ;
	}
}
