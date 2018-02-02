package fr.upmc.Thalasca.software.performanceController;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import fr.upmc.Thalasca.datacenter.software.VM.DynamicVM;
import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.Thalasca.datacenter.software.dispatcher.ports.DispatcherManagementOutboundport;
import fr.upmc.Thalasca.software.admissionController.connectors.AdmissionControllerConnector;
import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.Thalasca.software.admissionController.ports.AdmissionControllerOutBoundPort;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerDynamicStateI;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerStateDataConsumerI;
import fr.upmc.Thalasca.software.performanceController.ports.PerformanceControllerDynamicStateDataInboundPort;
import fr.upmc.Thalasca.software.performanceController.ports.PerformanceControllerDynamicStateDataOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.interfaces.PushModeControllingI;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */
public class PerformanceController
extends AbstractComponent
implements PushModeControllingI, PerformanceControllerStateDataConsumerI{

	protected final String performanceContollerUri;

	public final static Long UPDATE_INVERVAL = 10000L; // update every 10 sec
	public final static Long LOWER_WANTED_TIME_REQUEST = 2000L; // lower range for time execution of request
	public final static Long MAX_WANTED_TIME_REQUEST = 5000L; // max range for time execution of request

	protected DispatcherManagementOutboundport dmop;
	protected AdmissionControllerOutBoundPort acop;
	protected String applicationUri;
	protected PerformanceControllerDynamicStateDataInboundPort pcdsip;
	protected PerformanceControllerDynamicStateDataOutboundPort pcdsop;
	
	protected ScheduledFuture<?> pushingFuture;
	protected ArrayList<VM> listVmAvailable;
	
	public String dispatcherRequestNotificationInboundPortURI;
	public String vmRequestNotificationOutboundPortURI;
	
	protected boolean applicatioNeedVM; // set true if application need moreVM

	public PerformanceController(
			String performanceContollerUri,
			String dmipUri,
			String acipUri,
			String applicationUri,
			String performanceControllerInboundPortURI,
			String performanceControllerOutboundPortURI,
			String dispatcherRequestNotificationInboundPortURI,
			String vmRequestNotificationOutboundPortURI
			) throws Exception{
		super(performanceContollerUri,1,1);

		this.performanceContollerUri = performanceContollerUri;
		this.applicationUri = applicationUri;
		this.listVmAvailable = new ArrayList<VM>();
		this.applicatioNeedVM = true;
		
		this.dispatcherRequestNotificationInboundPortURI = dispatcherRequestNotificationInboundPortURI;
		this.vmRequestNotificationOutboundPortURI = vmRequestNotificationOutboundPortURI;

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
		
		this.pcdsip = new PerformanceControllerDynamicStateDataInboundPort(performanceControllerInboundPortURI, this);
		addPort(pcdsip);
		pcdsip.publishPort();
		
		this.pcdsop = new PerformanceControllerDynamicStateDataOutboundPort(performanceControllerOutboundPortURI, this);
		addPort(pcdsop);
		pcdsop.publishPort();

		this.startUnlimitedPushing(2000);

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
							System.out.println("App: "+applicationUri+"   up frequency vm "+i+":"
									+acop.upFrequencyCores(applicationUri, dmop.getIdVm(i)));
						else if(dmop.getAverageExecutionTimeRequest(i)<LOWER_WANTED_TIME_REQUEST)
							System.out.println("App: "+applicationUri+"   down frequency vm "+i+":"
									+acop.downFrequencyCores(applicationUri, dmop.getIdVm(i)));
					}


					System.out.println("App: "+applicationUri+"   Average Execution Time Request : "+ 
							dmop.getAverageExecutionTimeRequest()+" ms");
					for(int i=0; i<dmop.getNbConnectedVM(); i++)
						System.out.println("App: "+applicationUri+"   Average Execution Time Request for 20 last request on Vm " +i+" : "+
								dmop.getAverageExecutionTimeRequest(i) +" ms");
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

	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		final PerformanceController c = this ;
		this.pushingFuture =
			this.scheduleTaskAtFixedRate(
					new ComponentI.ComponentTask() {
						@Override
						public void run() {
							try {
								c.sendDynamicState();
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopPushing() throws Exception {
		if (this.pushingFuture != null &&
				!(this.pushingFuture.isCancelled() ||
									this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false) ;
		}
	}

	/**
	 *
	 * Accept and save VM from previous performanceController or from admissionController
	 * 
	 **/
	@Override
	public void acceptPerformanceControllerDynamicData(PerformanceControllerDynamicStateI currentDynamicState) 
			throws Exception {
		synchronized(this){
			if(applicatioNeedVM){
				applicatioNeedVM = false;
				System.out.println("Pick a Vm for Application "+this.applicationUri);
				
				// connect dispatcher to VM
				this.dmop.connectToVirtualMachine(currentDynamicState.getVM());
				// connect VM to dispatcher
				ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
				this.addPort(rop);
				rop.localPublishPort();
				rop.doConnection(currentDynamicState.getVM().getVmURI()+"_VM", ReflectionConnector.class.getCanonicalName());
				rop.doPortConnection(
						currentDynamicState.getVM().getVmURI()+"_"+vmRequestNotificationOutboundPortURI,
						currentDynamicState.getVM().getVmURI()+"_"+dispatcherRequestNotificationInboundPortURI,
						RequestNotificationConnector.class.getCanonicalName());
			}
			else
				this.listVmAvailable.add(currentDynamicState.getVM());
		}
	}
	
	/**
	 * 
	 * Send available VM to next performanceController or admissionController
	 *
	 **/
	public void sendDynamicState() throws Exception
	{
		if (this.pcdsip.connected()) {
			PerformanceControllerDynamicStateI cds = this.getDynamicState() ;
			this.pcdsip.send(cds) ;
		}
	}

	/**
	 * 
	 * get not used vm
	 *
	 **/
	public PerformanceControllerDynamicStateI getDynamicState() throws Exception {
		VM vm = null;
        synchronized(this){
            if(!this.listVmAvailable.isEmpty()) {
            	vm = this.listVmAvailable.remove(0);
            }
        }
        return new DynamicVM(vm);
	}
}
