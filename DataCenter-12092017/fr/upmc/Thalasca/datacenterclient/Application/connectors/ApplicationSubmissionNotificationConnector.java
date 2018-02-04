package fr.upmc.Thalasca.datacenterclient.Application.connectors;

import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * 
 * The class <code>ApplicationSubmissionNotificationConnector</code> implements the
 * connector between outbound and inboud ports implementing the interface
 * <code>ApplicationSubmissionNotificationI</code>.
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationSubmissionNotificationConnector 
extends AbstractConnector
implements ApplicationSubmissionNotificationI{

	@Override
	public void submitApplicationNotification(String application, ApplicationManagementOutBoundPort appmop, int nombreVM) throws Exception {
		((ApplicationSubmissionNotificationI)this.offering).submitApplicationNotification(application, appmop, nombreVM);
		
	}

}
