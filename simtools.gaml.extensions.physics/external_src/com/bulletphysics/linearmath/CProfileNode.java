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
 * A node in the Profile Hierarchy Tree.
 * 
 * @author jezek2
 */
class CProfileNode {

	protected String name;
	protected int totalCalls;
	protected float totalTime;
	protected long startTime;
	protected int recursionCounter;
	
	protected CProfileNode parent;
	protected CProfileNode child;
	protected CProfileNode sibling;

	public CProfileNode(String name, CProfileNode parent) {
		this.name = name;
		this.totalCalls = 0;
		this.totalTime = 0;
		this.startTime = 0;
		this.recursionCounter = 0;
		this.parent = parent;
		this.child = null;
		this.sibling = null;
		
		reset();
	}

	public CProfileNode getSubNode(String name) {
		// Try to find this sub node
		CProfileNode child = this.child;
		while (child != null) {
			if (child.name == name) {
				return child;
			}
			child = child.sibling;
		}

		// We didn't find it, so add it

		CProfileNode node = new CProfileNode(name, this);
		node.sibling = this.child;
		this.child = node;
		return node;
	}

	public CProfileNode getParent() {
		return parent;
	}

	public CProfileNode getSibling() {
		return sibling;
	}

	public CProfileNode getChild() {
		return child;
	}

	public void cleanupMemory() {
		child = null;
		sibling = null;
	}
	
	public void reset() {
		totalCalls = 0;
		totalTime = 0.0f;
		BulletStats.gProfileClock.reset();

		if (child != null) {
			child.reset();
		}
		if (sibling != null) {
			sibling.reset();
		}
	}
	
	public void call() {
		totalCalls++;
		if (recursionCounter++ == 0) {
			startTime = BulletStats.profileGetTicks();
		}
	}
	
	public boolean Return() {
		if (--recursionCounter == 0 && totalCalls != 0) {
			long time = BulletStats.profileGetTicks();
			time -= startTime;
			totalTime += (float) time / BulletStats.profileGetTickRate();
		}
		return (recursionCounter == 0);
	}

	public String getName() {
		return name;
	}
	
	public int getTotalCalls() {
		return totalCalls;
	}

	public float getTotalTime() {
		return totalTime;
	}
	
}
