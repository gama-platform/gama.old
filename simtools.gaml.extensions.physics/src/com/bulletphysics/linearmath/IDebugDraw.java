/*******************************************************************************************************
 *
 * IDebugDraw.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.DynamicsWorld;

/**
 * IDebugDraw interface class allows hooking up a debug renderer to visually debug simulations.
 * <p>
 *
 * Typical use case: create a debug drawer object, and assign it to a {@link CollisionWorld} or {@link DynamicsWorld}
 * using setDebugDrawer and call debugDrawWorld.
 * <p>
 *
 * A class that implements the IDebugDraw interface has to implement the drawLine method at a minimum.
 *
 * @author jezek2
 */
public abstract class IDebugDraw {

	// protected final BulletStack stack = BulletStack.get();

	/**
	 * Draw line.
	 *
	 * @param from the from
	 * @param to the to
	 * @param color the color
	 */
	public abstract void drawLine(Vector3f from, Vector3f to, Vector3f color);

	/**
	 * Draw triangle.
	 *
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @param v2 the v 2
	 * @param n0 the n 0
	 * @param n1 the n 1
	 * @param n2 the n 2
	 * @param color the color
	 * @param alpha the alpha
	 */
	public void drawTriangle(final Vector3f v0, final Vector3f v1, final Vector3f v2, final Vector3f n0,
			final Vector3f n1, final Vector3f n2, final Vector3f color, final float alpha) {
		drawTriangle(v0, v1, v2, color, alpha);
	}

	/**
	 * Draw triangle.
	 *
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @param v2 the v 2
	 * @param color the color
	 * @param alpha the alpha
	 */
	public void drawTriangle(final Vector3f v0, final Vector3f v1, final Vector3f v2, final Vector3f color,
			final float alpha) {
		drawLine(v0, v1, color);
		drawLine(v1, v2, color);
		drawLine(v2, v0, color);
	}

	/**
	 * Draw contact point.
	 *
	 * @param PointOnB the point on B
	 * @param normalOnB the normal on B
	 * @param distance the distance
	 * @param lifeTime the life time
	 * @param color the color
	 */
	public abstract void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB, float distance, int lifeTime,
			Vector3f color);

	/**
	 * Report error warning.
	 *
	 * @param warningString the warning string
	 */
	public abstract void reportErrorWarning(String warningString);

	/**
	 * Draw 3 d text.
	 *
	 * @param location the location
	 * @param textString the text string
	 */
	public abstract void draw3dText(Vector3f location, String textString);

	/**
	 * Sets the debug mode.
	 *
	 * @param debugMode the new debug mode
	 */
	public abstract void setDebugMode(int debugMode);

	/**
	 * Gets the debug mode.
	 *
	 * @return the debug mode
	 */
	public abstract int getDebugMode();

	/**
	 * Draw aabb.
	 *
	 * @param from the from
	 * @param to the to
	 * @param color the color
	 */
	public void drawAabb(final Vector3f from, final Vector3f to, final Vector3f color) {
		Vector3f halfExtents = VECTORS.get(to);
		halfExtents.sub(from);
		halfExtents.scale(0.5f);

		Vector3f center = VECTORS.get(to);
		center.add(from);
		center.scale(0.5f);

		int i, j;

		Vector3f edgecoord = VECTORS.get();
		edgecoord.set(1f, 1f, 1f);
		Vector3f pa = VECTORS.get(), pb = VECTORS.get();
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 3; j++) {
				pa.set(edgecoord.x * halfExtents.x, edgecoord.y * halfExtents.y, edgecoord.z * halfExtents.z);
				pa.add(center);

				int othercoord = j % 3;

				VectorUtil.mulCoord(edgecoord, othercoord, -1f);
				pb.set(edgecoord.x * halfExtents.x, edgecoord.y * halfExtents.y, edgecoord.z * halfExtents.z);
				pb.add(center);

				drawLine(pa, pb, color);
			}
			edgecoord.set(-1f, -1f, -1f);
			if (i < 3) { VectorUtil.mulCoord(edgecoord, i, -1f); }
		}
	}
}
