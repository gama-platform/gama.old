/*******************************************************************************************************
 *
 * BulletGlobals.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics;

import com.bulletphysics.util.ArrayPool;

/**
 * Bullet global settings and constants.
 *
 * @author jezek2
 */
public class BulletGlobals {

	/** The Constant DEBUG. */
	public static final boolean DEBUG = false;

	/** The Constant CONVEX_DISTANCE_MARGIN. */
	public static final float CONVEX_DISTANCE_MARGIN = 0.04f;
	
	/** The Constant FLT_EPSILON. */
	public static final float FLT_EPSILON = 1.19209290e-07f;
	
	/** The Constant SIMD_EPSILON. */
	public static final float SIMD_EPSILON = FLT_EPSILON;

	/** The Constant SIMD_2_PI. */
	public static final float SIMD_2_PI = 6.283185307179586232f;
	
	/** The Constant SIMD_PI. */
	public static final float SIMD_PI = SIMD_2_PI * 0.5f;
	
	/** The Constant SIMD_HALF_PI. */
	public static final float SIMD_HALF_PI = SIMD_2_PI * 0.25f;
	
	/** The Constant SIMD_RADS_PER_DEG. */
	public static final float SIMD_RADS_PER_DEG = SIMD_2_PI / 360f;
	
	/** The Constant SIMD_DEGS_PER_RAD. */
	public static final float SIMD_DEGS_PER_RAD = 360f / SIMD_2_PI;
	
	/** The Constant SIMD_INFINITY. */
	public static final float SIMD_INFINITY = Float.MAX_VALUE;

	////////////////////////////////////////////////////////////////////////////

	/** The thread local. */
	private static ThreadLocal<BulletGlobals> threadLocal = new ThreadLocal<>() {
		@Override
		protected BulletGlobals initialValue() {
			return new BulletGlobals();
		}
	};

	/** The contact breaking threshold. */
	private float contactBreakingThreshold = 0.02f;
	
	/** The deactivation time. */
	// RigidBody
	private float deactivationTime = 2f;
	
	/** The disable deactivation. */
	private boolean disableDeactivation = false;

	////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the contact breaking threshold.
	 *
	 * @return the contact breaking threshold
	 */
	public static float getContactBreakingThreshold() {
		return threadLocal.get().contactBreakingThreshold;
	}

	/**
	 * Sets the contact breaking threshold.
	 *
	 * @param threshold the new contact breaking threshold
	 */
	public static void setContactBreakingThreshold(final float threshold) {
		threadLocal.get().contactBreakingThreshold = threshold;
	}

	/**
	 * Gets the deactivation time.
	 *
	 * @return the deactivation time
	 */
	public static float getDeactivationTime() {
		return threadLocal.get().deactivationTime;
	}

	/**
	 * Sets the deactivation time.
	 *
	 * @param time the new deactivation time
	 */
	public static void setDeactivationTime(final float time) {
		threadLocal.get().deactivationTime = time;
	}

	/**
	 * Checks if is deactivation disabled.
	 *
	 * @return true, if is deactivation disabled
	 */
	public static boolean isDeactivationDisabled() {
		return threadLocal.get().disableDeactivation;
	}

	/**
	 * Sets the deactivation disabled.
	 *
	 * @param disable the new deactivation disabled
	 */
	public static void setDeactivationDisabled(final boolean disable) {
		threadLocal.get().disableDeactivation = disable;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * Cleans all current thread specific settings and caches.
	 */
	public static void cleanCurrentThread() {
		threadLocal.remove();
		// Stack.libraryCleanCurrentThread();
		// ObjectPool.cleanCurrentThread();
		ArrayPool.cleanCurrentThread();
	}

}
