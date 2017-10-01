package fr.upmc.datacenter.hardware.processors.interfaces;

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

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;

/**
 * The interface <code>ProcessorDynamicStateI</code> gives access to the
 * dynamic state information of processors transmitted by data interfaces of
 * processors.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface is used to type objects pulled from or pushed by a processor
 * using a data interface in pull or push mode.  It gives access to dynamic
 * information, that is information subject to changes during the existence of
 * the processor.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : April 7, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface			ProcessorDynamicStateI
extends 		DataOfferedI.DataI,
			DataRequiredI.DataI,
			TimeStampingI
{
	/**
	 * return a boolean array of cores' idle status i.e., for each core
	 * <code>i</code> the result contains at index <code>i</code>
	 * <code>true</code> if the core <code>i</code> is idle and
	 * <code>false</code> otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	array of cores' idle status.
	 */
	public	boolean[]	getCoresIdleStatus() ;

	/**
	 * return <code>true</code> if the core <code>codeNo</code> is idle and
	 * <code>false</code> otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo	number of the core tested.
	 * @return			true if the core is idle, false otherwise.
	 */
	public	boolean		getCoreIdleStatus(int coreNo) ;

	/**
	 * return an array of the current frequencies of the cores where the
	 * frequency of core i is at position i.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	array of the current frequencies of the cores.
	 */
	public int[]	getCurrentCoreFrequencies() ;

	/**
	 * return the current frequency of the given core.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo	number of the core to be inquired.
	 * @return			current frequency of the given core.
	 */
	public int		getCurrentCoreFrequency(int coreNo) ;
}
