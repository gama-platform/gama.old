/*******************************************************************************************************
 *
 * ByteBufferVertexData.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import java.nio.ByteBuffer;

import javax.vecmath.Tuple3f;

/**
 *
 * @author jezek2
 */
public class ByteBufferVertexData implements VertexData {

	/** The vertex data. */
	public ByteBuffer vertexData;
	
	/** The vertex count. */
	public int vertexCount;
	
	/** The vertex stride. */
	public int vertexStride;
	
	/** The vertex type. */
	public ScalarType vertexType;

	/** The index data. */
	public ByteBuffer indexData;
	
	/** The index count. */
	public int indexCount;
	
	/** The index stride. */
	public int indexStride;
	
	/** The index type. */
	public ScalarType indexType;

	@Override
	public int getVertexCount() {
		return vertexCount;
	}

	@Override
	public int getIndexCount() {
		return indexCount;
	}

	@Override
	public <T extends Tuple3f> T getVertex(final int idx, final T out) {
		int off = idx * vertexStride;
		out.x = vertexData.getFloat(off + 4 * 0);
		out.y = vertexData.getFloat(off + 4 * 1);
		out.z = vertexData.getFloat(off + 4 * 2);
		return out;
	}

	@Override
	public void setVertex(final int idx, final float x, final float y, final float z) {
		int off = idx * vertexStride;
		vertexData.putFloat(off + 4 * 0, x);
		vertexData.putFloat(off + 4 * 1, y);
		vertexData.putFloat(off + 4 * 2, z);
	}

	@Override
	public int getIndex(final int idx) {
		if (indexType == ScalarType.SHORT)
			return indexData.getShort(idx * indexStride) & 0xFFFF;
		else if (indexType == ScalarType.INTEGER)
			return indexData.getInt(idx * indexStride);
		else
			throw new IllegalStateException("indicies type must be short or integer");
	}

}
