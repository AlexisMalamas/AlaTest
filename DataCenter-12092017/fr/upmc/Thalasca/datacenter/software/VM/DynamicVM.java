package fr.upmc.Thalasca.datacenter.software.VM;

import java.net.InetAddress;

import fr.upmc.Thalasca.software.performanceController.interfaces.PerformanceControllerDynamicStateI;
import fr.upmc.datacenter.TimeManagement;

public class DynamicVM 
implements PerformanceControllerDynamicStateI {

	private static final long	serialVersionUID = 1L;
	protected final long timestamp;
	protected final String timestamperIP;
	
	protected VM vm;
	
	public DynamicVM(VM vm) throws Exception
	{
		super() ;
		this.timestamp = TimeManagement.timeStamp() ;
		this.timestamperIP = InetAddress.getLocalHost().getHostAddress() ;
		this.vm = vm;
	}
	
	@Override
	public long getTimeStamp() {
		return this.timestamp;
	}

	@Override
	public String getTimeStamperId() {
		return new String(this.timestamperIP);
	}

	@Override
	public VM getVM() {
		return this.vm;
	}

}
