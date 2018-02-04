package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public interface ApplicationAcceptNotificationI
extends OfferedI, RequiredI{

	public void acceptResponseFromAdmissionController(boolean response, String applicationUri) throws Exception;
	
}
