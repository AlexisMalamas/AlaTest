package fr.upmc.Thalasca.software.performanceController.connectors;

import java.util.ArrayList;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerManagementI;
import fr.upmc.components.connectors.AbstractConnector;


/**
 * 
 * @author Kevin GESNOUIN and Alexis MALAMAS
 *
 */

public class PerformanceControllerConnector
extends AbstractConnector
implements PerformanceControllerManagementI{

	@Override
	public void sendVirtualMachineAvailable(ArrayList<VM> listVM) throws Exception {
		((PerformanceControllerManagementI)this.offering).sendVirtualMachineAvailable(listVM);
	}

}
