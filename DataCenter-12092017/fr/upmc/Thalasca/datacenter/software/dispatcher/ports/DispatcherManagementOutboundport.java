package fr.upmc.Thalasca.datacenter.software.dispatcher.ports;

import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

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
	public void connectToVirtualMachine(String requestSubmissionInboundPortURI) throws Exception {
		( ( DispatcherManagementI ) this.connector ).connectToVirtualMachine(requestSubmissionInboundPortURI);
	}

	@Override
	public void disconnectVirtualMachine() throws Exception {
		( ( DispatcherManagementI ) this.connector ).disconnectVirtualMachine();
		
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
	public void addNotificationPortForVmInDispatcher(String requestNotificationInboundPortURI) throws Exception {
		( ( DispatcherManagementI ) this.connector ).addNotificationPortForVmInDispatcher(requestNotificationInboundPortURI);
		
	}

}
