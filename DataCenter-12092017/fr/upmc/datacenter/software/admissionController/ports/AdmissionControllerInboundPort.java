package fr.upmc.datacenter.software.admissionController.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissionController.AdmissionController;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionControllerI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class AdmissionControllerInboundPort 
extends AbstractInboundPort{

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public AdmissionControllerInboundPort(
			ComponentI owner
			) throws Exception
	{
		super(AdmissionControllerI.class, owner) ;

		assert owner instanceof AdmissionController ;
	}

	public AdmissionControllerInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, AdmissionControllerI.class, owner);

		assert owner instanceof AdmissionController ;
	}
}
