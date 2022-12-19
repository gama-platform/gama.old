/*******************************************************************************************************
 *
 * HeightfieldTerrainShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dom;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.shapes.ConcaveShape;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;

/**
 * The Class HeightfieldTerrainShape.
 */
public class HeightfieldTerrainShape extends ConcaveShape {

	/** The Constant XAXIS. */
	public static final int XAXIS = 0;
	
	/** The Constant YAXIS. */
	public static final int YAXIS = 1;
	
	/** The Constant ZAXIS. */
	public static final int ZAXIS = 2;

	/** The m local aabb min. */
	protected Vector3f m_localAabbMin = new Vector3f();
	
	/** The m local aabb max. */
	protected Vector3f m_localAabbMax = new Vector3f();
	
	/** The m local origin. */
	protected Vector3f m_localOrigin = new Vector3f();

	/** The m height stick width. */
	// /terrain data
	protected int m_heightStickWidth;
	
	/** The m height stick length. */
	protected int m_heightStickLength;
	
	/** The m min height. */
	protected float m_minHeight;
	
	/** The m max height. */
	protected float m_maxHeight;
	
	/** The m width. */
	protected float m_width;
	
	/** The m length. */
	protected float m_length;
	
	/** The m height scale. */
	protected float m_heightScale;
	
	/** The m heightfield data float. */
	protected float[] m_heightfieldDataFloat;
	
	/** The m height data type. */
	protected ScalarType m_heightDataType;
	
	/** The m flip quad edges. */
	protected boolean m_flipQuadEdges;
	
	/** The m use diamond subdivision. */
	protected boolean m_useDiamondSubdivision;
	
	/** The m up axis. */
	protected int m_upAxis;
	
	/** The m local scaling. */
	protected Vector3f m_localScaling = new Vector3f();

	/**
	 * Instantiates a new heightfield terrain shape.
	 *
	 * @param heightStickWidth the height stick width
	 * @param heightStickLength the height stick length
	 * @param heightfieldData the heightfield data
	 * @param heightScale the height scale
	 * @param minHeight the min height
	 * @param maxHeight the max height
	 * @param upAxis the up axis
	 * @param flipQuadEdges the flip quad edges
	 */
	public HeightfieldTerrainShape(final int heightStickWidth, final int heightStickLength,
			final float[] heightfieldData, final float heightScale, final float minHeight, final float maxHeight,
			final int upAxis, final boolean flipQuadEdges) {

		initialize(heightStickWidth, heightStickLength, heightfieldData, heightScale, minHeight, maxHeight, upAxis,
				ScalarType.FLOAT, flipQuadEdges);
	}

	/**
	 * Initialize.
	 *
	 * @param heightStickWidth the height stick width
	 * @param heightStickLength the height stick length
	 * @param heightfieldData the heightfield data
	 * @param heightScale the height scale
	 * @param minHeight the min height
	 * @param maxHeight the max height
	 * @param upAxis the up axis
	 * @param f the f
	 * @param flipQuadEdges the flip quad edges
	 */
	private void initialize(final int heightStickWidth, final int heightStickLength, final float[] heightfieldData,
			final float heightScale, final float minHeight, final float maxHeight, final int upAxis, final ScalarType f,
			final boolean flipQuadEdges) {
		m_heightStickWidth = heightStickWidth;
		m_heightStickLength = heightStickLength;
		m_minHeight = minHeight * heightScale;
		m_maxHeight = maxHeight * heightScale;
		m_width = heightStickWidth - 1;
		m_length = heightStickLength - 1;
		m_heightScale = heightScale;
		m_heightfieldDataFloat = heightfieldData;
		m_heightDataType = ScalarType.FLOAT;
		m_flipQuadEdges = flipQuadEdges;
		m_useDiamondSubdivision = false;
		m_upAxis = upAxis;
		m_localScaling.set(1.f, 1.f, 1.f);

		// determine min/max axis-aligned bounding box (aabb) values
		switch (m_upAxis) {
			case 0: {
				m_localAabbMin.set(m_minHeight, 0, 0);
				m_localAabbMax.set(m_maxHeight, m_width, m_length);
				break;
			}
			case 1: {
				m_localAabbMin.set(0, m_minHeight, 0);
				m_localAabbMax.set(m_width, m_maxHeight, m_length);
				break;
			}
			case 2: {
				m_localAabbMin.set(0, 0, m_minHeight);
				m_localAabbMax.set(m_width, m_length, m_maxHeight);
				break;
			}
		}

		// remember origin (defined as exact middle of aabb)
		// m_localOrigin = btScalar(0.5) * (m_localAabbMin + m_localAabbMax);

		m_localOrigin.set(m_localAabbMin);
		m_localOrigin.add(m_localAabbMax);
		m_localOrigin.x = m_localOrigin.x * 0.5f;
		m_localOrigin.y = m_localOrigin.y * 0.5f;
		m_localOrigin.z = m_localOrigin.z * 0.5f;

	}

