package fr.upmc.datacenter.software.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.upmc.components.examples.ddeployment_cs.components.DynamicAssembler;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class TestDispatcher 
extends AbstractCVM{

	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	// Predefined URI of the different ports visible at the component assembly
	// level.
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;
	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;

	public static final String	DispatcherRequestSubmissionInboundPortURI = "drsip" ;
	public static final String	DispatcherRequestSubmissionOutboundPortURI = "drsop" ;
	public static final String	DispatcherRequestSubmissionOutboundPortURI2 = "drsop2" ;
	public static final String	DispatcherRequestNotificationInboundPortURI = "drnip" ;
	public static final String	DispatcherRequestNotificationInboundPortURI2 = "drnip2" ;
	public static final String	DispatcherRequestNotificationOutboundPortURI = "drnop" ;
	public static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	public static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;
	public static final String	VmRequestNotificationOutboundPortURI = "vmrnop" ;
	public static final String	VmRequestSubmissionInboundPortURI = "vmsip" ;
	public static final String	VmRequestNotificationOutboundPortURI2 = "vmrnop2" ;
	public static final String	VmRequestSubmissionInboundPortURI2 = "vmsip2" ;

	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;


	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort			csPort ;
	/** 	Computer monitor component.										*/
	protected ComputerMonitor						cm ;
	/** 	Application virtual machine component.							*/
	protected ApplicationVM							vm ;
	protected ApplicationVM							vm2 ;
	/** 	Request generator component.										*/
	protected RequestGenerator						rg ;
	/** Dispatcher			*/
	protected Dispatcher						    ds ;
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ApplicationVMManagementOutboundPort	avmPort ;
	protected ApplicationVMManagementOutboundPort	avmPort2 ;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;


	protected DynamicAssembler	darg ;

	protected AbstractCVM	cvm ;


	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public				TestDispatcher()
			throws Exception
	{
		super();
	}

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		Computer c = new Computer(
				computerURI,
				admissibleFrequencies,
				processingPower,  
				1500,		// Test scenario 1, frequency = 1,5 GHz
				// 3000,	// Test scenario 2, frequency = 3 GHz
				1500,		// max frequency gap within a processor
				numberOfProcessors,
				numberOfCores,
				ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI,
				ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;

		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		this.csPort = new ComputerServicesOutboundPort(
				ComputerServicesOutboundPortURI,
				new AbstractComponent(0, 0) {}) ;
		this.csPort.publishPort() ;
		this.csPort.doConnection(
				ComputerServicesInboundPortURI,
				ComputerServicesConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.cm = new ComputerMonitor(computerURI,
				true,
				ComputerStaticStateDataOutboundPortURI,
				ComputerDynamicStateDataOutboundPortURI) ;
		this.addDeployedComponent(this.cm) ;
		this.cm.doPortConnection(
				ComputerStaticStateDataOutboundPortURI,
				ComputerStaticStateDataInboundPortURI,
				DataConnector.class.getCanonicalName()) ;

		this.cm.doPortConnection(
				ComputerDynamicStateDataOutboundPortURI,
				ComputerDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create an Application VM component
		// --------------------------------------------------------------------
		this.vm = new ApplicationVM("vm1",	// application vm component URI
				ApplicationVMManagementInboundPortURI,
				VmRequestSubmissionInboundPortURI,
				VmRequestNotificationOutboundPortURI) ;
		this.addDeployedComponent(this.vm) ;

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort2 = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent(0, 0) {}) ;
		this.avmPort2.publishPort() ;
		this.avmPort2.
		doConnection(
				ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm.toggleTracing() ;
		this.vm.toggleLogging() ;

		this.vm2 = new ApplicationVM("vm2",	// application vm component URI
				ApplicationVMManagementInboundPortURI,
				VmRequestSubmissionInboundPortURI2,
				VmRequestNotificationOutboundPortURI2) ;
		this.addDeployedComponent(this.vm2) ;

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent(0, 0) {}) ;
		this.avmPort.publishPort() ;
		this.avmPort.
		doConnection(
				ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm2.toggleTracing() ;
		this.vm2.toggleLogging() ;
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------


		this.rg = new RequestGenerator(
					"rg",			// generator component URI
					500.0,			// mean time between two requests
					6000000000L,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					GeneratorRequestSubmissionOutboundPortURI,
					GeneratorRequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		this.rg.toggleTracing() ;
		this.rg.toggleLogging() ;


		// --------------------------------------------------------------------
		// Creating the dispatcher component.
		// --------------------------------------------------------------------

		ArrayList<String> dispatcherOutboundPortList = new ArrayList<String>();
		dispatcherOutboundPortList.add(DispatcherRequestSubmissionOutboundPortURI);
		dispatcherOutboundPortList.add(DispatcherRequestSubmissionOutboundPortURI2);

		ArrayList<String> dispatcherRequestNotificationInboundPortList = new ArrayList<String>();
		dispatcherRequestNotificationInboundPortList.add(DispatcherRequestNotificationInboundPortURI);
		dispatcherRequestNotificationInboundPortList.add(DispatcherRequestNotificationInboundPortURI2);

		this.ds = new Dispatcher("ds", DispatcherRequestSubmissionInboundPortURI, dispatcherOutboundPortList,
				DispatcherRequestNotificationOutboundPortURI, dispatcherRequestNotificationInboundPortList);
		this.addDeployedComponent(ds) ;

		this.ds.toggleTracing() ;
		this.ds.toggleLogging() ;






		// --------------------------------------------------------------------


		// Connecting the request generator to the application virtual machine.
		// Request generators have three different interfaces:
		// - one for submitting requests to application virtual machines,
		// - one for receiving end of execution notifications from application
		//   virtual machines, and
		// - one for request generation management i.e., starting and stopping
		//   the generation process.
		this.rg.doPortConnection(
				GeneratorRequestSubmissionOutboundPortURI,
				DispatcherRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;

		this.ds.doPortConnection(
				DispatcherRequestNotificationOutboundPortURI,
				GeneratorRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;

		this.vm.doPortConnection(
				VmRequestNotificationOutboundPortURI,
				DispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;

		this.ds.doPortConnection(
				DispatcherRequestSubmissionOutboundPortURI,
				VmRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;

		this.ds.doPortConnection(
				DispatcherRequestSubmissionOutboundPortURI2,
				VmRequestSubmissionInboundPortURI2,
				RequestSubmissionConnector.class.getCanonicalName()) ;

		this.vm2.doPortConnection(
				VmRequestNotificationOutboundPortURI2,
				DispatcherRequestNotificationInboundPortURI2,
				RequestNotificationConnector.class.getCanonicalName()) ;

		// Create a mock up port to manage to request generator component
		// (starting and stopping the generation).
		this.rgmop = new RequestGeneratorManagementOutboundPort(
				RequestGeneratorManagementOutboundPortURI,
				new AbstractComponent(0, 0) {}) ;
		this.rgmop.publishPort() ;
		this.rgmop.doConnection(
				RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------


		// complete the deployment at the component virtual machine level.
		super.deploy();
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#start()
	 */
	@Override
	public void			start() throws Exception
	{
		super.start() ;

		// Allocate the 4 cores of the computer to the application virtual
		// machine.
		AllocatedCore[] ac = this.csPort.allocateCores(2) ;
		this.avmPort.allocateCores(ac) ;

		AllocatedCore[] ac2 = this.csPort.allocateCores(2) ;
		this.avmPort2.allocateCores(ac2) ;

		/*DynamicAssembler fDa = this.darg ;
		this.darg.runTask(
			new ComponentI.ComponentTask() {
					@Override
					public void run() {
						try {
							fDa.dynamicDeploy() ;
							//fDa.launch() ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}) ;*/
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		// disconnect all ports explicitly connected in the deploy phase.
		this.csPort.doDisconnection() ;
		this.avmPort.doDisconnection() ;
		this.rg.doPortDisconnection(GeneratorRequestSubmissionOutboundPortURI) ;
		this.vm.doPortDisconnection(VmRequestNotificationOutboundPortURI) ;
		this.ds.doPortDisconnection(DispatcherRequestSubmissionOutboundPortURI) ;
		this.ds.doPortDisconnection(DispatcherRequestNotificationOutboundPortURI) ;
		this.rgmop.doDisconnection() ;

		super.shutdown() ;
	}

	// ------------------------------------------------------------------------
	// Test scenarios and main execution.
	// ------------------------------------------------------------------------

	/**
	 * generate requests for 20 seconds and then stop generating.
	 *
	 * @throws Exception
	 */
	public void			testScenario() throws Exception
	{
		// start the request generation in the request generator.
		this.rgmop.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(20000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
	}

	/**
	 * execute the test application.
	 * 
	 * @param args	command line arguments, disregarded here.
	 */
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestDispatcher tds = new TestDispatcher() ;
			// Deploy the components
			tds.deploy() ;
			System.out.println("starting...") ;
			// Start them.
			tds.start() ;
			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						tds.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			tds.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}