package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;

public interface ApplicationManagementI 
extends OfferedI, RequiredI{
	
	public void connectionDispatcherWithRequestGeneratorForSubmission(
			String DispatcherRequestSubmissionInboundPortURI) throws Exception;
	public void connectionDispatcherWithRequestGeneratorForNotification(
			ReflectionOutboundPort ropDispatcher,
			String DispatcherRequestSubmissionInboundPortURI) throws Exception;
	
	public void submitApplicationToAdmissionController() throws Exception;
	
	public void requestApplicationToAdmissionController(String application) throws Exception;
}
