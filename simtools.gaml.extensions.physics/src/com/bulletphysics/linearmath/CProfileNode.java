/*******************************************************************************************************
 *
 * CProfileNode.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * A node in the Profile Hierarchy Tree.
 * 
 * @author jezek2
 */
class CProfileNode {

	/** The name. */
	protected String name;
	
	/** The total calls. */
	protected int totalCalls;
	
	/** The total time. */
	protected float totalTime;
	
	/** The start time. */
	protected long startTime;
	
	/** The recursion counter. */
	protected int recursionCounter;
	
	/** The parent. */
	protected CProfileNode parent;
	
	/** The child. */
	protected CProfileNode child;
	
	/** The sibling. */
	protected CProfileNode sibling;

	/**
	 * Instantiates a new c profile node.
	 *
	 * @param name the name
	 * @param parent the parent
	 */
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

	/**
	 * Gets the sub node.
	 *
	 * @param name the name
	 * @return the sub node
	 */
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

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public CProfileNode getParent() {
		return parent;
	}

	/**
	 * Gets the sibling.
	 *
	 * @return the sibling
	 */
	public CProfileNode getSibling() {
		return sibling;
	}

	/**
	 * Gets the child.
	 *
	 * @return the child
	 */
	public CProfileNode getChild() {
		return child;
	}

	/**
	 * Cleanup memory.
	 */
	public void cleanupMemory() {
		child = null;
		sibling = null;
	}
	
	/**
	 * Reset.
	 */
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
	
	/**
	 * Call.
	 */
	public void call() {
		totalCalls++;
		if (recursionCounter++ == 0) {
			startTime = BulletStats.profileGetTicks();
		}
	}
	
	/**
	 * Return.
	 *
	 * @return true, if successful
	 */
	public boolean Return() {
		if (--recursionCounter == 0 && totalCalls != 0) {
			long time = BulletStats.profileGetTicks();
			time -= startTime;
			totalTime += time / BulletStats.profileGetTickRate();
		}
		return (recursionCounter == 0);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the total calls.
	 *
	 * @return the total calls
	 */
	public int getTotalCalls() {
		return totalCalls;
	}

	/**
	 * Gets the total time.
	 *
	 * @return the total time
	 */
	public float getTotalTime() {
		return totalTime;
	}
	
}
