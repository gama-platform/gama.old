/*******************************************************************************************************
 *
 * Color3f.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/ 
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.jbox2d.common;

// updated to rev 100
/**
 * Similar to javax.vecmath.Color3f holder
 * @author ewjordan
 *
 */
public class Color3f {
	
	/** The Constant WHITE. */
	public static final Color3f WHITE = new Color3f(1, 1, 1);
	
	/** The Constant BLACK. */
	public static final Color3f BLACK = new Color3f(0, 0, 0);
	
	/** The Constant BLUE. */
	public static final Color3f BLUE = new Color3f(0, 0, 1);
	
	/** The Constant GREEN. */
	public static final Color3f GREEN = new Color3f(0, 1, 0);
	
	/** The Constant RED. */
	public static final Color3f RED = new Color3f(1, 0, 0);
	
	/** The x. */
	public float x;
	
	/** The y. */
	public float y;
	
	/** The z. */
	public float z;

	
	/**
	 * Instantiates a new color 3 f.
	 */
	public Color3f(){
		x = y = z = 0;
	}
	
	/**
	 * Instantiates a new color 3 f.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public Color3f(float r, float g, float b) {
		x = r;
		y = g;
		z = b;
	}
	
	/**
	 * Sets the.
	 *
	 * @param r the r
	 * @param g the g
	 * @param b the b
	 */
	public void set(float r, float g, float b){
		x = r;
		y = g;
		z = b;
	}
	
	/**
	 * Sets the.
	 *
	 * @param argColor the arg color
	 */
	public void set(Color3f argColor){
		x = argColor.x;
		y = argColor.y;
		z = argColor.z;
	}
}
