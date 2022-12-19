/*******************************************************************************************************
 *
 * PrimitiveManagerBase.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.extras.gimpact.BoxCollision.AABB;

/**
 * Prototype Base class for primitive classification.<p>
 * 
 * This class is a wrapper for primitive collections.<p>
 * 
 * This tells relevant info for the Bounding Box set classes, which take care of space classification.<p>
 * 
 * This class can manage Compound shapes and trimeshes, and if it is managing trimesh then the
 * Hierarchy Bounding Box classes will take advantage of primitive Vs Box overlapping tests for
 * getting optimal results and less Per Box compairisons.
 * 
 * @author jezek2
 */
abstract class PrimitiveManagerBase {

	/**
	 * Determines if this manager consist on only triangles, which special case will be optimized.
	 */
	public abstract boolean is_trimesh();

	/**
	 * Gets the primitive count.
	 *
	 * @return the primitive count
	 */
	public abstract int get_primitive_count();

	/**
	 * Gets the primitive box.
	 *
	 * @param prim_index the prim index
	 * @param primbox the primbox
	 * @return the primitive box
	 */
	public abstract void get_primitive_box(int prim_index, AABB primbox);
	
	/**
	 * Retrieves only the points of the triangle, and the collision margin.
	 */
	public abstract void get_primitive_triangle(int prim_index, PrimitiveTriangle triangle);
	
}
