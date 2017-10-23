package fr.upmc.datacenter.software.admissionController;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS
 *
 */

public class AdmissionController 
extends AbstractComponent{
	
	
	protected ArrayList<ApplicationVM> listApplicationVM;
	protected ArrayList<RequestGenerator> listRequestGenerator;
	protected ArrayList<Dispatcher> listDispatcher;
	
 	
	public AdmissionController()
	{
		super(1,1);
		listApplicationVM = new ArrayList<ApplicationVM>();
		listDispatcher = new ArrayList<Dispatcher>();
	}
	
	public boolean requestGeneratorAccept(RequestGenerator rg)
	{
		// create dispatcher
		// create vm if proc dispo
		// connect vm to processor
		// connect dispatcher to vm
		//connect rg to dispatcher
		return true;
	}
	
	public void createAndConnectVm()
	{
		
	}
}
