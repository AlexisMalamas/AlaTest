package fr.upmc.datacenterclient.application;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.application.interfaces.ApplicationManagementI;
import fr.upmc.datacenterclient.application.interfaces.ApplicationNotificationI;
import fr.upmc.datacenterclient.application.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.application.ports.ApplicationManagementInboundPort;
import fr.upmc.datacenterclient.application.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.application.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;


import fr.upmc.components.cvm.*;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class Application 
extends AbstractComponent{

	// the uri application
	protected final String applicationURI;

	/** the outbound port provided to manage the component.	*/
	protected RequestGeneratorManagementOutboundPort rgmop ;

	/** the outbound port to submission the application */
	protected ApplicationSubmissionOutboundPort apsop;

	/** the outbound port to notify that the requestgenerator has been created */
	protected ApplicationNotificationOutboundPort anop;

	/** the inbound port used to send/stop application **/
	protected ApplicationManagementInboundPort apmip;


	// REQUEST GENERATOR

	/** Request Generator URI */
	protected String rgURI;

	/** Request generator management inbound port */
	protected String rgmipURI;

	/** Request submission outbound port */
	protected String rsopURI;

	/** Request notification inbound port */
	protected String rnipURI;

	/** Request generator management outbound port */
	protected String rgmopURI;

	RequestGenerator rg;

	public Application(String applicationURI, 
			String ApplicationSubmissionOutboundPortURI,
			String RequestGeneratorManagementInboundPort,
			String applicationNotificationOutboundPortURI , 
			String managementInboundPortURI ) throws Exception{

		super(1,1);

		this.applicationURI=applicationURI;

		this.addRequiredInterface(ApplicationSubmissionI.class) ;
		this.apsop = new ApplicationSubmissionOutboundPort(ApplicationSubmissionOutboundPortURI,ApplicationSubmissionI.class, this) ;
		this.addPort(this.apsop) ;
		this.apsop.publishPort() ;

		this.addRequiredInterface( ApplicationNotificationI.class );
		this.anop = new ApplicationNotificationOutboundPort( applicationNotificationOutboundPortURI , this );
		this.addPort( anop );
		this.anop.localPublishPort();

		this.addOfferedInterface( ApplicationManagementI.class );
		this.apmip = new ApplicationManagementInboundPort( managementInboundPortURI , this );
		this.addPort( this.apmip );
		this.apmip.publishPort();


		//Initialize RG URI
		this.rgURI = applicationURI + "-rg";
		this.rgmipURI = applicationURI + "-rgmip";
		this.rsopURI = applicationURI + "-rsop";
		this.rnipURI = applicationURI + "-rnip";
		this.rgmopURI = applicationURI + "-rgmop";

	}

	public void submissionApplication() throws Exception{

		System.out.println("Submission Application");

		String result[] = this.apsop.submitApplication();

		if(result[0] != null){

			// --------------------------------------------------------------------
			// Creating the request generator component.
			// --------------------------------------------------------------------
			
			
			
			rg = new RequestGenerator(rgURI, 500.0, 6000000000L, rgmipURI, rsopURI, rnipURI) ;
			AbstractCVM.theCVM.addDeployedComponent(rg) ;


			RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rg.findPortFromURI(rsopURI);
            rsop.doConnection( result[0] , RequestSubmissionConnector.class.getCanonicalName() );
			
			rg.toggleTracing() ;
			rg.toggleLogging() ;

			

			this.rgmop = new RequestGeneratorManagementOutboundPort(rgmopURI, this) ;
			this.rgmop.publishPort() ;
			this.rgmop.doConnection( rgmipURI , RequestGeneratorManagementConnector.class.getCanonicalName() );


		}else{
			System.out.println("Pas de ressources disponibles.");
		}
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			if (this.apsop.connected()) {
				this.apsop.doDisconnection();
			}

			if (this.rgmop.connected()) 
				this.rgmop.doDisconnection();
		}
		catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	public void stopApplication() {
		// TODO Auto-generated method stub

	}



}
