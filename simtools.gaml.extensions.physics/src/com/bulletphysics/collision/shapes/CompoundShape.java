/*******************************************************************************************************
 *
 * CompoundShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

// JAVA NOTE: CompoundShape from 2.71

/**
 * CompoundShape allows to store multiple other {@link CollisionShape}s. This allows for moving concave collision
 * objects. This is more general than the {@link BvhTriangleMeshShape}.
 *
 * @author jezek2
 */
public class CompoundShape implements CollisionShape {

	/** The children. */
	private final ArrayList<CompoundShapeChild> children = new ArrayList<>();
	
	/** The local aabb min. */
	private final Vector3f localAabbMin = new Vector3f(1e30f, 1e30f, 1e30f);
	
	/** The local aabb max. */
	private final Vector3f localAabbMax = new Vector3f(-1e30f, -1e30f, -1e30f);

	/** The aabb tree. */
	private final OptimizedBvh aabbTree = null;

	/** The collision margin. */
	private float collisionMargin = 0f;
	
	/** The local scaling. */
	protected final Vector3f localScaling = new Vector3f(1f, 1f, 1f);

	/**
	 * Adds the child shape.
	 *
	 * @param localTransform the local transform
	 * @param shape the shape
	 */
	public void addChildShape(final Transform localTransform, final CollisionShape shape) {
		// m_childTransforms.push_back(localTransform);
		// m_childShapes.push_back(shape);
		CompoundShapeChild child = new CompoundShapeChild();
		child.transform.set(localTransform);
		child.childShape = shape;
		child.childShapeType = shape.getShapeType();
		child.childMargin = shape.getMargin();

		children.add(child);

		// extend the local aabbMin/aabbMax
		Vector3f _localAabbMin = VECTORS.get(), _localAabbMax = VECTORS.get();
		shape.getAabb(localTransform, _localAabbMin, _localAabbMax);

		// JAVA NOTE: rewritten
		// for (int i=0;i<3;i++)
		// {
		// if (this.localAabbMin[i] > _localAabbMin[i])
		// {
		// this.localAabbMin[i] = _localAabbMin[i];
		// }
		// if (this.localAabbMax[i] < _localAabbMax[i])
		// {
		// this.localAabbMax[i] = _localAabbMax[i];
		// }
		// }
		VectorUtil.setMin(this.localAabbMin, _localAabbMin);
		VectorUtil.setMax(this.localAabbMax, _localAabbMax);
	}

