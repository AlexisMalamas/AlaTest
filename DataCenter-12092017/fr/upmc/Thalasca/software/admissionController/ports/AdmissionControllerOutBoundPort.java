package fr.upmc.Thalasca.software.admissionController.ports;

import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

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
		public boolean addVirtualMachine(String applicationUri) throws Exception {
			return ((AdmissionControllerI)this.connector).addVirtualMachine(applicationUri);
			
		}

		@Override
		public boolean removeVirtualMachine(String applicationUri) throws Exception {
			return ((AdmissionControllerI)this.connector).removeVirtualMachine(applicationUri);
		}

		@Override
		public boolean upFrequencyCore(String applicationURI, int idVM) throws Exception {
			return ((AdmissionControllerI)this.connector).upFrequencyCore(applicationURI, idVM);
		}

}
