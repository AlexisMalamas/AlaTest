package fr.upmc.datacenterclient.application.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.application.interfaces.ApplicationNotificationI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationNotificationOutboundPort 
extends AbstractOutboundPort 
implements ApplicationNotificationI {

    public ApplicationNotificationOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationNotificationI.class , owner );
    }

    @Override
    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI , String rdnopUri ) throws Exception {
        ( ( ApplicationNotificationI ) this.connector )
                .notifyRequestGeneratorCreated( requestNotificationInboundPortURI , rdnopUri );
    }
}
