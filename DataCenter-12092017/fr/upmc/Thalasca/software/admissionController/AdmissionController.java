package fr.upmc.Thalasca.software.admissionController;

import java.util.ArrayList;
import java.util.HashMap;

import fr.upmc.Thalasca.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.Thalasca.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.Thalasca.datacenter.software.dispatcher.ports.DispatcherManagementOutboundport;
import fr.upmc.Thalasca.datacenterclient.Application.connectors.ApplicationControllerNotificationConnector;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationRequestI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationControllerNotificationOutboundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationSubmissionNotificationInboundPort;
import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.Thalasca.software.admissionController.ports.AdmissionControllerInBoundPort;
import fr.upmc.Thalasca.software.performanceController.PerformanceController;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;

public class AdmissionController 
extends AbstractComponent
implements ApplicationRequestI, AdmissionControllerI{

	public static final int NB_CORES_BY_VM=2;

	public static final String	DispatcherRequestSubmissionInboundPortURI = "drsip" ;
	public static final String	DispatcherRequestSubmissionOutboundPortURI = "drsop" ;
	public static final String	DispatcherRequestNotificationInboundPortURI = "drnip" ;
	public static final String	DispatcherRequestNotificationOutboundPortURI = "drnop" ;

	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	VmRequestNotificationOutboundPortURI = "vmrnop" ;
	public static final String	VmRequestSubmissionInboundPortURI = "vmsip" ;

	public static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	public static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;

	public static final String DispatcherManagementInboundPortURI = "dmip";

	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;

	public static final String AdmissionControllerInboundPortURI = "acip";

	protected final String ApplicationVmURI = "";
	protected final String DispatcherURI = "";
	protected final String PerformanceControllerURI = "";

	protected DynamicComponentCreationOutboundPort portApplicationVM;
	protected DynamicComponentCreationOutboundPort portDispatcher;
	protected DynamicComponentCreationOutboundPort portPerformanceController;

	protected ArrayList<ComputerServicesOutboundPort> csopList;
	protected ArrayList<ComputerStaticStateDataOutboundPort> cssdopList;
	protected ArrayList<ComputerDynamicStateDataOutboundPort> cdsdopList;

	protected ApplicationVMManagementOutboundPort avmOutBoundPort1;
	protected ApplicationVMManagementOutboundPort avmOutBoundPort2;

	protected ArrayList<ApplicationVMManagementOutboundPort> avmOutBoundPortList;

	protected ApplicationControllerNotificationOutboundPort appcnop;
	protected String appcnip;
	protected ApplicationSubmissionNotificationInboundPort appsnip;

	protected DispatcherManagementOutboundport dmop;
	protected AdmissionControllerInBoundPort acip;

	protected HashMap<String, ArrayList<String>> vmListUri; // uri of all vm by application
	protected ArrayList<String> dispatcherList;
	protected ArrayList<RequestSubmissionInboundPort> rsipList;

	protected ArrayList<String> computerURIList;

	public AdmissionController(
			ArrayList<String> computerServicesOutboundPortURI,
			ArrayList<String> computerStaticStateDataOutboundPortURI,
			ArrayList<String> computerDynamicStateDataOutboundPortURI,
			String applicationSubmissionNotificationInboundPortURI,
			String applicationControllerNotificationOutboundPortURI,
			ArrayList<String> computerURI,
			String admissionControllerURI,
			String applicationControllerNotificationInboundPortURI) throws Exception
	{
		super(admissionControllerURI, 1, 1);

		appcnip = applicationControllerNotificationInboundPortURI;

		this.addOfferedInterface(ApplicationSubmissionNotificationI.class);
		this.appsnip = new ApplicationSubmissionNotificationInboundPort(applicationSubmissionNotificationInboundPortURI, this);
		this.addPort(this.appsnip);
		this.appsnip.publishPort();

		this.addRequiredInterface(ApplicationControllerNotificationI.class);
		this.appcnop = new ApplicationControllerNotificationOutboundPort(applicationControllerNotificationOutboundPortURI, this);
		this.addPort(this.appcnop);
		this.appcnop.publishPort();

		this.computerURIList=computerURI;

		this.vmListUri = new HashMap<String, ArrayList<String>>();
		this.dispatcherList = new ArrayList<String>();
		this.avmOutBoundPortList = new ArrayList<ApplicationVMManagementOutboundPort>();
		this.csopList = new ArrayList<ComputerServicesOutboundPort>();
		this.cssdopList = new ArrayList<ComputerStaticStateDataOutboundPort>();
		this.cdsdopList = new ArrayList<ComputerDynamicStateDataOutboundPort>();

		for(int i=0; i<this.computerURIList.size(); i++){
			this.csopList.add(new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI+i, this));
			this.addPort(this.csopList.get(this.csopList.size()-1));
			this.csopList.get(this.csopList.size()-1).publishPort();

			this.cssdopList.add(new ComputerStaticStateDataOutboundPort(ComputerStaticStateDataOutboundPortURI+i, this, this.computerURIList.get(i)));
			this.addPort(this.cssdopList.get(this.cssdopList.size()-1));
			this.cssdopList.get(this.cssdopList.size()-1).publishPort();

			this.cdsdopList.add(new ComputerDynamicStateDataOutboundPort(ComputerDynamicStateDataOutboundPortURI+i, this, this.computerURIList.get(i)));
			this.addPort(this.cdsdopList.get(this.cdsdopList.size()-1));
			this.cdsdopList.get(this.cdsdopList.size()-1).publishPort();

			this.doPortConnection(				
					ComputerServicesOutboundPortURI+i,
					computerServicesOutboundPortURI.get(i),
					ComputerServicesConnector.class.getCanonicalName());

			this.doPortConnection(
					ComputerStaticStateDataOutboundPortURI+i,
					computerStaticStateDataOutboundPortURI.get(i),
					DataConnector.class.getCanonicalName());

			this.doPortConnection(
					ComputerDynamicStateDataOutboundPortURI+i,
					computerDynamicStateDataOutboundPortURI.get(i),
					ControlledDataConnector.class.getCanonicalName());
		}

		this.addRequiredInterface(DynamicComponentCreationI.class);
	}

	@Override
	public void start() throws ComponentStartException {

		super.start();			

		try {	
			this.portApplicationVM = new DynamicComponentCreationOutboundPort(this);
			this.portApplicationVM.localPublishPort();
			this.addPort(this.portApplicationVM);

			this.portApplicationVM.doConnection(					
					this.ApplicationVmURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());

			this.portDispatcher = new DynamicComponentCreationOutboundPort(this);
			this.portDispatcher.publishPort();
			this.addPort(this.portDispatcher);
			this.portDispatcher.doConnection(					
					this.DispatcherURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());
			
			this.portPerformanceController = new DynamicComponentCreationOutboundPort(this);
			this.portPerformanceController.publishPort();
			this.addPort(this.portPerformanceController);
			this.portPerformanceController.doConnection(					
					this.PerformanceControllerURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());


		} catch (Exception e) {
			throw new ComponentStartException("Error start AdmissionController", e);
		}
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			for(ComputerServicesOutboundPort csop: csopList){
				if (csop.connected()) {
					csop.doDisconnection();
				}
			}
			for(ComputerStaticStateDataOutboundPort cssdop: cssdopList){
				if (cssdop.connected()) {
					cssdop.doDisconnection();
				}
			}
			for(ComputerDynamicStateDataOutboundPort cdsdop: cdsdopList)
				if (cdsdop.connected()) {
					cdsdop.doDisconnection();
				}

			if (this.portDispatcher.connected()) {
				this.portDispatcher.doDisconnection();
			}
			if (this.portApplicationVM.connected()) {
				this.portApplicationVM.doDisconnection();
			}

			if (this.portPerformanceController.connected()) {
				this.portPerformanceController.doDisconnection();
			}



		} catch (Exception e) {
			throw new ComponentShutdownException("Error shutdown AdmissionController", e);
		}

		super.shutdown();
	}

	public void deployDynamicComponentsForApplication(String applicationUri, ArrayList<AllocatedCore[]> ac, ApplicationManagementOutBoundPort appmop, int nombreVM) throws Exception {						 			
		System.out.println("Deploy dynamic components for " + applicationUri);

		this.vmListUri.put(applicationUri, new ArrayList<String>());
		//create applicationVM for accepted application
		for(int i=0; i<nombreVM; i++){

			this.portApplicationVM.createComponent(
					ApplicationVM.class.getCanonicalName(),
					new Object[] {
							applicationUri+"_VM"+i,
							applicationUri+"_"+ApplicationVMManagementInboundPortURI+i,
							applicationUri+"_"+VmRequestSubmissionInboundPortURI+i,
							applicationUri+"_"+VmRequestNotificationOutboundPortURI+i
					});


			vmListUri.get(applicationUri).add(applicationUri+"_VM"+i);
		}

		// create dispatcher for accepted application
		this.portDispatcher.createComponent(
				Dispatcher.class.getCanonicalName(),
				new Object[] {
						applicationUri+"_dispatcher",
						applicationUri+"_"+DispatcherRequestSubmissionInboundPortURI,
						applicationUri+"_"+DispatcherManagementInboundPortURI,
						applicationUri+"_"+DispatcherRequestNotificationOutboundPortURI,
						applicationUri+"_"+DispatcherRequestNotificationInboundPortURI
				});					


		// create ApplicationVMManagementOutboundPort 
		for(int i=0; i<nombreVM; i++){
			this.avmOutBoundPortList.add(new ApplicationVMManagementOutboundPort(
					applicationUri+"_"+ApplicationVMManagementOutboundPortURI+i,
					new AbstractComponent(0, 0) {}));
			this.avmOutBoundPortList.get(this.avmOutBoundPortList.size()-1).publishPort();
			this.avmOutBoundPortList.get(this.avmOutBoundPortList.size()-1).doConnection(
					applicationUri+"_"+ApplicationVMManagementInboundPortURI+i,
					ApplicationVMManagementConnector.class.getCanonicalName());			

			this.avmOutBoundPortList.get(this.avmOutBoundPortList.size()-1).allocateCores(ac.get(i)) ;
		}

		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		this.addPort(rop);
		rop.localPublishPort();		


		// connect dispatcher
		rop.doConnection(applicationUri+"_dispatcher", ReflectionConnector.class.getCanonicalName());
		rop.toggleLogging();
		rop.toggleTracing();


		appmop.connectionDispatcherWithRequestGeneratorForSubmission(
				applicationUri+"_"+DispatcherRequestSubmissionInboundPortURI, applicationUri);

		appmop.connectionDispatcherWithRequestGeneratorForNotification(rop, applicationUri+"_"+DispatcherRequestNotificationOutboundPortURI, applicationUri);		



		// connect dispatcher to VM
		this.addRequiredInterface(DispatcherManagementI.class);
		this.dmop = new DispatcherManagementOutboundport(applicationUri+"_dmop", this);
		this.addPort(dmop);
		this.dmop.publishPort();
		this.dmop.doConnection(
				applicationUri+"_"+DispatcherManagementInboundPortURI,
				DispatcherManagementConnector.class.getCanonicalName());




		for(int i=0; i<nombreVM; i++){
			this.dmop.connectToVirtualMachine(applicationUri+"_"+VmRequestSubmissionInboundPortURI+i);

			// connect applicationVM
			rop.doConnection(applicationUri+"_VM"+i, ReflectionConnector.class.getCanonicalName());

			rop.toggleTracing();
			rop.toggleLogging();

			rop.doPortConnection(
					applicationUri+"_"+VmRequestNotificationOutboundPortURI+i,
					applicationUri+"_"+DispatcherRequestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());

		}

		// create performanceController for application

		this.addRequiredInterface(AdmissionControllerI.class) ;
		this.acip=new AdmissionControllerInBoundPort(applicationUri+"_"+AdmissionControllerInboundPortURI,this);
		this.addPort(this.acip);
		this.acip.publishPort();

		this.portPerformanceController.createComponent(
				PerformanceController.class.getCanonicalName(),
				new Object[] {
						applicationUri+"_performanceController",
						applicationUri+"_"+DispatcherManagementInboundPortURI,
						applicationUri+"_"+AdmissionControllerInboundPortURI,
						applicationUri
				});

		System.out.println("finish creation ressources for the application "+applicationUri);
	}

	@Override
	public void receiveApplicationToAdmissionController(String applicationURI, ApplicationManagementOutBoundPort appmop, int nombreVM) throws Exception {

		System.out.println("Application re�ue :"+applicationURI);

		ArrayList<AllocatedCore[]> allocatedCore = new ArrayList<>();
		boolean ressourcesAvailable = true;
		int currentComputer=0;
		for(int i=0; i<nombreVM; i++){
			allocatedCore.add(csopList.get(currentComputer).allocateCores(NB_CORES_BY_VM));

			if(allocatedCore.get(i).length!=NB_CORES_BY_VM){
				ressourcesAvailable=false;

				if(currentComputer<this.computerURIList.size()-1){
					currentComputer++;
					ressourcesAvailable=true;
				}
			}
		}

		this.appcnop.doConnection(applicationURI+"_"+this.appcnip, ApplicationControllerNotificationConnector.class.getCanonicalName());
		if (ressourcesAvailable) {
			System.out.println("Accept application " + applicationURI);
			deployDynamicComponentsForApplication(applicationURI, allocatedCore, appmop, nombreVM);
			this.appcnop.responseFromApplicationController(true, applicationURI);

		} else {
			System.out.println("Application rejected");	
			this.appcnop.responseFromApplicationController(false, applicationURI);
		}
	}

	@Override
	public boolean addVirtualMachine(String applicationUri) throws Exception {

		int idVm = this.vmListUri.get(applicationUri).size();

		// allocate core for new VM
		AllocatedCore[] allocatedCore = new AllocatedCore[NB_CORES_BY_VM];
		boolean ressourceAvailable = false;
		for(int i=0; i<this.computerURIList.size(); i++) {
			allocatedCore = csopList.get(i).allocateCores(NB_CORES_BY_VM);

			if(allocatedCore.length==NB_CORES_BY_VM){
				ressourceAvailable = true;
				break;
			}
		}
		if(!ressourceAvailable)
			return false; // not engouh core for new vm

		//create VM
		this.portApplicationVM.createComponent(
				ApplicationVM.class.getCanonicalName(),
				new Object[] {
						applicationUri+"_VM"+idVm,
						applicationUri+"_"+ApplicationVMManagementInboundPortURI+idVm,
						applicationUri+"_"+VmRequestSubmissionInboundPortURI+idVm,
						applicationUri+"_"+VmRequestNotificationOutboundPortURI+idVm
				});
		this.avmOutBoundPortList.add(new ApplicationVMManagementOutboundPort(
				applicationUri+"_"+ApplicationVMManagementOutboundPortURI+idVm,
				new AbstractComponent(0, 0) {}));
		this.avmOutBoundPortList.get(this.avmOutBoundPortList.size()-1).publishPort();
		this.avmOutBoundPortList.get(this.avmOutBoundPortList.size()-1).doConnection(
				applicationUri+"_"+ApplicationVMManagementInboundPortURI+idVm,
				ApplicationVMManagementConnector.class.getCanonicalName());			


		this.avmOutBoundPortList.get(this.avmOutBoundPortList.size()-1).allocateCores(allocatedCore) ;

		this.dmop.connectToVirtualMachine(applicationUri+"_"+VmRequestSubmissionInboundPortURI+idVm);

		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		this.addPort(rop);
		rop.localPublishPort();

		// connect applicationVM
		rop.doConnection(applicationUri+"_VM"+idVm, ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(
				applicationUri+"_"+VmRequestNotificationOutboundPortURI+idVm,
				applicationUri+"_"+DispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());


		this.vmListUri.get(applicationUri).add(applicationUri+"_"+VmRequestSubmissionInboundPortURI+idVm);

		return true;
	}

	@Override
	public boolean removeVirtualMachine(String applicationUri) throws Exception {
		return false;
	}
}
