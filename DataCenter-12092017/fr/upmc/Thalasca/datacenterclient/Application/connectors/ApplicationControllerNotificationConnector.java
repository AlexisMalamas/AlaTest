package fr.upmc.Thalasca.datacenterclient.Application.connectors;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.components.connectors.AbstractConnector;

public class ApplicationControllerNotificationConnector
extends AbstractConnector
implements ApplicationControllerNotificationI{

	@Override
	public void responseFromApplicationController(boolean response) throws Exception {
		((ApplicationControllerNotificationI)this.offering).responseFromApplicationController(response);
		
	}

}
