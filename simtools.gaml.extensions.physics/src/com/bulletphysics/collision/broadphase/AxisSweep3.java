/*******************************************************************************************************
 *
 * AxisSweep3.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import javax.vecmath.Vector3f;

/**
 * AxisSweep3 is an efficient implementation of the 3D axis sweep and prune broadphase.<p>
 * 
 * It uses arrays rather then lists for storage of the 3 axis. Also it operates using 16 bit
 * integer coordinates instead of floats. For large worlds and many objects, use {@link AxisSweep3_32}
 * instead. AxisSweep3_32 has higher precision and allows more than 16384 objects at the cost
 * of more memory and bit of performance.
 *
 * @author jezek2
 */
public class AxisSweep3 extends AxisSweep3Internal {

	/**
	 * Instantiates a new axis sweep 3.
	 *
	 * @param worldAabbMin the world aabb min
	 * @param worldAabbMax the world aabb max
	 */
	public AxisSweep3(Vector3f worldAabbMin, Vector3f worldAabbMax) {
		this(worldAabbMin, worldAabbMax, 16384, null);
	}

	/**
	 * Instantiates a new axis sweep 3.
	 *
	 * @param worldAabbMin the world aabb min
	 * @param worldAabbMax the world aabb max
	 * @param maxHandles the max handles
	 */
	public AxisSweep3(Vector3f worldAabbMin, Vector3f worldAabbMax, int maxHandles) {
		this(worldAabbMin, worldAabbMax, maxHandles, null);
	}
	
	/**
	 * Instantiates a new axis sweep 3.
	 *
	 * @param worldAabbMin the world aabb min
	 * @param worldAabbMax the world aabb max
	 * @param maxHandles the max handles
	 * @param pairCache the pair cache
	 */
	public AxisSweep3(Vector3f worldAabbMin, Vector3f worldAabbMax, int maxHandles/* = 16384*/, OverlappingPairCache pairCache/* = 0*/) {
		super(worldAabbMin, worldAabbMax, 0xfffe, 0xffff, maxHandles, pairCache);
		// 1 handle is reserved as sentinel
		assert (maxHandles > 1 && maxHandles < 32767);
	}
	
	@Override
	protected EdgeArray createEdgeArray(int size) {
		return new EdgeArrayImpl(size);
	}

	@Override
	protected Handle createHandle() {
		return new HandleImpl();
	}
	
	protected int getMask() {
		return 0xFFFF;
	}
	
	/**
	 * The Class EdgeArrayImpl.
	 */
	protected static class EdgeArrayImpl extends EdgeArray {
		
		/** The pos. */
		private short[] pos;
		
		/** The handle. */
		private short[] handle;

		/**
		 * Instantiates a new edge array impl.
		 *
		 * @param size the size
		 */
		public EdgeArrayImpl(int size) {
			pos = new short[size];
			handle = new short[size];
		}
		
		@Override
		public void swap(int idx1, int idx2) {
			short tmpPos = pos[idx1];
			short tmpHandle = handle[idx1];
			
			pos[idx1] = pos[idx2];
			handle[idx1] = handle[idx2];
			
			pos[idx2] = tmpPos;
			handle[idx2] = tmpHandle;
		}
		
		@Override
		public void set(int dest, int src) {
			pos[dest] = pos[src];
			handle[dest] = handle[src];
		}
		
		@Override
		public int getPos(int index) {
			return pos[index] & 0xFFFF;
		}

		@Override
		public void setPos(int index, int value) {
			pos[index] = (short)value;
		}

		@Override
		public int getHandle(int index) {
			return handle[index] & 0xFFFF;
		}

		@Override
		public void setHandle(int index, int value) {
			handle[index] = (short)value;
		}
	}
	
	/**
	 * The Class HandleImpl.
	 */
	protected static class HandleImpl extends Handle {
		
		/** The min edges 0. */
		private short minEdges0;
		
		/** The min edges 1. */
		private short minEdges1;
		
		/** The min edges 2. */
		private short minEdges2;

		/** The max edges 0. */
		private short maxEdges0;
		
		/** The max edges 1. */
		private short maxEdges1;
		
		/** The max edges 2. */
		private short maxEdges2;
		
		@Override
		public int getMinEdges(int edgeIndex) {
			switch (edgeIndex) {
				default:
				case 0: return minEdges0 & 0xFFFF;
				case 1: return minEdges1 & 0xFFFF;
				case 2: return minEdges2 & 0xFFFF;
			}
		}
		
		@Override
		public void setMinEdges(int edgeIndex, int value) {
			switch (edgeIndex) {
				case 0: minEdges0 = (short)value; break;
				case 1: minEdges1 = (short)value; break;
				case 2: minEdges2 = (short)value; break;
			}
		}
		
		@Override
		public int getMaxEdges(int edgeIndex) {
			switch (edgeIndex) {
				default:
				case 0: return maxEdges0 & 0xFFFF;
				case 1: return maxEdges1 & 0xFFFF;
				case 2: return maxEdges2 & 0xFFFF;
			}
		}
		
		@Override
		public void setMaxEdges(int edgeIndex, int value) {
			switch (edgeIndex) {
				case 0: maxEdges0 = (short)value; break;
				case 1: maxEdges1 = (short)value; break;
				case 2: maxEdges2 = (short)value; break;
			}
		}
	}

}
