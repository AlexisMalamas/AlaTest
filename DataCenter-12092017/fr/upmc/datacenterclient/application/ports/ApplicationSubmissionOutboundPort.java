package fr.upmc.datacenterclient.application.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.application.interfaces.ApplicationSubmissionI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationSubmissionOutboundPort 
extends AbstractOutboundPort
implements ApplicationSubmissionI{

	public ApplicationSubmissionOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}
	
	public ApplicationSubmissionOutboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void submissionApplication() {
		// TODO Auto-generated method stub
		
	}

	

}
