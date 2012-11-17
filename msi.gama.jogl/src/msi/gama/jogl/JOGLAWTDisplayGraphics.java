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

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.jogl.utils.*;
import msi.gama.jogl.utils.GraphicDataType.MyCollection;
import msi.gama.jogl.utils.GraphicDataType.MyImage;
import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;
import msi.gama.jogl.utils.GraphicDataType.MyString;
import msi.gama.jogl.utils.GraphicDataType.MyTexture;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.types.GamaGeometryType;

import org.geotools.data.simple.SimpleFeatureCollection;
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
	public float currentAlpha = 1;
	private int displayWidth, displayHeight, curWidth = 5,
			curHeight = 5, offsetX = 0, offsetY = 0;
	private double curX = 0, curY = 0;
	

	// OpenGL member
	private final GL myGl;
	private final GLU myGlu;
	private final GLUT glut; 

	// Handle opengl primitive.
	public MyGraphics graphicsGLUtils;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	// List of all the dynamic  JTS geometry.
	public ArrayList<MyJTSGeometry> myJTSGeometries = new ArrayList<MyJTSGeometry>();
	
	// List of all the static JTS geometry.
	public ArrayList<MyJTSGeometry> myJTSStaticGeometries = new ArrayList<MyJTSGeometry>();

	// each Image is stored in a list
	public ArrayList<MyImage> myImages = new ArrayList<MyImage>();
	
	// each Collection is stored in a list
	public ArrayList<MyCollection> myCollections = new ArrayList<MyCollection>();

	// List of all the String
	public ArrayList<MyString> myStrings = new ArrayList<MyString>();

	// Define the environment properties.
	public float envWidth, envHeight, maxEnvDim;

	// All the geometry of the same layer are drawn in the same z plan.
	public float currentZLayer = 0.0f;
	
	//Is the layer static data or dynamic geometry that has to be redrawn every iteration
	public boolean currentLayerIsStatic= false;
	
	public int currentLayerId = 0;

	// OpenGL list ID
	private int listID = -1;
	public boolean isListCreated = false;
	
	public boolean isStaticListCreated = false;
	
	public boolean isListShapeCreated = false;
		
	public boolean useDisplayList=false;
	
	private boolean drawCollectionAsList=false;
	

	// use to do the triangulation only once per timestep.
	public boolean isPolygonTriangulated = false;

	public boolean useVertexArray=false;
	// FIXME: This need to be remove. Only here to return the bounds of a
	// geometry.
	private final PointTransformation pt = new PointTransformation() {

		@Override
		public void transform(final Coordinate c, final Point2D p) {

		}
	};
	private final ShapeWriter sw = new ShapeWriter(pt);
	
	//Picked (to trigg when a new object has been picked)
	 int currentPicked = -1;
     public int pickedObjectIndex = -1;
     
	

	/**
	 * The environment property are given from the display surface.
	 * 
	 * @param GL
	 *            gl
	 * @param GLU
	 *            glu
	 * @param float env_width
	 * @param float env_height
	 */
	public JOGLAWTDisplayGraphics(final GL gl, final GLU glu,
			final JOGLAWTGLRenderer gLRender, final float env_width,
			final float env_height) {

		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		graphicsGLUtils = new MyGraphics(myGl, myGlu, myGLRender);
		
		glut = new GLUT();

		// Initialize the current environment data.
		envWidth = env_width;
		envHeight = env_height;

		if (envWidth > envHeight) {
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
		if (IntervalSize.isZeroWidth(alpha, currentAlpha)) {
			return;
		}
		currentAlpha = (float) alpha;
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
		curX =  x + offsetX;
		curY =  y + offsetY;
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
	 * @param z float            
	 */
	@Override
	public Rectangle2D drawGeometry(final IScope scope,final Geometry geometry, final Color color,
			final boolean fill, final Color border, final Integer angle) {
		// Check if the geometry has a height value (3D Shape or Volume)

		System.out.println("drawGeometry" + geometry.getGeometryType());
		GamaPoint offSet = new GamaPoint(offsetX,offsetY);

		if (geometry.getUserData() != null) {
			float height = new Float(geometry.getUserData().toString());
			this.AddJTSGeometryInJTSGeometries(geometry,scope.getAgentScope().getAgent(), currentZLayer, currentLayerId, color,
					fill, border, false, angle, height,offSet);
		} else {
			this.AddJTSGeometryInJTSGeometries(geometry,scope.getAgentScope().getAgent(), currentZLayer, currentLayerId, color,
					fill, border, false, angle, 0,offSet);
		}
		// FIXME: Need to remove the use of sw.
		return sw.toShape(geometry).getBounds2D();
	}

	@Override
	public void drawGrid(final BufferedImage image, final Color lineColor,
			final java.awt.Point point) {
		GamaPoint offSet = new GamaPoint(offsetX,offsetY);
		double stepX, stepY;
		for (int i = 0; i <= image.getWidth(); i++) {
			stepX = i / (double) image.getWidth() * image.getWidth();
			Geometry g = GamaGeometryType.buildLine(new GamaPoint(stepX, 0),
					new GamaPoint(stepX, image.getWidth())).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g,null, currentZLayer,currentLayerId, lineColor,
					true, null, false, 0, 0,offSet);
		}

		for (int i = 0; i <= image.getHeight(); i++) {
			stepY = (i / (double) image.getHeight()) * image.getHeight();
			;
			Geometry g = GamaGeometryType.buildLine(new GamaPoint(0, stepY),
					new GamaPoint(image.getHeight(), stepY)).getInnerGeometry();
			this.AddJTSGeometryInJTSGeometries(g,null, currentZLayer,currentLayerId, lineColor,
					true, null, false, 0, 0,offSet);
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
	public Rectangle2D drawImage(final IScope scope, final BufferedImage img, final Integer angle,
			final boolean smooth, final String name,final float z) {

		/*
		 * FIXME Dirty way to check that img represent the environment.
		 * 
		 * if(image represent the environment){ drawImage with the size of the
		 * envirnoment } else{ drawImage with the size of the agent (curWidth
		 * and curHeight) }
		 * 
		 * WARNING1: problem if an agent reach the 0,0 position it will be
		 * displayed wit the size of the enviroment WARNING2: if the environment
		 * is represented with a png and is not located at 0,0 (e.g boids
		 * environment is sky.png) how to know if this image is the enviroment?
		 */

		// System.out.println("drawImage" + "curX" + curX + "curY" +curY
		// +"img.getWidth()"+img.getWidth()+"img.getHeight()"+img.getHeight() +
		// name);

		GamaPoint offSet = new GamaPoint(offsetX,offsetY,currentZLayer);
		
		if (curX == 0
				&& curY == 0
				|| (name.equals("GridDisplay") == true || name
						.equals("QuadTreeDisplay"))) {
			AddImageInImages(img, null, curX, curY, z, this.envWidth,
					this.envHeight, name, angle,offSet);
			rect.setRect(curX, curY, img.getWidth(), img.getHeight());
		} else {
			AddImageInImages(img, scope.getAgentScope(), curX, curY, z, curWidth,
					curHeight, name, angle,offSet);
			rect.setRect(curX, curY, curWidth, curHeight);
		}

		return rect.getBounds2D();
	}

	@Override
	public Rectangle2D drawImage(final IScope scope,final BufferedImage img, final Integer angle,
			final String name,final float z) {
		return drawImage(scope, img, angle, true, name,z);
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
	public Rectangle2D drawCircle(final IScope scope, final Color c, final boolean fill,
			final Color border, final Integer angle,final float height) {
		GamaPoint offSet = new GamaPoint(offsetX,offsetY);

		Geometry g = GamaGeometryType.buildCircle((double) curWidth / 2,
				new GamaPoint(curX + (double) curWidth / 2, curY
						+ (double) curWidth / 2)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, scope.getAgentScope(), currentZLayer,currentLayerId, c, fill, border, false, 0,
				height,offSet);
		oval.setFrame(curX, curY, curWidth, curWidth);
		return oval.getBounds2D();
	}

	@Override
	public Rectangle2D drawTriangle(final IScope scope, final Color c, final boolean fill,
			final Color border, final Integer angle, final float height) {
		GamaPoint offSet = new GamaPoint(offsetX,offsetY);
		// FIXME: check if size is curWidth or curWidth/2
		Geometry g = GamaGeometryType.buildTriangle(curWidth,
				new GamaPoint(curX, curY)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g, scope.getAgentScope(),currentZLayer,currentLayerId, c, fill, border, false,
				angle, height,offSet);
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
		GamaPoint offSet = new GamaPoint(offsetX,offsetY);
		Geometry g = GamaGeometryType.buildLine(new GamaPoint(curX, curY),
				new GamaPoint(toX, toY)).getInnerGeometry();
		this.AddJTSGeometryInJTSGeometries(g,null,currentZLayer, currentLayerId, c, true, null, false, 0,
				0,offSet);
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
	 * @param height
	 *            height of the rectangle if using opengl and defining a z value
	 *             (e.g: draw shape: square  size:2 color: global_color z:2;)
	 */
	@Override
	public Rectangle2D drawRectangle(final IScope scope, final Color c, final boolean fill,
			final Color border, final Integer angle, final float height) {
		
		GamaPoint offSet = new GamaPoint(offsetX,offsetY);
		Geometry g = GamaGeometryType.buildRectangle(curWidth, curHeight,
				new GamaPoint(curX, curY)).getInnerGeometry();

		this.AddJTSGeometryInJTSGeometries(g,scope.getAgentScope(),  currentZLayer,currentLayerId, c, fill, border, false,
				angle, height,offSet);

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
			final Integer angle,final float z) {
		AddStringInStrings(string, (float)curX, -(float)curY, z);
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
	 * Add geometry and its associated parameter in the list of JTSGeometry that
	 * are drawn by Opengl
	 * 
	 * @param geometry
	 * @param color
	 * @param fill
	 * @param isTextured
	 * @param angle
	 * @param height
	 */
	private void AddJTSGeometryInJTSGeometries(final Geometry geometry,final IAgent agent,
			final float z_layer, final int currentLayerId,final Color color, final boolean fill, final Color border, 
			final boolean isTextured, final Integer angle, final float height, GamaPoint offSet) {
		MyJTSGeometry curJTSGeometry;
		if(angle!=null){
		  curJTSGeometry  = new MyJTSGeometry(geometry,agent,z_layer,currentLayerId,color,this.currentAlpha,fill,border,isTextured,angle,height,offSet);
		}
		else{
	      curJTSGeometry = new MyJTSGeometry(geometry,agent,z_layer,currentLayerId,color,this.currentAlpha,fill,border,isTextured,0,height,offSet);	
		}
		
		//Add the geometry either in the static list or in the dynamic one.
		if(currentLayerIsStatic == true){
			//only once (if isStaticListCreated =false)	
			if(this.isStaticListCreated == false){
				this.myJTSStaticGeometries.add(curJTSGeometry);
			}
		}
		else{
		this.myJTSGeometries.add(curJTSGeometry);
		}
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
	private void AddImageInImages(final BufferedImage img, final IAgent agent, final double curX,
			final double curY, final float z, final float widthInModel,
			final float heightInModel, final String name, final Integer angle, final GamaPoint offSet) {

		final MyImage curImage = new MyImage();

		curImage.image = img;
		curImage.agent = agent;
		curImage.x = (float)curX;
		curImage.y = (float)curY;

		if (String.valueOf(z).equals("NaN") == true) {
		  curImage.z = 0;
		}
		else{
	      curImage.z = z;
		}
		curImage.alpha = this.currentAlpha;
		curImage.width = widthInModel;
		curImage.height = heightInModel;
		curImage.offSet = offSet;
		if (angle == null) {
			curImage.angle = 0;
		} else {
			curImage.angle = angle;
		}

		curImage.name = name;

		// For grid display and quadtree display the image is recomputed every
		// iteration
		if (curImage.name.equals("GridDisplay") == true
				|| curImage.name.equals("QuadTreeDisplay")) {
			myGLRender.InitTexture(img, name);
		} else {// For texture coming from a file there is no need to redraw it.
			if (!IsTextureExist(name)) {
				myGLRender.InitTexture(img, name);
			}
		}
		this.myImages.add(curImage);

	}

	/**
	 * Add collection and its associated parameter in the list of Image that are
	 * drawn by Opengl
	 * 
	 * @param collection
	 * @param color
	 */
	public void AddCollectionInCollections(final SimpleFeatureCollection collection, final Color color) {

		final MyCollection curCol = new MyCollection();

		curCol.collection = collection;
		curCol.color = color;

		this.myCollections.add(curCol);
	}
	
	/**
	 * Check that the texture "name" has not already be created.
	 * 
	 * @param name
	 * @return
	 */
	private boolean IsTextureExist(final String name) {

		Iterator<MyTexture> it = myGLRender.myTextures.iterator();
		while (it.hasNext()) {
			MyTexture curTexture = it.next();
			if (name.equals(curTexture.ImageName) == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add string and its postion in the list of String that are drawn by Opengl
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param z
	 */
	private void AddStringInStrings(final String string, final float x,
			final float y, final float z) {

		final MyString curString = new MyString();
		curString.string = string;
		curString.x = x;
		curString.y = y;
		curString.z = z;
		this.myStrings.add(curString);

	}

	// /////////////// Draw Method ////////////////////

	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * @param picking
	 * 
	 */
	public void DrawMyJTSGeometries(boolean picking) {

		if(picking){
			myGl.glPushMatrix();
			myGl.glInitNames();
			myGl.glPushName(0);
			int i=0;
			Iterator<MyJTSGeometry> it = this.myJTSGeometries.iterator();
			while (it.hasNext()) {
				myGl.glPushMatrix();
        		myGl.glLoadName(i);  		
				MyJTSGeometry curGeometry = it.next();
				
				if(pickedObjectIndex ==i){
					MyJTSGeometry pickedGeometry = (MyJTSGeometry) curGeometry.clone();	
					pickedGeometry.color=Color.red;
					if(pickedGeometry.agent != null){
					}
					else{
						throw new GamaRuntimeException("Picking null agent" );	
					}
					
					if(pickedGeometry.agent != null && currentPicked !=  i){
					    	myGLRender.displaySurface.selectAgents(0,0,pickedGeometry.agent,pickedGeometry.layerId-1);
					    currentPicked=i;
					}
					
					
					graphicsGLUtils.basicDrawer.DrawJTSGeometry(pickedGeometry);
        		}
				else{
					graphicsGLUtils.basicDrawer.DrawJTSGeometry(curGeometry);	
				}
				
				myGl.glPopMatrix();
				i++;
			}
			myGl.glPopName();
			myGl.glPopMatrix();
		}
		else{
			// System.out.println("isListCreated="+isListCreated);
			if (useDisplayList) {
				//System.out.println("Geometries are build with displayList");
				if (!isListCreated) {
					System.out.println("Create" + this.myJTSGeometries.size()
							+ "list");
					graphicsGLUtils.displayListHandler.buildDisplayLists(this.myJTSGeometries);
					System.out.println("Create" + this.myJTSGeometries.size()
							+ "list ok");
					isListCreated = true;
				} else {
					//System.out.println("Call" + this.myJTSGeometries.size() +
					 //"list");
					graphicsGLUtils.displayListHandler.DrawDisplayList(this.myJTSGeometries.size());
				}
			} else {
	 
				if (!useVertexArray) {
					//System.out.println(this.myJTSGeometries.size() + " geometries are build with basicDrawer ");
					Iterator<MyJTSGeometry> it = this.myJTSGeometries.iterator();
					while (it.hasNext()) {
						MyJTSGeometry curGeometry = it.next();
						graphicsGLUtils.basicDrawer.DrawJTSGeometry(curGeometry);
					}
				}
				// use vertex array
				else {
					//triangulate all the geometries
					if (!isPolygonTriangulated) {
						graphicsGLUtils.vertexArrayHandler.buildVertexArray(this.myJTSGeometries);
						isPolygonTriangulated = true;
					} else {
						graphicsGLUtils.vertexArrayHandler.drawVertexArray();
					}
				}
			}
		}

	}
	
	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * 
	 */
	public void DrawMyJTSStaticGeometries(boolean picking) {
		if (picking){
			//TODO
		}
		else{
			if (!isStaticListCreated) {
				System.out.println("Create" + this.myJTSStaticGeometries.size()
						+ "list static");
				graphicsGLUtils.displayListHandler.buildDisplayLists(this.myJTSStaticGeometries);
				isStaticListCreated = true;
				System.out.println("Create" + this.myJTSStaticGeometries.size()
						+ "list static ok");
			} else {
				graphicsGLUtils.displayListHandler.DrawDisplayList(this.myJTSStaticGeometries.size());
			}
		}
	}

	/**
	 * Once the list of Images has been created, OpenGL display call this method
	 * every framerate. FIXME: Need to be optimize with the use of Vertex Array
	 * or even VBO
	 * 
	 */
	public void DrawMyImages(boolean picking) {

	    if(picking){
	    	
			myGl.glPushMatrix();
			myGl.glInitNames();
			myGl.glPushName(0);
			int i=0;
									
		    Iterator<MyImage> it = this.myImages.iterator();
			while (it.hasNext()) {
				myGl.glPushMatrix();
        		myGl.glLoadName(i);
        		
        		MyImage curImage = it.next();
				
				if(pickedObjectIndex ==i){
					if(curImage.agent != null){
	
					    myGl.glColor3d(0, 0, 0);
					    myGl.glWindowPos2d(2, 5);
						glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, curImage.agent.getSpeciesName() + curImage.agent.getIndex());
					    currentPicked=i;

					    if(currentPicked !=  i){
					      //Call Agent window inspector
					    }   
					}
					else{
						System.out.println("Picking null agent" );	
					}
					myGLRender.DrawTexture(curImage);
        		}
				else{
					myGLRender.DrawTexture(curImage);	
				}
				
				myGl.glPopMatrix();
				i++;
			}
			myGl.glPopName();
			myGl.glPopMatrix();	
	    	
	    }
	    else{
		boolean drawImageAsList = false;
		if (drawImageAsList) {
			if (!isListCreated) {
				graphicsGLUtils.displayListHandler.buildImageDisplayLists(this.myImages);
				isListCreated = true;
			} else {
				graphicsGLUtils.displayListHandler.DrawImageDisplayList(this.myImages.size());
			}

		} else {
			Iterator<MyImage> it = this.myImages.iterator();
			while (it.hasNext()) {
				MyImage curImage = it.next();
				myGLRender.DrawTexture(curImage);
			}
		}
	    }
	}

	/**
	 * Once the list of String has been created, OpenGL display call this method
	 * every framerate.
	 * 
	 */
	public void DrawMyStrings() {

		Iterator<MyString> it = this.myStrings.iterator();
		while (it.hasNext()) {
			MyString curString = it.next();
			graphicsGLUtils.DrawString(curString.string, curString.x,
					curString.y, curString.z);
		}
	}

	public void DrawEnvironmentBounds(boolean drawData) {
		GamaPoint offSet = new GamaPoint(0,0);
		if(drawData){
		// Draw Width and height value
		this.graphicsGLUtils.DrawString(String.valueOf(this.envWidth),
				this.envWidth / 2, this.envHeight * 0.01f, 0.0f);
		this.graphicsGLUtils.DrawString(String.valueOf(this.envHeight),
				this.envWidth * 1.01f, -(this.envHeight / 2), 0.0f);
		}

		// Draw environment rectangle
		Geometry g = GamaGeometryType.buildRectangle(envWidth, envHeight,
				new GamaPoint(envWidth / 2, envHeight / 2)).getInnerGeometry();

		Color c = new Color(225, 225, 225);
		MyJTSGeometry curGeometry = new MyJTSGeometry(g,null,-0.01f,-1,c,1.0f,true,null,false,0,0.0f,offSet);
		graphicsGLUtils.basicDrawer.DrawJTSGeometry(curGeometry);
	}

	
	
	public void DrawCollection(){
		
		//FIXME : need to be done for a list of collection
		drawCollectionAsList = true;
		if (drawCollectionAsList) {
			if (!isListShapeCreated) {
				graphicsGLUtils.displayListHandler.buildCollectionDisplayLists(myCollections);
				isListShapeCreated = true;
			} else {
				graphicsGLUtils.displayListHandler.DrawCollectionDisplayList(myCollections.size());
			}

		} else {
			
			Iterator<MyCollection> it = this.myCollections.iterator();
			while (it.hasNext()) {
				MyCollection curCol = it.next();
				graphicsGLUtils.basicDrawer.DrawSimpleFeatureCollection(curCol);
			}		
		}
		
	}
	
	// ///////////////Clean method /////////////////////////

	/**
	 * Call  when updateDisplay() is called
	 */
	public void CleanGeometries() {
		if(useDisplayList){
			graphicsGLUtils.displayListHandler.DeleteDisplayLists(this.myJTSGeometries.size());
		}
		if(useVertexArray){
			graphicsGLUtils.vertexArrayHandler.DeleteVertexArray();	
		}
		this.myJTSGeometries.clear();
		isListCreated = false;
		
	}

	/**
	 * Call every new iteration when updateDisplay() is called Remove only the
	 * texture that has to be redrawn. Keep all the texture coming form a file.
	 * FIXME: Only work for png and jpg/jpeg file.
	 */
	public void CleanImages() {
		this.myImages.clear();
		Iterator<MyTexture> it = this.myGLRender.myTextures.iterator();
		while (it.hasNext()) {
			MyTexture curtexture = it.next();
			// If the texture is coming from a file keep it
			if (curtexture.ImageName.indexOf(".png") != -1
					|| curtexture.ImageName.indexOf(".jpg") != -1
					|| curtexture.ImageName.indexOf(".jpeg") != -1) {

			}// Else remove to recreate a new texture (e.g for GridDisplay).
			else {
				it.remove();
			}
		}
	}
	
	public void CleanCollections(){
		if(drawCollectionAsList){
			graphicsGLUtils.displayListHandler.DeleteCollectionDisplayLists(this.myCollections.size());
		}
		this.myCollections.clear();
	}

	/**
	 * Call every new iteration when updateDisplay() is called
	 */
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

	/**
	 * Each new step the Z value of the first layer is set to 0.
	 */
	@Override
	public void initLayers() {
		currentLayerId=0;
		currentZLayer = 0.0f;
	}

	/**
	 * Set the value z of the current Layer. If no value is define is defined
	 * set it to 0.
	 * Set the type of the layer weither it's a static layer (refresh:false) or 
	 * a dynamic layer (by default or refresh:true)
	 */
	@Override
	public void newLayer(double zLayerValue,Boolean refresh) {
		currentZLayer = (float) (maxEnvDim * zLayerValue);

		//If refresh: false -> draw static geometry -> currentLayerIsStatic=true
		if(refresh != null){
		currentLayerIsStatic=!refresh;
		}
		else{
		currentLayerIsStatic=false;
		}
		currentLayerId++;
	}
	
	@Override
	public String getGraphicsType() {	
		return "opengl";
	}

}
