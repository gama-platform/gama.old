package msi.gama.jogl.utils;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;
import msi.gama.jogl.utils.GraphicDataType.MyLine;
import msi.gama.jogl.utils.GraphicDataType.MyTriangulatedGeometry;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.IList;

import com.sun.opengl.util.BufferUtil;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class VertexArrayHandler {
	
	// OpenGL member
	private GL myGl;
	private GLU myGlu;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;
	
	public BasicOpenGlDrawer basicDrawer;

	float alpha = 1.0f;

    private ArrayList<MyTriangulatedGeometry> triangulatedGeometries = new ArrayList<MyTriangulatedGeometry>();
    private ArrayList<MyLine> lines = new ArrayList<MyLine>();
	
    
	private int totalNumVertsTriangle;
	private FloatBuffer vertexBufferTriangle;
	private FloatBuffer colorBufferTriangle;
	
	private int totalNumVertsLine;
	private FloatBuffer vertexBufferLine;
	private FloatBuffer colorBufferLine;
	
	private MyJTSGeometry curGeometry;
	private int nbVerticesLine;
	private int nbVerticesTriangle;
	
	


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

		nbVerticesTriangle = 0;
		nbVerticesLine=0;

		
		Iterator<MyJTSGeometry> it = myJTSGeometries.iterator();

		// Loop over all the geometries, triangulate them and get the total
		// number of vertices.
		while (it.hasNext()) {
			curGeometry = it.next();
			for (int i = 0; i < curGeometry.geometry.getNumGeometries(); i++) {
				
				if (curGeometry.geometry.getGeometryType() == "MultiPolygon") {
					buildMultiPolygonVertexArray((MultiPolygon) curGeometry.geometry,
							curGeometry.z_layer, curGeometry.color,
							curGeometry.alpha,curGeometry.fill, curGeometry.angle, curGeometry.height);
				}
				
				else if (curGeometry.geometry.getGeometryType() == "Polygon") {
					buildPolygonVertexArray((Polygon) curGeometry.geometry,
							curGeometry.z_layer, curGeometry.color,
							curGeometry.alpha,curGeometry.fill, curGeometry.isTextured, curGeometry.angle);
				}
				
				else if (curGeometry.geometry.getGeometryType() == "MultiLineString") {
					buildMultiLineStringVertexArray((MultiLineString) curGeometry.geometry,curGeometry.z_layer, curGeometry.color,curGeometry.alpha);
				}
				
				else if (curGeometry.geometry.getGeometryType() == "LineString") {
					buildLineStringVertexArray((LineString)curGeometry.geometry,curGeometry.z_layer, curGeometry.color,curGeometry.alpha);

				}		
			}	
		}
		fillVertexArrayTriangle();
		fillVertexArrayLine();
		drawVertexArray();
	}
	
	
	public void fillVertexArrayTriangle(){
		
		totalNumVertsTriangle = nbVerticesTriangle;
		vertexBufferTriangle = BufferUtil.newFloatBuffer(nbVerticesTriangle * 3);

		colorBufferTriangle = BufferUtil.newFloatBuffer(nbVerticesTriangle * 3);

		Iterator<MyTriangulatedGeometry> it2 = triangulatedGeometries.iterator();
		// For each triangulated shape
		while (it2.hasNext()) {

			MyTriangulatedGeometry curTriangulatedGeo= it2.next();
			Iterator<IShape> it3 = curTriangulatedGeo.triangles.iterator();
			// For each traingle
			while (it3.hasNext()) {
				IShape curTriangle = it3.next();
				Polygon polygon = (Polygon) curTriangle.getInnerGeometry();
				for (int i = 0; i < 3; i++) {
					vertexBufferTriangle.put((float) polygon.getExteriorRing()
							.getPointN(i).getX());
					vertexBufferTriangle.put((float) -polygon.getExteriorRing()
							.getPointN(i).getY());
					vertexBufferTriangle.put(curTriangulatedGeo.z);
					colorBufferTriangle.put((float)curTriangulatedGeo.color.getRed()/ 255);
					colorBufferTriangle.put((float)curTriangulatedGeo.color.getGreen()/ 255);
					colorBufferTriangle.put((float)curTriangulatedGeo.color.getBlue()/ 255);
				}
			}
		}
		vertexBufferTriangle.rewind();
		colorBufferTriangle.rewind();	
	}
	
	
	public void fillVertexArrayLine(){
		
		totalNumVertsLine = nbVerticesLine;
		vertexBufferLine = BufferUtil.newFloatBuffer(nbVerticesLine*3*2);
		colorBufferLine = BufferUtil.newFloatBuffer(nbVerticesLine*3*2);

		Iterator<MyLine> it = lines.iterator();
		// For each line
		while (it.hasNext()) {

			MyLine curLine= it.next();

				for (int i = 0; i < curLine.line.getNumPoints()-1; i++) {
					vertexBufferLine.put((float) curLine.line.getPointN(i).getX());
					vertexBufferLine.put((float) -curLine.line.getPointN(i).getY());
					vertexBufferLine.put(curLine.z);
					colorBufferLine.put((float)curLine.color.getRed()/ 255);
					colorBufferLine.put((float)curLine.color.getGreen()/ 255);
					colorBufferLine.put((float)curLine.color.getBlue()/ 255);
					
					vertexBufferLine.put((float) curLine.line.getPointN(i+1).getX());
					vertexBufferLine.put((float) -curLine.line.getPointN(i+1).getY());
					vertexBufferLine.put(curLine.z);
					colorBufferLine.put((float)curLine.color.getRed()/ 255);
					colorBufferLine.put((float)curLine.color.getGreen()/ 255);
					colorBufferLine.put((float)curLine.color.getBlue()/ 255);
				}
			
		}
		vertexBufferLine.rewind();
		colorBufferLine.rewind();
		
	}

	public void drawVertexArray() {
		myGl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		myGl.glEnableClientState(GL.GL_COLOR_ARRAY);

		//Triangle vertexArray
		if(vertexBufferTriangle.hasRemaining()){
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexBufferTriangle);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, colorBufferTriangle);
			myGl.glDrawArrays(GL.GL_TRIANGLES, 0, totalNumVertsTriangle);
		}
		
		//Line vertex Array
		if(vertexBufferLine.hasRemaining()){
			myGl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexBufferLine);
			myGl.glColorPointer(3, GL.GL_FLOAT, 0, colorBufferLine);
			myGl.glDrawArrays(GL.GL_LINES, 0, totalNumVertsLine);
		}

		myGl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		myGl.glDisableClientState(GL.GL_COLOR_ARRAY);
	}

	public void DeleteVertexArray() {
		
		if(vertexBufferTriangle != null) vertexBufferTriangle.clear();
		if(colorBufferTriangle != null) colorBufferTriangle.clear();

		if(vertexBufferLine != null)vertexBufferLine.clear();
		if(colorBufferLine != null) colorBufferLine.clear();
		
		if(triangulatedGeometries != null) triangulatedGeometries.clear();
		if(lines != null) lines.clear();
		
	}
	
	
	public void buildMultiPolygonVertexArray(MultiPolygon polygons,float z, Color c,float alpha,
			boolean fill, Integer angle, float elevation){
		
		int numGeometries = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for (int j = 0; j < numGeometries; j++) {
			Polygon curPolygon = (Polygon) curGeometry.geometry.getGeometryN(j);
			MyTriangulatedGeometry curTriangulatedGeometry= new MyTriangulatedGeometry();			
			curTriangulatedGeometry.triangles = GeometryUtils.triangulation(curPolygon);
			curTriangulatedGeometry.z=z;
			curTriangulatedGeometry.color= c;
			curTriangulatedGeometry.alpha=alpha;
			curTriangulatedGeometry.fill=fill;
			curTriangulatedGeometry.angle=angle;
			curTriangulatedGeometry.elevation=elevation;
			triangulatedGeometries.add(curTriangulatedGeometry);
			nbVerticesTriangle = nbVerticesTriangle + curTriangulatedGeometry.triangles.size() * 3;
		}
		
	}
	
	public void buildPolygonVertexArray(Polygon polygon,float z, Color c, float alpha, boolean fill,
			boolean isTextured, Integer angle){
		
		MyTriangulatedGeometry curTriangulatedGeometry= new MyTriangulatedGeometry();
		curTriangulatedGeometry.triangles = GeometryUtils.triangulation(polygon);
		curTriangulatedGeometry.z=z;
		curTriangulatedGeometry.color= c;
		curTriangulatedGeometry.alpha=alpha;
		curTriangulatedGeometry.fill=fill;
		curTriangulatedGeometry.angle=angle;
		triangulatedGeometries.add(curTriangulatedGeometry);
		nbVerticesTriangle = nbVerticesTriangle + curTriangulatedGeometry.triangles.size() * 3;
		
	}
	
	public void buildMultiLineStringVertexArray(MultiLineString multiline,float z, Color c,float alpha){
		
		// get the number of line in the multiline.
		int numGeometries = multiline.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (int i = 0; i < numGeometries; i++) {
			
			MyLine curLine =  new MyLine();
			curLine.line=(LineString) multiline.getGeometryN(i);
			curLine.z=z;
			curLine.color=c;
			curLine.alpha=alpha;
			lines.add(curLine);
			nbVerticesLine=nbVerticesLine+(multiline.getGeometryN(i).getNumPoints()*2);
		}
		
	}
	
	public void buildLineStringVertexArray(LineString line,float z, Color c, float alpha){
		MyLine curLine =  new MyLine();
		curLine.line=line;
		curLine.z=z;
		curLine.color=c;
		curLine.alpha=alpha;
		lines.add(curLine);
		nbVerticesLine=nbVerticesLine+(line.getNumPoints()*2);
	}

}
