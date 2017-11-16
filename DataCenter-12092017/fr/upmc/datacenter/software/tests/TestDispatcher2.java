package fr.upmc.datacenter.software.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.components.examples.basic_cs.URIServiceConnector;
import fr.upmc.components.examples.basic_cs.components.URIProvider;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
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

public class TestDispatcher2
extends AbstractComponent{

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
	public static final String	DispatcherRequestNotificationInboundPortURI = "drnip" ;
	public static final String	DispatcherRequestNotificationOutboundPortURI = "drnop" ;
	public static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	public static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;
	public static final String	VmRequestNotificationOutboundPortURI = "vmrnop" ;
	public static final String	VmRequestSubmissionInboundPortURI = "vmsip" ;
	
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;


	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort			csPort ;
	/** 	Computer monitor component.										*/
	protected ComputerMonitor						cm ;
	/** 	Application virtual machine component.							*/
	protected ApplicationVM							vm ;
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
	
	protected String RG_COMPONENT_URI = "rg" ;
	protected String DS_COMPONENT_URI = "ds" ;
	
	protected DynamicComponentCreationOutboundPort darg;
	protected DynamicComponentCreationOutboundPort dads;

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

	public void			deploy() throws Exception
	{
		//launch
		this.addRequiredInterface(DynamicComponentCreationI.class) ;
		
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;
		
		this.darg =
				new DynamicComponentCreationOutboundPort(this) ;
		this.darg.localPublishPort() ;
		this.addPort(this.darg) ;
		this.darg.doConnection(
		this.RG_COMPONENT_URI,
		DynamicComponentCreationConnector.class.getCanonicalName()) ;
		
		this.dads =
				new DynamicComponentCreationOutboundPort(this) ;
		this.dads.localPublishPort() ;
		this.addPort(this.dads) ;
		this.dads.doConnection(
		this.DS_COMPONENT_URI,
		DynamicComponentCreationConnector.class.getCanonicalName()) ;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		/*String computerURI = "computer0" ;
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
		this.vm.toggleLogging() ;*/

		// --------------------------------------------------------------------
		
		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		
		this.darg.createComponent(
				RequestGenerator.class.getCanonicalName(),
				new Object[]{RG_COMPONENT_URI, new Double(500.0), new Long(6000000000L),
						RequestGeneratorManagementInboundPortURI,
						GeneratorRequestSubmissionOutboundPortURI,
						GeneratorRequestNotificationInboundPortURI}) ;
		
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this) ;
		this.addPort(rop) ;
		rop.localPublishPort() ;

		// connect to the consumer (client) component
		rop.doConnection(RG_COMPONENT_URI,
						 ReflectionConnector.class.getCanonicalName()) ;
		// toggle logging on the consumer component
		rop.toggleLogging() ;
		
		rop.doPortConnection(GeneratorRequestSubmissionOutboundPortURI,
				DispatcherRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;
		
		rop.doDisconnection();

		// --------------------------------------------------------------------
		// Creating the dispatcher component.
		// --------------------------------------------------------------------
		
		ArrayList<String> dispatcherOutboundPortList = new ArrayList<String>();
		dispatcherOutboundPortList.add(DispatcherRequestSubmissionOutboundPortURI);
		
		ArrayList<String> dispatcherRequestNotificationInboundPortList = new ArrayList<String>();
		dispatcherRequestNotificationInboundPortList.add(DispatcherRequestNotificationInboundPortURI);
		
		this.dads.createComponent(
				URIProvider.class.getCanonicalName(),
				new Object[]{DS_COMPONENT_URI, DispatcherRequestSubmissionInboundPortURI, 
						dispatcherOutboundPortList, DispatcherRequestNotificationOutboundPortURI, 
						dispatcherRequestNotificationInboundPortList}) ;

		// connect to the consumer (client) component
		rop.doConnection(DS_COMPONENT_URI,
						 ReflectionConnector.class.getCanonicalName()) ;
		// toggle logging on the consumer component
		rop.toggleLogging() ;
		
		rop.doPortConnection(
				DispatcherRequestSubmissionOutboundPortURI,
				VmRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;
		
		rop.toggleLogging() ;
		
		rop.doPortConnection(
				DispatcherRequestNotificationOutboundPortURI,
				GeneratorRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;
		
		rop.doDisconnection();
		
		// --------------------------------------------------------------------

		
		// Connecting the request generator to the application virtual machine.
		// Request generators have three different interfaces:
		// - one for submitting requests to application virtual machines,
		// - one for receiving end of execution notifications from application
		//   virtual machines, and
		// - one for request generation management i.e., starting and stopping
		//   the generation process.

		
		
		this.vm.doPortConnection(
					VmRequestNotificationOutboundPortURI,
					DispatcherRequestNotificationInboundPortURI,
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

	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#start()
	 */

	public void			start()
	{
		// Allocate the 4 cores of the computer to the application virtual
		// machine.
				
		try{			
			AllocatedCore[] ac = this.csPort.allocateCores(2) ;
			this.avmPort.allocateCores(ac) ;
			
			AllocatedCore[] ac2 = this.csPort.allocateCores(2) ;
		this.avmPort2.allocateCores(ac2) ;
		} catch(Exception e){System.out.println(e);}
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	
	public void			shutdown()
	{
		// disconnect all ports explicitly connected in the deploy phase.
		
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
			final TestDispatcher2 tds = new TestDispatcher2() ;
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