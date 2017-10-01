package fr.upmc.datacenter.hardware.tests;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorIntrospectionConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.RequestI;

/**
 * The class <code>TestsProcessor</code> deploys a <code>Processor</code>
 * component connected to a <code>ProcessorMonitor</code> component and then
 * execute one of two test scenarii on the simulated processor.
 *
 * <p><strong>Description</strong></p>
 * 
 * The two scenarii create a processor with two cores having two levels of
 * admissible frequencies. They then execute two tasks, one on each core and
 * respectively raise or lower the frequency of the first core to test the
 * dynamic adaptation of the task duration. In parallel, the processor
 * monitor starts the notification of the dynamic state of the processor
 * by requesting 25 pushes at the rate of one each second.
 * 
 * One scenario is activated by uncommenting its lines and commenting the
 * other's ones.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : January 19, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				TestsProcessor
extends		AbstractCVM
{
	public static final String	ProcessorServicesInboundPortURI = "ps-ibp" ;
	public static final String	ProcessorServicesOutboundPortURI = "ps-obp" ;
	public static final String	ProcessorServicesNotificationInboundPortURI = "psn-ibp" ;
	public static final String	ProcessorIntrospectionInboundPortURI = "pi-ibp" ;
	public static final String	ProcessorIntrospectionOutboundPortURI = "pi-obp" ;
	public static final String	ProcessorManagementInboundPortURI = "pm-ibp" ;
	public static final String	ProcessorManagementOutboundPortURI = "pm-obp" ;
	public static final String	ProcessorStaticStateDataInboundPortURI = "pss-dip" ;
	public static final String	ProcessorStaticStateDataOutboundPortURI = "pss-dop" ;
	public static final String	ProcessorDynamicStateDataInboundPortURI = "pds-dip" ;
	public static final String	ProcessorDynamicStateDataOutboundPortURI = "pds-dop" ;

	protected Processor								proc ;
	protected ProcessorServicesOutboundPort			psPort ;
	protected ProcessorIntrospectionOutboundPort	piPort ;
	protected ProcessorManagementOutboundPort		pmPort ;
	protected ProcessorStaticStateDataOutboundPort	pssPort ;
	protected ProcessorDynamicStateDataOutboundPort	pdsPort ;
	protected ProcessorMonitor						pm ;

	public			TestsProcessor()
	throws Exception
	{
		super();
	}

	@Override
	public void		deploy() throws Exception
	{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		String processorURI = "processor0" ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;
		admissibleFrequencies.add(3000) ;
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;
		processingPower.put(3000, 3000000) ;
		this.proc = new Processor(processorURI,
								  admissibleFrequencies,
								  processingPower,
								  1500,		// Test scenario 1
								  // 3000,	// Test scenario 2
								  1500,
								  2,
								  ProcessorServicesInboundPortURI,
								  ProcessorIntrospectionInboundPortURI,
								  ProcessorManagementInboundPortURI,
								  ProcessorStaticStateDataInboundPortURI,
								  ProcessorDynamicStateDataInboundPortURI) ;
		this.proc.toggleTracing() ;
		this.proc.toggleLogging() ;
		this.addDeployedComponent(this.proc) ;

		ComponentI nullComponent = new AbstractComponent(0, 0) {} ;
		this.psPort =
			new ProcessorServicesOutboundPort(ProcessorServicesOutboundPortURI,
											  nullComponent) ;
		this.psPort.publishPort() ;
		this.psPort.doConnection(
				ProcessorServicesInboundPortURI,
				ProcessorServicesConnector.class.getCanonicalName()) ;

		this.piPort =
			new ProcessorIntrospectionOutboundPort(
								ProcessorIntrospectionOutboundPortURI,
								nullComponent) ;
		this.piPort.publishPort() ;
		this.piPort.doConnection(
				ProcessorIntrospectionInboundPortURI,
				ProcessorIntrospectionConnector.class.getCanonicalName()) ;

		this.pmPort = new ProcessorManagementOutboundPort(
								ProcessorManagementOutboundPortURI,
								nullComponent) ;
		this.pmPort.publishPort() ;
		this.pmPort.doConnection(
				ProcessorManagementInboundPortURI,
				ProcessorManagementConnector.class.getCanonicalName()) ;

		this.pm = new ProcessorMonitor(
						processorURI,
						false,
						ProcessorServicesNotificationInboundPortURI,
						ProcessorStaticStateDataOutboundPortURI,
						ProcessorDynamicStateDataOutboundPortURI) ;
		this.addDeployedComponent(pm) ;
		pm.toggleLogging() ;
		pm.toggleTracing() ;

		this.pm.doPortConnection(
					ProcessorStaticStateDataOutboundPortURI,
					ProcessorStaticStateDataInboundPortURI,
					DataConnector.class.getCanonicalName()) ;

		this.pm.doPortConnection(
					ProcessorDynamicStateDataOutboundPortURI,
					ProcessorDynamicStateDataInboundPortURI,
					ControlledDataConnector.class.getCanonicalName());

		super.deploy() ;
	}

	@Override
	public void		start() throws Exception
	{
		super.start() ;

		System.out.println("0 isValidCoreNo: " + this.piPort.isValidCoreNo(0)) ;
		System.out.println("3000 isAdmissibleFrequency: " +
					this.piPort.isAdmissibleFrequency(3000)) ;
		System.out.println("3000 is CurrentlyPossibleFrequencyForCore 0: " +
					this.piPort.isCurrentlyPossibleFrequencyForCore(0, 3000)) ;

		this.psPort.executeTaskOnCoreAndNotify(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 30000000000L;
							}

							@Override
							public String getRequestURI() {
								return "r0" ;
							}
						};
					}

					@Override
					public String getTaskURI() {
						return "mytask-001";
					}
				},
				0,
				ProcessorServicesNotificationInboundPortURI) ;

		this.psPort.executeTaskOnCoreAndNotify(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 45000000000L ;
							}

							@Override
							public String getRequestURI() {
								return "r1" ;
							}
						};
					}
					@Override
					public String getTaskURI() {
						return "mytask-002";
					}
				},
				1,
				ProcessorServicesNotificationInboundPortURI) ;

		// Test scenario 1
		Thread.sleep(10000L) ;
		this.pmPort.setCoreFrequency(0, 3000) ;
		// Test scenario 2
		// Thread.sleep(5000L) ;
		// this.pmPort.setCoreFrequency(0, 1500) ;
	}

	@Override
	public void		shutdown() throws Exception
	{
		this.psPort.doDisconnection() ;
		this.piPort.doDisconnection() ;
		this.pmPort.doDisconnection() ;

		super.shutdown();
	}

	public static void	main(String[] args)
	{
		// AbstractCVM.toggleDebugMode() ;
		try {
			AbstractCVM c = new TestsProcessor() ;
			c.deploy() ;
			System.out.println("starting...") ;
			c.start() ;
			Thread.sleep(30000L) ;
			System.out.println("shutting down...") ;
			c.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
