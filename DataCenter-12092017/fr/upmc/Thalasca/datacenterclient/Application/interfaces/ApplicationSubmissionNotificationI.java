package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationSubmissionNotificationI
extends OfferedI, RequiredI{
	public void submitApplicationNotification(String application, ApplicationManagementOutBoundPort appmop, int nombreVM) throws Exception;
}
