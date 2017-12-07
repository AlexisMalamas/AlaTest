package fr.upmc.Thalasca.datacenterclient.Application;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationAcceptNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationControllerNotificationInboundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementInboundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationSubmissionNotificationOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
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

	protected RequestGenerator rg;
	
	protected String  RequestGeneratorJvmUri = "";
	protected static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	protected static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;
	protected static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	protected static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;	

	protected final Double meanTime = 500.0;
	protected final Long meanNumberInstructions = 6000000000L;

	protected DynamicComponentCreationOutboundPort portRequestGenerator;
	protected RequestGeneratorManagementOutboundPort rgmop;
	protected ReflectionOutboundPort rop;

	protected ApplicationManagementInboundPort appmip;
	protected ApplicationSubmissionNotificationOutboundPort appsnop;
	protected ApplicationControllerNotificationInboundPort appcnip;

	private String applicationUri;

	public Application(String applicationUri,
			String applicationControllerNotificationInboundPortURI,
			String applicationManagementInboundPortURI,
			String applicationSubmissionNotificationOutboundPortURI
			) throws Exception 
	{
		super(applicationUri, 1, 1);

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
	}

	@Override
	public void start() throws ComponentStartException {

		/*try {
			this.portRequestGenerator = new DynamicComponentCreationOutboundPort(this);
			this.portRequestGenerator.localPublishPort();
			this.addPort(this.portRequestGenerator);
			this.portRequestGenerator.doConnection(					
					this.RequestGeneratorJvmUri + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

		super.start();*/
	}

	@Override
	public void shutdown() throws ComponentShutdownException {	

		try {
			if(this.portRequestGenerator.connected()) {
				this.portRequestGenerator.doDisconnection();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}	

	/**
	 * 
	 * Create dynamically request generator of application
	 * 
	 * */
	public void createDynamicRequestGenerator() throws Exception
	{	
		// create request generator
		/*System.out.println("Start creation of request generator");
		this.portRequestGenerator.createComponent(
			RequestGenerator.class.getCanonicalName(),
			new Object[] {
					this.requestGeneratorUri,			// generator component URI
					this.meanTime,						// mean time between two requests
					this.meanNumberInstructions,		// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					GeneratorRequestSubmissionOutboundPortURI,
					GeneratorRequestNotificationInboundPortURI});

		System.out.println("Request generator requested");

		rop = new ReflectionOutboundPort(this);
		this.addPort(rop);
		rop.localPublishPort();
		rop.doConnection(this.requestGeneratorUri, ReflectionConnector.class.getCanonicalName());
		rop.toggleLogging();
		rop.toggleTracing();*/

		 this.rg = new RequestGenerator(
				applicationUri+"_rg",			// generator component URI
				500.0,			// mean time between two requests
				6000000000L,	// mean number of instructions in requests
				applicationUri+"_"+RequestGeneratorManagementInboundPortURI,
				applicationUri+"_"+GeneratorRequestSubmissionOutboundPortURI,
				applicationUri+"_"+GeneratorRequestNotificationInboundPortURI) ;
		//this.addDeployedComponent(rg) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		this.rg.toggleTracing() ;
		this.rg.toggleLogging() ;
	}

	@Override
	public void connectionDispatcherWithRequestGeneratorForSubmission(String DispatcherRequestSubmissionInboundPortURI)
			throws Exception {

		this.rg.doPortConnection(
				applicationUri+"_"+GeneratorRequestSubmissionOutboundPortURI,
				DispatcherRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());
	}

	@Override
	public void connectionDispatcherWithRequestGeneratorForNotification(ReflectionOutboundPort ropDispatcher,
			String DispatcherRequestSubmissionInboundPortURI) throws Exception {

		ropDispatcher.doPortConnection(
				DispatcherRequestSubmissionInboundPortURI,
				applicationUri+"_"+GeneratorRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
	}

	@Override
	public void submitApplicationToAdmissionController() throws Exception {
		System.out.println("Submit Application");

		createDynamicRequestGenerator();

		System.out.println("Request generator created");

		this.appsnop.submitApplicationNotification(this.applicationUri);

	}

	@Override
	public void acceptResponseFromApplicationController(boolean response) throws Exception {

		System.out.println("Response from AdmissionController "+response );

		if (response) {	
			this.rgmop.doConnection(
					applicationUri+"_"+RequestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName());			

			this.rgmop.startGeneration();
			Thread.sleep(20000L);		
			this.rgmop.stopGeneration();					
		}
		else
			System.out.println("Reponse negative from admission Controller");

	}


}
