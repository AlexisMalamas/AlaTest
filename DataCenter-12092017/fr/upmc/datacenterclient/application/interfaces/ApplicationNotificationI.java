package fr.upmc.datacenterclient.application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationNotificationI 
extends OfferedI, RequiredI{

	public void notifyRequestGeneratorCreated(String requestNotificationInboundPortURI , String requestDispatcherNotificationOutboundPortURI) throws Exception;

}
