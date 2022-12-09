/*******************************************************************************************************
 *
 * ShapeHull.java, in simtools.gaml.extensions.physics, is part of the source code of the
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

import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.linearmath.convexhull.HullDesc;
import com.bulletphysics.linearmath.convexhull.HullFlags;
import com.bulletphysics.linearmath.convexhull.HullLibrary;
import com.bulletphysics.linearmath.convexhull.HullResult;
import com.bulletphysics.util.IntArrayList;
import java.util.ArrayList;

/**
 * ShapeHull takes a {@link ConvexShape}, builds the convex hull using {@link HullLibrary} and provides triangle indices
 * and vertices.
 *
 * @author jezek2
 */
public class ShapeHull {

	/** The vertices. */
	protected ArrayList<Vector3f> vertices = new ArrayList<>();
	
	/** The indices. */
	protected IntArrayList indices = new IntArrayList();
	
	/** The num indices. */
	protected int numIndices;
	
	/** The shape. */
	protected ConvexShape shape;

	/** The unit sphere points. */
	protected ArrayList<Vector3f> unitSpherePoints = new ArrayList<>();

	/**
	 * Instantiates a new shape hull.
	 *
	 * @param shape the shape
	 */
	public ShapeHull(final ConvexShape shape) {
		this.shape = shape;
		this.vertices.clear();
		this.indices.clear();
		this.numIndices = 0;

		MiscUtil.resize(unitSpherePoints, NUM_UNITSPHERE_POINTS + ConvexShape.MAX_PREFERRED_PENETRATION_DIRECTIONS * 2,
				Vector3f.class);
		for (int i = 0; i < constUnitSpherePoints.size(); i++) {
			unitSpherePoints.get(i).set(constUnitSpherePoints.get(i));
		}
	}

	/**
	 * Builds the hull.
	 *
	 * @param margin the margin
	 * @return true, if successful
	 */
	public boolean buildHull(final float margin) {
		Vector3f norm = VECTORS.get();

		int numSampleDirections = NUM_UNITSPHERE_POINTS;
		{
			int numPDA = shape.getNumPreferredPenetrationDirections();
			if (numPDA != 0) {
				for (int i = 0; i < numPDA; i++) {
					shape.getPreferredPenetrationDirection(i, norm);
					unitSpherePoints.get(numSampleDirections).set(norm);
					numSampleDirections++;
				}
			}
		}
		VECTORS.release(norm);

		ArrayList<Vector3f> supportPoints = new ArrayList<>();
		MiscUtil.resize(supportPoints, NUM_UNITSPHERE_POINTS + ConvexShape.MAX_PREFERRED_PENETRATION_DIRECTIONS * 2,
				Vector3f.class);

		for (int i = 0; i < numSampleDirections; i++) {
			shape.localGetSupportingVertex(unitSpherePoints.get(i), supportPoints.get(i));
		}

		HullDesc hd = new HullDesc();
		hd.flags = HullFlags.TRIANGLES;
		hd.vcount = numSampleDirections;

		// #ifdef BT_USE_DOUBLE_PRECISION
		// hd.mVertices = &supportPoints[0];
		// hd.mVertexStride = sizeof(btVector3);
		// #else
		hd.vertices = supportPoints;
		// hd.vertexStride = 3 * 4;
		// #endif

		HullLibrary hl = new HullLibrary();
		HullResult hr = new HullResult();
		if (!hl.createConvexHull(hd, hr)) return false;

		MiscUtil.resize(vertices, hr.numOutputVertices, Vector3f.class);

		for (int i = 0; i < hr.numOutputVertices; i++) {
			vertices.get(i).set(hr.outputVertices.get(i));
		}
		numIndices = hr.numIndices;
		MiscUtil.resize(indices, numIndices, 0);
		for (int i = 0; i < numIndices; i++) {
			indices.set(i, hr.indices.get(i));
		}

		// free temporary hull result that we just copied
		hl.releaseResult(hr);

		return true;
	}

	/**
	 * Num triangles.
	 *
	 * @return the int
	 */
	public int numTriangles() {
		return numIndices / 3;
	}

	/**
	 * Num vertices.
	 *
	 * @return the int
	 */
	public int numVertices() {
		return vertices.size();
	}

	/**
	 * Num indices.
	 *
	 * @return the int
	 */
	public int numIndices() {
		return numIndices;
	}

	/**
	 * Gets the vertex pointer.
	 *
	 * @return the vertex pointer
	 */
	public ArrayList<Vector3f> getVertexPointer() {
		return vertices;
	}

	/**
	 * Gets the index pointer.
	 *
	 * @return the index pointer
	 */
	public IntArrayList getIndexPointer() {
		return indices;
	}

	////////////////////////////////////////////////////////////////////////////