	/**
	 * Remove all children shapes that contain the specified shape.
	 */
	public void removeChildShape(final CollisionShape shape) {
		boolean done_removing;

		// Find the children containing the shape specified, and remove those children.
		do {
			done_removing = true;

			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).childShape == shape) {
					children.remove(i);
					done_removing = false; // Do another iteration pass after removing from the vector
					break;
				}
			}
		} while (!done_removing);

		recalculateLocalAabb();
	}

	/**
	 * Gets the num child shapes.
	 *
	 * @return the num child shapes
	 */
	public int getNumChildShapes() {
		return children.size();
	}

	/**
	 * Gets the child shape.
	 *
	 * @param index the index
	 * @return the child shape
	 */
	public CollisionShape getChildShape(final int index) {
		return children.get(index).childShape;
	}

	/**
	 * Gets the child transform.
	 *
	 * @param index the index
	 * @param out the out
	 * @return the child transform
	 */
	public Transform getChildTransform(final int index, final Transform out) {
		out.set(children.get(index).transform);
		return out;
	}

	/**
	 * Gets the child list.
	 *
	 * @return the child list
	 */
	public ArrayList<CompoundShapeChild> getChildList() {
		return children;
	}

	/**
	 * getAabb's default implementation is brute force, expected derived classes to implement a fast dedicated version.
	 */
	@Override
	public void getAabb(final Transform trans, final Vector3f aabbMin, final Vector3f aabbMax) {
		Vector3f localHalfExtents = VECTORS.get();
		localHalfExtents.sub(localAabbMax, localAabbMin);
		localHalfExtents.scale(0.5f);
		localHalfExtents.x += getMargin();
		localHalfExtents.y += getMargin();
		localHalfExtents.z += getMargin();

		Vector3f localCenter = VECTORS.get();
		localCenter.add(localAabbMax, localAabbMin);
		localCenter.scale(0.5f);

		Matrix3f abs_b = MATRICES.get(trans.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f center = VECTORS.get(localCenter);
		trans.transform(center);

		Vector3f tmp = VECTORS.get();

		Vector3f extent = VECTORS.get();
		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(localHalfExtents);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(localHalfExtents);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(localHalfExtents);

		aabbMin.sub(center, extent);
		aabbMax.add(center, extent);
		MATRICES.release(abs_b);
		VECTORS.release(localCenter, extent, tmp, localHalfExtents);
	}

	/**
	 * Re-calculate the local Aabb. Is called at the end of removeChildShapes. Use this yourself if you modify the
	 * children or their transforms.
	 */
	public void recalculateLocalAabb() {
		// Recalculate the local aabb
		// Brute force, it iterates over all the shapes left.
		localAabbMin.set(1e30f, 1e30f, 1e30f);
		localAabbMax.set(-1e30f, -1e30f, -1e30f);

		Vector3f tmpLocalAabbMin = VECTORS.get();
		Vector3f tmpLocalAabbMax = VECTORS.get();

		// extend the local aabbMin/aabbMax
		for (CompoundShapeChild child : children) {
			child.childShape.getAabb(child.transform, tmpLocalAabbMin, tmpLocalAabbMax);

			for (int i = 0; i < 3; i++) {
				if (VectorUtil.getCoord(localAabbMin, i) > VectorUtil.getCoord(tmpLocalAabbMin, i)) {
					VectorUtil.setCoord(localAabbMin, i, VectorUtil.getCoord(tmpLocalAabbMin, i));
				}
				if (VectorUtil.getCoord(localAabbMax, i) < VectorUtil.getCoord(tmpLocalAabbMax, i)) {
					VectorUtil.setCoord(localAabbMax, i, VectorUtil.getCoord(tmpLocalAabbMax, i));
				}
			}
		}
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		localScaling.set(scaling);
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f out) {
		out.set(localScaling);
		return out;
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		// approximation: take the inertia from the aabb for now
		Transform ident = TRANSFORMS.get();
		ident.setIdentity();
		Vector3f aabbMin = VECTORS.get(), aabbMax = VECTORS.get();
		getAabb(ident, aabbMin, aabbMax);

		Vector3f halfExtents = VECTORS.get();
		halfExtents.sub(aabbMax, aabbMin);
		halfExtents.scale(0.5f);

		float lx = 2f * halfExtents.x;
		float ly = 2f * halfExtents.y;
		float lz = 2f * halfExtents.z;

		inertia.x = mass / 12f * (ly * ly + lz * lz);
		inertia.y = mass / 12f * (lx * lx + lz * lz);
		inertia.z = mass / 12f * (lx * lx + ly * ly);
		TRANSFORMS.release(ident);
		VECTORS.release(aabbMin, aabbMax, halfExtents);
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.COMPOUND_SHAPE_PROXYTYPE;
	}

	@Override
	public void setMargin(final float margin) {
		collisionMargin = margin;
	}

	@Override
	public float getMargin() {
		return collisionMargin;
	}

	@Override
	public String getName() {
		return "Compound";
	}

	// this is optional, but should make collision queries faster, by culling non-overlapping nodes
	// void createAabbTreeFromChildren();

	/**
	 * Gets the aabb tree.
	 *
	 * @return the aabb tree
	 */
	public OptimizedBvh getAabbTree() {
		return aabbTree;
	}

	/**
	 * Computes the exact moment of inertia and the transform from the coordinate system defined by the principal axes
	 * of the moment of inertia and the center of mass to the current coordinate system. "masses" points to an array of
	 * masses of the children. The resulting transform "principal" has to be applied inversely to all children
	 * transforms in order for the local coordinate system of the compound shape to be centered at the center of mass
	 * and to coincide with the principal axes. This also necessitates a correction of the world transform of the
	 * collision object by the principal transform.
	 */
	public void calculatePrincipalAxisTransform(final float[] masses, final Transform principal,
			final Vector3f inertia) {
		int n = children.size();

		float totalMass = 0;
		Vector3f center = VECTORS.get();
		center.set(0, 0, 0);
		for (int k = 0; k < n; k++) {
			center.scaleAdd(masses[k], children.get(k).transform.origin, center);
			totalMass += masses[k];
		}
		center.scale(1f / totalMass);
		principal.origin.set(center);

		Matrix3f tensor = MATRICES.get();
		tensor.setZero();

		for (int k = 0; k < n; k++) {
			Vector3f i = VECTORS.get();
			children.get(k).childShape.calculateLocalInertia(masses[k], i);

			Transform t = children.get(k).transform;
			Vector3f o = VECTORS.get();
			o.sub(t.origin, center);

			// compute inertia tensor in coordinate system of compound shape
			Matrix3f j = MATRICES.get();
			j.transpose(t.basis);

			j.m00 *= i.x;
			j.m01 *= i.x;
			j.m02 *= i.x;
			j.m10 *= i.y;
			j.m11 *= i.y;
			j.m12 *= i.y;
			j.m20 *= i.z;
			j.m21 *= i.z;
			j.m22 *= i.z;

			j.mul(t.basis, j);

			// add inertia tensor
			tensor.add(j);

			// compute inertia tensor of pointmass at o
			float o2 = o.lengthSquared();
			j.setRow(0, o2, 0, 0);
			j.setRow(1, 0, o2, 0);
			j.setRow(2, 0, 0, o2);
			j.m00 += o.x * -o.x;
			j.m01 += o.y * -o.x;
			j.m02 += o.z * -o.x;
			j.m10 += o.x * -o.y;
			j.m11 += o.y * -o.y;
			j.m12 += o.z * -o.y;
			j.m20 += o.x * -o.z;
			j.m21 += o.y * -o.z;
			j.m22 += o.z * -o.z;

			// add inertia tensor of pointmass
			tensor.m00 += masses[k] * j.m00;
			tensor.m01 += masses[k] * j.m01;
			tensor.m02 += masses[k] * j.m02;
			tensor.m10 += masses[k] * j.m10;
			tensor.m11 += masses[k] * j.m11;
			tensor.m12 += masses[k] * j.m12;
			tensor.m20 += masses[k] * j.m20;
			tensor.m21 += masses[k] * j.m21;
			tensor.m22 += masses[k] * j.m22;
			MATRICES.release(j);
			VECTORS.release(i, o);
		}

		MatrixUtil.diagonalize(tensor, principal.basis, 0.00001f, 20);

		inertia.set(tensor.m00, tensor.m11, tensor.m22);
		VECTORS.release(center);
		MATRICES.release(tensor);
	}

}
