package fr.upmc.Thalasca.datacenter.software.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.Thalasca.datacenter.software.dispatcher.ports.DispatcherManagementInboundport;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */
public class Dispatcher 
extends AbstractComponent
implements RequestSubmissionHandlerI, RequestNotificationHandlerI, DispatcherManagementI{

	protected final String dispatcherURI ;
	protected final String applicationURI;

	// current VM
	private int currentVm;

	// send request to VM
	protected ArrayList<RequestSubmissionOutboundPort>	rsopList ;

	// receive request from RequesGenerator
	protected RequestSubmissionInboundPort rsip ;

	//send notification to RequesGenerator
	protected RequestNotificationOutboundPort rnop ;

	//receive notification from VM
	protected RequestNotificationInboundPort rnip ;

	protected DispatcherManagementInboundport dmip;
	
	//List VM
	protected ArrayList<VM> listVM;
	
	// add for part 2
	private static int NB_LAST_REQUEST = 20; // 20 last request for calculate average time for vm
	private int nbTotalRequest;
	private long TotalRequestExectutionTime;
	private ArrayList<Long> TotalRequestExectutionTimeVM;
	private ArrayList<Integer> nbTotalRequestVM;
	private HashMap<String, Integer> nameRequestToVm; // to know which VM excute a request
	private HashMap<String, Long> startTimeRequest; // to know time excution of a request
	
	private HashMap<Integer,  LinkedList<Long>> executionTimeRequest; // first index for idVM

	public Dispatcher( String applicationURI,
			String dispatcherURI,
			String requestSubmissionInboundPortURI,
			String dispatcherManagementInboundPortURI,
			String requestNotificationOutboundPortURI,
			String requestNotificationInboundPortURI
			) throws Exception
	{
		super(dispatcherURI,1, 1);

		// preconditions check
		assert  requestSubmissionInboundPortURI != null ;
		assert  requestNotificationOutboundPortURI != null ;

		this.dispatcherURI = dispatcherURI;
		this.applicationURI = applicationURI;
		this.currentVm = 0;
		this.TotalRequestExectutionTime = 0;
		this.nbTotalRequest = 0;
		this.startTimeRequest = new HashMap<String, Long>();
		this.executionTimeRequest = new HashMap<Integer,  LinkedList<Long>>();
		this.listVM = new ArrayList<VM>();
		
		this.addRequiredInterface(DispatcherManagementI.class) ;
		this.dmip =new DispatcherManagementInboundport(dispatcherManagementInboundPortURI,this) ;
		this.addPort(this.dmip) ;
		this.dmip.publishPort() ;

		this.rsopList = new ArrayList<RequestSubmissionOutboundPort>();
		this.addRequiredInterface(RequestSubmissionI.class) ;
		
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.rnip = new RequestNotificationInboundPort(requestNotificationInboundPortURI, this);
		this.addPort(this.rnip) ;
		this.rnip.publishPort() ;
		
		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.rsip = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this) ;
		this.addPort(this.rsip) ;
		this.rsip.publishPort() ;

		this.addRequiredInterface(RequestNotificationI.class) ;
		this.rnop = new RequestNotificationOutboundPort(requestNotificationOutboundPortURI, this) ;
		this.addPort(this.rnop) ;
		this.rnop.publishPort() ;
		
		this.TotalRequestExectutionTimeVM = new ArrayList<Long>();
		this.nbTotalRequestVM = new ArrayList<Integer>();
		this.nameRequestToVm = new HashMap<String, Integer>();
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.rsopList.get(this.currentVm).submitRequest(r) ;
		this.currentVm += 1;
		this.currentVm %= this.rsopList.size();
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		// add for part 2
		this.nbTotalRequest++;
		this.startTimeRequest.put(r.getRequestURI(), System.currentTimeMillis());
		this.nbTotalRequestVM.set(this.currentVm, this.nbTotalRequestVM.get(this.currentVm)+1);
		this.nameRequestToVm.put(r.getRequestURI(), this.currentVm);
		
		this.rsopList.get(this.currentVm).submitRequestAndNotify(r) ;
		this.currentVm += 1;
		this.currentVm %= this.rsopList.size();
	}

	/**
	 * 
	 * Reception of terminated requests
	 * @param	r	RequestTerminate 
	 * 
	 * */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		
		// add for part 2
		long requestTime = System.currentTimeMillis() - this.startTimeRequest.remove(r.getRequestURI());
		this.TotalRequestExectutionTime += requestTime;
		
		int vm = this.nameRequestToVm.remove(r.getRequestURI());
		this.TotalRequestExectutionTimeVM.set(vm, this.TotalRequestExectutionTimeVM.get(vm)+requestTime);

		if(!this.executionTimeRequest.containsKey(vm)) // if first time we see that vm
			this.executionTimeRequest.put(vm, new  LinkedList<Long>());
		this.executionTimeRequest.get(vm).add(requestTime);
		
		if(this.executionTimeRequest.get(vm).size()>NB_LAST_REQUEST)  // only saves last request
			this.executionTimeRequest.get(vm).removeFirst();
		
		this.rnop.notifyRequestTermination(r) ;
	}

	/**
	 * 
	 *  Connect Dispatcher To RequestSubmissionInboundPort of VM
	 *  @param	requestSubmissionInboundPortURI		URI of request Submission Inbound Port of Vm to connect
	 * 
	 */
	@Override
	public void connectToVirtualMachine(VM vm) throws Exception {
		this.listVM.add(vm);
		String portURI = this.applicationURI+"vmPort-"+this.rsopList.size();
		RequestSubmissionOutboundPort port = new RequestSubmissionOutboundPort( portURI, this );

		this.rsopList.add(port);
		this.addPort( port );
		port.publishPort();

		this.doPortConnection(
				port.getPortURI(),
				vm.getVmRequestSubmissionInboundURI(),
				RequestSubmissionConnector.class.getCanonicalName());
		
		this.TotalRequestExectutionTimeVM.add(0L);
		this.nbTotalRequestVM.add(0);
	}

	/**
	 * 
	 * 	Disconnect Last VM
	 * 
	 */
	@Override
	public VM disconnectVirtualMachine() throws Exception {
		if(this.listVM.size()>1) {
			this.rsopList.get(this.rsopList.size()-1).doDisconnection();
			// if current Vm is removed vm, go next vm
			if(this.currentVm == this.rsopList.size()-1)
				this.currentVm=0;
			this.rsopList.remove(this.rsopList.size()-1);
			
			this.TotalRequestExectutionTimeVM.remove(this.TotalRequestExectutionTimeVM.size()-1);
			this.nbTotalRequestVM.remove(this.nbTotalRequestVM.size()-1);
		
			return this.listVM.remove(this.listVM.size()-1);
		}
		else
			return null;
	}

	public int getNbTotalRequest() {
		return this.nbTotalRequest;
	}

	public long getTotalRequestExectutionTime() {
		return this.TotalRequestExectutionTime;
	}

	@Override
	public Long getAverageExecutionTimeRequest() throws Exception {
		if(this.nbTotalRequest!=0)
			return TotalRequestExectutionTime/this.nbTotalRequest;
		else
			return 0L;
	}

	/**
	 * 
	 * @param vm	id of vm
	 * @return 		Average time for last NB_LAST_REQUEST requests on the given vm in parameter
	 * 
	 * */
	@Override
	public Long getAverageExecutionTimeRequest(int vm) throws Exception {
		if(this.executionTimeRequest.containsKey(vm) && this.executionTimeRequest.get(vm).size()!=0) {
			Long sumTime = 0L;
			for(int i=0; i<this.executionTimeRequest.get(vm).size(); i++)
				sumTime += this.executionTimeRequest.get(vm).get(i);

			return sumTime/this.executionTimeRequest.get(vm).size();
		}
		return 0L;
	}

	/**
	 * 
	 *  @return		Number of connected VM to the dispatcher
	 * 
	 */
	@Override
	public int getNbConnectedVM() {
		return this.rsopList.size();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			if (this.rnop.connected()) {
				this.rnop.doDisconnection();
			}
			for (RequestSubmissionOutboundPort rsoport : this.rsopList)
				if (rsoport.connected()) {
					rsoport.doDisconnection();
				}

			if (this.rsip.connected()) 
				this.rsip.doDisconnection();

			if (this.rnip.connected()) 
				this.rnip.doDisconnection();
		}
		catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	@Override
	public int getIdVm(int i) throws Exception {
		return this.listVM.get(i).getIdVM();
	}
}