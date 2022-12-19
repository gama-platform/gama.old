/*******************************************************************************************************
 *
 * Int3.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
class Int3 {

	/** The z. */
	public int x, y, z;

	/**
	 * Instantiates a new int 3.
	 */
	public Int3() {
	}

	/**
	 * Instantiates a new int 3.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public Int3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Instantiates a new int 3.
	 *
	 * @param i the i
	 */
	public Int3(Int3 i) {
		x = i.x;
		y = i.y;
		z = i.z;
	}
	
	/**
	 * Sets the.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets the.
	 *
	 * @param i the i
	 */
	public void set(Int3 i) {
		x = i.x;
		y = i.y;
		z = i.z;
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
			default: return z;
		}
	}

	/**
	 * Sets the coord.
	 *
	 * @param coord the coord
	 * @param value the value
	 */
	public void setCoord(int coord, int value) {
		switch (coord) {
			case 0: x = value; break;
			case 1: y = value; break;
			case 2: z = value; break;
		}
	}
	
	/**
	 * Equals.
	 *
	 * @param i the i
	 * @return true, if successful
	 */
	public boolean equals(Int3 i) {
		return (x == i.x && y == i.y && z == i.z);
	}
	
	/**
	 * Gets the ref.
	 *
	 * @param coord the coord
	 * @return the ref
	 */
	public IntRef getRef(final int coord) {
		return new IntRef() {
			@Override
			public int get() {
				return getCoord(coord);
			}

			@Override
			public void set(int value) {
				setCoord(coord, value);
			}
		};
	}

}
