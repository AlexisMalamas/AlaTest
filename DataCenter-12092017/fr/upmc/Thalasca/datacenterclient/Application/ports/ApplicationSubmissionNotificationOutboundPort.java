package fr.upmc.Thalasca.datacenterclient.Application.ports;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * 
 * The class <code>ApplicationSubmissionNotificationOutboundPort</code> implements a data
 * outbound port requiring the <code>ApplicationSubmissionNotificationI</code> interface.
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationSubmissionNotificationOutboundPort
extends AbstractOutboundPort
implements ApplicationSubmissionNotificationI{
	

	public ApplicationSubmissionNotificationOutboundPort(ComponentI owner) throws Exception {
		
		super(ApplicationSubmissionNotificationI.class, owner);
	}
	
	public ApplicationSubmissionNotificationOutboundPort(String uri, ComponentI owner) throws Exception {
		
		super(uri, ApplicationSubmissionNotificationI.class, owner);

		assert uri != null;
	}
	
	@Override
	public void submitApplicationNotification(String application, ApplicationManagementOutBoundPort appmop, int nombreVM) throws Exception {
		((ApplicationSubmissionNotificationI)this.connector).
		submitApplicationNotification(application, appmop, nombreVM);
	}

}
