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
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.RequestI;

/**
 * The class <code>TestsComputer</code> deploys a <code>Computer</code>
 * component connected to a <code>ComputerMonitor</code> component and then
 * execute one of two test scenarii on the simulated computer.
 *
 * <p><strong>Description</strong></p>
 * 
 * The two scenarii create a computer with one processor having two cores with
 * two levels of admissible frequencies. They then execute two tasks, one on
 * each core and respectively raise or lower the frequency of the first core
 * to test the dynamic adaptation of the task duration. In parallel, the
 * computer monitor starts the notification of the dynamic state of the
 * computer by requesting 25 pushes at the rate of one each second.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : April 15, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				TestsComputer
extends AbstractCVM
{
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;

	protected ComputerServicesOutboundPort	csPort ;
	protected ComputerMonitor				cm ;

	public				TestsComputer()
	throws Exception
	{
		super();
	}

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		String computerURI = "computer0" ;
		int numberOfProcessors = 1 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;
		admissibleFrequencies.add(3000) ;
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;
		processingPower.put(3000, 3000000) ;
		Computer c = new Computer(
							computerURI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1
							// 3000,	// Test scenario 2
							1500,
							numberOfProcessors,
							numberOfCores,
							ComputerServicesInboundPortURI,
							ComputerStaticStateDataInboundPortURI,
							ComputerDynamicStateDataInboundPortURI) ;
		c.toggleTracing() ;
		c.toggleLogging() ;
		this.addDeployedComponent(c) ;

		this.csPort = new ComputerServicesOutboundPort(
										ComputerServicesOutboundPortURI,
										new AbstractComponent(0, 0){}) ;
		this.csPort.publishPort() ;
		this.csPort.doConnection(
						ComputerServicesInboundPortURI,
						ComputerServicesConnector.class.getCanonicalName()) ;

		this.cm = new ComputerMonitor(
								computerURI,
								true,
								ComputerStaticStateDataOutboundPortURI,
								ComputerDynamicStateDataOutboundPortURI) ;
		cm.toggleTracing() ;
		cm.toggleLogging() ;
		this.addDeployedComponent(cm) ;
		this.cm.doPortConnection(ComputerStaticStateDataOutboundPortURI,
								 ComputerStaticStateDataInboundPortURI,
								 DataConnector.class.getCanonicalName()) ;

		this.cm.doPortConnection(ComputerDynamicStateDataOutboundPortURI,
								 ComputerDynamicStateDataInboundPortURI,
								 ControlledDataConnector.class.getCanonicalName());

		super.deploy() ;
	}

	@Override
	public void			start() throws Exception
	{
		super.start() ;
	}

	@Override
	public void			shutdown() throws Exception
	{
		this.csPort.doDisconnection() ;
		this.cm.doPortDisconnection(ComputerStaticStateDataOutboundPortURI) ;
		this.cm.doPortDisconnection(ComputerDynamicStateDataOutboundPortURI) ;

		super.shutdown();
	}

	public void			testScenario() throws Exception
	{
		AllocatedCore[] ac = this.csPort.allocateCores(2) ;

		final String processorServicesInboundPortURI =
			ac[0].processorInboundPortURI.get(ProcessorPortTypes.SERVICES) ;
		final String processorManagementInboundPortURI =
			ac[0].processorInboundPortURI.get(ProcessorPortTypes.MANAGEMENT) ;

		ProcessorServicesOutboundPort psPort =
			new ProcessorServicesOutboundPort(new AbstractComponent(0, 0) {}) ;
		psPort.publishPort() ;
		psPort.doConnection(
					processorServicesInboundPortURI,
					ProcessorServicesConnector.class.getCanonicalName()) ;

		ProcessorManagementOutboundPort pmPort =
			new ProcessorManagementOutboundPort(new AbstractComponent(0, 0) {}) ;
		pmPort.publishPort() ;
		pmPort.doConnection(
					processorManagementInboundPortURI,
					ProcessorManagementConnector.class.getCanonicalName()) ;

		System.out.println("starting mytask-001 on core 0") ;
		psPort.executeTaskOnCore(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 15000000000L;
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
				ac[0].coreNo) ;

		System.out.println("starting mytask-002 on core 1") ;
		psPort.executeTaskOnCore(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 30000000000L ;
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
				ac[1].coreNo) ;

		// Test scenario 1
		Thread.sleep(5000L) ;
		pmPort.setCoreFrequency(0, 3000) ;
		// Test scenario 2
		// Thread.sleep(3000L) ;
		// pmPort.setCoreFrequency(0, 1500) ;

		psPort.doDisconnection() ;
		pmPort.doDisconnection() ;
		psPort.unpublishPort() ;
		pmPort.unpublishPort() ;
	}

	public static void	main(String[] args)
	{
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestsComputer c = new TestsComputer() ;
			c.deploy() ;
			System.out.println("starting...") ;
			c.start() ;
			new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									c.testScenario() ;
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						}).start() ;
			Thread.sleep(25000L) ;
			System.out.println("shutting down...") ;
			c.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
