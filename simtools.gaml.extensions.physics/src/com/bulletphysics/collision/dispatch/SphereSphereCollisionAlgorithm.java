/*******************************************************************************************************
 *
 * SphereSphereCollisionAlgorithm.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import static com.bulletphysics.Pools.SPHERE_SPHERE_COLLISIONS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * Provides collision detection between two spheres.
 *
 * @author jezek2
 */
public class SphereSphereCollisionAlgorithm extends CollisionAlgorithm {

	/** The own manifold. */
	private boolean ownManifold;
	
	/** The manifold ptr. */
	private PersistentManifold manifoldPtr;
	
	/** The tmp trans 1. */
	private final Transform tmpTrans1 = TRANSFORMS.get();
	
	/** The tmp trans 2. */
	private final Transform tmpTrans2 = TRANSFORMS.get();
	
	/** The tmp. */
	private final Vector3f tmp = VECTORS.get();
	
	/** The pos 0. */
	private final Vector3f pos0 = VECTORS.get();
	
	/** The pos 1. */
	private final Vector3f pos1 = VECTORS.get();

	/**
	 * Inits the.
	 *
	 * @param mf the mf
	 * @param ci the ci
	 * @param col0 the col 0
	 * @param col1 the col 1
	 */
	public void init(final PersistentManifold mf, final CollisionAlgorithmConstructionInfo ci,
			final CollisionObject col0, final CollisionObject col1) {
		super.init(ci);
		manifoldPtr = mf;

		if (manifoldPtr == null) {
			manifoldPtr = dispatcher.getNewManifold(col0, col1);
			ownManifold = true;
		}
	}

	@Override
	public void init(final CollisionAlgorithmConstructionInfo ci) {
		super.init(ci);
	}

	@Override
	public void destroy() {
		if (ownManifold) {
			if (manifoldPtr != null) { dispatcher.releaseManifold( manifoldPtr); }
			manifoldPtr = null;
		}
		TRANSFORMS.release(tmpTrans1, tmpTrans2);
		VECTORS.release(tmp, pos0, pos1);
	}

	@Override
	public void processCollision( final CollisionObject col0, final CollisionObject col1,
			final ManifoldResult resultOut) {
		if (manifoldPtr == null) return;

		Vector3f diff = VECTORS.get();

		resultOut.setPersistentManifold(manifoldPtr);

		SphereShape sphere0 = (SphereShape) col0.getCollisionShape();
		SphereShape sphere1 = (SphereShape) col1.getCollisionShape();

		diff.sub(col0.getWorldTransform(tmpTrans1).origin, col1.getWorldTransform(tmpTrans2).origin);

		float len = diff.length();
		float radius0 = sphere0.getRadius();
		float radius1 = sphere1.getRadius();

		// #ifdef CLEAR_MANIFOLD
		// manifoldPtr.clearManifold(); // don't do this, it disables warmstarting
		// #endif

		// if distance positive, don't generate a new contact
		if (len > radius0 + radius1) {
			resultOut.refreshContactPoints();
			// TRANSFORMS.release(tmpTrans1, tmpTrans2);
			VECTORS.release(diff);
			return;
		}
		// distance (negative means penetration)
		float dist = len - (radius0 + radius1);

		Vector3f normalOnSurfaceB = VECTORS.get();
		normalOnSurfaceB.set(1f, 0f, 0f);
		if (len > BulletGlobals.FLT_EPSILON) { normalOnSurfaceB.scale(1f / len, diff); }

		// point on A (worldspace)

		tmp.scale(radius0, normalOnSurfaceB);
		pos0.sub(col0.getWorldTransform(tmpTrans1).origin, tmp);

		// point on B (worldspace)
		// Vector3f pos1 = VECTORS.get();
		tmp.scale(radius1, normalOnSurfaceB);
		pos1.add(col1.getWorldTransform(tmpTrans2).origin, tmp);

		// report a contact. internally this will be kept persistent, and contact reduction is done
		resultOut.addContactPoint( normalOnSurfaceB, pos1, dist);

		resultOut.refreshContactPoints();
		// TRANSFORMS.release(tmpTrans1, tmpTrans2);
		VECTORS.release(/* tmp, pos0, pos1, */normalOnSurfaceB, diff);

	}

	@Override
	public float calculateTimeOfImpact( final CollisionObject body0,
			final CollisionObject body1, final ManifoldResult resultOut) {
		return 1f;
	}

	@Override
	public void getAllContactManifolds(final ArrayList<PersistentManifold> manifoldArray) {
		if (manifoldPtr != null && ownManifold) { manifoldArray.add(manifoldPtr); }
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class CreateFunc.
	 */
	public static class CreateFunc implements CollisionAlgorithmCreateFunc {
		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			SphereSphereCollisionAlgorithm algo = SPHERE_SPHERE_COLLISIONS.get();
			algo.init(null, ci, body0, body1);
			return algo;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {
			SPHERE_SPHERE_COLLISIONS.release((SphereSphereCollisionAlgorithm) algo);
		}
	}

}
