package msi.gama.jogl.utils.JTSGeometryOpenGLDrawer;

import static javax.media.opengl.GL.GL_CLIP_PLANE0;
import static javax.media.opengl.GL.GL_CLIP_PLANE1;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL.GL_POINTS;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_TRIANGLE_FAN;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;
import javax.vecmath.Vector3d;

import msi.gama.common.util.GeometryUtils;
import msi.gama.common.util.ImageUtils;
import msi.gama.jogl.scene.GeometryObject;
import msi.gama.jogl.scene.MyTexture;
import msi.gama.jogl.utils.GLUtilNormal;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Vertex;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gama.util.IList;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

public class JTSDrawer {

	// OpenGL member
	private final GL gl;
	private final GLU myGlu;
	private final GLUT myGlut;
	public TessellCallBack tessCallback;
	private final GLUtessellator tobj;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	JTSVisitor visitor;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numGeometries;

	double tempPolygon[][];
	double temp[];

	// Use for JTS triangulation
	IList<IShape> triangles;
	Iterator<IShape> it;

	// USe to inverse y composaant
	public int yFlag;

	/** The earth texture. */
	// private Texture earthTexture;
	public float textureTop, textureBottom, textureLeft, textureRight;
	public Texture[] textures = new Texture[3];
	// Use for texture mapping;
	BufferedImage image = null;
	MyTexture texture = null;

	public boolean colorpicking = false;
	
	public JTSDrawer(final JOGLAWTGLRenderer gLRender) {

		gl = gLRender.gl;
		myGlu = gLRender.glu;
		myGlut = new GLUT();
		myGLRender = gLRender;
		tessCallback = new TessellCallBack(gl, myGlu);
		tobj = myGlu.gluNewTess();

		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);

		visitor = new JTSVisitor(gl);

		yFlag = -1;

