/*******************************************************************************************************
 *
 * TetrahedronShapeEx.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.collision.shapes.BU_Simplex1to4;
import javax.vecmath.Vector3f;

/**
 * Helper class for tetrahedrons.
 * 
 * @author jezek2
 */
class TetrahedronShapeEx extends BU_Simplex1to4 {

	/**
	 * Instantiates a new tetrahedron shape ex.
	 */
	public TetrahedronShapeEx() {
		numVertices = 4;
		for (int i = 0; i < numVertices; i++) {
			vertices[i] = new Vector3f();
		}
	}

	/**
	 * Sets the vertices.
	 *
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @param v2 the v 2
	 * @param v3 the v 3
	 */
	public void setVertices(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3) {
		vertices[0].set(v0);
		vertices[1].set(v1);
		vertices[2].set(v2);
		vertices[3].set(v3);
		recalcLocalAabb();
	}
	
}
