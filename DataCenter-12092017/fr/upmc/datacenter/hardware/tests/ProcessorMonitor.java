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

import java.util.Map.Entry;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorMonitor</code> implements a monitor component
 * receiving data from processor components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : April 24, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				ProcessorMonitor
extends		AbstractComponent
implements	ProcessorStateDataConsumerI,
			ProcessorServicesNotificationConsumerI
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	protected boolean		active ;
	protected String			processorURI ;
	protected ProcessorStaticStateDataOutboundPort	pssPort ;
	protected ProcessorDynamicStateDataOutboundPort	pdsPort ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				ProcessorMonitor(
		String processorURI,
		boolean active,
		String processorServicesNotificationInboundPortURI,
		String processorStaticStateDataOutboundPortURI,
		String processorDynamicStateDataOutboundPortURI
		) throws Exception
	{
		super(1, 0) ;
		this.processorURI = processorURI ;
		this.active = active ;

		this.addOfferedInterface(ProcessorServicesNotificationI.class) ;
		ProcessorServicesNotificationInboundPort pnPort =
				new ProcessorServicesNotificationInboundPort(
						processorServicesNotificationInboundPortURI,
						this) ;
		this.addPort(pnPort) ;
		pnPort.publishPort() ;

		this.addOfferedInterface(DataRequiredI.PushI.class) ;
		this.addRequiredInterface(DataRequiredI.PullI.class) ;
		this.pssPort = new ProcessorStaticStateDataOutboundPort(
								processorStaticStateDataOutboundPortURI,
								this,
								processorURI) ;
		this.addPort(this.pssPort) ;
		this.pssPort.publishPort() ;
		
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.pdsPort = new ProcessorDynamicStateDataOutboundPort(
								processorDynamicStateDataOutboundPortURI,
								this,
								processorURI) ;
		this.addPort(this.pdsPort);
		this.pdsPort.publishPort() ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		super.shutdown();
	}

	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		// start the pushing of dynamic state information from the processor;
		// here only one push of information is planned after one second.
		try {
			this.pdsPort.startLimitedPushing(1000, 25) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	@Override
	public void			acceptProcessorStaticData(
		String processorURI,
		ProcessorStaticStateI ss
		) throws Exception
	{
		if (this.active) {
			StringBuffer sb = new StringBuffer() ;
			sb.append("Accepting static data from " + processorURI + "\n") ;
			sb.append("  timestamp              : " + ss.getTimeStamp() + "\n") ;
			sb.append("  timestamper id         : " + ss.getTimeStamperId() + "\n") ;
			sb.append("  number of cores        : " + ss.getNumberOfCores() + "\n") ;
			sb.append("  default frequency      : " + ss.getDefaultFrequency() + "\n") ;
			sb.append("  max. frequency gap     : " + ss.getMaxFrequencyGap() + "\n") ;
			sb.append("  admissible frequencies : [") ;
			int count = ss.getAdmissibleFrequencies().size() ;
			for (Integer f : ss.getAdmissibleFrequencies()) {
				sb.append(f) ;
				count-- ;
				if (count >0) {
					sb.append(", ") ;
				}
			}
			sb.append("]\n") ;
			sb.append( "  processing power       : [") ;
			count = ss.getProcessingPower().entrySet().size() ;
			for (Entry<Integer,Integer> e : ss.getProcessingPower().entrySet()) {
				sb.append("(" + e.getKey() + " => " + e.getValue() + ")") ;
				count-- ;
				if (count > 0) {
					sb.append(", ") ;
				}
			}
			sb.append("]\n") ;
			this.logMessage(sb.toString()) ;
		}
	}

	@Override
	public void			acceptProcessorDynamicData(
		String processorURI,
		ProcessorDynamicStateI cds
		) throws Exception
	{
		if (this.active) {
			StringBuffer sb = new StringBuffer() ;
			sb.append("Accepting dynamic data from " + processorURI + "\n") ;
			sb.append("  timestamp                : " + cds.getTimeStamp() + "\n") ;
			sb.append("  timestamper id           : " + cds.getTimeStamperId() + "\n") ;
			sb.append(  "  current idle status      : [") ;
			for (int i = 0 ; i < cds.getCoresIdleStatus().length ; i++) {
				sb.append(cds.getCoreIdleStatus(i)) ;
				if (i < cds.getCoresIdleStatus().length - 1) {
					sb.append(", ") ;
				}
			}
			sb.append("]\n") ;
			sb.append(  "  current core frequencies : [") ;
			for (int i = 0 ; i < cds.getCurrentCoreFrequencies().length ; i++) {
				sb.append(cds.getCurrentCoreFrequency(i)) ;
				if (i < cds.getCurrentCoreFrequencies().length - 1) {
					sb.append(", ") ;
				}
			}
			sb.append("]\n") ;
			this.logMessage(sb.toString()) ;
		}
	}		

	@Override
	public void			acceptNotifyEndOfTask(TaskI t) throws Exception
	{
		if (this.active) {
			this.logMessage(this.processorURI + " notifies end of task "+
														t.getTaskURI() + ".") ;
		}
	}

}
