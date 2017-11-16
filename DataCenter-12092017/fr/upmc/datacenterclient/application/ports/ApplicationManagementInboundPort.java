package fr.upmc.datacenterclient.application.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.application.Application;
import fr.upmc.datacenterclient.application.interfaces.ApplicationManagementI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */


public class ApplicationManagementInboundPort
extends AbstractInboundPort
implements ApplicationManagementI{

	private static final long serialVersionUID = 1L;

	public ApplicationManagementInboundPort( ComponentI owner ) throws Exception {
		super( ApplicationManagementI.class , owner );

	}

	public ApplicationManagementInboundPort( String uri , ComponentI owner ) throws Exception {
		super( uri , ApplicationManagementI.class , owner );
	}

	@Override
	public void submissionApplication() throws Exception {
		final Application ap = ( Application ) this.owner;

		this.owner.handleRequestAsync( new ComponentService<String>() {

			@Override
			public String call() throws Exception {
				ap.submissionApplication();
				return null;

			}
		} );

	}

	@Override
	public void stopApplication() throws Exception {
		final Application ap = ( Application ) this.owner;

		this.owner.handleRequestAsync( new ComponentService<String>() {

			@Override
			public String call() throws Exception {
				ap.stopApplication();
				return null;

			}
		} );

	}


}
