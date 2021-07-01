/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Stan Melax Convex Hull Computation
 * Copyright (c) 2008 Stan Melax http://www.melax.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.linearmath.convexhull;

/**
 *
 * @author jezek2
 */
class Tri extends Int3 {
	
	public Int3 n = new Int3();
	public int id;
	public int vmax;
	public float rise;

	public Tri(int a, int b, int c) {
		super(a, b, c);
		n.set(-1, -1, -1);
		vmax = -1;
		rise = 0f;
	}

	private static int er = -1;
	
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
