/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.jogl;

import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.jogl.utils.*;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.types.GamaGeometryType;

import org.jfree.chart.JFreeChart;

import com.sun.opengl.util.GLUT;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

/**
 * 
 * Simplifies the drawing of circles, rectangles, and so forth. Rectangles are
 * generally faster to draw than circles. The Displays should take care of
 * layouts while objects that wish to be drawn as a shape need only call the
 * appropriate method.
 * 
 * @author Arnaud Grignard, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class JOGLAWTDisplayGraphics implements IGraphics {

	boolean ready = false;
	private Rectangle clipping;
	private final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	private final Ellipse2D oval = new Ellipse2D.Double(0, 0, 1, 1);
	private final Line2D line = new Line2D.Double();
	private double currentAlpha = 1;
	private int displayWidth, displayHeight, curX = 0, curY = 0, curWidth = 5,
			curHeight = 5, offsetX = 0, offsetY = 0;

	// OpenGL member
	private GL myGl;
	private GLU myGlu;

	// Handle opengl primitive.
	public MyGraphics graphicsGLUtils;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	// Each geometry drawn is stored in a list.
	public ArrayList<MyGeometry> myGeometries = new ArrayList<MyGeometry>();

	// List of all the JTS gemoetry present in the model
	public ArrayList<MyJTSGeometry> myJTSGeometries = new ArrayList<MyJTSGeometry>();

	// each Image is stored in a list
	public ArrayList<MyImage> myImages = new ArrayList<MyImage>();

	// Define the environment properties.
	public float myWidth, myHeight, myMaxDim;

	// All the geometry are drawn in the same z plan (depend on the sale rate).
	public float z;

	// OpenGL list ID
	private int listID = -1;

	private final PointTransformation pt = new PointTransformation() {

		@Override
		public void transform(final Coordinate c, final Point2D p) {

		}
	};
	private final ShapeWriter sw = new ShapeWriter(pt);

	/**
	 * Constructor for OpenGL DisplayGraphics constructor. Simplify for opengl
	 * display. The environment property are given from the display surface.
	 * 
	 * @param GL
	 *            gl
	 * @param GLU
	 *            glu
	 * @param float env_width
	 * @param float env_height
	 * @param float scale_rate
	 */
	public JOGLAWTDisplayGraphics(final GL gl, final GLU glu,
			JOGLAWTGLRenderer gLRender, float env_width, float env_height) {
		myGl = gl;
		myGlu = glu;
		myWidth = env_width;
		myHeight = env_height;

		if (myWidth > myHeight) {
			myMaxDim = myWidth;
		} else {
			myMaxDim = myHeight;
		}

		z = 0.0f;
		graphicsGLUtils = new MyGraphics();

		myGLRender = gLRender;
	}

	/**
	 * Method setGraphics.
	 * 
	 * @param g
	 *            Graphics2D
	 */
	@Override
	public void setGraphics(final Graphics2D g) {
		ready = true;
		setQualityRendering(false);
	}

	@Override
	public void setQualityRendering(final boolean quality) {

	}

	@Override
	public boolean isReady() {
		return ready;
	}

	/**
	 * Method setComposite.
	 * 
	 * @param alpha
	 *            AlphaComposite
	 */
	@Override
	public void setOpacity(final double alpha) {
		// 1 means opaque ; 0 means transparent
		if (IntervalSize.isZeroWidth(alpha, currentAlpha)) {
			return;
		}
		currentAlpha = alpha;
	}

	/**
	 * Method getDisplayWidth.
	 * 
	 * @return int
	 */
	@Override
	public int getDisplayWidth() {
		return displayWidth;
	}

	/**
	 * Method getDisplayHeight.
	 * 
	 * @return int
	 */
	@Override
	public int getDisplayHeight() {
		return displayHeight;
	}

	/**
	 * Method setDisplayDimensions.
	 * 
	 * @param width
	 *            int
	 * @param height
	 *            int
	 */
	@Override
	public void setDisplayDimensions(final int width, final int height) {
		displayWidth = width;
		displayHeight = height;
	}

	/**
	 * Method setFont.
	 * 
	 * @param font
	 *            Font
	 */
	@Override
	public void setFont(final Font font) {

	}

	/**
	 * Method getXScale.
	 * 
	 * @return double
	 */
	@Override
	public double getXScale() {
		return 1;
	}

	/**
	 * Method setXScale.
	 * 
	 * @param scale
	 *            double
	 */
	@Override
	public void setXScale(final double scale) {
		// this.currentXScale = scale;
	}

	/**
	 * Method getYScale.
	 * 
	 * @return double
	 */
	@Override
	public double getYScale() {
		return 1;
	}

	/**
	 * Method setYScale.
	 * 
	 * @param scale
	 *            double
	 */
	@Override
	public void setYScale(final double scale) {
		// this.currentYScale = scale;
	}

	/**
	 * Method setDrawingCoordinates.
	 * 
	 * @param x
	 *            double
	 * @param y
	 *            double
	 */
	@Override
	public void setDrawingCoordinates(final double x, final double y) {
		curX = (int) x + offsetX;
		curY = (int) y + offsetY;
	}

	/**
	 * Method setDrawingOffset.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	@Override
	public void setDrawingOffset(final int x, final int y) {
		offsetX = x;
		offsetY = y;
	}

	/**
	 * Method setDrawingDimensions.
	 * 
	 * @param width
	 *            int
	 * @param height
	 *            int
	 */
	@Override
	public void setDrawingDimensions(final int width, final int height) {
		curWidth = width;
		curHeight = height;
	}

	/**
	 * Method setDrawingColor.
	 * 
	 * @param c
	 *            Color
	 */
	private void setDrawingColor(final Color c) {

	}

	// //////// draw method ///////////////

	/**
	 * Method drawGeometry. Add a given JTS Geometry in the list of all the
	 * existing geometry that will be displayed by openGl.
	 * 
	 * @param geometry
	 *            Geometry
	 * @param color
	 *            Color
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawGeometry(final Geometry geometry, final Color color,
			final boolean fill, final Integer angle) {
		//System.out.println("drawGeometry:" + geometry.getGeometryType());
		this.AddJTSGeometryInJTSGeometries(geometry, color);
		return sw.toShape(geometry).getBounds2D();
	}

	
	@Override
	public void drawGrid(BufferedImage image, Color lineColor,
			java.awt.Point point) {

		double stepX, stepY;
		for (int i = 0; i <= image.getWidth(); i++) {
			stepX = (i / (double) image.getWidth()) * (double) image.getWidth();
			Geometry g = GamaGeometryType.buildLine(new GamaPoint(stepX, 0),
					new GamaPoint(stepX, -image.getWidth())).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g, lineColor);
		}

		for (int i = 0; i <= image.getHeight(); i++) {
			stepY = -(i / (double) image.getHeight())
					* (double) image.getHeight();
			;
			Geometry g = GamaGeometryType.buildLine(new GamaPoint(0, stepY),
					new GamaPoint(image.getHeight(), stepY)).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g, lineColor);
		}

	}

	/**
	 * Method drawImage.
	 * 
	 * @param img
	 *            Image
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawImage(final BufferedImage img, final Integer angle,
			final boolean smooth) {

		AddImageInImages(img, curX, curY);
		rect.setRect(curX, curY, curWidth, curHeight);
		return rect.getBounds2D();
	}

	@Override
	public Rectangle2D drawImage(final BufferedImage img, final Integer angle) {
		return drawImage(img, angle, true);
	}

	/**
	 * Method drawChart.
	 * 
	 * @param chart
	 *            JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final JFreeChart chart) {
		rect.setRect(curX, curY, curWidth, curHeight);
		return rect.getBounds2D();
	}

	/**
	 * Method drawCircle.
	 * 
	 * @param c
	 *            Color
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawCircle(final Color c, final boolean fill,
			final Integer angle) {
		// FIXME : Need to check if the circle is at the right place.
		Geometry g = GamaGeometryType.buildCircle(
				((double) curWidth) / 2,
				new GamaPoint(curX + ((double) curWidth) / 2, curY
						+ ((double) curWidth) / 2)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, c);
		oval.setFrame(curX, curY, curWidth, curWidth);
		return oval.getBounds2D();
	}

	@Override
	public Rectangle2D drawTriangle(final Color c, final boolean fill,
			final Integer angle) {
		// FIXME: check if size is curWidth or curWidth/2
		Geometry g = GamaGeometryType.buildTriangle(curWidth,
				new GamaPoint(curX, curY)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, c);
		Rectangle2D r = null;
		return r;
	}

	/**
	 * Method .
	 * 
	 * @param c
	 *            Color
	 * @param toX
	 *            double
	 * @param toY
	 *            double
	 */
	@Override
	public Rectangle2D drawLine(final Color c, final double toX,
			final double toY) {
		Geometry g = GamaGeometryType.buildLine(new GamaPoint(curX, curY),
				new GamaPoint(toX, toY)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, c);
		line.setLine(curX, curY, toX + offsetX, toY + offsetY);
		return line.getBounds2D();
	}

	/**
	 * Method drawRectangle.
	 * 
	 * @param color
	 *            Color
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawRectangle(final Color c, final boolean fill,
			final Integer angle) {
		Geometry g = GamaGeometryType.buildRectangle(curWidth, curHeight,
				new GamaPoint(curX, curY)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, c);
		rect.setFrame(curX, curY, curWidth, curHeight);
		return rect.getBounds2D();
	}

	/**
	 * Method drawString.
	 * 
	 * @param string
	 *            String
	 * @param stringColor
	 *            Color
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawString(final String string, final Color stringColor,
			final Integer angle) {
		setDrawingColor(stringColor);
		Rectangle2D r = null;
		return r;
	}

	@Override
	public void fill(final Color bgColor, final double opacity) {
		setOpacity(opacity);
	}

	@Override
	public void setClipping(final Rectangle imageClipBounds) {
		clipping = imageClipBounds;
	}

	@Override
	public Rectangle getClipping() {
		return clipping;
	}

	// ///////////////Add method ////////////////////////////

	/**
	 * Add geometry and its associated color in the list of JTSGeometry that are
	 * drawn by Opengl
	 * 
	 * @param geometry
	 * @param color
	 */
	private void AddJTSGeometryInJTSGeometries(Geometry geometry, Color color) {

		//System.out.println("Add:"+ geometry.getGeometryType() + color.getRed() +" " +color.getGreen() +" " + color.getBlue());
		MyJTSGeometry curJTSGeometry = new MyJTSGeometry();
		curJTSGeometry.geometry = geometry;
		curJTSGeometry.color = color;
		this.myJTSGeometries.add(curJTSGeometry);
	}



	private void AddImageInImages(final BufferedImage img, int curX, int curY) {

		final MyImage curImage = new MyImage();
		curImage.image = img;
		curImage.x = (float) (curX);
		curImage.y = (float) (curY);
		myGLRender.InitTexture(img);
		this.myImages.add(curImage);

	}

	// /////////////// Draw Method ////////////////////

	public void DrawMyJTSGeometries() {

		Iterator<MyJTSGeometry> it = this.myJTSGeometries.iterator();
		while (it.hasNext()) {
			MyJTSGeometry curGeometry = it.next();
			// System.out.println(curGeometry.geometry.getGeometryType());
			//System.out.println("Draw:"+ curGeometry.geometry.getGeometryType() + curGeometry.color.getRed() +" " +curGeometry.color.getGreen() +" " + curGeometry.color.getBlue());	
			graphicsGLUtils.DrawJTSGeometry(myGl, myGlu, curGeometry.geometry,curGeometry.color);
		}

	}
	
	public void DrawMyImages() {

		Iterator<MyImage> it = this.myImages.iterator();
		int id = 0;
		while (it.hasNext()) {
			MyImage curImage = (MyImage) it.next();
			myGLRender.DrawTexture(id, curImage);
			id++;
		}
	}

	public void DrawEnvironmentBounds() {

		// Draw Width and height value
		GLUT glut = new GLUT();
		myGl.glRasterPos3f(this.myWidth / 2, this.myHeight * 0.01f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				String.valueOf(this.myWidth));

		myGl.glRasterPos3f(this.myWidth * 1.01f, -(this.myHeight / 2), 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				String.valueOf(this.myHeight));

		// Draw environment rectangle
		Geometry g = GamaGeometryType.buildRectangle(myWidth, myHeight,
				new GamaPoint(myWidth / 2, myHeight / 2)).getInnerGeometry();


		Color c =new Color (0,0,255);
		graphicsGLUtils.DrawJTSGeometry(myGl, myGlu, g,c);
	}

	public void DrawXYZAxis(float size) {
		GLUT glut = new GLUT();

		myGl.glRasterPos3f(size, size, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				"1:" + String.valueOf(size));
		// X Axis
		myGl.glRasterPos3f(1.2f * size, 0.0f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "x");
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor3f(1.0f, 0, 0);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(size, 0, 0);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(1.0f * size, 0.05f * size, 0.0f);
		myGl.glVertex3f(1.0f * size, -0.05f * size, 0.0f);
		myGl.glVertex3f(1.1f * size, 0.0f, 0.0f);
		myGl.glEnd();

		// Y Axis
		myGl.glRasterPos3f(0.0f, 1.2f * size, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "y");
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor3f(0, 1.0f, 0);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(0, size, 0);
		myGl.glEnd();
		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(-0.05f * size, 1.0f * size, 0.0f);
		myGl.glVertex3f(0.05f * size, 1.0f * size, 0.0f);
		myGl.glVertex3f(0.0f, 1.1f * size, 0.0f);
		myGl.glEnd();

		// Z Axis
		myGl.glRasterPos3f(0.0f, 0.0f, 1.2f * size);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "z");
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor3f(0, 0, 1.0f);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(0, 0, size);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(0.0f, 0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, -0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, 0.0f, 1.1f * size);
		myGl.glEnd();

	}

	public void DrawZValue(float pos, float value) {

		GLUT glut = new GLUT();
		myGl.glRasterPos3f(pos, pos, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				"z:" + String.valueOf(value));
	}

	public void DrawScale() {
		GLUT glut = new GLUT();
		// Draw Scale
		float y_text_pos = -(this.myHeight + this.myHeight * 0.05f);
		myGl.glRasterPos3f(0.0f, y_text_pos * 0.99f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "Scale:" + "1");
		myGl.glBegin(GL.GL_LINES);
		myGl.glVertex3f(0, 0 + y_text_pos, 0);
		myGl.glVertex3f(this.myWidth * 0.05f, 0 + y_text_pos, 0);

		myGl.glVertex3f(0, -0.05f + y_text_pos, 0);
		myGl.glVertex3f(0, 0.05f + y_text_pos, 0);

		myGl.glVertex3f(1, -0.05f + y_text_pos, 0);
		myGl.glVertex3f(1, 0.05f + y_text_pos, 0);
		myGl.glEnd();
	}

	public void CleanGeometries() {
		this.myGeometries.clear();
		this.myJTSGeometries.clear();
	}

	public void CleanImages() {
		this.myImages.clear();
		myGLRender.myTextures.clear();
	}

	public void draw(GL gl) {

		if (listID == -1) {
			createDisplayList(gl);
		}
		gl.glCallList(listID);

	}

	private void createDisplayList(GL gl) {
		GLU glu = new GLU();
		GLUquadric quadric = glu.gluNewQuadric();
		listID = gl.glGenLists(1);
		gl.glNewList(listID, GL.GL_COMPILE);
		gl.glPushMatrix();
		gl.glColor3d(1, 1, 1);
		glu.gluSphere(quadric, 1 + 0.4, 30, 30);
		gl.glPopMatrix();
		gl.glEndList();
	}

}
