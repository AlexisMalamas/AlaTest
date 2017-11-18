package fr.upmc.Thalasca.datacenterclient.Application.ports;

import fr.upmc.Thalasca.datacenterclient.Application.Application;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationManagementI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationRequestI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;

public class ApplicationManagementInboundPort extends AbstractInboundPort implements ApplicationManagementI{

	private static final long serialVersionUID = 1L;

	public				ApplicationManagementInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(ApplicationManagementI.class, owner) ;

			assert	owner != null && owner instanceof Application ;
		}

		public				ApplicationManagementInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, ApplicationManagementI.class, owner);

			assert	owner != null && owner instanceof Application ;
		}

		@Override
		public void connectionDispatcherWithRequestGeneratorForSubmission(
				String DispatcherRequestSubmissionInboundPortURI) throws Exception {
			
			final Application application = (Application) this.owner;
			
			this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							application.connectionDispatcherWithRequestGeneratorForSubmission(
									DispatcherRequestSubmissionInboundPortURI);
							return null;
						}
					});	
			
		}

		@Override
		public void connectionDispatcherWithRequestGeneratorForNotification(ReflectionOutboundPort ropDispatcher,
				String DispatcherRequestSubmissionInboundPortURI) throws Exception {
			final Application application = (Application) this.owner;
			
			this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							application.connectionDispatcherWithRequestGeneratorForNotification(ropDispatcher, 
									DispatcherRequestSubmissionInboundPortURI);
							return null;
						}
					});
			
		}

		@Override
		public void submitApplicationToAdmissionController() throws Exception {
			
			final Application application = (Application) this.owner;
			
			this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							application.submitApplicationToAdmissionController();
							return null;
						}
					});	
			
		}

		
}
