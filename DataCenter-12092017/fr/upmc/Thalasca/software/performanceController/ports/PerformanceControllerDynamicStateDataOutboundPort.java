package fr.upmc.Thalasca.software.performanceController.ports;

import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerDynamicStateI;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerStateDataConsumerI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;

public class PerformanceControllerDynamicStateDataOutboundPort
extends AbstractControlledDataOutboundPort{
	
	private static final long serialVersionUID = 1L;

	public PerformanceControllerDynamicStateDataOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, owner);

		assert	owner instanceof PerformanceControllerStateDataConsumerI ;
	}
	
	@Override
	public void	receive(DataRequiredI.DataI d)
	throws Exception
	{
		((PerformanceControllerStateDataConsumerI)this.owner).
						acceptPerformanceControllerDynamicData((PerformanceControllerDynamicStateI) d) ;
	}
}
