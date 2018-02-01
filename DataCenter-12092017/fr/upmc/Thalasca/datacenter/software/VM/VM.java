package fr.upmc.Thalasca.datacenter.software.VM;

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

	public VM(int idVM, String vmURI, String vmRequestSubmissionInboundURI, 
			String vmRequestNotificationOutboundPortURI) {
		this.idVM = idVM;
		this.vmURI = vmURI;
		this.vmRequestSubmissionInboundURI = vmRequestSubmissionInboundURI;
		this.VmRequestNotificationOutboundPortURI = vmRequestNotificationOutboundPortURI;
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
