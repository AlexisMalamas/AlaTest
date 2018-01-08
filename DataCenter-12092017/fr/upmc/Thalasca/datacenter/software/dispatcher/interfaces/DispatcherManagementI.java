package fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface DispatcherManagementI
extends OfferedI, RequiredI{
	
	public void addVirtualMachine(String requestSubmissionInboundPortURI) throws Exception;
	public void removeVirtualMachine() throws Exception;
	public Long getAverageExecutionTimeRequest() throws Exception;
	
}
