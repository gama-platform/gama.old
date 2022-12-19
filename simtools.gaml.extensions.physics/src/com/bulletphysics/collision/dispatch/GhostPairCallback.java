/*******************************************************************************************************
 *
 * GhostPairCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.broadphase.OverlappingPairCallback;

/**
 * GhostPairCallback interfaces and forwards adding and removal of overlapping pairs from the
 * {@link BroadphaseInterface} to {@link GhostObject}.
 *
 * @author tomrbryn
 */
public class GhostPairCallback implements OverlappingPairCallback {

	@Override
	public BroadphasePair addOverlappingPair(final BroadphaseProxy proxy0, final BroadphaseProxy proxy1) {
		CollisionObject colObj0 = (CollisionObject) proxy0.clientObject;
		CollisionObject colObj1 = (CollisionObject) proxy1.clientObject;
		GhostObject ghost0 = GhostObject.upcast(colObj0);
		GhostObject ghost1 = GhostObject.upcast(colObj1);

		if (ghost0 != null) { ghost0.addOverlappingObjectInternal(proxy1, proxy0); }
		if (ghost1 != null) { ghost1.addOverlappingObjectInternal(proxy0, proxy1); }
		return null;
	}

	@Override
	public Object removeOverlappingPair( final BroadphaseProxy proxy0,
			final BroadphaseProxy proxy1, final Dispatcher dispatcher) {
		CollisionObject colObj0 = (CollisionObject) proxy0.clientObject;
		CollisionObject colObj1 = (CollisionObject) proxy1.clientObject;
		GhostObject ghost0 = GhostObject.upcast(colObj0);
		GhostObject ghost1 = GhostObject.upcast(colObj1);

		if (ghost0 != null) { ghost0.removeOverlappingObjectInternal( proxy1, dispatcher, proxy0); }
		if (ghost1 != null) { ghost1.removeOverlappingObjectInternal( proxy0, dispatcher, proxy1); }
		return null;
	}

	@Override
	public void removeOverlappingPairsContainingProxy( final BroadphaseProxy proxy0,
			final Dispatcher dispatcher) {
		assert false;

		// need to keep track of all ghost objects and call them here
		// hashPairCache.removeOverlappingPairsContainingProxy(proxy0, dispatcher);
	}

}
