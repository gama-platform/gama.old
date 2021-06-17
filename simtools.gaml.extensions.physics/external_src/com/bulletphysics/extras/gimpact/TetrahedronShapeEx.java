/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371.
 * email: projectileman@yahoo.com
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

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.collision.shapes.BU_Simplex1to4;
import javax.vecmath.Vector3f;

/**
 * Helper class for tetrahedrons.
 * 
 * @author jezek2
 */
class TetrahedronShapeEx extends BU_Simplex1to4 {

	public TetrahedronShapeEx() {
		numVertices = 4;
		for (int i = 0; i < numVertices; i++) {
			vertices[i] = new Vector3f();
		}
	}

	public void setVertices(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3) {
		vertices[0].set(v0);
		vertices[1].set(v1);
		vertices[2].set(v2);
		vertices[3].set(v3);
		recalcLocalAabb();
	}
	
}
