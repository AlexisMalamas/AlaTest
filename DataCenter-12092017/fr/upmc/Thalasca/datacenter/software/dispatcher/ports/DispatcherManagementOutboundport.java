package fr.upmc.Thalasca.datacenter.software.dispatcher.ports;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * 
 * The class <code>DispatcherManagementOutboundport</code> implements a data
 * outbound port requiring the <code>DispatcherManagementI</code> interface.
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */
public class DispatcherManagementOutboundport 
extends		AbstractOutboundPort
implements	DispatcherManagementI
{

	public DispatcherManagementOutboundport(
		ComponentI owner
		) throws Exception
	{
		super(DispatcherManagementI.class, owner) ;
	}

	public DispatcherManagementOutboundport(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, DispatcherManagementI.class, owner);
	}
	
	@Override
	public void connectToVirtualMachine(VM vm) throws Exception {
		( ( DispatcherManagementI ) this.connector ).connectToVirtualMachine(vm);
	}

	@Override
	public VM disconnectVirtualMachine() throws Exception {
		return ( ( DispatcherManagementI ) this.connector ).disconnectVirtualMachine();
		
	}

	@Override
	public Long getAverageExecutionTimeRequest() throws Exception {
		return ( ( DispatcherManagementI ) this.connector ).getAverageExecutionTimeRequest();
	}

	@Override
	public Long getAverageExecutionTimeRequest(int vm) throws Exception {
		return ( ( DispatcherManagementI ) this.connector ).getAverageExecutionTimeRequest(vm);
	}

	@Override
	public int getNbConnectedVM() throws Exception {
		return ( ( DispatcherManagementI ) this.connector ).getNbConnectedVM();
	}

	@Override
	public int getIdVm(int i) throws Exception {
		return ( ( DispatcherManagementI ) this.connector ).getIdVm(i);
	}

}
