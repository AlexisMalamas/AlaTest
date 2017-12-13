package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 	Request from Application to AdmissionController
 * 
 * */
public interface ApplicationRequestI 
extends OfferedI, RequiredI{
	
	public void receiveApplicationToAdmissionController(String applicationURI, ApplicationManagementOutBoundPort appmop) throws Exception;

}
