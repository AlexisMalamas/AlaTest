package fr.upmc.Thalasca.datacenterclient.Application.connectors;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;

public class ApplicationManagementConnector 
extends AbstractConnector
implements ApplicationManagementI{

	@Override
	public void connectionDispatcherWithRequestGeneratorForSubmission(String DispatcherRequestSubmissionInboundPortURI)
			throws Exception {
		
		((ApplicationManagementI)this.offering).
		connectionDispatcherWithRequestGeneratorForSubmission(DispatcherRequestSubmissionInboundPortURI);
		
	}

	@Override
	public void connectionDispatcherWithRequestGeneratorForNotification(ReflectionOutboundPort ropDispatcher,
			String DispatcherRequestSubmissionInboundPortURI) throws Exception {
		
		((ApplicationManagementI)this.offering).connectionDispatcherWithRequestGeneratorForNotification(ropDispatcher, 
				DispatcherRequestSubmissionInboundPortURI);
	}

	@Override
	public void submitApplicationToAdmissionController() throws Exception {
		((ApplicationManagementI)this.offering).submitApplicationToAdmissionController();
		
	}
	
}
