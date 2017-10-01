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

/**
 * The interface <code>ComputerStateDataConsumerI</code> defines the consumer
 * side methods used to receive state data pushed by a computer, both static
 * and dynamic.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface must be implemented by all classes representing components
 * that will consume as clients state data pushed by a computer.  They are
 * used by <code>ComputerStaticStateOutboundPort</code> and
 * <code>ComputerDynamicStateOutboundPort</code> to pass these data
 * upon reception from the processor component.
 * 
 * As a client component may receive data from several different computers,
 * it can assign URI to each at the creation of outbound ports, so that these
 * can pass these URI when receiving data.  Hence, the methods defined in this
 * interface will be unique in one client component but receive the data pushed
 * by all of the different computers.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : April 15, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface			ComputerStateDataConsumerI
{
	/**
	 * accept the static data pushed by a computer with the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	computerURI != null && staticState != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI	URI of the computer sending the data.
	 * @param staticState	static state of this computer.
	 * @throws Exception
	 */
	public void			acceptComputerStaticData(
		String				computerURI,
		ComputerStaticStateI	staticState
		) throws Exception ;

	/**
	 * accept the dynamic data pushed by a computer with the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	computerURI != null && currentDynamicState != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI			URI of the computer sending the data.
	 * @param currentDynamicState	current dynamic state of this computer.
	 * @throws Exception
	 */
	public void			acceptComputerDynamicData(
		String					computerURI,
		ComputerDynamicStateI	currentDynamicState
		) throws Exception ;
}
