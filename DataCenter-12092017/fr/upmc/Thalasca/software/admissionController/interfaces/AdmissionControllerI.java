package fr.upmc.Thalasca.software.admissionController.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface AdmissionControllerI extends OfferedI, RequiredI{
	public boolean addVirtualMachine(String applicationUri) throws Exception;
	public boolean removeVirtualMachine(String applicationUri) throws Exception;

	public boolean upFrequencyCore(String applicationURI, int idVM) throws Exception;
}
