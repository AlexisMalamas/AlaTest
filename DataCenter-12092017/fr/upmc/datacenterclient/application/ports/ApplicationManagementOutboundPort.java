package fr.upmc.datacenterclient.application.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.application.interfaces.ApplicationManagementI;

public class ApplicationManagementOutboundPort
extends AbstractOutboundPort
implements ApplicationManagementI{

	
	public ApplicationManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationManagementI.class , owner );
    }

    @Override
    public void submissionApplication() throws Exception {
        ( ( ApplicationManagementI ) this.connector ).submissionApplication();

    }

    @Override
    public void stopApplication() throws Exception {
        ( ( ApplicationManagementI ) this.connector ).stopApplication();
    }

}
