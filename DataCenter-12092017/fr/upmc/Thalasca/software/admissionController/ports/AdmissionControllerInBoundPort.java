package fr.upmc.Thalasca.software.admissionController.ports;

import java.util.ArrayList;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.software.admissionController.AdmissionController;
import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class AdmissionControllerInBoundPort
extends AbstractInboundPort
implements AdmissionControllerI{
	
	private static final long serialVersionUID = 1L;

	public				AdmissionControllerInBoundPort(
			ComponentI owner
			) throws Exception
		{
			super(AdmissionControllerI.class, owner) ;

			assert	owner != null && owner instanceof AdmissionController ;
		}

		public				AdmissionControllerInBoundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, AdmissionControllerI.class, owner);

			assert	owner != null && owner instanceof AdmissionController ;
		}
		
		@Override
		public boolean upFrequencyCores(String applicationURI, int idVM) throws Exception {
			final AdmissionControllerI ac = (AdmissionControllerI) this.owner;
			
			return this.owner.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ac.upFrequencyCores(applicationURI, idVM);
							
						}
					}) ;
		}

		@Override
		public boolean downFrequencyCores(String applicationURI, int idVM) throws Exception {
			final AdmissionControllerI ac = (AdmissionControllerI) this.owner;
			
			return this.owner.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ac.downFrequencyCores(applicationURI, idVM);
							
						}
					}) ;
		}

		@Override
		public ArrayList<Integer> getFrequencyCores(String applicationURI, int idVM) throws Exception {
			final AdmissionControllerI ac = (AdmissionControllerI) this.owner;
			
			return this.owner.handleRequestSync(
					new ComponentI.ComponentService<ArrayList<Integer>>() {
						@Override
						public ArrayList<Integer> call() throws Exception {
							return ac.getFrequencyCores(applicationURI, idVM);
							
						}
					}) ;
		}

}
