package fr.upmc.Thalasca.datacenter.software.VM;

import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;

/**
 * 
 * @author Kevin GESNOUIN and Alexis MALAMAS
 *
 */
public class VM {

	protected int idVM;
	protected String vmURI;
	protected String vmRequestSubmissionInboundURI;
	protected String VmRequestNotificationOutboundPortURI;
	protected ApplicationVMManagementOutboundPort avmop;

	public VM(int idVM, String vmURI, String vmRequestSubmissionInboundURI, 
			String vmRequestNotificationOutboundPortURI, ApplicationVMManagementOutboundPort avmop) {
		this.idVM = idVM;
		this.vmURI = vmURI;
		this.vmRequestSubmissionInboundURI = vmRequestSubmissionInboundURI;
		this.VmRequestNotificationOutboundPortURI = vmRequestNotificationOutboundPortURI;
		this.avmop = avmop;
	}
	
	public ApplicationVMManagementOutboundPort getAvmop() {
		return avmop;
	}

	public String getVmURI() {
		return vmURI;
	}

	public String getVmRequestSubmissionInboundURI() {
		return vmRequestSubmissionInboundURI;
	}
	public String getVmRequestNotificationOutboundPortURI() {
		return VmRequestNotificationOutboundPortURI;
	}
	
	public int getIdVM() {
		return idVM;
	}
}
