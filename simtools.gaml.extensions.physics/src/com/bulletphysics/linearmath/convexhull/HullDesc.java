/*******************************************************************************************************
 *
 * HullDesc.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath.convexhull;

import java.util.ArrayList;
import javax.vecmath.Vector3f;

/**
 * Describes point cloud data and other input for conversion to polygonal representation.
 * 
 * @author jezek2
 */
public class HullDesc {

	/** Flags to use when generating the convex hull, see {@link HullFlags}. */
	public int flags = HullFlags.DEFAULT;
	
	/** Number of vertices in the input point cloud. */
	public int vcount = 0;
	
	/** Array of vertices. */
	public ArrayList<Vector3f> vertices;
	
	/** Stride of each vertex, in bytes. */
	int vertexStride = 3*4;       
	
	/** Epsilon value for removing duplicates. This is a normalized value, if normalized bit is on. */
	public float normalEpsilon = 0.001f;
	
	/** Maximum number of vertices to be considered for the hull. */
	public int maxVertices = 4096;

	/** Maximum number of faces to be considered for the hull. */
	public int maxFaces = 4096;

	/**
	 * Instantiates a new hull desc.
	 */
	public HullDesc() {
	}

	/**
	 * Instantiates a new hull desc.
	 *
	 * @param flag the flag
	 * @param vcount number of vertices in the input point cloud.
	 * @param vertices array of vertices.
	 */
	public HullDesc(int flag, int vcount, ArrayList<Vector3f> vertices) {
		this(flag, vcount, vertices, 3*4);
	}
	
	/**
	 * Instantiates a new hull desc.
	 *
	 * @param flag the flag
	 * @param vcount number of vertices in the input point cloud.
	 * @param vertices array of vertices.
	 * @param stride the stride
	 */
	public HullDesc(int flag, int vcount, ArrayList<Vector3f> vertices, int stride) {
		this.flags = flag;
		this.vcount = vcount;
		this.vertices = vertices;
		this.vertexStride = stride;
		this.normalEpsilon = 0.001f;
		this.maxVertices = 4096;
	}

	/**
	 * Checks for hull flag.
	 *
	 * @param flag the flag
	 * @return true, if successful
	 */
	public boolean hasHullFlag(int flag) {
		if ((flags & flag) != 0) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the hull flag.
	 *
	 * @param flag the new hull flag
	 */
	public void setHullFlag(int flag) {
		flags |= flag;
	}

	/**
	 * Clear hull flag.
	 *
	 * @param flag the flag
	 */
	public void clearHullFlag(int flag) {
		flags &= ~flag;
	}
	
}
