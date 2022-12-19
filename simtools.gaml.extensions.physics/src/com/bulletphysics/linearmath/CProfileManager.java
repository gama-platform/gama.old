/*******************************************************************************************************
 *
 * CProfileManager.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

/***************************************************************************************************
 **
 ** Real-Time Hierarchical Profiling for Game Programming Gems 3
 **
 ** by Greg Hjelstrom & Byon Garrabrant
 **
 ***************************************************************************************************/

package com.bulletphysics.linearmath;

import com.bulletphysics.BulletStats;

/**
 * Manager for the profile system.
 *
 * @author jezek2
 */
public class CProfileManager {

	/** The root. */
	private static CProfileNode root = new CProfileNode("Root", null);
	
	/** The current node. */
	private static CProfileNode currentNode = root;
	
	/** The frame counter. */
	private static int frameCounter = 0;
	
	/** The reset time. */
	private static long resetTime = 0;

	/**
	 * @param name
	 *            must be {@link String#intern interned} String (not needed for String literals)
	 */
	public static void startProfile(final String name) {
		if (!name.equals(currentNode.getName())) { currentNode = currentNode.getSubNode(name); }

		currentNode.call();
	}

	/**
	 * Stop profile.
	 */
	public static void stopProfile() {
		// Return will indicate whether we should back up to our parent (we may
		// be profiling a recursive function)
		if (currentNode.Return()) { currentNode = currentNode.getParent(); }
	}

	/**
	 * Cleanup memory.
	 */
	public static void cleanupMemory() {
		root.cleanupMemory();
	}

	/**
	 * Reset.
	 */
	public static void reset() {
		root.reset();
		root.call();
		frameCounter = 0;
		resetTime = BulletStats.profileGetTicks();
	}

	/**
	 * Increment frame counter.
	 */
	public static void incrementFrameCounter() {
		frameCounter++;
	}

	/**
	 * Gets the frame count since reset.
	 *
	 * @return the frame count since reset
	 */
	public static int getFrameCountSinceReset() { return frameCounter; }

	/**
	 * Gets the time since reset.
	 *
	 * @return the time since reset
	 */
	public static float getTimeSinceReset() {
		long time = BulletStats.profileGetTicks();
		time -= resetTime;
		return time / BulletStats.profileGetTickRate();
	}

	/**
	 * Gets the iterator.
	 *
	 * @return the iterator
	 */
	public static CProfileIterator getIterator() { return new CProfileIterator(root); }

	/**
	 * Release iterator.
	 *
	 * @param iterator the iterator
	 */
	public static void releaseIterator(final CProfileIterator iterator) {
		/* delete ( iterator); */
	}

}
