package fr.upmc.datacenter.software.admissionController;

import java.util.ArrayList;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;

public class AdmissionController 
extends AbstractComponent{
	
	ArrayList<ApplicationVM> listApplicationVM;
	ArrayList<Dispatcher> listDispatcher;
	
	public AdmissionController()
	{
		super(1,1);
		listApplicationVM = new ArrayList<ApplicationVM>();
		listDispatcher = new ArrayList<Dispatcher>();
	}
}
