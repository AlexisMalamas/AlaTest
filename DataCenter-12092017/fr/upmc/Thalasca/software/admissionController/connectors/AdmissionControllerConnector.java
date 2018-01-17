package fr.upmc.Thalasca.software.admissionController.connectors;

import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.components.connectors.AbstractConnector;

public class AdmissionControllerConnector 
extends AbstractConnector
implements AdmissionControllerI{

	@Override
	public boolean addVirtualMachine(String applicationUri) throws Exception {
		return ((AdmissionControllerI)this.offering).addVirtualMachine(applicationUri);
	}

	@Override
	public boolean removeVirtualMachine(String applicationUri) throws Exception {
		return ((AdmissionControllerI)this.offering).removeVirtualMachine(applicationUri);
	}

	@Override
	public boolean upFrequencyCore(String applicationURI, int idVM) throws Exception {
		return ((AdmissionControllerI)this.offering).upFrequencyCore(applicationURI, idVM);
	}

}
