package fr.upmc.Thalasca.datacenterclient.Application.connectors;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationControllerNotificationConnector
extends AbstractConnector
implements ApplicationControllerNotificationI{

	@Override
	public void responseFromApplicationController(boolean response, String applicationUri) throws Exception {
		((ApplicationControllerNotificationI)this.offering).responseFromApplicationController(response, applicationUri);
		
	}

}
