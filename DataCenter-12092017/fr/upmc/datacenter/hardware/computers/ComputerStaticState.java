package fr.upmc.datacenter.hardware.computers;

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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;

/**
 * The class <code>ComputerStaticState</code> implements objects representing
 * a snapshot of the static state of a computer component to be pulled or
 * pushed through the static state data interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TDOO: complete!
 * 
 * <pre>
 * invariant		timestamp >= 0 && timestamperIP != null
 * invariant		computerURI != null
 * invariant		numberOfProcessors > 0
 * invariant		numberOfCoresPerProcessor > 0
 * invariant		processorURIs != null && processorURIs.size() == numberOfProcessors
 * invariant		processorsPortURIs != null
 * invariant		processorsPortURIs.keySet().size() == numberOfProcessors
 * </pre>
 * 
 * <p>Created on : April 14, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				ComputerStaticState
implements	ComputerStaticStateI
{
	// ------------------------------------------------------------------------
	// Instance variables and constants
	// ------------------------------------------------------------------------

	private static final long 				serialVersionUID = 1L ;
	/** timestamp in Unix time format, local time of the timestamper.		*/
	protected final long					timestamp ;
	/** IP of the node that did the timestamping.							*/
	protected final String					timestamperIP ;
	/** URI of the computer to which this static state relates.				*/
	protected final String					computerURI ;
	/** number of processors owned by the computer.							*/
	protected final int						numberOfProcessors ;
	/** number of cores per processors owned by the computer.				*/
	protected final int						numberOfCoresPerProcessor ;
	/** map between processor numbers and their URI.						*/
	protected final Map<Integer,String>		processorURIs ;
	/** map between processor URI and their different ports URI.			*/
	protected final Map<String, Map<ProcessorPortTypes, String>>
											processorsPortURIs ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a static state snapshot.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	computerURI != null
	 * pre	numberOfProcessors > 0
	 * pre	numberOfCoresPerProcessor > 0
	 * pre	processorURIs != null && processorURIs.size() == numberOfProcessors
	 * pre	processorsPortURIs != null
	 * pre	processorsPortURIs.keySet().size() == numberOfProcessors
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI				URI of the computer to which this static state relates.
	 * @param numberOfProcessors		number of processors owned by the computer.
	 * @param numberOfCoresPerProcessor	number of cores per processors owned by the computer.
	 * @param processorURIs				map between processor numbers and their URI.
	 * @param processorsPortURIs		map between processor URI and their different ports URI.
	 * @throws Exception
	 */
	public				ComputerStaticState(
		String computerURI,
		int numberOfProcessors,
		int numberOfCoresPerProcessor,
		Map<Integer, String> processorURIs,
		Map<String, Map<ProcessorPortTypes, String>> processorsPortURIs
		) throws Exception
	{
		super() ;

		// Preconditions
		assert	computerURI != null ;
		assert	numberOfProcessors > 0 ;
		assert	numberOfCoresPerProcessor > 0 ;
		assert	processorURIs != null && processorURIs.size() == numberOfProcessors ;
		assert	processorsPortURIs != null ;
		assert	processorsPortURIs.keySet().size() == numberOfProcessors ;

		this.timestamp = TimeManagement.timeStamp() ;
		this.timestamperIP = InetAddress.getLocalHost().getHostAddress() ;
		this.computerURI = computerURI;
		this.numberOfProcessors = numberOfProcessors;
		this.numberOfCoresPerProcessor = numberOfCoresPerProcessor;
		this.processorURIs = processorURIs;
		this.processorsPortURIs = processorsPortURIs;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI#getTimeStamp()
	 */
	@Override
	public long			getTimeStamp()
	{
		return this.timestamp ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI#getTimeStamperId()
	 */
	@Override
	public String		getTimeStamperId()
	{
		return new String(this.timestamperIP) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI#getComputerURI()
	 */
	@Override
	public String		getComputerURI()
	{
		return new String(this.computerURI) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI#getNumberOfProcessors()
	 */
	@Override
	public int			getNumberOfProcessors()
	{
		return this.numberOfProcessors ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI#getNumberOfCoresPerProcessor()
	 */
	@Override
	public int			getNumberOfCoresPerProcessor()
	{
		return this.numberOfCoresPerProcessor ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI#getProcessorURIs()
	 */
	@Override
	public	Map<Integer, String>	getProcessorURIs()
	{
		// copy not to provide direct access to internal data structures.
		Map<Integer,String> ret =
					new HashMap<Integer,String>(this.processorURIs.size()) ;
		for(int n : this.processorURIs.keySet()) {
			ret.put(n, this.processorURIs.get(n)) ;
		}
		return ret ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI#getProcessorPortMap()
	 */
	@Override
	public Map<String, Map<ProcessorPortTypes, String>>	getProcessorPortMap()
	{
		// copy not to provide direct access to internal data structures.
		Map<String, Map<ProcessorPortTypes, String>> ret =
			new HashMap<String, Map<ProcessorPortTypes, String>>(
											this.processorsPortURIs.size()) ;
		for(String processorURI : this.processorsPortURIs.keySet()) {
			Map<ProcessorPortTypes, String> portURIs =
									this.processorsPortURIs.get(processorURI) ;
			Map<ProcessorPortTypes, String> ps =
								new HashMap<ProcessorPortTypes, String>() ;
			for(ProcessorPortTypes ppt : portURIs.keySet()) {
				ps.put(ppt, portURIs.get(ppt)) ;
			}
			ret.put(processorURI, ps) ;
		}
		return ret ;
	}
}
