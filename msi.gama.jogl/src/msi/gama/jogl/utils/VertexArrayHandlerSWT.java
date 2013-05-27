package msi.gama.jogl.utils;

import java.awt.Color;
import java.nio.DoubleBuffer;
import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.scene.*;
import msi.gama.metamodel.shape.IShape;
import com.sun.opengl.util.BufferUtil;
import com.vividsolutions.jts.geom.*;

public class VertexArrayHandlerSWT {

	// OpenGL member
	private final GL myGl;
	private final GLU myGlu;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLSWTGLRenderer myGLRender;

	public BasicOpenGlDrawerSWT basicDrawer;

	float alpha = 1.0f;

	private final ArrayList<MyTriangulatedGeometry> triangulatedGeometries = new ArrayList<MyTriangulatedGeometry>();
	private final ArrayList<MyLine> lines = new ArrayList<MyLine>();

	private int totalNumVertsTriangle;
	private DoubleBuffer vertexBufferTriangle;
	private DoubleBuffer colorBufferTriangle;

	private int totalNumVertsLine;
	private DoubleBuffer vertexBufferLine;
	private DoubleBuffer colorBufferLine;
	
	private GeometryObject curGeometry;
	private int nbVerticesLine;
	private int nbVerticesTriangle;

	public VertexArrayHandlerSWT(final GL gl, final GLU glu, final JOGLSWTGLRenderer gLRender) {
		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		basicDrawer = new BasicOpenGlDrawerSWT(myGLRender);
	}

