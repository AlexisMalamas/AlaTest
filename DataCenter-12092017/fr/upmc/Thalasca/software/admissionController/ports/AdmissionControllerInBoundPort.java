package fr.upmc.Thalasca.software.admissionController.ports;

import fr.upmc.Thalasca.software.admissionController.AdmissionController;
import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

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
		public boolean addVirtualMachine(String applicationUri) throws Exception {
			final AdmissionControllerI ac = (AdmissionControllerI) this.owner;
			
			return this.owner.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ac.addVirtualMachine(applicationUri);
							
						}
					}) ;
		}

		@Override
		public boolean removeVirtualMachine(String applicationUri) throws Exception {
			final AdmissionControllerI ac = (AdmissionControllerI) this.owner;
			
			return this.owner.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ac.removeVirtualMachine(applicationUri);
							
						}
					}) ;
		}

		@Override
		public boolean upFrequencyCore(String applicationURI, int idVM) throws Exception {
			final AdmissionControllerI ac = (AdmissionControllerI) this.owner;
			
			return this.owner.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ac.upFrequencyCore(applicationURI, idVM);
							
						}
					}) ;
		}

}
