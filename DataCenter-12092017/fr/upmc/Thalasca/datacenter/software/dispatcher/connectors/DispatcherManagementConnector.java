package fr.upmc.Thalasca.datacenter.software.dispatcher.connectors;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class DispatcherManagementConnector
extends AbstractConnector
implements	DispatcherManagementI
{
	@Override
	public void connectToVirtualMachine(VM vm) throws Exception {
		( ( DispatcherManagementI ) this.offering ).connectToVirtualMachine(vm);
	}

	@Override
	public VM disconnectVirtualMachine() throws Exception {
		return ( ( DispatcherManagementI ) this.offering ).disconnectVirtualMachine();
		
	}

	@Override
	public Long getAverageExecutionTimeRequest() throws Exception {
		return ( ( DispatcherManagementI ) this.offering ).getAverageExecutionTimeRequest();
	}

	@Override
	public Long getAverageExecutionTimeRequest(int vm) throws Exception {
		return ( ( DispatcherManagementI ) this.offering ).getAverageExecutionTimeRequest(vm);
	}

	@Override
	public int getNbConnectedVM() throws Exception{
		return ( ( DispatcherManagementI ) this.offering ).getNbConnectedVM();
	}

	@Override
	public int getIdVm(int i) throws Exception {
		return ( ( DispatcherManagementI ) this.offering ).getIdVm(i);
	}
}
