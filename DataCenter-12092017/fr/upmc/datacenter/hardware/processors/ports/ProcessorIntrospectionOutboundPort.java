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

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorIntrospectionOutboundPort</code> defines
 * an outbound port associated with the interface
 * <code>ProcessorIntrospectionI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : January 29, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				ProcessorIntrospectionOutboundPort
extends		AbstractOutboundPort
implements	ProcessorIntrospectionI
{
	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				ProcessorIntrospectionOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ProcessorIntrospectionI.class, owner);
	}

	public				ProcessorIntrospectionOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ProcessorIntrospectionI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getNumberOfCores()
	 */
	@Override
	public int			getNumberOfCores() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getNumberOfCores() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDefaultFrequency()
	 */
	@Override
	public int			getDefaultFrequency() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getDefaultFrequency() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getMaxFrequencyGap()
	 */
	@Override
	public int			getMaxFrequencyGap() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getMaxFrequencyGap() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isValidCoreNo(int)
	 */
	@Override
	public boolean		isValidCoreNo(final int coreNo) throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).isValidCoreNo(coreNo) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isAdmissibleFrequency(int)
	 */
	@Override
	public boolean		isAdmissibleFrequency(final int frequency) throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).
											isAdmissibleFrequency(frequency) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isCurrentlyPossibleFrequencyForCore(int, int)
	 */
	@Override
	public boolean		isCurrentlyPossibleFrequencyForCore(
		final int coreNo,
		final int frequency
		) throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).
						isCurrentlyPossibleFrequencyForCore(coreNo, frequency) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getStaticState()
	 */
	@Override
	public ProcessorStaticStateI getStaticState() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getStaticState() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDynamicState()
	 */
	@Override
	public ProcessorDynamicStateI getDynamicState() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getDynamicState() ;
	}
}
