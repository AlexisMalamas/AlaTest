package fr.upmc.Thalasca.datacenterclient.Application.ports;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * 
 * The class <code>ApplicationControllerNotificationOutboundPort</code> implements a data
 * outbound port requiring the <code>ApplicationControllerNotificationI</code> interface.
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationControllerNotificationOutboundPort 
extends AbstractOutboundPort
implements ApplicationControllerNotificationI{

public ApplicationControllerNotificationOutboundPort(ComponentI owner) throws Exception {
		
		super(ApplicationControllerNotificationI.class, owner);
	}
	
	public ApplicationControllerNotificationOutboundPort(String uri, ComponentI owner) throws Exception {
			
		super(uri, ApplicationControllerNotificationI.class, owner);

		assert uri != null;
	}
	
	@Override
	public void responseFromAdmissionController(boolean response, String applicationUri) throws Exception {
		((ApplicationControllerNotificationI)this.connector).responseFromAdmissionController(response, applicationUri);			
	}
}
