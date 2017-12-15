package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;

public interface ApplicationManagementI 
extends OfferedI, RequiredI{
	
	public void connectionDispatcherWithRequestGeneratorForSubmission(
			String DispatcherRequestSubmissionInboundPortURI, String applicationUri) throws Exception;
	
	public void connectionDispatcherWithRequestGeneratorForNotification(
			ReflectionOutboundPort ropDispatcher,
			String DispatcherRequestSubmissionInboundPortURI, String applicationUri) throws Exception;
	
	public void submitApplicationToAdmissionController(String applicationUri, int nombreVM) throws Exception;
	
	
}
