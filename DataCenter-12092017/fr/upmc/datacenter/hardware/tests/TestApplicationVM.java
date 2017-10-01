package fr.upmc.datacenter.hardware.tests;

//Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

/**
 * The class <code>TestApplicationVM</code> deploys an
 * <code>ApplicationVM</code> running on a <code>Computer</code> component
 * connected to a <code>ComputerMonitor</code> component and then
 * execute a test scenario.
 *
 * <p><strong>Description</strong></p>
 * 
 * The test scenario submits ten requests to the application virtual machine
 * and the waits for the completion of these requests. In parallel, the
 * computer monitor starts the notification of the dynamic state of the
 * computer by requesting 25 pushes at the rate of one each second.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : May 4, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				TestApplicationVM
extends		AbstractCVM
{
	// ------------------------------------------------------------------------
	// Static inner classes
	// ------------------------------------------------------------------------

	public static class	Request
	implements	RequestI
	{
		private static final long serialVersionUID = 1L ;
		protected final long	numberOfInstructions ;
		protected final String	requestURI ;

		public			Request(long numberOfInstructions)
		{
			super() ;
			this.numberOfInstructions = numberOfInstructions ;
			this.requestURI = java.util.UUID.randomUUID().toString() ;
		}

		public			Request(
			String uri,
			long numberOfInstructions
			)
		{
			super() ;
			this.numberOfInstructions = numberOfInstructions ;
			this.requestURI = uri ;
		}

		@Override
		public long		getPredictedNumberOfInstructions()
		{
			return this.numberOfInstructions ;
		}

		@Override
		public String	getRequestURI()
		{
			return this.requestURI ;
		}
	}

	public static class	RequestionNotificationConsumer
	extends		AbstractComponent
	implements	RequestNotificationHandlerI
	{
		public static boolean	ACTIVE = true ;

		public			RequestionNotificationConsumer()
		{
			super(1, 0);
		}

		@Override
		public void		acceptRequestTerminationNotification(RequestI r)
		throws Exception
		{
			if (RequestionNotificationConsumer.ACTIVE) {
				this.logMessage(" Request " +
							   				r.getRequestURI() + " has ended.") ;
			}
		}
	}

	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;
	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;

	protected ComputerServicesOutboundPort	csPort ;
	protected ComputerMonitor				cm ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				TestApplicationVM()
	throws Exception
	{
		super() ;
	}

	public				TestApplicationVM(boolean isDistributed) throws Exception
	{
		super(isDistributed);
		// TODO Auto-generated constructor stub
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;
		admissibleFrequencies.add(3000) ;
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;
		processingPower.put(3000, 3000000) ;
		Computer c = new Computer(
							computerURI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1
							// 3000,	// Test scenario 2
							1500,
							numberOfProcessors,
							numberOfCores,
							ComputerServicesInboundPortURI,
							ComputerStaticStateDataInboundPortURI,
							ComputerDynamicStateDataInboundPortURI) ;
		c.toggleTracing() ;
		c.toggleLogging() ;
		this.addDeployedComponent(c) ;

		this.csPort = new ComputerServicesOutboundPort(
										ComputerServicesOutboundPortURI,
										new AbstractComponent(0, 0){}) ;
		this.csPort.publishPort() ;
		this.csPort.doConnection(
						ComputerServicesInboundPortURI,
						ComputerServicesConnector.class.getCanonicalName()) ;

		this.cm = new ComputerMonitor(computerURI,
									  true,
									  ComputerStaticStateDataOutboundPortURI,
									  ComputerDynamicStateDataOutboundPortURI) ;
		this.cm.toggleLogging() ;
		this.cm.toggleTracing() ;
		this.addDeployedComponent(this.cm) ;
		this.cm.doPortConnection(ComputerStaticStateDataOutboundPortURI,
								 ComputerStaticStateDataInboundPortURI,
								 DataConnector.class.getCanonicalName()) ;

		this.cm.doPortConnection(ComputerDynamicStateDataOutboundPortURI,
								 ComputerDynamicStateDataInboundPortURI,
								 ControlledDataConnector.class.getCanonicalName());

		super.deploy();
	}

	@Override
	public void			start() throws Exception
	{
		super.start() ;
	}

	@Override
	public void			shutdown() throws Exception
	{
		this.csPort.doDisconnection() ;
		this.cm.doPortDisconnection(ComputerStaticStateDataOutboundPortURI) ;
		this.cm.doPortDisconnection(ComputerDynamicStateDataOutboundPortURI) ;

		super.shutdown();
	}

	public void			testScenario() throws Exception
	{
		AllocatedCore[] ac = this.csPort.allocateCores(4) ;

		ApplicationVM vm =
			new ApplicationVM("vm0",
							  ApplicationVMManagementInboundPortURI,
							  RequestSubmissionInboundPortURI,
							  RequestNotificationOutboundPortURI) ;
		this.addDeployedComponent(vm) ;
		vm.toggleTracing() ;
		vm.toggleLogging() ;
		vm.start() ;

		ApplicationVMManagementOutboundPort avmPort =
				new ApplicationVMManagementOutboundPort(
						ApplicationVMManagementOutboundPortURI,
						new AbstractComponent(0, 0) {}) ;
		avmPort.publishPort() ;
		avmPort.doConnection(
					ApplicationVMManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
		avmPort.allocateCores(ac) ;

		RequestSubmissionOutboundPort rsobp =
					new RequestSubmissionOutboundPort(
									RequestSubmissionOutboundPortURI,
									new AbstractComponent(0, 0) {}) ;
		rsobp.publishPort() ;
		rsobp.doConnection(
				RequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;

		RequestionNotificationConsumer rnc =
										new RequestionNotificationConsumer() ;
		rnc.toggleLogging() ;
		rnc.toggleTracing() ;
		this.addDeployedComponent(rnc) ;
		rnc.start() ;
		RequestNotificationInboundPort nibp =
					new RequestNotificationInboundPort(
									RequestNotificationInboundPortURI,
									rnc) ;
		nibp.publishPort() ;

		vm.doPortConnection(
				RequestNotificationOutboundPortURI,
				RequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;

		for(int i = 0 ; i < 10 ; i++) {
			rsobp.submitRequestAndNotify(new Request("r" + i, 6000000000L)) ;
			Thread.sleep(500L) ;
		}
		Thread.sleep(40000L) ;
		rsobp.doDisconnection() ;
		rsobp.unpublishPort() ;
	}

	public static void	main(String[] args)
	{
		//AbstractCVM.toggleDebugMode() ;
		try {
			final TestApplicationVM tappvm = new TestApplicationVM() ;
			tappvm.deploy() ;
			System.out.println("starting...") ;
			tappvm.start() ;
			new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									tappvm.testScenario() ;
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						}).start() ;
			Thread.sleep(60000L) ;
			System.out.println("shutting down...") ;
			tappvm.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
