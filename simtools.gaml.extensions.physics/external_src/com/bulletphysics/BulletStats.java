/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

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
	
	public static int gTotalContactPoints;
	
	// GjkPairDetector
	// temp globals, to improve GJK/EPA/penetration calculations
	public static int gNumDeepPenetrationChecks = 0;
	public static int gNumGjkChecks = 0;
	public static int gNumSplitImpulseRecoveries = 0;
	
	public static int gNumAlignedAllocs;
	public static int gNumAlignedFree;
	public static int gTotalBytesAlignedAllocs;	
	
	public static int gPickingConstraintId = 0;
	public static final Vector3f gOldPickingPos = new Vector3f();
	public static float gOldPickingDist = 0.f;
	
	public static int gOverlappingPairs = 0;
	public static int gRemovePairs = 0;
	public static int gAddedPairs = 0;
	public static int gFindPairs = 0;
	
	public static final Clock gProfileClock = new Clock();

	// DiscreteDynamicsWorld:
	public static int gNumClampedCcdMotions = 0;

	// JAVA NOTE: added for statistics in applet demo
	public static long stepSimulationTime;
	public static long updateTime;
	
	private static boolean enableProfile = false;
	
	////////////////////////////////////////////////////////////////////////////
	
	public static boolean isProfileEnabled() {
		return enableProfile;
	}

	public static void setProfileEnabled(boolean b) {
		enableProfile = b;
	}
	
	public static long profileGetTicks() {
		long ticks = gProfileClock.getTimeMicroseconds();
		return ticks;
	}

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
