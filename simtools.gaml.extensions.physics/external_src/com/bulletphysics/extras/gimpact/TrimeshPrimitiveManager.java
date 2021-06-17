/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371. email: projectileman@yahoo.com
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.TRIANGLES;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.collision.shapes.VertexData;
import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
class TrimeshPrimitiveManager extends PrimitiveManagerBase {

	public float margin;
	public StridingMeshInterface meshInterface;
	public final Vector3f scale = new Vector3f();
	public int part;
	public int lock_count;

	private final int[] tmpIndices = new int[3];

	private VertexData vertexData;

	public TrimeshPrimitiveManager() {
		meshInterface = null;
		part = 0;
		margin = 0.01f;
		scale.set(1f, 1f, 1f);
		lock_count = 0;
	}

	public TrimeshPrimitiveManager(final TrimeshPrimitiveManager manager) {
		meshInterface = manager.meshInterface;
		part = manager.part;
		margin = manager.margin;
		scale.set(manager.scale);
		lock_count = 0;
	}

	public TrimeshPrimitiveManager(final StridingMeshInterface meshInterface, final int part) {
		this.meshInterface = meshInterface;
		this.part = part;
		this.meshInterface.getScaling(scale);
		margin = 0.1f;
		lock_count = 0;
	}

	public void lock() {
		if (lock_count > 0) {
			lock_count++;
			return;
		}
		vertexData = meshInterface.getLockedReadOnlyVertexIndexBase(part);

		lock_count = 1;
	}

	public void unlock() {
		if (lock_count == 0) return;
		if (lock_count > 1) {
			--lock_count;
			return;
		}
		meshInterface.unLockReadOnlyVertexBase(part);
		vertexData = null;
		lock_count = 0;
	}

	@Override
	public boolean is_trimesh() {
		return true;
	}

	@Override
	public int get_primitive_count() {
		return vertexData.getIndexCount() / 3;
	}

	public int get_vertex_count() {
		return vertexData.getVertexCount();
	}

	public void get_indices(final int face_index, final int[] out) {
		out[0] = vertexData.getIndex(face_index * 3 + 0);
		out[1] = vertexData.getIndex(face_index * 3 + 1);
		out[2] = vertexData.getIndex(face_index * 3 + 2);
	}

	public void get_vertex(final int vertex_index, final Vector3f vertex) {
		vertexData.getVertex(vertex_index, vertex);
		VectorUtil.mul(vertex, vertex, scale);
	}

	@Override
	public void get_primitive_box(final int prim_index, final AABB primbox) {
		PrimitiveTriangle triangle = TRIANGLES.get();
		get_primitive_triangle(prim_index, triangle);
		primbox.calc_from_triangle_margin(triangle.vertices[0], triangle.vertices[1], triangle.vertices[2],
				triangle.margin);
		TRIANGLES.release(triangle);
	}

	@Override
	public void get_primitive_triangle(final int prim_index, final PrimitiveTriangle triangle) {
		get_indices(prim_index, tmpIndices);
		get_vertex(tmpIndices[0], triangle.vertices[0]);
		get_vertex(tmpIndices[1], triangle.vertices[1]);
		get_vertex(tmpIndices[2], triangle.vertices[2]);
		triangle.margin = margin;
	}

	public void get_bullet_triangle(final int prim_index, final TriangleShapeEx triangle) {
		get_indices(prim_index, tmpIndices);
		get_vertex(tmpIndices[0], triangle.vertices1[0]);
		get_vertex(tmpIndices[1], triangle.vertices1[1]);
		get_vertex(tmpIndices[2], triangle.vertices1[2]);
		triangle.setMargin(margin);
	}

}
