package fr.upmc.Thalasca.datacenterclient.Application;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationAcceptNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationControllerNotificationInboundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementInboundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationSubmissionNotificationOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class Application 
extends AbstractComponent
implements ApplicationManagementI, ApplicationAcceptNotificationI{
	
	protected final String requestGeneratorUri = "rg";
	
	protected final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	protected final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;
	
	protected final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	protected final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;
	
	protected final String RequestGeneratorJvmUri = "";
	
	protected DynamicComponentCreationOutboundPort portToRequestGeneratorJVM;
	protected RequestGeneratorManagementOutboundPort rgmop;
	protected ReflectionOutboundPort rop;
	
	protected ApplicationManagementInboundPort appmip;
	protected ApplicationSubmissionNotificationOutboundPort appsnop;
	protected ApplicationControllerNotificationInboundPort appcnip;
	
	private String applicationUri;
	
	public ApplicationSubmissionNotificationOutboundPort asnop;

	public Application(String applicationUri,
			String applicationControllerNotificationInboundPortURI,
			String applicationManagementInboundPortURI,
			String applicationSubmissionNotificationOutboundPortURI
			) throws Exception 
	{
		super(1,1);
		this.applicationUri = applicationUri;
		
		this.addRequiredInterface(RequestGeneratorManagementI.class);
		this.rgmop = new RequestGeneratorManagementOutboundPort(this);
		this.addPort(this.rgmop);
		this.rgmop.publishPort();
		
		this.addOfferedInterface(ApplicationControllerNotificationI.class);
		this.appcnip = new ApplicationControllerNotificationInboundPort(applicationControllerNotificationInboundPortURI, this);
		this.addPort(this.appcnip);
		this.appcnip.publishPort();
		
		this.addOfferedInterface(ApplicationManagementI.class);
		this.appmip = new ApplicationManagementInboundPort(applicationManagementInboundPortURI, this);
		this.addPort(this.appmip);
		this.appmip.publishPort();
		
		this.addRequiredInterface(ApplicationSubmissionNotificationI.class);
		this.appsnop = new ApplicationSubmissionNotificationOutboundPort(applicationSubmissionNotificationOutboundPortURI, this);
		this.addPort(this.appsnop);
		this.appsnop.publishPort();	
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		
	}
	
	@Override
	public void start() throws ComponentStartException {
		
		try {
			this.portToRequestGeneratorJVM = new DynamicComponentCreationOutboundPort(this);
			this.portToRequestGeneratorJVM.localPublishPort();
			this.addPort(this.portToRequestGeneratorJVM);
			this.portToRequestGeneratorJVM.doConnection(					
					this.RequestGeneratorJvmUri + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());								
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
		super.start();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {	

		try {
			if(this.portToRequestGeneratorJVM.connected()) {
				this.portToRequestGeneratorJVM.doDisconnection();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}	

	public void createDynamicGeneratorRequestForApplication() throws Exception
	{
		Double meanTime = 500.0;
		Long meanNumberInstructions = 6000000000L;
		
		// create request Generator
		this.portToRequestGeneratorJVM.createComponent(
			RequestGenerator.class.getCanonicalName(),
			new Object[] {
					this.requestGeneratorUri,			// generator component URI
					meanTime,			// mean time between two requests
					meanNumberInstructions,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					GeneratorRequestSubmissionOutboundPortURI,
					GeneratorRequestNotificationInboundPortURI});
		// connect request Generator
		
		System.out.println("test");
		rop = new ReflectionOutboundPort(this);
		this.addPort(rop);
		rop.localPublishPort();
		rop.doConnection(this.requestGeneratorUri, ReflectionConnector.class.getCanonicalName());
		rop.toggleLogging();
		rop.toggleTracing();
	}
	
	public void	launch() throws Exception {				
		this.rgmop.doConnection(
				this.RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName());			
									
		this.rgmop.startGeneration();
		Thread.sleep(20000L);		
		this.rgmop.stopGeneration();
	}

	@Override
	public void connectionDispatcherWithRequestGeneratorForSubmission(String DispatcherRequestSubmissionInboundPortURI)
			throws Exception {
		
		rop.doPortConnection(
				this.GeneratorRequestSubmissionOutboundPortURI,
				DispatcherRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());
	}

	@Override
	public void connectionDispatcherWithRequestGeneratorForNotification(ReflectionOutboundPort ropDispatcher,
			String DispatcherRequestSubmissionInboundPortURI) throws Exception {
		
		ropDispatcher.doPortConnection(
				DispatcherRequestSubmissionInboundPortURI,
				GeneratorRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
	}

	@Override
	public void submitApplicationToAdmissionController() throws Exception {
		System.out.println("Submit Application");
		
		createDynamicGeneratorRequestForApplication();
		
		System.out.println("Request generator created");
		
		this.asnop.submitApplicationNotification(this.applicationUri);
		
	}

	@Override
	public void acceptResponseFromApplicationController(boolean response) throws Exception {
		
		System.out.println("Response from AdmissionController "+response );
		
		if (response) {				
			launch();					
		}
		
	}


}
