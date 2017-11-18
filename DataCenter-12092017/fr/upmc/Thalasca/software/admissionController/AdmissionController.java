package fr.upmc.Thalasca.software.admissionController;

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
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.Thalasca.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;

public class AdmissionController extends AbstractComponent {
	
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
	
	
	protected final String ApplicationVmURI = "avmuri";
	protected final String DispatcherURI = "duri";
	
	protected DynamicComponentCreationOutboundPort portApplicationVM;
	protected DynamicComponentCreationOutboundPort portDispatcher;
	
	protected ComputerServicesOutboundPort csop;
	protected ComputerStaticStateDataOutboundPort cssdop;
	protected ComputerDynamicStateDataOutboundPort cdsdop;
	protected ApplicationManagementOutBoundPort amop;
	protected ApplicationVMManagementOutboundPort avmOutBoundPort;
	
	public AdmissionController(
			String computerServicesOutboundPortURI,
			String computerStaticStateDataOutboundPortURI,
			String computerDynamicStateDataOutboundPortURI,
			String applicationManagementOutboundPortURI, 
			String computerURI) throws Exception
	{
		super(1, 1);
		
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
		this.amop = new ApplicationManagementOutBoundPort(applicationManagementOutboundPortURI, this);
		this.addPort(this.amop);
		this.amop.publishPort();										
		
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
			this.portDispatcher.localPublishPort();
			this.addPort(this.portDispatcher);
			this.portDispatcher.doConnection(					
					this.DispatcherURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());
		
			// start the pushing of dynamic state information from the computer;
			// here only one push of information is planned after one second.
			this.cdsdop.startUnlimitedPushing(1000);
			//this.cdsdop.startLimitedPushing(1000, 25);
													
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
			if (this.amop.connected()) {
				this.amop.doDisconnection();
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
	
	public void acceptApplication(String applicationUri, int nbCoresForApplication) throws Exception {
		System.out.println("Accept application " + applicationUri);
		deployDynamicComponentsForApplication(applicationUri);
		
		AllocatedCore[] ac = this.csop.allocateCores(nbCoresForApplication);
		this.avmOutBoundPort.allocateCores(ac);
	}
	
	public void rejectApplication(String appUri) {
		System.out.println("Application rejected");	
	}
	
	public void deployDynamicComponentsForApplication(String applicationUri) throws Exception {						 			
		System.out.println("Deploy dynamic components for " + applicationUri);
		
		//create applicationVM for accepted application
		this.portApplicationVM.createComponent(
				ApplicationVM.class.getCanonicalName(),
				new Object[] {
						"applicationVM",
						ApplicationVMManagementInboundPortURI,
						VmRequestSubmissionInboundPortURI,
						VmRequestNotificationOutboundPortURI
				});			
		
		// create dispatcher for accepted application
		this.portDispatcher.createComponent(
				Dispatcher.class.getCanonicalName(),
				new Object[] {
						"dispatcher",							
						DispatcherRequestSubmissionInboundPortURI,
						DispatcherRequestSubmissionOutboundPortURI,
						DispatcherRequestNotificationOutboundPortURI,
						DispatcherRequestNotificationInboundPortURI
				});					
		
		// create ApplicationVMManagementOutboundPort 
		this.avmOutBoundPort = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
						new AbstractComponent(0, 0) {});
		this.avmOutBoundPort.publishPort();
		this.avmOutBoundPort.doConnection(
				ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName());			
		
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		this.addPort(rop);
		rop.localPublishPort();		
		
		// connect dispatcher
		rop.doConnection("dispatcher", ReflectionConnector.class.getCanonicalName());
		rop.toggleLogging();
		rop.toggleTracing();
		
		
		this.amop.connectionDispatcherWithRequestGeneratorForSubmission(DispatcherRequestSubmissionInboundPortURI);
		
		this.amop.connectionDispatcherWithRequestGeneratorForNotification(rop, DispatcherRequestNotificationOutboundPortURI);		
		
		// connect dispatcher to VM
		rop.doPortConnection(
				DispatcherRequestSubmissionOutboundPortURI,
				VmRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());
		
		rop.doDisconnection();
		
		// connect applicationVM
		rop.doConnection("applicationVM", ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();
		
		rop.doPortConnection(
				VmRequestNotificationOutboundPortURI,
				DispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
		
		rop.doDisconnection();
	}
}
