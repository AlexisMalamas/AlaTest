package fr.upmc.Thalasca.datacenter.software.VM;


/**
 * 
 * @author Kevin GESNOUIN and Alexis MALAMAS
 *
 */

public class VM {

	protected String vmURI;
	protected String vmRequestSubmissionInboundURI;
	protected String VmRequestNotificationOutboundPortURI;

	public VM(String vmURI, String vmRequestSubmissionInboundURI, String vmRequestNotificationOutboundPortURI) {
		this.vmURI = vmURI;
		this.vmRequestSubmissionInboundURI = vmRequestSubmissionInboundURI;
		VmRequestNotificationOutboundPortURI = vmRequestNotificationOutboundPortURI;
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
}
