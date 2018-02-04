package fr.upmc.Thalasca.datacenterclient.Application.connectors;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * 
 * The class <code>ApplicationControllerNotificationConnector</code> implements the
 * connector between outbound and inboud ports implementing the interface
 * <code>ApplicationControllerNotificationI</code>.
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationControllerNotificationConnector
extends AbstractConnector
implements ApplicationControllerNotificationI{

	@Override
	public void responseFromAdmissionController(boolean response, String applicationUri) throws Exception {
		((ApplicationControllerNotificationI)this.offering).responseFromAdmissionController(response, applicationUri);
		
	}

}
