package fr.upmc.datacenterclient.application.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationManagementI 
extends OfferedI, RequiredI{

	
	public void submissionApplication() throws Exception;

    public void stopApplication() throws Exception;
}
