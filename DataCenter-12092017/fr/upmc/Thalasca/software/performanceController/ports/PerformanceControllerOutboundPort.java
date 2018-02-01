package fr.upmc.Thalasca.software.performanceController.ports;

import java.util.ArrayList;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerManagementI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;


/**
 * 
 * @author Kevin GESNOUIN and Alexis MALAMAS
 *
 */

public class PerformanceControllerOutboundPort
extends AbstractOutboundPort
implements PerformanceControllerManagementI{

	
	public PerformanceControllerOutboundPort(
			ComponentI owner
			) throws Exception
		{
			super(PerformanceControllerManagementI.class, owner) ;

			assert	owner != null ;
		}

		public PerformanceControllerOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, PerformanceControllerManagementI.class, owner) ;

			assert	uri != null && owner != null ;
		}

		@Override
		public void sendVirtualMachineAvailable(ArrayList<VM> listVM) throws Exception {
			((PerformanceControllerManagementI)this.connector).sendVirtualMachineAvailable(listVM);
			
		}

}
