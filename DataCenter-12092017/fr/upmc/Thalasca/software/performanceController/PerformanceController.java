package fr.upmc.Thalasca.software.performanceController;

import java.util.concurrent.TimeUnit;

import fr.upmc.Thalasca.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.Thalasca.datacenter.software.dispatcher.ports.DispatcherManagementOutboundport;
import fr.upmc.Thalasca.software.admissionController.connectors.AdmissionControllerConnector;
import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.Thalasca.software.admissionController.ports.AdmissionControllerOutBoundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;

public class PerformanceController
extends AbstractComponent{

	protected final String performanceContollerUri;

	public static Long UPDATE_INVERVAL = 1000L; // update every 1 sec

	protected DispatcherManagementOutboundport dmop;
	protected AdmissionControllerOutBoundPort acop;
	protected String applicationUri;

	public PerformanceController(
			String performanceContollerUri,
			String dmipUri,
			String acipUri,
			String applicationUri
			) throws Exception{
		super(performanceContollerUri,1,1);

		this.performanceContollerUri = performanceContollerUri;
		this.applicationUri = applicationUri;

		// connect PerformanceController to Dispatcher
		this.addRequiredInterface(DispatcherManagementI.class);
		this.dmop = new DispatcherManagementOutboundport(applicationUri+"_dmop", this);
		this.addPort(dmop);
		this.dmop.publishPort();
		this.dmop.doConnection(
				dmipUri,
				DispatcherManagementConnector.class.getCanonicalName());

		// connect PerformanceController to AdmissionController
		this.addRequiredInterface(AdmissionControllerI.class);
		this.acop = new AdmissionControllerOutBoundPort(applicationUri+"_acop", this);
		this.addPort(acop);
		this.acop.publishPort();
		this.acop.doConnection(
				acipUri,
				AdmissionControllerConnector.class.getCanonicalName());


		System.out.println(this.dmop.getNbConnectedVM()+" Vm connected for "+performanceContollerUri);
		displayAverageExecutionTimeRequest();

	}

	public void displayAverageExecutionTimeRequest()
	{
		this.scheduleTask(new ComponentI.ComponentTask() {
			@Override
			public void run() {
				try {
					System.out.println("***********************************");
					System.out.println(performanceContollerUri);
					System.out.println("Average Execution Time Request : "+ dmop.getAverageExecutionTimeRequest()+" ms");
					for(int i=0; i<dmop.getNbConnectedVM(); i++)
						System.out.println("Average Execution Time Request for Vm " +i+" : "+
								dmop.getAverageExecutionTimeRequest(i) +" ms");
					System.out.println("***********************************");
					System.out.println("test"+applicationUri+dmop.getNbConnectedVM());
					if(dmop.getNbConnectedVM()<6) {
						System.out.println("test");
						acop.addVirtualMachine(applicationUri);
					}

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				displayAverageExecutionTimeRequest();
			}
		}, UPDATE_INVERVAL, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}

}
