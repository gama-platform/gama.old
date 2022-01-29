/*******************************************************************************************************
 *
 * Filter.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics;

// updated to rev 100
/**
 * This holds contact filtering data.
 * 
 * @author daniel
 */
public class Filter {
	/**
	 * The collision category bits. Normally you would just set one bit.
	 */
	public int categoryBits;
	
	/**
	 * The collision mask bits. This states the categories that this
	 * shape would accept for collision.
	 */
	public int maskBits;
	
	/**
	 * Collision groups allow a certain group of objects to never collide (negative)
	 * or always collide (positive). Zero means no collision group. Non-zero group
	 * filtering always wins against the mask bits.
	 */
	public int groupIndex;
	
	/**
	 * Instantiates a new filter.
	 */
	public Filter() {
	  categoryBits = 0x0001;
      maskBits = 0xFFFF;
      groupIndex = 0;
    }
	
	/**
	 * Sets the.
	 *
	 * @param argOther the arg other
	 */
	public void set(Filter argOther) {
		categoryBits = argOther.categoryBits;
		maskBits = argOther.maskBits;
		groupIndex = argOther.groupIndex;
	}
}
