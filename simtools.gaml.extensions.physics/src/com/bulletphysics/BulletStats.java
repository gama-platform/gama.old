/*******************************************************************************************************
 *
 * BulletStats.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics;

import com.bulletphysics.linearmath.CProfileManager;
import com.bulletphysics.linearmath.Clock;
import javax.vecmath.Vector3f;

/**
 * Bullet statistics and profile support.
 * 
 * @author jezek2
 */
public class BulletStats {
	
	/** The g total contact points. */
	public static int gTotalContactPoints;
	
	// GjkPairDetector
	/** The g num deep penetration checks. */
	// temp globals, to improve GJK/EPA/penetration calculations
	public static int gNumDeepPenetrationChecks = 0;
	
	/** The g num gjk checks. */
	public static int gNumGjkChecks = 0;
	
	/** The g num split impulse recoveries. */
	public static int gNumSplitImpulseRecoveries = 0;
	
	/** The g num aligned allocs. */
	public static int gNumAlignedAllocs;
	
	/** The g num aligned free. */
	public static int gNumAlignedFree;
	
	/** The g total bytes aligned allocs. */
	public static int gTotalBytesAlignedAllocs;	
	
	/** The g picking constraint id. */
	public static int gPickingConstraintId = 0;
	
	/** The Constant gOldPickingPos. */
	public static final Vector3f gOldPickingPos = new Vector3f();
	
	/** The g old picking dist. */
	public static float gOldPickingDist = 0.f;
	
	/** The g overlapping pairs. */
	public static int gOverlappingPairs = 0;
	
	/** The g remove pairs. */
	public static int gRemovePairs = 0;
	
	/** The g added pairs. */
	public static int gAddedPairs = 0;
	
	/** The g find pairs. */
	public static int gFindPairs = 0;
	
	/** The Constant gProfileClock. */
	public static final Clock gProfileClock = new Clock();

	/** The g num clamped ccd motions. */
	// DiscreteDynamicsWorld:
	public static int gNumClampedCcdMotions = 0;

	/** The step simulation time. */
	// JAVA NOTE: added for statistics in applet demo
	public static long stepSimulationTime;
	
	/** The update time. */
	public static long updateTime;
	
	/** The enable profile. */
	private static boolean enableProfile = false;
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Checks if is profile enabled.
	 *
	 * @return true, if is profile enabled
	 */
	public static boolean isProfileEnabled() {
		return enableProfile;
	}

	/**
	 * Sets the profile enabled.
	 *
	 * @param b the new profile enabled
	 */
	public static void setProfileEnabled(boolean b) {
		enableProfile = b;
	}
	
	/**
	 * Profile get ticks.
	 *
	 * @return the long
	 */
	public static long profileGetTicks() {
		long ticks = gProfileClock.getTimeMicroseconds();
		return ticks;
	}

	/**
	 * Profile get tick rate.
	 *
	 * @return the float
	 */
	public static float profileGetTickRate() {
		//return 1000000f;
		return 1000f;
	}
	
	/**
	 * Pushes profile node. Use try/finally block to call {@link #popProfile} method.
	 * 
	 * @param name must be {@link String#intern interned} String (not needed for String literals)
	 */
	public static void pushProfile(String name) {
		if (enableProfile) {
			CProfileManager.startProfile(name);
		}
	}
	
	/**
	 * Pops profile node.
	 */
	public static void popProfile() {
		if (enableProfile) {
			CProfileManager.stopProfile();
		}
	}
	
}
