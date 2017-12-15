package fr.upmc.Thalasca.datacenterclient.Application.connectors;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;

public class ApplicationManagementConnector 
extends AbstractConnector
implements ApplicationManagementI{

	@Override
	public void connectionDispatcherWithRequestGeneratorForSubmission(String DispatcherRequestSubmissionInboundPortURI, String applicationUri)
			throws Exception {
		
		((ApplicationManagementI)this.offering).
		connectionDispatcherWithRequestGeneratorForSubmission(DispatcherRequestSubmissionInboundPortURI, applicationUri);
		
	}

	@Override
	public void connectionDispatcherWithRequestGeneratorForNotification(ReflectionOutboundPort ropDispatcher,
			String DispatcherRequestSubmissionInboundPortURI, String applicationUri) throws Exception {
		
		((ApplicationManagementI)this.offering).connectionDispatcherWithRequestGeneratorForNotification(ropDispatcher, 
				DispatcherRequestSubmissionInboundPortURI, applicationUri);
	}

	@Override
	public void submitApplicationToAdmissionController(String applicationUri, int nombreVM) throws Exception {
		((ApplicationManagementI)this.offering).submitApplicationToAdmissionController(applicationUri, nombreVM);
		
	}
	
}
