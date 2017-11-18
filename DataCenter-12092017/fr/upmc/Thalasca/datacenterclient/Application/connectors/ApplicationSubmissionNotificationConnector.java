package fr.upmc.Thalasca.datacenterclient.Application.connectors;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.components.connectors.AbstractConnector;

public class ApplicationSubmissionNotificationConnector 
extends AbstractConnector
implements ApplicationSubmissionNotificationI{

	@Override
	public void submitApplicationNotification(String application) throws Exception {
		((ApplicationSubmissionNotificationI)this.offering).submitApplicationNotification(application);
		
	}

}