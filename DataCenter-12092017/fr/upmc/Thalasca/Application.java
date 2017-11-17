package fr.upmc.Thalasca;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

public class Application extends AbstractComponent {
	
	public static final String	GeneratorRequestSubmissionOutboundPortURI = "grsop" ;
	public static final String	GeneratorRequestNotificationInboundPortURI = "grnip" ;
	
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;
	
	protected DynamicComponentCreationOutboundPort portRequestGenerator;
	
	public Application() throws Exception
	{
		// create request Generator
		this.portRequestGenerator.createComponent(
			ApplicationVM.class.getCanonicalName(),
			new Object[] {
					"rg",			// generator component URI
					500.0,			// mean time between two requests
					6000000000L,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					GeneratorRequestSubmissionOutboundPortURI,
					GeneratorRequestNotificationInboundPortURI});	
			
	}
}
