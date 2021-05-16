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

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.TriangleCallback;
import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
class GImpactTriangleCallback extends TriangleCallback {

	public GImpactCollisionAlgorithm algorithm;
	public CollisionObject body0;
	public CollisionObject body1;
	public GImpactShapeInterface gimpactshape0;
	public boolean swapped;
	public float margin;
	
	public void processTriangle(Vector3f[] triangle, int partId, int triangleIndex) {
		TriangleShapeEx tri1 = new TriangleShapeEx(triangle[0], triangle[1], triangle[2]);
		tri1.setMargin(margin);
		if (swapped) {
			algorithm.setPart0(partId);
			algorithm.setFace0(triangleIndex);
		}
		else {
			algorithm.setPart1(partId);
			algorithm.setFace1(triangleIndex);
		}
		algorithm.gimpact_vs_shape(body0, body1, gimpactshape0, tri1, swapped);
	}

}
