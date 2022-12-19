/*******************************************************************************************************
 *
 * BU_Simplex1to4.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import javax.vecmath.Vector3f;

/**
 * BU_Simplex1to4 implements feature based and implicit simplex of up to 4 vertices
 * (tetrahedron, triangle, line, vertex).
 * 
 * @author jezek2
 */
public class BU_Simplex1to4 extends PolyhedralConvexShape {

	/** The num vertices. */
	protected int numVertices = 0;
	
	/** The vertices. */
	protected Vector3f[] vertices = new Vector3f[4];

	/**
	 * Instantiates a new b U simplex 1 to 4.
	 */
	public BU_Simplex1to4() {
	}

	/**
	 * Instantiates a new b U simplex 1 to 4.
	 *
	 * @param pt0 the pt 0
	 */
	public BU_Simplex1to4(Vector3f pt0) {
		addVertex(pt0);
	}

	/**
	 * Instantiates a new b U simplex 1 to 4.
	 *
	 * @param pt0 the pt 0
	 * @param pt1 the pt 1
	 */
	public BU_Simplex1to4(Vector3f pt0, Vector3f pt1) {
		addVertex(pt0);
		addVertex(pt1);
	}

	/**
	 * Instantiates a new b U simplex 1 to 4.
	 *
	 * @param pt0 the pt 0
	 * @param pt1 the pt 1
	 * @param pt2 the pt 2
	 */
	public BU_Simplex1to4(Vector3f pt0, Vector3f pt1, Vector3f pt2) {
		addVertex(pt0);
		addVertex(pt1);
		addVertex(pt2);
	}

	/**
	 * Instantiates a new b U simplex 1 to 4.
	 *
	 * @param pt0 the pt 0
	 * @param pt1 the pt 1
	 * @param pt2 the pt 2
	 * @param pt3 the pt 3
	 */
	public BU_Simplex1to4(Vector3f pt0, Vector3f pt1, Vector3f pt2, Vector3f pt3) {
		addVertex(pt0);
		addVertex(pt1);
		addVertex(pt2);
		addVertex(pt3);
	}
	
	/**
	 * Reset.
	 */
	public void reset() {
		numVertices = 0;
	}
	
	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.TETRAHEDRAL_SHAPE_PROXYTYPE;
	}
	
	/**
	 * Adds the vertex.
	 *
	 * @param pt the pt
	 */
	public void addVertex(Vector3f pt) {
		if (vertices[numVertices] == null) {
			vertices[numVertices] = new Vector3f();
		}
		
		vertices[numVertices++] = pt;

		recalcLocalAabb();
	}

	
	@Override
	public int getNumVertices() {
		return numVertices;
	}

	@Override
	public int getNumEdges() {
		// euler formula, F-E+V = 2, so E = F+V-2

		switch (numVertices) {
			case 0: return 0;
			case 1: return 0;
			case 2: return 1;
			case 3: return 3;
			case 4: return 6;
		}

		return 0;
	}

	@Override
	public void getEdge(int i, Vector3f pa, Vector3f pb) {
		switch (numVertices) {
			case 2:
				pa.set(vertices[0]);
				pb.set(vertices[1]);
				break;
			case 3:
				switch (i) {
					case 0:
						pa.set(vertices[0]);
						pb.set(vertices[1]);
						break;
					case 1:
						pa.set(vertices[1]);
						pb.set(vertices[2]);
						break;
					case 2:
						pa.set(vertices[2]);
						pb.set(vertices[0]);
						break;
				}
				break;
			case 4:
				switch (i) {
					case 0:
						pa.set(vertices[0]);
						pb.set(vertices[1]);
						break;
					case 1:
						pa.set(vertices[1]);
						pb.set(vertices[2]);
						break;
					case 2:
						pa.set(vertices[2]);
						pb.set(vertices[0]);
						break;
					case 3:
						pa.set(vertices[0]);
						pb.set(vertices[3]);
						break;
					case 4:
						pa.set(vertices[1]);
						pb.set(vertices[3]);
						break;
					case 5:
						pa.set(vertices[2]);
						pb.set(vertices[3]);
						break;
				}
		}
	}

	@Override
	public void getVertex(int i, Vector3f vtx) {
		vtx.set(vertices[i]);
	}

	@Override
	public int getNumPlanes() {
		switch (numVertices) {
			case 0: return 0;
			case 1: return 0;
			case 2: return 0;
			case 3: return 2;
			case 4: return 4;
		}
		return 0;
	}

	@Override
	public void getPlane(Vector3f planeNormal, Vector3f planeSupport, int i) {
	}
	
	/**
	 * Gets the index.
	 *
	 * @param i the i
	 * @return the index
	 */
	public int getIndex(int i) {
		return 0;
	}

	@Override
	public boolean isInside(Vector3f pt, float tolerance) {
		return false;
	}

	@Override
	public String getName() {
		return "BU_Simplex1to4";
	}

}
