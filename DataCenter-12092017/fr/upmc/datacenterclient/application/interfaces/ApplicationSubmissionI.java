package fr.upmc.datacenterclient.application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public interface ApplicationSubmissionI
extends OfferedI, RequiredI{

	public void submissionApplication();
	
}
