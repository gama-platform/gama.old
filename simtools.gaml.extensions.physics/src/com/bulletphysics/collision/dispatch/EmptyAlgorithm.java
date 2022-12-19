/*******************************************************************************************************
 *
 * EmptyAlgorithm.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import java.util.ArrayList;

import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.narrowphase.PersistentManifold;

/**
 * Empty algorithm, used as fallback when no collision algorithm is found for given shape type pair.
 *
 * @author jezek2
 */
public class EmptyAlgorithm extends CollisionAlgorithm {

	/** The Constant INSTANCE. */
	private static final EmptyAlgorithm INSTANCE = new EmptyAlgorithm();

	@Override
	public void destroy() {}

	@Override
	public void processCollision( final CollisionObject body0, final CollisionObject body1,
			final ManifoldResult resultOut) {}

	@Override
	public float calculateTimeOfImpact( final CollisionObject body0,
			final CollisionObject body1, final ManifoldResult resultOut) {
		return 1f;
	}

	@Override
	public void getAllContactManifolds(final ArrayList<PersistentManifold> manifoldArray) {}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class CreateFunc.
	 */
	public static class CreateFunc implements CollisionAlgorithmCreateFunc {
		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			return INSTANCE;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {}
	}

}
