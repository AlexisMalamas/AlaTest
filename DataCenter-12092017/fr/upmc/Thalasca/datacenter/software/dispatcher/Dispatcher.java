package fr.upmc.Thalasca.datacenter.software.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;

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

	// current VM
	private int currentVm;
	
	// add for part 2
	private int nbTotalRequest; // for all vm
	private long TotalRequestExectutionTime;
	private ArrayList<Long> TotalRequestExectutionTimeVM;
	private ArrayList<Integer> nbTotalRequestVM;
	private HashMap<String, Integer> nameRequestToVm; // to know which VM excute a request
	private HashMap<String, Long> startTimeRequest; // to know time excution of a request

	// send request to VM
	protected ArrayList<RequestSubmissionOutboundPort>	rsopList ;

	// receive request from RequesGenerator
	protected RequestSubmissionInboundPort rsip ;

	//send notification to RequesGenerator
	protected RequestNotificationOutboundPort rnop ;

	//receive notification from VM
	protected RequestNotificationInboundPort rnip ;

	protected DispatcherManagementInboundport dmip;

	public Dispatcher(String dispatcherURI,
			String requestSubmissionInboundPortURI,
			String dispatcherManagementInboundPortURI,
			String requestNotificationOutboundPortURI,
			String requestNotificationInboundPortURI
			) throws Exception
	{
		super(dispatcherURI,1, 1);

		// preconditions check
		assert  requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		assert  requestNotificationOutboundPortURI != null ;

		this.dispatcherURI = dispatcherURI;
		this.currentVm = 0;
		this.TotalRequestExectutionTime = 0;
		this.nbTotalRequest = 0;
		this.startTimeRequest = new HashMap<String, Long>();
		this.addRequiredInterface(DispatcherManagementI.class) ;
		this.dmip =new DispatcherManagementInboundport(dispatcherManagementInboundPortURI,this) ;
		this.addPort(this.dmip) ;
		this.dmip.publishPort() ;

		this.rsopList = new ArrayList<RequestSubmissionOutboundPort>();
		this.addRequiredInterface(RequestSubmissionI.class) ;

		this.addOfferedInterface(RequestNotificationI.class) ;
		this.rnip=(new RequestNotificationInboundPort(requestNotificationInboundPortURI, this)) ;
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
		this.rsopList.get(this.currentVm).submitRequestAndNotify(r) ;
		this.currentVm += 1;
		this.currentVm %= this.rsopList.size();
		
		// add for part 2
		this.nbTotalRequest++;
		this.startTimeRequest.put(r.getRequestURI(), System.currentTimeMillis());
		this.nbTotalRequestVM.set(this.currentVm, this.nbTotalRequestVM.get(this.currentVm)+1);
		this.nameRequestToVm.put(r.getRequestURI(), this.currentVm);
		
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		
		// add for part 2
		long requestTime = System.currentTimeMillis() - this.startTimeRequest.remove(r.getRequestURI());
		this.TotalRequestExectutionTime += requestTime;
		
		int vm = this.nameRequestToVm.get(r.getRequestURI());
		this.TotalRequestExectutionTimeVM.set(vm, this.TotalRequestExectutionTimeVM.get(vm)+requestTime);
		
		this.rnop.notifyRequestTermination(r) ;
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

			if (rnip.connected()) 
				rnip.doDisconnection();
		}
		catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}


	@Override
	public void connectToVirtualMachine(String requestSubmissionInboundPortURI) throws Exception {
		String portURI = "vmPort-"+this.rsopList.size();
		RequestSubmissionOutboundPort port = new RequestSubmissionOutboundPort( portURI, this );
		
		this.rsopList.add(port);
		this.addPort( port );
		port.publishPort();

		this.doPortConnection(
				port.getPortURI(),
				requestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());
		
		this.TotalRequestExectutionTimeVM.add(0L);
		this.nbTotalRequestVM.add(0);
	}

	@Override
	public void disconnectVirtualMachine() throws Exception {
		if(!this.rsopList.isEmpty())
			if (rsopList.get(rsopList.size()-1).connected()){
				rsopList.get(rsopList.size()-1).doDisconnection();
			}
	}

	public int getNbTotalRequest() {
		return nbTotalRequest;
	}

	public long getTotalRequestExectutionTime() {
		return TotalRequestExectutionTime;
	}

	@Override
	public Long getAverageExecutionTimeRequest() throws Exception {
		if(nbTotalRequest!=0)
			return TotalRequestExectutionTime/nbTotalRequest;
		else
			return 0L;
	}

	@Override
	public Long getAverageExecutionTimeRequest(int vm) throws Exception {
		if(vm<this.rsopList.size() && nbTotalRequestVM.get(vm)!=0)
			return TotalRequestExectutionTimeVM.get(vm)/nbTotalRequestVM.get(vm);
		else
			return 0L;
	}

	@Override
	public int getNbConnectedVM() {
		return this.rsopList.size();
	}

}