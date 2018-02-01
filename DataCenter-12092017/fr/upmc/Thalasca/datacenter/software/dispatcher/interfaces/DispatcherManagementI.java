package fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public interface DispatcherManagementI
extends OfferedI, RequiredI{
	
	public void connectToVirtualMachine(VM vm) throws Exception;
	public void disconnectVirtualMachine() throws Exception;
	public Long getAverageExecutionTimeRequest() throws Exception;
	public Long getAverageExecutionTimeRequest(int vm) throws Exception;
	public int getNbConnectedVM() throws Exception;
	
}
