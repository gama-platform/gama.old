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

import com.bulletphysics.collision.shapes.CollisionShape;

/**
 *
 * @author jezek2
 */
class GIM_ShapeRetriever {

	public GImpactShapeInterface gim_shape;
	public TriangleShapeEx trishape = new TriangleShapeEx();
	public TetrahedronShapeEx tetrashape = new TetrahedronShapeEx();

	public ChildShapeRetriever child_retriever = new ChildShapeRetriever();
	public TriangleShapeRetriever tri_retriever = new TriangleShapeRetriever();
	public TetraShapeRetriever tetra_retriever = new TetraShapeRetriever();
	public ChildShapeRetriever current_retriever;

	public GIM_ShapeRetriever(GImpactShapeInterface gim_shape) {
		this.gim_shape = gim_shape;
		
		// select retriever
		if (gim_shape.needsRetrieveTriangles()) {
			current_retriever = tri_retriever;
		}
		else if (gim_shape.needsRetrieveTetrahedrons()) {
			current_retriever = tetra_retriever;
		}
		else {
			current_retriever = child_retriever;
		}

		current_retriever.parent = this;
	}

	public CollisionShape getChildShape(int index) {
		return current_retriever.getChildShape(index);
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static class ChildShapeRetriever {
		public GIM_ShapeRetriever parent;

		public CollisionShape getChildShape(int index) {
			return parent.gim_shape.getChildShape(index);
		}
	}

	public static class TriangleShapeRetriever extends ChildShapeRetriever {
		@Override
		public CollisionShape getChildShape(int index) {
			parent.gim_shape.getBulletTriangle(index, parent.trishape);
			return parent.trishape;
		}
	}

	public static class TetraShapeRetriever extends ChildShapeRetriever {
		@Override
		public CollisionShape getChildShape(int index) {
			parent.gim_shape.getBulletTetrahedron(index, parent.tetrashape);
			return parent.tetrashape;
		}
	}
	
}
