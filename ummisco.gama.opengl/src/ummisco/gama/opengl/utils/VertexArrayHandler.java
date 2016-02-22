/*********************************************************************************************
 *
 *
 * 'VertexArrayHandler.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

import java.awt.Color;
import java.nio.*;
import java.util.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.glu.GLU;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.util.IList;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.scene.*;

public class VertexArrayHandler {

	private class MyTriangulatedGeometry {

		public IList<IShape> triangles;
		public double z;
		public Color color;
		public double alpha;
		// public String type;
		// public Boolean fill;
		// public Boolean isTextured = false;
		// public double elevation = 0;
	}

	private class MyLine {

		public LineString line;
		public double z;
		public Color color;
		public double alpha;

	}

	// OpenGL member
	private final GL2 myGl;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLRenderer myGLRender;

	// public BasicOpenGlDrawer basicDrawer;

	float alpha = 1.0f;

	private ArrayList<MyTriangulatedGeometry> triangulatedGeometries = new ArrayList<MyTriangulatedGeometry>();
	private final ArrayList<MyLine> lines = new ArrayList<MyLine>();

	private final ArrayList<Vertex[]> vertexArrayContours = new ArrayList<Vertex[]>();
	private final ArrayList<Color> colorBorder = new ArrayList<Color>();

	private ArrayList<Polygon> tempArray = new ArrayList<Polygon>();

	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;

	/* draw sphere */
	private ArrayList<Float> sphereTriangles = new ArrayList<Float>();
	private ArrayList<Float> sphereNormals = new ArrayList<Float>();
	private ArrayList<Integer> sphereIndices = new ArrayList<Integer>();
	private FloatBuffer spheresVertexBuffer;
	private FloatBuffer spheresNormalBuffer;
	private FloatBuffer spheresColorBuffer;
	private IntBuffer spheresIndicesBuffer;
	private ArrayList<Color> sphereColor = new ArrayList<Color>();
	private int nbVerticesSphere = 0;
	private int sphereIndicesBufferID;

	private FloatBuffer vertexBufferTriangle;
	private FloatBuffer colorBufferTriangle;
	int indicesPolygonsVertex = 0;

	private FloatBuffer vertexBufferLine;
	private FloatBuffer colorBufferLine;

	private FloatBuffer vertexBufferContours;
	private FloatBuffer colorBufferContours;

	private GeometryObject curGeometry;
	private int nbVerticesLine;
	private int nbVerticesTriangle;

	private int nbVerticesContours;
	private FloatBuffer vertexBufferPolyContours;
	private FloatBuffer colorBufferPolyContours;

	/* draw Point */
	private final ArrayList<GamaPoint> pointTriangles = new ArrayList<GamaPoint>();
	private final ArrayList<Color> pointColor = new ArrayList<Color>();
	private FloatBuffer pointVertexBuffer;
	private FloatBuffer pointColorBuffer;
	private IntBuffer pointIndicesBuffer;

	/* draw faces */
	private int totalNumVertsQuads;
	private FloatBuffer vertexBufferQuads;
	private FloatBuffer colorBufferQuads;
	private ArrayList<double[]> normalArrayFaces = new ArrayList<double[]>();
	private ArrayList<Float> alphaFaces = new ArrayList<Float>();
	private ArrayList<Vertex[]> vertexArrayFaces = new ArrayList<Vertex[]>();
	private ArrayList<Color> colorFaces = new ArrayList<Color>();
	private int nbVerticesFaces;
	private FloatBuffer vertexBufferFacesN;

	/* draw plan */
	private int nbVerticesPlan;
	private int nbVerticesBorder;
	private final ArrayList<GamaPoint> planTriangles = new ArrayList<GamaPoint>();
	private final ArrayList<GamaPoint> planContours = new ArrayList<GamaPoint>();
	private FloatBuffer planVertexBuffer;
	private FloatBuffer planColorBuffer;
	private FloatBuffer planBorderBuffer;
	private FloatBuffer planBorderColorBuffer;
	// private Color planColor;
	// private Color planBorderColor;

	private ArrayList<Float> sphereAlpha = new ArrayList<Float>();

	private int size = 0;

	/* For drawing a sphere from an icosahedron */
	private static float[][] icoshedronVertices; // Vertices of an icosahedron.
	private static int[][] icoshedronFaces; // Faces of the icosahedron, with a face
											// represented as 3 indices into the vertex array.

	private FloatBuffer icosphereVertexBuffer; // Buffer to hold isosphere vertex data.
	private IntBuffer icosphereIndexBuffer; // Buffer to hold isosphere face data, with
											// a face represented as 3 indices into
											// the vertex buffer.

	private int vertexCount, indexCount; // Number of values stored in the BufferUtil.

	private boolean createIcosphere = false;

	private int nbIndexSphere;

	private int polygonsIndicesBufferID;

	private IntBuffer indicesBufferTriangle;

	private IntBuffer IndicesBufferPolyContours;

	private int contoursIndicesBufferID;

	private int indicesContours = 0;

	private IntBuffer facesIndicesBuffer;

	private int indicesFaces, indicesFacesContours = 0;

	private int facesIndicesBufferID;

	private IntBuffer facesContoursIndicesBuffer;

	private int facescontoursIndicesBufferID;

	private IntBuffer planIndicesBuffer;

	private IntBuffer planBorderIndicesBuffer;

	// private int planIndices;

	// private final int planBorderIndices = 0;

	private int planIndicesBufferID;

	private int planBorderIndicesBufferID;

	private IntBuffer indicesBufferLine;

	private int indicesLines = 0;

	private int linesIndicesBufferID;

	private int pointIndicesBufferID;

	// private float planAlpha;

	static { // Initialize the data for the icosahedron.
		float t = (float) ((FastMath.sqrt(5) - 1) / 2);
		icoshedronVertices = new float[][] { new float[] { -1, -t, 0 }, new float[] { 0, 1, t },
			new float[] { 0, 1, -t }, new float[] { 1, t, 0 }, new float[] { 1, -t, 0 }, new float[] { 0, -1, -t },
			new float[] { 0, -1, t }, new float[] { t, 0, 1 }, new float[] { -t, 0, 1 }, new float[] { t, 0, -1 },
			new float[] { -t, 0, -1 }, new float[] { -1, t, 0 }, };
		for ( float[] v : icoshedronVertices ) {
			// Normalize the vertices to have unit length.
			float length = (float) FastMath.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
			v[0] /= length;
			v[1] /= length;
			v[2] /= length;
		}
		icoshedronFaces = new int[][] { { 3, 7, 1 }, { 4, 7, 3 }, { 6, 7, 4 }, { 8, 7, 6 }, { 7, 8, 1 }, { 9, 4, 3 },
			{ 2, 9, 3 }, { 2, 3, 1 }, { 11, 2, 1 }, { 10, 2, 11 }, { 10, 9, 2 }, { 9, 5, 4 }, { 6, 4, 5 }, { 0, 6, 5 },
			{ 0, 11, 8 }, { 11, 1, 8 }, { 10, 0, 5 }, { 10, 5, 9 }, { 0, 8, 6 }, { 0, 10, 11 }, };
	}

	public VertexArrayHandler(final GL2 gl, final GLU glu, final JOGLRenderer gLRender) {
		myGl = gl;
		myGLRender = gLRender;
		// basicDrawer = new BasicOpenGlDrawer(myGLRender);
	}

	/**
	 * Create the vertex array for all JTS geometries by using a triangulation
	 *
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildVertexArray(final Iterable<? extends AbstractObject> list) {

		nbVerticesTriangle = 0;
		nbVerticesLine = 0;

		Iterator<? extends AbstractObject> it = list.iterator();

		// Loop over all the geometries, triangulate them and get the total
		// number of vertices.
		while (it.hasNext()) {
			AbstractObject o = it.next();
			if ( !(o instanceof GeometryObject) ) {
				continue;
			}
			curGeometry = (GeometryObject) o;
			for ( int i = 0; i < curGeometry.geometry.getNumGeometries(); i++ ) {

				if ( curGeometry.geometry.getGeometryType() == "MultiPolygon" ) {
					buildMultiPolygonVertexArray((MultiPolygon) curGeometry.geometry, curGeometry.getLayerZ(),
						curGeometry.getColor(), curGeometry.getAlpha(), curGeometry.isFilled(), curGeometry.getBorder(),
						/* curGeometry.angle, */curGeometry.getHeight());
				}

				else if ( curGeometry.geometry.getGeometryType() == "Polygon" ) {
					if ( curGeometry.getType() != null && curGeometry.getType().equals(IShape.Type.SPHERE) ) {
						if ( !createIcosphere ) {
							makeIcosphere(2);
							createIcosphere = true;
						}
						buildSphereVertexArray((Polygon) curGeometry.geometry, curGeometry.getLayerZ(),
							curGeometry.getHeight(), curGeometry.getColor(), curGeometry.getAlpha());
					} else {
						if ( curGeometry.getHeight() > 0 ) {
							buildPolyhedreVertexArray((Polygon) curGeometry.geometry, curGeometry.getLayerZ(),
								curGeometry.getColor(), curGeometry.getAlpha(), curGeometry.isFilled(),
								curGeometry.getHeight(), /* curGeometry.angle, */true, curGeometry.getBorder());
						} else {
							buildPolygonVertexArray((Polygon) curGeometry.geometry, curGeometry.getLayerZ(),
								curGeometry.getColor(), curGeometry.getAlpha(), curGeometry.isFilled(),
								curGeometry.hasTextures(), /* curGeometry.angle, */true);
							buildVertexArrayContours(curGeometry.getLayerZ());
						}
					}
				}

				else if ( curGeometry.geometry.getGeometryType() == "MultiLineString" ) {
					buildMultiLineStringVertexArray((MultiLineString) curGeometry.geometry, curGeometry.getLayerZ(),
						curGeometry.getColor(), curGeometry.getAlpha());
				}

				else if ( curGeometry.geometry.getGeometryType() == "LineString" ) {
					if ( curGeometry.getHeight() > 0 ) {
						buildPlanVertexArray((LineString) curGeometry.geometry, curGeometry.getLayerZ(),
							curGeometry.getColor(), curGeometry.getAlpha(), curGeometry.getHeight(), 0, true);
					} else {
						buildLineStringVertexArray((LineString) curGeometry.geometry, curGeometry.getLayerZ(),
							curGeometry.getColor(), curGeometry.getAlpha());
					}
				} else if ( curGeometry.geometry.getGeometryType() == "Point" ) {
					// FIXME: Should never go here even with a height value as the geometry of a sphere is a polygon...
					if ( curGeometry.getHeight() > 0 ) {
						if ( !createIcosphere ) {
							makeIcosphere(2);
							createIcosphere = true;
						}
						buildSphereVertexArray((Polygon) curGeometry.geometry.getEnvelope().buffer(1),
							curGeometry.getLayerZ(), curGeometry.getHeight(), curGeometry.getColor(),
							curGeometry.getAlpha());
					} else {
						buildPointVertexArray((Point) curGeometry.geometry, curGeometry.getLayerZ(), 10,
							myGLRender.getMaxEnvDim() / 1000, curGeometry.getColor(), curGeometry.getAlpha());
					}
				}

			}
		}

		// fillPointBufferUtil();
		// fillPolygonBufferUtil();
		// fillContoursBufferUtil();
		// fillFacesBufferUtil();
		// fillFacesContoursBufferUtil();
		// fillPlanBufferUtil();
		// fillLineBuffer();
		// fillSphereVertexArray();

		// loadCollada("null");

		// createVBOs();
	}

	public void createVBOs() {

		if ( indicesBufferTriangle.hasRemaining() ) {
			/* create polygons vertex buffer objects */
			int[] buffer1 = new int[3];
			myGl.glGenBuffers(3, buffer1, 0);
			polygonsIndicesBufferID = buffer1[2];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer1[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, nbVerticesTriangle * 3 * Buffers.SIZEOF_FLOAT, vertexBufferTriangle,
				GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer1[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, nbVerticesTriangle * 4 * Buffers.SIZEOF_FLOAT, colorBufferTriangle,
				GL.GL_STATIC_DRAW);
			myGl.glColorPointer(4, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer1[2]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, nbVerticesTriangle * Buffers.SIZEOF_INT,
				indicesBufferTriangle, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, polygonsIndicesBufferID);
			myGl.glDrawElements(GL.GL_TRIANGLES, nbVerticesTriangle, GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(3, buffer1, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		if ( IndicesBufferPolyContours.hasRemaining() ) {
			/* create polygons contours vertex buffer objects */
			int[] buffer2 = new int[3];
			myGl.glGenBuffers(3, buffer2, 0);
			contoursIndicesBufferID = buffer2[2];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer2[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, nbVerticesContours * 3 * Buffers.SIZEOF_FLOAT,
				vertexBufferPolyContours, GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer2[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, nbVerticesContours * 3 * Buffers.SIZEOF_FLOAT,
				colorBufferPolyContours, GL.GL_STATIC_DRAW);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer2[2]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, nbVerticesContours * Buffers.SIZEOF_INT,
				IndicesBufferPolyContours, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, contoursIndicesBufferID);
			myGl.glDrawElements(GL.GL_LINES, nbVerticesContours, GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(3, buffer2, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		if ( facesIndicesBuffer.hasRemaining() ) {
			/* create polyhedron faces vertex buffer objects */
			int[] buffer4 = new int[4];
			myGl.glGenBuffers(4, buffer4, 0);
			facesIndicesBufferID = buffer4[3];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer4[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, nbVerticesFaces * 6 * Buffers.SIZEOF_FLOAT, vertexBufferQuads,
				GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer4[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, nbVerticesFaces * 6 * Buffers.SIZEOF_FLOAT, vertexBufferFacesN,
				GL.GL_STATIC_DRAW);
			myGl.glNormalPointer(GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer4[2]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, nbVerticesFaces * 7 * Buffers.SIZEOF_FLOAT, colorBufferQuads,
				GL.GL_STATIC_DRAW);
			myGl.glColorPointer(4, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer4[3]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, facesIndicesBuffer.capacity() * Buffers.SIZEOF_INT,
				facesIndicesBuffer, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, facesIndicesBufferID);
			myGl.glDrawElements(GL.GL_TRIANGLES, facesIndicesBuffer.capacity(), GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(4, buffer4, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		/*
		 * create polyhedron faces borders
		 */
		if ( facesContoursIndicesBuffer.hasRemaining() ) {
			int[] buffer5 = new int[3];
			myGl.glGenBuffers(3, buffer5, 0);
			facescontoursIndicesBufferID = buffer5[2];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer5[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBufferContours.capacity() * Buffers.SIZEOF_FLOAT,
				vertexBufferContours, GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer5[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, colorBufferContours.capacity() * Buffers.SIZEOF_FLOAT,
				colorBufferContours, GL.GL_STATIC_DRAW);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer5[2]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, facesContoursIndicesBuffer.capacity() * Buffers.SIZEOF_INT,
				facesContoursIndicesBuffer, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, facescontoursIndicesBufferID);
			myGl.glDrawElements(GL.GL_LINES, facesContoursIndicesBuffer.capacity(), GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(3, buffer5, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		/*
		 * create line vertex buffer objects
		 */
		if ( indicesBufferLine.hasRemaining() ) {
			int[] buffer8 = new int[3];
			myGl.glGenBuffers(3, buffer8, 0);
			linesIndicesBufferID = buffer8[2];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer8[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBufferLine.capacity() * Buffers.SIZEOF_FLOAT, vertexBufferLine,
				GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer8[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, colorBufferLine.capacity() * Buffers.SIZEOF_FLOAT, colorBufferLine,
				GL.GL_STATIC_DRAW);
			myGl.glColorPointer(4, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer8[2]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBufferLine.capacity() * Buffers.SIZEOF_INT,
				indicesBufferLine, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, linesIndicesBufferID);
			myGl.glDrawElements(GL.GL_LINES, indicesBufferLine.capacity(), GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(3, buffer8, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		/*
		 * create plan vertex buffer objects
		 */
		if ( planIndicesBuffer.hasRemaining() ) {
			int[] buffer6 = new int[3];
			myGl.glGenBuffers(3, buffer6, 0);
			planIndicesBufferID = buffer6[2];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer6[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, planVertexBuffer.capacity() * Buffers.SIZEOF_FLOAT, planVertexBuffer,
				GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer6[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, planColorBuffer.capacity() * Buffers.SIZEOF_FLOAT, planColorBuffer,
				GL.GL_STATIC_DRAW);
			myGl.glColorPointer(4, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer6[2]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, planIndicesBuffer.capacity() * Buffers.SIZEOF_INT,
				planIndicesBuffer, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, planIndicesBufferID);
			myGl.glDrawElements(GL.GL_TRIANGLES, planIndicesBuffer.capacity(), GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(3, buffer6, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		/*
		 * create plan borders buffer objects
		 */
		if ( planBorderIndicesBuffer.hasRemaining() ) {
			int[] buffer7 = new int[3];
			myGl.glGenBuffers(3, buffer7, 0);
			planBorderIndicesBufferID = buffer7[2];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer7[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, planBorderBuffer.capacity() * Buffers.SIZEOF_FLOAT, planBorderBuffer,
				GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer7[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, planBorderColorBuffer.capacity() * Buffers.SIZEOF_FLOAT,
				planBorderColorBuffer, GL.GL_STATIC_DRAW);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer7[2]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, planBorderIndicesBuffer.capacity() * Buffers.SIZEOF_INT,
				planBorderIndicesBuffer, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, planBorderIndicesBufferID);
			myGl.glDrawElements(GL.GL_LINES, planBorderIndicesBuffer.capacity(), GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(3, buffer7, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		/*
		 * create point buffer objects
		 */
		if ( pointIndicesBuffer.hasRemaining() ) {
			int[] buffer9 = new int[3];
			myGl.glGenBuffers(3, buffer9, 0);
			pointIndicesBufferID = buffer9[2];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer9[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, pointVertexBuffer.capacity() * Buffers.SIZEOF_FLOAT,
				pointVertexBuffer, GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer9[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, pointColorBuffer.capacity() * Buffers.SIZEOF_FLOAT, pointColorBuffer,
				GL.GL_STATIC_DRAW);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer9[2]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, pointIndicesBuffer.capacity() * Buffers.SIZEOF_INT,
				pointIndicesBuffer, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, pointIndicesBufferID);
			myGl.glDrawElements(GL.GL_LINES, pointIndicesBuffer.capacity(), GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(3, buffer9, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

		/*
		 * create sphere vertex buffer objects
		 */
		if ( spheresIndicesBuffer.hasRemaining() ) {
			int[] buffer3 = new int[4];
			myGl.glGenBuffers(4, buffer3, 0);
			sphereIndicesBufferID = buffer3[3];

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer3[0]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, spheresVertexBuffer.capacity() * Buffers.SIZEOF_FLOAT,
				spheresVertexBuffer, GL.GL_STATIC_DRAW);
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer3[1]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, spheresNormalBuffer.capacity() * Buffers.SIZEOF_FLOAT,
				spheresNormalBuffer, GL.GL_STATIC_DRAW);
			myGl.glNormalPointer(GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer3[2]);
			myGl.glBufferData(GL.GL_ARRAY_BUFFER, spheresColorBuffer.capacity() * Buffers.SIZEOF_FLOAT,
				spheresColorBuffer, GL.GL_STATIC_DRAW);
			myGl.glColorPointer(4, GL.GL_FLOAT, 0, 0);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer3[3]);
			myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, spheresIndicesBuffer.capacity() * Buffers.SIZEOF_INT,
				spheresIndicesBuffer, GL.GL_STATIC_DRAW);

			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

			myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
			myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, sphereIndicesBufferID);
			myGl.glDrawElements(GL.GL_TRIANGLES, spheresIndicesBuffer.capacity(), GL.GL_UNSIGNED_INT, 0);

			myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
			myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			myGl.glDeleteBuffers(4, buffer3, 0);

			myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
			myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
		}

	}

	public void DeleteVertexArray() {

		if ( vertexBufferTriangle != null ) {
			vertexBufferTriangle.clear();
		}
		if ( colorBufferTriangle != null ) {
			colorBufferTriangle.clear();
		}

		if ( vertexBufferLine != null ) {
			vertexBufferLine.clear();
		}
		if ( colorBufferLine != null ) {
			colorBufferLine.clear();
		}

		if ( triangulatedGeometries != null ) {
			triangulatedGeometries.clear();
		}
		if ( lines != null ) {
			lines.clear();
		}

	}

	/**
	 * Function that build a point vertex buffer
	 */
	private void buildPointVertexArray(final Point point, double z, final int numPoints, final double radius,
		final Color color, final Double alpha2) {

		// FIXME: Does not work for Point.
		// Add z value
		if ( Double.isNaN(point.getCoordinate().z) == false ) {
			z = z + point.getCoordinate().z;
		}

		double angle;
		for ( int k = 0; k < numPoints; k++ ) {
			angle = k * 2 * FastMath.PI / numPoints;

			pointTriangles.add(new GamaPoint(point.getCoordinate().x + FastMath.cos(angle) * radius,
				-1 * point.getCoordinate().y + FastMath.sin(angle) * radius, z));

			pointColor.add(color);
		}
	}

	// private void fillPointBufferUtil() {
	// pointVertexBuffer = Buffers.newDirectFloatBuffer(pointTriangles.size() * 3);
	// pointColorBuffer = Buffers.newDirectFloatBuffer(pointTriangles.size() * 3);
	// pointIndicesBuffer = Buffers.newDirectIntBuffer(pointTriangles.size());
	//
	// for ( int i = 0; i < pointTriangles.size(); i++ ) {
	// pointVertexBuffer.put((float) pointTriangles.get(i).x);
	// pointVertexBuffer.put((float) pointTriangles.get(i).y);
	// pointVertexBuffer.put((float) pointTriangles.get(i).z);
	//
	// pointColorBuffer.put(pointColor.get(i).getRed() / 255);
	// pointColorBuffer.put(pointColor.get(i).getGreen() / 255);
	// pointColorBuffer.put(pointColor.get(i).getBlue() / 255);
	//
	// pointIndicesBuffer.put(i);
	// }
	// pointVertexBuffer.rewind();
	// pointColorBuffer.rewind();
	// pointIndicesBuffer.rewind();
	// }

	/**
	 *
	 * Function that create a temporary ArrayList of MyTriangulated objects to be used later by the
	 * fillVertexArrayTriangle() that will fill a vertex, color and indexes BufferUtil.
	 *
	 */
	public void buildPolygonVertexArray(final Polygon polygon, final double z_layer, final Color c, final double alpha,
		final boolean fill, final boolean isTextured, /* final Integer angle, */final boolean drawPolygonContour) {
		if ( fill == true ) {
			MyTriangulatedGeometry curTriangulatedGeometry = new MyTriangulatedGeometry();
			curTriangulatedGeometry.triangles = GeometryUtils.triangulation(null, polygon);

			// Add z value
			double z = 0.0;
			if ( Double.isNaN(polygon.getCoordinate().z) == false ) {
				z = z + polygon.getCoordinate().z;
			}

			if ( drawPolygonContour == true ) {
				tempArray.add(polygon); // temporary ArrayList to build polygon contours
			}

			curTriangulatedGeometry.z = z_layer + z;
			curTriangulatedGeometry.color = c;
			curTriangulatedGeometry.alpha = alpha;
			// curTriangulatedGeometry.fill = fill;
			// curTriangulatedGeometry.angle = angle;
			triangulatedGeometries.add(curTriangulatedGeometry);
			nbVerticesTriangle = nbVerticesTriangle + curTriangulatedGeometry.triangles.size() * 3;
		} else {
			tempArray.add(polygon); // temporary ArrayList to only build borders
		}

		// FIXME add texture....
		if ( isTextured ) {}
	}

	public void fillPolygonBufferUtil() {

		vertexBufferTriangle = Buffers.newDirectFloatBuffer(nbVerticesTriangle * 3);
		colorBufferTriangle = Buffers.newDirectFloatBuffer(nbVerticesTriangle * 4);
		indicesBufferTriangle = Buffers.newDirectIntBuffer(nbVerticesTriangle);

		Iterator<MyTriangulatedGeometry> it2 = triangulatedGeometries.iterator();
		// For each triangulated shape
		while (it2.hasNext()) {

			MyTriangulatedGeometry curTriangulatedGeo = it2.next();
			Iterator<IShape> it3 = curTriangulatedGeo.triangles.iterator();
			// For each traingle
			while (it3.hasNext()) {
				IShape curTriangle = it3.next();
				Polygon polygon = (Polygon) curTriangle.getInnerGeometry();
				for ( int i = 0; i < 3; i++ ) {
					vertexBufferTriangle.put((float) polygon.getExteriorRing().getPointN(i).getX());
					vertexBufferTriangle.put((float) -polygon.getExteriorRing().getPointN(i).getY());
					vertexBufferTriangle.put((float) curTriangulatedGeo.z);

					colorBufferTriangle.put((float) curTriangulatedGeo.color.getRed() / 255);
					colorBufferTriangle.put((float) curTriangulatedGeo.color.getGreen() / 255);
					colorBufferTriangle.put((float) curTriangulatedGeo.color.getBlue() / 255);
					colorBufferTriangle.put((float) curTriangulatedGeo.alpha);

					indicesBufferTriangle.put(indicesPolygonsVertex);
					indicesPolygonsVertex++;
				}
			}
		}

		vertexBufferTriangle.rewind();
		colorBufferTriangle.rewind();
		indicesBufferTriangle.rewind();

		triangulatedGeometries.clear();

		triangulatedGeometries = new ArrayList<MyTriangulatedGeometry>();

	}

	/**
	 *
	 * functions that build polygons contours using a temporary arrayList of polygons
	 *
	 */
	public void buildVertexArrayContours(final double z_layer) {
		int totalNbVertPoly = 0;

		for ( int l = 0; l < tempArray.size(); l++ ) {
			Polygon curPolygon = tempArray.get(l);

			double z = 0.0;
			if ( Double.isNaN(curPolygon.getCoordinate().z) == false ) {
				z = z + curPolygon.getCoordinate().z;
			}

			int curPolyGonNumPoints = curPolygon.getExteriorRing().getNumPoints();

			totalNbVertPoly += curPolyGonNumPoints * 2;

			for ( int j = 0; j < curPolyGonNumPoints; j++ ) {

				int k = (j + 1) % curPolyGonNumPoints;

				// // Build the 2 vertices of the contour
				Vertex[] vertices = new Vertex[2];
				for ( int i = 0; i < 2; i++ ) {
					vertices[i] = new Vertex();
				}

				vertices[0].x = curPolygon.getExteriorRing().getPointN(j).getX();
				vertices[0].y = -1 * curPolygon.getExteriorRing().getPointN(j).getY();
				vertices[0].z = z_layer + z;

				vertices[1].x = curPolygon.getExteriorRing().getPointN(k).getX();
				vertices[1].y = -1 * curPolygon.getExteriorRing().getPointN(k).getY();
				vertices[1].z = z_layer + z;

				vertexArrayContours.add(vertices);
			}
		}

		nbVerticesContours = nbVerticesContours + totalNbVertPoly;

		tempArray.clear();
		tempArray = new ArrayList<Polygon>();
	}

	public void fillContoursBufferUtil() {
		vertexBufferPolyContours = Buffers.newDirectFloatBuffer(nbVerticesContours * 3);
		colorBufferPolyContours = Buffers.newDirectFloatBuffer(nbVerticesContours * 3);
		IndicesBufferPolyContours = Buffers.newDirectIntBuffer(nbVerticesContours);

		for ( int i = 0; i < vertexArrayContours.size(); i++ ) {

			vertexBufferPolyContours.put((float) vertexArrayContours.get(i)[0].x);
			vertexBufferPolyContours.put((float) vertexArrayContours.get(i)[0].y);
			vertexBufferPolyContours.put((float) vertexArrayContours.get(i)[0].z);
			colorBufferPolyContours.put((float) 0.0);
			colorBufferPolyContours.put((float) 0.0);
			colorBufferPolyContours.put((float) 0.0);
			vertexBufferPolyContours.put((float) vertexArrayContours.get(i)[1].x);
			vertexBufferPolyContours.put((float) vertexArrayContours.get(i)[1].y);
			vertexBufferPolyContours.put((float) vertexArrayContours.get(i)[1].z);
			colorBufferPolyContours.put((float) 0.0);
			colorBufferPolyContours.put((float) 0.0);
			colorBufferPolyContours.put((float) 0.0);
		}
		for ( int i = 0; i < vertexArrayContours.size() * 2; i++ ) {
			IndicesBufferPolyContours.put(indicesContours);
			indicesContours++;
		}

		vertexBufferPolyContours.rewind();
		colorBufferPolyContours.rewind();
		IndicesBufferPolyContours.rewind();
	}

	/**
	 * Function that build a polyhedron using the previously defined functions
	 *
	 */
	private void buildPolyhedreVertexArray(final Polygon geometry, final double z_layer, final Color color,
		final Double alpha2, final Boolean fill, final double height, /* final int angle, */final boolean b,
		final Color border) {

		buildPolygonVertexArray(geometry, z_layer, color, alpha2, fill, false, /* angle, */true);
		buildVertexArrayContours(z_layer);
		buildPolygonVertexArray(geometry, z_layer + height, color, alpha2, fill, false, /* angle, */true);
		buildVertexArrayContours(z_layer + height);
		buildFacesVertexArray(geometry, color, alpha2, fill, border, z_layer, height, true);

	}

	/**
	 *
	 * functions that build polyhedron faces
	 *
	 */
	public void buildFacesVertexArray(final Polygon p, final Color c, final double alpha, final boolean fill,
		final Color b, final double z_layer, final double height, final boolean drawPolygonContour) {

		double z = 0.0;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = z + p.getCoordinate().z;
		}

		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

		totalNumVertsQuads = 4 * curPolyGonNumPoints;

		for ( int j = 0; j < curPolyGonNumPoints; j++ ) {

			int k = (j + 1) % curPolyGonNumPoints;

			// // Build the 4 vertices of the face.
			Vertex[] vertices = new Vertex[4];
			for ( int i = 0; i < 4; i++ ) {
				vertices[i] = new Vertex();
			}

			vertices[0].x = p.getExteriorRing().getPointN(j).getX();
			vertices[0].y = -1 * p.getExteriorRing().getPointN(j).getY();
			vertices[0].z = z_layer + height + z;

			vertices[1].x = p.getExteriorRing().getPointN(k).getX();
			vertices[1].y = -1 * p.getExteriorRing().getPointN(k).getY();
			vertices[1].z = z_layer + height + z;

			vertices[2].x = p.getExteriorRing().getPointN(k).getX();
			vertices[2].y = -1 * p.getExteriorRing().getPointN(k).getY();
			vertices[2].z = z_layer + z;

			vertices[3].x = p.getExteriorRing().getPointN(j).getX();
			vertices[3].y = -1 * p.getExteriorRing().getPointN(j).getY();
			vertices[3].z = z_layer + z;

			double[] normal = CalculateNormal(vertices[2], vertices[1], vertices[0]);

			alphaFaces.add((float) alpha);
			colorFaces.add(c);
			colorBorder.add(b);
			normalArrayFaces.add(normal);
			vertexArrayFaces.add(vertices);
		}
		nbVerticesFaces = nbVerticesFaces + totalNumVertsQuads;
	}

	public void fillFacesBufferUtil() {
		vertexBufferQuads = Buffers.newDirectFloatBuffer(nbVerticesFaces * 6);

		colorBufferQuads = Buffers.newDirectFloatBuffer(nbVerticesFaces * 7);

		vertexBufferFacesN = Buffers.newDirectFloatBuffer(nbVerticesFaces * 6);

		facesIndicesBuffer = Buffers.newDirectIntBuffer(nbVerticesFaces * 6);

		for ( int i = 0; i < vertexArrayFaces.size(); i++ ) {

			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[0].x);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[0].y);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[0].z);

			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[0]);
			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[1]);
			vertexBufferFacesN.put((float) 0.0);

			colorBufferQuads.put((float) colorFaces.get(i).getRed() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getGreen() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getBlue() / 255);
			colorBufferQuads.put(alphaFaces.get(i));

			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[1].x);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[1].y);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[1].z);

			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[0]);
			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[1]);
			vertexBufferFacesN.put((float) 0.0);

			colorBufferQuads.put((float) colorFaces.get(i).getRed() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getGreen() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getBlue() / 255);
			colorBufferQuads.put(alphaFaces.get(i));

			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[3].x);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[3].y);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[3].z);

			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[0]);
			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[1]);
			vertexBufferFacesN.put((float) 0.0);

			colorBufferQuads.put((float) colorFaces.get(i).getRed() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getGreen() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getBlue() / 255);
			colorBufferQuads.put(alphaFaces.get(i));

			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[3].x);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[3].y);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[3].z);

			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[0]);
			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[1]);
			vertexBufferFacesN.put((float) 0.0);

			colorBufferQuads.put((float) colorFaces.get(i).getRed() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getGreen() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getBlue() / 255);
			colorBufferQuads.put(alphaFaces.get(i));

			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[2].x);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[2].y);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[2].z);

			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[0]);
			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[1]);
			vertexBufferFacesN.put((float) 0.0);

			colorBufferQuads.put((float) colorFaces.get(i).getRed() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getGreen() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getBlue() / 255);
			colorBufferQuads.put(alphaFaces.get(i));

			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[1].x);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[1].y);
			vertexBufferQuads.put((float) vertexArrayFaces.get(i)[1].z);

			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[0]);
			vertexBufferFacesN.put((float) normalArrayFaces.get(i)[1]);
			vertexBufferFacesN.put((float) 0.0);

			colorBufferQuads.put((float) colorFaces.get(i).getRed() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getGreen() / 255);
			colorBufferQuads.put((float) colorFaces.get(i).getBlue() / 255);
			colorBufferQuads.put(alphaFaces.get(i));

		}

		for ( int i = 0; i < vertexArrayFaces.size() * 6; i++ ) {
			facesIndicesBuffer.put(indicesFaces);
			indicesFaces++;
		}

		vertexBufferQuads.rewind();
		colorBufferQuads.rewind();
		vertexBufferFacesN.rewind();
		facesIndicesBuffer.rewind();

		normalArrayFaces.clear();
		colorFaces.clear();
		alphaFaces.clear();

		normalArrayFaces = new ArrayList<double[]>();
		colorFaces = new ArrayList<Color>();
		alphaFaces = new ArrayList<Float>();
	}

	public void fillFacesContoursBufferUtil() {

		vertexBufferContours = Buffers.newDirectFloatBuffer(nbVerticesFaces * 8);

		colorBufferContours = Buffers.newDirectFloatBuffer(nbVerticesFaces * 8);

		facesContoursIndicesBuffer = Buffers.newDirectIntBuffer(nbVerticesFaces * 8);

		for ( int i = 0; i < vertexArrayFaces.size(); i++ ) {

			vertexBufferContours.put((float) vertexArrayFaces.get(i)[0].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[0].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[0].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[1].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[1].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[1].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);

			vertexBufferContours.put((float) vertexArrayFaces.get(i)[1].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[1].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[1].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[2].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[2].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[2].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);

			vertexBufferContours.put((float) vertexArrayFaces.get(i)[2].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[2].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[2].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[3].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[3].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[3].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);

			vertexBufferContours.put((float) vertexArrayFaces.get(i)[3].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[3].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[3].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[0].x);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[0].y);
			vertexBufferContours.put((float) vertexArrayFaces.get(i)[0].z);
			colorBufferContours.put((float) colorBorder.get(i).getRed() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getGreen() / 255);
			colorBufferContours.put((float) colorBorder.get(i).getBlue() / 255);

		}

		for ( int i = 0; i < vertexArrayFaces.size() * 8; i++ ) {
			facesContoursIndicesBuffer.put(indicesFacesContours);
			indicesFacesContours++;
		}

		vertexBufferContours.rewind();
		colorBufferContours.rewind();
		facesContoursIndicesBuffer.rewind();

		vertexArrayFaces.clear();

		vertexArrayFaces = new ArrayList<Vertex[]>();
	}

	/**
	 *
	 * Function that build a MultiPolygon geometry using the previously defined
	 * buildPolyhedreVertexArray or buildPolygonVertexArray functions.
	 */
	public void buildMultiPolygonVertexArray(final MultiPolygon polygons, final double z_layer, final Color c,
		final double alpha, final boolean fill, final Color border, /* final Integer angle, */final double height) {

		int numGeometries = polygons.getNumGeometries();

		for ( int i = 0; i < numGeometries; i++ ) {
			curPolygon = (Polygon) polygons.getGeometryN(i);

			if ( height > 0 ) {
				buildPolyhedreVertexArray(curPolygon, z_layer, c, alpha, fill, height, /* angle, */false, border);
			} else {
				buildPolygonVertexArray(curPolygon, z_layer, c, alpha, fill, false, /* angle, */true);
			}
		}
	}

	/**
	 *
	 * Functions that build a plan
	 *
	 */
	private void buildPlanVertexArray(final LineString l, double z, final Color c, final double alpha2,
		final double height, final int i, final boolean drawPolygonContour) {
		// TODO Auto-generated method stub
		buildLineStringVertexArray(l, z, c, alpha);
		buildLineStringVertexArray(l, z + height, c, alpha);

		// planColor = c;
		// planAlpha = (float) alpha2;
		int numPoints = l.getNumPoints();

		// Add z value
		if ( Double.isNaN(l.getCoordinate().z) == false ) {
			z = z + l.getCoordinate().z;
		}

		for ( int j = 0; j < numPoints - 1; j++ ) {
			planTriangles.add(new GamaPoint(l.getPointN(j).getX(), -1 * l.getPointN(j).getY(), z));
			planTriangles.add(new GamaPoint(l.getPointN(j + 1).getX(), -1 * l.getPointN(j + 1).getY(), z));
			planTriangles.add(new GamaPoint(l.getPointN(j + 1).getX(), -1 * l.getPointN(j + 1).getY(), z + height));

			planTriangles.add(new GamaPoint(l.getPointN(j + 1).getX(), -1 * l.getPointN(j + 1).getY(), z + height));
			planTriangles.add(new GamaPoint(l.getPointN(j).getX(), -1 * l.getPointN(j).getY(), z));
			planTriangles.add(new GamaPoint(l.getPointN(j).getX(), -1 * l.getPointN(j).getY(), z + height));
		}

		if ( drawPolygonContour == true ) {
			// planBorderColor = new Color(0.0f, 0.0f, 0.0f);
			for ( int j = 0; j < numPoints - 1; j++ ) {
				planContours.add(new GamaPoint(l.getPointN(j).getX(), -1 * l.getPointN(j).getY(), z));
				planContours.add(new GamaPoint(l.getPointN(j + 1).getX(), -1 * l.getPointN(j + 1).getY(), z));

				planContours.add(new GamaPoint(l.getPointN(j + 1).getX(), -1 * l.getPointN(j + 1).getY(), z));
				planContours.add(new GamaPoint(l.getPointN(j + 1).getX(), -1 * l.getPointN(j + 1).getY(), z + height));

				planContours.add(new GamaPoint(l.getPointN(j + 1).getX(), -1 * l.getPointN(j + 1).getY(), z + height));
				planContours.add(new GamaPoint(l.getPointN(j).getX(), -1 * l.getPointN(j).getY(), z + height));

				planContours.add(new GamaPoint(l.getPointN(j).getX(), -1 * l.getPointN(j).getY(), z + height));
				planContours.add(new GamaPoint(l.getPointN(j).getX(), -1 * l.getPointN(j).getY(), z));
			}
		}
		nbVerticesPlan = nbVerticesPlan + (numPoints - 1) * 6;
		nbVerticesBorder = nbVerticesBorder + (numPoints - 1) * 8;
	}

	// private void fillPlanBufferUtil() {
	// this.planColorBuffer = Buffers.newDirectFloatBuffer(nbVerticesPlan * 4);
	// this.planVertexBuffer = Buffers.newDirectFloatBuffer(nbVerticesPlan * 3);
	// this.planBorderBuffer = Buffers.newDirectFloatBuffer(nbVerticesBorder * 3);
	// this.planBorderColorBuffer = Buffers.newDirectFloatBuffer(nbVerticesBorder * 3);
	// this.planIndicesBuffer = Buffers.newDirectIntBuffer(nbVerticesPlan);
	// this.planBorderIndicesBuffer = Buffers.newDirectIntBuffer(nbVerticesBorder);
	//
	// for ( int i = 0; i < planTriangles.size(); i++ ) {
	// planVertexBuffer.put((float) planTriangles.get(i).getX());
	// planVertexBuffer.put((float) planTriangles.get(i).getY());
	// planVertexBuffer.put((float) planTriangles.get(i).getZ());
	//
	// planColorBuffer.put(planColor.getRed());
	// planColorBuffer.put(planColor.getGreen());
	// planColorBuffer.put(planColor.getBlue());
	// planColorBuffer.put(planAlpha);
	//
	// planIndicesBuffer.put(planIndices);
	// planIndices++;
	// }
	//
	// for ( int i = 0; i < planContours.size(); i++ ) {
	// planBorderBuffer.put((float) planContours.get(i).getX());
	// planBorderBuffer.put((float) planContours.get(i).getY());
	// planBorderBuffer.put((float) planContours.get(i).getZ());
	//
	// planBorderColorBuffer.put(planBorderColor.getRed());
	// planBorderColorBuffer.put(planBorderColor.getGreen());
	// planBorderColorBuffer.put(planBorderColor.getBlue());
	//
	// planBorderIndicesBuffer.put(planBorderIndices);
	// planBorderIndices++;
	// }
	//
	// this.planBorderBuffer.rewind();
	// this.planBorderColorBuffer.rewind();
	// this.planColorBuffer.rewind();
	// this.planVertexBuffer.rewind();
	// this.planBorderIndicesBuffer.rewind();
	// this.planIndicesBuffer.rewind();
	//
	// planTriangles.clear(); // desalocate memory used by ArrayList
	// planContours.clear();
	//
	// planTriangles = new ArrayList<GamaPoint>(); // desalocate memory used by ArrayList
	// planContours = new ArrayList<GamaPoint>();
	//
	// }

	/**
	 * Functions that build a LineString or a MultiLineString geometry and fill it into a vertex buffer
	 *
	 * @param line
	 * @param z
	 * @param c
	 * @param alpha2
	 */
	public void buildLineStringVertexArray(final LineString line, final double z, final Color c, final double alpha2) {
		MyLine curLine = new MyLine();

		curLine.line = line;
		curLine.z = z + curLine.line.getCoordinate().z;
		curLine.color = c;
		curLine.alpha = alpha2;
		lines.add(curLine);
		nbVerticesLine = nbVerticesLine + line.getNumPoints() * 2;
	}

	public void buildMultiLineStringVertexArray(final MultiLineString multiline, final double z_layer, final Color c,
		final double alpha2) {

		// get the number of line in the multiline.
		int numGeometries = multiline.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {

			MyLine curLine = new MyLine();
			curLine.line = (LineString) multiline.getGeometryN(i);

			// Add z value
			double z = 0.0;
			if ( Double.isNaN(curLine.line.getCoordinate().z) == false ) {
				z = z + curLine.line.getCoordinate().z;
			}

			curLine.z = z_layer + z;
			curLine.color = c;
			curLine.alpha = alpha2;
			lines.add(curLine);
			nbVerticesLine = nbVerticesLine + multiline.getGeometryN(i).getNumPoints() * 2;
		}

	}

	public void fillLineBuffer() {

		vertexBufferLine = Buffers.newDirectFloatBuffer(nbVerticesLine * 3 * 2);
		colorBufferLine = Buffers.newDirectFloatBuffer(nbVerticesLine * 4 * 2);
		indicesBufferLine = Buffers.newDirectIntBuffer(nbVerticesLine * 2);

		Iterator<MyLine> it = lines.iterator();
		// For each line
		while (it.hasNext()) {

			MyLine curLine = it.next();

			for ( int i = 0; i < curLine.line.getNumPoints() - 1; i++ ) {
				vertexBufferLine.put((float) curLine.line.getPointN(i).getX());
				vertexBufferLine.put((float) -curLine.line.getPointN(i).getY());
				vertexBufferLine.put((float) curLine.z);
				colorBufferLine.put((float) curLine.color.getRed() / 255);
				colorBufferLine.put((float) curLine.color.getGreen() / 255);
				colorBufferLine.put((float) curLine.color.getBlue() / 255);
				colorBufferLine.put((float) curLine.alpha);

				vertexBufferLine.put((float) curLine.line.getPointN(i + 1).getX());
				vertexBufferLine.put((float) -curLine.line.getPointN(i + 1).getY());
				vertexBufferLine.put((float) curLine.z);
				colorBufferLine.put((float) curLine.color.getRed() / 255);
				colorBufferLine.put((float) curLine.color.getGreen() / 255);
				colorBufferLine.put((float) curLine.color.getBlue() / 255);
				colorBufferLine.put((float) curLine.alpha);
			}

			for ( int i = 0; i < (curLine.line.getNumPoints() - 1) * 2; i++ ) {
				indicesBufferLine.put(indicesLines);
				indicesLines++;
			}

		}
		vertexBufferLine.rewind();
		colorBufferLine.rewind();
		indicesBufferLine.rewind();

	}

	/**
	 * Functions that build a sphere using the unit icosphere and by adding the Polygon p caracteristics
	 *
	 * @param p
	 * @param z_layer
	 * @param radius
	 * @param c
	 * @param alpha
	 */
	public void buildSphereVertexArray(final Polygon p, final double z_layer, final double radius, final Color c,
		final double alpha) {

		this.nbVerticesSphere += vertexCount;

		this.nbIndexSphere += indexCount;

		double z = 0.0;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = z + p.getCoordinate().z;
		}

		for ( int i = 0; i < icosphereVertexBuffer.capacity(); i = i + 3 ) {
			this.sphereTriangles.add((float) (radius * icosphereVertexBuffer.get(i) + p.getCentroid().getX()));
			this.sphereTriangles.add((float) (radius * icosphereVertexBuffer.get(i + 1) - p.getCentroid().getY()));
			this.sphereTriangles.add((float) (radius * icosphereVertexBuffer.get(i + 2) + (z_layer + z)));

			this.sphereNormals.add(icosphereVertexBuffer.get(i));
			this.sphereNormals.add(icosphereVertexBuffer.get(i + 1));
			this.sphereNormals.add(icosphereVertexBuffer.get(i + 2));

			sphereColor.add(c);
			sphereAlpha.add((float) alpha);
		}

		for ( int i = 0; i < icosphereIndexBuffer.capacity(); i++ ) {
			this.sphereIndices.add(icosphereIndexBuffer.get(i) + this.size);
		}
		this.size = this.nbVerticesSphere / 3;
	}

	public void fillSphereVertexArray() {
		spheresVertexBuffer = Buffers.newDirectFloatBuffer(nbVerticesSphere);
		spheresNormalBuffer = Buffers.newDirectFloatBuffer(nbVerticesSphere);
		spheresIndicesBuffer = Buffers.newDirectIntBuffer(nbIndexSphere);
		spheresColorBuffer = Buffers.newDirectFloatBuffer(nbVerticesSphere * 2);

		for ( int i = 0; i < sphereTriangles.size(); i++ ) {
			spheresVertexBuffer.put(sphereTriangles.get(i));
			spheresNormalBuffer.put(sphereNormals.get(i));
		}

		for ( int i = 0; i < sphereTriangles.size() / 3; i++ ) {
			spheresColorBuffer.put(sphereColor.get(i).getRed() / 255);
			spheresColorBuffer.put(sphereColor.get(i).getGreen() / 255);
			spheresColorBuffer.put(sphereColor.get(i).getBlue() / 255);
			spheresColorBuffer.put(sphereAlpha.get(i));
		}

		for ( int i = 0; i < sphereIndices.size(); i++ ) {
			spheresIndicesBuffer.put(sphereIndices.get(i));
		}

		spheresVertexBuffer.rewind();
		spheresNormalBuffer.rewind();
		spheresColorBuffer.rewind();
		spheresIndicesBuffer.rewind();

		sphereNormals.clear();
		sphereTriangles.clear();
		sphereIndices.clear();
		sphereColor.clear();
		sphereAlpha.clear();

		sphereAlpha = new ArrayList<Float>();
		sphereNormals = new ArrayList<Float>();
		sphereTriangles = new ArrayList<Float>();
		sphereIndices = new ArrayList<Integer>();
		sphereColor = new ArrayList<Color>();

	}

	public class TriangleIndices {

		public int v1, v2, v3;

		public TriangleIndices(final int v1, final int v2, final int v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}

	}

	// Calculate the normal, from three points on a surface
	protected double[] CalculateNormal(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
		// Step 1
		// build two vectors, one pointing from A to B, the other pointing from
		// A to C
		double[] vector1 = new double[3];
		double[] vector2 = new double[3];

		vector1[0] = pointB.x - pointA.x;
		vector2[0] = pointC.x - pointA.x;

		vector1[1] = pointB.y - pointA.y;
		vector2[1] = pointC.y - pointA.y;

		vector1[2] = pointB.z - pointA.z;
		vector2[2] = pointC.z - pointA.z;

		// Step 2
		// do the cross product of these two vectors to find the normal
		// of the surface

		double[] normal = new double[3];
		normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
		normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
		normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];

		// Step 3
		// "normalise" the normal (make sure it has length of one)

		double total = 0.0d;
		for ( int i = 0; i < 3; i++ ) {
			total += normal[i] * normal[i];
		}
		double length = FastMath.sqrt(total);

		for ( int i = 0; i < 3; i++ ) {
			normal[i] /= length;
		}

		// done
		return normal;
	}

	/**
	 * Create an isosphere, based on an icosahedron. The number of times
	 * that the faces of the icoasahedron are to be subdivided is given
	 * by the level parameters. The isosphere is considered as an
	 * approximation for the unit sphere. The data for the isosphere
	 * is stored in the instance variables icosphereVertexBuffer,
	 * icosphereFaceBuffer, vertexCount, and indexCount.
	 */
	private void makeIcosphere(final int level) {
		ArrayList<Float> verts = new ArrayList<Float>(); // Vertex data.
		ArrayList<Integer> faceIndx = new ArrayList<Integer>(); // Face data.
		for ( float[] v : icoshedronVertices ) { // Add icosahedron vertex to vertex data.
			verts.add(v[0]);
			verts.add(v[1]);
			verts.add(v[2]);
		}
		for ( int[] f : icoshedronFaces ) {
			// Subdivide each face of the icosahedron the given number of times.
			subdivide(f[0], f[1], f[2], verts, faceIndx, level);
		}
		vertexCount = verts.size();
		indexCount = faceIndx.size();
		icosphereVertexBuffer = Buffers.newDirectFloatBuffer(verts.size());
		for ( float x : verts ) {
			icosphereVertexBuffer.put(x);
		}
		icosphereVertexBuffer.rewind();
		icosphereIndexBuffer = Buffers.newDirectIntBuffer(faceIndx.size());
		for ( int i : faceIndx ) {
			icosphereIndexBuffer.put(i);
		}
		icosphereIndexBuffer.rewind();
	}

	/**
	 * Subdivides a triangular face on the unit sphere and stores the
	 * data for all the (sub-)faces that are generated into the list of
	 * vertex coordinates and the list of vertex indices for faces.
	 * (Note that a given vertex will actually be generated twice, and that
	 * no attempt is made to eliminate this redundancy.)
	 * @param v1 Index in vertex list of the first vertex of the face.
	 * @param v2 Index in vertex list of the second vertex of the face.
	 * @param v3 Index in vertex list of the third vertex of the face.
	 * @param vertices The vertex list.
	 * @param faces The list of vertex indices for each face that is generated.
	 * @param level The number of times the face is to be subdivided.
	 */
	private void subdivide(final int v1, final int v2, final int v3, final ArrayList<Float> vertices,
		final ArrayList<Integer> faces, final int level) {
		if ( level == 0 ) {
			// For level 0, add the vertex indices for this face to the vertex data.
			faces.add(v1);
			faces.add(v2);
			faces.add(v3);
		} else { // Subdivide the face into 4 triangles, and then subdivide
					// each of those triangles (level-1) times. The new vertices
					// that are generated are placed in the vertex list. There
					// is a new vertex half-way between each pair of vertices
					// of the original face.
			float a1 = vertices.get(3 * v1) + vertices.get(3 * v2);
			float a2 = vertices.get(3 * v1 + 1) + vertices.get(3 * v2 + 1);
			float a3 = vertices.get(3 * v1 + 2) + vertices.get(3 * v2 + 2);
			float length = (float) FastMath.sqrt(a1 * a1 + a2 * a2 + a3 * a3);
			a1 /= length;
			a2 /= length;
			a3 /= length;
			int indexA = vertices.size() / 3;
			vertices.add(a1);
			vertices.add(a2);
			vertices.add(a3);

			float b1 = vertices.get(3 * v3) + vertices.get(3 * v2);
			float b2 = vertices.get(3 * v3 + 1) + vertices.get(3 * v2 + 1);
			float b3 = vertices.get(3 * v3 + 2) + vertices.get(3 * v2 + 2);
			length = (float) FastMath.sqrt(b1 * b1 + b2 * b2 + b3 * b3);
			b1 /= length;
			b2 /= length;
			b3 /= length;
			int indexB = vertices.size() / 3;
			vertices.add(b1);
			vertices.add(b2);
			vertices.add(b3);

			float c1 = vertices.get(3 * v1) + vertices.get(3 * v3);
			float c2 = vertices.get(3 * v1 + 1) + vertices.get(3 * v3 + 1);
			float c3 = vertices.get(3 * v1 + 2) + vertices.get(3 * v3 + 2);
			length = (float) FastMath.sqrt(c1 * c1 + c2 * c2 + c3 * c3);
			c1 /= length;
			c2 /= length;
			c3 /= length;
			int indexC = vertices.size() / 3;
			vertices.add(c1);
			vertices.add(c2);
			vertices.add(c3);

			subdivide(v1, indexA, indexC, vertices, faces, level - 1);
			subdivide(indexA, v2, indexB, vertices, faces, level - 1);
			subdivide(indexC, indexB, v3, vertices, faces, level - 1);
			subdivide(indexA, indexB, indexC, vertices, faces, level - 1);
		}
	}

	/**
	 * Function that show the free, total and used memory at a given time.
	 * @param label
	 */
	public static void showMemory(final String label) {
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long total = rt.totalMemory();
		long used = total - free;
		System.out.println(label + " FREE : " + free + " TOTAL : " + total + " USED : " + used);
	}

	/**
	 * Function that load a COLLADA file
	 *
	 *
	 *
	 */
	// public void loadCollada(final String filename) {
	//
	// //COLLADA handler = new COLLADA();
	//
	// SAXParserFactory factory = SAXParserFactory.newInstance();
	// try {
	// SAXParser saxParser = factory.newSAXParser();
	// saxParser.parse("c:\\test2.dae", handler);
	// } catch (ParserConfigurationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SAXException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// handler.ColladaIntoVbo();
	// // handler.printCollada();
	//
	// for ( int i = 0; i < handler.getVertexBufferArray().size(); i++ ) {
	// int[] buffer3 = new int[3];
	// myGl.glGenBuffers(3, buffer3, 0);
	// sphereIndicesBufferID = buffer3[2];
	//
	// myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer3[0]);
	// myGl.glBufferData(GL.GL_ARRAY_BUFFER, handler.getVertexBufferArray().get(i).capacity() *
	// Buffers.SIZEOF_FLOAT, handler.getVertexBufferArray().get(i), GL.GL_STATIC_DRAW);
	// myGl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
	//
	// myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	//
	// myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer3[1]);
	// myGl.glBufferData(GL.GL_ARRAY_BUFFER, handler.getColorsBufferArray().get(i).capacity() *
	// Buffers.SIZEOF_FLOAT, handler.getColorsBufferArray().get(i), GL.GL_STATIC_DRAW);
	// myGl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
	//
	// myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	//
	// myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, buffer3[2]);
	// myGl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, handler.getIndicesBufferArray().get(i).capacity() *
	// Buffers.SIZEOF_INT, handler.getIndicesBufferArray().get(i), GL.GL_STATIC_DRAW);
	//
	// myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	//
	// myGl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
	// myGl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);
	//
	// myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, sphereIndicesBufferID);
	// myGl.glDrawElements(GL.GL_TRIANGLES, handler.getIndicesBufferArray().get(i).capacity(), GL.GL_UNSIGNED_INT,
	// 0);
	//
	// myGl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
	// myGl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	// myGl.glDeleteBuffers(3, buffer3, 0);
	//
	// myGl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
	// myGl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
	// }
	//
	// }

}
