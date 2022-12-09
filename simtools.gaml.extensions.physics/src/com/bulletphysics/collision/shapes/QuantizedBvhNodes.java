/*******************************************************************************************************
 *
 * QuantizedBvhNodes.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import java.io.Serializable;

/**
 * QuantizedBvhNodes is array of compressed AABB nodes, each of 16 bytes. Node can be used for leaf node or internal
 * node. Leaf nodes can point to 32-bit triangle index (non-negative range).
 * <p>
 *
 * <i>Implementation note:</i> the nodes are internally stored in int[] array and bit packed. The actual structure is:
 *
 * <pre>
 * unsigned short  quantizedAabbMin[3]
 * unsigned short  quantizedAabbMax[3]
 * signed   int    escapeIndexOrTriangleIndex
 * </pre>
 *
 * @author jezek2
 */
public class QuantizedBvhNodes implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant STRIDE. */
	private static final int STRIDE = 4; // 16 bytes

	/** The buf. */
	private int[] buf;

	/** The size. */
	private int size = 0;

	/**
	 * Instantiates a new quantized bvh nodes.
	 */
	public QuantizedBvhNodes() {
		resize(16);
	}

	/**
	 * Adds the.
	 *
	 * @return the int
	 */
	public int add() {
		while (size + 1 >= capacity()) { resize(capacity() * 2); }
		return size++;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return size;
	}

	/**
	 * Capacity.
	 *
	 * @return the int
	 */
	public int capacity() {
		return buf.length / STRIDE;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		size = 0;
	}

	/**
	 * Resize.
	 *
	 * @param num
	 *            the num
	 */
	public void resize(final int num) {
		int[] oldBuf = buf;

		buf = new int[num * STRIDE];
		if (oldBuf != null) { System.arraycopy(oldBuf, 0, buf, 0, Math.min(oldBuf.length, buf.length)); }
	}

	/**
	 * Gets the node size.
	 *
	 * @return the node size
	 */
	public static int getNodeSize() { return STRIDE * 4; }

	/**
	 * Sets the.
	 *
	 * @param destId
	 *            the dest id
	 * @param srcNodes
	 *            the src nodes
	 * @param srcId
	 *            the src id
	 */
	public void set(final int destId, final QuantizedBvhNodes srcNodes, final int srcId) {
		// assert STRIDE == 4;

		// save field access:
		int[] buf = this.buf;
		int[] srcBuf = srcNodes.buf;

		buf[destId * STRIDE + 0] = srcBuf[srcId * STRIDE + 0];
		buf[destId * STRIDE + 1] = srcBuf[srcId * STRIDE + 1];
		buf[destId * STRIDE + 2] = srcBuf[srcId * STRIDE + 2];
		buf[destId * STRIDE + 3] = srcBuf[srcId * STRIDE + 3];
	}

	/**
	 * Swap.
	 *
	 * @param id1
	 *            the id 1
	 * @param id2
	 *            the id 2
	 */
	public void swap(final int id1, final int id2) {
		// assert (STRIDE == 4);

		// save field access:
		int[] buf = this.buf;

		int temp0 = buf[id1 * STRIDE + 0];
		int temp1 = buf[id1 * STRIDE + 1];
		int temp2 = buf[id1 * STRIDE + 2];
		int temp3 = buf[id1 * STRIDE + 3];

		buf[id1 * STRIDE + 0] = buf[id2 * STRIDE + 0];
		buf[id1 * STRIDE + 1] = buf[id2 * STRIDE + 1];
		buf[id1 * STRIDE + 2] = buf[id2 * STRIDE + 2];
		buf[id1 * STRIDE + 3] = buf[id2 * STRIDE + 3];

		buf[id2 * STRIDE + 0] = temp0;
		buf[id2 * STRIDE + 1] = temp1;
		buf[id2 * STRIDE + 2] = temp2;
		buf[id2 * STRIDE + 3] = temp3;
	}

	/**
	 * Gets the quantized aabb min.
	 *
	 * @param nodeId
	 *            the node id
	 * @param index
	 *            the index
	 * @return the quantized aabb min
	 */
	public int getQuantizedAabbMin(final int nodeId, final int index) {
		switch (index) {
			default:
			case 0:
				return buf[nodeId * STRIDE + 0] & 0xFFFF;
			case 1:
				return buf[nodeId * STRIDE + 0] >>> 16 & 0xFFFF;
			case 2:
				return buf[nodeId * STRIDE + 1] & 0xFFFF;
		}
	}

	/**
	 * Gets the quantized aabb min.
	 *
	 * @param nodeId
	 *            the node id
	 * @return the quantized aabb min
	 */
	public long getQuantizedAabbMin(final int nodeId) {
		return buf[nodeId * STRIDE + 0] & 0xFFFFFFFFL | (buf[nodeId * STRIDE + 1] & 0xFFFFL) << 32;
	}

	/**
	 * Sets the quantized aabb min.
	 *
	 * @param nodeId
	 *            the node id
	 * @param value
	 *            the value
	 */
	public void setQuantizedAabbMin(final int nodeId, final long value) {
		buf[nodeId * STRIDE + 0] = (int) value;
		setQuantizedAabbMin(nodeId, 2, (short) ((value & 0xFFFF00000000L) >>> 32));
	}

	/**
	 * Sets the quantized aabb max.
	 *
	 * @param nodeId
	 *            the node id
	 * @param value
	 *            the value
	 */
	public void setQuantizedAabbMax(final int nodeId, final long value) {
		setQuantizedAabbMax(nodeId, 0, (short) value);
		buf[nodeId * STRIDE + 2] = (int) (value >>> 16);
	}

	/**
	 * Sets the quantized aabb min.
	 *
	 * @param nodeId
	 *            the node id
	 * @param index
	 *            the index
	 * @param value
	 *            the value
	 */
	public void setQuantizedAabbMin(final int nodeId, final int index, final int value) {
		switch (index) {
			case 0:
				buf[nodeId * STRIDE + 0] = buf[nodeId * STRIDE + 0] & 0xFFFF0000 | value & 0xFFFF;
				break;
			case 1:
				buf[nodeId * STRIDE + 0] = buf[nodeId * STRIDE + 0] & 0x0000FFFF | (value & 0xFFFF) << 16;
				break;
			case 2:
				buf[nodeId * STRIDE + 1] = buf[nodeId * STRIDE + 1] & 0xFFFF0000 | value & 0xFFFF;
				break;
		}
	}

	/**
	 * Gets the quantized aabb max.
	 *
	 * @param nodeId
	 *            the node id
	 * @param index
	 *            the index
	 * @return the quantized aabb max
	 */
	public int getQuantizedAabbMax(final int nodeId, final int index) {
		switch (index) {
			default:
			case 0:
				return buf[nodeId * STRIDE + 1] >>> 16 & 0xFFFF;
			case 1:
				return buf[nodeId * STRIDE + 2] & 0xFFFF;
			case 2:
				return buf[nodeId * STRIDE + 2] >>> 16 & 0xFFFF;
		}
	}

	/**
	 * Gets the quantized aabb max.
	 *
	 * @param nodeId
	 *            the node id
	 * @return the quantized aabb max
	 */
	public long getQuantizedAabbMax(final int nodeId) {
		return (buf[nodeId * STRIDE + 1] & 0xFFFF0000L) >>> 16 | (buf[nodeId * STRIDE + 2] & 0xFFFFFFFFL) << 16;
	}

	/**
	 * Sets the quantized aabb max.
	 *
	 * @param nodeId
	 *            the node id
	 * @param index
	 *            the index
	 * @param value
	 *            the value
	 */
	public void setQuantizedAabbMax(final int nodeId, final int index, final int value) {
		switch (index) {
			case 0:
				buf[nodeId * STRIDE + 1] = buf[nodeId * STRIDE + 1] & 0x0000FFFF | (value & 0xFFFF) << 16;
				break;
			case 1:
				buf[nodeId * STRIDE + 2] = buf[nodeId * STRIDE + 2] & 0xFFFF0000 | value & 0xFFFF;
				break;
			case 2:
				buf[nodeId * STRIDE + 2] = buf[nodeId * STRIDE + 2] & 0x0000FFFF | (value & 0xFFFF) << 16;
				break;
		}
	}

	/**
	 * Gets the escape index or triangle index.
	 *
	 * @param nodeId
	 *            the node id
	 * @return the escape index or triangle index
	 */
	public int getEscapeIndexOrTriangleIndex(final int nodeId) {
		return buf[nodeId * STRIDE + 3];
	}

	/**
	 * Sets the escape index or triangle index.
	 *
	 * @param nodeId
	 *            the node id
	 * @param value
	 *            the value
	 */
	public void setEscapeIndexOrTriangleIndex(final int nodeId, final int value) {
		buf[nodeId * STRIDE + 3] = value;
	}

	/**
	 * Checks if is leaf node.
	 *
	 * @param nodeId
	 *            the node id
	 * @return true, if is leaf node
	 */
	public boolean isLeafNode(final int nodeId) {
		// skipindex is negative (internal node), triangleindex >=0 (leafnode)
		return getEscapeIndexOrTriangleIndex(nodeId) >= 0;
	}

	/**
	 * Gets the escape index.
	 *
	 * @param nodeId
	 *            the node id
	 * @return the escape index
	 */
	public int getEscapeIndex(final int nodeId) {
		assert !isLeafNode(nodeId);
		return -getEscapeIndexOrTriangleIndex(nodeId);
	}

	/**
	 * Gets the triangle index.
	 *
	 * @param nodeId
	 *            the node id
	 * @return the triangle index
	 */
	public int getTriangleIndex(final int nodeId) {
		assert isLeafNode(nodeId);
		// Get only the lower bits where the triangle index is stored
		return getEscapeIndexOrTriangleIndex(nodeId) & ~(~0 << 31 - OptimizedBvh.MAX_NUM_PARTS_IN_BITS);
	}

	/**
	 * Gets the part id.
	 *
	 * @param nodeId
	 *            the node id
	 * @return the part id
	 */
	public int getPartId(final int nodeId) {
		assert isLeafNode(nodeId);
		// Get only the highest bits where the part index is stored
		return getEscapeIndexOrTriangleIndex(nodeId) >>> 31 - OptimizedBvh.MAX_NUM_PARTS_IN_BITS;
	}

	/**
	 * Gets the coord.
	 *
	 * @param vec
	 *            the vec
	 * @param index
	 *            the index
	 * @return the coord
	 */
	public static int getCoord(final long vec, final int index) {
		switch (index) {
			default:
			case 0:
				return (int) (vec & 0x00000000FFFFL) & 0xFFFF;
			case 1:
				return (int) ((vec & 0x0000FFFF0000L) >>> 16) & 0xFFFF;
			case 2:
				return (int) ((vec & 0xFFFF00000000L) >>> 32) & 0xFFFF;
		}
	}

}
