/*******************************************************************************************************
 *
 * GImpactShapeInterface.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.AABBS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.dispatch.CollisionWorld.RayResultCallback;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConcaveShape;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import com.bulletphysics.linearmath.Transform;

/**
 * Base class for gimpact shapes.
 *
 * @author jezek2
 */
public abstract class GImpactShapeInterface extends ConcaveShape {

	/** The local AABB. */
	protected AABB localAABB = new AABB();
	
	/** The needs update. */
	protected boolean needs_update;
	
	/** The local scaling. */
	protected final Vector3f localScaling = new Vector3f();
	
	/** The box set. */
	GImpactBvh box_set = new GImpactBvh(); // optionally boxset

	/**
	 * Instantiates a new g impact shape interface.
	 */
	public GImpactShapeInterface() {
		localAABB.invalidate();
		needs_update = true;
		localScaling.set(1f, 1f, 1f);
	}

	/**
	 * Performs refit operation.
	 * <p>
	 * Updates the entire Box set of this shape.
	 * <p>
	 *
	 * postUpdate() must be called for attemps to calculating the box set, else this function will does nothing.
	 * <p>
	 *
	 * if m_needs_update == true, then it calls calcLocalAABB();
	 */
	public void updateBound() {
		if (!needs_update) return;
		calcLocalAABB();
		needs_update = false;
	}

	/**
	 * If the Bounding box is not updated, then this class attemps to calculate it.
	 * <p>
	 * Calls updateBound() for update the box set.
	 */
	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		AABB transformedbox = AABBS.get(localAABB);
		transformedbox.appy_transform(t);
		aabbMin.set(transformedbox.min);
		aabbMax.set(transformedbox.max);
	}

	/**
	 * Tells to this object that is needed to refit the box set.
	 */
	public void postUpdate() {
		needs_update = true;
	}

	/**
	 * Obtains the local box, which is the global calculated box of the total of subshapes.
	 */
	public AABB getLocalBox(final AABB out) {
		out.set(localAABB);
		return out;
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.GIMPACT_SHAPE_PROXYTYPE;
	}

	/**
	 * You must call updateBound() for update the box set.
	 */
	@Override
	public void setLocalScaling( final Vector3f scaling) {
		localScaling.set(scaling);
		postUpdate();
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f out) {
		out.set(localScaling);
		return out;
	}

	@Override
	public void setMargin(final float margin) {
		collisionMargin = margin;
		int i = getNumChildShapes();
		while (i-- != 0) {
			CollisionShape child = getChildShape(i);
			child.setMargin(margin);
		}

		needs_update = true;
	}

	/**
	 * Base method for determinig which kind of GIMPACT shape we get.
	 */
	abstract ShapeType getGImpactShapeType();

	/**
	 * Gets the box set.
	 *
	 * @return the box set
	 */
	GImpactBvh getBoxSet() {
		return box_set;
	}

	/**
	 * Determines if this class has a hierarchy structure for sorting its primitives.
	 */
	public boolean hasBoxSet() {
		if (box_set.getNodeCount() == 0) return false;
		return true;
	}

	/**
	 * Obtains the primitive manager.
	 */
	abstract PrimitiveManagerBase getPrimitiveManager();

	/**
	 * Gets the number of children.
	 */
	public abstract int getNumChildShapes();

	/**
	 * If true, then its children must get transforms.
	 */
	public abstract boolean childrenHasTransform();

	/**
	 * Determines if this shape has triangles.
	 */
	public abstract boolean needsRetrieveTriangles();

	/**
	 * Determines if this shape has tetrahedrons.
	 */
	public abstract boolean needsRetrieveTetrahedrons();

	/**
	 * Gets the bullet triangle.
	 *
	 * @param prim_index the prim index
	 * @param triangle the triangle
	 * @return the bullet triangle
	 */
	public abstract void getBulletTriangle(int prim_index, TriangleShapeEx triangle);

	/**
	 * Gets the bullet tetrahedron.
	 *
	 * @param prim_index the prim index
	 * @param tetrahedron the tetrahedron
	 * @return the bullet tetrahedron
	 */
	abstract void getBulletTetrahedron(int prim_index, TetrahedronShapeEx tetrahedron);

	/**
	 * Call when reading child shapes.
	 */
	public void lockChildShapes() {}

	/**
	 * Unlock child shapes.
	 */
	public void unlockChildShapes() {}

	/**
	 * If this trimesh.
	 */
	void getPrimitiveTriangle(final int index, final PrimitiveTriangle triangle) {
		getPrimitiveManager().get_primitive_triangle(index, triangle);
	}

	/**
	 * Use this function for perfofm refit in bounding boxes.
	 */
	protected void calcLocalAABB() {
		lockChildShapes();
		if (box_set.getNodeCount() == 0) {
			box_set.buildSet();
		} else {
			box_set.update();
		}
		unlockChildShapes();

		box_set.getGlobalBox(localAABB);
	}

	/**
	 * Retrieves the bound from a child.
	 */
	public void getChildAabb(final int child_index, final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		AABB child_aabb = AABBS.get();
		getPrimitiveManager().get_primitive_box(child_index, child_aabb);
		child_aabb.appy_transform(t);
		aabbMin.set(child_aabb.min);
		aabbMax.set(child_aabb.max);
	}

	/**
	 * Gets the children.
	 */
	public abstract CollisionShape getChildShape(int index);

	/**
	 * Gets the children transform.
	 */
	public abstract Transform getChildTransform(int index);

	/**
	 * Sets the children transform.
	 * <p>
	 * You must call updateBound() for update the box set.
	 */
	public abstract void setChildTransform(int index, Transform transform);

	/**
	 * Virtual method for ray collision.
	 */
	public void rayTest(final Vector3f rayFrom, final Vector3f rayTo, final RayResultCallback resultCallback) {}

	/**
	 * Function for retrieve triangles. It gives the triangles in local space.
	 */
	@Override
	public void processAllTriangles( final TriangleCallback callback, final Vector3f aabbMin,
			final Vector3f aabbMax) {}

}
