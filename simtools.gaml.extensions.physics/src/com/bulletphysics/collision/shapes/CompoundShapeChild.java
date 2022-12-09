/*******************************************************************************************************
 *
 * CompoundShapeChild.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.Transform;

/**
 * Compound shape child.
 * 
 * @author jezek2
 */
public class CompoundShapeChild {
	
	/** The transform. */
	public final Transform transform = new Transform();
	
	/** The child shape. */
	public CollisionShape childShape;
	
	/** The child shape type. */
	public BroadphaseNativeType childShapeType;
	
	/** The child margin. */
	public float childMargin;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof CompoundShapeChild)) return false;
		CompoundShapeChild child = (CompoundShapeChild)obj;
		return transform.equals(child.transform) &&
		       childShape == child.childShape &&
		       childShapeType == child.childShapeType &&
		       childMargin == child.childMargin;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 19 * hash + transform.hashCode();
		hash = 19 * hash + childShape.hashCode();
		hash = 19 * hash + childShapeType.hashCode();
		hash = 19 * hash + Float.floatToIntBits(childMargin);
		return hash;
	}

}
