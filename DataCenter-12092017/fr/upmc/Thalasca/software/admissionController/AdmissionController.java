package fr.upmc.Thalasca.software.admissionController;

import java.util.ArrayList;

import fr.upmc.Thalasca.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.Thalasca.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.Thalasca.datacenter.software.dispatcher.ports.DispatcherManagementOutboundport;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationRequestI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationControllerNotificationOutboundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationSubmissionNotificationInboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateDataI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;

public class AdmissionController 
extends AbstractComponent
implements ApplicationRequestI{

	public static final int NB_CORES=2;

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

	protected ArrayList<String> DispatcherRequestSubmissionOutboundPortList = new ArrayList<String>();

	protected final String ApplicationVmURI = "";
	protected final String DispatcherURI = "";

	protected DynamicComponentCreationOutboundPort portApplicationVM;
	protected DynamicComponentCreationOutboundPort portDispatcher;

	protected ComputerServicesOutboundPort csop;
	protected ComputerStaticStateDataOutboundPort cssdop;
	protected ComputerDynamicStateDataOutboundPort cdsdop;
	protected ApplicationManagementOutBoundPort appmop;
	protected ApplicationVMManagementOutboundPort avmOutBoundPort1;
	protected ApplicationVMManagementOutboundPort avmOutBoundPort2;
	protected ApplicationControllerNotificationOutboundPort appcnop;
	protected ApplicationSubmissionNotificationInboundPort appsnip;

	protected DispatcherManagementOutboundport dmop;

	protected ArrayList<String> vmList;
	protected ArrayList<String> dispatcherList;
	protected ArrayList<RequestSubmissionInboundPort> rsipList;

	public AdmissionController(
			String computerServicesOutboundPortURI,
			String computerStaticStateDataOutboundPortURI,
			String computerDynamicStateDataOutboundPortURI,
			String applicationManagementOutboundPortURI, 
			String applicationSubmissionNotificationInboundPortURI,
			String applicationControllerNotificationOutboundPortURI,
			String computerURI,
			String admissionControllerURI) throws Exception
	{
		super(admissionControllerURI, 1, 1);

		this.addRequiredInterface(ComputerServicesI.class);
		this.csop = new ComputerServicesOutboundPort(computerServicesOutboundPortURI, this);
		this.addPort(this.csop);
		this.csop.publishPort();

		this.addOfferedInterface(ComputerStaticStateDataI.class);

		this.cssdop = new ComputerStaticStateDataOutboundPort(computerStaticStateDataOutboundPortURI, this, computerURI);
		this.addPort(this.cssdop);
		this.cssdop.publishPort();

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		this.cdsdop = new ComputerDynamicStateDataOutboundPort(computerDynamicStateDataOutboundPortURI, this, computerURI);
		this.addPort(this.cdsdop);
		this.cdsdop.publishPort();

		this.addRequiredInterface(ApplicationManagementI.class);
		this.appmop = new ApplicationManagementOutBoundPort(applicationManagementOutboundPortURI, this);
		this.addPort(this.appmop);
		this.appmop.publishPort();										

		this.addOfferedInterface(ApplicationSubmissionNotificationI.class);
		this.appsnip = new ApplicationSubmissionNotificationInboundPort(applicationSubmissionNotificationInboundPortURI, this);
		this.addPort(this.appsnip);
		this.appsnip.publishPort();

		this.addRequiredInterface(ApplicationControllerNotificationI.class);
		this.appcnop = new ApplicationControllerNotificationOutboundPort(applicationControllerNotificationOutboundPortURI, this);
		this.addPort(this.appcnop);
		this.appcnop.publishPort();

		this.vmList = new ArrayList<String>();
		this.dispatcherList = new ArrayList<String>();

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


		} catch (Exception e) {
			throw new ComponentStartException("Error start AdmissionController", e);
		}
	}

	@Override
	public void shutdown() throws ComponentShutdownException {

		try {			
			if (this.csop.connected()) {
				this.csop.doDisconnection();
			}
			if (this.cssdop.connected()) {
				this.cssdop.doDisconnection();
			}
			if (this.cdsdop.connected()) {
				this.cdsdop.doDisconnection();
			}
			if (this.appmop.connected()) {
				this.appmop.doDisconnection();
			}
			if (this.portDispatcher.connected()) {
				this.portDispatcher.doDisconnection();
			}
			if (this.portApplicationVM.connected()) {
				this.portApplicationVM.doDisconnection();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException("Error shutdown AdmissionController", e);
		}

		super.shutdown();
	}

	public void deployDynamicComponentsForApplication(String applicationUri, AllocatedCore[] ac1, AllocatedCore[] ac2) throws Exception {						 			
		System.out.println("Deploy dynamic components for " + applicationUri);

		//create applicationVM for accepted application
		this.portApplicationVM.createComponent(
				ApplicationVM.class.getCanonicalName(),
				new Object[] {
						"VM"+vmList.size(),
						ApplicationVMManagementInboundPortURI+vmList.size(),
						VmRequestSubmissionInboundPortURI+vmList.size(),
						VmRequestNotificationOutboundPortURI+vmList.size()
				});


		vmList.add(VmRequestSubmissionInboundPortURI+vmList.size());

		this.portApplicationVM.createComponent(
				ApplicationVM.class.getCanonicalName(),
				new Object[] {
						"VM"+vmList.size(),
						ApplicationVMManagementInboundPortURI+vmList.size(),
						VmRequestSubmissionInboundPortURI+vmList.size(),
						VmRequestNotificationOutboundPortURI+vmList.size()
				});

		vmList.add(VmRequestSubmissionInboundPortURI+vmList.size());

		System.out.println("finish create component application vm");

		DispatcherRequestSubmissionOutboundPortList.add(DispatcherRequestSubmissionOutboundPortURI);

		// create dispatcher for accepted application
		this.portDispatcher.createComponent(
				Dispatcher.class.getCanonicalName(),
				new Object[] {
						"dispatcher"+dispatcherList.size(),
						DispatcherRequestSubmissionInboundPortURI+dispatcherList.size(),
						DispatcherManagementInboundPortURI+dispatcherList.size(),
						DispatcherRequestNotificationOutboundPortURI+dispatcherList.size(),
						DispatcherRequestNotificationInboundPortURI+dispatcherList.size()
				});					



		System.out.println("finish create component dispatcher");

		// create ApplicationVMManagementOutboundPort 
		this.avmOutBoundPort1 = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent(0, 0) {});
		this.avmOutBoundPort1.publishPort();
		this.avmOutBoundPort1.doConnection(
				ApplicationVMManagementInboundPortURI+"0",
				ApplicationVMManagementConnector.class.getCanonicalName());			

		this.avmOutBoundPort1.allocateCores(ac1) ;

		// create ApplicationVMManagementOutboundPort 
		this.avmOutBoundPort2 = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent(0, 0) {});
		this.avmOutBoundPort2.publishPort();
		this.avmOutBoundPort2.doConnection(
				ApplicationVMManagementInboundPortURI+"1",
				ApplicationVMManagementConnector.class.getCanonicalName());			

		this.avmOutBoundPort2.allocateCores(ac2) ;

		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		this.addPort(rop);
		rop.localPublishPort();		


		System.out.println("finish create component app management");
		// connect dispatcher
		rop.doConnection("dispatcher"+dispatcherList.size(), ReflectionConnector.class.getCanonicalName());
		rop.toggleLogging();
		rop.toggleTracing();

		this.appmop.connectionDispatcherWithRequestGeneratorForSubmission(DispatcherRequestSubmissionInboundPortURI+dispatcherList.size());

		this.appmop.connectionDispatcherWithRequestGeneratorForNotification(rop, DispatcherRequestNotificationOutboundPortURI+dispatcherList.size());		

		System.out.println("connection with the request generator done");

		// connect dispatcher to VM
		this.addRequiredInterface(DispatcherManagementI.class);
		this.dmop = new DispatcherManagementOutboundport("dmop", this);
		this.addPort(dmop);
		this.dmop.publishPort();
		this.dmop.doConnection(
				DispatcherManagementInboundPortURI+dispatcherList.size(),
				DispatcherManagementConnector.class.getCanonicalName());

		// modify for multiple vm
		for(int i=0; i<vmList.size();i++){
			this.dmop.addVirtualMachine(vmList.get(i));
		}

		//rop.doDisconnection();

		System.out.println("connect dispatcher to the vm");

		// connect applicationVM
		rop.doConnection("VM"+"0", ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(
				VmRequestNotificationOutboundPortURI+"0",
				DispatcherRequestNotificationInboundPortURI+dispatcherList.size(),
				RequestNotificationConnector.class.getCanonicalName());

		// connect applicationVM
		rop.doConnection("VM"+"1", ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(
				VmRequestNotificationOutboundPortURI+"1",
				DispatcherRequestNotificationInboundPortURI+dispatcherList.size(),
				RequestNotificationConnector.class.getCanonicalName());

		System.out.println("connection app vm");

		//rop.doDisconnection();

		System.out.println("finish creation");
	}

	@Override
	public void receiveApplicationToAdmissionController(String applicationURI) throws Exception {

		System.out.println("Application reçue");

		AllocatedCore[] allocatedCore1 = csop.allocateCores(NB_CORES);
		AllocatedCore[] allocatedCore2 = csop.allocateCores(NB_CORES);
		
		System.out.println("allocated core 1");
		for(int i=0; i<allocatedCore1.length; i++){
			System.out.println(allocatedCore1[i]);
		}

		System.out.println("allocated core 2");
		for(int i=0; i<allocatedCore2.length; i++){
			System.out.println(allocatedCore2[i]);
		}

		
		if (allocatedCore1.length==NB_CORES && allocatedCore2.length==NB_CORES) {
			System.out.println("Accept application " + applicationURI);
			deployDynamicComponentsForApplication(applicationURI, allocatedCore1, allocatedCore2);
			this.appcnop.responseFromApplicationController(true);

		} else {
			System.out.println("Application rejected");	
			this.appcnop.responseFromApplicationController(false);
		}	

	}
}
