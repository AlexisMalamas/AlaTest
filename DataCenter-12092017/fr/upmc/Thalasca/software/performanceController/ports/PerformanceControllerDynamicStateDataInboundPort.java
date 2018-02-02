package fr.upmc.Thalasca.software.performanceController.ports;

import fr.upmc.Thalasca.software.admissionController.AdmissionController;
import fr.upmc.Thalasca.software.performanceController.PerformanceController;
import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;

public class PerformanceControllerDynamicStateDataInboundPort
extends AbstractControlledDataInboundPort
{
	private static final long serialVersionUID = 1L;

	public PerformanceControllerDynamicStateDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(owner) ;
	}

	public PerformanceControllerDynamicStateDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, owner);
	}
	
	@Override
	public DataOfferedI.DataI get() throws Exception
	{
		if(this.owner instanceof PerformanceController) {
            final PerformanceController pc = (PerformanceController) this.owner;
            return pc.handleRequestSync(
                    new ComponentI.ComponentService<DataOfferedI.DataI>() {
                        @Override
                        public DataOfferedI.DataI call() throws Exception {
                            return pc.getDynamicState() ;
                        }
                    });            
        }else if(this.owner instanceof AdmissionController) {
            final AdmissionController ac = (AdmissionController) this.owner;
            return ac.handleRequestSync(
                    new ComponentI.ComponentService<DataOfferedI.DataI>() {
                        @Override
                        public DataOfferedI.DataI call() throws Exception {
                            return ac.getDynamicState() ;
                        }
                    });
        }
        return null;
    }
}
