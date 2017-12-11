package fr.upmc.Thalasca.datacenterclient.Application.ports;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationAcceptNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ApplicationControllerNotificationInboundPort 
extends AbstractInboundPort
implements ApplicationControllerNotificationI{

	private static final long serialVersionUID = 1L;

	public ApplicationControllerNotificationInboundPort(ComponentI owner) throws Exception {
		
		super(ApplicationControllerNotificationI.class, owner);

		assert owner instanceof ApplicationAcceptNotificationI;		
	}
	
	public ApplicationControllerNotificationInboundPort(String uri, ComponentI owner) throws Exception {
			
		super(uri, ApplicationControllerNotificationI.class, owner);

		assert uri != null && owner instanceof ApplicationAcceptNotificationI;
	}
	
	@Override
	public void responseFromApplicationController(boolean response, String applicationUri) throws Exception {
		
		final ApplicationAcceptNotificationI application = (ApplicationAcceptNotificationI) this.owner;
		
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						application.acceptResponseFromApplicationController(response, applicationUri);
						return null;
					}
				});		
	}
}
