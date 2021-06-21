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

package com.bulletphysics.collision.shapes;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.Transform;

/**
 * Compound shape child.
 * 
 * @author jezek2
 */
public class CompoundShapeChild {
	
	public final Transform transform = new Transform();
	public CollisionShape childShape;
	public BroadphaseNativeType childShapeType;
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
