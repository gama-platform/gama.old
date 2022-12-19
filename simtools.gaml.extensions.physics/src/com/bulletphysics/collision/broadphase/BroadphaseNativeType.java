/*******************************************************************************************************
 *
 * BroadphaseNativeType.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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
	
	/** The box shape proxytype. */
	// polyhedral convex shapes:
	BOX_SHAPE_PROXYTYPE,
	
	/** The triangle shape proxytype. */
	TRIANGLE_SHAPE_PROXYTYPE,
	
	/** The tetrahedral shape proxytype. */
	TETRAHEDRAL_SHAPE_PROXYTYPE,
	
	/** The convex trianglemesh shape proxytype. */
	CONVEX_TRIANGLEMESH_SHAPE_PROXYTYPE,
	
	/** The convex hull shape proxytype. */
	CONVEX_HULL_SHAPE_PROXYTYPE,
	
	/** The implicit convex shapes start here. */
	// implicit convex shapes:
	IMPLICIT_CONVEX_SHAPES_START_HERE,
	
	/** The sphere shape proxytype. */
	SPHERE_SHAPE_PROXYTYPE,
	
	/** The multi sphere shape proxytype. */
	MULTI_SPHERE_SHAPE_PROXYTYPE,
	
	/** The capsule shape proxytype. */
	CAPSULE_SHAPE_PROXYTYPE,
	
	/** The cone shape proxytype. */
	CONE_SHAPE_PROXYTYPE,
	
	/** The convex shape proxytype. */
	CONVEX_SHAPE_PROXYTYPE,
	
	/** The cylinder shape proxytype. */
	CYLINDER_SHAPE_PROXYTYPE,
	
	/** The uniform scaling shape proxytype. */
	UNIFORM_SCALING_SHAPE_PROXYTYPE,
	
	/** The minkowski sum shape proxytype. */
	MINKOWSKI_SUM_SHAPE_PROXYTYPE,
	
	/** The minkowski difference shape proxytype. */
	MINKOWSKI_DIFFERENCE_SHAPE_PROXYTYPE,
	
	/** The concave shapes start here. */
	// concave shapes:
	CONCAVE_SHAPES_START_HERE,
	
	/** The triangle mesh shape proxytype. */
	// keep all the convex shapetype below here, for the check IsConvexShape in broadphase proxy!
	TRIANGLE_MESH_SHAPE_PROXYTYPE,
	
	/** The scaled triangle mesh shape proxytype. */
	SCALED_TRIANGLE_MESH_SHAPE_PROXYTYPE,
	
	/** The fast concave mesh proxytype. */
	// used for demo integration FAST/Swift collision library and Bullet:
	FAST_CONCAVE_MESH_PROXYTYPE,
	
	/** The terrain shape proxytype. */
	// terrain:
	TERRAIN_SHAPE_PROXYTYPE,
	
	/** The gimpact shape proxytype. */
	// used for GIMPACT Trimesh integration:
	GIMPACT_SHAPE_PROXYTYPE,
	
	/** The multimaterial triangle mesh proxytype. */
	// multimaterial mesh:
	MULTIMATERIAL_TRIANGLE_MESH_PROXYTYPE,
	
	/** The empty shape proxytype. */
	EMPTY_SHAPE_PROXYTYPE,
	
	/** The static plane proxytype. */
	STATIC_PLANE_PROXYTYPE,
	
	/** The concave shapes end here. */
	CONCAVE_SHAPES_END_HERE,
	
	/** The compound shape proxytype. */
	COMPOUND_SHAPE_PROXYTYPE,
	
	/** The softbody shape proxytype. */
	SOFTBODY_SHAPE_PROXYTYPE,

	/** The invalid shape proxytype. */
	INVALID_SHAPE_PROXYTYPE,
	
	/** The max broadphase collision types. */
	MAX_BROADPHASE_COLLISION_TYPES;
	
	/** The values. */
	private static BroadphaseNativeType[] values = values();
	
	/**
	 * For value.
	 *
	 * @param value the value
	 * @return the broadphase native type
	 */
	public static BroadphaseNativeType forValue(int value) {
		return values[value];
	}
	
	/**
	 * Checks if is polyhedral.
	 *
	 * @return true, if is polyhedral
	 */
	public boolean isPolyhedral() {
		return (ordinal() < IMPLICIT_CONVEX_SHAPES_START_HERE.ordinal());
	}

	/**
	 * Checks if is convex.
	 *
	 * @return true, if is convex
	 */
	public boolean isConvex() {
		return (ordinal() < CONCAVE_SHAPES_START_HERE.ordinal());
	}

	/**
	 * Checks if is concave.
	 *
	 * @return true, if is concave
	 */
	public boolean isConcave() {
		return ((ordinal() > CONCAVE_SHAPES_START_HERE.ordinal()) &&
				(ordinal() < CONCAVE_SHAPES_END_HERE.ordinal()));
	}

	/**
	 * Checks if is compound.
	 *
	 * @return true, if is compound
	 */
	public boolean isCompound() {
		return (ordinal() == COMPOUND_SHAPE_PROXYTYPE.ordinal());
	}

	/**
	 * Checks if is infinite.
	 *
	 * @return true, if is infinite
	 */
	public boolean isInfinite() {
		return (ordinal() == STATIC_PLANE_PROXYTYPE.ordinal());
	}
	
}
