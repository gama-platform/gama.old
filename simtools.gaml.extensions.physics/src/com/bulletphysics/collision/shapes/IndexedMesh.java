/*******************************************************************************************************
 *
 * IndexedMesh.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import java.nio.ByteBuffer;

/**
 * IndexedMesh indexes into existing vertex and index arrays, in a similar way to
 * OpenGL's glDrawElements. Instead of the number of indices, we pass the number
 * of triangles.
 * 
 * @author jezek2
 */
public class IndexedMesh {
	
	/** The num triangles. */
	public int numTriangles;
	
	/** The triangle index base. */
	public ByteBuffer triangleIndexBase;
	
	/** The triangle index stride. */
	public int triangleIndexStride;
	
	/** The num vertices. */
	public int numVertices;
	
	/** The vertex base. */
	public ByteBuffer vertexBase;
	
	/** The vertex stride. */
	public int vertexStride;
	// The index type is set when adding an indexed mesh to the
	/** The index type. */
	// TriangleIndexVertexArray, do not set it manually
	public ScalarType indexType;

}
