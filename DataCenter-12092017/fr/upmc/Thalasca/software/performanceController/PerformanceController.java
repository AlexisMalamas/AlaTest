package fr.upmc.Thalasca.software.performanceController;

import fr.upmc.Thalasca.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.Thalasca.datacenter.software.dispatcher.ports.DispatcherManagementOutboundport;
import fr.upmc.Thalasca.software.admissionController.AdmissionController;
import fr.upmc.Thalasca.software.admissionController.connectors.AdmissionControllerConnector;
import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.Thalasca.software.admissionController.ports.AdmissionControllerOutBoundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;

public class PerformanceController
extends AbstractComponent{
	
	protected final String performanceContollerUri;
	private boolean shutdown;
	
	protected DispatcherManagementOutboundport dmop;
	protected AdmissionControllerOutBoundPort acop;
	
	public PerformanceController(
			String performanceContollerUri,
			String dmipUri,
			String acipUri
			) throws Exception{
		super(performanceContollerUri,1,1);
		
		this.performanceContollerUri = performanceContollerUri;
		this.shutdown = false;
		
		// connect PerformanceController to Dispatcher
		this.addRequiredInterface(DispatcherManagementI.class);
		this.dmop = new DispatcherManagementOutboundport("dmop", this);
		this.addPort(dmop);
		this.dmop.publishPort();
		this.dmop.doConnection(
				dmipUri,
				DispatcherManagementConnector.class.getCanonicalName());
		
		// connect PerformanceController to AdmissionController
		this.addRequiredInterface(AdmissionControllerI.class);
		this.acop = new AdmissionControllerOutBoundPort("acop", this);
		this.addPort(acop);
		this.acop.publishPort();

		System.out.println("tttttt");
		this.acop.doConnection(
				acipUri,
				AdmissionControllerConnector.class.getCanonicalName());
		

		System.out.println(this.dmop.getNbConnectedVM()+" Vm connected for "+performanceContollerUri);
		displayAverageExecutionTimeRequest();
		
	}
	
	public void displayAverageExecutionTimeRequest()
	{
		final DispatcherManagementOutboundport dmop = this.dmop;
		final AdmissionControllerOutBoundPort acop = this.acop;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Long lastTime = 0L;
					while(!shutdown)
					{
						if(System.currentTimeMillis()-lastTime>=1000) // every second
						{
							lastTime = System.currentTimeMillis();
							System.out.println("***********************************");
							System.out.println(performanceContollerUri);
							System.out.println("Average Execution Time Request : "+ dmop.getAverageExecutionTimeRequest()+" ms");
							for(int i=0; i<dmop.getNbConnectedVM(); i++)
								System.out.println("Average Execution Time Request for Vm " +(i+1)+" : "+
								dmop.getAverageExecutionTimeRequest(i) +" ms");
							System.out.println("***********************************");
							
							//acop.addVirtualMachine(performanceContollerUri);
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
		shutdown = true;
	}

}
