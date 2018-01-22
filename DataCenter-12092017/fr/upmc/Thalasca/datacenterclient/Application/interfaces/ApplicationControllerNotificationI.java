package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public interface ApplicationControllerNotificationI 
extends OfferedI, RequiredI {

	public void responseFromApplicationController(boolean response, String applicationUri) throws Exception;
}