		// FIXME: When using erroCallback there is a out of memory problem.
		// myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);//
		// errorCallback)

	}

	public void drawMultiPolygon(final MultiPolygon polygons, final Color c, final double alpha, final boolean fill,
		final Color border, final boolean isTextured, final IList<String> textureFileNames,/* final Integer angle, */
		final double height, final boolean rounded, final double z_fighting_value) {


		numGeometries = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {
			curPolygon = (Polygon) polygons.getGeometryN(i);

			if ( height > 0 ) {
				DrawPolyhedre(curPolygon, c, alpha, fill, height,/* angle, */false, border, isTextured,
					textureFileNames, rounded, z_fighting_value);
			} else {
				DrawPolygon(curPolygon, c, alpha, fill, border, isTextured, textureFileNames, /* angle, */true, rounded,
					z_fighting_value,1);
			}
		}
	}


	public void DrawPolygon(final Polygon p, final Color c, final double alpha, final boolean fill, final Color border,
		final boolean isTextured, final IList<String> textureFileNames,/* final Integer angle, */
		final boolean drawPolygonContour, final boolean rounded, final double z_fighting_value, final int norm_dir) {
        // calculate the normal vectors for each of the polygonal facets and then average the normal
		if(myGLRender.computeNormal){			
			Vertex[] vertices = getExteriorRingVertices(p);
			GLUtilNormal.HandleNormal(vertices,c,alpha,norm_dir,myGLRender);
		}

		if ( isTextured == false ) {

			if ( fill == true ) {

				if ( !colorpicking ) {
					gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
						alpha * c.getAlpha() / 255);
				}

				// Draw rectangle with curved corner (only work for rectangle)
				if ( rounded == true ) {
					drawRoundRectangle(p);
				} else {
					if ( myGLRender.getTessellation() ) {
						DrawTesselatedPolygon(p,norm_dir,c,alpha);
						if ( drawPolygonContour == true ) {
							DrawPolygonContour(p, border, z_fighting_value);
						}
					}
					// use JTS triangulation on simplified geometry (DouglasPeucker)
					// FIXME: not working with a z_layer value!!!!
					else {
						drawTriangulatedPolygon(p, myGLRender.JTSTriangulation);
						gl.glColor4d(0.0d, 0.0d, 0.0d, alpha);
						if ( drawPolygonContour == true ) {
							DrawPolygonContour(p, border, z_fighting_value);
						}
					}
				}
			}
			// fill = false. Draw only the contour of the polygon.
			else {
				boolean testZFight = false;
				if ( !testZFight ) {

					// if no border has been define draw empty shape with their original color
					if ( border.equals(Color.black) ) {
						DrawPolygonContour(p, c, z_fighting_value);
					} else {
						DrawPolygonContour(p, border, z_fighting_value);
					}
				} else {
					gl.glBegin(GL.GL_QUADS);
					gl.glVertex3d(p.getExteriorRing().getCoordinateN(0).x, -p.getExteriorRing().getCoordinateN(0).y, p
						.getExteriorRing().getCoordinateN(0).z);
					gl.glVertex3d(p.getExteriorRing().getCoordinateN(1).x, -p.getExteriorRing().getCoordinateN(1).y, p
						.getExteriorRing().getCoordinateN(1).z);
					gl.glVertex3d(p.getExteriorRing().getCoordinateN(2).x, -p.getExteriorRing().getCoordinateN(2).y, p
						.getExteriorRing().getCoordinateN(2).z);
					gl.glVertex3d(p.getExteriorRing().getCoordinateN(3).x, -p.getExteriorRing().getCoordinateN(3).y, p
						.getExteriorRing().getCoordinateN(3).z);
					gl.glEnd();
				}
			}
		}

		// FIXME: Need to check that the polygon is a quad
		else {
			try {
				image = ImageUtils.getInstance().getImageFromFile(textureFileNames.get(0));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			texture = myGLRender.getScene().getTextures().get(image);
			if ( texture == null ) { // If the texture has not been created yet we created it and put it in the map of
										// texture.
				texture = myGLRender.createTexture(image, false, 0);
				myGLRender.getScene().getTextures().put(image, texture);
			}

			DrawTexturedPolygon(p, /* angle, */texture.texture);
		}
	}

	void DrawTesselatedPolygon(final Polygon p, int norm_dir,Color c, double alpha) {

		myGlu.gluTessBeginPolygon(tobj, null);

		// Exterior contour
		myGlu.gluTessBeginContour(tobj);
		
		if(myGLRender.computeNormal){
		   Vertex[] vertices = getExteriorRingVertices(p);
		   
		   double[] normalmean = new double[3];
			for (int i= 0; i< vertices.length-2;i++){
				double[] normal = GLUtilNormal.CalculateNormal(vertices[i+2], vertices[i+1], vertices[i]);
				normalmean[0]= (normalmean[0] + normal[0])/(i+1);
				normalmean[1]= (normalmean[1] + normal[1])/(i+1);
				normalmean[2]= (normalmean[2] + normal[2])/(i+1);
			}	
		   
		   if(myGLRender.getDrawNorm()){
				Vertex center = GLUtilNormal.GetCenter(vertices);
				gl.glBegin(GL_LINES);
			    gl.glColor3d(1.0, 0.0, 0.0);
			    gl.glVertex3d(center.x, center.y, center.z);		   
			    gl.glVertex3d(center.x + normalmean[0] *norm_dir, center.y + normalmean[1]*norm_dir, center.z + normalmean[2]*norm_dir);
			    gl.glEnd();

				gl.glPointSize(2.0f);
				gl.glBegin(GL_POINTS);
				gl.glVertex3d(center.x + normalmean[0]*norm_dir, center.y + normalmean[1]*norm_dir, center.z + normalmean[2]*norm_dir);
				gl.glEnd();
				
				if ( !colorpicking ) {
					if(c!=null){
						gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
								alpha * c.getAlpha() / 255);
					}
					
				}
			}
			
			myGlu.gluTessNormal(tobj, normalmean[0]*norm_dir, normalmean[1]*norm_dir, normalmean[2]*norm_dir);
		}


		tempPolygon = new double[p.getExteriorRing().getNumPoints()][3];
		// Convert vertices as a list of double for gluTessVertex
		for ( int j = 0; j < p.getExteriorRing().getNumPoints(); j++ ) {
			tempPolygon[j][0] = p.getExteriorRing().getPointN(j).getX();
			tempPolygon[j][1] = yFlag * p.getExteriorRing().getPointN(j).getY();

			if ( Double.isNaN(p.getExteriorRing().getPointN(j).getCoordinate().z) == true ) {
				tempPolygon[j][2] = 0.0d;
			} else {
				tempPolygon[j][2] = 0.0d + p.getExteriorRing().getPointN(j).getCoordinate().z;
			}
		}

		for ( int j = 0; j < p.getExteriorRing().getNumPoints(); j++ ) {
			myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}

		myGlu.gluTessEndContour(tobj);

		// interior contour
		for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
			myGlu.gluTessBeginContour(tobj);
			int numIntPoints = p.getInteriorRingN(i).getNumPoints();
			tempPolygon = new double[numIntPoints][3];
			// Convert vertices as a list of double for gluTessVertex
			for ( int j = 0; j < numIntPoints; j++ ) {
				tempPolygon[j][0] = p.getInteriorRingN(i).getPointN(j).getX();
				tempPolygon[j][1] = yFlag * p.getInteriorRingN(i).getPointN(j).getY();

				if ( Double.isNaN(p.getInteriorRingN(i).getPointN(j).getCoordinate().z) == true ) {
					tempPolygon[j][2] = 0.0d;
				} else {
					tempPolygon[j][2] = 0.0d + p.getInteriorRingN(i).getPointN(j).getCoordinate().z;
				}
			}

			for ( int j = 0; j < numIntPoints; j++ ) {
				myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
			}
			myGlu.gluTessEndContour(tobj);
		}

		myGlu.gluTessEndPolygon(tobj);
	}

	void drawTriangulatedPolygon(Polygon p, final boolean showTriangulation) {
		boolean simplifyGeometry = false;
		if ( simplifyGeometry ) {
			double sizeTol = Math.sqrt(p.getArea()) / 100.0;
			Geometry g2 = DouglasPeuckerSimplifier.simplify(p, sizeTol);
			if ( g2 instanceof Polygon ) {
				p = (Polygon) g2;
			}
		}
		// Workaround to compute the z value of each triangle as triangulation
		// create new point during the triangulation that are set with z=NaN
		if ( p.getNumPoints() > 4 ) {
			triangles = GeometryUtils.triangulation(null, p); // VERIFY NULL SCOPE

			GamaList<Geometry> segments = new GamaList<Geometry>();
			for ( int i = 0; i < p.getNumPoints() - 1; i++ ) {
				Coordinate[] cs = new Coordinate[2];
				cs[0] = p.getCoordinates()[i];
				cs[1] = p.getCoordinates()[i + 1];
				segments.add(GeometryUtils.FACTORY.createLineString(cs));
			}
			for ( IShape tri : triangles ) {
				for ( int i = 0; i < tri.getInnerGeometry().getNumPoints(); i++ ) {
					Coordinate coord = tri.getInnerGeometry().getCoordinates()[i];
					if ( Double.isNaN(coord.z) ) {
						Point pt = GeometryUtils.FACTORY.createPoint(coord);
						double distMin = Double.MAX_VALUE;
						Geometry closestSeg = null;
						for ( Geometry seg : segments ) {
							double dist = seg.distance(pt);
							if ( dist < distMin ) {
								distMin = dist;
								closestSeg = seg;
							}
						}
						Point pt1 = GeometryUtils.FACTORY.createPoint(closestSeg.getCoordinates()[0]);
						Point pt2 = GeometryUtils.FACTORY.createPoint(closestSeg.getCoordinates()[1]);

						double dist1 = pt.distance(pt1);
						double dist2 = pt.distance(pt2);
						// FIXME: Work only for geometry
						coord.z =
							(1 - dist1 / closestSeg.getLength()) * closestSeg.getCoordinates()[0].z +
								(1 - dist2 / closestSeg.getLength()) * closestSeg.getCoordinates()[1].z;
						DrawShape(tri, showTriangulation);
					}
				}
			}
		} else if ( p.getNumPoints() == 4 ) {
			triangles = new GamaList<IShape>();
			triangles.add(new GamaShape(p));
		}
		for ( IShape tri : triangles ) {
			DrawShape(tri, showTriangulation);
		}
	}

	//FIXME: This function only work for quad (otherwise it draw a gray polygon)
	void DrawTexturedPolygon(final Polygon p,/* final int angle, */final Texture texture) {

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glColor3d(1.0, 1.0, 1.0);// Set the color to white to avoid color and texture mixture
		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the
		// current GL context's state.
		myGLRender.getContext().makeCurrent();
		texture.enable();
		// Binds this texture to the current GL context.
		texture.bind();

		if(p.getNumPoints()>5){
			DrawTesselatedPolygon(p,1,null,1.0);
		}
		else{
			Vertex[] vertices =  this.getExteriorRingVertices(p);
			if(myGLRender.computeNormal){
				GLUtilNormal.HandleNormal(vertices,null,0,1,myGLRender);
			}
			gl.glColor3d(1.0, 1.0, 1.0);// Set the color to white to avoid color and texture mixture
			
			gl.glBegin(GL_QUADS);

			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);

			gl.glTexCoord2f(1.0f, 1.0f);;
			gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);

			gl.glTexCoord2f(1.0f, 0.0f);;
			gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);

			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);

			gl.glEnd();
		}
				
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	public void DrawPolygonContour(final Polygon p, final Color border, final double z_fighting_value) {

		// FIXME: when rendering with this method the triangulation does not work anymore
		if ( myGLRender.getZFighting() ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			// }

			// myGl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
			gl.glPolygonOffset(0.0f, -(float) (z_fighting_value * 1.1));
			// myGl.glPolygonOffset(0.0f,10.0f);
			gl.glBegin(GL.GL_POLYGON);
			if ( !colorpicking ) {
				gl.glColor4d((double) border.getRed() / 255, (double) border.getGreen() / 255,
					(double) border.getBlue() / 255, 1.0d);
			}
			p.getExteriorRing().apply(visitor);
			gl.glEnd();

			if ( p.getNumInteriorRing() > 0 ) {
				// Draw Interior ring
				for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
					gl.glBegin(GL.GL_POLYGON);
					p.getInteriorRingN(i).apply(visitor);
					gl.glEnd();
				}
			}

			// myGl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			// myGl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glDisable(GL.GL_POLYGON_OFFSET_LINE);
			if ( !myGLRender.triangulation ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			}
		} else {
			gl.glBegin(GL.GL_LINES);
			if ( !colorpicking ) {
				gl.glColor4d((double) border.getRed() / 255, (double) border.getGreen() / 255,
					(double) border.getBlue() / 255, 1.0d);
			}
			p.getExteriorRing().apply(visitor);
			gl.glEnd();

			if ( p.getNumInteriorRing() > 0 ) {
				// Draw Interior ring
				for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
					gl.glBegin(GL.GL_LINES);
					p.getInteriorRingN(i).apply(visitor);
					gl.glEnd();
				}
			}
		}
	}

	void SetLine(final Point src, final Point dest, final double z, final boolean hasZValue) {
		if ( hasZValue == false ) {
			gl.glVertex3d(src.getX(), yFlag * src.getY(), z);
			gl.glVertex3d(dest.getX(), yFlag * dest.getY(), z);
		} else {
			gl.glVertex3d(src.getX(), yFlag * src.getY(), z + src.getCoordinate().z);
			gl.glVertex3d(dest.getX(), yFlag * dest.getY(), z + dest.getCoordinate().z);
		}
	}

	public void DrawPolyhedre(final Polygon p, final Color c, final double alpha, final boolean fill,
		final double height, /* final Integer angle, */final boolean drawPolygonContour, final Color border,
		final boolean isTextured, final IList<String> textureFileNames, final boolean rounded,
		final Double z_fighting_value) {
		
		int face_norm_dir=-1;
		int p_norm_dir=1;;
		if(this.myGLRender.computeNormal){
		Vertex[] vertices = getExteriorRingVertices(p);
			if(IsClockwise(vertices)){
				face_norm_dir = -1;
				p_norm_dir =1;
			}
			else{
				face_norm_dir =1;
				p_norm_dir= -1;
			}
		}
		
		DrawPolygon(p, c, alpha, fill, border, isTextured, textureFileNames/* ,angle */, drawPolygonContour, rounded,
			z_fighting_value,-p_norm_dir);
		gl.glTranslated(0, 0, height);
		DrawPolygon(p, c, alpha, fill, border, isTextured, textureFileNames/* ,angle */, drawPolygonContour, rounded,
			z_fighting_value,p_norm_dir);
		gl.glTranslated(0, 0, -height);
		// FIXME : Will be wrong if angle =!0

		if ( isTextured ) {
			if ( textureFileNames.size() > 1 ) {
				DrawTexturedFaces(p, c, alpha, fill, border, isTextured, textureFileNames.get(1), height,
					drawPolygonContour);
			} else {
				DrawTexturedFaces(p, c, alpha, fill, border, isTextured, textureFileNames.get(0), height,
					drawPolygonContour);
			}

		} else {
			DrawFaces(p, c, alpha, fill, border, isTextured, height, drawPolygonContour,face_norm_dir);
		}

	}

	// //////////////////////////////FACE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Given a polygon this will draw the different faces of the 3D polygon.
	 * 
	 * @param p
	 *            :Base polygon
	 * @param c
	 *            : color
	 * @param height
	 *            : height of the polygon
	 */
	public void DrawFaces(final Polygon p, final Color c, final double alpha, final boolean fill, final Color b,
		final boolean isTextured, final double height, final boolean drawPolygonContour,final int norm_dir) {

		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				alpha * c.getAlpha() / 255);
		}

		double elevation = 0.0d;

		if ( Double.isNaN(p.getExteriorRing().getPointN(0).getCoordinate().z) == false ) {
			elevation = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();


		for ( int j = 0; j < curPolyGonNumPoints; j++ ) {
			int k = (j + 1) % curPolyGonNumPoints;
			Vertex[] vertices = getFaceVertices(p, j, k, elevation, height);

			if ( fill ) {
				
				if(myGLRender.computeNormal){
					GLUtilNormal.HandleNormal(vertices,c,alpha,norm_dir,myGLRender);
				}
				gl.glBegin(GL.GL_QUADS);
					gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
					gl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
					gl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
					gl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
				gl.glEnd();
			}

			if ( drawPolygonContour == true || fill == false ) {

				if ( !colorpicking ) {
					gl.glColor4d((double) b.getRed() / 255, (double) b.getGreen() / 255, (double) b.getBlue() / 255,
						alpha * c.getAlpha() / 255);
				}
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				gl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
				gl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
				gl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
				gl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
				gl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
				gl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
				gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				gl.glEnd();
				if ( !colorpicking ) {
					gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
						alpha * c.getAlpha() / 255);
				}
			}
		}
	}

	/**
	 * Given a polygon this will draw the different faces of the 3D polygon.
	 * 
	 * @param p
	 *            :Base polygon
	 * @param c
	 *            : color
	 * @param height
	 *            : height of the polygon
	 */
	public void DrawTexturedFaces(final Polygon p, final Color c, final double alpha, final boolean fill,
		final Color b, final boolean isTextured, final String textureFileName, final double height,
		final boolean drawPolygonContour) {
		try {
			image = ImageUtils.getInstance().getImageFromFile(textureFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		texture = myGLRender.getScene().getTextures().get(image);
		if ( texture == null ) { // If the texture has not been created yet we created it and put it in the map of
									// texture.
			texture = myGLRender.createTexture(image, false, 0);
			myGLRender.getScene().getTextures().put(image, texture);
		}

		gl.glEnable(GL.GL_TEXTURE_2D);
		
		myGLRender.getContext().makeCurrent();
		texture.texture.enable();
		texture.texture.bind();

		double elevation = 0.0d;

		if ( Double.isNaN(p.getExteriorRing().getPointN(0).getCoordinate().z) == false ) {
			elevation = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

		for ( int j = 0; j < curPolyGonNumPoints; j++ ) {

			int k = (j + 1) % curPolyGonNumPoints;

			Vertex[] vertices = getFaceVertices(p, j, k, elevation, height);

			if(myGLRender.computeNormal){
				GLUtilNormal.HandleNormal(vertices,null,0,1,myGLRender);
			}
			gl.glColor3d(0.25, 0.25, 0.25);// Set the color to white to avoid color and texture mixture
			gl.glBegin(GL.GL_QUADS);
				gl.glColor3d(1.0, 1.0, 1.0);// Set the color to white to avoid color and texture mixture
				gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
				gl.glTexCoord2f(1.0f, 1.0f);
				gl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
			gl.glEnd();

		}
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	public Vertex[] getFaceVertices(final Polygon p, final int j, final int k, final double elevation,
		final double height) {
		// Build the 4 vertices of the face.
		Vertex[] vertices = new Vertex[4];
		for ( int i = 0; i < 4; i++ ) {
			vertices[i] = new Vertex();
		}
		// FIXME; change double to double in Vertex
		vertices[0].x = p.getExteriorRing().getPointN(j).getX();
		vertices[0].y = yFlag * p.getExteriorRing().getPointN(j).getY();
		vertices[0].z = elevation + height;

		vertices[1].x = p.getExteriorRing().getPointN(k).getX();
		vertices[1].y = yFlag * p.getExteriorRing().getPointN(k).getY();
		vertices[1].z = elevation + height;

		vertices[2].x = p.getExteriorRing().getPointN(k).getX();
		vertices[2].y = yFlag * p.getExteriorRing().getPointN(k).getY();
		vertices[2].z = elevation;

		vertices[3].x = p.getExteriorRing().getPointN(j).getX();
		vertices[3].y = yFlag * p.getExteriorRing().getPointN(j).getY();
		vertices[3].z = elevation;

		return vertices;
	}

	public Vertex[] getTriangleVertices(final Polygon p) {
			// Build the 3 vertices of the face from the 3 first point (maybe wrong in some case).
			Vertex[] vertices = new Vertex[3];
			for ( int i = 0; i < 3; i++ ) {
				vertices[i] = new Vertex();
			}
			// FIXME; change double to double in Vertex
			vertices[0].x = p.getExteriorRing().getPointN(0).getX();
			vertices[0].y = yFlag * p.getExteriorRing().getPointN(0).getY();
			vertices[0].z = p.getExteriorRing().getPointN(0).getCoordinate().z;

			vertices[1].x = p.getExteriorRing().getPointN(1).getX();
			vertices[1].y = yFlag * p.getExteriorRing().getPointN(1).getY();
			vertices[1].z = p.getExteriorRing().getPointN(1).getCoordinate().z;

			vertices[2].x = p.getExteriorRing().getPointN(2).getX();
			vertices[2].y = yFlag * p.getExteriorRing().getPointN(2).getY();
			vertices[2].z = p.getExteriorRing().getPointN(2).getCoordinate().z;

			return vertices;
	}
	
	public Vertex[] getExteriorRingVertices(final Polygon p) {
		// Build the n vertices of the facet of the polygon.
		Vertex[] vertices = new Vertex[p.getExteriorRing().getNumPoints()-1];
		for ( int i = 0; i < p.getExteriorRing().getNumPoints()-1; i++ ) {
			vertices[i] = new Vertex();
			vertices[i].x = p.getExteriorRing().getPointN(i).getX();
			vertices[i].y = yFlag * p.getExteriorRing().getPointN(i).getY();
			vertices[i].z = p.getExteriorRing().getPointN(i).getCoordinate().z;
		}
		return vertices;
}

	// ////////////////////////////// LINE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void DrawMultiLineString(final MultiLineString lines, final double z, final Color c, final double alpha,
		final double height) {

		// get the number of line in the multiline.
		numGeometries = lines.getNumGeometries();

		// FIXME: Why setting the color here?
		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				alpha * c.getAlpha() / 255);
		}

		// for each line of a multiline, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {

			LineString l = (LineString) lines.getGeometryN(i);
			if ( height > 0 ) {
				drawPlan(l, z, c, alpha, height, 0, true);
			} else {
				drawLineString(l, z, 1.2f, c, alpha);
			}

		}
	}

	public void drawLineString(final LineString line, final double z, final double size, final Color c,
		final double alpha) {

		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				alpha * c.getAlpha() / 255);
		}

		int numPoints = line.getNumPoints();

		gl.glLineWidth((float) size);

		// Add z value (if the whole line as a z value (add_z)
		/*
		 * if (Double.isNaN (line.getCoordinate().z) == false) {
		 * z = z + (double) line.getCoordinate().z; }
		 */

		// FIXME: this will draw a 3d line if the z value of each point has been
		// set thanks to add_z_pt but if
		gl.glBegin(GL.GL_LINES);
		for ( int j = 0; j < numPoints - 1; j++ ) {

			if ( Double.isNaN(line.getPointN(j).getCoordinate().z) == true ) {
				gl.glVertex3d(line.getPointN(j).getX(), yFlag * line.getPointN(j).getY(), z);

			} else {
				gl.glVertex3d(line.getPointN(j).getX(), yFlag * line.getPointN(j).getY(), z +
					line.getPointN(j).getCoordinate().z);
			}
			if ( Double.isNaN(line.getPointN(j + 1).getCoordinate().z) == true ) {
				gl.glVertex3d(line.getPointN(j + 1).getX(), yFlag * line.getPointN(j + 1).getY(), z);
			} else {
				gl.glVertex3d(line.getPointN(j + 1).getX(), yFlag * line.getPointN(j + 1).getY(),
					z + line.getPointN(j + 1).getCoordinate().z);
			}

		}
		gl.glEnd();

	}

	public void drawPlan(final LineString l, double z, final Color c, final double alpha, final double height,
		final Integer angle, final boolean drawPolygonContour) {

		drawLineString(l, z, 1.2f, c, alpha);
		drawLineString(l, z + height, 1.2f, c, alpha);

		// Draw a quad
		gl.glColor4d(c.getRed() / 255, c.getGreen() / 255, c.getBlue() / 255, alpha * c.getAlpha() / 255);
		int numPoints = l.getNumPoints();

		// Add z value
		if ( Double.isNaN(l.getCoordinate().z) == false ) {
			z = z + l.getCoordinate().z;
		}
		
		
		for ( int j = 0; j < numPoints - 1; j++ ) {
			
			if(myGLRender.computeNormal){
				Vertex[] vertices = new Vertex[3];
				for ( int i = 0; i < 3; i++ ) {
					vertices[i] = new Vertex();
				}
				vertices[0].x = l.getPointN(j).getX();
				vertices[0].y = yFlag * l.getPointN(j).getY();
				vertices[0].z = z;
				
				vertices[1].x = l.getPointN(j+1).getX();
				vertices[1].y = yFlag * l.getPointN(j+1).getY();
				vertices[1].z = z;
				
				vertices[2].x = l.getPointN(j+1).getX();
				vertices[2].y = yFlag * l.getPointN(j+1).getY();
				vertices[2].z = z+height;
				GLUtilNormal.HandleNormal(vertices,c,alpha,1,myGLRender);
			}
			
			gl.glBegin(GL.GL_QUADS);
				gl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z + height);
				gl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z + height);
			gl.glEnd();
		}

		if ( drawPolygonContour == true ) {
			if ( !colorpicking ) {
				gl.glColor4d(0.0d, 0.0d, 0.0d, alpha * c.getAlpha() / 255);
			}

			for ( int j = 0; j < numPoints - 1; j++ ) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z);

				gl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z + height);

				gl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z + height);
				gl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z + height);

				gl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z + height);
				gl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z);

				gl.glEnd();
			}
			if ( !colorpicking ) {
				gl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, alpha);
			}

		}
	}

	public void DrawPoint(final Point point, double z, final int numPoints, final double radius, final Color c,
		final double alpha) {
		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				alpha * c.getAlpha() / 255);
		}

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);
		// FIXME: Does not work for Point.
		// Add z value
		if ( Double.isNaN(point.getCoordinate().z) == false ) {
			z = z + point.getCoordinate().z;
		}

		double angle;
		double tempPolygon[][] = new double[100][3];
		for ( int k = 0; k < numPoints; k++ ) {
			angle = k * 2 * Math.PI / numPoints;

			tempPolygon[k][0] = point.getCoordinate().x + Math.cos(angle) * radius;
			tempPolygon[k][1] = yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			tempPolygon[k][2] = z;
		}

		for ( int k = 0; k < numPoints; k++ ) {
			myGlu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		if ( !colorpicking ) {
			gl.glColor4d(0.0d, 0.0d, 0.0d, alpha * c.getAlpha() / 255);
		}
		gl.glLineWidth(1.1f);
		gl.glBegin(GL.GL_LINES);
		double xBegin, xEnd, yBegin, yEnd;
		for ( int k = 0; k < numPoints; k++ ) {
			angle = k * 2 * Math.PI / numPoints;
			xBegin = point.getCoordinate().x + Math.cos(angle) * radius;
			yBegin = yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			angle = (k + 1) * 2 * Math.PI / numPoints;
			xEnd = point.getCoordinate().x + Math.cos(angle) * radius;
			yEnd = yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			gl.glVertex3d(xBegin, yBegin, z);
			gl.glVertex3d(xEnd, yEnd, z);
		}
		gl.glEnd();

	}

	// //////////////////////////////SPECIAL 3D SHAPE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void drawSphere(final GeometryObject g) {
		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				g.getAlpha() * c.getAlpha() / 255);
		}

		GLUquadric quad = myGlu.gluNewQuadric();
		if ( !myGLRender.triangulation ) {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
	
		myGlu.gluSphere(quad, g.height, slices, stacks);
		myGlu.gluDeleteQuadric(quad);
		gl.glTranslated(-p.getCentroid().getX(), -yFlag * p.getCentroid().getY(), -z);


	}
	
	public void drawSphere(final GeometryObject g, double z) {
		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		Geometry p = g.geometry;
		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				g.getAlpha() * c.getAlpha() / 255);
		}

		GLUquadric quad = myGlu.gluNewQuadric();
		if ( !myGLRender.triangulation ) {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
	
		myGlu.gluSphere(quad, g.height, slices, stacks);
		myGlu.gluDeleteQuadric(quad);
		gl.glTranslated(-p.getCentroid().getX(), -yFlag * p.getCentroid().getY(), -z);


	}
	
	public void drawHemiSphere(final GeometryObject g) {
		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				g.getAlpha() * c.getAlpha() / 255);
		}

		GLUquadric quad = myGlu.gluNewQuadric();
		if ( !myGLRender.triangulation ) {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		
		
        double angle = (g.ratio*360);
        if(angle%360 <=180){
        	gl.glEnable(GL_CLIP_PLANE0);
    		gl.glEnable(GL_CLIP_PLANE1);
    		
    		gl.glColor4d(1.0,0.0,0.0,1.0);
    		gl.glClipPlane(GL_CLIP_PLANE0, new double[]{1,0,0,0},0);
    		gl.glClipPlane(GL_CLIP_PLANE1, new double[]{-Math.cos((angle* Math.PI)/180),Math.sin((angle* Math.PI)/180),0,0},0);
    		myGlu.gluSphere(quad, g.height*1.05, slices, stacks);
    		
    		gl.glDisable(GL_CLIP_PLANE0);
    		gl.glDisable(GL_CLIP_PLANE1);
    		
    		gl.glColor4d(0.0,1.0,0.0,1.0);
    		myGlu.gluSphere(quad, g.height, slices, stacks);
        }
        
        else{
        	angle = ((1-g.ratio)*360);
        	gl.glEnable(GL_CLIP_PLANE0);
    		gl.glEnable(GL_CLIP_PLANE1);
    		
    		gl.glColor4d(0.0,1.0,0.0,1.0);
    		gl.glClipPlane(GL_CLIP_PLANE0, new double[]{-1,0,0,0},0);
    		gl.glClipPlane(GL_CLIP_PLANE1, new double[]{Math.cos((angle* Math.PI)/180),Math.sin((angle* Math.PI)/180),0,0},0);
    		myGlu.gluSphere(quad, g.height*1.05, slices, stacks);
    		
    		gl.glDisable(GL_CLIP_PLANE0);
    		gl.glDisable(GL_CLIP_PLANE1);
    		
    		gl.glColor4d(1.0,0.0,0.0,1.0);
    		myGlu.gluSphere(quad, g.height, slices, stacks);
        	
        }
		
	}
	
	
	public void drawNemiSphere(final GeometryObject g) {
		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				g.getAlpha() * c.getAlpha() / 255);
		}

		GLUquadric quad = myGlu.gluNewQuadric();
		if ( !myGLRender.triangulation ) {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		


		gl.glEnable(GL_CLIP_PLANE0);
		gl.glEnable(GL_CLIP_PLANE1);
		gl.glColor4d(1.0,0.0,0.0,1.0);
		gl.glClipPlane(GL_CLIP_PLANE0, new double[]{1,0,0,(2*g.ratio-1)*g.height/2},0);
		gl.glClipPlane(GL_CLIP_PLANE1, new double[]{0,1,0,(2*g.ratio-1)*g.height/2},0);
		myGlu.gluSphere(quad, g.height, slices, stacks);
		
		gl.glColor4d(0.0,1.0,0.0,1.0);
		gl.glClipPlane(GL_CLIP_PLANE0, new double[]{1,0,0,(2*g.ratio-1)*g.height/2},0);
		gl.glClipPlane(GL_CLIP_PLANE1, new double[]{0,-1,0,(2*g.ratio-1)*g.height/2},0);
		myGlu.gluSphere(quad, g.height, slices, stacks);
		
		gl.glColor4d(0.0,0.0,1.0,1.0);
		gl.glClipPlane(GL_CLIP_PLANE0, new double[]{-1,0,0,(2*g.ratio-1)*g.height/2},0);
		gl.glClipPlane(GL_CLIP_PLANE1, new double[]{0,-1,0,(2*g.ratio-1)*g.height/2},0);
		myGlu.gluSphere(quad, g.height, slices, stacks);
		
		gl.glColor4d(1.0,1.0,0.0,1.0);
		gl.glClipPlane(GL_CLIP_PLANE0, new double[]{-1,0,0,(2*g.ratio-1)*g.height/2},0);
		gl.glClipPlane(GL_CLIP_PLANE1, new double[]{0,1,0,(2*g.ratio-1)*g.height/2},0);
		myGlu.gluSphere(quad, g.height, slices, stacks);
		
		gl.glDisable(GL_CLIP_PLANE0);
		gl.glDisable(GL_CLIP_PLANE1);
	

	}

	public void drawCone3D(final GeometryObject g) {
		// (final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		if ( !colorpicking ) {
			Color c = g.getColor();
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				g.getAlpha() * c.getAlpha() / 255);
		}
		if ( !myGLRender.triangulation ) {
			myGlut.glutSolidCone(g.height, g.height, 10, 10);
		} else {
			myGlut.glutWireCone(g.height, g.height, 10, 10);
		}

		gl.glTranslated(-p.getCentroid().getX(), -yFlag * p.getCentroid().getY(), -z);
	}

	public void drawTeapot(final GeometryObject g) {
		// final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( !Double.isNaN(p.getCoordinate().z) ) {
			// TODO Normally, the NaN case is not true anymore
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), yFlag * p.getCentroid().getY(), z);
		if ( !colorpicking ) {
			Color c = g.getColor();
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				g.getAlpha() * c.getAlpha() / 255);
		}
		gl.glRotated(90, 1.0, 0.0, 0.0);
		myGlut.glutSolidTeapot(g.height);
		gl.glRotated(-90, 1.0, 0.0, 0.0);
		gl.glTranslated(-p.getCentroid().getX(), -yFlag * p.getCentroid().getY(), -z);
	}

	public void drawPyramid(final GeometryObject g) {
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(0, 0, z);
		if ( !colorpicking ) {
			Color c = g.getColor();
			gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
				g.getAlpha() * c.getAlpha() / 255);
		}
		PyramidSkeleton(p, g.height,g.getColor(),g.getAlpha());
		// border
		if ( !colorpicking ) {
			Color border = g.border;
			gl.glColor4d((double) border.getRed() / 255, (double) border.getGreen() / 255,
				(double) border.getBlue() / 255, g.getAlpha() * border.getAlpha() / 255);
		}
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
		gl.glPolygonOffset(0.0f, -(float) 1.1);
		PyramidSkeleton(p, g.height,g.border,g.getAlpha());
		gl.glDisable(GL.GL_POLYGON_OFFSET_LINE);
		if ( !myGLRender.triangulation ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		}
		gl.glTranslated(0, 0, -z);
	}
	
	public void drawRGBCube(final GeometryObject g) {
		// final Polygon p, final double radius, final Color c, final double alpha) {
				// Add z value (Note: getCentroid does not return a z value)
				double z = 0.0;
				Polygon p = (Polygon) g.geometry;
				if ( !Double.isNaN(p.getCoordinate().z) ) {
					// TODO Normally, the NaN case is not true anymore
					z = p.getExteriorRing().getPointN(0).getCoordinate().z;
				}

				if ( !colorpicking ) {
					Color c = g.getColor();
					gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
						g.getAlpha() * c.getAlpha() / 255);
				}
				
				if(g.picked){
					Color c = g.getColor();
					gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
						g.getAlpha() * c.getAlpha() / 255);
					gl.glBegin(GL_QUADS);
				    gl.glNormal3d(0.0, 0.0, -1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
				gl.glEnd();
				
				gl.glBegin(GL_QUADS);
				    gl.glNormal3d(1.0, 0.0, 0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), g.height);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), g.height);
			    gl.glEnd();
			    
			    gl.glBegin(GL_QUADS);
		            gl.glNormal3d(0.0, -1.0, 0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), g.height);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), g.height);
			    gl.glEnd();
			    
			    gl.glBegin(GL_QUADS);
		            gl.glNormal3d(-1.0, 0.0, 0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), g.height);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), g.height);
		        gl.glEnd();
		        
		        gl.glBegin(GL_QUADS);
		            gl.glNormal3d(0.0, 1.0, 0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), g.height);
				 	gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), g.height);
	            gl.glEnd();
	            
	            gl.glBegin(GL_QUADS);
				    gl.glNormal3d(0.0, 0.0, 1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), g.height);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), g.height);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), g.height);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), g.height);
			    gl.glEnd();
					
					
				}
				else{
				gl.glBegin(GL_QUADS);
				    gl.glNormal3d(0.0, 0.0, -1.0);
				    gl.glColor3d(1.0, 0.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
					gl.glColor3d(1.0, 1.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
					gl.glColor3d(0.0, 1.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
					gl.glColor3d(0.0, 0.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
				gl.glEnd();
				
				gl.glBegin(GL_QUADS);
				    gl.glNormal3d(1.0, 0.0, 0.0);
			        gl.glColor3d(1.0, 1.0,0.0); 
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
					gl.glColor3d(0.0, 1.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
					gl.glColor3d(0.0, 1.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), g.height);
					gl.glColor3d(1.0, 1.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), g.height);
			    gl.glEnd();
			    
			    gl.glBegin(GL_QUADS);
		            gl.glNormal3d(0.0, -1.0, 0.0);
			        gl.glColor3d(0.0, 1.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
					gl.glColor3d(0.0, 0.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
					gl.glColor3d(0.0, 0.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), g.height);
					gl.glColor3d(0.0, 1.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), g.height);
			    gl.glEnd();
			    
			    gl.glBegin(GL_QUADS);
		            gl.glNormal3d(-1.0, 0.0, 0.0);
			        gl.glColor3d(0.0, 0.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
					gl.glColor3d(1.0, 0.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
					gl.glColor3d(1.0, 0.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), g.height);
					gl.glColor3d(0.0, 0.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), g.height);
		        gl.glEnd();
		        
		        gl.glBegin(GL_QUADS);
		            gl.glNormal3d(0.0, 1.0, 0.0);
			        gl.glColor3d(1.0, 0.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
					gl.glColor3d(1.0, 1.0,0.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
					gl.glColor3d(1.0, 1.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), g.height);
					gl.glColor3d(1.0, 0.0,1.0);
				 	gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), g.height);
	            gl.glEnd();
	            
	            gl.glBegin(GL_QUADS);
				    gl.glNormal3d(0.0, 0.0, 1.0);
				    gl.glColor3d(1.0, 0.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), g.height);
					gl.glColor3d(1.0, 1.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), g.height);
					gl.glColor3d(0.0, 1.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), g.height);
					gl.glColor3d(0.0, 0.0,1.0);
					gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), g.height);
			    gl.glEnd();
				}


				
				if ( !colorpicking ) {
					Color c = g.getColor();
					gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
						g.getAlpha() * c.getAlpha() / 255);
				}
	}
	
	public void drawRGBTriangle(final GeometryObject g) {
				double z = 0.0;
				Polygon p = (Polygon) g.geometry;
				if ( !Double.isNaN(p.getCoordinate().z) ) {
					// TODO Normally, the NaN case is not true anymore
					z = p.getExteriorRing().getPointN(0).getCoordinate().z;
				}
				if(g.picked){
					Color c = g.getColor();
					gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
						g.getAlpha() * c.getAlpha() / 255);
					gl.glBegin(GL_TRIANGLES);
						gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
						gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
						gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
					gl.glEnd();
					
				}
				else{
					gl.glBegin(GL_TRIANGLES);
					    gl.glColor3d(1.0, 0.0,0.0);
						gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
						gl.glColor3d(0.0, 1.0,0.0);
						gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
						gl.glColor3d(0.0, 0.0,1.0);
						gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
				    gl.glEnd();	
				}				
	}
	
	
	
	public void DrawMultiLineCylinder(final Geometry g, final Color c, final double alpha, final double height) {
			// get the number of line in the multiline.
		    MultiLineString lines = ((MultiLineString) g);
			int numGeometries = lines.getNumGeometries();

			// for each line of a multiline, get each point coordinates.
			for ( int i = 0; i < numGeometries; i++ ) {
				Geometry gg = lines.getGeometryN(i);
				drawLineCylinder(gg,c,alpha,height);

			}
		}
	
	
	
	public void drawLineCylinder(final Geometry g, final Color c, final double alpha, final double height) {

		double z = 0.0;
		
		Geometry gg = (Geometry) g;
		
		
		if ( Double.isNaN(gg.getCoordinate().z) == false ) {
			z = gg.getCentroid().getCoordinate().z;
		}
		if (gg instanceof Point) {
			//drawSphere(g, z);
			return;
		}
		LineString l = (LineString) gg;
		
		for(int i=0;i<=l.getNumPoints()-2;i++){
			
			if ( Double.isNaN(l.getCoordinate().z) == false ) {
				z= l.getPointN(i).getCoordinate().z;
			}
			
			double x_length = l.getPointN(i+1).getX() - l.getPointN(i).getX();
			double y_length = l.getPointN(i+1).getY() - l.getPointN(i).getY();
			double z_length = l.getPointN(i+1).getCoordinate().z - l.getPointN(i).getCoordinate().z;
	
			double distance = Math.sqrt(x_length*x_length + y_length*y_length + z_length*z_length) ;
			
			gl.glTranslated(l.getPointN(i).getX(), yFlag * l.getPointN(i).getY(), z);
			Vector3d  d;
			if ( Double.isNaN(l.getCoordinate().z) == false ) {
			  d = new Vector3d((l.getPointN(i+1).getX() - l.getPointN(i).getX())/distance, -(l.getPointN(i+1).getY() - l.getPointN(i).getY())/distance,(l.getPointN(i+1).getCoordinate().z - l.getPointN(i).getCoordinate().z)/distance);
			}else{
			  d = new Vector3d((l.getPointN(i+1).getX() - l.getPointN(i).getX())/distance, -(l.getPointN(i+1).getY() - l.getPointN(i).getY())/distance,0);
			}
					
			Vector3d z_up = new Vector3d(0,0,1);
			
			Vector3d a = new Vector3d();
			a.cross(z_up, d);
		
			double omega = Math.acos(z_up.dot(d));
			omega= omega *180 / Math.PI;
			gl.glRotated(omega, a.x, a.y, a.z);
	
			if ( !colorpicking ) {
				gl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
					alpha * c.getAlpha() / 255);
			}
	
			GLUquadric quad = myGlu.gluNewQuadric();
			if ( !myGLRender.triangulation ) {
				myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
			} else {
				myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
			}
			myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
			myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
			final int slices = 16;
			final int stacks = 16;
			myGlu.gluCylinder(quad, height, height, distance, slices, stacks);
			myGlu.gluDeleteQuadric(quad);
			
			
			gl.glRotated(-omega, a.x, a.y, a.z);
			gl.glTranslated(-(l.getPointN(i).getX()), -yFlag * (l.getPointN(i).getY()), -z);
		}

		
		


	}
	
	
	public Vertex[] GetPyramidfaceVertices(final Polygon p, int i, int j, double size,int x,int y){
		Vertex[] vertices = new Vertex[3];
		for ( int i1 = 0; i1 < 3; i1++ ) {
			vertices[i1] = new Vertex();
		}
		
		vertices[0].x = p.getExteriorRing().getPointN(i).getX();
		vertices[0].y = yFlag * p.getExteriorRing().getPointN(i).getY();
		vertices[0].z = 0.0d;

		vertices[1].x = p.getExteriorRing().getPointN(j).getX();
		vertices[1].y = yFlag * p.getExteriorRing().getPointN(j).getY();
		vertices[1].z = 0.0d;

		vertices[2].x = p.getExteriorRing().getPointN(i).getX() + size / 2 *x ;
		vertices[2].y = yFlag *(p.getExteriorRing().getPointN(i).getY() + size / 2 *y);
		vertices[2].z = size;
		return vertices;
	}

	public void PyramidSkeleton(final Polygon p, final double size, final Color c, final double alpha) {
				
		Vertex[] vertices;
		double[] normal;
		
		if(myGLRender.computeNormal){
			vertices = getExteriorRingVertices(p);
			GLUtilNormal.HandleNormal(vertices,c,alpha,1,myGLRender);
		}

		gl.glBegin(GL_QUADS);
			gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
		gl.glEnd();
		

		if(myGLRender.computeNormal){
			vertices = GetPyramidfaceVertices(p,0,1,size,1,-1);
			GLUtilNormal.HandleNormal(vertices,c,alpha,-1,myGLRender);
		}
		gl.glBegin(GL_TRIANGLES);
			gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(0).getX() + size / 2, yFlag *
				(p.getExteriorRing().getPointN(0).getY() - size / 2), size);
		gl.glEnd();
		
		if(myGLRender.computeNormal){
			vertices = GetPyramidfaceVertices(p,1,2,size,-1,-1);
			GLUtilNormal.HandleNormal(vertices,c,alpha,-1,myGLRender);
		}
		gl.glBegin(GL_TRIANGLES);
			gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(1).getX() - size / 2, yFlag *
				(p.getExteriorRing().getPointN(1).getY() - size / 2), size);
		gl.glEnd();
		
		if(myGLRender.computeNormal){
			vertices = GetPyramidfaceVertices(p,2,3,size,-1,1);
			GLUtilNormal.HandleNormal(vertices,c,alpha,-1,myGLRender);
		}
		gl.glBegin(GL_TRIANGLES);
			gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(2).getX() - size / 2, yFlag *
				(p.getExteriorRing().getPointN(2).getY() + size / 2), size);
		gl.glEnd();
		
		if(myGLRender.computeNormal){
			vertices = GetPyramidfaceVertices(p,3,0,size,1,1);
			GLUtilNormal.HandleNormal(vertices,c,alpha,-1,myGLRender);
		}
		gl.glBegin(GL_TRIANGLES);
			gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);
			gl.glVertex3d(p.getExteriorRing().getPointN(3).getX() + size / 2, yFlag *
				(p.getExteriorRing().getPointN(3).getY() + size / 2), size);
		gl.glEnd();
	}

	public void DrawShape(final IShape shape, final boolean showTriangulation) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();

		if ( showTriangulation ) {

			if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {
				gl.glBegin(GL.GL_LINES); // draw using triangles
				gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
				gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0d);

				gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
				gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0d);

				gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
				gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
				gl.glEnd();
			} else {
				gl.glBegin(GL.GL_LINES); // draw using triangles
				gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing().getPointN(0)
					.getCoordinate().z);
				gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing().getPointN(0)
					.getCoordinate().z);

				gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing().getPointN(1)
					.getCoordinate().z);
				gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing().getPointN(2)
					.getCoordinate().z);

				gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing().getPointN(2)
					.getCoordinate().z);
				gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing().getPointN(0)
					.getCoordinate().z);
				gl.glEnd();

			}
		} else {
			if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {

				gl.glBegin(GL_TRIANGLES); // draw using triangles
					gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
						polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
	
					gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
						polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
	
					gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
						polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
				gl.glEnd();
			} else {
				gl.glBegin(GL_TRIANGLES); // draw using triangles

					gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
						polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing().getPointN(0)
						.getCoordinate().z);	
	
					gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
						polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing().getPointN(1)
						.getCoordinate().z);
					
					gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
						polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing().getPointN(2)
						.getCoordinate().z);
				gl.glEnd();
			}

		}
	}

	/*
	 * Return 9 array with the 3 vertex coordinates of the traingle
	 */
	public double[] GetTriangleVertices(final IShape shape) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();
		double[] vertices = new double[9];
		for ( int i = 0; i < 3; i++ ) {
			vertices[i * 3] = polygon.getExteriorRing().getPointN(0).getX();
			vertices[i * 3 + 1] = yFlag * polygon.getExteriorRing().getPointN(0).getY();
			vertices[i * 3 + 2] = 0.0d;
		}
		return vertices;
	}
	
	
	public boolean IsClockwise(Vertex[] vertices)
	{
	    double sum = 0.0;
	    for (int i = 0; i < vertices.length; i++) {
	    	Vertex v1 = vertices[i];
	    	Vertex v2 = vertices[(i + 1) % vertices.length];
	        sum += (v2.x - v1.x) * (v2.y + v1.y);
	    }
	    return sum > 0.0;
	}
	


	

	public void drawRoundRectangle(final Polygon p) {

		double width = p.getEnvelopeInternal().getWidth();
		double height = p.getEnvelopeInternal().getHeight();

		gl.glTranslated(p.getCentroid().getX(), -p.getCentroid().getY(), 0.0d);
		DrawRectangle(width, height * 0.8, p.getCentroid());
		DrawRectangle(width * 0.8, height, p.getCentroid());
		DrawRoundCorner(width, height, width * 0.1, height * 0.1, 5);
		gl.glTranslated(-p.getCentroid().getX(), p.getCentroid().getY(), 0.0d);

	}

	void DrawRectangle(final double width, final double height, final Point point) {
		gl.glBegin(GL_POLYGON); // draw using quads
		gl.glVertex3d(-width / 2, height / 2, 0.0d);
		gl.glVertex3d(width / 2, height / 2, 0.0d);
		gl.glVertex3d(width / 2, -height / 2, 0.0d);
		gl.glVertex3d(-width / 2, -height / 2, 0.0d);
		gl.glEnd();
	}

	void DrawFan(final double radius, final double x, final double y, final int or_x, final int or_y, final int timestep) {
		gl.glBegin(GL_TRIANGLE_FAN); // upper right
		gl.glVertex3d(or_x * x, or_y * y, 0.0d);
		for ( int i = 0; i <= timestep; i++ ) {
			double anglerad = Math.PI / 2 * i / timestep;
			double xi = Math.cos(anglerad) * radius;
			double yi = Math.sin(anglerad) * radius;
			gl.glVertex3d(or_x * (x + xi), y + yi, 0.0d);
		}
		gl.glEnd();
	}

	void DrawRoundCorner(final double width, final double height, final double x_radius, final double y_radius,
		final int nbPoints) {

		double xc = width / 2 * 0.8;
		double yc = height / 2 * 0.8;
		// Enhancement implement DrawFan(radius, xc, yc, 10);

		gl.glBegin(GL_TRIANGLE_FAN); // upper right
		gl.glVertex3d(xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(xc + xi, yc + yi, 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL_TRIANGLE_FAN); // upper right

		gl.glVertex3d(xc, -yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(xc + xi, -(yc + yi), 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL_TRIANGLE_FAN); // upper left

		gl.glVertex3d(-xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(-(xc + xi), yc + yi, 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL_TRIANGLE_FAN);
		gl.glVertex3d(-xc, -yc, 0.0d); // down left
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(-(xc + xi), -(yc + yi), 0.0d);
		}
		gl.glEnd();
	}

}
