package fr.upmc.datacenterclient.utils;

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

import java.util.Calendar;
import java.util.TimeZone;

/**
 * The class <code>TimeProcessing</code> implements utilities for processing
 * time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : May 5, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			TimeProcessing
{
	/**
	 * transform a time in milliseconds into a the more readable format
	 * HH:MM:SS:MS.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	millis >= 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param millis	time in milliseconds from January 1970 to be formatted.
	 * @return	 		a string in the format HH:MM:SS:MS.
	 */
	public static String	toString(long millis)
	{
		assert	millis >= 0 ;

		Calendar cal = (Calendar.getInstance()) ;
		cal.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		cal.setTimeInMillis(millis) ;

		return 	"" + (((cal.get(Calendar.AM_PM) == Calendar.AM) ? 0 : 12) +
													cal.get(Calendar.HOUR)) +
				":" + cal.get(Calendar.MINUTE) +
				":" + cal.get(Calendar.SECOND) +
				":" + cal.get(Calendar.MILLISECOND) ;
	}

}
