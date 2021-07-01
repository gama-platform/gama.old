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

import static com.bulletphysics.Pools.COMPOUND_COLLISIONS;
import static com.bulletphysics.Pools.TRANSFORMS;

import java.util.ArrayList;

import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;

/**
 * CompoundCollisionAlgorithm supports collision between {@link CompoundShape}s and other collision shapes.
 *
 * @author jezek2
 */
public class CompoundCollisionAlgorithm extends CollisionAlgorithm {

	private final ArrayList<CollisionAlgorithm> childCollisionAlgorithms = new ArrayList<>();
	private boolean isSwapped;

	public void init(final CollisionAlgorithmConstructionInfo ci, final CollisionObject body0,
			final CollisionObject body1, final boolean isSwapped) {
		super.init(ci);

		this.isSwapped = isSwapped;

		CollisionObject colObj = isSwapped ? body1 : body0;
		CollisionObject otherObj = isSwapped ? body0 : body1;
		assert colObj.getCollisionShape().isCompound();

		CompoundShape compoundShape = (CompoundShape) colObj.getCollisionShape();
		int numChildren = compoundShape.getNumChildShapes();
		int i;

		// childCollisionAlgorithms.resize(numChildren);
		for (i = 0; i < numChildren; i++) {
			CollisionShape tmpShape = colObj.getCollisionShape();
			CollisionShape childShape = compoundShape.getChildShape(i);
			colObj.internalSetTemporaryCollisionShape(childShape);
			childCollisionAlgorithms.add(ci.dispatcher1.findAlgorithm(colObj, otherObj));
			colObj.internalSetTemporaryCollisionShape(tmpShape);
		}
	}

	@Override
	public void destroy() {
		int numChildren = childCollisionAlgorithms.size();
		for (int i = 0; i < numChildren; i++) {
			// childCollisionAlgorithms.get(i).destroy();
			dispatcher.freeCollisionAlgorithm( childCollisionAlgorithms.get(i));
		}
		childCollisionAlgorithms.clear();
	}

	@Override
	public void processCollision(final CollisionObject body0, final CollisionObject body1,
			final ManifoldResult resultOut) {
		CollisionObject colObj = isSwapped ? body1 : body0;
		CollisionObject otherObj = isSwapped ? body0 : body1;

		assert colObj.getCollisionShape().isCompound();
		CompoundShape compoundShape = (CompoundShape) colObj.getCollisionShape();

		// We will use the OptimizedBVH, AABB tree to cull potential child-overlaps
		// If both proxies are Compound, we will deal with that directly, by performing sequential/parallel tree
		// traversals
		// given Proxy0 and Proxy1, if both have a tree, Tree0 and Tree1, this means:
		// determine overlapping nodes of Proxy1 using Proxy0 AABB against Tree1
		// then use each overlapping node AABB against Tree0
		// and vise versa.

		// Transform tmpTrans = TRANSFORMS.get();
		Transform orgTrans = TRANSFORMS.get();
		Transform childTrans = TRANSFORMS.get();
		Transform orgInterpolationTrans = TRANSFORMS.get();
		Transform newChildWorldTrans = TRANSFORMS.get();

		int numChildren = childCollisionAlgorithms.size();
		int i;
		for (i = 0; i < numChildren; i++) {
			// temporarily exchange parent btCollisionShape with childShape, and recurse
			CollisionShape childShape = compoundShape.getChildShape(i);

			// backup
			colObj.getWorldTransform(orgTrans);
			colObj.getInterpolationWorldTransform(orgInterpolationTrans);

			compoundShape.getChildTransform(i, childTrans);
			newChildWorldTrans.mul(orgTrans, childTrans);
			colObj.setWorldTransform(newChildWorldTrans);
			colObj.setInterpolationWorldTransform(newChildWorldTrans);

			// the contactpoint is still projected back using the original inverted worldtrans
			CollisionShape tmpShape = colObj.getCollisionShape();
			colObj.internalSetTemporaryCollisionShape(childShape);
			childCollisionAlgorithms.get(i).processCollision(colObj, otherObj, resultOut);
			// revert back
			colObj.internalSetTemporaryCollisionShape(tmpShape);
			colObj.setWorldTransform(orgTrans);
			colObj.setInterpolationWorldTransform(orgInterpolationTrans);
		}
		TRANSFORMS.release(orgInterpolationTrans, orgTrans, childTrans, newChildWorldTrans);
	}

	@Override
	public float calculateTimeOfImpact(final CollisionObject body0, final CollisionObject body1,
			final ManifoldResult resultOut) {
		CollisionObject colObj = isSwapped ? body1 : body0;
		CollisionObject otherObj = isSwapped ? body0 : body1;

		assert colObj.getCollisionShape().isCompound();

		CompoundShape compoundShape = (CompoundShape) colObj.getCollisionShape();

		// We will use the OptimizedBVH, AABB tree to cull potential child-overlaps
		// If both proxies are Compound, we will deal with that directly, by performing sequential/parallel tree
		// traversals
		// given Proxy0 and Proxy1, if both have a tree, Tree0 and Tree1, this means:
		// determine overlapping nodes of Proxy1 using Proxy0 AABB against Tree1
		// then use each overlapping node AABB against Tree0
		// and vise versa.

		Transform tmpTrans = TRANSFORMS.get();
		Transform orgTrans = TRANSFORMS.get();
		Transform childTrans = TRANSFORMS.get();
		float hitFraction = 1f;

		int numChildren = childCollisionAlgorithms.size();
		int i;
		for (i = 0; i < numChildren; i++) {
			// temporarily exchange parent btCollisionShape with childShape, and recurse
			CollisionShape childShape = compoundShape.getChildShape(i);

			// backup
			colObj.getWorldTransform(orgTrans);

			compoundShape.getChildTransform(i, childTrans);
			// btTransform newChildWorldTrans = orgTrans*childTrans ;
			tmpTrans.set(orgTrans);
			tmpTrans.mul(childTrans);
			colObj.setWorldTransform(tmpTrans);

			CollisionShape tmpShape = colObj.getCollisionShape();
			colObj.internalSetTemporaryCollisionShape(childShape);
			float frac = childCollisionAlgorithms.get(i).calculateTimeOfImpact(colObj, otherObj, resultOut);
			if (frac < hitFraction) { hitFraction = frac; }
			// revert back
			colObj.internalSetTemporaryCollisionShape(tmpShape);
			colObj.setWorldTransform(orgTrans);
		}
		TRANSFORMS.release(tmpTrans, orgTrans, childTrans);
		return hitFraction;
	}

	@Override
	public void getAllContactManifolds(final ArrayList<PersistentManifold> manifoldArray) {
		for (CollisionAlgorithm childCollisionAlgorithm : childCollisionAlgorithms) {
			childCollisionAlgorithm.getAllContactManifolds(manifoldArray);
		}
	}

	////////////////////////////////////////////////////////////////////////////

	public static class CreateFunc implements CollisionAlgorithmCreateFunc {

		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			CompoundCollisionAlgorithm algo = COMPOUND_COLLISIONS.get();
			algo.init(ci, body0, body1, false);
			return algo;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {
			COMPOUND_COLLISIONS.release((CompoundCollisionAlgorithm) algo);
		}
	}

	public static class SwappedCreateFunc implements CollisionAlgorithmCreateFunc {

		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			CompoundCollisionAlgorithm algo = COMPOUND_COLLISIONS.get();
			algo.init(ci, body0, body1, true);
			return algo;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {
			COMPOUND_COLLISIONS.release((CompoundCollisionAlgorithm) algo);
		}
	}

}
