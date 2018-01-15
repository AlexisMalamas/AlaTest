package fr.upmc.Thalasca.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.Thalasca.datacenterclient.Application.Application;
import fr.upmc.Thalasca.datacenterclient.Application.connectors.ApplicationManagementConnector;
import fr.upmc.Thalasca.datacenterclient.Application.connectors.ApplicationSubmissionNotificationConnector;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.Thalasca.software.admissionController.AdmissionController;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.processors.Processor;

public class Test2Application extends AbstractCVM{
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;

	public static final String AdmissionControllerURI = "ac";
	
	public static final String ApplicationControllerNotificationOutboundPortURI = "appcnop";
	public static final String ApplicationControllerNotificationInboundPortURI = "appcnip";
	
	public static final String ApplicationSubmissionNotificationInboundPortURI = "appsnip";

	public static final String ApplicationURI = "ThalascaEnterprise";
	public static final String ApplicationManagementInboundPortURI = "appmip";
	public static final String ApplicationSubmissionNotificationOutboundPortURI = "appsnop";
	public static final String ApplicationManagementOutboundPortURI = "appmop";

	public static final String ApplicationURI2 = "JavaEnterprise";
	public static final String ApplicationManagementInboundPortURI2 = "appmip2";
	public static final String ApplicationSubmissionNotificationOutboundPortURI2= "appsnop2";
	public static final String ApplicationManagementOutboundPortURI2 = "appmop2";
	

	public static final int nombreVM = 4;
	
	protected AdmissionController ac;
	protected Application app;
	protected ApplicationManagementOutBoundPort appmop;

	protected Application app2;
	protected ApplicationManagementOutBoundPort appmop2;


	public Test2Application() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {

		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		// create and deploy computer
		String computerURI = "computer" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 4 ;
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
				1500,
				1500,
				numberOfProcessors,
				numberOfCores,
				ComputerServicesInboundPortURI+1,
				ComputerStaticStateDataInboundPortURI+1,
				ComputerDynamicStateDataInboundPortURI+1) ;
		this.addDeployedComponent(c) ;

		String computerURI2 = "computer2" ;
		int numberOfProcessors2 = 2 ;
		int numberOfCores2 = 4 ;
		Set<Integer> admissibleFrequencies2 = new HashSet<Integer>() ;
		admissibleFrequencies2.add(1500) ;
		admissibleFrequencies2.add(3000) ;
		Map<Integer,Integer> processingPower2 = new HashMap<Integer,Integer>() ;
		processingPower2.put(1500, 1500000) ;
		processingPower2.put(3000, 3000000) ;
		Computer c2 = new Computer(
				computerURI2,
				admissibleFrequencies2,
				processingPower2,  
				1500,
				1500,
				numberOfProcessors2,
				numberOfCores2,
				ComputerServicesInboundPortURI+2,
				ComputerStaticStateDataInboundPortURI+2,
				ComputerDynamicStateDataInboundPortURI+2) ;
		this.addDeployedComponent(c2) ;

		
		ArrayList<String> csdipList = new ArrayList<>();
		csdipList.add(ComputerServicesInboundPortURI+1);
		csdipList.add(ComputerServicesInboundPortURI+2);

		ArrayList<String> cpssdipList = new ArrayList<>();
		cpssdipList.add(ComputerStaticStateDataInboundPortURI+1);
		cpssdipList.add(ComputerStaticStateDataInboundPortURI+2);

		ArrayList<String> cdsdipList = new ArrayList<>();
		cdsdipList.add(ComputerDynamicStateDataInboundPortURI+1);
		cdsdipList.add(ComputerDynamicStateDataInboundPortURI+2);
		
		ArrayList<String> computersURI = new ArrayList<>();
		computersURI.add(computerURI);
		computersURI.add(computerURI2);
		
		//create admission controller
		this.ac = new AdmissionController(								
				csdipList,
				cpssdipList,
				cdsdipList, 
				ApplicationSubmissionNotificationInboundPortURI,
				ApplicationControllerNotificationOutboundPortURI,
				computersURI,
				AdmissionControllerURI,
				ApplicationControllerNotificationInboundPortURI);

		this.addDeployedComponent(this.ac);

		this.ac.toggleTracing();
		this.ac.toggleLogging();			

		// create first application
		this.app = new Application(				
				ApplicationURI,
				ApplicationURI+"_"+ApplicationControllerNotificationInboundPortURI,
				ApplicationManagementInboundPortURI,
				ApplicationManagementOutboundPortURI,
				ApplicationSubmissionNotificationOutboundPortURI);

		this.addDeployedComponent(app);

		this.app.toggleTracing();
		this.app.toggleLogging();

		this.app.doPortConnection(
				ApplicationSubmissionNotificationOutboundPortURI,
				ApplicationSubmissionNotificationInboundPortURI,
				ApplicationSubmissionNotificationConnector.class.getCanonicalName());

		this.app.doPortConnection(				
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


		// create second application
		this.app2 = new Application(				
				ApplicationURI2,
				ApplicationURI2+"_"+ApplicationControllerNotificationInboundPortURI,
				ApplicationManagementInboundPortURI2,
				ApplicationManagementOutboundPortURI2,
				ApplicationSubmissionNotificationOutboundPortURI2);

		this.addDeployedComponent(app2);

		this.app2.toggleTracing();
		this.app2.toggleLogging();

		this.app2.doPortConnection(
				ApplicationSubmissionNotificationOutboundPortURI2,
				ApplicationSubmissionNotificationInboundPortURI,
				ApplicationSubmissionNotificationConnector.class.getCanonicalName());

		this.app2.doPortConnection(				
				ApplicationManagementOutboundPortURI2,
				ApplicationManagementInboundPortURI2,
				ApplicationManagementConnector.class.getCanonicalName());

		this.appmop2 = new ApplicationManagementOutBoundPort(				
				ApplicationManagementOutboundPortURI2,
				new AbstractComponent(0, 0) {});

		this.appmop2.publishPort();

		this.appmop2.doConnection(
				ApplicationManagementInboundPortURI2,
				ApplicationManagementConnector.class.getCanonicalName());

		super.deploy();
	}

	@Override
	public void shutdown() throws Exception {
		this.appmop.doDisconnection();
		this.appmop2.doDisconnection();

		super.shutdown();
	}

	public void testScenario1() throws Exception {
		this.appmop.submitApplicationToAdmissionController(ApplicationURI, nombreVM);	

	}

	public void testScenario2() throws Exception{
		this.appmop2.submitApplicationToAdmissionController(ApplicationURI2, nombreVM);
	}

	public static void main(String[] args) {


		try {
			final Test2Application test = new Test2Application();
			test.deploy();
			System.out.println("starting...");
			test.start();

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						test.testScenario1();
						test.testScenario2();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}).start();			

			Thread.sleep(900000L);

			System.out.println("shutting down...");
			test.shutdown();

			System.out.println("ending...");
			System.exit(0);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
