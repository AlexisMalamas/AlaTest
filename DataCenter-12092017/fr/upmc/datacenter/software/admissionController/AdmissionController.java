package fr.upmc.datacenter.software.admissionController;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class AdmissionController 
extends AbstractComponent
implements ComputerStateDataConsumerI{

	String admissionControllerURI;

	private static final int NB_CORES_TO_ALLOCATE=4;

	// all informations for the dispatcher
	public static final String dispatcherURI = "ds";
	public static final String DispatcherRequestNotificationOutboundPortURI	= "drnop";
	public static final String DispatcherRequestNotificationInboundPortURI	= "drnip";
	public static final String DispatcherRequestSubmissionInboundPortURI = "drsip" ;
	public static final String DispatcherRequestSubmissionOutboundPortURI = "drsop" ;
	public ArrayList<String> DispatcherRequestSubmissionOutboundPortList = new ArrayList<String>();
	public ArrayList<String> dispatcherRequestNotificationInboundPortList = new ArrayList<String>();
	int nbPortDispatcher =1;

	//all informations application VM
	public static final String ApplicationVMURI = "vm";
	public static final String ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String VmRequestNotificationOutboundPortURI = "vmrnop" ;
	public static final String VmRequestSubmissionInboundPortURI = "vmsip" ;
	int nbVM=1;

	protected ApplicationVMManagementOutboundPort AVMop;

	protected int nbProcessors;
	protected int nbCoresPerProcessor;	
	protected boolean[][] reservedCores;


	protected DynamicComponentCreationOutboundPort portToApplicationVMJVM;
	protected DynamicComponentCreationOutboundPort portToDispatcherJVM;

	protected ArrayList<ApplicationVM> listApplicationVM;
	//protected ArrayList<RequestGenerator> listRequestGenerator;
	//protected ArrayList<Dispatcher> listDispatcher;

	protected ComputerServicesOutboundPort CSop;


	public AdmissionController(String admissionCOntrollerURI)
	{
		super(1,1);

		this.admissionControllerURI=admissionCOntrollerURI;
	}

	public boolean requestGeneratorAccept(RequestGenerator rg)
	{
		// create dispatcher
		// create vm if proc dispo
		// connect vm to processor
		// connect dispatcher to vm
		//connect rg to dispatcher
		return true;
	}

	public void acceptApplicationAndCreateRessources()
	{

		this.logMessage("Admission controller accept the application and create ressources");

		this.portToApplicationVMJVM.createComponent(
				ApplicationVM.class.getCanonicalName(),
				new Object[] {
						ApplicationVMURI+nbVM,
						ApplicationVMManagementInboundPortURI,
						VmRequestSubmissionInboundPortURI,
						VmRequestNotificationOutboundPortURI
				});			

		DispatcherRequestSubmissionOutboundPortList.add(DispatcherRequestSubmissionOutboundPortURI+nbPortDispatcher);
		dispatcherRequestNotificationInboundPortList.add(DispatcherRequestNotificationInboundPortURI+nbPortDispatcher);
		
		this.portToDispatcherJVM.createComponent(
				Dispatcher.class.getCanonicalName(),
				new Object[] {
						dispatcherURI,							
						DispatcherRequestSubmissionInboundPortURI,
						DispatcherRequestSubmissionOutboundPortList,
						DispatcherRequestNotificationOutboundPortURI,
						dispatcherRequestNotificationInboundPortList
				});					
		
		
		this.AVMop = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent(0, 0) {});
		this.AVMop.publishPort();
		this.AVMop.doConnection(
				ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName());		
		
		
		ReflectionOutboundPort reflectionOutbounPort = new ReflectionOutboundPort(this);
		this.addPort(reflectionOutbounPort);
		reflectionOutbounPort.localPublishPort();			

		reflectionOutbounPort.doConnection(dispatcherURI, ReflectionConnector.class.getCanonicalName());

		
		reflectionOutbounPort.toggleLogging();
		reflectionOutbounPort.toggleTracing();

		this.amop.doDynamicConnectionWithDispatcherForSubmission(RD_REQUEST_SUBMISSION_IN_PORT_URI);
		this.amop.doDynamicConnectionWithDispatcherForNotification(rop, RD_REQUEST_NOTIFICATION_OUT_PORT_URI);		
		
		reflectionOutbounPort.doPortConnection(
				DispatcherRequestSubmissionOutboundPortURI,
				VmRequestSubmissionInboundPortURI,
				ReflectionConnector.class.getCanonicalName());

		reflectionOutbounPort.doDisconnection();
		
		
		reflectionOutbounPort.doConnection(ApplicationVMURI+nbVM, ReflectionConnector.class.getCanonicalName());

		reflectionOutbounPort.toggleTracing();
		reflectionOutbounPort.toggleLogging();

		reflectionOutbounPort.doPortConnection(
				VmRequestNotificationOutboundPortURI,
				DispatcherRequestNotificationInboundPortURI,
				ReflectionConnector.class.getCanonicalName());

		reflectionOutbounPort.doDisconnection();


		AllocatedCore[] ac = this.CSop.allocateCores(NB_CORES_TO_ALLOCATE);
		this.AVMop.allocateCores(ac);
	}


	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
		this.nbProcessors = staticState.getNumberOfProcessors();
		this.nbCoresPerProcessor = staticState.getNumberOfCoresPerProcessor();

	}


	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception {
		this.reservedCores = currentDynamicState.getCurrentCoreReservations();

	}

	public void getInformationProcessor(){

		this.reservedCores = new boolean[this.nbProcessors][this.nbCoresPerProcessor];
		for (int i = 0; i < this.nbProcessors; i++) {
			for(int j = 0; j < this.nbCoresPerProcessor; j++) {
				this.reservedCores[i][j] = false;
			}
		}
	}

	public boolean ressourceAvailableCores() {		

		for (int i = 0; i < reservedCores.length; i++) {
			for (int j = 0; j < reservedCores[0].length; j++) {
				if (!this.reservedCores[i][j]) {					
					return true;
				}
			}
		}

		return false;
	}
}
