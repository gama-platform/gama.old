/*******************************************************************************************************
 *
 * UniformScalingShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The child convex shape. */
	private final ConvexShape childConvexShape;
	
	/** The uniform scaling factor. */
	private final float uniformScalingFactor;

	/**
	 * Instantiates a new uniform scaling shape.
	 *
	 * @param convexChildShape the convex child shape
	 * @param uniformScalingFactor the uniform scaling factor
	 */
	public UniformScalingShape(final ConvexShape convexChildShape, final float uniformScalingFactor) {
		this.childConvexShape = convexChildShape;
		this.uniformScalingFactor = uniformScalingFactor;
	}

	/**
	 * Gets the uniform scaling factor.
	 *
	 * @return the uniform scaling factor
	 */
	public float getUniformScalingFactor() {
		return uniformScalingFactor;
	}

	/**
	 * Gets the child shape.
	 *
	 * @return the child shape
	 */
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
