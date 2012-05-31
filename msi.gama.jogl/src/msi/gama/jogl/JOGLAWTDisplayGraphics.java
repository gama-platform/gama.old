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

import static javax.media.opengl.GL.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
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
	private int displayWidth, displayHeight, curX = 0, curY = 0, curWidth = 5, curHeight = 5,
		offsetX = 0, offsetY = 0;

	// OpenGL member
	private final GL myGl;
	private final GLU myGlu;
	
	// Handle opengl primitive.
	public MyGraphics graphicsGLUtils;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	// List of all the JTS gemoetry present in the model
	public ArrayList<MyJTSGeometry> myJTSGeometries = new ArrayList<MyJTSGeometry>();

	// each Image is stored in a list
	public ArrayList<MyImage> myImages = new ArrayList<MyImage>();

	// List of all the String
	public ArrayList<MyString> myStrings = new ArrayList<MyString>();

	// Define the environment properties.
	public float envWidth, envHeight, maxEnvDim;

	// All the geometry of the same layer are drawn in the same z plan.
	public float currentZvalue=0.0f;

	// OpenGL list ID
	private int listID = -1;
	public boolean isListCreated = false;
	
	
	
	//FIXME: This need to be remove. Only here to return the bounds of a geometry.
	private final PointTransformation pt = new PointTransformation() {
	
		@Override
		public void transform(final Coordinate c, final Point2D p) {
		
				}
			};
	private final ShapeWriter sw = new ShapeWriter(pt);

	
	/**
	 * The environment property are given from the display surface.
	 * 
	 * @param GL gl
	 * @param GLU glu
	 * @param float env_width
	 * @param float env_height
	 */
	public JOGLAWTDisplayGraphics(final GL gl, final GLU glu, final JOGLAWTGLRenderer gLRender,
		final float env_width, final float env_height) {
		
		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		graphicsGLUtils = new MyGraphics(myGl, myGlu, myGLRender);
		
		
		//Initialize the current environment data.
		envWidth = env_width;
		envHeight = env_height;

		if ( envWidth > envHeight ) {
			maxEnvDim = envWidth;
		} else {
			maxEnvDim = envHeight;
		}	
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
		if ( IntervalSize.isZeroWidth(alpha, currentAlpha) ) { return; }
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
	public Rectangle2D drawGeometry(final Geometry geometry, final Color color, final boolean fill,
		final Integer angle) {
		
		//Check if the geometry has a z elevation value
		float elevation;
		if (geometry.getUserData() !=null){
			elevation = new Float(geometry.getUserData().toString());
			this.AddJTSGeometryInJTSGeometries(geometry,currentZvalue, color, fill, false, angle,elevation);
		}
		else{
			this.AddJTSGeometryInJTSGeometries(geometry,currentZvalue, color, fill, false, angle,0);
		}
		//FIXME: Need to remove the use of sw.
		return sw.toShape(geometry).getBounds2D();
	}

	@Override
	public void drawGrid(final BufferedImage image, final Color lineColor,
		final java.awt.Point point) {

		double stepX, stepY;
		for ( int i = 0; i <= image.getWidth(); i++ ) {
			stepX = i / (double) image.getWidth() * image.getWidth();
			Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(stepX, 0),
					new GamaPoint(stepX, -image.getWidth())).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g,currentZvalue, lineColor, true, false, 0,0);
		}

		for ( int i = 0; i <= image.getHeight(); i++ ) {
			stepY = -(i / (double) image.getHeight()) * image.getHeight();;
			Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(0, stepY),
					new GamaPoint(image.getHeight(), stepY)).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g,currentZvalue, lineColor, true, false, 0,0);
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
		final boolean smooth, final String name) {
	
		/* FIXME Dirty way to check that img represent the environment. 
		
			if(image represent the environment){
			   drawImage with the size of the envirnoment
			   }
			 else{
			 drawImage with the size of the agent (curWidth and curHeight)
			}
	
         WARNING1: problem if an agent reach the 0,0 position it will be displayed wit the size of the enviroment
         WARNING2: if the environment is represented with a png and is not located at 0,0 (e.g boids environment is sky.png) how to know if this image is the enviroment?
		 */

		//System.out.println("drawImage" + "curX" + curX + "curY" +curY
		//+"img.getWidth()"+img.getWidth()+"img.getHeight()"+img.getHeight() + name);
		
		if ( curX == 0 && curY == 0 || (name.equals("GridDisplay") == true || name.equals("QuadTreeDisplay"))) {
			AddImageInImages(img, curX, curY, currentZvalue,this.envWidth, this.envHeight, name, angle);
			rect.setRect(curX, curY, img.getWidth(), img.getHeight());
		} else {
			AddImageInImages(img, curX, curY,currentZvalue, curWidth, curHeight, name, angle);	
			rect.setRect(curX, curY, curWidth, curHeight);
		}

		return rect.getBounds2D();
	}

	@Override
	public Rectangle2D drawImage(final BufferedImage img, final Integer angle, final String name) {
		return drawImage(img, angle, true, name);
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
	public Rectangle2D drawCircle(final Color c, final boolean fill, final Integer angle) {
		// FIXME : Need to check if the circle is at the right place.
		Geometry g =
			GamaGeometryType.buildCircle((double) curWidth / 2,
				new GamaPoint(curX + (double) curWidth / 2, curY + (double) curWidth / 2))
				.getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g,currentZvalue, c, fill, false, 0,0);
		oval.setFrame(curX, curY, curWidth, curWidth);
		return oval.getBounds2D();
	}

	@Override
	public Rectangle2D drawTriangle(final Color c, final boolean fill, final Integer angle) {
		// FIXME: check if size is curWidth or curWidth/2
		Geometry g =
			GamaGeometryType.buildTriangle(curWidth, new GamaPoint(curX, curY)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g,currentZvalue, c, fill, false, angle,0);
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
	public Rectangle2D drawLine(final Color c, final double toX, final double toY) {
		Geometry g =
			GamaGeometryType.buildLine(new GamaPoint(curX, curY), new GamaPoint(toX, toY))
				.getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g,currentZvalue, c, true, false, 0,0);
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
	public Rectangle2D drawRectangle(final Color c, final boolean fill, final Integer angle) {
		Geometry g =
			GamaGeometryType.buildRectangle(curWidth, curHeight, new GamaPoint(curX, curY))
				.getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g,currentZvalue, c, fill, false, angle,0);
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
	public Rectangle2D drawString(final String string, final Color stringColor, final Integer angle) {
		AddStringInStrings(string, curX, -curY, 0.0f);
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
	 * Add geometry and its associated parameter in the list of JTSGeometry that are
	 * drawn by Opengl
	 * 
	 * @param geometry
	 * @param color
	 * @param fill
	 * @param isTextured
	 * @param angle
	 * @param elevation
	 */
	private void AddJTSGeometryInJTSGeometries(final Geometry geometry, final float z,final Color color,
		final boolean fill, final boolean isTextured, final Integer angle,final float elevation) {

		MyJTSGeometry curJTSGeometry = new MyJTSGeometry();
		curJTSGeometry.geometry = geometry;
		curJTSGeometry.z=z;
		curJTSGeometry.color = color;
		curJTSGeometry.fill = fill;
		curJTSGeometry.isTextured = isTextured;
		curJTSGeometry.angle = angle;
		curJTSGeometry.elevation= elevation;
		this.myJTSGeometries.add(curJTSGeometry);
	}

	/**
	 * Add image and its associated parameter in the list of Image that are
	 * drawn by Opengl
	 * 
	 * @param img
	 * @param curX
	 * @param curY
	 * @param z
	 * @param widthInModel
	 * @param heightInModel
	 * @param name
	 * @param angle
	 */
	private void AddImageInImages(final BufferedImage img, final int curX, final int curY,final float z,
		final float widthInModel, final float heightInModel, final String name, final Integer angle) {

		final MyImage curImage = new MyImage();

		curImage.image = img;
		curImage.x = curX;
		curImage.y = curY;
		curImage.z= z;
		curImage.width = widthInModel;
		curImage.height = heightInModel;
		if ( angle == null ) {
			curImage.angle = 0;
		} else {
			curImage.angle = angle;
		}

		curImage.name = name;

		// For grid display and quadtree display the image is recomputed every iteration
		if ( curImage.name.equals("GridDisplay") == true || curImage.name.equals("QuadTreeDisplay") ) {
			myGLRender.InitTexture(img, name);
		} else {//For texture coming from a file there is no need to redraw it.
			if ( !IsTextureExist(name) ) {
				myGLRender.InitTexture(img, name);
			}
		}
		this.myImages.add(curImage);

	}

	/**
	 * Check that the texture "name" has not already be created.
	 * @param name
	 * @return
	 */
	private boolean IsTextureExist(final String name) {
		
		Iterator<MyTexture> it = myGLRender.myTextures.iterator();
		while (it.hasNext()) {
			MyTexture curTexture = it.next();
			if ( name.equals(curTexture.ImageName) == true ) { return true; }
		}
		return false;
	}

	/**
	 * Add string and its postion in the list of String that are
	 * drawn by Opengl
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param z
	 */
	private void AddStringInStrings(final String string, final float x, final float y, final float z) {

		final MyString curString = new MyString();
		curString.string = string;
		curString.x = x;
		curString.y = y;
		curString.z = z;
		this.myStrings.add(curString);

	}

	// /////////////// Draw Method ////////////////////

	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this method every framerate.
	 * FIXME: Need to be optimize with the use of Vertex Array or even VBO
	 * 
	 */
	public void DrawMyJTSGeometries() {

		boolean drawAsList = false;
		//	System.out.println("isListCreated="+isListCreated);
		if ( drawAsList ) {
			if ( !isListCreated ) {
				System.out.println("Create" + this.myJTSGeometries.size() + "list");
				graphicsGLUtils.buildDisplayLists(this.myJTSGeometries);
				System.out.println("Create" + this.myJTSGeometries.size() + "list ok");
				isListCreated = true;
			} else {
				//System.out.println("Call" + this.myJTSGeometries.size() + "list");
				graphicsGLUtils.DrawDisplayList(this.myJTSGeometries.size());
			}
		} else {

			Iterator<MyJTSGeometry> it = this.myJTSGeometries.iterator();
			while (it.hasNext()) {
				MyJTSGeometry curGeometry = it.next();
				graphicsGLUtils.DrawJTSGeometry(curGeometry);
			}
		}

	}

	/**
	 * Once the list of Images has been created, OpenGL display call this method every framerate.
	 * FIXME: Need to be optimize with the use of Vertex Array or even VBO
	 * 
	 */
	public void DrawMyImages() {

		boolean drawImageAsList = false;
		if ( drawImageAsList ) {
			if ( !isListCreated ) {
				graphicsGLUtils.buildImageDisplayLists(this.myImages);
				isListCreated = true;
			} else {
				graphicsGLUtils.DrawImageDisplayList(this.myImages.size());
			}

		} else {
			Iterator<MyImage> it = this.myImages.iterator();
			while (it.hasNext()) {
				MyImage curImage = it.next();
				myGLRender.DrawTexture(curImage);
			}
		}
	}

	/**
	 * Once the list of String has been created, OpenGL display call this method every framerate.
	 * 
	 */
	public void DrawMyStrings() {

		Iterator<MyString> it = this.myStrings.iterator();
		while (it.hasNext()) {
			MyString curString = it.next();
			graphicsGLUtils.DrawString(curString.string, curString.x, curString.y, 0.0f);
		}
	}
	
	public void DrawEnvironmentBounds() {

		// Draw Width and height value
		this.graphicsGLUtils.DrawString(String.valueOf(this.envWidth), this.envWidth / 2, this.envHeight * 0.01f, 0.0f);
		this.graphicsGLUtils.DrawString(String.valueOf(this.envHeight), this.envWidth * 1.01f, -(this.envHeight / 2),
			0.0f);

		// Draw environment rectangle
		Geometry g =
			GamaGeometryType.buildRectangle(envWidth, envHeight,
				new GamaPoint(envWidth / 2, envHeight / 2)).getInnerGeometry();

		Color c = new Color(0, 0, 255);
		MyJTSGeometry curGeometry = new MyJTSGeometry();
		curGeometry.geometry = g;
		curGeometry.color = c;
		curGeometry.fill = true;
		curGeometry.isTextured = false;
		graphicsGLUtils.DrawJTSGeometry(curGeometry);
	}

/////////////////Clean method /////////////////////////

	/**
	 * Call every new iteration when updateDisplay() is called
	 */
	public void CleanGeometries() {
		// FIXME : check that display list is used.
		graphicsGLUtils.DeleteDisplayLists(this.myJTSGeometries.size());
		this.myJTSGeometries.clear();
	}

	/**
	 *  Call every new iteration when updateDisplay() is called
	 *  Remove only the texture that has to be redrawn.
	 *  Keep all the texture coming form a file.
	 *  FIXME: Only work for png and jpg/jpeg file.
	 */
	public void CleanImages() {
		this.myImages.clear();
		Iterator<MyTexture> it = this.myGLRender.myTextures.iterator();
		while (it.hasNext()) {
			MyTexture curtexture = it.next();
			// If the texture is coming from a file keep it
			if ( curtexture.ImageName.indexOf(".png") != -1 ||
				curtexture.ImageName.indexOf(".jpg") != -1 ||
				curtexture.ImageName.indexOf(".jpeg") != -1 ) {

			}// Else remove to recreate a new texture (e.g for GridDisplay).
			else {
				it.remove();
			}
		}
	}

	/**
	 * Call every new iteration when updateDisplay() is called
	 */
	public void CleanStrings() {
		this.myStrings.clear();

	}

	public void draw(final GL gl) {

		if ( listID == -1 ) {
			createDisplayList(gl);
		}
		gl.glCallList(listID);

	}

	private void createDisplayList(final GL gl) {
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
	

	@Override
	public int[] getHighlightColor() {
		return null;
	}

	@Override
	public void setHighlightColor(final int[] rgb) {}

	@Override
	public void highlight(final Rectangle2D r) {}

	
	/**
	 * Each new step the Z value of the first layer is set to 0.
	 */
	@Override
	public void initLayers() {
		currentZvalue=0.0f;
	}

	/**
	 * Set the value z of the current Layer. If no value is define is defined set it to 0.
	 */
	@Override
	public void newLayer(double elevation) {
		currentZvalue=(float) (maxEnvDim*elevation);
	}

}
