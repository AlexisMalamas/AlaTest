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

	public static Long UPDATE_INVERVAL = 10000L; // update every 10 sec
	public static Long LOWER_WANTED_TIME_REQUEST = 2000L;
	public static Long MAX_WANTED_TIME_REQUEST = 5000L;

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
		this.dmop = new DispatcherManagementOutboundport(applicationUri+"_dmop_performanceController", this);
		this.addPort(dmop);
		this.dmop.publishPort();
		this.dmop.doConnection(
				dmipUri,
				DispatcherManagementConnector.class.getCanonicalName());

		// connect PerformanceController to AdmissionController
		this.addRequiredInterface(AdmissionControllerI.class);
		this.acop = new AdmissionControllerOutBoundPort(applicationUri+"_acop_performanceController", this);
		this.addPort(acop);
		this.acop.publishPort();
		this.acop.doConnection(
				acipUri,
				AdmissionControllerConnector.class.getCanonicalName());


		System.out.println(this.dmop.getNbConnectedVM()+" Vm connected for "+performanceContollerUri);
		update();

	}

	public void update()
	{
		this.scheduleTask(new ComponentI.ComponentTask() {
			@Override
			public void run() {
				try {
					for(int i=0; i<dmop.getNbConnectedVM(); i++) {
						if(dmop.getAverageExecutionTimeRequest(i)>MAX_WANTED_TIME_REQUEST)
							System.out.println("App: "+applicationUri+"   up frequency vm "+i+":"+acop.upFrequencyCores(applicationUri, i));
						else if(dmop.getAverageExecutionTimeRequest(i)<LOWER_WANTED_TIME_REQUEST)
							System.out.println("App: "+applicationUri+"   down frequency vm "+i+":"+acop.downFrequencyCores(applicationUri, i));
					}
					
					
					System.out.println("App: "+applicationUri+"   Average Execution Time Request : "+ 
							dmop.getAverageExecutionTimeRequest()+" ms");
					for(int i=0; i<dmop.getNbConnectedVM(); i++)
						System.out.println("App: "+applicationUri+"   Average Execution Time Request for 20 last request on Vm " +i+" : "+
								dmop.getAverageExecutionTimeRequest(i) +" ms");
					
					
					// test for add Vm
					/*
					if(dmop.getNbConnectedVM()<6) {
						if(acop.addVirtualMachine(applicationUri))
							System.out.println("VM ADD");
						else
							System.out.println("VM NOT ADD");
							
					}*/
					
					// test for remove vm
					/*
					if(dmop.getNbConnectedVM()>2) {
						if(acop.removeVirtualMachine(applicationUri))
							System.out.println("VM Removed");
						else
							System.out.println("VM NOT Removed");		
					}*/
					
					// test for upFrequency
					/*
					acop.upFrequencyCores(applicationUri, 1);
					for(int i=0; i<dmop.getNbConnectedVM(); i++)
						System.out.println(applicationUri+" idVm: "+i+" cores:"+acop.getFrequencyCores(applicationUri, i));
					*/
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				update();
			}
		}, UPDATE_INVERVAL, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}

}
