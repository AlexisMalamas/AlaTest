package fr.upmc.datacenterclient.application.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.application.interfaces.ApplicationSubmissionI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class ApplicationSubmissionConnector 
extends AbstractConnector 
implements ApplicationSubmissionI {

	@Override
	public String[] submissionApplication() throws Exception {
		return ( ( ApplicationSubmissionI ) this.offering ).submissionApplication();
	}
}
