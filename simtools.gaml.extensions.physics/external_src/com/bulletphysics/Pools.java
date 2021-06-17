package com.bulletphysics;

import static msi.gama.common.util.PoolUtils.create;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.collision.dispatch.CompoundCollisionAlgorithm;
import com.bulletphysics.collision.dispatch.ConvexConcaveCollisionAlgorithm;
import com.bulletphysics.collision.dispatch.ConvexConvexAlgorithm;
import com.bulletphysics.collision.dispatch.ConvexPlaneCollisionAlgorithm;
import com.bulletphysics.collision.dispatch.SphereSphereCollisionAlgorithm;
import com.bulletphysics.collision.narrowphase.DiscreteCollisionDetectorInterface.ClosestPointInput;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.narrowphase.VoronoiSimplexSolver.SubSimplexClosestResult;
import com.bulletphysics.dynamics.constraintsolver.JacobianEntry;
import com.bulletphysics.dynamics.constraintsolver.SolverBody;
import com.bulletphysics.dynamics.constraintsolver.SolverConstraint;
import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import com.bulletphysics.extras.gimpact.BoxCollision.BoxBoxTransformCache;
import com.bulletphysics.extras.gimpact.PrimitiveTriangle;
import com.bulletphysics.extras.gimpact.TriangleContact;
import com.bulletphysics.linearmath.Transform;

import msi.gama.common.util.PoolUtils.ObjectPool;

public class Pools {

	public static final ObjectPool<Quat4f> QUATS =
			create("Quats", true, () -> new Quat4f(), (from, to) -> to.set(from), null);

	public static final ObjectPool<Vector3f> VECTORS =
			create("Vectors3F", true, () -> new Vector3f(), (from, to) -> to.set(from), null);

	public static final ObjectPool<Vector4f> VECTORS4 =
			create("Vectors4F", true, () -> new Vector4f(), (from, to) -> to.set(from), null);

	public static final ObjectPool<Transform> TRANSFORMS =
			create("Transforms", true, () -> new Transform(), (from, to) -> to.set(from), null);

	public static final ObjectPool<Matrix3f> MATRICES =
			create("Matrices", true, () -> new Matrix3f(), (from, to) -> to.set(from), null);

	public static final ObjectPool<AABB> AABBS =
			create("AABB", true, () -> new AABB(), (from, to) -> to.set(from), null);

	public static final ObjectPool<PrimitiveTriangle> TRIANGLES =
			create("Triangles", true, () -> new PrimitiveTriangle(), (from, to) -> to.set(from), null);

	public static final ObjectPool<TriangleContact> CONTACTS =
			create("Contacts", true, () -> new TriangleContact(), (from, to) -> to.set(from), null);

	public static final ObjectPool<BoxBoxTransformCache> BBTCS =
			create("BoxBoxTransformCaches", true, () -> new BoxBoxTransformCache(), (from, to) -> to.set(from), null);

	public static final ObjectPool<SolverConstraint> CONSTRAINTS =
			create("SolverConstraints", true, () -> new SolverConstraint(), null, null);

	public static final ObjectPool<SolverBody> BODIES =
			create("SolverBodies", true, () -> new SolverBody(), null, null);

	public static final ObjectPool<JacobianEntry> JACOBIANS =
			create("JacobianEntries", true, () -> new JacobianEntry(), null, null);

	public static final ObjectPool<PersistentManifold> MANIFOLDS =
			create("PersistentManifolds", true, () -> new PersistentManifold(), null, null);

	public static final ObjectPool<CompoundCollisionAlgorithm> COMPOUND_COLLISIONS =
			create("CompoundCollisionAlgorithms", true, () -> new CompoundCollisionAlgorithm(), null, null);

	public static final ObjectPool<ConvexConcaveCollisionAlgorithm> CONVEX_CONCAVE_COLLISIONS =
			create("ConvexConcaveCollisionAlgorithms", true, () -> new ConvexConcaveCollisionAlgorithm(), null, null);

	public static final ObjectPool<ConvexConvexAlgorithm> CONVEX_CONVEX_COLLISIONS =
			create("ConvexConvexAlgorithms", true, () -> new ConvexConvexAlgorithm(), null, null);

	public static final ObjectPool<ConvexPlaneCollisionAlgorithm> CONVEX_PLANE_COLLISIONS =
			create("ConvexPlaneCollisionAlgorithms", true, () -> new ConvexPlaneCollisionAlgorithm(), null, null);

	public static final ObjectPool<SphereSphereCollisionAlgorithm> SPHERE_SPHERE_COLLISIONS =
			create("SphereSphereCollisionAlgorithms", true, () -> new SphereSphereCollisionAlgorithm(), null, null);

	public static final ObjectPool<ClosestPointInput> CLOSEST_POINTS =
			create("ClosestPointInputs", true, () -> new ClosestPointInput(), null, null);

	public static final ObjectPool<SubSimplexClosestResult> SUB_SIMPLEX =
			create("SubSimplexClosestResults", true, () -> new SubSimplexClosestResult(), null, null);

	public static final ObjectPool<ManifoldPoint> MANIFOLD_POINTS =
			create("ManifoldPoints", true, () -> new ManifoldPoint(), null, null);

}
