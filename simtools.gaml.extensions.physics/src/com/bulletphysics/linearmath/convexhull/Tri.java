/*******************************************************************************************************
 *
 * Tri.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
class Tri extends Int3 {
	
	/** The n. */
	public Int3 n = new Int3();
	
	/** The id. */
	public int id;
	
	/** The vmax. */
	public int vmax;
	
	/** The rise. */
	public float rise;

	/**
	 * Instantiates a new tri.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 */
	public Tri(int a, int b, int c) {
		super(a, b, c);
		n.set(-1, -1, -1);
		vmax = -1;
		rise = 0f;
	}

	/** The er. */
	private static int er = -1;
	
	/** The er ref. */
	private static IntRef erRef = new IntRef() {
		@Override
		public int get() {
			return er;
		}

		@Override
		public void set(int value) {
			er = value;
		}
	};
	
	/**
	 * Neib.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the int ref
	 */
	public IntRef neib(int a, int b) {
		for (int i = 0; i < 3; i++) {
			int i1 = (i + 1) % 3;
			int i2 = (i + 2) % 3;
			
			if (getCoord(i) == a && getCoord(i1) == b) {
				return n.getRef(i2);
			}
			if (getCoord(i) == b && getCoord(i1) == a) {
				return n.getRef(i2);
			}
		}
		assert (false);
		return erRef;
	}

}
