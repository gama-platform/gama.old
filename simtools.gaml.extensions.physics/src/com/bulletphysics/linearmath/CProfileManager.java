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

	private static CProfileNode root = new CProfileNode("Root", null);
	private static CProfileNode currentNode = root;
	private static int frameCounter = 0;
	private static long resetTime = 0;

	/**
	 * @param name must be {@link String#intern interned} String (not needed for String literals)
	 */
	public static void startProfile(String name) {
		if (name != currentNode.getName()) {
			currentNode = currentNode.getSubNode(name);
		}

		currentNode.call();
	}
	
	public static void stopProfile() {
		// Return will indicate whether we should back up to our parent (we may
		// be profiling a recursive function)
		if (currentNode.Return()) {
			currentNode = currentNode.getParent();
		}
	}

	public static void cleanupMemory() {
		root.cleanupMemory();
	}

	public static void reset() {
		root.reset();
		root.call();
		frameCounter = 0;
		resetTime = BulletStats.profileGetTicks();
	}
	
	public static void incrementFrameCounter() {
		frameCounter++;
	}
	
	public static int getFrameCountSinceReset() {
		return frameCounter;
	}
	
	public static float getTimeSinceReset() {
		long time = BulletStats.profileGetTicks();
		time -= resetTime;
		return (float) time / BulletStats.profileGetTickRate();
	}

	public static CProfileIterator getIterator() {
		return new CProfileIterator(root);
	}
	
	public static void releaseIterator(CProfileIterator iterator) {
		/*delete ( iterator);*/
	}
	
}
