package fr.upmc.Thalasca.datacenterclient.Application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationAcceptNotificationI
extends OfferedI, RequiredI{

	public void acceptResponseFromApplicationController(boolean response) throws Exception;
	
}
