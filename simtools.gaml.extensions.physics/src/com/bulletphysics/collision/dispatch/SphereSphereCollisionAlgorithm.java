/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
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

	private boolean ownManifold;
	private PersistentManifold manifoldPtr;
	private final Transform tmpTrans1 = TRANSFORMS.get();
	private final Transform tmpTrans2 = TRANSFORMS.get();
	private final Vector3f tmp = VECTORS.get();
	private final Vector3f pos0 = VECTORS.get();
	private final Vector3f pos1 = VECTORS.get();

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
