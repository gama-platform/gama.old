/*******************************************************************************************************
 *
 * FloatArray.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling.arrays;

import java.util.HashMap;

/**
 * Not thread safe float[] pooling.
 * @author Daniel
 */
public class FloatArray {
	
	/** The map. */
	private final HashMap<Integer, float[]> map = new HashMap<Integer, float[]>();
	
	/**
	 * Gets the.
	 *
	 * @param argLength the arg length
	 * @return the float[]
	 */
	public float[] get( int argLength){
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
	protected float[] getInitializedArray(int argLength){
		return new float[argLength];
	}
}
