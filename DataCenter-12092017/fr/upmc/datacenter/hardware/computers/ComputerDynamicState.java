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

import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;

/**
 * The class <code>ComputerDynamicState</code> implements objects representing
 * a snapshot of the dynamic state of a computer component to be pulled or
 * pushed through the dynamic state data interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant		timestamp >= 0 && timestamperIP != null
 * invariant		computerURI != null && reservedCores != null
 * </pre>
 * 
 * <p>Created on : April 23, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				ComputerDynamicState
implements	ComputerDynamicStateI
{
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** timestamp in Unix time format, local time of the timestamper.		*/
	protected final long		timestamp ;
	/** IP of the node that did the timestamping.							*/
	protected final String		timestamperIP ;
	/** URI of the computer to which this dynamic state relates.			*/
	protected final String		computerURI ;
	/** reservation status of the cores of all computer's processors.		*/
	protected final boolean[][]	reservedCores ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a dynamic state object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete!
	 * 
	 * <pre>
	 * pre	computerURI != null
	 * pre	reservedCores != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI	URI of the computer to which this dynamic state relates.
	 * @param reservedCores	reservation status of the cores of all computer's processors.
	 * @throws Exception
	 */
	public				ComputerDynamicState(
		String computerURI,
		boolean[][] reservedCores
		) throws Exception
	{
		super() ;
		this.timestamp = TimeManagement.timeStamp() ;
		this.timestamperIP = InetAddress.getLocalHost().getHostAddress() ;
		this.computerURI = computerURI ;
		this.reservedCores =
					new boolean[reservedCores.length][reservedCores[0].length] ;
		for (int p = 0 ; p < reservedCores.length ; p++) {
			for (int c = 0 ; c < reservedCores[0].length ; c++) {
				this.reservedCores[p][c] = reservedCores[p][c] ;
			}
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getTimeStamp()
	 */
	@Override
	public long			getTimeStamp()
	{
		return this.timestamp ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getTimeStamperId()
	 */
	@Override
	public String		getTimeStamperId()
	{
		return new String(this.timestamperIP) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getComputerURI()
	 */
	@Override
	public String		getComputerURI()
	{
		return new String(this.computerURI) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getCurrentCoreReservations()
	 */
	@Override
	public boolean[][]	getCurrentCoreReservations()
	{
		// copy not to provide direct access to internal data structures.
		boolean[][] ret =
				new boolean[this.reservedCores.length][
				                                this.reservedCores[0].length] ;
		for (int i = 0 ; i < this.reservedCores.length ; i++) {
			for (int j = 0 ; j < this.reservedCores[i].length ; j++) {
				ret[i][j] = this.reservedCores[i][j] ;
			}
		}
		return ret ;
	}
}
