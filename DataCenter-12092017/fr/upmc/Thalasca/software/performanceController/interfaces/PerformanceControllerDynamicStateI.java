package fr.upmc.Thalasca.software.performanceController.interfaces;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;

public interface PerformanceControllerDynamicStateI 
extends DataOfferedI.DataI, DataRequiredI.DataI, TimeStampingI {
	
	public VM getVM();
}
