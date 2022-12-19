/*******************************************************************************************************
 *
 * CProfileIterator.java, in simtools.gaml.extensions.physics, is part of the source code of the
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

/**
 * Iterator to navigate through profile tree.
 * 
 * @author jezek2
 */
public class CProfileIterator {

	/** The current parent. */
	private CProfileNode currentParent;
	
	/** The current child. */
	private CProfileNode currentChild;

	/**
	 * Instantiates a new c profile iterator.
	 *
	 * @param start the start
	 */
	CProfileIterator(CProfileNode start) {
		currentParent = start;
		currentChild = currentParent.getChild();
	}
	
	// Access all the children of the current parent
	
	/**
	 * First.
	 */
	public void first() {
		currentChild = currentParent.getChild();
	}
	
	/**
	 * Next.
	 */
	public void next() {
		currentChild = currentChild.getSibling();
	}
	
	/**
	 * Checks if is done.
	 *
	 * @return true, if is done
	 */
	public boolean isDone() {
		return (currentChild == null);
	}
	
	/**
	 * Checks if is root.
	 *
	 * @return true, if is root
	 */
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
	
	/**
	 * Gets the current name.
	 *
	 * @return the current name
	 */
	public String getCurrentName() {
		return currentChild.getName();
	}

	/**
	 * Gets the current total calls.
	 *
	 * @return the current total calls
	 */
	public int getCurrentTotalCalls() {
		return currentChild.getTotalCalls();
	}

	/**
	 * Gets the current total time.
	 *
	 * @return the current total time
	 */
	public float getCurrentTotalTime() {
		return currentChild.getTotalTime();
	}

	// Access the current parent
	
	/**
	 * Gets the current parent name.
	 *
	 * @return the current parent name
	 */
	public String getCurrentParentName() {
		return currentParent.getName();
	}

	/**
	 * Gets the current parent total calls.
	 *
	 * @return the current parent total calls
	 */
	public int getCurrentParentTotalCalls() {
		return currentParent.getTotalCalls();
	}

	/**
	 * Gets the current parent total time.
	 *
	 * @return the current parent total time
	 */
	public float getCurrentParentTotalTime() {
		return currentParent.getTotalTime();
	}
	
}
