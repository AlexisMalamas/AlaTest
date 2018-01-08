package fr.upmc.Thalasca.datacenter.software.dispatcher.connectors;

import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.components.connectors.AbstractConnector;

public class DispatcherManagementConnector
extends AbstractConnector
implements	DispatcherManagementI
{
	@Override
	public void addVirtualMachine(String requestSubmissionInboundPortURI) throws Exception {
		( ( DispatcherManagementI ) this.offering ).addVirtualMachine(requestSubmissionInboundPortURI);
	}

	@Override
	public void removeVirtualMachine() throws Exception {
		( ( DispatcherManagementI ) this.offering ).removeVirtualMachine();
		
	}

	@Override
	public Long getAverageExecutionTimeRequest() throws Exception {
		return ( ( DispatcherManagementI ) this.offering ).getAverageExecutionTimeRequest();
	}
}
