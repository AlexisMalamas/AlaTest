package fr.upmc.Thalasca.software.performanceController.ports;

import java.util.ArrayList;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;


/**
 * 
 * @author Kevin GESNOUIN and Alexis MALAMAS
 *
 */

public class PerformanceControllerInboundPort
extends AbstractInboundPort
implements PerformanceControllerManagementI{

	private static final long serialVersionUID = 1L;

	public PerformanceControllerInboundPort(ComponentI owner) throws Exception {

		super(PerformanceControllerManagementI.class, owner);

		assert owner instanceof PerformanceControllerManagementI;		
	}

	public PerformanceControllerInboundPort(String uri, ComponentI owner) throws Exception {

		super(uri, PerformanceControllerManagementI.class, owner);

		assert uri != null && owner instanceof PerformanceControllerManagementI;
	}

	@Override
	public void sendVirtualMachineAvailable(ArrayList<VM> listVM) throws Exception {
		final PerformanceControllerManagementI pc = (PerformanceControllerManagementI) this.owner;

		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						pc.sendVirtualMachineAvailable(listVM);
						return null;
					}
				}) ;
	}

}
