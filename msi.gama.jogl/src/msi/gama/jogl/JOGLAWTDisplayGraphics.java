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

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_TRIANGLES;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.jogl.utils.*;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.operators.Maths;
import msi.gaml.types.GamaGeometryType;

import org.geotools.filter.expression.ThisPropertyAccessorFactory;
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
	private final GL myGl;
	private final GLU myGlu;
	private final GLUT glut;

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
	
	//List of all the String 
	public ArrayList<MyString> myStrings = new ArrayList<MyString>();

	// Define the environment properties.
	public float envWidth, envHeight, maxEnvDim;

	// All the geometry are drawn in the same z plan (depend on the sale rate).
	public float z;

	// OpenGL list ID
	private int listID = -1;
	public boolean isListCreated = false;

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
			final JOGLAWTGLRenderer gLRender, final float env_width,
			final float env_height) {
		myGl = gl;
		myGlu = glu;
		glut = new GLUT();
		envWidth = env_width;
		envHeight = env_height;

		if (envWidth > envHeight) {
			maxEnvDim = envWidth;
		} else {
			maxEnvDim = envHeight;
		}

		z = 0.0f;
		

		myGLRender = gLRender;
		
		graphicsGLUtils = new MyGraphics(myGl, myGlu,myGLRender);
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
		this.AddJTSGeometryInJTSGeometries(geometry, color,fill,false,angle);
		return sw.toShape(geometry).getBounds2D();
	}

	@Override
	public void drawGrid(final BufferedImage image, final Color lineColor,
			final java.awt.Point point) {

		double stepX, stepY;
		for (int i = 0; i <= image.getWidth(); i++) {
			stepX = i / (double) image.getWidth() * image.getWidth();
			Geometry g = GamaGeometryType.buildLine(new GamaPoint(stepX, 0),
					new GamaPoint(stepX, -image.getWidth())).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g, lineColor,true,false,0);
		}

		for (int i = 0; i <= image.getHeight(); i++) {
			stepY = -(i / (double) image.getHeight()) * image.getHeight();
			;
			Geometry g = GamaGeometryType.buildLine(new GamaPoint(0, stepY),
					new GamaPoint(image.getHeight(), stepY)).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g, lineColor,true,false,0);
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

        //FIXME Dirty way to check that img represent the environment. When the image is the enviroment
        //we set the dimensions on the image as the env dimensions.
		//FIXME: We should display the image with the size of the environment, this not the case here if the size of the image is greater than the environment
		//WARNING: problem if an agent reach the 0,0 position...
		//System.out.println("drawImage" + "curX" + curX + "curY" +curY +"img.getWidth()"+img.getWidth()+"img.getHeight()"+img.getHeight());
        if((curX == 0 && curY == 0)  ){
        	//AddImageInImages(img, curX, curY,img.getWidth(),img.getHeight(),angle);
        	AddImageInImages(img, curX, curY,this.envWidth,this.envHeight,angle);
        	rect.setRect(curX, curY, img.getWidth(), img.getHeight());
        }
        else{
        	//FIXME:how to get the real x and y?
        	boolean AddAsImage=true;
        	
        	//If AddAsImage is true the image is drawn as a buffered Image (much more time consuming)
        	//If AddAsImage is false the image is drawn as a texture bended on a rectangle (faster need to load alony once the bufferedImage)
        	if(AddAsImage){
        	AddImageInImages(img, curX, curY,curWidth,curHeight,angle);
        	}
        	else{	
        	Geometry g = GamaGeometryType.buildRectangle(curWidth, curHeight,
    				new GamaPoint(curX, curY)).getInnerGeometry();
        	Color c= new Color(255,0,0);
        	
    		this.AddJTSGeometryInJTSGeometries(g, c,false,true,angle);
        	}
        	
        	rect.setRect(curX, curY, curWidth, curHeight);
        }
        

        
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
				(double) curWidth / 2,
				new GamaPoint(curX + (double) curWidth / 2, curY
						+ (double) curWidth / 2)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, c,fill,false,0);
		oval.setFrame(curX, curY, curWidth, curWidth);
		return oval.getBounds2D();
	}

	@Override
	public Rectangle2D drawTriangle(final Color c, final boolean fill,
			final Integer angle) {
		// FIXME: check if size is curWidth or curWidth/2
		Geometry g = GamaGeometryType.buildTriangle(curWidth,
				new GamaPoint(curX, curY)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, c, fill,false,angle);
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
		this.AddJTSGeometryInJTSGeometries(g, c,true,false,0);
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
		this.AddJTSGeometryInJTSGeometries(g, c,fill,false,angle);
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
        //FIXME String must be drawn from the gl current context, that the reason why using a list of string
		AddStringInStrings(string,curX,-curY,0.0f);
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
	 * @param fill
	 * @param isTextured
	 */
	private void AddJTSGeometryInJTSGeometries(final Geometry geometry,
			final Color color, final boolean fill,final boolean isTextured,final Integer angle) {

		// System.out.println("Add:"+ geometry.getGeometryType());
		MyJTSGeometry curJTSGeometry = new MyJTSGeometry();
		curJTSGeometry.geometry = geometry;
		curJTSGeometry.color = color;
		curJTSGeometry.fill = fill;
		curJTSGeometry.isTextured= isTextured;
		curJTSGeometry.angle=angle;
		this.myJTSGeometries.add(curJTSGeometry);
	}

	private void AddImageInImages(BufferedImage img, final int curX,
			final int curY, float widthInModel, float heightInModel, final Integer angle) {

		final MyImage curImage = new MyImage();
		
		//FIXME. This is really heavy find a way to do it in a faster way.
		if ( angle != null ) {
		AffineTransform tx = new AffineTransform();
	    tx.rotate(Maths.toRad * angle, img.getWidth() / 2, img.getHeight() / 2);
	    //tx.rotate(Maths.toRad * angle, curX + curWidth / 2, curY + curHeight / 2);

    
	    AffineTransformOp op = new AffineTransformOp(tx,
	        AffineTransformOp.TYPE_BILINEAR);
	    img = op.filter(img, null);
		}
		
		curImage.image = img;
		curImage.x = curX;
		curImage.y = curY;
		curImage.width = widthInModel;
		curImage.height = heightInModel;
				
		
		myGLRender.InitTexture(img);
		this.myImages.add(curImage);
	}
	
	private void AddStringInStrings(String string, float x, float y, float z){
		
		final MyString curString = new MyString();
		curString.string= string;
		curString.x=x;
		curString.y=y;
		curString.z=z;
		this.myStrings.add(curString);
		
	}

	// /////////////// Draw Method ////////////////////

	public void DrawMyJTSGeometries() {

		boolean drawAsList = false;

		if (drawAsList) {
			if (!isListCreated) {
				graphicsGLUtils.buildDisplayLists(this.myJTSGeometries);
				isListCreated = true;
			} else {
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

	public void DrawMyImages() {

		boolean drawImageAsList = false;
		if (drawImageAsList){
			if (!isListCreated) {
				graphicsGLUtils.buildImageDisplayLists(this.myImages);
				isListCreated = true;
			} else {
				graphicsGLUtils.DrawImageDisplayList(this.myImages.size());
			}
			
		}
		else {
		Iterator<MyImage> it = this.myImages.iterator();
				
		int id = 0;
		while (it.hasNext()) {
			
			MyImage curImage = it.next();
			myGLRender.DrawTexture(id, curImage);
			id++;
		}
		}
	}
	
	public void DrawMyStrings() {

		Iterator<MyString> it = this.myStrings.iterator();
				
		int id = 0;
		while (it.hasNext()) {
			
			MyString curString = it.next();
			DrawString(curString.string,curString.x,curString.y,0.0f);
			id++;
		}
	}

	public void DrawEnvironmentBounds() {

		// Draw Width and height value		
		DrawString(String.valueOf(this.envWidth),this.envWidth / 2, this.envHeight * 0.01f, 0.0f);
		DrawString(String.valueOf(this.envHeight),this.envWidth * 1.01f, -(this.envHeight / 2), 0.0f);

		// Draw environment rectangle
		Geometry g = GamaGeometryType.buildRectangle(envWidth, envHeight,
				new GamaPoint(envWidth / 2, envHeight / 2)).getInnerGeometry();

		Color c = new Color(0, 0, 255);
		MyJTSGeometry curGeometry = new MyJTSGeometry();
		curGeometry.geometry = g;
		curGeometry.color =c;
		curGeometry.fill=true;
		curGeometry.isTextured=false;
		graphicsGLUtils.DrawJTSGeometry(curGeometry);
	}

	public void DrawXYZAxis(final float size) {
	
	
		myGl.glColor4f(0.0f, 0.0f, 0.0f,1.0f);		
		DrawString("1:" + String.valueOf(size),size,size,0.0f);
		// X Axis
		DrawString("x",1.2f*size,0.0f,0.0f);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4f(1.0f, 0, 0,1.0f);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(size, 0, 0);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(1.0f * size, 0.05f * size, 0.0f);
		myGl.glVertex3f(1.0f * size, -0.05f * size, 0.0f);
		myGl.glVertex3f(1.1f * size, 0.0f, 0.0f);
		myGl.glEnd();

		// Y Axis
		DrawString("y",0.0f,1.2f*size,0.0f);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4f(0, 1.0f, 0,1.0f);
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
		DrawString("z",0.0f, 0.0f, 1.2f * size);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4f(0, 0, 1.0f,1.0f);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(0, 0, size);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(0.0f, 0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, -0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, 0.0f, 1.1f * size);
		myGl.glEnd();
		

	
	}

	public void DrawZValue(final float pos, final float value) {
		DrawString("z:" + String.valueOf(value),pos,pos,0.0f);
	}
	
	public void DrawString(String string, float x, float y,float z){
		
		//Need to disable blending when drawing glutBitmapString;
		myGl.glDisable(GL_BLEND);
		myGl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		myGl.glRasterPos3f(x, y, z);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,string);
		//myGl.glEnable(GL_BLEND);
		
		
		//glut.glutStrokeString(GLUT.STROKE_ROMAN,string);
		
	}

	public void DrawScale() {

		// Draw Scale
		float y_text_pos = -(this.envHeight + this.envHeight * 0.05f);
		DrawString("Scale:" + "1",0.0f, y_text_pos * 0.99f, 0.0f);
		myGl.glBegin(GL.GL_LINES);
		myGl.glVertex3f(0, 0 + y_text_pos, 0);
		myGl.glVertex3f(this.envWidth * 0.05f, 0 + y_text_pos, 0);

		myGl.glVertex3f(0, -0.05f + y_text_pos, 0);
		myGl.glVertex3f(0, 0.05f + y_text_pos, 0);

		myGl.glVertex3f(1, -0.05f + y_text_pos, 0);
		myGl.glVertex3f(1, 0.05f + y_text_pos, 0);
		myGl.glEnd();
	}

	public void CleanGeometries() {
		//FIXME : check that display list is used.
		//graphicsGLUtils.DeleteDisplayLists(this.myJTSGeometries.size());
		this.myGeometries.clear();
		this.myJTSGeometries.clear();
	}

	public void CleanImages() {
		this.myImages.clear();
		myGLRender.myTextures.clear();
	}
	
	public void CleanStrings() {
		this.myStrings.clear();
	
	}
	


	public void draw(final GL gl) {

		if (listID == -1) {
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
	public void setHighlightColor(final int[] rgb) {
	}

	@Override
	public void highlight(final Rectangle2D r) {
	}

}
