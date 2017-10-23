package fr.upmc.datacenter.software.admissionController.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.admissionController.AdmissionController;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionControllerI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class AdmissionControllerOutboundPort 
extends AbstractOutboundPort{
	
	public AdmissionControllerOutboundPort(
			ComponentI owner
			) throws Exception
	{
		super(AdmissionControllerI.class, owner) ;

		assert owner instanceof AdmissionController ;
	}

	public AdmissionControllerOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, AdmissionControllerI.class, owner);

		assert owner instanceof AdmissionController ;
	}

}
