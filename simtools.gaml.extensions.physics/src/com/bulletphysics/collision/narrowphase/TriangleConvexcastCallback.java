/*******************************************************************************************************
 *
 * TriangleConvexcastCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.narrowphase.ConvexCast.CastResult;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.collision.shapes.TriangleShape;
import com.bulletphysics.linearmath.Transform;

/**
 *
 * @author jezek2
 */
public abstract class TriangleConvexcastCallback implements TriangleCallback {

	/** The convex shape. */
	public ConvexShape convexShape;
	
	/** The convex shape from. */
	public final Transform convexShapeFrom = new Transform();
	
	/** The convex shape to. */
	public final Transform convexShapeTo = new Transform();
	
	/** The triangle to world. */
	public final Transform triangleToWorld = new Transform();
	
	/** The hit fraction. */
	public float hitFraction;
	
	/** The triangle collision margin. */
	public float triangleCollisionMargin;

	/**
	 * Instantiates a new triangle convexcast callback.
	 *
	 * @param convexShape the convex shape
	 * @param convexShapeFrom the convex shape from
	 * @param convexShapeTo the convex shape to
	 * @param triangleToWorld the triangle to world
	 * @param triangleCollisionMargin the triangle collision margin
	 */
	public TriangleConvexcastCallback(final ConvexShape convexShape, final Transform convexShapeFrom,
			final Transform convexShapeTo, final Transform triangleToWorld, final float triangleCollisionMargin) {
		this.convexShape = convexShape;
		this.convexShapeFrom.set(convexShapeFrom);
		this.convexShapeTo.set(convexShapeTo);
		this.triangleToWorld.set(triangleToWorld);
		this.hitFraction = 1f;
		this.triangleCollisionMargin = triangleCollisionMargin;
	}

	@Override
	public void processTriangle( final Vector3f[] triangle, final int partId,
			final int triangleIndex) {
		TriangleShape triangleShape = new TriangleShape(triangle[0], triangle[1], triangle[2]);
		triangleShape.setMargin(triangleCollisionMargin);

		VoronoiSimplexSolver simplexSolver = new VoronoiSimplexSolver();
		// GjkEpaPenetrationDepthSolver gjkEpaPenetrationSolver = new GjkEpaPenetrationDepthSolver();

		// #define USE_SUBSIMPLEX_CONVEX_CAST 1
		// if you reenable USE_SUBSIMPLEX_CONVEX_CAST see commented out code below
		// #ifdef USE_SUBSIMPLEX_CONVEX_CAST
		// TODO: implement ContinuousConvexCollision
		SubsimplexConvexCast convexCaster = new SubsimplexConvexCast(convexShape, triangleShape, simplexSolver);
		// #else
		// //btGjkConvexCast convexCaster(m_convexShape,&triangleShape,&simplexSolver);
		// btContinuousConvexCollision
		// convexCaster(m_convexShape,&triangleShape,&simplexSolver,&gjkEpaPenetrationSolver);
		// #endif //#USE_SUBSIMPLEX_CONVEX_CAST

		CastResult castResult = new CastResult();
		castResult.fraction = 1f;
		if (convexCaster.calcTimeOfImpact( convexShapeFrom, convexShapeTo, triangleToWorld, triangleToWorld,
				castResult)) {
			// add hit
			if (castResult.normal.lengthSquared() > 0.0001f) {
				if (castResult.fraction < hitFraction) {

					/* btContinuousConvexCast's normal is already in world space */
					/*
					 * //#ifdef USE_SUBSIMPLEX_CONVEX_CAST // rotate normal into worldspace
					 * convexShapeFrom.basis.transform(castResult.normal); //#endif //USE_SUBSIMPLEX_CONVEX_CAST
					 */
					castResult.normal.normalize();

					reportHit(castResult.normal, castResult.hitPoint, castResult.fraction, partId, triangleIndex);
				}
			}
		}
	}

	/**
	 * Report hit.
	 *
	 * @param hitNormalLocal the hit normal local
	 * @param hitPointLocal the hit point local
	 * @param hitFraction the hit fraction
	 * @param partId the part id
	 * @param triangleIndex the triangle index
	 * @return the float
	 */
	public abstract float reportHit(Vector3f hitNormalLocal, Vector3f hitPointLocal, float hitFraction, int partId,
			int triangleIndex);

}
