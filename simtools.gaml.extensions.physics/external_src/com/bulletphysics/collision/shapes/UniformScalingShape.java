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

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.Transform;

/**
 * UniformScalingShape allows to re-use uniform scaled instances of {@link ConvexShape} in a memory efficient way.
 * Istead of using {@link UniformScalingShape}, it is better to use the non-uniform setLocalScaling method on convex
 * shapes that implement it.
 *
 * @author jezek2
 */
public class UniformScalingShape implements ConvexShape {

	private final ConvexShape childConvexShape;
	private final float uniformScalingFactor;

	public UniformScalingShape(final ConvexShape convexChildShape, final float uniformScalingFactor) {
		this.childConvexShape = convexChildShape;
		this.uniformScalingFactor = uniformScalingFactor;
	}

	public float getUniformScalingFactor() {
		return uniformScalingFactor;
	}

	public ConvexShape getChildShape() {
		return childConvexShape;
	}

	@Override
	public Vector3f localGetSupportingVertex(final Vector3f vec, final Vector3f out) {
		childConvexShape.localGetSupportingVertex(vec, out);
		out.scale(uniformScalingFactor);
		return out;
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec, final Vector3f out) {
		childConvexShape.localGetSupportingVertexWithoutMargin(vec, out);
		out.scale(uniformScalingFactor);
		return out;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		childConvexShape.batchedUnitVectorGetSupportingVertexWithoutMargin(vectors, supportVerticesOut, numVectors);
		for (int i = 0; i < numVectors; i++) {
			supportVerticesOut[i].scale(uniformScalingFactor);
		}
	}

	@Override
	public void getAabbSlow(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		childConvexShape.getAabbSlow(t, aabbMin, aabbMax);
		Vector3f aabbCenter = VECTORS.get();
		aabbCenter.add(aabbMax, aabbMin);
		aabbCenter.scale(0.5f);

		Vector3f scaledAabbHalfExtends = VECTORS.get();
		scaledAabbHalfExtends.sub(aabbMax, aabbMin);
		scaledAabbHalfExtends.scale(0.5f * uniformScalingFactor);

		aabbMin.sub(aabbCenter, scaledAabbHalfExtends);
		aabbMax.add(aabbCenter, scaledAabbHalfExtends);
		VECTORS.release(aabbCenter, scaledAabbHalfExtends);
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		childConvexShape.setLocalScaling( scaling);
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f out) {
		childConvexShape.getLocalScaling(out);
		return out;
	}

	@Override
	public void setMargin(final float margin) {
		childConvexShape.setMargin(margin);
	}

	@Override
	public float getMargin() {
		return childConvexShape.getMargin() * uniformScalingFactor;
	}

	@Override
	public int getNumPreferredPenetrationDirections() {
		return childConvexShape.getNumPreferredPenetrationDirections();
	}

	@Override
	public void getPreferredPenetrationDirection(final int index, final Vector3f penetrationVector) {
		childConvexShape.getPreferredPenetrationDirection(index, penetrationVector);
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		childConvexShape.getAabb(t, aabbMin, aabbMax);
		Vector3f aabbCenter = VECTORS.get();
		aabbCenter.add(aabbMax, aabbMin);
		aabbCenter.scale(0.5f);

		Vector3f scaledAabbHalfExtends = VECTORS.get();
		scaledAabbHalfExtends.sub(aabbMax, aabbMin);
		scaledAabbHalfExtends.scale(0.5f * uniformScalingFactor);

		aabbMin.sub(aabbCenter, scaledAabbHalfExtends);
		aabbMax.add(aabbCenter, scaledAabbHalfExtends);
		VECTORS.release(aabbCenter, scaledAabbHalfExtends);

	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.UNIFORM_SCALING_SHAPE_PROXYTYPE;
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		// this linear upscaling is not realistic, but we don't deal with large mass ratios...
		childConvexShape.calculateLocalInertia(mass, inertia);
		inertia.scale(uniformScalingFactor);
	}

	@Override
	public String getName() {
		return "UniformScalingShape";
	}

}
