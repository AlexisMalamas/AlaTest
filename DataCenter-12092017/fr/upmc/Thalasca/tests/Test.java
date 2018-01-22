package fr.upmc.Thalasca.tests;

import java.util.ArrayList;
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
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.processors.Processor;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

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

	public static final String ApplicationURI = "ThalascaEnterprise";
	public static final String ApplicationControllerNotificationInboundPortURI = "appcnip";
	public static final String ApplicationManagementInboundPortURI = "appmip";
	public static final String ApplicationSubmissionNotificationOutboundPortURI = "appsnop";

	public static final int nbVMatStart = 2;

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
				ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI,
				ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;
		
		ArrayList<String> csdipList = new ArrayList<>();
		csdipList.add(ComputerServicesInboundPortURI);

		ArrayList<String> cpssdipList = new ArrayList<>();
		cpssdipList.add(ComputerStaticStateDataInboundPortURI);

		ArrayList<String> cdsdipList = new ArrayList<>();
		cdsdipList.add(ComputerDynamicStateDataInboundPortURI);
		
		ArrayList<String> csdopList = new ArrayList<>();
		csdopList.add(ComputerServicesOutboundPortURI);

		ArrayList<String> cpssdopList = new ArrayList<>();
		cpssdopList.add(ComputerStaticStateDataOutboundPortURI);

		ArrayList<String> cdsdopList = new ArrayList<>();
		cdsdopList.add(ComputerDynamicStateDataOutboundPortURI);
		
		ArrayList<String> computersURI = new ArrayList<>();
		computersURI.add(computerURI);
		
		//create admission controller
		this.ac = new AdmissionController(								
				csdipList,
				csdopList,
				cpssdipList,
				cpssdopList,
				cdsdipList, 
				cdsdopList,
				ApplicationSubmissionNotificationInboundPortURI,
				ApplicationControllerNotificationOutboundPortURI,
				computersURI,
				AdmissionControllerURI,
				ApplicationControllerNotificationInboundPortURI);

		this.addDeployedComponent(this.ac);

		this.ac.toggleTracing();
		this.ac.toggleLogging();			

		// create application
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

		super.deploy();
	}

	@Override
	public void shutdown() throws Exception {
		this.appmop.doDisconnection();
		super.shutdown();
	}

	public void testScenario1() throws Exception {
		this.appmop.submitApplicationToAdmissionController(ApplicationURI, nbVMatStart);				
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
