package fr.upmc.Thalasca;

import java.util.ArrayList;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.application.interfaces.ApplicationManagementI;
import fr.upmc.datacenterclient.application.interfaces.ApplicationNotificationI;
import fr.upmc.datacenterclient.application.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.application.ports.ApplicationManagementOutboundPort;
import fr.upmc.datacenterclient.application.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.application.ports.ApplicationSubmissionInboundPort;

public class AdmissionController 
	extends AbstractComponent {
	
	public static final String	DispatcherRequestSubmissionInboundPortURI = "drsip" ;
	public static final String	DispatcherRequestSubmissionOutboundPortURI = "drsop" ;
	public static final String	DispatcherRequestNotificationInboundPortURI = "drnip" ;
	public static final String	DispatcherRequestNotificationOutboundPortURI = "drnop" ;
	
	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	
	public static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	public static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;
	
	public static final String	VmRequestNotificationOutboundPortURI = "vmrnop" ;
	public static final String	VmRequestSubmissionInboundPortURI = "vmsip" ;

	protected DynamicComponentCreationOutboundPort portApplicationVM;
	protected DynamicComponentCreationOutboundPort portDispatcher;
	
	protected ComputerServicesOutboundPort csop;
	protected ComputerStaticStateDataOutboundPort cssdop;
	protected ComputerDynamicStateDataOutboundPort cdsdop;
	protected ApplicationManagementOutboundPort amop;
	protected ApplicationSubmissionInboundPort asip;
	protected ApplicationNotificationOutboundPort anop;
	
	protected ApplicationVMManagementOutboundPort avmOutBoundPort;
	
	public AdmissionController(
			ArrayList<String> computersURI,			
			String computerServicesOutboundPortURI,
			String computerStaticStateDataOutboundPortURI,
			String computerDynamicStateDataOutboundPortURI,
			String applicationManagementOutboundPortURI,
			String applicationSubmissionInboundPortURI,
			String applicationNotificationOutboundPortURI) throws Exception {
		
		super(1, 1);
		
		this.addRequiredInterface(ComputerServicesI.class);
		this.csop = new ComputerServicesOutboundPort(computerServicesOutboundPortURI, this);
		this.addPort(this.csop);
		this.csop.publishPort();
		
		this.cssdop = new ComputerStaticStateDataOutboundPort(computerStaticStateDataOutboundPortURI, this, computersURI.get(0));
		this.addPort(this.cssdop);
		this.cssdop.publishPort();

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		this.cdsdop = new ComputerDynamicStateDataOutboundPort(computerDynamicStateDataOutboundPortURI, this, computersURI.get(0));
		this.addPort(this.cdsdop);
		this.cdsdop.publishPort();
				
		this.addRequiredInterface(ApplicationManagementI.class);
		this.amop = new ApplicationManagementOutboundPort(applicationManagementOutboundPortURI, this);
		this.addPort(this.amop);
		this.amop.publishPort();
		
		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.asip = new ApplicationSubmissionInboundPort(applicationSubmissionInboundPortURI, ApplicationSubmissionI.class, this);
		this.addPort(this.asip);
		this.asip.publishPort();
		
		this.addRequiredInterface(ApplicationNotificationI.class);
		this.anop = new ApplicationNotificationOutboundPort(applicationNotificationOutboundPortURI, this);
		this.addPort(this.anop);
		this.anop.publishPort();											
		
		// maybe delete, maybe
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.portApplicationVM = new DynamicComponentCreationOutboundPort("test", this);
		this.addPort(this.portApplicationVM);
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.portDispatcher = new DynamicComponentCreationOutboundPort("test2", this);
		this.addPort(this.portDispatcher);
		
	}

	
	public void deployComponents(String applicationUri) throws Exception {						 			
	
		this.logMessage("Deploy components for " + applicationUri);
		
		// create ApplicationVM
		this.portApplicationVM.createComponent(
				ApplicationVM.class.getCanonicalName(),
				new Object[] {
						"vm",
						ApplicationVMManagementInboundPortURI,
					    VmRequestSubmissionInboundPortURI,
					    VmRequestNotificationOutboundPortURI
				});			
		
		// create Dispatcher
		this.portDispatcher.createComponent(
				Dispatcher.class.getCanonicalName(),
				new Object[] {
						"rd",							
						DispatcherRequestSubmissionInboundPortURI,
						DispatcherRequestSubmissionOutboundPortURI,
						DispatcherRequestNotificationInboundPortURI,
						DispatcherRequestNotificationOutboundPortURI
				});					
		
		// connect ApplicationVM to ApplicationVMManagement
		this.avmOutBoundPort = new ApplicationVMManagementOutboundPort(
						ApplicationVMManagementOutboundPortURI,
						new AbstractComponent(0, 0) {});
		this.avmOutBoundPort.publishPort();
		this.avmOutBoundPort.doConnection(
				ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName());
		
		// allocate ressources
				
	}
	
	public boolean applicationRequest(String appUri) throws Exception {
		
		this.logMessage("Application " + appUri + " request Admission controller");
		
		if (isAvailableComputer()) {
			acceptApplication(appUri);
			return true;
			
		} else {
			rejectApplication(appUri);
			return false;
		}		
	}
	
	public void acceptApplication(String applicationUri) throws Exception {
		
		this.logMessage("Admission controller accept application " + applicationUri);
		
		deployComponents(applicationUri);
		
	}
	
	public void rejectApplication(String appUri) {
		
		this.logMessage("Admission controller can't accept application " + appUri + " because of lack of resources.");		
	}
	
	//complete
	public boolean isAvailableComputer()
	{
		return true;
	}
}
