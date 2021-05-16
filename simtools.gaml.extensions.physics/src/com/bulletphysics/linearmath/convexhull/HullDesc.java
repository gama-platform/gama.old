/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Stan Melax Convex Hull Computation
 * Copyright (c) 2008 Stan Melax http://www.melax.com/
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

package com.bulletphysics.linearmath.convexhull;

import com.bulletphysics.util.ObjectArrayList;
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
	public ObjectArrayList<Vector3f> vertices;
	
	/** Stride of each vertex, in bytes. */
	int vertexStride = 3*4;       
	
	/** Epsilon value for removing duplicates. This is a normalized value, if normalized bit is on. */
	public float normalEpsilon = 0.001f;
	
	/** Maximum number of vertices to be considered for the hull. */
	public int maxVertices = 4096;

	/** Maximum number of faces to be considered for the hull. */
	public int maxFaces = 4096;

	public HullDesc() {
	}

	public HullDesc(int flag, int vcount, ObjectArrayList<Vector3f> vertices) {
		this(flag, vcount, vertices, 3*4);
	}
	
	public HullDesc(int flag, int vcount, ObjectArrayList<Vector3f> vertices, int stride) {
		this.flags = flag;
		this.vcount = vcount;
		this.vertices = vertices;
		this.vertexStride = stride;
		this.normalEpsilon = 0.001f;
		this.maxVertices = 4096;
	}

	public boolean hasHullFlag(int flag) {
		if ((flags & flag) != 0) {
			return true;
		}
		return false;
	}

	public void setHullFlag(int flag) {
		flags |= flag;
	}

	public void clearHullFlag(int flag) {
		flags &= ~flag;
	}
	
}
