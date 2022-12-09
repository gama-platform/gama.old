/*******************************************************************************************************
 *
 * GImpactMeshShapePart.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.AABBS;
import static com.bulletphysics.Pools.TRIANGLES;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.IntArrayList;

/**
 * This class manages a sub part of a mesh supplied by the StridingMeshInterface interface.
 * <p>
 *
 * - Simply create this shape by passing the StridingMeshInterface to the constructor GImpactMeshShapePart, then you
 * must call updateBound() after creating the mesh<br>
 * - When making operations with this shape, you must call <b>lock</b> before accessing to the trimesh primitives, and
 * then call <b>unlock</b><br>
 * - You can handle deformable meshes with this shape, by calling postUpdate() every time when changing the mesh
 * vertices.
 *
 * @author jezek2
 */
public class GImpactMeshShapePart extends GImpactShapeInterface {

	/** The primitive manager. */
	TrimeshPrimitiveManager primitive_manager = new TrimeshPrimitiveManager();

	/** The collided. */
	private final IntArrayList collided = new IntArrayList();

	/**
	 * Instantiates a new g impact mesh shape part.
	 */
	public GImpactMeshShapePart() {
		box_set.setPrimitiveManager(primitive_manager);
	}

	/**
	 * Instantiates a new g impact mesh shape part.
	 *
	 * @param meshInterface the mesh interface
	 * @param part the part
	 */
	public GImpactMeshShapePart(final StridingMeshInterface meshInterface, final int part) {
		primitive_manager.meshInterface = meshInterface;
		primitive_manager.part = part;
		box_set.setPrimitiveManager(primitive_manager);
	}

	@Override
	public boolean childrenHasTransform() {
		return false;
	}

	@Override
	public void lockChildShapes() {
		TrimeshPrimitiveManager dummymanager = (TrimeshPrimitiveManager) box_set.getPrimitiveManager();
		dummymanager.lock();
	}

	@Override
	public void unlockChildShapes() {
		TrimeshPrimitiveManager dummymanager = (TrimeshPrimitiveManager) box_set.getPrimitiveManager();
		dummymanager.unlock();
	}

	@Override
	public int getNumChildShapes() {
		return primitive_manager.get_primitive_count();
	}

	@Override
	public CollisionShape getChildShape(final int index) {
		assert false;
		return null;
	}

	@Override
	public Transform getChildTransform(final int index) {
		assert false;
		return null;
	}

	@Override
	public void setChildTransform(final int index, final Transform transform) {
		assert false;
	}

	@Override
	PrimitiveManagerBase getPrimitiveManager() {
		return primitive_manager;
	}

	/**
	 * Gets the trimesh primitive manager.
	 *
	 * @return the trimesh primitive manager
	 */
	TrimeshPrimitiveManager getTrimeshPrimitiveManager() {
		return primitive_manager;
	}

	@Override
	public void calculateLocalInertia(final float mass, final Vector3f inertia) {
		lockChildShapes();

		// #define CALC_EXACT_INERTIA 1
		// #ifdef CALC_EXACT_INERTIA
		inertia.set(0f, 0f, 0f);

		int i = getVertexCount();
		float pointmass = mass / i;

		Vector3f pointintertia = VECTORS.get();

		while (i-- != 0) {
			getVertex(i, pointintertia);
			GImpactMassUtil.get_point_inertia(pointintertia, pointmass, pointintertia);
			inertia.add(pointintertia);
		}

		// #else
		//
		//// Calc box inertia
		//
		// float lx= localAABB.max.x - localAABB.min.x;
		// float ly= localAABB.max.y - localAABB.min.y;
		// float lz= localAABB.max.z - localAABB.min.z;
		// float x2 = lx*lx;
		// float y2 = ly*ly;
		// float z2 = lz*lz;
		// float scaledmass = mass * 0.08333333f;
		//
		// inertia.set(y2+z2,x2+z2,x2+y2);
		// inertia.scale(scaledmass);
		//
		// #endif
		VECTORS.release(pointintertia);
		unlockChildShapes();
	}

	@Override
	public String getName() {
		return "GImpactMeshShapePart";
	}

	@Override
	ShapeType getGImpactShapeType() {
		return ShapeType.TRIMESH_SHAPE_PART;
	}

	@Override
	public boolean needsRetrieveTriangles() {
		return true;
	}

	@Override
	public boolean needsRetrieveTetrahedrons() {
		return false;
	}

	@Override
	public void getBulletTriangle(final int prim_index, final TriangleShapeEx triangle) {
		primitive_manager.get_bullet_triangle(prim_index, triangle);
	}

	@Override
	void getBulletTetrahedron(final int prim_index, final TetrahedronShapeEx tetrahedron) {
		assert false;
	}

	/**
	 * Gets the vertex count.
	 *
	 * @return the vertex count
	 */
	public int getVertexCount() {
		return primitive_manager.get_vertex_count();
	}

	/**
	 * Gets the vertex.
	 *
	 * @param vertex_index the vertex index
	 * @param vertex the vertex
	 * @return the vertex
	 */
	public void getVertex(final int vertex_index, final Vector3f vertex) {
		primitive_manager.get_vertex(vertex_index, vertex);
	}

	@Override
	public void setMargin(final float margin) {
		primitive_manager.margin = margin;
		postUpdate();
	}

	@Override
	public float getMargin() {
		return primitive_manager.margin;
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		primitive_manager.scale.set(scaling);
		postUpdate();
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f out) {
		out.set(primitive_manager.scale);
		return out;
	}

	/**
	 * Gets the part.
	 *
	 * @return the part
	 */
	public int getPart() {
		return primitive_manager.part;
	}

	@Override
	public void processAllTriangles( final TriangleCallback callback, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		lockChildShapes();
		AABB box = AABBS.get();
		box.min.set(aabbMin);
		box.max.set(aabbMax);

		collided.clear();
		box_set.boxQuery(box, collided);

		if (collided.size() == 0) {
			unlockChildShapes();
			return;
		}

		int part = getPart();
		PrimitiveTriangle triangle = TRIANGLES.get();
		int i = collided.size();
		while (i-- != 0) {
			getPrimitiveTriangle(collided.get(i), triangle);
			callback.processTriangle( triangle.vertices, part, collided.get(i));
		}
		TRIANGLES.release(triangle);
		AABBS.release(box);
		unlockChildShapes();
	}

}
