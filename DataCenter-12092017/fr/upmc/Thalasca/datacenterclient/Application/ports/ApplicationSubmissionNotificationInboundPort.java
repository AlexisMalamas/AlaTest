package fr.upmc.Thalasca.datacenterclient.Application.ports;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationRequestI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * 
 * The class <code>ApplicationSubmissionNotificationInboundPort</code> implements a data
 * inbound port offering the <code>ApplicationSubmissionNotificationI</code> interface.
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationSubmissionNotificationInboundPort
extends AbstractInboundPort
implements ApplicationSubmissionNotificationI{

	
	private static final long serialVersionUID = 1L;

	public ApplicationSubmissionNotificationInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(ApplicationSubmissionNotificationI.class, owner) ;

			assert	owner != null && owner instanceof ApplicationRequestI ;
		}

	public ApplicationSubmissionNotificationInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ApplicationSubmissionNotificationI.class, owner);

		assert	owner != null && owner instanceof ApplicationRequestI ;
	}
	
	@Override
	public void submitApplicationNotification(String application, ApplicationManagementOutBoundPort appmop, int nombreVM) throws Exception {
		final ApplicationRequestI app = (ApplicationRequestI) this.owner;
				
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						app.receiveApplicationToAdmissionController(application, appmop, nombreVM);
						return null;
					}
				});
		
	}
}
