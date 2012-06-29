package msi.gama.jogl.utils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.IList;

import com.sun.opengl.util.BufferUtil;
import com.vividsolutions.jts.geom.Polygon;

public class VertexArrayHandler {
	
	// OpenGL member
	private GL myGl;
	private GLU myGlu;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;
	
	public BasicOpenGlDrawer basicDrawer;

	float alpha = 1.0f;


	
	// Use to store VertexArray and indexes

	private ArrayList<VertexArray> vertexArrays = new ArrayList<VertexArray>();

	private int totalNumVerts;
	private FloatBuffer vertexBuffer;
	private FloatBuffer colorBuffer;


	public VertexArrayHandler(final GL gl, final GLU glu,
			final JOGLAWTGLRenderer gLRender) {
		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		basicDrawer= new BasicOpenGlDrawer(myGl, myGlu, myGLRender);
	}
	
	
	/**
	 * Create the vertex array for all JTS geometries by using a triangulation
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildVertexArray(ArrayList<MyJTSGeometry> myJTSGeometries) {

		int nbVertices = 0;

		ArrayList<IList<IShape>> triangulatedGeometries = new ArrayList<IList<IShape>>();
		Iterator<MyJTSGeometry> it = myJTSGeometries.iterator();

		// Loop over all the geometries, triangulate them and get the total
		// number of vertices.
		while (it.hasNext()) {
			MyJTSGeometry curGeometry = it.next();
			if (curGeometry.geometry.getGeometryType() == "Polygon") {
				Polygon polygon = (Polygon) curGeometry.geometry;
				IList<IShape> triangles = GeometryUtils.triangulation(polygon);
				triangulatedGeometries.add(triangles);
				nbVertices = nbVertices + triangles.size() * 3;
			}
		}

		totalNumVerts = nbVertices;
		vertexBuffer = BufferUtil.newFloatBuffer(nbVertices * 3);
		colorBuffer = BufferUtil.newFloatBuffer(nbVertices * 3);

		Iterator<IList<IShape>> it2 = triangulatedGeometries.iterator();
		// For each triangulated shape
		while (it2.hasNext()) {

			Iterator<IShape> it3 = it2.next().iterator();
			// For each traingle
			while (it3.hasNext()) {
				IShape curTriangle = it3.next();
				Polygon polygon = (Polygon) curTriangle.getInnerGeometry();
				for (int i = 0; i < 3; i++) {
					vertexBuffer.put((float) polygon.getExteriorRing()
							.getPointN(i).getX());
					vertexBuffer.put((float) -polygon.getExteriorRing()
							.getPointN(i).getY());
					vertexBuffer.put(0.0f);
					colorBuffer.put(1.0f);
					colorBuffer.put(0.0f);
					colorBuffer.put(0.0f);
				}
			}
		}
		vertexBuffer.rewind();
		colorBuffer.rewind();
		drawVertexArray();
	}

	public void drawVertexArray() {
		myGl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		myGl.glEnableClientState(GL.GL_COLOR_ARRAY);

		myGl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexBuffer);
		myGl.glColorPointer(3, GL.GL_FLOAT, 0, colorBuffer);

		myGl.glDrawArrays(GL.GL_TRIANGLES, 0, totalNumVerts);

		myGl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		myGl.glDisableClientState(GL.GL_COLOR_ARRAY);
	}

	public void DeleteVertexArray() {
		vertexArrays.clear();
	}

}