	/** The num unitsphere points. */
	private static int NUM_UNITSPHERE_POINTS = 42;

	/** The const unit sphere points. */
	private static ArrayList<Vector3f> constUnitSpherePoints = new ArrayList<>();

	static {
		constUnitSpherePoints.add(new Vector3f(0.000000f, -0.000000f, -1.000000f));
		constUnitSpherePoints.add(new Vector3f(0.723608f, -0.525725f, -0.447219f));
		constUnitSpherePoints.add(new Vector3f(-0.276388f, -0.850649f, -0.447219f));
		constUnitSpherePoints.add(new Vector3f(-0.894426f, -0.000000f, -0.447216f));
		constUnitSpherePoints.add(new Vector3f(-0.276388f, 0.850649f, -0.447220f));
		constUnitSpherePoints.add(new Vector3f(0.723608f, 0.525725f, -0.447219f));
		constUnitSpherePoints.add(new Vector3f(0.276388f, -0.850649f, 0.447220f));
		constUnitSpherePoints.add(new Vector3f(-0.723608f, -0.525725f, 0.447219f));
		constUnitSpherePoints.add(new Vector3f(-0.723608f, 0.525725f, 0.447219f));
		constUnitSpherePoints.add(new Vector3f(0.276388f, 0.850649f, 0.447219f));
		constUnitSpherePoints.add(new Vector3f(0.894426f, 0.000000f, 0.447216f));
		constUnitSpherePoints.add(new Vector3f(-0.000000f, 0.000000f, 1.000000f));
		constUnitSpherePoints.add(new Vector3f(0.425323f, -0.309011f, -0.850654f));
		constUnitSpherePoints.add(new Vector3f(-0.162456f, -0.499995f, -0.850654f));
		constUnitSpherePoints.add(new Vector3f(0.262869f, -0.809012f, -0.525738f));
		constUnitSpherePoints.add(new Vector3f(0.425323f, 0.309011f, -0.850654f));
		constUnitSpherePoints.add(new Vector3f(0.850648f, -0.000000f, -0.525736f));
		constUnitSpherePoints.add(new Vector3f(-0.525730f, -0.000000f, -0.850652f));
		constUnitSpherePoints.add(new Vector3f(-0.688190f, -0.499997f, -0.525736f));
		constUnitSpherePoints.add(new Vector3f(-0.162456f, 0.499995f, -0.850654f));
		constUnitSpherePoints.add(new Vector3f(-0.688190f, 0.499997f, -0.525736f));
		constUnitSpherePoints.add(new Vector3f(0.262869f, 0.809012f, -0.525738f));
		constUnitSpherePoints.add(new Vector3f(0.951058f, 0.309013f, 0.000000f));
		constUnitSpherePoints.add(new Vector3f(0.951058f, -0.309013f, 0.000000f));
		constUnitSpherePoints.add(new Vector3f(0.587786f, -0.809017f, 0.000000f));
		constUnitSpherePoints.add(new Vector3f(0.000000f, -1.000000f, 0.000000f));
		constUnitSpherePoints.add(new Vector3f(-0.587786f, -0.809017f, 0.000000f));
		constUnitSpherePoints.add(new Vector3f(-0.951058f, -0.309013f, -0.000000f));
		constUnitSpherePoints.add(new Vector3f(-0.951058f, 0.309013f, -0.000000f));
		constUnitSpherePoints.add(new Vector3f(-0.587786f, 0.809017f, -0.000000f));
		constUnitSpherePoints.add(new Vector3f(-0.000000f, 1.000000f, -0.000000f));
		constUnitSpherePoints.add(new Vector3f(0.587786f, 0.809017f, -0.000000f));
		constUnitSpherePoints.add(new Vector3f(0.688190f, -0.499997f, 0.525736f));
		constUnitSpherePoints.add(new Vector3f(-0.262869f, -0.809012f, 0.525738f));
		constUnitSpherePoints.add(new Vector3f(-0.850648f, 0.000000f, 0.525736f));
		constUnitSpherePoints.add(new Vector3f(-0.262869f, 0.809012f, 0.525738f));
		constUnitSpherePoints.add(new Vector3f(0.688190f, 0.499997f, 0.525736f));
		constUnitSpherePoints.add(new Vector3f(0.525730f, 0.000000f, 0.850652f));
		constUnitSpherePoints.add(new Vector3f(0.162456f, -0.499995f, 0.850654f));
		constUnitSpherePoints.add(new Vector3f(-0.425323f, -0.309011f, 0.850654f));
		constUnitSpherePoints.add(new Vector3f(-0.425323f, 0.309011f, 0.850654f));
		constUnitSpherePoints.add(new Vector3f(0.162456f, 0.499995f, 0.850654f));
	}

}
