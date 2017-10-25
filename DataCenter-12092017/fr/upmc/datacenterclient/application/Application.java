package fr.upmc.datacenterclient.application;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenterclient.application.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.application.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class Application 
extends AbstractComponent
implements ApplicationSubmissionI{

	// the uri application
	protected final String applicationURI;
	
	/**Request generator component.*/
	protected RequestGenerator rg ;
	
	/** the outbound port provided to manage the component.	*/
	protected RequestGeneratorManagementInboundPort rgmop ;
	
	/** the outbound port to submission the application */
	protected ApplicationSubmissionOutboundPort apsop;

	public Application(String applicationURI, String ApplicationSubmissionOutboundPortURI, String RequestGeneratorManagementInboundPort) throws Exception{
		
		super(1,1);
		
		this.applicationURI=applicationURI;
		
		this.addRequiredInterface(ApplicationSubmissionI.class) ;
		this.apsop = new ApplicationSubmissionOutboundPort(ApplicationSubmissionOutboundPortURI,ApplicationSubmissionI.class, this) ;
		this.addPort(this.apsop) ;
		this.apsop.publishPort() ;
		
		this.addRequiredInterface(RequestGeneratorManagementI.class) ;
		this.rgmop = new RequestGeneratorManagementInboundPort(RequestGeneratorManagementInboundPort, this) ;
		this.addPort(this.rgmop) ;
		this.rgmop.publishPort() ;
		
		
	}

	@Override
	public void submissionApplication() {
		// TODO Auto-generated method stub
		/*
		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		this.rg = new RequestGenerator(
					"rg",			// generator component URI
					500.0,			// mean time between two requests
					6000000000L,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					RequestSubmissionOutboundPortURI,
					RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		this.rg.toggleTracing() ;
		this.rg.toggleLogging() ;

		// Create a mock up port to manage to request generator component
		// (starting and stopping the generation).
		this.rgmop = new RequestGeneratorManagementOutboundPort(
							RequestGeneratorManagementOutboundPortURI,
							new AbstractComponent(0, 0) {}) ;
		this.rgmop.publishPort() ;
		this.rgmop.doConnection(
				RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------
		*/
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
            if (this.apsop.connected()) {
                this.apsop.doDisconnection();
            }
                       
            if (this.rgmop.connected()) 
            	this.rgmop.doDisconnection();
        }
        catch (Exception e) {
            throw new ComponentShutdownException(e);
        }
		super.shutdown();
	}
	
	

}
