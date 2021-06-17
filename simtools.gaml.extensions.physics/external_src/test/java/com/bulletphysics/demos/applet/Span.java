/*
 * Software OpenGL-like 3D renderer (c) 2008 Martin Dvorak <jezek2@advel.cz>
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

package com.bulletphysics.demos.applet;

/**
 *
 * @author jezek2
 */
class Span {

	public int x1, x2;
	public float z1, z2;
	public short c1r, c1g, c1b;
	public short c2r, c2g, c2b;
	
	public Span prev, next;
	
	public void set(Span s) {
		x1 = s.x1;
		x2 = s.x2;
		z1 = s.z1;
		z2 = s.z2;
		c1r = s.c1r;
		c1g = s.c1g;
		c1b = s.c1b;
		c2r = s.c2r;
		c2g = s.c2g;
		c2b = s.c2b;
	}
	
}
