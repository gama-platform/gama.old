/*******************************************************************************************************
 *
 * ContactID.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 *
 * JBox2D homepage: http://jbox2d.sourceforge.net/ Box2D homepage: http://www.box2d.org
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */
package org.jbox2d.collision;

/**
 * Contact ids to facilitate warm starting. Note: the ContactFeatures class is just embedded in here
 */
public class ContactID implements Comparable<ContactID> {

	/**
	 * The Enum Type.
	 */
	public enum Type {
		
		/** The vertex. */
		VERTEX, 
 /** The face. */
 FACE
	}

	/** The index A. */
	public byte indexA;
	
	/** The index B. */
	public byte indexB;
	
	/** The type A. */
	public byte typeA;
	
	/** The type B. */
	public byte typeB;

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public int getKey() { return indexA << 24 | indexB << 16 | typeA << 8 | typeB; }

	/**
	 * Checks if is equal.
	 *
	 * @param cid the cid
	 * @return true, if is equal
	 */
	public boolean isEqual(final ContactID cid) {
		return getKey() == cid.getKey();
	}

	/**
	 * Instantiates a new contact ID.
	 */
	public ContactID() {}

	/**
	 * Instantiates a new contact ID.
	 *
	 * @param c the c
	 */
	public ContactID(final ContactID c) {
		set(c);
	}

	/**
	 * Sets the.
	 *
	 * @param c the c
	 */
	public void set(final ContactID c) {
		indexA = c.indexA;
		indexB = c.indexB;
		typeA = c.typeA;
		typeB = c.typeB;
	}

	/**
	 * Flip.
	 */
	public void flip() {
		byte tempA = indexA;
		indexA = indexB;
		indexB = tempA;
		tempA = typeA;
		typeA = typeB;
		typeB = tempA;
	}

	/**
	 * zeros out the data
	 */
	public void zero() {
		indexA = 0;
		indexB = 0;
		typeA = 0;
		typeB = 0;
	}

	@Override
	public int compareTo(final ContactID o) {
		return getKey() - o.getKey();
	}
}