	/**
	 * Create the vertex array for all JTS geometries by using a triangulation
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildVertexArray(final List<GeometryObject> list) {

		nbVerticesTriangle = 0;
		nbVerticesLine = 0;

		Iterator<GeometryObject> it = list.iterator();

		// Loop over all the geometries, triangulate them and get the total
		// number of vertices.
		while (it.hasNext()) {
			curGeometry = it.next();
			for ( int i = 0; i < curGeometry.geometry.getNumGeometries(); i++ ) {

				if ( curGeometry.geometry.getGeometryType() == "MultiPolygon" ) {
					buildMultiPolygonVertexArray((MultiPolygon) curGeometry.geometry, curGeometry.z_layer,
						curGeometry.color, curGeometry.alpha, curGeometry.fill, curGeometry.angle, curGeometry.height);
				}

				else if ( curGeometry.geometry.getGeometryType() == "Polygon" ) {
					buildPolygonVertexArray((Polygon) curGeometry.geometry, curGeometry.z_layer, curGeometry.color,
						curGeometry.alpha, curGeometry.fill, curGeometry.isTextured, curGeometry.angle);
				}

				else if ( curGeometry.geometry.getGeometryType() == "MultiLineString" ) {
					buildMultiLineStringVertexArray((MultiLineString) curGeometry.geometry, curGeometry.z_layer,
						curGeometry.color, curGeometry.alpha);
				}

				else if ( curGeometry.geometry.getGeometryType() == "LineString" ) {
					buildLineStringVertexArray((LineString) curGeometry.geometry, curGeometry.z_layer,
						curGeometry.color, curGeometry.alpha);

				}
			}
		}
		fillVertexArrayTriangle();
		fillVertexArrayLine();
		drawVertexArray();
	}

	public void fillVertexArrayTriangle() {

		totalNumVertsTriangle = nbVerticesTriangle;
		vertexBufferTriangle = BufferUtil.newDoubleBuffer(nbVerticesTriangle * 3);

		colorBufferTriangle = BufferUtil.newDoubleBuffer(nbVerticesTriangle * 3);

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
					vertexBufferTriangle.put(curTriangulatedGeo.z);
					colorBufferTriangle.put((float) curTriangulatedGeo.color.getRed() / 255);
					colorBufferTriangle.put((float) curTriangulatedGeo.color.getGreen() / 255);
					colorBufferTriangle.put((float) curTriangulatedGeo.color.getBlue() / 255);
				}
			}
		}
		vertexBufferTriangle.rewind();
		colorBufferTriangle.rewind();
	}

	public void fillVertexArrayLine() {

		totalNumVertsLine = nbVerticesLine;
		vertexBufferLine = BufferUtil.newDoubleBuffer(nbVerticesLine * 3 * 2);
		colorBufferLine = BufferUtil.newDoubleBuffer(nbVerticesLine * 3 * 2);

		Iterator<MyLine> it = lines.iterator();
		// For each line
		while (it.hasNext()) {

			MyLine curLine = it.next();

			for ( int i = 0; i < curLine.line.getNumPoints() - 1; i++ ) {
				vertexBufferLine.put((float) curLine.line.getPointN(i).getX());
				vertexBufferLine.put((float) -curLine.line.getPointN(i).getY());
				vertexBufferLine.put(curLine.z);
				colorBufferLine.put((float) curLine.color.getRed() / 255);
				colorBufferLine.put((float) curLine.color.getGreen() / 255);
				colorBufferLine.put((float) curLine.color.getBlue() / 255);

				vertexBufferLine.put((float) curLine.line.getPointN(i + 1).getX());
				vertexBufferLine.put((float) -curLine.line.getPointN(i + 1).getY());
				vertexBufferLine.put(curLine.z);
				colorBufferLine.put((float) curLine.color.getRed() / 255);
				colorBufferLine.put((float) curLine.color.getGreen() / 255);
				colorBufferLine.put((float) curLine.color.getBlue() / 255);
			}

		}
		vertexBufferLine.rewind();
		colorBufferLine.rewind();

	}

	public void drawVertexArray() {
		myGl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		myGl.glEnableClientState(GL.GL_COLOR_ARRAY);

		// Triangle vertexArray
		if ( vertexBufferTriangle.hasRemaining() ) {
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexBufferTriangle);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, colorBufferTriangle);
			myGl.glDrawArrays(GL.GL_TRIANGLES, 0, totalNumVertsTriangle);
		}

		// Line vertex Array
		if ( vertexBufferLine.hasRemaining() ) {
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexBufferLine);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, colorBufferLine);
			myGl.glDrawArrays(GL.GL_LINES, 0, totalNumVertsLine);
		}

		myGl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		myGl.glDisableClientState(GL.GL_COLOR_ARRAY);
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

	public void buildMultiPolygonVertexArray(final MultiPolygon polygons, final double z, final Color c,
		final double alpha, final boolean fill, final Integer angle, final double elevation) {

		int numGeometries = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for ( int j = 0; j < numGeometries; j++ ) {
			Polygon curPolygon = (Polygon) curGeometry.geometry.getGeometryN(j);
			MyTriangulatedGeometry curTriangulatedGeometry = new MyTriangulatedGeometry();
			curTriangulatedGeometry.triangles = GeometryUtils.triangulation(null, curPolygon); // VERIFY
																								// NULL
																								// SCOPE
			curTriangulatedGeometry.z = z;
			curTriangulatedGeometry.color = c;
			curTriangulatedGeometry.alpha = alpha;
			curTriangulatedGeometry.fill = fill;
			curTriangulatedGeometry.angle = angle;
			curTriangulatedGeometry.elevation = elevation;
			triangulatedGeometries.add(curTriangulatedGeometry);
			nbVerticesTriangle = nbVerticesTriangle + curTriangulatedGeometry.triangles.size() * 3;
		}

	}

	public void buildPolygonVertexArray(final Polygon polygon, final double z, final Color c, final double alpha,
		final boolean fill, final boolean isTextured, final Integer angle) {

		MyTriangulatedGeometry curTriangulatedGeometry = new MyTriangulatedGeometry();
		curTriangulatedGeometry.triangles = GeometryUtils.triangulation(null, polygon); // VERIFY
																						// NULL
																						// SCOPE
		curTriangulatedGeometry.z = z;
		curTriangulatedGeometry.color = c;
		curTriangulatedGeometry.alpha = alpha;
		curTriangulatedGeometry.fill = fill;
		curTriangulatedGeometry.angle = angle;
		triangulatedGeometries.add(curTriangulatedGeometry);
		nbVerticesTriangle = nbVerticesTriangle + curTriangulatedGeometry.triangles.size() * 3;

	}

	public void buildMultiLineStringVertexArray(final MultiLineString multiline, final double z, final Color c,
		final double alpha2) {

		// get the number of line in the multiline.
		int numGeometries = multiline.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {

			MyLine curLine = new MyLine();
			curLine.line = (LineString) multiline.getGeometryN(i);
			curLine.z = z;
			curLine.color = c;
			curLine.alpha = alpha2;
			lines.add(curLine);
			nbVerticesLine = nbVerticesLine + multiline.getGeometryN(i).getNumPoints() * 2;
		}

	}

	public void buildLineStringVertexArray(final LineString line, final double z, final Color c, final double alpha2) {
		MyLine curLine = new MyLine();
		curLine.line = line;
		curLine.z = z;
		curLine.color = c;
		curLine.alpha = alpha2;
		lines.add(curLine);
		nbVerticesLine = nbVerticesLine + line.getNumPoints() * 2;
	}

}
