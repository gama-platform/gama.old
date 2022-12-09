/*******************************************************************************************************
 *
 * ConeShapeX.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

/**
 * ConeShape implements a cone shape, around the X axis.
 * 
 * @author jezek2
 */
public class ConeShapeX extends ConeShape {

	/**
	 * Instantiates a new cone shape X.
	 *
	 * @param radius the radius
	 * @param height the height
	 */
	public ConeShapeX(float radius, float height) {
		super(radius, height);
		setConeUpIndex(0);
	}

}
