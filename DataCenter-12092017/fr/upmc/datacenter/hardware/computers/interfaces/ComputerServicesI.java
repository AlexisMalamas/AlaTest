package fr.upmc.datacenter.hardware.computers.interfaces;

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

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>ComputerServicesI</code> defines the services offered by
 * <code>Computer>/code> components (allocating cores).
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: add the deallocation of cores.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : April 9, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface			ComputerServicesI
extends		OfferedI,
			RequiredI
{
	/**
	 * allocate one core on this computer and return an instance of
	 * <code>AllocatedCore</code> containing the processor number,
	 * the core number and a map giving the URI of the processor
	 * inbound ports; return null if no core is available.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	an instance of <code>AllocatedCore</code> with the data about the allocated core.
	 * @throws Exception
	 */
	public AllocatedCore	allocateCore() throws Exception ;

	/**
	 * allocate up to <code>numberRequested</code> cores on this computer and
	 * return and array of <code>AllocatedCore</code> containing the data for
	 * each requested core; return an empty array if no core is available.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	numberRequested > 0
	 * post	return.length >= 0 && return.length <= numberRequested
	 * </pre>
	 *
	 * @param numberRequested	number of cores to be allocated.
	 * @return					an array of instances of <code>AllocatedCore</code> with the data about the allocated cores.
	 * @throws Exception
	 */
	public AllocatedCore[]	allocateCores(final int numberRequested)
	throws Exception ;
}
