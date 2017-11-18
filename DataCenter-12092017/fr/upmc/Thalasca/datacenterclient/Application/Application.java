package fr.upmc.Thalasca.datacenterclient.Application;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
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
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class Application 
extends AbstractComponent
implements ApplicationManagementI{
	
	protected final String requestGeneratorUri = "rg";
	
	public static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	public static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;
	
	protected final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;
	
	protected final String RequestGeneratorJvmUri = "rgjvmuri";
	
	protected DynamicComponentCreationOutboundPort portToRequestGeneratorJVM;
	protected RequestGeneratorManagementOutboundPort rgmop;
	protected ReflectionOutboundPort rop;
	
	private String applicationUri;

	public Application(String applicationUri) throws Exception 
	{
		super(1,1);
		this.applicationUri = applicationUri;
		
		this.addRequiredInterface(RequestGeneratorManagementI.class);
		this.rgmop = new RequestGeneratorManagementOutboundPort(this);
		this.addPort(this.rgmop);
		this.rgmop.publishPort();
		
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
			ApplicationVM.class.getCanonicalName(),
			new Object[] {
					this.requestGeneratorUri,			// generator component URI
					meanTime,			// mean time between two requests
					meanNumberInstructions,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					GeneratorRequestSubmissionOutboundPortURI,
					GeneratorRequestNotificationInboundPortURI});
		
		// connect request Generator
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
		
		
	}

	@Override
	public void connectionDispatcherWithRequestGeneratorForNotification(ReflectionOutboundPort ropDispatcher,
			String DispatcherRequestSubmissionInboundPortURI) throws Exception {
		
		
	}

	@Override
	public void submitApplicationToAdmissionController() throws Exception {
		System.out.println("Submit Application");
		
		createDynamicGeneratorRequestForApplication();
		
		this.asop.requestApplicationToAdmissionController(this.applicationUri);
		
	}

	@Override
	public void requestApplicationToAdmissionController(String application) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
