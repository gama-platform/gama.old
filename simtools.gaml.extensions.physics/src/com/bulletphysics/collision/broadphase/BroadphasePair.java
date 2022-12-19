/*******************************************************************************************************
 *
 * BroadphasePair.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

/**
 * BroadphasePair class contains a pair of AABB-overlapping objects. {@link Dispatcher} can search a
 * {@link CollisionAlgorithm} that performs exact/narrowphase collision detection on the actual collision shapes.
 *
 * @author jezek2
 */
public class BroadphasePair implements Comparable<BroadphasePair> {

	/** The p proxy 0. */
	public BroadphaseProxy pProxy0;
	
	/** The p proxy 1. */
	public BroadphaseProxy pProxy1;
	
	/** The algorithm. */
	public CollisionAlgorithm algorithm;
	
	/** The user info. */
	public Object userInfo;

	/**
	 * Instantiates a new broadphase pair.
	 */
	public BroadphasePair() {}

	/**
	 * Instantiates a new broadphase pair.
	 *
	 * @param pProxy0 the proxy 0
	 * @param pProxy1 the proxy 1
	 */
	public BroadphasePair(final BroadphaseProxy pProxy0, final BroadphaseProxy pProxy1) {
		this.pProxy0 = pProxy0;
		this.pProxy1 = pProxy1;
		this.algorithm = null;
		this.userInfo = null;
	}

	/**
	 * Sets the.
	 *
	 * @param p the p
	 */
	public void set(final BroadphasePair p) {
		pProxy0 = p.pProxy0;
		pProxy1 = p.pProxy1;
		algorithm = p.algorithm;
		userInfo = p.userInfo;
	}

	/**
	 * Equals.
	 *
	 * @param p the p
	 * @return true, if successful
	 */
	public boolean equals(final BroadphasePair p) {
		return pProxy0 == p.pProxy0 && pProxy1 == p.pProxy1;
	}

	@Override
	public int compareTo(final BroadphasePair b) {
		boolean result = pProxy0.getUid() > b.pProxy0.getUid()
				|| pProxy0.getUid() == b.pProxy0.getUid() && pProxy1.getUid() >= pProxy1.getUid();
		return result ? -1 : 1;
	}

}
