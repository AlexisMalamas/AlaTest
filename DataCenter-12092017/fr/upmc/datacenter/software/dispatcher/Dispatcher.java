package fr.upmc.datacenter.software.dispatcher;

import java.util.ArrayList;

import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
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
implements RequestSubmissionHandlerI, RequestNotificationHandlerI{

	protected final String dispatcherURI ;

	// current VM
	private int currentVm;

	// send request to VM
	protected ArrayList<RequestSubmissionOutboundPort>	rsopList ;

	// receive request from RequesGenerator
	protected RequestSubmissionInboundPort rsip ;

	//send notification to RequesGenerator
	protected RequestNotificationOutboundPort rnop ;

	//receive notification from VM
	protected ArrayList<RequestNotificationInboundPort> rnipList;

	public Dispatcher(String dispatcherURI,
			String requestSubmissionInboundPortURI,
			ArrayList<String> requestSubmissionOutboundPortURI,
			String requestNotificationOutboundPortURI,
			String requestNotificationInboundPortURI
			) throws Exception
	{
		super(1, 1);

		// preconditions check
		assert  requestSubmissionInboundPortURI != null ;
		assert	requestSubmissionOutboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		assert  requestNotificationOutboundPortURI != null ;

		this.dispatcherURI = dispatcherURI;
		this.currentVm = 0;

		this.rsopList = new ArrayList<RequestSubmissionOutboundPort>();
		for(int i=0; i<requestSubmissionOutboundPortURI.size(); i++){
			this.addRequiredInterface(RequestSubmissionI.class) ;
			this.rsopList.add(new RequestSubmissionOutboundPort(requestSubmissionOutboundPortURI.get(i), this)) ;
			this.addPort(this.rsopList.get(i)) ;
			this.rsopList.get(i).publishPort() ;
		}

		this.rsopList = new ArrayList<RequestSubmissionOutboundPort>();
		for(int i=0; i<requestSubmissionOutboundPortURI.size(); i++){

			this.addOfferedInterface(RequestNotificationI.class) ;
			this.rnipList.add(new RequestNotificationInboundPort(requestNotificationInboundPortURI, this)) ;
			this.addPort(this.rnipList.get(i)) ;
			this.rnipList.get(i).publishPort() ;
		}

		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.rsip = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this) ;
		this.addPort(this.rsip) ;
		this.rsip.publishPort() ;

		this.addRequiredInterface(RequestNotificationI.class) ;
		this.rnop = new RequestNotificationOutboundPort(requestNotificationOutboundPortURI, this) ;
		this.addPort(this.rnop) ;
		this.rnop.publishPort() ;
	}

	public void addRequestSubmissionOutboundPort(String rsop) throws Exception{
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.rsopList.add(new RequestSubmissionOutboundPort(rsop, this)) ;
		this.addPort(this.rsopList.get(this.rsopList.size()-1)) ;
		this.rsopList.get(this.rsopList.size()-1).publishPort() ;
	}

	public void removeRequestSubmissionOutboundPort(){
		if(!this.rsopList.isEmpty())
			this.rsopList.remove(this.rsopList.size()-1);
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
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
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
			for( RequestNotificationInboundPort rnip: this.rnipList)
				if (rnip.connected()) 
					rnip.doDisconnection();
		}
		catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

}