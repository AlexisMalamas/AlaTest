package fr.upmc.datacenter;

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
 * The class <code>TimeManagement</code> manages the relationship
 * between the silulated time and the real time of the underlying
 * operating system.
 *
 * <p><strong>Description</strong></p>
 * 
 * It allow to perform simlations in accelerated time compared to
 * the real time.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : October 18, 2016</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				TimeManagement
{
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	protected static double		ACCELERATION_FACTOR = 1.0 ;
	protected static long		START_TIME ;

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	public static void	setACCELERATION_FACTOR(double accelerationFactor)
	{
		ACCELERATION_FACTOR = accelerationFactor ;
	}

	public static long	getSTART_TIME()
	{
		return START_TIME ;
	}

	public static void	setSTART_TIME(long startTime)
	{
		START_TIME = startTime ;
	}

	public static long	acceleratedDelay(long realDelay)
	{
		return (long)(realDelay/ACCELERATION_FACTOR) ;
	}

	public static long	realDelay(long acceleratedDelay)
	{
		return (long)(acceleratedDelay * ACCELERATION_FACTOR) ;
	}

	public static long	acceleratedTime(long realTime)
	{
		return START_TIME + (long)((realTime-START_TIME)/ACCELERATION_FACTOR) ;
	}

	public static long	currentTime()
	{
		return TimeManagement.realTime(System.currentTimeMillis()) ;
	}

	public static long	realTime(long acceleratedTime)
	{
		return START_TIME +
					(long)((acceleratedTime-START_TIME)*ACCELERATION_FACTOR) ;
	}

	public static long	timeStamp()
	{
		return TimeManagement.realTime(System.currentTimeMillis()) ;
	}
}