	@Override
	public void processAllTriangles( final TriangleCallback callback, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		Vector3f localAabbMin = VECTORS.get();
		Vector3f localAabbMax = VECTORS.get();

		localAabbMin.x = aabbMin.x * (1.f / m_localScaling.x);
		localAabbMin.y = aabbMin.y * (1.f / m_localScaling.y);
		localAabbMin.z = aabbMin.z * (1.f / m_localScaling.z);

		localAabbMax.x = aabbMax.x * (1.f / m_localScaling.x);
		localAabbMax.y = aabbMax.y * (1.f / m_localScaling.y);
		localAabbMax.z = aabbMax.z * (1.f / m_localScaling.z);

		localAabbMin.add(m_localOrigin);
		localAabbMax.add(m_localOrigin);

		// quantize the aabbMin and aabbMax, and adjust the start/end ranges
		int[] quantizedAabbMin = new int[3];
		int[] quantizedAabbMax = new int[3];
		quantizeWithClamp(quantizedAabbMin, localAabbMin);
		quantizeWithClamp(quantizedAabbMax, localAabbMax);
		VECTORS.release(localAabbMin, localAabbMax);

		// expand the min/max quantized values
		// this is to catch the case where the input aabb falls between grid points!
		for (int i = 0; i < 3; ++i) {
			quantizedAabbMin[i]--;
			quantizedAabbMax[i]++;
		}

		int startX = 0;
		int endX = m_heightStickWidth - 1;
		int startJ = 0;
		int endJ = m_heightStickLength - 1;

		switch (m_upAxis) {
			case 0: {
				if (quantizedAabbMin[1] > startX) { startX = quantizedAabbMin[1]; }
				if (quantizedAabbMax[1] < endX) { endX = quantizedAabbMax[1]; }
				if (quantizedAabbMin[2] > startJ) { startJ = quantizedAabbMin[2]; }
				if (quantizedAabbMax[2] < endJ) { endJ = quantizedAabbMax[2]; }
				break;
			}
			case 1: {
				if (quantizedAabbMin[0] > startX) { startX = quantizedAabbMin[0]; }
				if (quantizedAabbMax[0] < endX) { endX = quantizedAabbMax[0]; }
				if (quantizedAabbMin[2] > startJ) { startJ = quantizedAabbMin[2]; }
				if (quantizedAabbMax[2] < endJ) { endJ = quantizedAabbMax[2]; }
				break;
			}

			case 2: {
				if (quantizedAabbMin[0] > startX) { startX = quantizedAabbMin[0]; }
				if (quantizedAabbMax[0] < endX) { endX = quantizedAabbMax[0]; }
				if (quantizedAabbMin[1] > startJ) { startJ = quantizedAabbMin[1]; }
				if (quantizedAabbMax[1] < endJ) { endJ = quantizedAabbMax[1]; }
				break;
			}
		}

		for (int j = startJ; j < endJ; j++) {
			for (int x = startX; x < endX; x++) {
				// Vector3f vertices[3];
				Vector3f[] vertices = new Vector3f[3];
				vertices[0] = VECTORS.get();
				vertices[1] = VECTORS.get();
				vertices[2] = VECTORS.get();
				if (m_flipQuadEdges || m_useDiamondSubdivision && (j + x & 1) != 0) {// XXX
					// first triangle
					getVertex(x, j, vertices[0]);
					getVertex(x + 1, j, vertices[1]);
					getVertex(x + 1, j + 1, vertices[2]);
					callback.processTriangle( vertices, x, j);
					// callback->processTriangle(vertices,x,j);
					// second triangle
					getVertex(x, j, vertices[0]);
					getVertex(x + 1, j + 1, vertices[1]);
					getVertex(x, j + 1, vertices[2]);
					// callback->processTriangle(vertices,x,j);
					callback.processTriangle( vertices, x, j);
				} else {
					// first triangle
					getVertex(x, j, vertices[0]);
					getVertex(x, j + 1, vertices[1]);
					getVertex(x + 1, j, vertices[2]);
					// callback->processTriangle(vertices,x,j);
					callback.processTriangle( vertices, x, j);
					// second triangle
					getVertex(x + 1, j, vertices[0]);
					getVertex(x, j + 1, vertices[1]);
					getVertex(x + 1, j + 1, vertices[2]);
					// callback->processTriangle(vertices,x,j);
					callback.processTriangle( vertices, x, j);
				}
				VECTORS.release(vertices);
			}
		}

	}

