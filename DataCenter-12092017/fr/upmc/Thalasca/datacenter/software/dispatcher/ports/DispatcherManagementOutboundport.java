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
	public void addVirtualMachine(String requestSubmissionInboundPortURI) throws Exception {
		( ( DispatcherManagementI ) this.connector ).addVirtualMachine(requestSubmissionInboundPortURI);
	}

}
