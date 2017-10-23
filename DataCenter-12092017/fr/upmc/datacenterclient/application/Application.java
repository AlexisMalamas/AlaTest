package fr.upmc.datacenterclient.application;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenterclient.application.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.application.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;

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
		
		//create the request generator
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
