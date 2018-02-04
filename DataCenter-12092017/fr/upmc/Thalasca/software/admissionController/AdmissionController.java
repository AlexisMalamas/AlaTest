package fr.upmc.Thalasca.software.admissionController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.Thalasca.datacenter.software.VM.DynamicVM;
import fr.upmc.Thalasca.datacenter.software.VM.VM;
import fr.upmc.Thalasca.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.Thalasca.datacenter.software.dispatcher.connectors.DispatcherManagementConnector;
import fr.upmc.Thalasca.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.Thalasca.datacenter.software.dispatcher.ports.DispatcherManagementOutboundport;
import fr.upmc.Thalasca.datacenterclient.Application.connectors.ApplicationControllerNotificationConnector;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationControllerNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationRequestI;
import fr.upmc.Thalasca.datacenterclient.Application.interfaces.ApplicationSubmissionNotificationI;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationControllerNotificationOutboundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationManagementOutBoundPort;
import fr.upmc.Thalasca.datacenterclient.Application.ports.ApplicationSubmissionNotificationInboundPort;
import fr.upmc.Thalasca.software.admissionController.interfaces.AdmissionControllerI;
import fr.upmc.Thalasca.software.admissionController.ports.AdmissionControllerInBoundPort;
import fr.upmc.Thalasca.software.performanceController.PerformanceController;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerDynamicStateI;
import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerStateDataConsumerI;
import fr.upmc.Thalasca.software.performanceController.ports.PerformanceControllerDynamicStateDataInboundPort;
import fr.upmc.Thalasca.software.performanceController.ports.PerformanceControllerDynamicStateDataOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.ports.DataInboundPortI;
import fr.upmc.components.ports.DataOutboundPortI;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.processors.ProcessorStaticState;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.PushModeControllingI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */
public class AdmissionController 
extends AbstractComponent
implements ApplicationRequestI, AdmissionControllerI, PushModeControllingI, PerformanceControllerStateDataConsumerI{

	public static final int NB_CORES_BY_VM=2;
	public int ID_VM = 1;

	public static final String	DispatcherRequestSubmissionInboundPortURI = "drsip" ;
	public static final String	DispatcherRequestSubmissionOutboundPortURI = "drsop" ;
	public static final String	DispatcherRequestNotificationInboundPortURI = "drnip" ;
	public static final String	DispatcherRequestNotificationOutboundPortURI = "drnop" ;

	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	VmRequestNotificationOutboundPortURI = "vmrnop" ;
	public static final String	VmRequestSubmissionInboundPortURI = "vmsip" ;

	public static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	public static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;

	public static final String DispatcherManagementInboundPortURI = "dmip";

	public static final String AdmissionControllerInboundPortURI = "acip";
	public static final String AdmissionControllerOutboundPortURI = "acop";
	
	public static final String performanceControllerInboundPortURI = "pcip";
	public static final String performanceControllerOutboundPortURI = "pcop";
	
	public static final int intervalPushingTime = 500;

	protected final String ApplicationVmURI = "";
	protected final String DispatcherURI = "";
	protected final String PerformanceControllerURI = "";
	protected final String admissionControllerURI;

	protected DynamicComponentCreationOutboundPort portApplicationVM;
	protected DynamicComponentCreationOutboundPort portDispatcher;
	protected DynamicComponentCreationOutboundPort portPerformanceController;

	protected ArrayList<ComputerServicesOutboundPort> csopList;
	protected ArrayList<ComputerStaticStateDataOutboundPort> cssdopList;
	protected ArrayList<ComputerDynamicStateDataOutboundPort> cdsdopList;

	protected ApplicationControllerNotificationOutboundPort appcnop;
	protected String appcnip;
	protected ApplicationSubmissionNotificationInboundPort appsnip;

	protected HashMap<String, DispatcherManagementOutboundport> dmopList;
	protected ArrayList<String> dispatcherList;
	protected ArrayList<String> computerURIList;
	protected ArrayList<String> performanceControllerURIList;
	

	protected PerformanceControllerDynamicStateDataInboundPort pcdsip;
	protected PerformanceControllerDynamicStateDataOutboundPort pcdsop;
	protected AdmissionControllerInBoundPort acip;
	
	protected ScheduledFuture<?> pushingFuture;
	protected ArrayList<VM> listVmAvailable;
	
	// add for update frequency core and proc
	protected ArrayList<ArrayList<String>> processorURIList; // processor Uri List by computer. First index for computer
	protected ArrayList<ArrayList<String>> processorManagementInboudPortURIList;
	protected ArrayList<ArrayList<String>> processorStaticStateInboudPortURIList;
	protected ArrayList<ArrayList<String>> processorDynamicStateInboudPortURIList;
	protected HashMap<Integer, String> processorURIListByVM; // first index for id vm
	protected HashMap<Integer, ArrayList<Integer>> coreNoListByVM; // first index for id vm
	
	
	public AdmissionController(
			ArrayList<String> computerServicesInboundPortURI,
			ArrayList<String> computerServicesOutboundPortURI,
			ArrayList<String> computerStaticStateDataInboundPortURI,
			ArrayList<String> computerStaticStateDataOutboundPortURI,
			ArrayList<String> computerDynamicStateDataInboundPortURI,
			ArrayList<String> computerDynamicStateDataOutboundPortURI,
			String applicationSubmissionNotificationInboundPortURI,
			String applicationControllerNotificationOutboundPortURI,
			ArrayList<String> computerURI,
			String admissionControllerURI,
			String applicationControllerNotificationInboundPortURI) throws Exception
	{
		super(admissionControllerURI, 1, 1);
		this.admissionControllerURI = admissionControllerURI;
		appcnip = applicationControllerNotificationInboundPortURI;

		this.addOfferedInterface(ApplicationSubmissionNotificationI.class);
		this.appsnip = new ApplicationSubmissionNotificationInboundPort(applicationSubmissionNotificationInboundPortURI, this);
		this.addPort(this.appsnip);
		this.appsnip.publishPort();

		this.addRequiredInterface(ApplicationControllerNotificationI.class);
		this.appcnop = new ApplicationControllerNotificationOutboundPort(applicationControllerNotificationOutboundPortURI, this);
		this.addPort(this.appcnop);
		this.appcnop.publishPort();
		
		this.addOfferedInterface(AdmissionControllerI.class) ;
		this.acip=new AdmissionControllerInBoundPort(AdmissionControllerInboundPortURI, this);
		this.addPort(this.acip);
		this.acip.publishPort();
		
		this.addOfferedInterface(DataInboundPortI.class) ;
		this.pcdsip = new PerformanceControllerDynamicStateDataInboundPort(performanceControllerInboundPortURI, this);
		addPort(pcdsip);
		pcdsip.publishPort();
		
		this.addRequiredInterface(DataOutboundPortI.class) ;
		this.pcdsop = new PerformanceControllerDynamicStateDataOutboundPort(performanceControllerOutboundPortURI, this);
		addPort(pcdsop);
		pcdsop.publishPort();
		pcdsop.doConnection(performanceControllerInboundPortURI, ControlledDataConnector.class.getCanonicalName());

		this.computerURIList=computerURI;

		this.dmopList = new HashMap<String, DispatcherManagementOutboundport>();
		this.dispatcherList = new ArrayList<String>();
		this.performanceControllerURIList = new ArrayList<String>();
		this.csopList = new ArrayList<ComputerServicesOutboundPort>();
		this.cssdopList = new ArrayList<ComputerStaticStateDataOutboundPort>();
		this.cdsdopList = new ArrayList<ComputerDynamicStateDataOutboundPort>();
		this.listVmAvailable = new ArrayList<VM>();

		for(int i=0; i<this.computerURIList.size(); i++){

			this.csopList.add(new ComputerServicesOutboundPort(computerServicesOutboundPortURI.get(i), this));
			this.addPort(this.csopList.get(this.csopList.size()-1));
			this.csopList.get(this.csopList.size()-1).publishPort();
			
			this.cssdopList.add(new ComputerStaticStateDataOutboundPort(computerStaticStateDataOutboundPortURI.get(i), this, this.computerURIList.get(i)));
			this.addPort(this.cssdopList.get(this.cssdopList.size()-1));
			this.cssdopList.get(this.cssdopList.size()-1).publishPort();
			
			this.cdsdopList.add(new ComputerDynamicStateDataOutboundPort(computerDynamicStateDataOutboundPortURI.get(i), this, this.computerURIList.get(i)));
			this.addPort(this.cdsdopList.get(this.cdsdopList.size()-1));
			this.cdsdopList.get(this.cdsdopList.size()-1).publishPort();

			this.doPortConnection(				
					computerServicesOutboundPortURI.get(i),
					computerServicesInboundPortURI.get(i),
					ComputerServicesConnector.class.getCanonicalName());

			this.doPortConnection(
					computerStaticStateDataOutboundPortURI.get(i),
					computerStaticStateDataInboundPortURI.get(i),
					DataConnector.class.getCanonicalName());

			this.doPortConnection(
					computerDynamicStateDataOutboundPortURI.get(i),
					computerDynamicStateDataInboundPortURI.get(i),
					ControlledDataConnector.class.getCanonicalName());
		}
		
		// add for update frequency core and proc
		this.processorURIList = new ArrayList<ArrayList<String>>();
		this.processorManagementInboudPortURIList = new ArrayList<ArrayList<String>>();
		this.processorStaticStateInboudPortURIList = new ArrayList<ArrayList<String>>();
		this.processorDynamicStateInboudPortURIList = new ArrayList<ArrayList<String>>();
		
		this.processorURIListByVM = new HashMap<Integer, String>();
		this.coreNoListByVM = new HashMap<Integer, ArrayList<Integer>>();
		
		for(int i=0; i<this.computerURIList.size(); i++){
			this.processorURIList.add(new ArrayList<String>());
			this.processorManagementInboudPortURIList.add(new ArrayList<String>());
			this.processorStaticStateInboudPortURIList.add(new ArrayList<String>());
			this.processorDynamicStateInboudPortURIList.add(new ArrayList<String>());
			
			ComputerStaticStateI computerStatic  = (ComputerStaticStateI) this.cssdopList.get(i).request();
			Map<Integer, String> processorURI = computerStatic.getProcessorURIs();
			
			for( Map.Entry<Integer, String> e : processorURI.entrySet()){
				Map<ProcessorPortTypes, String> processorPortsList = computerStatic.getProcessorPortMap().get(e.getValue());
				processorURIList.get(i).add(e.getValue());
				processorManagementInboudPortURIList.get(i).add(processorPortsList.get(Processor.ProcessorPortTypes.MANAGEMENT));
				processorStaticStateInboudPortURIList.get(i).add(processorPortsList.get(Processor.ProcessorPortTypes.STATIC_STATE));
				processorDynamicStateInboudPortURIList.get(i).add(processorPortsList.get(Processor.ProcessorPortTypes.DYNAMIC_STATE));
			}
		}
		this.addRequiredInterface(DynamicComponentCreationI.class);
	}

	@Override
	public void start() throws ComponentStartException {

		super.start();			

		try {	
			this.portApplicationVM = new DynamicComponentCreationOutboundPort(this);
			this.portApplicationVM.localPublishPort();
			this.addPort(this.portApplicationVM);

			this.portApplicationVM.doConnection(					
					this.ApplicationVmURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());

			this.portDispatcher = new DynamicComponentCreationOutboundPort(this);
			this.portDispatcher.publishPort();
			this.addPort(this.portDispatcher);
			this.portDispatcher.doConnection(					
					this.DispatcherURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());

			this.portPerformanceController = new DynamicComponentCreationOutboundPort(this);
			this.portPerformanceController.publishPort();
			this.addPort(this.portPerformanceController);
			this.portPerformanceController.doConnection(					
					this.PerformanceControllerURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
					DynamicComponentCreationConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			throw new ComponentStartException("Error start AdmissionController", e);
		}
	}

	public void deployDynamicComponentsForApplication(String applicationUri, ArrayList<AllocatedCore[]> ac,
			ApplicationManagementOutBoundPort appmop, int nombreVM, int idVM) throws Exception {						 			
		System.out.println("Deploy dynamic components for " + applicationUri);
		ArrayList<VM> listCreatedVm = new ArrayList<VM>();
		
		//create applicationVM for accepted application
		for(int i=0; i<nombreVM; i++){
			this.portApplicationVM.createComponent(
					ApplicationVM.class.getCanonicalName(),
					new Object[] {
							idVM+"_VM",
							idVM+"_"+ApplicationVMManagementInboundPortURI,
							idVM+"_"+VmRequestSubmissionInboundPortURI,
							idVM+"_"+VmRequestNotificationOutboundPortURI
					});

			// create ApplicationVMManagementOutboundPort 
			ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(
					idVM+"_"+ApplicationVMManagementOutboundPortURI, this);
			avmop.publishPort();
			avmop.doConnection(idVM+"_"+ApplicationVMManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName());			

			avmop.allocateCores(ac.get(i));
			listCreatedVm.add(new VM(idVM, idVM+"_VM", idVM+"_"+VmRequestSubmissionInboundPortURI,
					idVM+"_"+VmRequestNotificationOutboundPortURI, avmop));
			idVM++;
		}

		// create dispatcher for accepted application
		this.portDispatcher.createComponent(
				Dispatcher.class.getCanonicalName(),
				new Object[] {
						applicationUri,
						applicationUri+"_dispatcher",
						applicationUri+"_"+DispatcherRequestSubmissionInboundPortURI,
						applicationUri+"_"+DispatcherManagementInboundPortURI,
						applicationUri+"_"+DispatcherRequestNotificationOutboundPortURI,
						applicationUri+"_"+DispatcherRequestNotificationInboundPortURI
				});

		// connect dispatcher
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		this.addPort(rop);
		rop.localPublishPort();
		rop.doConnection(applicationUri+"_dispatcher", ReflectionConnector.class.getCanonicalName());
		rop.toggleLogging();
		rop.toggleTracing();


		appmop.connectionDispatcherWithRequestGeneratorForSubmission(
				applicationUri+"_"+DispatcherRequestSubmissionInboundPortURI, applicationUri);

		appmop.connectionDispatcherWithRequestGeneratorForNotification(rop, applicationUri+"_"+DispatcherRequestNotificationOutboundPortURI, applicationUri);		


		// create DispatcherManagementOutboundport and connect
		this.addRequiredInterface(DispatcherManagementI.class);
		this.dmopList.put(applicationUri, new DispatcherManagementOutboundport(applicationUri+"_dmop", this));
		this.addPort(this.dmopList.get(applicationUri));
		this.dmopList.get(applicationUri).publishPort();
		this.dmopList.get(applicationUri).doConnection(
			applicationUri+"_"+DispatcherManagementInboundPortURI,
			DispatcherManagementConnector.class.getCanonicalName());

		for(int i=0; i<listCreatedVm.size(); i++){
			// connect dispatcher to VM
			this.dmopList.get(applicationUri).connectToVirtualMachine(listCreatedVm.get(i));
			// connect VM to dispatcher
			rop.doConnection(listCreatedVm.get(i).getVmURI(), ReflectionConnector.class.getCanonicalName());
			rop.toggleTracing();
			rop.toggleLogging();
			rop.doPortConnection(
					listCreatedVm.get(i).getVmRequestNotificationOutboundPortURI(),
					applicationUri+"_"+DispatcherRequestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
		}
		// create performanceController for application
		this.portPerformanceController.createComponent(
				PerformanceController.class.getCanonicalName(),
				new Object[] {
						applicationUri+"_performanceController",
						applicationUri+"_"+DispatcherManagementInboundPortURI,
						AdmissionControllerInboundPortURI,
						applicationUri,
						applicationUri+"_"+performanceControllerInboundPortURI,
						applicationUri+"_"+performanceControllerOutboundPortURI,
						applicationUri+"_"+DispatcherRequestNotificationInboundPortURI,
						VmRequestNotificationOutboundPortURI
				});

		// stop pushing for add a performanceController in Ring
		this.stopPushing();
		addPerformanceControllerInRing(applicationUri);
		this.startUnlimitedPushing(intervalPushingTime);

		System.out.println("finish creation ressources for the application "+applicationUri);
	}

	@Override
	public void receiveApplicationToAdmissionController(String applicationURI, ApplicationManagementOutBoundPort appmop, int nombreVM) throws Exception {

		System.out.println("Application reçue :"+applicationURI);

		ArrayList<AllocatedCore[]> allocatedCores = new ArrayList<>();
		boolean ressourcesAvailable = false;
		for(int i=0; i<nombreVM; i++){
			for(int j=0; j<this.computerURIList.size(); j++) {
				AllocatedCore[] allocatedCore= csopList.get(j).allocateCores(NB_CORES_BY_VM);
				if(allocatedCore.length == NB_CORES_BY_VM) {
					allocatedCores.add(allocatedCore);
					ressourcesAvailable = true;
					break;
				}
				else
					ressourcesAvailable = false;
			}
		}

		this.appcnop.doConnection(applicationURI+"_"+this.appcnip, ApplicationControllerNotificationConnector.class.getCanonicalName());
		if (ressourcesAvailable) {
			System.out.println("Accept application " + applicationURI);
			int idVM;
			//reserve ID_Vm
			synchronized(this) {
				idVM = this.ID_VM;
				this.ID_VM += nombreVM;
			}
			
			//deploy all components for new accepted Application
			deployDynamicComponentsForApplication(applicationURI, allocatedCores, appmop, nombreVM, idVM);
			
			for(int i=0; i<nombreVM; i++) {
				this.coreNoListByVM.put(idVM, new ArrayList<Integer>());
				for(int j=0; j<allocatedCores.get(i).length; j++){
					this.processorURIListByVM.put(idVM, allocatedCores.get(i)[j].processorURI);
					this.coreNoListByVM.get(idVM).add(allocatedCores.get(i)[j].coreNo);
				}
				idVM++;
			}
			
			//send response to application
			this.appcnop.responseFromAdmissionController(true, applicationURI);

		} else {
			System.out.println("Application rejected");	
			this.appcnop.responseFromAdmissionController(false, applicationURI);
		}
	}

	/**
	 * 
	 *  call changedFrequencyCore function with true parameter for up
	 *  @param 	applicationURI	URI of application
	 * 	@param	idVM			id of VM
	 * 	@return	boolean			return if we changed frequency of given VM else return false
	 * 
	 */
	@Override
	public boolean upFrequencyCores(String applicationURI, int idVM) throws Exception {
		return changedFrequencyCore(applicationURI, idVM, true);
	}
	
	/**
	 * 
	 *  call changedFrequencyCore function with false parameter for down
	 *  @param 	applicationURI	URI of application
	 * 	@param	idVM			id of VM
	 * 	@return	boolean			return if we changed frequency of given VM else return false
	 * 
	 */
	@Override
	public boolean downFrequencyCores(String applicationURI, int idVM) throws Exception {
		return changedFrequencyCore(applicationURI, idVM, false);
	}
	
	/**
	 * 
	 *  down or up (depend on value of up boolean given in parameter) frequency of all cores used by idVM.
	 *  return true if frequency changed for at least one core or return false
	 * 	@param 	applicationURI	URI of application
	 * 	@param	idVM			id of VM
	 * 	@return	boolean			return if we changed frequency of given VM else return false
	 * 
	 */
	public boolean changedFrequencyCore(String applicationURI, int idVM, boolean up) throws Exception
	{
		String procUri = processorURIListByVM.get(idVM);
		
		int idComputerInprocessorURIList = -1;
		int idVmInprocessorURIList = -1;
		for(int i=0; i<this.processorURIList.size() && idComputerInprocessorURIList==-1; i++)
			for(int j=0; j<this.processorURIList.get(i).size(); j++)
			{
				if(this.processorURIList.get(i).get(j).equals(procUri))
				{
					idComputerInprocessorURIList = i;
					idVmInprocessorURIList = j;
					break;
				}
			}
		
		String pssInBoundPort = this.processorStaticStateInboudPortURIList
				.get(idComputerInprocessorURIList).get(idVmInprocessorURIList);
		addRequiredInterface(ProcessorStaticState.class);
		ProcessorStaticStateDataOutboundPort pss = new ProcessorStaticStateDataOutboundPort(this, applicationURI+idVM+"cssdop");
		addPort(pss);
		pss.publishPort();
		pss.doConnection(pssInBoundPort, ControlledDataConnector.class.getCanonicalName());
		
		ProcessorStaticStateI pssi = (ProcessorStaticStateI) pss.request();
		
		Integer[] addmissibleFrequencies = pssi.getAdmissibleFrequencies().toArray(new Integer[pssi.getAdmissibleFrequencies().size()]);
		Arrays.sort(addmissibleFrequencies);
		
		String pdsInBoundPort = this.processorDynamicStateInboudPortURIList
				.get(idComputerInprocessorURIList).get(idVmInprocessorURIList);
		
		addRequiredInterface(ProcessorDynamicStateI.class);
		ProcessorDynamicStateDataOutboundPort pds = new ProcessorDynamicStateDataOutboundPort(this, applicationURI+idVM+"cdsdop");
		addPort(pds);
		pds.publishPort();
		pds.doConnection(pdsInBoundPort, ControlledDataConnector.class.getCanonicalName());
		
		ProcessorDynamicStateI pdsi = (ProcessorDynamicStateI) pds.request();
		
		String pmInBoundPort = this.processorManagementInboudPortURIList
				.get(idComputerInprocessorURIList).get(idVmInprocessorURIList);
		
		addRequiredInterface(ProcessorManagementI.class);
		ProcessorManagementOutboundPort pm = new ProcessorManagementOutboundPort(applicationURI+idVM+"pmop", this);
		addPort(pm);
		pm.publishPort();
		pm.doConnection(pmInBoundPort, ProcessorManagementConnector.class.getCanonicalName());
		
		boolean frequencyChanged = false;
		//changed frequency of all cores used by VM
		for(int i=0; i<this.coreNoListByVM.get(idVM).size(); i++)
		{
			int numeroCore = this.coreNoListByVM.get(idVM).get(i);
			int currentFrequency = pdsi.getCurrentCoreFrequencies()[numeroCore];
			
			// if we want up Cores frequencies to next availableFrequency
			if(up)
				//find FrequencyAvailble
				for(int j=0; j<addmissibleFrequencies.length;j++) 
				{
					if(addmissibleFrequencies[j]>currentFrequency)
					{
						//update frequency
						pm.setCoreFrequency(numeroCore, addmissibleFrequencies[j]);
						frequencyChanged = true;
						break;
					}
				}
			// if we want down Cores frequencies to previous availableFrequency
			else
			{
				for(int j=addmissibleFrequencies.length-1; j>0;j--) 
				{
					if(addmissibleFrequencies[j]>currentFrequency)
					{
						//update frequency
						pm.setCoreFrequency(numeroCore, addmissibleFrequencies[j]);
						frequencyChanged = true;
						break;
					}
				}
			}
		}
		
		return frequencyChanged;
	}

	/**
	 * 
	 * Return table of frequencies of cores used by given VM in parameter
	 * @param	applicationURI		URI of application
	 * @param	idVM				id of VM
	 * @return	ArrayList<Integer>	list of frequencies for given VM
	 * 
	 * */
	@Override
	public ArrayList<Integer> getFrequencyCores(String applicationURI, int idVM) throws Exception {
		
		ArrayList<Integer> coreFrequencies = new ArrayList<Integer>();
		
		String procUri = processorURIListByVM.get(idVM);
		
		int idComputerInprocessorURIList = -1;
		int idVmInprocessorURIList = -1;
		for(int i=0; i<this.processorURIList.size() && idComputerInprocessorURIList==-1; i++)
			for(int j=0; j<this.processorURIList.get(i).size(); j++)
			{
				if(this.processorURIList.get(i).get(j).equals(procUri))
				{
					idComputerInprocessorURIList = i;
					idVmInprocessorURIList = j;
					break;
				}
			}
		
		
		String pdsInBoundPort = this.processorDynamicStateInboudPortURIList
				.get(idComputerInprocessorURIList).get(idVmInprocessorURIList);
		
		addRequiredInterface(ProcessorDynamicStateI.class);
		ProcessorDynamicStateDataOutboundPort pds = new ProcessorDynamicStateDataOutboundPort(this, applicationURI+idVM+"cdsdop");
		addPort(pds);
		pds.publishPort();
		pds.doConnection(pdsInBoundPort, ControlledDataConnector.class.getCanonicalName());
		ProcessorDynamicStateI pdsi = (ProcessorDynamicStateI) pds.request();
		int[] frequencies = pdsi.getCurrentCoreFrequencies();
		
		for(int i=0; i<this.coreNoListByVM.get(idVM).size(); i++) {
			int numeroCore = this.coreNoListByVM.get(idVM).get(i);
			coreFrequencies.add(frequencies[numeroCore]);
		}
		
		return coreFrequencies;
	}

	/**
	 * 
	 * Create and add virtual Machine in list of available VM if enough available cores in computers.
	 * 
	 * @return	boolean	return true if VM added else false
	 * 
	 **/
	public boolean addVirtualMachine() throws Exception {
		System.out.println("Try create and add virtual Machine in list of available VM");
		int idVm = this.ID_VM;
		this.ID_VM++;
		
		// allocate core for new VM
		AllocatedCore[] allocatedCore = {};
		boolean ressourcesAvailable = false;
		for(int j=0; j<this.computerURIList.size(); j++) {
			allocatedCore= csopList.get(j).allocateCores(NB_CORES_BY_VM);
			if(allocatedCore.length == NB_CORES_BY_VM) {
				ressourcesAvailable = true;
				break;
			}
			else
				ressourcesAvailable = false;
		}
		
		if(!ressourcesAvailable)
			return false;
		
		// processor uri for allocated vm
		this.coreNoListByVM.put(idVm, new ArrayList<Integer>());
		for(int j=0; j<allocatedCore.length; j++){
			this.processorURIListByVM.put(idVm, allocatedCore[j].processorURI);
			this.coreNoListByVM.get(idVm).add(allocatedCore[j].coreNo);
		}

		//create VM
		this.portApplicationVM.createComponent(
				ApplicationVM.class.getCanonicalName(),
				new Object[] {
						idVm+"_VM",
						idVm+"_"+ApplicationVMManagementInboundPortURI,
						idVm+"_"+VmRequestSubmissionInboundPortURI,
						idVm+"_"+VmRequestNotificationOutboundPortURI
				});
		ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(
				idVm+"_"+ApplicationVMManagementOutboundPortURI, new AbstractComponent(0, 0) {});
		avmop.publishPort();
		avmop.doConnection(
				idVm+"_"+ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName());			

		avmop.allocateCores(allocatedCore);
		
		this.listVmAvailable.add(new VM(idVm, idVm+"_VM", idVm+"_"+VmRequestSubmissionInboundPortURI,
				idVm+"_"+VmRequestNotificationOutboundPortURI, avmop));
		
		System.out.println("VM created and added in list of available VM");
		return true;
	}
	
	/**
	 * 
	 * Function for add performance controller in ring and connect his port
	 * @param	applicationUri	URI of application
	 * 
	 **/
	public void addPerformanceControllerInRing(String applicationUri) throws Exception {
		System.out.println("Add performance controller in ring");
		performanceControllerURIList.add(applicationUri);
		if(performanceControllerURIList.size()==1)
		{
			// connect performance controller to admissionController
			ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
			this.addPort(rop);
			rop.localPublishPort();
			rop.doConnection(performanceControllerURIList.get(0)+"_performanceController", ReflectionConnector.class.getCanonicalName());
			rop.doPortConnection(
					performanceControllerURIList.get(0)+"_"+performanceControllerOutboundPortURI,
					performanceControllerInboundPortURI, ControlledDataConnector.class.getCanonicalName());

			// connect admissionController to performance controller
			this.pcdsop.doConnection(performanceControllerURIList.get(performanceControllerURIList.size()-1)
					+"_"+performanceControllerInboundPortURI, ControlledDataConnector.class.getCanonicalName());
			System.out.println("Performance controller added in ring");
		
		}
		else
		{
			// connect performance controller to next controller performance
			ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
			this.addPort(rop);
			rop.localPublishPort();
			rop.doConnection(performanceControllerURIList.get(performanceControllerURIList.size()-2)+"_performanceController",
					ReflectionConnector.class.getCanonicalName());
			rop.doPortConnection(
					performanceControllerURIList.get(performanceControllerURIList.size()-2)+"_"+performanceControllerOutboundPortURI,
					performanceControllerURIList.get(performanceControllerURIList.size()-1)+"_"+performanceControllerInboundPortURI,
					ControlledDataConnector.class.getCanonicalName());
			
			// connect performance controller to next admissionController
			ReflectionOutboundPort rop2 = new ReflectionOutboundPort(this);
			this.addPort(rop2);
			rop2.localPublishPort();
			rop2.doConnection(performanceControllerURIList.get(performanceControllerURIList.size()-1)+"_performanceController",
					ReflectionConnector.class.getCanonicalName());
			rop2.doPortConnection(
					performanceControllerURIList.get(performanceControllerURIList.size()-1)+"_"+performanceControllerOutboundPortURI,
					this.pcdsip.getPortURI(),
					ControlledDataConnector.class.getCanonicalName());
		}
	}

	/**
	 * Send available VM to next performanceController or admissionController
	 **/
	public void sendDynamicState() throws Exception
	{
		if (this.pcdsip.connected()) {
			PerformanceControllerDynamicStateI cds = this.getDynamicState();
			if(cds.getVM()!=null) {
				System.out.println("Admission controller send vm: "+cds.getVM().getIdVM());
				this.pcdsip.send(cds);
			}
		}
	}
	
	/**
	 *
	 * Accept and save VM from previous performanceController or from admissionController
	 * @param	currentDynamicState	Vm to send in ring
	 * 
	 **/
	@Override
	public void acceptPerformanceControllerDynamicData(PerformanceControllerDynamicStateI currentDynamicState) 
			throws Exception {
		System.out.println("AdmissionController recieve VM "+currentDynamicState.getVM().getIdVM());
		this.listVmAvailable.add(currentDynamicState.getVM());		
	}
	
	/**
	 *
	 * Push VM in ring
	 * @param	interval	time interval between 2 push of vm
	 * 
	 **/
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		final AdmissionController c = this ;
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
					TimeUnit.MILLISECONDS);
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {}

	/**
	 *	Stop pushing of VM in ring 
	 **/
	@Override
	public void stopPushing() throws Exception {
		if (this.pushingFuture != null &&
				!(this.pushingFuture.isCancelled() ||
									this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false) ;
		}
	}

	/**
	 *	Function used to return not used vm and wrap in DynamicVM object
	 *	@return	DynamicVM	Return not used vm and wrap in DynamicVM object
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
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			for(ComputerServicesOutboundPort csop: csopList){
				if (csop.connected()) {
					csop.doDisconnection();
				}
			}
			for(ComputerStaticStateDataOutboundPort cssdop: cssdopList){
				if (cssdop.connected()) {
					cssdop.doDisconnection();
				}
			}
			for(ComputerDynamicStateDataOutboundPort cdsdop: cdsdopList)
				if (cdsdop.connected()) {
					cdsdop.doDisconnection();
				}

			if (this.portDispatcher.connected()) {
				this.portDispatcher.doDisconnection();
			}
			if (this.portApplicationVM.connected()) {
				this.portApplicationVM.doDisconnection();
			}

			if (this.portPerformanceController.connected()) {
				this.portPerformanceController.doDisconnection();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException("Error shutdown AdmissionController", e);
		}

		super.shutdown();
	}
}
