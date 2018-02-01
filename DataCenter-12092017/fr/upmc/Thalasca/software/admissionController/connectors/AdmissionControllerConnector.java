package fr.upmc.Thalasca.software.admissionController.connectors;

import java.util.ArrayList;

import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class AdmissionControllerConnector 
extends AbstractConnector
implements AdmissionControllerI{

	@Override
	public boolean createAndaddVirtualMachine(String applicationUri) throws Exception {
		return ((AdmissionControllerI)this.offering).createAndaddVirtualMachine(applicationUri);
	}

	@Override
	public boolean deleteAndremoveVirtualMachine(String applicationUri) throws Exception {
		return ((AdmissionControllerI)this.offering).deleteAndremoveVirtualMachine(applicationUri);
	}

	@Override
	public boolean upFrequencyCores(String applicationURI, int idVM) throws Exception {
		return ((AdmissionControllerI)this.offering).upFrequencyCores(applicationURI, idVM);
	}

	@Override
	public boolean downFrequencyCores(String applicationURI, int idVM) throws Exception {
		return ((AdmissionControllerI)this.offering).downFrequencyCores(applicationURI, idVM);
	}

	@Override
	public ArrayList<Integer> getFrequencyCores(String applicationURI, int idVM) throws Exception {
		return ((AdmissionControllerI)this.offering).getFrequencyCores(applicationURI, idVM);
	}

}
