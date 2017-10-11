package fr.upmc.datacenter.software.dispatcher;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public class Dispatcher 
extends AbstractComponent
implements RequestSubmissionHandlerI, RequestNotificationHandlerI, ProcessorServicesNotificationConsumerI{
	
	protected final String dispatcherURI ;
	
	// send request to VM
	protected RequestSubmissionOutboundPort	rsop ;
	
	// receive request from RequesGenerator
	protected RequestSubmissionInboundPort rsip ;
	
	//send notification to RequesGenerator
	protected RequestNotificationOutboundPort rnop ;
	
	//receive notification from VM
	protected RequestNotificationInboundPort rnip ;
	
	public Dispatcher(String dispatcherURI,
			String requestSubmissionInboundPortURI,
			String requestSubmissionOutboundPortURI,
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
		
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.rsop = new RequestSubmissionOutboundPort(requestSubmissionOutboundPortURI, this) ;
		this.addPort(this.rsop) ;
		this.rsop.publishPort() ;
		
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.rnip = new RequestNotificationInboundPort(requestNotificationInboundPortURI, this) ;
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
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.rsop.submitRequest(r) ;
		System.out.println("test1");
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		this.rsop.submitRequestAndNotify(r) ;
		System.out.println("test2");
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		this.rnop.notifyRequestTermination(r);
		System.out.println("test3");
	}

	@Override
	public void acceptNotifyEndOfTask(TaskI t) throws Exception {
		this.rnop.notifyRequestTermination(t.getRequest());
		System.out.println("test4");
	}
}