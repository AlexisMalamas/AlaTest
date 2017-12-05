package fr.upmc.Thalasca.datacenter.software.dispatcher.ports;

import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class DispatcherManagementInboundport
extends AbstractInboundPort
implements DispatcherManagementI{

	private static final long serialVersionUID = 1L;

	public	DispatcherManagementInboundport(
			ComponentI owner
			) throws Exception
	{
		super(DispatcherManagementInboundport.class, owner) ;

		assert	owner instanceof DispatcherManagementI ;
	}

	public	DispatcherManagementInboundport(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, DispatcherManagementI.class, owner);

		assert	uri != null && owner instanceof DispatcherManagementI ;
	}

	@Override
	public void addVirtualMachine(String requestSubmissionInboundPortURI) throws Exception {

		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;

		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						dm.addVirtualMachine(requestSubmissionInboundPortURI) ;
						return null;
					}
				}) ;
	}

}
