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
	
	public String getVmURI() {
		return vmURI;
	}

	public void setVmURI(String vmURI) {
		this.vmURI = vmURI;
	}

	public String getVmRequestSubmissionInboundURI() {
		return vmRequestSubmissionInboundURI;
	}

	public void setVmRequestSubmissionInboundURI(String vmRequestSubmissionInboundURI) {
		this.vmRequestSubmissionInboundURI = vmRequestSubmissionInboundURI;
	}

	public String getVmRequestNotificationOutboundPortURI() {
		return VmRequestNotificationOutboundPortURI;
	}

	public void setVmRequestNotificationOutboundPortURI(String vmRequestNotificationOutboundPortURI) {
		VmRequestNotificationOutboundPortURI = vmRequestNotificationOutboundPortURI;
	}

	public ApplicationVMManagementOutboundPort getAvmop() {
		return avmop;
	}

	public void setAvmop(ApplicationVMManagementOutboundPort avmop) {
		this.avmop = avmop;
	}

	public int getIdVM() {
		return idVM;
	}

	public void setIdVM(int idVM) {
		this.idVM = idVM;
	}
}
