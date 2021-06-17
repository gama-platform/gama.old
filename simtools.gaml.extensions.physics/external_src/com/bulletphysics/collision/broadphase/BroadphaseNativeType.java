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

package com.bulletphysics.collision.broadphase;

/**
 * Dispatcher uses these types.<p>
 * 
 * IMPORTANT NOTE: The types are ordered polyhedral, implicit convex and concave
 * to facilitate type checking.
 * 
 * @author jezek2
 */
public enum BroadphaseNativeType {
	
	// polyhedral convex shapes:
	BOX_SHAPE_PROXYTYPE,
	TRIANGLE_SHAPE_PROXYTYPE,
	TETRAHEDRAL_SHAPE_PROXYTYPE,
	CONVEX_TRIANGLEMESH_SHAPE_PROXYTYPE,
	CONVEX_HULL_SHAPE_PROXYTYPE,
	
	// implicit convex shapes:
	IMPLICIT_CONVEX_SHAPES_START_HERE,
	SPHERE_SHAPE_PROXYTYPE,
	MULTI_SPHERE_SHAPE_PROXYTYPE,
	CAPSULE_SHAPE_PROXYTYPE,
	CONE_SHAPE_PROXYTYPE,
	CONVEX_SHAPE_PROXYTYPE,
	CYLINDER_SHAPE_PROXYTYPE,
	UNIFORM_SCALING_SHAPE_PROXYTYPE,
	MINKOWSKI_SUM_SHAPE_PROXYTYPE,
	MINKOWSKI_DIFFERENCE_SHAPE_PROXYTYPE,
	
	// concave shapes:
	CONCAVE_SHAPES_START_HERE,
	
	// keep all the convex shapetype below here, for the check IsConvexShape in broadphase proxy!
	TRIANGLE_MESH_SHAPE_PROXYTYPE,
	SCALED_TRIANGLE_MESH_SHAPE_PROXYTYPE,
	
	// used for demo integration FAST/Swift collision library and Bullet:
	FAST_CONCAVE_MESH_PROXYTYPE,
	
	// terrain:
	TERRAIN_SHAPE_PROXYTYPE,
	
	// used for GIMPACT Trimesh integration:
	GIMPACT_SHAPE_PROXYTYPE,
	
	// multimaterial mesh:
	MULTIMATERIAL_TRIANGLE_MESH_PROXYTYPE,
	
	EMPTY_SHAPE_PROXYTYPE,
	STATIC_PLANE_PROXYTYPE,
	CONCAVE_SHAPES_END_HERE,
	COMPOUND_SHAPE_PROXYTYPE,
	
	SOFTBODY_SHAPE_PROXYTYPE,

	INVALID_SHAPE_PROXYTYPE,
	
	MAX_BROADPHASE_COLLISION_TYPES;
	
	private static BroadphaseNativeType[] values = values();
	
	public static BroadphaseNativeType forValue(int value) {
		return values[value];
	}
	
	public boolean isPolyhedral() {
		return (ordinal() < IMPLICIT_CONVEX_SHAPES_START_HERE.ordinal());
	}

	public boolean isConvex() {
		return (ordinal() < CONCAVE_SHAPES_START_HERE.ordinal());
	}

	public boolean isConcave() {
		return ((ordinal() > CONCAVE_SHAPES_START_HERE.ordinal()) &&
				(ordinal() < CONCAVE_SHAPES_END_HERE.ordinal()));
	}

	public boolean isCompound() {
		return (ordinal() == COMPOUND_SHAPE_PROXYTYPE.ordinal());
	}

	public boolean isInfinite() {
		return (ordinal() == STATIC_PLANE_PROXYTYPE.ordinal());
	}
	
}
