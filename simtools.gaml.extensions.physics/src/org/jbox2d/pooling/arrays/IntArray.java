/*******************************************************************************************************
 *
 * IntArray.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 4:14:34 AM Jul 17, 2010
 */
package org.jbox2d.pooling.arrays;

import java.util.HashMap;

/**
 * Not thread safe int[] pooling
 * @author Daniel Murphy
 */
public class IntArray {
	
	/** The map. */
	private final HashMap<Integer, int[]> map = new HashMap<Integer, int[]>();
	
	/**
	 * Gets the.
	 *
	 * @param argLength the arg length
	 * @return the int[]
	 */
	public int[] get( int argLength){
		assert(argLength > 0);
		
		if(!map.containsKey(argLength)){
			map.put(argLength, getInitializedArray(argLength));
		}
		
		assert(map.get(argLength).length == argLength) : "Array not built of correct length";
		return map.get(argLength);
	}
	
	/**
	 * Gets the initialized array.
	 *
	 * @param argLength the arg length
	 * @return the initialized array
	 */
	protected int[] getInitializedArray(int argLength){
		return new int[argLength];
	}
}
