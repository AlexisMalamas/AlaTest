package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationSubmissionNotificationI
extends OfferedI, RequiredI{

	
	public void submitApplicationNotification(String application) throws Exception;
}
