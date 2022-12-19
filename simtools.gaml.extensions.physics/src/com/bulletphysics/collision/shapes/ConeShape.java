/*******************************************************************************************************
 *
 * ConeShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * ConeShape implements a cone shape primitive, centered around the origin and aligned with the Y axis. The
 * {@link ConeShapeX} is aligned around the X axis and {@link ConeShapeZ} around the Z axis.
 *
 * @author jezek2
 */
public class ConeShape extends ConvexInternalShape {

	/** The sin angle. */
	private final float sinAngle;
	
	/** The radius. */
	private final float radius;
	
	/** The height. */
	private final float height;
	
	/** The cone indices. */
	private final int[] coneIndices = new int[3];

	/**
	 * Instantiates a new cone shape.
	 *
	 * @param radius the radius
	 * @param height the height
	 */
	public ConeShape(final float radius, final float height) {
		this.radius = radius;
		this.height = height;
		setConeUpIndex(1);
		sinAngle = radius / (float) Math.sqrt(this.radius * this.radius + this.height * this.height);
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * Cone local support.
	 *
	 * @param v the v
	 * @param out the out
	 * @return the vector 3 f
	 */
	private Vector3f coneLocalSupport(final Vector3f v, final Vector3f out) {
		float halfHeight = height * 0.5f;

		if (VectorUtil.getCoord(v, coneIndices[1]) > v.length() * sinAngle) {
			VectorUtil.setCoord(out, coneIndices[0], 0f);
			VectorUtil.setCoord(out, coneIndices[1], halfHeight);
			VectorUtil.setCoord(out, coneIndices[2], 0f);
			return out;
		} else {
			float v0 = VectorUtil.getCoord(v, coneIndices[0]);
			float v2 = VectorUtil.getCoord(v, coneIndices[2]);
			float s = (float) Math.sqrt(v0 * v0 + v2 * v2);
			if (s > BulletGlobals.FLT_EPSILON) {
				float d = radius / s;
				VectorUtil.setCoord(out, coneIndices[0], VectorUtil.getCoord(v, coneIndices[0]) * d);
				VectorUtil.setCoord(out, coneIndices[1], -halfHeight);
				VectorUtil.setCoord(out, coneIndices[2], VectorUtil.getCoord(v, coneIndices[2]) * d);
				return out;
			} else {
				VectorUtil.setCoord(out, coneIndices[0], 0f);
				VectorUtil.setCoord(out, coneIndices[1], -halfHeight);
				VectorUtil.setCoord(out, coneIndices[2], 0f);
				return out;
			}
		}
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec, final Vector3f out) {
		return coneLocalSupport(vec, out);
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		for (int i = 0; i < numVectors; i++) {
			Vector3f vec = vectors[i];
			coneLocalSupport(vec, supportVerticesOut[i]);
		}
	}

	@Override
	public Vector3f localGetSupportingVertex(final Vector3f vec, final Vector3f out) {
		Vector3f supVertex = coneLocalSupport(vec, out);
		if (getMargin() != 0f) {
			Vector3f vecnorm = VECTORS.get(vec);
			if (vecnorm.lengthSquared() < BulletGlobals.FLT_EPSILON * BulletGlobals.FLT_EPSILON) {
				vecnorm.set(-1f, -1f, -1f);
			}
			vecnorm.normalize();
			supVertex.scaleAdd(getMargin(), vecnorm, supVertex);
			VECTORS.release(vecnorm);
		}
		return supVertex;
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.CONE_SHAPE_PROXYTYPE;
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		Transform identity = TRANSFORMS.get();
		identity.setIdentity();
		Vector3f aabbMin = VECTORS.get(), aabbMax = VECTORS.get();
		getAabb(identity, aabbMin, aabbMax);

		Vector3f halfExtents = VECTORS.get();
		halfExtents.sub(aabbMax, aabbMin);
		halfExtents.scale(0.5f);

		float margin = getMargin();

		float lx = 2f * (halfExtents.x + margin);
		float ly = 2f * (halfExtents.y + margin);
		float lz = 2f * (halfExtents.z + margin);
		float x2 = lx * lx;
		float y2 = ly * ly;
		float z2 = lz * lz;
		float scaledmass = mass * 0.08333333f;

		inertia.set(y2 + z2, x2 + z2, x2 + y2);
		inertia.scale(scaledmass);

		// inertia.x() = scaledmass * (y2+z2);
		// inertia.y() = scaledmass * (x2+z2);
		// inertia.z() = scaledmass * (x2+y2);
		TRANSFORMS.release(identity);
		VECTORS.release(aabbMin, aabbMax, halfExtents);
	}

	@Override
	public String getName() {
		return "Cone";
	}

	/**
	 * Sets the cone up index.
	 *
	 * @param upIndex the new cone up index
	 */
	// choose upAxis index
	protected void setConeUpIndex(final int upIndex) {
		switch (upIndex) {
			case 0:
				coneIndices[0] = 1;
				coneIndices[1] = 0;
				coneIndices[2] = 2;
				break;

			case 1:
				coneIndices[0] = 0;
				coneIndices[1] = 1;
				coneIndices[2] = 2;
				break;

			case 2:
				coneIndices[0] = 0;
				coneIndices[1] = 2;
				coneIndices[2] = 1;
				break;

			default:
				assert false;
		}
	}

	/**
	 * Gets the cone up index.
	 *
	 * @return the cone up index
	 */
	public int getConeUpIndex() {
		return coneIndices[1];
	}

}
