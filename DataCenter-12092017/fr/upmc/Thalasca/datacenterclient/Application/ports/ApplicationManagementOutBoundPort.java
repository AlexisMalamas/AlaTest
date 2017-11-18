package fr.upmc.Thalasca.datacenterclient.Application.ports;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;

public class ApplicationManagementOutBoundPort extends AbstractOutboundPort implements ApplicationManagementI {
	public ApplicationManagementOutBoundPort(
			ComponentI owner
			) throws Exception
		{
			super(ApplicationManagementI.class, owner) ;

			assert	owner != null ;
		}

		public ApplicationManagementOutBoundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, ApplicationManagementI.class, owner) ;

			assert	uri != null && owner != null ;
		}

		@Override
		public void connectionDispatcherWithRequestGeneratorForSubmission(
				String DispatcherRequestSubmissionInboundPortURI) throws Exception {
			((ApplicationManagementI)this.connector).
			connectionDispatcherWithRequestGeneratorForSubmission(DispatcherRequestSubmissionInboundPortURI);
			
		}

		@Override
		public void connectionDispatcherWithRequestGeneratorForNotification(ReflectionOutboundPort ropDispatcher,
				String DispatcherRequestSubmissionInboundPortURI) throws Exception {
			((ApplicationManagementI)this.connector).
			connectionDispatcherWithRequestGeneratorForNotification(ropDispatcher, DispatcherRequestSubmissionInboundPortURI);
			
		}

		@Override
		public void submitApplicationToAdmissionController() throws Exception {
			((ApplicationManagementI)this.connector).
			submitApplicationToAdmissionController();
		}

		@Override
		public void requestApplicationToAdmissionController(String application) throws Exception {
			((ApplicationManagementI)this.connector).
			requestApplicationToAdmissionController(application);
		}
}
