/*******************************************************************************************************
 *
 * Int4.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath.convexhull;

/**
 *
 * @author jezek2
 */
class Int4 {

	/** The w. */
	public int x, y, z, w;

	/**
	 * Instantiates a new int 4.
	 */
	public Int4() {
	}

	/**
	 * Instantiates a new int 4.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 */
	public Int4(int x, int y, int z, int w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Sets the.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param w the w
	 */
	public void set(int x, int y, int z, int w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	/**
	 * Gets the coord.
	 *
	 * @param coord the coord
	 * @return the coord
	 */
	public int getCoord(int coord) {
		switch (coord) {
			case 0: return x;
			case 1: return y;
			case 2: return z;
			default: return w;
		}
	}

}
