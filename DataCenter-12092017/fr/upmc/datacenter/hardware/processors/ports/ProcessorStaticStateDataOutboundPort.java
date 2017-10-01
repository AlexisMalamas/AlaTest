package fr.upmc.datacenter.hardware.processors.ports;

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

import fr.upmc.components.ComponentI ;
import fr.upmc.components.interfaces.DataRequiredI ;
import fr.upmc.components.ports.AbstractDataOutboundPort ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateDataI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorStaticStateDataOutboundPort</code> is the
 * client-side port to exchange static state data with a processor component.
 *
 * <p><strong>Description</strong></p>
 * 
 * Outbound port used to pull or push static state data from a processor or to
 * a processor client.  These ports can be connected by a simple
 * <code>DataConnector</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : April 8, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				ProcessorStaticStateDataOutboundPort
extends		AbstractDataOutboundPort
implements	ProcessorStaticStateDataI
{
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	protected final String		processorURI ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				ProcessorStaticStateDataOutboundPort(
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.processorURI = processorURI ;

		assert owner instanceof ProcessorStateDataConsumerI ;
	}

	public				ProcessorStaticStateDataOutboundPort(
		String uri,
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(uri, DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.processorURI = processorURI ;

		assert owner instanceof ProcessorStateDataConsumerI ;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void			receive(DataRequiredI.DataI d) throws Exception
	{
		((ProcessorStateDataConsumerI)this.owner).
					acceptProcessorStaticData(this.processorURI,
											  ((ProcessorStaticStateI)d)) ;
	}
}
