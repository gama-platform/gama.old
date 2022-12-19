/*******************************************************************************************************
 *
 * TriangleIndexVertexArray.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import java.util.ArrayList;
import java.nio.ByteBuffer;

/**
 * TriangleIndexVertexArray allows to use multiple meshes, by indexing into existing
 * triangle/index arrays. Additional meshes can be added using {@link #addIndexedMesh addIndexedMesh}.<p>
 * 
 * No duplicate is made of the vertex/index data, it only indexes into external vertex/index
 * arrays. So keep those arrays around during the lifetime of this TriangleIndexVertexArray.
 * 
 * @author jezek2
 */
public class TriangleIndexVertexArray extends StridingMeshInterface {

	/** The indexed meshes. */
	protected ArrayList<IndexedMesh> indexedMeshes = new ArrayList<IndexedMesh>();

	/** The data. */
	private ByteBufferVertexData data = new ByteBufferVertexData();

	/**
	 * Instantiates a new triangle index vertex array.
	 */
	public TriangleIndexVertexArray() {
	}

	/**
	 * Just to be backwards compatible.
	 */
	public TriangleIndexVertexArray(int numTriangles, ByteBuffer triangleIndexBase, int triangleIndexStride, int numVertices, ByteBuffer vertexBase, int vertexStride) {
		IndexedMesh mesh = new IndexedMesh();

		mesh.numTriangles = numTriangles;
		mesh.triangleIndexBase = triangleIndexBase;
		mesh.triangleIndexStride = triangleIndexStride;
		mesh.numVertices = numVertices;
		mesh.vertexBase = vertexBase;
		mesh.vertexStride = vertexStride;

		addIndexedMesh(mesh);
	}

	/**
	 * Adds the indexed mesh.
	 *
	 * @param mesh the mesh
	 */
	public void addIndexedMesh(IndexedMesh mesh) {
		addIndexedMesh(mesh, ScalarType.INTEGER);
	}

	/**
	 * Adds the indexed mesh.
	 *
	 * @param mesh the mesh
	 * @param indexType the index type
	 */
	public void addIndexedMesh(IndexedMesh mesh, ScalarType indexType) {
		indexedMeshes.add(mesh);
		indexedMeshes.get(indexedMeshes.size() - 1).indexType = indexType;
	}
	
	@Override
	public VertexData getLockedVertexIndexBase(int subpart) {
		assert (subpart < getNumSubParts());

		IndexedMesh mesh = indexedMeshes.get(subpart);

		data.vertexCount = mesh.numVertices;
		data.vertexData = mesh.vertexBase;
		//#ifdef BT_USE_DOUBLE_PRECISION
		//type = PHY_DOUBLE;
		//#else
		data.vertexType = ScalarType.FLOAT;
		//#endif
		data.vertexStride = mesh.vertexStride;

		data.indexCount = mesh.numTriangles*3;

		data.indexData = mesh.triangleIndexBase;
		data.indexStride = mesh.triangleIndexStride/3;
		data.indexType = mesh.indexType;
		return data;
	}

	@Override
	public VertexData getLockedReadOnlyVertexIndexBase(int subpart) {
		return getLockedVertexIndexBase(subpart);
	}

	/**
	 * unLockVertexBase finishes the access to a subpart of the triangle mesh.
	 * Make a call to unLockVertexBase when the read and write access (using getLockedVertexIndexBase) is finished.
	 */
	@Override
	public void unLockVertexBase(int subpart) {
		data.vertexData = null;
		data.indexData = null;
	}

	@Override
	public void unLockReadOnlyVertexBase(int subpart) {
		unLockVertexBase(subpart);
	}

	/**
	 * getNumSubParts returns the number of seperate subparts.
	 * Each subpart has a continuous array of vertices and indices.
	 */
	@Override
	public int getNumSubParts() {
		return indexedMeshes.size();
	}

	/**
	 * Gets the indexed mesh array.
	 *
	 * @return the indexed mesh array
	 */
	public ArrayList<IndexedMesh> getIndexedMeshArray() {
		return indexedMeshes;
	}
	
	@Override
	public void preallocateVertices(int numverts) {
	}

	@Override
	public void preallocateIndices(int numindices) {
	}

}
