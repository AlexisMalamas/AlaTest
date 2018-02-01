package fr.upmc.Thalasca.software.performanceController.interfaces;

import java.util.ArrayList;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 * @author Kevin GESNOUIN and Alexis MALAMAS
 *
 */

public interface PerformanceControllerManagementI
extends RequiredI, OfferedI{

	public void sendVirtualMachineAvailable(ArrayList<VM> listVM) throws Exception;
	
}