	/**
	 * Gets the vertex.
	 *
	 * @param x the x
	 * @param y the y
	 * @param vertex the vertex
	 * @return the vertex
	 */
	// / this returns the vertex in bullet-local coordinates
	private void getVertex(final int x, final int y, final Vector3f vertex) {
		float height = getRawHeightFieldValue(x, y);

		switch (m_upAxis) {
			case 0: {
				vertex.set(height - m_localOrigin.x, -m_width / 2.0f + x, -m_length / 2.0f + y);
				break;
			}
			case 1: {
				vertex.set(-m_width / 2.0f + x, height - m_localOrigin.y, -m_length / 2.0f + y);
				break;
			}

			case 2: {
				vertex.set(-m_width / 2.0f + x, -m_length / 2.0f + y, height - m_localOrigin.z);
				break;
			}
		}

		vertex.x = vertex.x * m_localScaling.x;
		vertex.y = vertex.y * m_localScaling.y;
		vertex.z = vertex.z * m_localScaling.z;
	}

	@Override
	public void calculateLocalInertia(final float arg0, final Vector3f inertia) {
		inertia.set(0.f, 0.f, 0.f);
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		Vector3f halfExtents = VECTORS.get(m_localAabbMax);
		halfExtents.sub(m_localAabbMin);
		halfExtents.x = halfExtents.x * m_localScaling.x * 0.5f;
		halfExtents.y = halfExtents.y * m_localScaling.y * 0.5f;
		halfExtents.z = halfExtents.z * m_localScaling.z * 0.5f;

		/*
		 * Vector3f localOrigin(0, 0, 0); localOrigin[m_upAxis] = (m_minHeight + m_maxHeight) * 0.5f; XXX localOrigin *=
		 * m_localScaling;
		 */

		Matrix3f abs_b = MATRICES.get(t.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f tmp = VECTORS.get();

		Vector3f center = VECTORS.get(t.origin);
		Vector3f extent = VECTORS.get();
		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(halfExtents);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(halfExtents);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(halfExtents);

		Vector3f margin = VECTORS.get();
		margin.set(getMargin(), getMargin(), getMargin());
		extent.add(margin);

		aabbMin.sub(center, extent);
		aabbMax.add(center, extent);
		VECTORS.release(tmp, center, extent, margin, halfExtents);
		MATRICES.release(abs_b);
	}

	@Override
	public Vector3f getLocalScaling(final Vector3f arg0) {
		return m_localScaling;
	}

	@Override
	public String getName() {
		return "Terrain";
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.TERRAIN_SHAPE_PROXYTYPE;
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		m_localScaling = scaling;
	}

	// / This returns the "raw" (user's initial) height, not the actual height.
	// / The actual height needs to be adjusted to be relative to the center
	// / of the heightfield's AABB.

	/**
	 * Gets the raw height field value.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the raw height field value
	 */
	private float getRawHeightFieldValue(final int x, final int y) {
		return m_heightfieldDataFloat[y * m_heightStickWidth + x] * m_heightScale;
	}

	/**
	 * Gets the quantized.
	 *
	 * @param x the x
	 * @return the quantized
	 */
	public static int getQuantized(final float x) {
		if (x < 0.0) return (int) (x - 0.5);
		return (int) (x + 0.5);
	}

	// / given input vector, return quantized version
	/**
	 * This routine is basically determining the gridpoint indices for a given input vector, answering the question:
	 * "which gridpoint is closest to the provided point?".
	 *
	 * "with clamp" means that we restrict the point to be in the heightfield's axis-aligned bounding box.
	 */
	private void quantizeWithClamp(final int[] out, final Vector3f clampedPoint) {

		/*
		 * btVector3 clampedPoint(point); XXX clampedPoint.setMax(m_localAabbMin); clampedPoint.setMin(m_localAabbMax);
		 *
		 * clampedPoint.clampMax(m_localAabbMax,); clampedPoint.clampMax(m_localAabbMax);
		 * clampedPoint.clampMax(m_localAabbMax);
		 *
		 * clampedPoint.clampMin(m_localAabbMin); clampedPoint.clampMin(m_localAabbMin); ///CLAMPS
		 * clampedPoint.clampMin(m_localAabbMin);
		 */

		out[0] = getQuantized(clampedPoint.x);
		out[1] = getQuantized(clampedPoint.y);
		out[2] = getQuantized(clampedPoint.z);
	}
}
