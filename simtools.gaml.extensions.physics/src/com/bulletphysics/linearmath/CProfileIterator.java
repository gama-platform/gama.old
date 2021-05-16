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

/**
 * Iterator to navigate through profile tree.
 * 
 * @author jezek2
 */
public class CProfileIterator {

	private CProfileNode currentParent;
	private CProfileNode currentChild;

	CProfileIterator(CProfileNode start) {
		currentParent = start;
		currentChild = currentParent.getChild();
	}
	
	// Access all the children of the current parent
	
	public void first() {
		currentChild = currentParent.getChild();
	}
	
	public void next() {
		currentChild = currentChild.getSibling();
	}
	
	public boolean isDone() {
		return (currentChild == null);
	}
	
	public boolean isRoot() {
		return (currentParent.getParent() == null);
	}

	/**
	 * Make the given child the new parent.
	 */
	public void enterChild(int index) {
		currentChild = currentParent.getChild();
		while ((currentChild != null) && (index != 0)) {
			index--;
			currentChild = currentChild.getSibling();
		}

		if (currentChild != null) {
			currentParent = currentChild;
			currentChild = currentParent.getChild();
		}
	}
	
	//public void enterLargestChild(); // Make the largest child the new parent
	
	/**
	 * Make the current parent's parent the new parent.
	 */
	public void enterParent() {
		if (currentParent.getParent() != null) {
			currentParent = currentParent.getParent();
		}
		currentChild = currentParent.getChild();
	}

	// Access the current child
	
	public String getCurrentName() {
		return currentChild.getName();
	}

	public int getCurrentTotalCalls() {
		return currentChild.getTotalCalls();
	}

	public float getCurrentTotalTime() {
		return currentChild.getTotalTime();
	}

	// Access the current parent
	
	public String getCurrentParentName() {
		return currentParent.getName();
	}

	public int getCurrentParentTotalCalls() {
		return currentParent.getTotalCalls();
	}

	public float getCurrentParentTotalTime() {
		return currentParent.getTotalTime();
	}
	
}
