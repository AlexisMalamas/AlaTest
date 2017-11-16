package fr.upmc.datacenterclient.application.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissionController.AdmissionController;
import fr.upmc.datacenterclient.application.interfaces.ApplicationSubmissionI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationSubmissionInboundPort 
extends AbstractInboundPort
implements ApplicationSubmissionI{

	
	private static final long serialVersionUID = 1L;

	public ApplicationSubmissionInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}
	
	public ApplicationSubmissionInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] submissionApplication() throws Exception {
		// TODO Auto-generated method stub
		final AdmissionController ac = ( AdmissionController ) this.owner;
		return this.owner.handleRequestSync( new ComponentService<String[]>() {

            @Override
            public String[] call() throws Exception {
                return ac.submissionApplication();
            }
        } );
    }
	
	
}
