package fr.upmc.Thalasca.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.Thalasca.datacenterclient.Application.Application;
import fr.upmc.Thalasca.datacenterclient.Application.connectors.ApplicationControllerNotificationConnector;
import fr.upmc.Thalasca.datacenterclient.Application.connectors.ApplicationManagementConnector;
import fr.upmc.Thalasca.datacenterclient.Application.connectors.ApplicationSubmissionNotificationConnector;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.Thalasca.software.admissionController.AdmissionController;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.processors.Processor;

public class Test extends AbstractCVM{
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;


	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;
	
	
	public static final String ApplicationManagementOutboundPortURI = "appmop";
	public static final String ApplicationSubmissionNotificationInboundPortURI = "appsnip";
	public static final String ApplicationControllerNotificationOutboundPortURI = "appcnop";
	public static final String AdmissionControllerURI = "ac";
	
	public static final String ApplicationURI = "app";
	public static final String ApplicationControllerNotificationInboundPortURI = "appcnip";
	public static final String ApplicationManagementInboundPortURI = "appmip";
	public static final String ApplicationSubmissionNotificationOutboundPortURI = "appsnop";
	
	protected AdmissionController ac;
	protected Application app;
	protected ApplicationManagementOutBoundPort appmop;
	
	
	public Test() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		// create and deploy computer
		String computerURI = "computer" ;
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


		
		//create admission controller
		this.ac = new AdmissionController(								
				ComputerServicesOutboundPortURI,
				ComputerStaticStateDataOutboundPortURI,
				ComputerDynamicStateDataOutboundPortURI, 
				ApplicationManagementOutboundPortURI,
				ApplicationSubmissionNotificationInboundPortURI,
				ApplicationControllerNotificationOutboundPortURI,
				AdmissionControllerURI);
		
		this.addDeployedComponent(this.ac);
		
		this.ac.toggleTracing();
		this.ac.toggleLogging();			
		
		this.ac.doPortConnection(				
				ComputerServicesOutboundPortURI,
				ComputerServicesInboundPortURI,
				ComputerServicesConnector.class.getCanonicalName());
		
		this.ac.doPortConnection(
				ComputerStaticStateDataOutboundPortURI,
				ComputerStaticStateDataInboundPortURI,
				DataConnector.class.getCanonicalName());

		this.ac.doPortConnection(
				ComputerDynamicStateDataOutboundPortURI,
				ComputerDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());			
		
		// create application
		this.app = new Application(				
				ApplicationURI,
				ApplicationControllerNotificationInboundPortURI,
				ApplicationManagementInboundPortURI,
				ApplicationSubmissionNotificationOutboundPortURI);
		
		this.addDeployedComponent(app);
	
		this.app.toggleTracing();
		this.app.toggleLogging();
	
		this.app.doPortConnection(
				ApplicationSubmissionNotificationOutboundPortURI,
				ApplicationSubmissionNotificationInboundPortURI,
				ApplicationSubmissionNotificationConnector.class.getCanonicalName());
		
		this.ac.doPortConnection(				
				ApplicationControllerNotificationOutboundPortURI,
				ApplicationControllerNotificationInboundPortURI,
				ApplicationControllerNotificationConnector.class.getCanonicalName());
		
		this.ac.doPortConnection(				
				ApplicationManagementOutboundPortURI,
				ApplicationManagementInboundPortURI,
				ApplicationManagementConnector.class.getCanonicalName());
		
		
		this.appmop = new ApplicationManagementOutBoundPort(				
				ApplicationManagementOutboundPortURI,
				new AbstractComponent(0, 0) {});
		
		this.appmop.publishPort();
		
		this.appmop.doConnection(
				ApplicationManagementInboundPortURI,
				ApplicationManagementConnector.class.getCanonicalName());
		
		super.deploy();
	}
	
	@Override
	public void shutdown() throws Exception {
				
		/*this.admissionController.doPortDisconnection(AC_SERVICES_OUT_PORT_URI);
		this.admissionController.doPortDisconnection(AC_STATIC_STATE_DATA_OUT_PORT_URI);
		this.admissionController.doPortDisconnection(AC_DYNAMIC_STATE_DATA_OUT_PORT_URI);
		this.admissionController.doPortDisconnection(AC_APPLICATION_MANAGEMENT_OUT_PORT_URI);
		this.admissionController.doPortDisconnection(AC_APPLICATION_NOTIFICATION_OUT_PORT_URI);	
		this.asMockUpOutPort.doDisconnection();*/

		// print logs on files, if activated
		/*this.applicationVM.printExecutionLogOnFile("applicationVM");
		this.requestDispatcher.printExecutionLogOnFile("requestDispatcher");
		this.requestGenerator.printExecutionLogOnFile("requestGenerator");*/
		
		super.shutdown();
	}
	
	public void testScenario1() throws Exception {
		this.appmop.submitApplicationToAdmissionController();				
	}
	
	public static void main(String[] args) {
		
		try {
			final Test test = new Test();
			test.deploy();
			
			System.out.println("starting...");
			test.start();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						test.testScenario1();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
			
			Thread.sleep(90000L);
			
			System.out.println("shutting down...");
			test.shutdown();
			
			System.out.println("ending...");
			System.exit(0);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
