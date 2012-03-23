package msi.gama.jogl.gis_3D;

import static javax.media.opengl.GL.GL_QUADS;

import msi.gama.jogl.utils.MyGraphics;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import msi.gama.jogl.utils.TessellCallBack;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class Sector {

	public MyGeometry[] myGeometries;
	String myGeometryType = null;
	float myBoundCenter_x = 0.0f;
	float myBoundCenter_y = 0.0f;
	float mymaxBoundDimension = 0.0f;

	float refBoundCenter_x = 0.0f;
	float refBoundCenter_y = 0.0f;
	float refmaxBoundDimension = 0.0f;

	// Z value for each sector
	float z = -5.0f;// (float) -(Math.random()) * 5;

	double scale_rate;

	float red, green, blue = 0.0f;

	// Draw polygon with contour or in plain
	boolean drawAsPolygon = false;

	// Handle openg gl primitive.
	public MyGraphics graphics;

	public Sector(int numGeometries) {
 
		myGeometries = new MyGeometry[numGeometries];
		graphics = new MyGraphics();

	}



	// ///// SETUP SECTOR FUNCTION ////////////////

	public void SetupSector(SimpleFeatureCollection collection,
			float mymaxBoundRefDimension, float myBoundRefCenter_x,
			float myBoundRefCenter_y) {

		this.red = 0.0f;
		this.green = (float) (Math.random() * 1);
		this.blue = (float) (Math.random() * 0.5) + 0.5f;

		// FIXME CHANGE THE NAME OF myBoundRefCenter_x
		this.refBoundCenter_x = myBoundRefCenter_x;
		this.refBoundCenter_y = myBoundRefCenter_y;
		this.refmaxBoundDimension = mymaxBoundRefDimension;

		// Get the maximum dimension of the Bounding box containing all the
		// polygons to scale it.
		scale_rate = 5 / this.refmaxBoundDimension;

		try {

			SimpleFeatureIterator iterator = collection.features();

			// Set Bound characteristics
			this.myBoundCenter_x = (float) collection.getBounds().centre().x;
			this.myBoundCenter_y = (float) collection.getBounds().centre().y;

			// get Width and Height of the bound
			float boundWidth = (float) collection.getBounds().getWidth();
			float boundHeight = (float) collection.getBounds().getHeight();

			if (boundHeight < boundWidth) {
				this.mymaxBoundDimension = boundWidth;
			} else {
				this.mymaxBoundDimension = boundHeight;
			}



			// Create a polygon for each feature of the collection.
			try {
				int curPolygon = 0;
				int curLine = 0;
				int curPoint = 0;
				while (iterator.hasNext()) {
					SimpleFeature feature = (SimpleFeature) iterator.next();

					Geometry sourceGeometry = (Geometry) feature
							.getDefaultGeometry();

					if (sourceGeometry.getGeometryType() == "MultiPolygon") {

						MultiPolygon polygons = (MultiPolygon) sourceGeometry;
						SetupPolygonSector(polygons, curPolygon);
						curPolygon++;
					}

					else if (sourceGeometry.getGeometryType() == "MultiLineString") {
						MultiLineString lines = (MultiLineString) sourceGeometry;
						SetupLineSector(lines, curLine);
						curLine++;

					} else if (sourceGeometry.getGeometryType() == "Point") {
						// FIXME: We should only set the value once.
						Point p = (Point) sourceGeometry;
						SetupPointSector(p, curPoint);
						curPoint++;
					} else {
						System.out.println("Geometry Type: "
								+ this.myGeometryType + "not supported yet");
					}
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
			System.out.println("SetupSector: \nERROR: " + e.getMessage());
		}
	}

	private void SetupPolygonSector(MultiPolygon polygons, int curPolygon) {
		
		// FIXME: We should only set the value once.
		this.myGeometryType = "MultiPolygon";

		int N = polygons.getNumGeometries();
		
		

		// for each polygon of a multipolygon, get each point coordinates.
		for (int i = 0; i < N; i++) {

			Polygon p = (Polygon) polygons.getGeometryN(i);

			int numExtPoints = p.getExteriorRing().getNumPoints();
			this.myGeometries[curPolygon] = new MyGeometry(numExtPoints);

			// Get exterior ring (Be sure not to exceed the
			// number of point of the exterior ring)
			for (int j = 0; j < numExtPoints; j++) {
				this.myGeometries[curPolygon].vertices[j].x = (float) ((p
						.getExteriorRing().getPointN(j).getX() - this.refBoundCenter_x) * scale_rate);
				this.myGeometries[curPolygon].vertices[j].y = (float) ((p
						.getExteriorRing().getPointN(j).getY() - this.refBoundCenter_y) * scale_rate);
				this.myGeometries[curPolygon].vertices[j].z = z;
				this.myGeometries[curPolygon].vertices[j].u = 6.0f + (float) j;
				this.myGeometries[curPolygon].vertices[j].v = 0.0f + (float) j;
			}
		}
	}

	private void SetupLineSector(MultiLineString lines, int curLine) {
		// FIXME: We should only set the value once.
		this.myGeometryType = "MultiLineString";
		// get the number of line in the multiline.
		int N = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (int i = 0; i < N; i++) {
			LineString l = (LineString) lines.getGeometryN(i);

			int numPoints = l.getNumPoints();

			this.myGeometries[curLine] = new MyGeometry(numPoints);

			for (int j = 0; j < numPoints; j++) {

				this.myGeometries[curLine].vertices[j].x = (float) ((l
						.getPointN(j).getX() - this.refBoundCenter_x) * scale_rate);
				this.myGeometries[curLine].vertices[j].y = (float) ((l
						.getPointN(j).getY() - this.refBoundCenter_y) * scale_rate);
				this.myGeometries[curLine].vertices[j].z = z;
				this.myGeometries[curLine].vertices[j].u = 0.0f;
				this.myGeometries[curLine].vertices[j].v = 0.0f;
			}
		}
	}

	private void SetupPointSector(Point p, int curPoint) {
		this.myGeometryType = "Point";
		this.myGeometries[curPoint] = new MyGeometry(1);
		this.myGeometries[curPoint].vertices[0].x = (float) ((p.getCoordinate().x - this.refBoundCenter_x) * scale_rate);
		this.myGeometries[curPoint].vertices[0].y = (float) ((p.getCoordinate().y - this.refBoundCenter_y) * scale_rate);
		this.myGeometries[curPoint].vertices[0].z = z;
		this.myGeometries[curPoint].vertices[0].u = 0.0f;
		this.myGeometries[curPoint].vertices[0].v = 0.0f;
	}

	// ////////////////// DRAWING SECTOR FUNCTION //////////////////////

	/**
	 * Draw the sector depending on its geometry. Depending on the value of the
	 * boolean drawAsPolygon the polygon will be drawn as polygons or as lines.
	 * 
	 * @param gl
	 * @param glu
	 * @param zEnabled
	 *            boolean to display or not polygons in 3D
	 * @return void
	 */
	public void draw(GL gl, GLU glu, boolean zEnabled) {

		gl.glColor3f(this.red, this.green, this.blue);

		if (this.myGeometryType == "MultiPolygon") {

			if (drawAsPolygon) {
				DrawPolygons(gl, glu, zEnabled);

			} else {
				DrawLines(gl, glu);
			}
		}
		// Sector is made of line
		else if (this.myGeometryType == "MultiLineString") {
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			DrawLines(gl, glu);
		}
		// Sector is made of line
		else if (this.myGeometryType == "Point") {
			// gl.glColor3f(1.0f, 0.0f, 0.0f);

			DrawPoints(gl, glu);
		}
	}

	/**
	 * Draw sector as plain polygon or as line representing the contour of the
	 * polygon. .
	 * 
	 * 
	 * @param gl
	 * @param glu
	 * @param zEnabled
	 *            define wether or not the data are displayed in 3D.
	 * @return void
	 */
	private void DrawPolygons(GL gl, GLU glu, boolean zEnabled) {

		if (zEnabled == false) {
			Draw2DPolygons(gl, glu);
		}
		// 3D
		else {
			float z_offset = 1.0f;
			Draw3DPolygons(gl, glu, z_offset);
		}

	}

	private void Draw2DPolygons(GL gl, GLU glu) {

		for (int i = 0; i < this.myGeometries.length; i++) {
			graphics.DrawGeometry(gl, glu, this.myGeometries[i], 0.0f);
		}

	}

	private void Draw3DPolygons(GL gl, GLU glu, float z_offset) {

		for (int i = 0; i < this.myGeometries.length; i++) {

			// top face
			graphics.DrawGeometry(gl, glu, this.myGeometries[i], z_offset);

			// bottom face
			graphics.DrawGeometry(gl, glu, this.myGeometries[i], 0.0f);

			// all the front-face of the polygons as a verticle quads.
			graphics.Draw3DQuads(gl, glu, this.myGeometries[i], z_offset);
		}

	}

	/**
	 * Draw circle representing point data.
	 * 
	 * 
	 * @param gl
	 * @param glu
	 * @return void
	 */
	private void DrawPoints(GL gl, GLU glu) {

		for (int i = 0; i < this.myGeometries.length; i++) {
			// FIXME: Should test that vertices is initialized before to
			// draw it.

			graphics.DrawCircle(gl, glu,
					(float) (this.myGeometries[i].vertices[0].x),
					(float) (this.myGeometries[i].vertices[0].y),
					(float) (this.myGeometries[i].vertices[0].z), 12, 0.025f);
		}

	}

	/**
	 * Representing point and a data associated to this point. This data can be
	 * represented as color variation of the element or as a z value associated
	 * to the data we want to visualize.
	 * 
	 * 
	 * @param gl
	 * @param glu
	 * @param lightTrapsDatas
	 *            2D table with an integer value for each point (e.g number of
	 *            insect in a given lighttrap).
	 * @param iteration
	 *            temporary trick to display the value corresponding to a given
	 *            iteration.
	 * @param zEnabled
	 *            define wether or not the data are displayed in 3D.
	 * @return void
	 */
	public void DrawPointsFromValue(GL gl, GLU glu, float lightTrapsDatas[][],
			int iteration, boolean zEnabled) {

		if (!zEnabled) {
			for (int i = 0; i < this.myGeometries.length; i++) {
				// FIXME: Should test that vertices is initialized before to
				// draw it.
				float red_value = lightTrapsDatas[i][iteration % 30] / 10;
				gl.glColor3f(red_value, 0.0f, 0.0f);

				graphics.DrawCircle(gl, glu,
						(float) (this.myGeometries[i].vertices[0].x),
						(float) (this.myGeometries[i].vertices[0].y),
						(float) (this.myGeometries[i].vertices[0].z), 12,
						0.025f);

			}
		} else {
			for (int i = 0; i < this.myGeometries.length; i++) {
				float z_data = lightTrapsDatas[i][iteration % 30] / 100;
				gl.glColor3f(0.3f, 0.9f, 0.2f);

				TessellCallBack tessCallback = new TessellCallBack(gl, glu);

				GLUtessellator tobj = glu.gluNewTess();
				glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
				glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
				glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
				glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

				float x = (float) (this.myGeometries[i].vertices[0].x);
				float y = (float) (this.myGeometries[i].vertices[0].y);
				float z = (float) (this.myGeometries[i].vertices[0].z);

				int curPolyGonNumPoints = 12;
				float radius = 0.05f;

				float angle, angle1;

				// top face
				graphics.DrawCircle(gl, glu, (float) (x), (float) (y),
						(float) (z), curPolyGonNumPoints, radius);

				graphics.DrawCircle(gl, glu, (float) (x), (float) (y),
						(float) (z + z_data), curPolyGonNumPoints, radius);

				// all the front-face of the polygons as a verticle
				// quads.

				for (int j = 0; j < curPolyGonNumPoints; j++) {
					angle = (float) (j * 2 * Math.PI / curPolyGonNumPoints);
					angle1 = (float) (j + 1 * 2 * Math.PI / curPolyGonNumPoints);
					gl.glBegin(GL_QUADS);
					if (j == 3) {
						gl.glNormal3f(0.0f, 0.0f, 1.0f);
					}
					if (j == 0) {
						gl.glNormal3f(-1.0f, 0.0f, 0.0f);
					}
					if (j == 1) {
						gl.glNormal3f(0.0f, 0.0f, -1.0f);
					}

					if (j == 2) {
						gl.glNormal3f(1.0f, 0.0f, 0.0f);
					}

					gl.glVertex3f(
							this.myGeometries[i].vertices[0].x
									+ (float) (Math.cos(angle)) * radius,
							this.myGeometries[i].vertices[0].y
									+ (float) (Math.sin(angle)) * radius,
							this.myGeometries[i].vertices[0].z);
					gl.glVertex3f(
							this.myGeometries[i].vertices[0].x
									+ (float) (Math.cos(angle1)) * radius,
							this.myGeometries[i].vertices[0].y
									+ (float) (Math.sin(angle1)) * radius,
							this.myGeometries[i].vertices[0].z);
					gl.glVertex3f(
							this.myGeometries[i].vertices[0].x
									+ (float) (Math.cos(angle1)) * radius,
							this.myGeometries[i].vertices[0].y
									+ (float) (Math.sin(angle1)) * radius,
							this.myGeometries[i].vertices[0].z + z_data);
					gl.glVertex3f(
							this.myGeometries[i].vertices[0].x
									+ (float) (Math.cos(angle)) * radius,
							this.myGeometries[i].vertices[0].y
									+ (float) (Math.sin(angle)) * radius,
							this.myGeometries[i].vertices[0].z + z_data);
					gl.glEnd();
				}

			}
		}

	}

	/**
	 * Draw line corresponding to the sector.
	 * 
	 * 
	 * @param gl
	 * @param glu
	 * @return void
	 */
	private void DrawLines(GL gl, GLU glu) {
		for (int i = 0; i < this.myGeometries.length; i++) {
			graphics.DrawLine(gl, glu, this.myGeometries[i]);
		}

	}

	public void SetZValue(float z) {
		for (int i = 0; i < this.myGeometries.length; i++) {
			for (int j = 0; j < this.myGeometries[i].vertices.length; j++) {
				this.myGeometries[i].vertices[j].z = z;
			}
		}

	}

	public void SetDrawAsPoygon(boolean value) {
		this.drawAsPolygon = value;

	}
}



