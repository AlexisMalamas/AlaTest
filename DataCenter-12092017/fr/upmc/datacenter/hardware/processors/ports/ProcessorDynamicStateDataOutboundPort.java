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
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI ;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;

/**
 * The class <code>ProcessorDynamicStateDataOutboundPort</code> is the
 * client-side port to exchange dynamic state data with a processor component.
 *
 * <p><strong>Description</strong></p>
 * 
 * Outbound port used to pull or push dynamic state data from a processor or to
 * a processor client.  These ports must be connected by a
 * <code>ProcessorDynamicStateDataConnector</code>.
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
public class				ProcessorDynamicStateDataOutboundPort
extends		AbstractControlledDataOutboundPort
{
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	protected String			processorURI ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				ProcessorDynamicStateDataOutboundPort(
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(owner) ;
		this.processorURI = processorURI ;

		assert owner instanceof ProcessorStateDataConsumerI ;
	}

	public				ProcessorDynamicStateDataOutboundPort(
		String uri,
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(uri, owner) ;
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
	public void			receive(final DataRequiredI.DataI d) throws Exception
	{
		final ProcessorStateDataConsumerI psdc =
									(ProcessorStateDataConsumerI) this.owner ;
		final String uri = this.processorURI ;
		this.owner.handleRequestAsync(
						new ComponentI.ComponentService<Void>() {
							@Override
							public Void call() throws Exception {
								psdc.acceptProcessorDynamicData(
											uri, ((ProcessorDynamicStateI)d)) ;
								return null;
							}
						}) ;
	}
}
