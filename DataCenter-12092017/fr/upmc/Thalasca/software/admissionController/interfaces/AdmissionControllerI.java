package fr.upmc.Thalasca.software.admissionController.interfaces;

import java.util.ArrayList;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */
public interface AdmissionControllerI extends OfferedI, RequiredI{
	public boolean upFrequencyCores(String applicationURI, int idVM) throws Exception;
	public boolean downFrequencyCores(String applicationURI, int idVM) throws Exception;
	public ArrayList<Integer> getFrequencyCores(String applicationURI, int idVM) throws Exception;
}
