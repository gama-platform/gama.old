/*********************************************************************************************
 *
 *
 * 'JTSDrawer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.jts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.file.GamaFile;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.utils.GLUtilNormal;
import ummisco.gama.opengl.utils.Vertex;

public class JTSDrawer {

	// OpenGL member
	// protected final GLU myGlu;
	private final GLUT myGlut;
	public TessellCallBack tessCallback;
	private final GLUtessellator tobj;
	private static boolean useJTSForTriangulation = false;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLRenderer renderer;

	JTSVisitor visitor;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numGeometries;

	double tempPolygon[][];
	// double temp[];

	// Use for JTS triangulation
	List<IShape> triangles;
	// Iterator<IShape> it;


	/** The earth texture. */
	// private Texture earthTexture;
	// public float textureTop, textureBottom, textureLeft, textureRight;
	public Texture[] textures = new Texture[3];
	// Use for texture mapping;
	// BufferedImage image = null;
	// Texture texture = null;

	public boolean colorpicking = false;

	public boolean bigPolygonDecomposition = true;
	public int nbPtsForDecomp = 2000;
	
	
	Integer openNestedGLListIndex;
	
	public JTSDrawer(final JOGLRenderer gLRender) {
		myGlut = new GLUT();
		renderer = gLRender;
		tessCallback = new TessellCallBack(renderer.getGlu());
		tobj = GLU.gluNewTess();

		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		visitor = new JTSVisitor();
	}
	
	public void setColor(final Color c, final double alpha) {
		if ( c == null ) { return; }
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, alpha * c.getAlpha() / 255.0);
	}
	
	
    public void drawGeometryCached(final GamaFile file) {	
		GL2 gl = GLContext.getCurrentGL().getGL2();
		renderer.getCache().initializeStaticGeometry(file);
		if(renderer.getCache().contains(file.getFile().getAbsoluteFile().toString())){
			Integer index = renderer.getCache().getListIndex(gl,file.getFile().getAbsoluteFile().toString());
			//gl.getContext().makeCurrent();
		    gl.glCallList(index);    
		}	
	}
	
	public void drawGeometryCollection(final GeometryCollection geoms, final Color c, final double alpha,
	final boolean fill, final Color border, final boolean isTextured,
	final GeometryObject object, /* final Integer angle, */
	final double height, final boolean rounded, final double z_fighting_value, final double z) {

		numGeometries = geoms.getNumGeometries();

		for ( int i = 0; i < numGeometries; i++ ) {
			Geometry geom = geoms.getGeometryN(i);
			if ( geom instanceof Polygon ) {
				curPolygon = (Polygon) geom;
				if ( height > 0 ) {
					DrawPolyhedre(curPolygon, c, alpha, fill, height, false, border, isTextured, object,
						rounded, z_fighting_value);
				} else {
					DrawPolygon(curPolygon, c, alpha, fill, border, isTextured, object, true, rounded,
						z_fighting_value, 1);
				}
			} else if ( geom instanceof LineString ) {
				LineString l = (LineString) geom;
				if ( height > 0 ) {
					drawPlan(l, z, c, alpha, height, 0, true);
				} else {
					drawLineString(l, z, 1.2f, c, alpha);
				}
			}
		}
	}

	public void drawMultiPolygon(final MultiPolygon polygons, final Color c, final double alpha, final boolean fill,
		final Color border, final boolean isTextured, final GeometryObject object, /* final Integer angle, */
		final double height, final boolean rounded, final double z_fighting_value) {

		numGeometries = polygons.getNumGeometries();

		for ( int i = 0; i < numGeometries; i++ ) {
			curPolygon = (Polygon) polygons.getGeometryN(i);

			if ( height > 0 ) {
				DrawPolyhedre(curPolygon, c, alpha, fill, height, /* angle, */false, border, isTextured, object,
					rounded, z_fighting_value);
			} else {
				DrawPolygon(curPolygon, c, alpha, fill, border, isTextured, object, /* angle, */true, rounded,
					z_fighting_value, 1);
			}
		}
	}

	public void DrawPolygon(final Polygon p, final Color c, final double alpha, final boolean fill, final Color border,
		final boolean isTextured, final GeometryObject object, /* final Integer angle, */
		final boolean drawPolygonContour, final boolean rounded, final double z_fighting_value, final int norm_dir) {
		GL2 gl = GLContext.getCurrentGL().getGL2();

		if ( renderer.getComputeNormal() ) {
			Vertex[] vertices = getExteriorRingVertices(p);
			GLUtilNormal.HandleNormal(vertices, c, alpha, norm_dir, renderer);
		}
		if ( isTextured == false ) {
			if ( fill == true ) {
				if ( !colorpicking ) {
					setColor(c, alpha);
				}
				if ( rounded == true ) {
					drawRoundRectangle(p);
				} else {
					DrawTesselatedPolygon(p, norm_dir, c, alpha);
					gl.glColor4d(0.0d, 0.0d, 0.0d, alpha);
					if ( drawPolygonContour == true ) {
						DrawPolygonContour(p, border, alpha, z_fighting_value);
					}
				}
			}

			else { // Draw only the contour of the polygon. If no border has been define draw empty shape with their original color
				if ( border.equals(Color.black) ) {
					DrawPolygonContour(p, c, alpha, z_fighting_value);
				} else {
					DrawPolygonContour(p, border, alpha, z_fighting_value);
				}
			}
		}
		else {
			Texture texture = object.getTexture(gl, renderer, 0);
			if ( texture != null ) {
				DrawTexturedPolygon(p, texture);
			}
		}
	}

	void DrawTesselatedPolygon(final Polygon p, final int norm_dir, final Color c, final double alpha) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		GLU.gluTessBeginPolygon(tobj, null);

		// Exterior contour
		GLU.gluTessBeginContour(tobj);

		if ( renderer.getComputeNormal() ) {
			Vertex[] vertices = getExteriorRingVertices(p);

			double[] normalmean = new double[3];
			for ( int i = 0; i < vertices.length - 2; i++ ) {
				double[] normal = GLUtilNormal.CalculateNormal(vertices[i + 2], vertices[i + 1], vertices[i]);
				normalmean[0] = (normalmean[0] + normal[0]) / (i + 1);
				normalmean[1] = (normalmean[1] + normal[1]) / (i + 1);
				normalmean[2] = (normalmean[2] + normal[2]) / (i + 1);
			}

			if ( renderer.data.isDraw_norm() ) {
				Vertex center = GLUtilNormal.GetCenter(vertices);
				gl.glBegin(GL.GL_LINES);
				gl.glColor3d(1.0, 0.0, 0.0);
				gl.glVertex3d(center.x, center.y, center.z);
				gl.glVertex3d(center.x + normalmean[0] * norm_dir, center.y + normalmean[1] * norm_dir,center.z + normalmean[2] * norm_dir);
				gl.glEnd();

				gl.glPointSize(2.0f);
				gl.glBegin(GL.GL_POINTS);
				gl.glVertex3d(center.x + normalmean[0] * norm_dir, center.y + normalmean[1] * norm_dir,center.z + normalmean[2] * norm_dir);
				gl.glEnd();

				if ( !colorpicking ) {
					if ( c != null ) {
						setColor(c, alpha);
					}
				}
			}

			GLU.gluTessNormal(tobj, normalmean[0] * norm_dir, normalmean[1] * norm_dir, normalmean[2] * norm_dir);
		}

		tempPolygon = new double[p.getExteriorRing().getNumPoints()][3];
		// Convert vertices as a list of double for gluTessVertex
		for ( int j = 0; j < p.getExteriorRing().getNumPoints(); j++ ) {
			Point pp = p.getExteriorRing().getPointN(j);
			tempPolygon[j][0] = pp.getX();
			tempPolygon[j][1] = renderer.yFlag * pp.getY();
			if ( Double.isNaN(pp.getCoordinate().z) == true ) {
				tempPolygon[j][2] = 0.0d;
			} else {
				tempPolygon[j][2] = 0.0d + pp.getCoordinate().z;
			}
		}

		for ( int j = 0; j < p.getExteriorRing().getNumPoints(); j++ ) {
			GLU.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}

		GLU.gluTessEndContour(tobj);

		// interior contour
		for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
			GLU.gluTessBeginContour(tobj);
			int numIntPoints = p.getInteriorRingN(i).getNumPoints();
			tempPolygon = new double[numIntPoints][3];
			// Convert vertices as a list of double for gluTessVertex
			for ( int j = 0; j < numIntPoints; j++ ) {
				Point pp = p.getInteriorRingN(i).getPointN(j);
				tempPolygon[j][0] = pp.getX();
				tempPolygon[j][1] = renderer.yFlag * pp.getY();

				if ( Double.isNaN(pp.getCoordinate().z) == true ) {
					tempPolygon[j][2] = 0.0d;
				} else {
					tempPolygon[j][2] = 0.0d + pp.getCoordinate().z;
				}
			}

			for ( int j = 0; j < numIntPoints; j++ ) {
				GLU.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
			}
			GLU.gluTessEndContour(tobj);
		}

		GLU.gluTessEndPolygon(tobj);
	}
	
	void DrawTexturedPolygon(final Polygon p, final Texture texture) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glColor3d(1.0, 1.0, 1.0);
		texture.enable(gl);
		texture.bind(gl);

		if ( p.getNumPoints() > 5 ) {
			drawTriangulatedPolygon(p, useJTSForTriangulation, texture);

		} else {
			if ( renderer.getComputeNormal() ) {
				Vertex[] vertices = this.getExteriorRingVertices(p);
				GLUtilNormal.HandleNormal(vertices, null, 0, 1, renderer);
			}
			gl.glColor3d(1.0, 1.0, 1.0);// Set the color to white to avoid color and texture mixture
			gl.glBegin(GL2ES3.GL_QUADS);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), renderer.yFlag * p.getExteriorRing().getPointN(0).getY(),p.getExteriorRing().getCoordinateN(0).z);
			gl.glTexCoord2f(1.0f, 1.0f);;
			gl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), renderer.yFlag * p.getExteriorRing().getPointN(1).getY(),p.getExteriorRing().getCoordinateN(1).z);
			gl.glTexCoord2f(1.0f, 0.0f);;
			gl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), renderer.yFlag * p.getExteriorRing().getPointN(2).getY(),p.getExteriorRing().getCoordinateN(2).z);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), renderer.yFlag * p.getExteriorRing().getPointN(3).getY(),p.getExteriorRing().getCoordinateN(3).z);
			gl.glEnd();
		}

		texture.disable(gl);
	}

	void drawTriangulatedPolygon(Polygon p, final boolean showTriangulation, final Texture texture) {
		boolean simplifyGeometry = false;
		// Workaround to compute the z value of each triangle as triangulation
		// create new point during the triangulation that are set with z=NaN
		if ( p.getNumPoints() > 4 ) {
			triangles = GeometryUtils.triangulationSimple(null, p); // VERIFY NULL SCOPE

			List<Geometry> segments = new ArrayList<Geometry>();
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
						Geometry closestSeg = segments.get(0);
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
						coord.z = (1 - dist1 / closestSeg.getLength()) * closestSeg.getCoordinates()[0].z +
							(1 - dist2 / closestSeg.getLength()) * closestSeg.getCoordinates()[1].z;
						DrawTriangulatedPolygonShape(p, tri, showTriangulation, texture);
					}
				}
			}
		} else if ( p.getNumPoints() == 4 ) {
			triangles = new ArrayList<IShape>();
			triangles.add(new GamaShape(p));
		}
		for ( IShape tri : triangles ) {
			DrawTriangulatedPolygonShape(p, tri, showTriangulation, texture);
		}
	}
  
	public void DrawTriangulatedPolygonShape(final Polygon triangulatedPolygon, final IShape shape,
			final boolean showTriangulation, final Texture texture) {
			GL2 gl = GLContext.getCurrentGL().getGL2();
			Polygon polygon = (Polygon) shape.getInnerGeometry();

			final Envelope env = triangulatedPolygon.getEnvelopeInternal();
			final double xMin = env.getMinX();
			final double xMax = env.getMaxX();
			final double yMin = env.getMinY();
			final double yMax = env.getMaxY();

			if ( showTriangulation ) {
				if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {
					gl.glBegin(GL.GL_LINES); // draw using triangles
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
		                gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
					gl.glEnd();
				} else {
					gl.glBegin(GL.GL_LINES); // draw using triangles
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(),polygon.getExteriorRing().getPointN(0).getCoordinate().z);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(),polygon.getExteriorRing().getPointN(0).getCoordinate().z);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(),polygon.getExteriorRing().getPointN(1).getCoordinate().z);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(),polygon.getExteriorRing().getPointN(2).getCoordinate().z);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(),polygon.getExteriorRing().getPointN(2).getCoordinate().z);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(),polygon.getExteriorRing().getPointN(0).getCoordinate().z);
					gl.glEnd();

				}
			} else {
				if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {
					if ( texture != null ) {
						gl.glColor3d(1.0, 1.0, 1.0);// Set the color to white to avoid color and texture mixture
						gl.glBegin(GL2ES3.GL_TRIANGLES); // draw using triangles
						gl.glTexCoord2f(0.0f, 1.0f);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
						gl.glTexCoord2f(1.0f, 1.0f);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
						gl.glTexCoord2f(1.0f, 0.0f);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
						gl.glEnd();

					} else {
						gl.glBegin(GL.GL_TRIANGLES); // draw using triangles
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
						gl.glEnd();
					}

				} else {
					if ( texture != null ) {						
						gl.glColor3d(1.0, 1.0, 1.0);// Set the color to white to avoid color and texture mixture
						gl.glBegin(GL2ES3.GL_TRIANGLES); // draw using triangles
						//gl.glTexCoord2d(polygon.getExteriorRing().getPointN(0).getX() / (xMax - xMin),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY() / (yMax - yMin));
						gl.glTexCoord2f(0.0f, 1.0f);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(),polygon.getExteriorRing().getPointN(0).getCoordinate().z);
						//gl.glTexCoord2d(polygon.getExteriorRing().getPointN(1).getX() / (xMax - xMin),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY() / (yMax - yMin));
						gl.glTexCoord2f(1.0f, 1.0f);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(),polygon.getExteriorRing().getPointN(1).getCoordinate().z);
						//gl.glTexCoord2d(polygon.getExteriorRing().getPointN(2).getX() / (xMax - xMin),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY() / (yMax - yMin));
						gl.glTexCoord2f(1.0f, 0.0f);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(),polygon.getExteriorRing().getPointN(2).getCoordinate().z);				
						gl.glEnd();
					} else {
						gl.glBegin(GL.GL_TRIANGLES); // draw using triangles
						gl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY(),polygon.getExteriorRing().getPointN(0).getCoordinate().z);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(1).getY(),polygon.getExteriorRing().getPointN(1).getCoordinate().z);
						gl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),renderer.yFlag * polygon.getExteriorRing().getPointN(2).getY(),polygon.getExteriorRing().getPointN(2).getCoordinate().z);
						gl.glEnd();
					}
				}

			}
		}

	public void DrawPolygonContour(final Polygon p, final Color border, final double alpha,
		final double z_fighting_value) {
		if ( border == null ) { return; }
		GL2 gl = GLContext.getCurrentGL().getGL2();
		// FIXME: when rendering with this method the triangulation does not work anymore
		if ( renderer.data.isZ_fighting() ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			// }

			// myGl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			gl.glPolygonOffset(0.0f, -(float) (z_fighting_value * 1.1));
			// myGl.glPolygonOffset(0.0f,10.0f);
			gl.glBegin(GL2.GL_POLYGON);
			if ( !colorpicking ) {
				setColor(border, alpha);
			}
			p.getExteriorRing().apply(visitor);
			gl.glEnd();

			if ( p.getNumInteriorRing() > 0 ) {
				// Draw Interior ring
				for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
					gl.glBegin(GL2.GL_POLYGON);
					p.getInteriorRingN(i).apply(visitor);
					gl.glEnd();
				}
			}
			gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			if ( !renderer.data.isTriangulation() ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			}
		} else {
			gl.glBegin(GL.GL_LINES);
			if ( !colorpicking ) {
				setColor(border, alpha);
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

	public void DrawPolyhedre(final Polygon p, final Color c, final double alpha, final boolean fill,
			final double height, /* final Integer angle, */final boolean drawPolygonContour, final Color border,
			final boolean isTextured, final GeometryObject object, final boolean rounded, final Double z_fighting_value) {
			GL2 gl = GLContext.getCurrentGL().getGL2();
			int p_norm_dir = 1;
			int face_norm_dir = -1;
			boolean polyCW = true;
			if ( renderer.getComputeNormal() ) {
				Vertex[] vertices = getExteriorRingVertices(p);
				polyCW =  IsClockwise(vertices);
				if (polyCW ) {
					face_norm_dir = 1;
					p_norm_dir = 1;
				} else {
					face_norm_dir = 1;
					p_norm_dir = 1;
				}
				
			}
			
			DrawPolygon(p, c, alpha, fill, border, isTextured, object, drawPolygonContour, rounded, z_fighting_value,-p_norm_dir);
			//gl.glTranslated(0, 0, height);
			double[] vectorNormal =  CalculatePolygonNormal(p,polyCW);
			
			gl.glTranslated(-vectorNormal[0]*height,- vectorNormal[1]*height,- vectorNormal[2]*height);
			DrawPolygon(p, c, alpha, fill, border, isTextured, object/* ,angle */, drawPolygonContour, rounded,z_fighting_value,  p_norm_dir);
			//gl.glTranslated(0, 0, -height);
		   // gl.glTranslated(-vectorNormal[0]*height, -vectorNormal[1]*height, vectorNormal[2]*height);
			// FIXME : Will be wrong if angle =!0

			if ( isTextured ) {
				if ( object.hasTextures() ) {
					DrawTexturedFaces(p, c, alpha, fill, border, isTextured, object.getTexture(gl, renderer, 1), height,
						drawPolygonContour, polyCW);
				} else {
					DrawTexturedFaces(p, c, alpha, fill, border, isTextured, object.getTexture(gl, renderer, 0), height,
						drawPolygonContour, polyCW);
				}

			} else {
				DrawFaces(p, c, alpha, fill, border, isTextured, height, drawPolygonContour, face_norm_dir, polyCW);
			}

		}


	// //////////////////////////////FACE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void DrawFaces(final Polygon p, final Color c, final double alpha, final boolean fill, final Color b,
			final boolean isTextured, final double height, final boolean drawPolygonContour, final int norm_dir, final boolean clockwise) {
			GL2 gl = GLContext.getCurrentGL().getGL2();
			if ( !colorpicking ) {
				setColor(c, alpha);
			}

			double elevation = 0.0d;

			if ( Double.isNaN(p.getExteriorRing().getPointN(0).getCoordinate().z) == false ) {
				elevation = p.getExteriorRing().getPointN(0).getCoordinate().z;
			}

			int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

			for ( int j = 0; j < curPolyGonNumPoints; j++ ) {
				int k = (j + 1) % curPolyGonNumPoints;
				Vertex[] vertices = getFaceVertices(p, j, k, elevation, height, clockwise);

				if ( fill ) {
					if ( renderer.getComputeNormal() ) {
						GLUtilNormal.HandleNormal(vertices, c, alpha, norm_dir, renderer);
					}
					gl.glBegin(GL2ES3.GL_QUADS);
					gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
					gl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
					gl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
					gl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
					gl.glEnd();
				}

				if ( drawPolygonContour == true || fill == false ) {

					if ( !colorpicking ) {
						setColor(b, alpha);
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
						setColor(c, alpha);
					}
				}
			}
		}

		public void DrawTexturedFaces(final Polygon p, final Color c, final double alpha, final boolean fill, final Color b,
			final boolean isTextured, final Texture texture, final double height, final boolean drawPolygonContour, final boolean clockwise) {
			GL2 gl = GLContext.getCurrentGL().getGL2();
			texture.enable(gl);
			texture.bind(gl);

			double elevation = 0.0d;

			if ( Double.isNaN(p.getExteriorRing().getPointN(0).getCoordinate().z) == false ) {
				elevation = p.getExteriorRing().getPointN(0).getCoordinate().z;
			}

			int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

			for ( int j = 0; j < curPolyGonNumPoints; j++ ) {

				int k = (j + 1) % curPolyGonNumPoints;

				Vertex[] vertices = getFaceVertices(p, j, k, elevation, height, clockwise);
				GLUtilNormal.HandleNormal(vertices, null, alpha, 1, renderer);

				gl.glColor3d(0.25, 0.25, 0.25);// Set the color to white to avoid color and texture mixture
				gl.glBegin(GL2ES3.GL_QUADS);
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
			texture.disable(gl);
		}



	public Vertex[] getFaceVerticesOld(final Polygon p, final int j, final int k, final double elevation,
		final double height) {
		// Build the 4 vertices of the face.
		Vertex[] vertices = new Vertex[4];
		for ( int i = 0; i < 4; i++ ) {
			vertices[i] = new Vertex();
		}
		// FIXME; change double to double in Vertex
		vertices[0].x = p.getExteriorRing().getPointN(j).getX();
		vertices[0].y = renderer.yFlag * p.getExteriorRing().getPointN(j).getY();
		vertices[0].z = elevation + height;

		vertices[1].x = p.getExteriorRing().getPointN(k).getX();
		vertices[1].y = renderer.yFlag * p.getExteriorRing().getPointN(k).getY();
		vertices[1].z = elevation + height;

		vertices[2].x = p.getExteriorRing().getPointN(k).getX();
		vertices[2].y = renderer.yFlag * p.getExteriorRing().getPointN(k).getY();
		vertices[2].z = elevation;

		vertices[3].x = p.getExteriorRing().getPointN(j).getX();
		vertices[3].y = renderer.yFlag * p.getExteriorRing().getPointN(j).getY();
		vertices[3].z = elevation;

		return vertices;
	}
	
	public double[] CalculatePolygonNormal(final Polygon p, Boolean clockwise){
		// Get 3 vertices of the initial polygon.
		Vertex[] verticesP = new Vertex[3];
		for ( int i = 0; i < 3; i++ ) {
			verticesP[i] = new Vertex();
		}
		
		verticesP[0].x = p.getExteriorRing().getPointN(0).getX();
		verticesP[0].y = renderer.yFlag * p.getExteriorRing().getPointN(0).getY();
		verticesP[0].z = p.getExteriorRing().getPointN(0).getCoordinate().z;

		verticesP[1].x = p.getExteriorRing().getPointN(1).getX();
		verticesP[1].y = renderer.yFlag * p.getExteriorRing().getPointN(1).getY();
		verticesP[1].z = p.getExteriorRing().getPointN(1).getCoordinate().z;

		verticesP[2].x = p.getExteriorRing().getPointN((2)).getX();
		verticesP[2].y = renderer.yFlag * p.getExteriorRing().getPointN((2) ).getY();
		verticesP[2].z = p.getExteriorRing().getPointN((2) ).getCoordinate().z;
		double[] normal = GLUtilNormal.CalculateNormal(verticesP[0], verticesP[1], verticesP[2]);
		if (clockwise == null || (clockwise != IsClockwise(verticesP))) {
			normal[0] *= -1;
			normal[1] *= -1;
			normal[2] *= -1;
		}
		return normal;
	}
	
	public Vertex[] getFaceVertices(final Polygon p, final int j, final int k, final double elevation,
			final double height, final boolean clockwise) {
			
			double[] vectorNormal =  CalculatePolygonNormal(p, clockwise);
			// Build the 4 vertices of the face.
			Vertex[] vertices = new Vertex[4];
			for ( int i = 0; i < 4; i++ ) {
				vertices[i] = new Vertex();
			}
			
			vertices[0].x = p.getExteriorRing().getPointN(j).getX() + vectorNormal[0] *height;
			vertices[0].y = renderer.yFlag * p.getExteriorRing().getPointN(j).getY() + vectorNormal[1] *height;
			vertices[0].z = p.getExteriorRing().getPointN(j).getCoordinate().z + vectorNormal[2] *height;

			vertices[1].x = p.getExteriorRing().getPointN(k).getX() + vectorNormal[0] *height;
			vertices[1].y = renderer.yFlag * p.getExteriorRing().getPointN(k).getY() + vectorNormal[1] *height;
			vertices[1].z = p.getExteriorRing().getPointN(k).getCoordinate().z + vectorNormal[2] *height;

			vertices[2].x = p.getExteriorRing().getPointN(k).getX();
			vertices[2].y = renderer.yFlag * p.getExteriorRing().getPointN(k).getY();
			vertices[2].z = p.getExteriorRing().getPointN(k).getCoordinate().z;

			vertices[3].x = p.getExteriorRing().getPointN(j).getX();
			vertices[3].y = renderer.yFlag * p.getExteriorRing().getPointN(j).getY();
			vertices[3].z = p.getExteriorRing().getPointN(j).getCoordinate().z;
			
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
		vertices[0].y = renderer.yFlag * p.getExteriorRing().getPointN(0).getY();
		vertices[0].z = p.getExteriorRing().getPointN(0).getCoordinate().z;

		vertices[1].x = p.getExteriorRing().getPointN(1).getX();
		vertices[1].y = renderer.yFlag * p.getExteriorRing().getPointN(1).getY();
		vertices[1].z = p.getExteriorRing().getPointN(1).getCoordinate().z;

		vertices[2].x = p.getExteriorRing().getPointN(2).getX();
		vertices[2].y = renderer.yFlag * p.getExteriorRing().getPointN(2).getY();
		vertices[2].z = p.getExteriorRing().getPointN(2).getCoordinate().z;

		return vertices;
	}

	public Vertex[] getExteriorRingVertices(final Polygon p) {
		// Build the n vertices of the facet of the polygon.
		Vertex[] vertices = new Vertex[p.getExteriorRing().getNumPoints() - 1];
		for ( int i = 0; i < p.getExteriorRing().getNumPoints() - 1; i++ ) {
			vertices[i] = new Vertex();
			vertices[i].x = p.getExteriorRing().getPointN(i).getX();
			vertices[i].y = renderer.yFlag * p.getExteriorRing().getPointN(i).getY();
			vertices[i].z = p.getExteriorRing().getPointN(i).getCoordinate().z;
			if ( Double.isNaN(vertices[i].z) ) {
				vertices[i].z = 0.0d;
			}
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
			setColor(c, alpha);
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
		GL2 gl = GLContext.getCurrentGL().getGL2();
		if ( !colorpicking ) {
			setColor(c, alpha);
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
				gl.glVertex3d(line.getPointN(j).getX(), renderer.yFlag * line.getPointN(j).getY(), z);

			} else {
				gl.glVertex3d(line.getPointN(j).getX(), renderer.yFlag * line.getPointN(j).getY(),
					z + line.getPointN(j).getCoordinate().z);
			}
			if ( Double.isNaN(line.getPointN(j + 1).getCoordinate().z) == true ) {
				gl.glVertex3d(line.getPointN(j + 1).getX(), renderer.yFlag * line.getPointN(j + 1).getY(), z);
			} else {
				gl.glVertex3d(line.getPointN(j + 1).getX(), renderer.yFlag * line.getPointN(j + 1).getY(),
					z + line.getPointN(j + 1).getCoordinate().z);
			}

		}
		gl.glEnd();

	}

	public void drawPlan(final LineString l, double z, final Color c, final double alpha, final double height,
		final Integer angle, final boolean drawPolygonContour) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		drawLineString(l, z, 1.2f, c, alpha);
		drawLineString(l, z + height, 1.2f, c, alpha);

		// Draw a quad
		gl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, alpha * c.getAlpha() / 255.0);
		int numPoints = l.getNumPoints();

		// Add z value
		if ( Double.isNaN(l.getCoordinate().z) == false ) {
			z = z + l.getCoordinate().z;
		}

		for ( int j = 0; j < numPoints - 1; j++ ) {

			if ( renderer.getComputeNormal() ) {
				Vertex[] vertices = new Vertex[3];
				for ( int i = 0; i < 3; i++ ) {
					vertices[i] = new Vertex();
				}
				vertices[0].x = l.getPointN(j).getX();
				vertices[0].y = renderer.yFlag * l.getPointN(j).getY();
				vertices[0].z = z;

				vertices[1].x = l.getPointN(j + 1).getX();
				vertices[1].y = renderer.yFlag * l.getPointN(j + 1).getY();
				vertices[1].z = z;

				vertices[2].x = l.getPointN(j + 1).getX();
				vertices[2].y = renderer.yFlag * l.getPointN(j + 1).getY();
				vertices[2].z = z + height;
				GLUtilNormal.HandleNormal(vertices, c, alpha, 1, renderer);
			}

			gl.glBegin(GL2ES3.GL_QUADS);
			gl.glVertex3d(l.getPointN(j).getX(), renderer.yFlag * l.getPointN(j).getY(), z);
			gl.glVertex3d(l.getPointN(j + 1).getX(), renderer.yFlag * l.getPointN(j + 1).getY(), z);
			gl.glVertex3d(l.getPointN(j + 1).getX(), renderer.yFlag * l.getPointN(j + 1).getY(), z + height);
			gl.glVertex3d(l.getPointN(j).getX(), renderer.yFlag * l.getPointN(j).getY(), z + height);
			gl.glEnd();
		}

		if ( drawPolygonContour == true ) {
			if ( !colorpicking ) {
				gl.glColor4d(0.0d, 0.0d, 0.0d, alpha * c.getAlpha() / 255.0);
			}

			for ( int j = 0; j < numPoints - 1; j++ ) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3d(l.getPointN(j).getX(), renderer.yFlag * l.getPointN(j).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), renderer.yFlag * l.getPointN(j + 1).getY(), z);

				gl.glVertex3d(l.getPointN(j + 1).getX(), renderer.yFlag * l.getPointN(j + 1).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), renderer.yFlag * l.getPointN(j + 1).getY(), z + height);

				gl.glVertex3d(l.getPointN(j + 1).getX(), renderer.yFlag * l.getPointN(j + 1).getY(), z + height);
				gl.glVertex3d(l.getPointN(j).getX(), renderer.yFlag * l.getPointN(j).getY(), z + height);

				gl.glVertex3d(l.getPointN(j).getX(), renderer.yFlag * l.getPointN(j).getY(), z + height);
				gl.glVertex3d(l.getPointN(j).getX(), renderer.yFlag * l.getPointN(j).getY(), z);

				gl.glEnd();
			}
			if ( !colorpicking ) {
				setColor(c, alpha);
			}

		}
	}

	public void DrawPoint(final Point point, double z, final int numPoints, final double radius, final Color c,
		final double alpha) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		if ( !colorpicking ) {
			setColor(c, alpha);
		}

		GLU.gluTessBeginPolygon(tobj, null);
		GLU.gluTessBeginContour(tobj);
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
			tempPolygon[k][1] = renderer.yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			tempPolygon[k][2] = z;
		}

		for ( int k = 0; k < numPoints; k++ ) {
			GLU.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		GLU.gluTessEndContour(tobj);
		GLU.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		if ( !colorpicking ) {
			gl.glColor4d(0.0d, 0.0d, 0.0d, alpha * c.getAlpha() / 255.0);
		}
		gl.glLineWidth(1.1f);
		gl.glBegin(GL.GL_LINES);
		double xBegin, xEnd, yBegin, yEnd;
		for ( int k = 0; k < numPoints; k++ ) {
			angle = k * 2 * Math.PI / numPoints;
			xBegin = point.getCoordinate().x + Math.cos(angle) * radius;
			yBegin = renderer.yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			angle = (k + 1) * 2 * Math.PI / numPoints;
			xEnd = point.getCoordinate().x + Math.cos(angle) * radius;
			yEnd = renderer.yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			gl.glVertex3d(xBegin, yBegin, z);
			gl.glVertex3d(xEnd, yEnd, z);
		}
		gl.glEnd();

	}

	// //////////////////////////////SPECIAL 3D SHAPE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void drawSphere(final GeometryObject g) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), renderer.yFlag * p.getCentroid().getY(), z);
		Color c = g.getColor();
		if ( !colorpicking ) {
			setColor(c, g.getAlpha());
		}
		Texture t = null;
		if ( g.isTextured ) {

			if ( g.hasTextures() ) {
				t = g.getTexture(gl, renderer, 1);
			} else {
				t = g.getTexture(gl, renderer, 0);
			}
			t.enable(gl);
			t.bind(gl);
			gl.glColor3d(1.0, 1.0, 1.0);
		}

		GLU glu = renderer.getGlu();

		GLUquadric quad = glu.gluNewQuadric();

		if ( g.isTextured ) {
			glu.gluQuadricTexture(quad, true);
		}

		if ( !renderer.data.isTriangulation() ) {

			glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		if ( g.isTextured ) {
			glu.gluQuadricOrientation(quad, GLU.GLU_INSIDE);
		} else {
			glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		}

		final int slices = 16;
		final int stacks = 16;

		glu.gluSphere(quad, g.height, slices, stacks);
		glu.gluDeleteQuadric(quad);
		if ( t != null ) {
			t.disable(gl);
		}

		gl.glTranslated(-p.getCentroid().getX(), -renderer.yFlag * p.getCentroid().getY(), -z);

	}

	public void drawCone3D(final GeometryObject g) {
		// (final Polygon p, final double radius, final Color c, final double alpha) {
		// Add z value (Note: getCentroid does not return a z value)
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(p.getCentroid().getX(), renderer.yFlag * p.getCentroid().getY(), z);
		if ( !colorpicking ) {
			Color c = g.getColor();
			setColor(c, g.getAlpha());
		}
		if ( !renderer.data.isTriangulation() ) {
			myGlut.glutSolidCone(g.height, g.height, 10, 10);
		} else {
			myGlut.glutWireCone(g.height, g.height, 10, 10);
		}

		gl.glTranslated(-p.getCentroid().getX(), -renderer.yFlag * p.getCentroid().getY(), -z);
	}

	public void drawTeapot(final GeometryObject g) {
		
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( !Double.isNaN(p.getCoordinate().z) ) {
			// TODO Normally, the NaN case is not true anymore
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}
 
		gl.glTranslated(p.getCentroid().getX(), renderer.yFlag * p.getCentroid().getY(), z);
		if ( !colorpicking ) {
			setColor(g.getColor(), g.getAlpha());
		}
		gl.glRotated(90, 1.0, 0.0, 0.0);
		myGlut.glutSolidTeapot(g.height);
		gl.glRotated(-90, 1.0, 0.0, 0.0);
		gl.glTranslated(-p.getCentroid().getX(), -renderer.yFlag * p.getCentroid().getY(), -z);
	}


	public void drawPyramid(final GeometryObject g) {

		GL2 gl = GLContext.getCurrentGL().getGL2();
		double z = 0.0;
		Polygon p = (Polygon) g.geometry;
		if ( Double.isNaN(p.getCoordinate().z) == false ) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glTranslated(0, 0, z);
		if ( !colorpicking ) {
			setColor(g.getColor(), g.getAlpha());;
		}
		PyramidSkeleton(p, g.height, g.getColor(), g.getAlpha());
		// border
		if ( g.border != null ) {
			if ( !colorpicking ) {
				setColor(g.border, g.getAlpha());
			}
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			gl.glPolygonOffset(0.0f, -(float) 1.1);
			PyramidSkeleton(p, g.height, g.border, g.getAlpha());
			gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			if ( !renderer.data.isTriangulation() ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			}
		}
		gl.glTranslated(0, 0, -z);
	}
	
	public void DrawMultiLineCylinder(final Geometry g, final Color c, final double alpha, final double height) {
		// get the number of line in the multiline.
		MultiLineString lines = (MultiLineString) g;
		int numGeometries = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {
			Geometry gg = lines.getGeometryN(i);
			drawLineCylinder(gg, c, alpha, height);

		}
	}

	public void drawLineCylinder(final Geometry g, final Color c, final double alpha, final double height) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double z = 0.0;

		Geometry gg = g;

		if ( Double.isNaN(gg.getCoordinate().z) == false ) {
			z = gg.getCentroid().getCoordinate().z;
		}
		if ( gg instanceof Point ) {
			// drawSphere(g, z);
			return;
		}
		LineString l = (LineString) gg;

		for ( int i = 0; i <= l.getNumPoints() - 2; i++ ) {

			if ( Double.isNaN(l.getCoordinate().z) == false ) {
				z = l.getPointN(i).getCoordinate().z;
			}

			double x_length = l.getPointN(i + 1).getX() - l.getPointN(i).getX();
			double y_length = l.getPointN(i + 1).getY() - l.getPointN(i).getY();
			double z_length = l.getPointN(i + 1).getCoordinate().z - l.getPointN(i).getCoordinate().z;

			double distance = Math.sqrt(x_length * x_length + y_length * y_length + z_length * z_length);

			gl.glTranslated(l.getPointN(i).getX(), renderer.yFlag * l.getPointN(i).getY(), z);
			Vector3d d;
			if ( Double.isNaN(l.getCoordinate().z) == false ) {
				d = new Vector3d((l.getPointN(i + 1).getX() - l.getPointN(i).getX()) / distance,
					-(l.getPointN(i + 1).getY() - l.getPointN(i).getY()) / distance,
					(l.getPointN(i + 1).getCoordinate().z - l.getPointN(i).getCoordinate().z) / distance);
			} else {
				d = new Vector3d((l.getPointN(i + 1).getX() - l.getPointN(i).getX()) / distance,
					-(l.getPointN(i + 1).getY() - l.getPointN(i).getY()) / distance, 0);
			}

			Vector3d z_up = new Vector3d(0, 0, 1);

			Vector3d a = new Vector3d();
			a.cross(z_up, d);

			double omega = Math.acos(z_up.dot(d));
			omega = omega * 180 / Math.PI;
			gl.glRotated(omega, a.x, a.y, a.z);

			if ( !colorpicking ) {
				setColor(c, alpha);
			}
			GLU myGlu = renderer.getGlu();
			GLUquadric quad = myGlu.gluNewQuadric();
			if ( !renderer.data.isTriangulation() ) {
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
			gl.glTranslated(-l.getPointN(i).getX(), -renderer.yFlag * l.getPointN(i).getY(), -z);
		}

	}

	public Vertex[] GetPyramidfaceVertices(final Polygon p, final int i, final int j, final double size, final int x,
		final int y) {
		Vertex[] vertices = new Vertex[3];
		for ( int i1 = 0; i1 < 3; i1++ ) {
			vertices[i1] = new Vertex();
		}

		vertices[0].x = p.getExteriorRing().getPointN(i).getX();
		vertices[0].y = renderer.yFlag * p.getExteriorRing().getPointN(i).getY();
		vertices[0].z = 0.0d;

		vertices[1].x = p.getExteriorRing().getPointN(j).getX();
		vertices[1].y = renderer.yFlag * p.getExteriorRing().getPointN(j).getY();
		vertices[1].z = 0.0d;

		vertices[2].x = p.getExteriorRing().getPointN(i).getX() + size / 2 * x;
		vertices[2].y = renderer.yFlag * (p.getExteriorRing().getPointN(i).getY() + size / 2 * y);
		vertices[2].z = size;
		return vertices;
	}

	public void PyramidSkeleton(final Polygon p, final double size, final Color c, final double alpha) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		Vertex[] vertices;
		double[] normal;

		if ( renderer.getComputeNormal() ) {
			vertices = getExteriorRingVertices(p);
			GLUtilNormal.HandleNormal(vertices, c, alpha, 1, renderer);
		}
		Coordinate coords[] = p.getExteriorRing().getCoordinates();
		

		gl.glBegin(GL2ES3.GL_QUADS);
		gl.glVertex3d(coords[0].x, renderer.yFlag * coords[0].y,coords[0].z);
		gl.glVertex3d(coords[1].x, renderer.yFlag * coords[1].y,coords[1].z);
		gl.glVertex3d(coords[2].x, renderer.yFlag * coords[2].y,coords[2].z);
		gl.glVertex3d(coords[3].x, renderer.yFlag * coords[3].y,coords[3].z);
		gl.glEnd();

		if ( renderer.getComputeNormal() ) {
			vertices = GetPyramidfaceVertices(p, 0, 1, size, 1, -1);
			GLUtilNormal.HandleNormal(vertices, c, alpha, -1, renderer);
		}
		
		double[] norm = CalculatePolygonNormal(p, null);
		norm[0] = norm[0]*size + p.getCentroid().getX();
		norm[1] = norm[1]*size + renderer.yFlag * p.getCentroid().getY();
		norm[2] = -norm[2]*size + p.getCentroid().getCoordinate().z;
		
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3d(coords[0].x, renderer.yFlag * coords[0].y, coords[0].z);
		gl.glVertex3d(coords[1].x, renderer.yFlag * coords[1].y, coords[1].z);
		gl.glVertex3d(norm[0],norm[1], norm[2]);
		gl.glEnd();

		if ( renderer.getComputeNormal() ) {
			vertices = GetPyramidfaceVertices(p, 1, 2, size, -1, -1);
			GLUtilNormal.HandleNormal(vertices, c, alpha, -1, renderer);
		}

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3d(coords[1].x, renderer.yFlag * coords[1].y, coords[1].z);
		gl.glVertex3d(coords[2].x, renderer.yFlag * coords[2].y, coords[2].z);
		gl.glVertex3d(norm[0],norm[1], norm[2]);
		gl.glEnd();
		
		
		if ( renderer.getComputeNormal() ) {
			vertices = GetPyramidfaceVertices(p, 2, 3, size, -1, 1);
			GLUtilNormal.HandleNormal(vertices, c, alpha, -1, renderer);
		}

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3d(coords[2].x, renderer.yFlag * coords[2].y, coords[2].z);
		gl.glVertex3d(coords[3].x, renderer.yFlag * coords[3].y, coords[3].z);
		gl.glVertex3d(norm[0],norm[1], norm[2]);
		gl.glEnd();
		
	
		if ( renderer.getComputeNormal() ) {
			vertices = GetPyramidfaceVertices(p, 3, 0, size, 1, 1);
			GLUtilNormal.HandleNormal(vertices, c, alpha, -1, renderer);
		}

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3d(coords[3].x, renderer.yFlag * coords[3].y, coords[3].z);
		gl.glVertex3d(coords[0].x, renderer.yFlag * coords[0].y, coords[0].z);
		gl.glVertex3d(norm[0],norm[1], norm[2]);
		gl.glEnd();
		
	
	}

	public boolean IsClockwise(final Vertex[] vertices) {
		double sum = 0.0;
		for ( int i = 0; i < vertices.length; i++ ) {
			Vertex v1 = vertices[i];
			Vertex v2 = vertices[(i + 1) % vertices.length];
			sum += (v2.x - v1.x) * (v2.y + v1.y);
		}
		return sum > 0.0;
	}

	public void drawRoundRectangle(final Polygon p) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double width = p.getEnvelopeInternal().getWidth();
		double height = p.getEnvelopeInternal().getHeight();

		gl.glTranslated(p.getCentroid().getX(), -p.getCentroid().getY(), 0.0d);
		DrawRectangle(width, height * 0.8, p.getCentroid());
		DrawRectangle(width * 0.8, height, p.getCentroid());
		DrawRoundCorner(width, height, width * 0.1, height * 0.1, 5);
		gl.glTranslated(-p.getCentroid().getX(), p.getCentroid().getY(), 0.0d);

	}

	void DrawRectangle(final double width, final double height, final Point point) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glBegin(GL2.GL_POLYGON); // draw using quads
		gl.glVertex3d(-width / 2, height / 2, 0.0d);
		gl.glVertex3d(width / 2, height / 2, 0.0d);
		gl.glVertex3d(width / 2, -height / 2, 0.0d);
		gl.glVertex3d(-width / 2, -height / 2, 0.0d);
		gl.glEnd();
	}

	void DrawFan(final double radius, final double x, final double y, final int or_x, final int or_y,
		final int timestep) {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glBegin(GL.GL_TRIANGLE_FAN); // upper right
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
		GL2 gl = GLContext.getCurrentGL().getGL2();
		double xc = width / 2 * 0.8;
		double yc = height / 2 * 0.8;
		// Enhancement implement DrawFan(radius, xc, yc, 10);

		gl.glBegin(GL.GL_TRIANGLE_FAN); // upper right
		gl.glVertex3d(xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(xc + xi, yc + yi, 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL.GL_TRIANGLE_FAN); // upper right

		gl.glVertex3d(xc, -yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(xc + xi, -(yc + yi), 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL.GL_TRIANGLE_FAN); // upper left

		gl.glVertex3d(-xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(-(xc + xi), yc + yi, 0.0d);
		}
		gl.glEnd();

		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3d(-xc, -yc, 0.0d); // down left
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			gl.glVertex3d(-(xc + xi), -(yc + yi), 0.0d);
		}
		gl.glEnd();
	}

	/*
	 * Return 9 array with the 3 vertex coordinates of the traingle
	 */
	public double[] GetTriangleVertices(final IShape shape) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();
		double[] vertices = new double[9];
		for ( int i = 0; i < 3; i++ ) {
			vertices[i * 3] = polygon.getExteriorRing().getPointN(0).getX();
			vertices[i * 3 + 1] = renderer.yFlag * polygon.getExteriorRing().getPointN(0).getY();
			vertices[i * 3 + 2] = 0.0d;
		}
		return vertices;
	}
}
