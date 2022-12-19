/*******************************************************************************************************
 *
 * TrimeshPrimitiveManager.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The margin. */
	public float margin;
	
	/** The mesh interface. */
	public StridingMeshInterface meshInterface;
	
	/** The scale. */
	public final Vector3f scale = new Vector3f();
	
	/** The part. */
	public int part;
	
	/** The lock count. */
	public int lock_count;

	/** The tmp indices. */
	private final int[] tmpIndices = new int[3];

	/** The vertex data. */
	private VertexData vertexData;

	/**
	 * Instantiates a new trimesh primitive manager.
	 */
	public TrimeshPrimitiveManager() {
		meshInterface = null;
		part = 0;
		margin = 0.01f;
		scale.set(1f, 1f, 1f);
		lock_count = 0;
	}

	/**
	 * Instantiates a new trimesh primitive manager.
	 *
	 * @param manager the manager
	 */
	public TrimeshPrimitiveManager(final TrimeshPrimitiveManager manager) {
		meshInterface = manager.meshInterface;
		part = manager.part;
		margin = manager.margin;
		scale.set(manager.scale);
		lock_count = 0;
	}

	/**
	 * Instantiates a new trimesh primitive manager.
	 *
	 * @param meshInterface the mesh interface
	 * @param part the part
	 */
	public TrimeshPrimitiveManager(final StridingMeshInterface meshInterface, final int part) {
		this.meshInterface = meshInterface;
		this.part = part;
		this.meshInterface.getScaling(scale);
		margin = 0.1f;
		lock_count = 0;
	}

	/**
	 * Lock.
	 */
	public void lock() {
		if (lock_count > 0) {
			lock_count++;
			return;
		}
		vertexData = meshInterface.getLockedReadOnlyVertexIndexBase(part);

		lock_count = 1;
	}

	/**
	 * Unlock.
	 */
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

	/**
	 * Gets the vertex count.
	 *
	 * @return the vertex count
	 */
	public int get_vertex_count() {
		return vertexData.getVertexCount();
	}

	/**
	 * Gets the indices.
	 *
	 * @param face_index the face index
	 * @param out the out
	 * @return the indices
	 */
	public void get_indices(final int face_index, final int[] out) {
		out[0] = vertexData.getIndex(face_index * 3 + 0);
		out[1] = vertexData.getIndex(face_index * 3 + 1);
		out[2] = vertexData.getIndex(face_index * 3 + 2);
	}

	/**
	 * Gets the vertex.
	 *
	 * @param vertex_index the vertex index
	 * @param vertex the vertex
	 * @return the vertex
	 */
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

	/**
	 * Gets the bullet triangle.
	 *
	 * @param prim_index the prim index
	 * @param triangle the triangle
	 * @return the bullet triangle
	 */
	public void get_bullet_triangle(final int prim_index, final TriangleShapeEx triangle) {
		get_indices(prim_index, tmpIndices);
		get_vertex(tmpIndices[0], triangle.vertices1[0]);
		get_vertex(tmpIndices[1], triangle.vertices1[1]);
		get_vertex(tmpIndices[2], triangle.vertices1[2]);
		triangle.setMargin(margin);
	}

}
