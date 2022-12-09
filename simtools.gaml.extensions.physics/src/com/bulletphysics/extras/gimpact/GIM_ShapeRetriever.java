/*******************************************************************************************************
 *
 * GIM_ShapeRetriever.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.collision.shapes.CollisionShape;

/**
 *
 * @author jezek2
 */
class GIM_ShapeRetriever {

	/** The gim shape. */
	public GImpactShapeInterface gim_shape;
	
	/** The trishape. */
	public TriangleShapeEx trishape = new TriangleShapeEx();
	
	/** The tetrashape. */
	public TetrahedronShapeEx tetrashape = new TetrahedronShapeEx();

	/** The child retriever. */
	public ChildShapeRetriever child_retriever = new ChildShapeRetriever();
	
	/** The tri retriever. */
	public TriangleShapeRetriever tri_retriever = new TriangleShapeRetriever();
	
	/** The tetra retriever. */
	public TetraShapeRetriever tetra_retriever = new TetraShapeRetriever();
	
	/** The current retriever. */
	public ChildShapeRetriever current_retriever;

	/**
	 * Instantiates a new GI M shape retriever.
	 *
	 * @param gim_shape the gim shape
	 */
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

	/**
	 * Gets the child shape.
	 *
	 * @param index the index
	 * @return the child shape
	 */
	public CollisionShape getChildShape(int index) {
		return current_retriever.getChildShape(index);
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The Class ChildShapeRetriever.
	 */
	public static class ChildShapeRetriever {
		
		/** The parent. */
		public GIM_ShapeRetriever parent;

		/**
		 * Gets the child shape.
		 *
		 * @param index the index
		 * @return the child shape
		 */
		public CollisionShape getChildShape(int index) {
			return parent.gim_shape.getChildShape(index);
		}
	}

	/**
	 * The Class TriangleShapeRetriever.
	 */
	public static class TriangleShapeRetriever extends ChildShapeRetriever {
		@Override
		public CollisionShape getChildShape(int index) {
			parent.gim_shape.getBulletTriangle(index, parent.trishape);
			return parent.trishape;
		}
	}

	/**
	 * The Class TetraShapeRetriever.
	 */
	public static class TetraShapeRetriever extends ChildShapeRetriever {
		@Override
		public CollisionShape getChildShape(int index) {
			parent.gim_shape.getBulletTetrahedron(index, parent.tetrashape);
			return parent.tetrashape;
		}
	}
	
}
