package fr.upmc.Thalasca.datacenter.software.dispatcher.ports;

import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

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
	public void connectToVirtualMachine(String requestSubmissionInboundPortURI) throws Exception {

		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;

		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						dm.connectToVirtualMachine(requestSubmissionInboundPortURI) ;
						return null;
					}
				}) ;
	}

	@Override
	public void disconnectVirtualMachine() throws Exception {
		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;

		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						dm.disconnectVirtualMachine();
						return null;
					}
				}) ;
	}

	@Override
	public Long getAverageExecutionTimeRequest() throws Exception {
		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<Long>() {
					@Override
					public Long call() throws Exception {
						return dm.getAverageExecutionTimeRequest();
						
					}
				}) ;
	}

	@Override
	public Long getAverageExecutionTimeRequest(int vm) throws Exception {
		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<Long>() {
					@Override
					public Long call() throws Exception {
						return dm.getAverageExecutionTimeRequest(vm);
						
					}
				}) ;
	}

	@Override
	public int getNbConnectedVM() throws Exception {
		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return dm.getNbConnectedVM();
						
					}
				}) ;
	}
}
