package fr.upmc.Thalasca.software.admissionController.ports;

import java.util.ArrayList;

import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class AdmissionControllerOutBoundPort 
extends AbstractOutboundPort 
implements AdmissionControllerI{
	
	public AdmissionControllerOutBoundPort(
			ComponentI owner
			) throws Exception
		{
			super(AdmissionControllerI.class, owner) ;

			assert	owner != null ;
		}

		public AdmissionControllerOutBoundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, AdmissionControllerI.class, owner) ;

			assert	uri != null && owner != null ;
		}

		@Override
		public boolean createAndaddVirtualMachine(String applicationUri) throws Exception {
			return ((AdmissionControllerI)this.connector).createAndaddVirtualMachine(applicationUri);
			
		}

		@Override
		public boolean deleteAndremoveVirtualMachine(String applicationUri) throws Exception {
			return ((AdmissionControllerI)this.connector).deleteAndremoveVirtualMachine(applicationUri);
		}

		@Override
		public boolean upFrequencyCores(String applicationURI, int idVM) throws Exception {
			return ((AdmissionControllerI)this.connector).upFrequencyCores(applicationURI, idVM);
		}

		@Override
		public boolean downFrequencyCores(String applicationURI, int idVM) throws Exception {
			return ((AdmissionControllerI)this.connector).downFrequencyCores(applicationURI, idVM);
		}

		@Override
		public ArrayList<Integer> getFrequencyCores(String applicationURI, int idVM) throws Exception {
			return ((AdmissionControllerI)this.connector).getFrequencyCores(applicationURI, idVM);
		}

}
