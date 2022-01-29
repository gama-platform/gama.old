/*******************************************************************************************************
 *
 * Vec2Array.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling.arrays;

import java.util.HashMap;

import org.jbox2d.common.Vec2;

/**
 * not thread safe Vec2[] pool
 * @author dmurph
 *
 */
public class Vec2Array {

	/** The map. */
	private final HashMap<Integer, Vec2[]> map = new HashMap<Integer, Vec2[]>();
	
	/**
	 * Gets the.
	 *
	 * @param argLength the arg length
	 * @return the vec 2 []
	 */
	public Vec2[] get( int argLength){
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
	protected Vec2[] getInitializedArray(int argLength){
		final Vec2[] ray = new Vec2[argLength];
		for (int i = 0; i < ray.length; i++) {
			ray[i] = new Vec2();
		}
		return ray;
	}
}
