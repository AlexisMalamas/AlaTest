package fr.upmc.Thalasca.datacenter.software.dispatcher.ports;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

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
	public void connectToVirtualMachine(VM vm) throws Exception {

		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;

		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						dm.connectToVirtualMachine(vm) ;
						return null;
					}
				}) ;
	}

	@Override
	public VM disconnectVirtualMachine() throws Exception {
		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;

		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<VM>() {
					@Override
					public VM call() throws Exception {
						return dm.disconnectVirtualMachine();
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

	@Override
	public int getIdVm(int i) throws Exception {
		final DispatcherManagementI dm = ( DispatcherManagementI ) this.owner;
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return dm.getIdVm(i);
						
					}
				}) ;
	}
}
