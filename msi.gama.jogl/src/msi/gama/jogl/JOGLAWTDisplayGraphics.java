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
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.GraphicDataType.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaFile;
import msi.gaml.types.GamaGeometryType;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.jfree.chart.JFreeChart;
import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Geometry;
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

public class JOGLAWTDisplayGraphics implements IGraphics.OpenGL {

	boolean ready = false;
	private Rectangle clipping;
	private final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	private final Ellipse2D oval = new Ellipse2D.Double(0, 0, 1, 1);
	private final Line2D line = new Line2D.Double();
	private float currentAlpha = 1;
	private int displayWidth, displayHeight, curWidth = 5, curHeight = 5, offsetX = 0, offsetY = 0;
	private double curX = 0, curY = 0;
	// GLRenderer.
	private final JOGLAWTGLRenderer GLRender;
	// List of all the dynamic JTS geometry.
	private java.util.List<MyJTSGeometry> geometries = new ArrayList<MyJTSGeometry>();
	// List of all the static JTS geometry.
	private java.util.List<MyJTSGeometry> staticGeometries = new ArrayList<MyJTSGeometry>();
	// each Image is stored in a list
	private java.util.List<MyImage> images = new ArrayList<MyImage>();
	// each Collection is stored in a list
	private java.util.List<MyCollection> collections = new ArrayList<MyCollection>();
	// List of all the String
	private java.util.List<MyString> strings = new ArrayList<MyString>();
	// Environment properties.
	private float envWidth;
	private float envHeight;
	private float maxEnvDim;
	// All the geometry of the same layer are drawn in the same z plan.
	private float currentZLayer = 0.0f;
	private int currentLayerId = 0;
	// Is the layer static data or dynamic geometry that has to be redrawn every iteration
	private boolean currentLayerIsStatic = false;
	// OpenGL list ID
	private int listID = -1;
	private boolean isListCreated = false;
	private boolean isStaticListCreated = false;
	private boolean isListShapeCreated = false;
	private boolean useDisplayList = false;
	private boolean drawCollectionAsList = false;
	// use to do the triangulation only once per timestep.
	private boolean isPolygonTriangulated = false;
	private boolean useVertexArray = false;
	private final ShapeWriter sw = new ShapeWriter();
	// Picked (to trigg when a new object has been picked)
	private int currentPicked = -1;
	private int pickedObjectIndex = -1;

	/**
	 * @param JOGLAWTDisplaySurface displaySurface
	 */
	public JOGLAWTDisplayGraphics(final JOGLAWTDisplaySurface displaySurface) {

		// Initialize the current environment data.
		setEnvWidth(displaySurface.envWidth);
		setEnvHeight(displaySurface.envHeight);

		if ( getEnvWidth() > getEnvHeight() ) {
			setMaxEnvDim(getEnvWidth());
		} else {
			setMaxEnvDim(getEnvHeight());
		}

		GLRender = new JOGLAWTGLRenderer(displaySurface);
		// TODO Verify this (too soon ?)
		getMyGLRender().animator.start();
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
		if ( IntervalSize.isZeroWidth(alpha, getCurrentAlpha()) ) { return; }
		setCurrentAlpha((float) alpha);
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
		curX = x + offsetX;
		curY = y + offsetY;
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
	public Rectangle2D drawGeometry(final IScope scope, final Geometry geometry, final Color color, final boolean fill,
		final Color border, final Integer angle, final boolean rounded) {

		// Check if the geometry has a height value (3D Shape or Volume)
		Geometry geom = null;
		ITopology topo = scope.getTopology();
		if ( topo != null && topo.isTorus() ) {
			geom = topo.returnToroidalGeom(geometry);
		} else {
			geom = geometry;
		}

		GamaPoint offSet = new GamaPoint(offsetX, offsetY);

		// FIXME : Here should check the value of the Property3D of the GamaShape
		if ( geom.getUserData() != null ) {
			float height = new Float(geom.getUserData().toString());
			this.addJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(), getCurrentZLayer(),
				getCurrentLayerId(), color, fill, border, false, angle, height, offSet, rounded, "JTS");
		} else {
			this.addJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(), getCurrentZLayer(),
				getCurrentLayerId(), color, fill, border, false, angle, 0, offSet, rounded, "JTS");
		}
		// FIXME: Need to remove the use of sw.
		return sw.toShape(geom).getBounds2D();
	}

	/**
	 * Method drawGeometry. Add a given JTS Geometry in the list of all the
	 * existing geometry that will be displayed by openGl.
	 * 
	 * @param geometry
	 *            GamaShape
	 * @param color
	 *            Color
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 * @param z float
	 */
	@Override
	public Rectangle2D drawGamaShape(IScope scope, GamaShape geometry, Color color, boolean fill, Color border,
		Integer angle, boolean rounded) {

		// Check if the geometry has a height value (3D Shape or Volume)
		Geometry geom = null;
		if ( geometry == null ) { return null; }
		ITopology topo = scope.getTopology();
		if ( topo != null && topo.isTorus() ) {
			geom = topo.returnToroidalGeom(geometry.getInnerGeometry());
		} else {
			geom = geometry.getInnerGeometry();
		}

		GamaPoint offSet = new GamaPoint(offsetX, offsetY);

		// Add a geometry with a depth and type coming from Attributes
		if ( geometry.getAttribute("depth") != null && geometry.getAttribute("type") != null ) {
			Double depth = (Double) geometry.getAttribute("depth");
			String type = (String) geometry.getAttribute("type");
			this.addJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(), getCurrentZLayer(),
				getCurrentLayerId(), color, fill, border, false, angle, depth.floatValue(), offSet, rounded,
				type.toString());
		}

		else {
			// Add a geometry with a depth and type coming from getUSerData (with add_z operator)
			if ( geometry.getInnerGeometry().getUserData() != null ) {
				float height = new Float(geom.getUserData().toString());
				this.addJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(), getCurrentZLayer(),
					getCurrentLayerId(), color, fill, border, false, angle, height, offSet, rounded, "JTS");
			} else {
				// add a 2D geometry without any 3D data.
				this.addJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(), getCurrentZLayer(),
					getCurrentLayerId(), color, fill, border, false, angle, 0, offSet, rounded, "none");
			}

		}

		// FIXME : Here should check the value of the Property3D of the GamaShape
		/*
		 * if ( geom.getUserData() != null ) {
		 * float height = new Float(geom.getUserData().toString());
		 * this.AddJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(),
		 * currentZLayer, currentLayerId, color, fill, border, false, angle, height,
		 * offSet,rounded);
		 * } else {
		 * this.AddJTSGeometryInJTSGeometries(geom, scope.getAgentScope().getAgent(),
		 * currentZLayer, currentLayerId, color, fill, border, false, angle, 0, offSet,rounded);
		 * }
		 */
		// FIXME: Need to remove the use of sw.
		return sw.toShape(geom).getBounds2D();
	}

	@Override
	public void drawGrid(final BufferedImage image, final Color lineColor, final java.awt.Point point) {
		GamaPoint offSet = new GamaPoint(offsetX, offsetY);
		double stepX, stepY;
		for ( int i = 0; i <= image.getWidth(); i++ ) {
			stepX = i / (double) image.getWidth() * image.getWidth();
			Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(stepX, 0), new GamaPoint(stepX, image.getWidth()))
					.getInnerGeometry();
			this.addJTSGeometryInJTSGeometries(g, null, getCurrentZLayer(), getCurrentLayerId(), lineColor, true, null,
				false, 0, 0, offSet, false, "grid");
		}

		for ( int i = 0; i <= image.getHeight(); i++ ) {
			stepY = i / (double) image.getHeight() * image.getHeight();;
			Geometry g =
				GamaGeometryType.buildLine(new GamaPoint(0, stepY), new GamaPoint(image.getHeight(), stepY))
					.getInnerGeometry();
			this.addJTSGeometryInJTSGeometries(g, null, getCurrentZLayer(), getCurrentLayerId(), lineColor, true, null,
				false, 0, 0, offSet, false, "grid");
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
		final boolean smooth, final String name, final float z) {

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

		GamaPoint offSet = new GamaPoint(offsetX, offsetY, getCurrentZLayer());

		if ( curX == 0 && curY == 0 || name.equals("GridDisplay") == true || name.equals("QuadTreeDisplay") ) {
			addImageInImages(img, null, curX, curY, z, this.getEnvWidth(), this.getEnvHeight(), name, angle, offSet);
			rect.setRect(curX, curY, img.getWidth(), img.getHeight());
		} else {
			if ( scope != null ) {
				addImageInImages(img, scope.getAgentScope(), curX, curY, z, curWidth, curHeight, name, angle, offSet);
			} else {
				addImageInImages(img, null, curX, curY, z, curWidth, curHeight, name, angle, offSet);
			}

			rect.setRect(curX, curY, curWidth, curHeight);
		}

		return rect.getBounds2D();
	}

	@Override
	public Rectangle2D drawImage(final IScope scope, final BufferedImage img, final Integer angle, final String name,
		final float z) {
		return drawImage(scope, img, angle, true, name, z);
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
	public Rectangle2D drawCircle(final IScope scope, final Color c, final boolean fill, final Color border,
		final Integer angle, final float height) {
		GamaPoint offSet = new GamaPoint(offsetX, offsetY);

		// Geometry g = GamaGeometryType.buildCircle((double) curWidth / 2, new GamaPoint(curX +
		// (double) curWidth / 2, curY+ (double) curWidth / 2)).getInnerGeometry();
		Geometry g = GamaGeometryType.buildCircle((double) curWidth / 2, new GamaPoint(curX, curY)).getInnerGeometry();

		this.addJTSGeometryInJTSGeometries(g, scope.getAgentScope(), getCurrentZLayer(), getCurrentLayerId(), c, fill,
			border, false, 0, height, offSet, false, "shape");
		oval.setFrame(curX, curY, curWidth, curWidth);
		return oval.getBounds2D();
	}

	@Override
	public Rectangle2D drawTriangle(final IScope scope, final Color c, final boolean fill, final Color border,
		final Integer angle, final float height) {
		GamaPoint offSet = new GamaPoint(offsetX, offsetY);
		// FIXME: check if size is curWidth or curWidth/2
		Geometry g = GamaGeometryType.buildTriangle(curWidth, new GamaPoint(curX, curY)).getInnerGeometry();
		this.addJTSGeometryInJTSGeometries(g, scope.getAgentScope(), getCurrentZLayer(), getCurrentLayerId(), c, fill,
			border, false, angle, height, offSet, false, "shape");
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
		GamaPoint offSet = new GamaPoint(offsetX, offsetY);
		Geometry g = GamaGeometryType.buildLine(new GamaPoint(curX, curY), new GamaPoint(toX, toY)).getInnerGeometry();
		this.addJTSGeometryInJTSGeometries(g, null, getCurrentZLayer(), getCurrentLayerId(), c, true, null, false, 0,
			0, offSet, false, "shape");
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
	 *            (e.g: draw shape: square size:2 color: global_color z:2;)
	 */
	@Override
	public Rectangle2D drawRectangle(final IScope scope, final Color c, final boolean fill, final Color border,
		final Integer angle, final float height) {

		GamaPoint offSet = new GamaPoint(offsetX, offsetY);

		Geometry g = GamaGeometryType.buildRectangle(curWidth, curHeight, new GamaPoint(curX, curY)).getInnerGeometry();

		this.addJTSGeometryInJTSGeometries(g, scope.getAgentScope(), getCurrentZLayer(), getCurrentLayerId(), c, fill,
			border, false, angle, height, offSet, false, "shape");

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
	public Rectangle2D drawString(final IAgent agent, final String string, final Color stringColor,
		final Integer angle, final float z) {
		// Draw the text at the centroid of the Agent
		if ( agent != null ) {
			addStringInStrings(string, (float) agent.getGeometry().getLocation().getX(), -(float) agent.getGeometry()
				.getLocation().getY(), z);
		} else {
			addStringInStrings(string, (float) curX, -(float) curY, z);
		}
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
	private void addJTSGeometryInJTSGeometries(final Geometry geometry, final IAgent agent, final float z_layer,
		final int currentLayerId, final Color color, final boolean fill, final Color border, final boolean isTextured,
		final Integer angle, final float height, final GamaPoint offSet, final boolean roundCorner, final String type) {
		MyJTSGeometry curJTSGeometry;
		if ( angle != null ) {
			curJTSGeometry =
				new MyJTSGeometry(geometry, agent, z_layer, currentLayerId, color, this.getCurrentAlpha(), fill,
					border, isTextured, angle, height, offSet, roundCorner, type);
		} else {
			curJTSGeometry =
				new MyJTSGeometry(geometry, agent, z_layer, currentLayerId, color, this.getCurrentAlpha(), fill,
					border, isTextured, 0, height, offSet, roundCorner, type);
		}

		// Add the geometry either in the static list or in the dynamic one.
		if ( isCurrentLayerIsStatic() == true ) {
			// only once (if isStaticListCreated =false)
			if ( this.isStaticListCreated() == false ) {
				this.getMyJTSStaticGeometries().add(curJTSGeometry);
			}
		} else {
			this.getJTSGeometries().add(curJTSGeometry);
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
	private void addImageInImages(final BufferedImage img, final IAgent agent, final double curX, final double curY,
		final float z, final float widthInModel, final float heightInModel, final String name, final Integer angle,
		final GamaPoint offSet) {

		final MyImage curImage = new MyImage();

		curImage.image = img;
		curImage.agent = agent;
		curImage.x = (float) curX;
		curImage.y = (float) curY;

		if ( Double.isNaN(z) == true ) {
			curImage.z = 0;
		} else {
			curImage.z = z;
		}
		curImage.alpha = this.getCurrentAlpha();
		curImage.width = widthInModel;
		curImage.height = heightInModel;
		curImage.offSet = offSet;
		if ( angle == null ) {
			curImage.angle = 0;
		} else {
			curImage.angle = angle;
		}

		curImage.name = name;
		// For grid display and quadtree display the image is recomputed every
		// iteration
		if ( curImage.name.equals("GridDisplay") == true || curImage.name.equals("QuadTreeDisplay") ) {
			getMyGLRender().InitTexture(img, name);
		} else {// For texture coming from a file there is no need to redraw it.
			if ( !isTextureExist(name) ) {
				getMyGLRender().InitTexture(img, name);
			}
		}
		this.getImages().add(curImage);

	}

	/**
	 * Add collection and its associated parameter in the list of Image that are
	 * drawn by Opengl
	 * 
	 * @param collection
	 * @param color
	 */
	@Override
	public void addCollectionInCollections(final SimpleFeatureCollection collection, final Color color) {

		final MyCollection curCol = new MyCollection();

		curCol.collection = collection;
		curCol.color = color;

		this.getCollections().add(curCol);
	}

	/**
	 * Check that the texture "name" has not already be created.
	 * 
	 * @param name
	 * @return
	 */
	private boolean isTextureExist(final String name) {

		Iterator<MyTexture> it = getMyGLRender().myTextures.iterator();
		while (it.hasNext()) {
			MyTexture curTexture = it.next();
			if ( name.equals(curTexture.ImageName) == true ) { return true; }
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
	private void addStringInStrings(final String string, final float x, final float y, final float z) {

		final MyString curString = new MyString();
		curString.string = string;
		curString.x = x;
		curString.y = y;
		curString.z = z;
		this.getStrings().add(curString);

	}

	// /////////////// Draw Method ////////////////////

	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * @param picking
	 * 
	 */
	@Override
	public void drawMyJTSGeometries(final boolean picking) {

		if ( picking ) {
			getMyGLRender().gl.glPushMatrix();
			getMyGLRender().gl.glInitNames();
			getMyGLRender().gl.glPushName(0);
			int i = 0;
			Iterator<MyJTSGeometry> it = this.getJTSGeometries().iterator();
			while (it.hasNext()) {
				getMyGLRender().gl.glPushMatrix();
				getMyGLRender().gl.glLoadName(i);
				MyJTSGeometry curGeometry = it.next();

				if ( getPickedObjectIndex() == i ) {
					MyJTSGeometry pickedGeometry = (MyJTSGeometry) curGeometry.clone();
					pickedGeometry.color = Color.red;
					if ( pickedGeometry.agent != null ) {} else {
						throw new GamaRuntimeException("Picking null agent");
					}

					if ( pickedGeometry.agent != null && getCurrentPicked() != i ) {
						getMyGLRender().displaySurface.selectAgents(0, 0, pickedGeometry.agent,
							pickedGeometry.layerId - 1);
						setCurrentPicked(i);
					}

					getMyGLRender().graphicsGLUtils.basicDrawer.DrawJTSGeometry(pickedGeometry);
				} else {
					getMyGLRender().graphicsGLUtils.basicDrawer.DrawJTSGeometry(curGeometry);
				}

				getMyGLRender().gl.glPopMatrix();
				i++;
			}
			getMyGLRender().gl.glPopName();
			getMyGLRender().gl.glPopMatrix();
		} else {
			// System.out.println("isListCreated="+isListCreated);
			if ( isUseDisplayList() ) {
				// System.out.println("Geometries are build with displayList");
				if ( !isListCreated() ) {
					System.out.println("Create" + this.getJTSGeometries().size() + "list");
					getMyGLRender().graphicsGLUtils.displayListHandler
						.buildDisplayLists((ArrayList<MyJTSGeometry>) this.getJTSGeometries());
					System.out.println("Create" + this.getJTSGeometries().size() + "list ok");
					setListCreated(true);
				} else {
					// System.out.println("Call" + this.myJTSGeometries.size() +
					// "list");
					getMyGLRender().graphicsGLUtils.displayListHandler.DrawDisplayList(this.getJTSGeometries().size());
				}
			} else {

				if ( !isUseVertexArray() ) {
					// System.out.println(this.myJTSGeometries.size() +
					// " geometries are build with basicDrawer ");
					Iterator<MyJTSGeometry> it = this.getJTSGeometries().iterator();
					while (it.hasNext()) {
						MyJTSGeometry curGeometry = it.next();
						getMyGLRender().graphicsGLUtils.basicDrawer.DrawJTSGeometry(curGeometry);
					}
				}
				// use vertex array
				else {
					// triangulate all the geometries
					if ( !isPolygonTriangulated() ) {
						getMyGLRender().graphicsGLUtils.vertexArrayHandler
							.buildVertexArray((ArrayList<MyJTSGeometry>) this.getJTSGeometries());
						setPolygonTriangulated(true);
					} else {
						getMyGLRender().graphicsGLUtils.vertexArrayHandler.drawVertexArray();
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
	@Override
	public void drawMyJTSStaticGeometries(final boolean picking) {
		if ( picking ) {
			// TODO
		} else {
			if ( !isStaticListCreated() ) {
				System.out.println("Create" + getMyJTSStaticGeometries().size() + "list static");
				getMyGLRender().graphicsGLUtils.displayListHandler.buildDisplayLists((List<MyJTSGeometry>) this
					.getMyJTSStaticGeometries());
				setStaticListCreated(true);
				System.out.println("Create" + getMyJTSStaticGeometries().size() + "list static ok");
			} else {
				getMyGLRender().graphicsGLUtils.displayListHandler.DrawDisplayList(this.getMyJTSStaticGeometries()
					.size());
			}
		}
	}

	/**
	 * Once the list of Images has been created, OpenGL display call this method
	 * every framerate. FIXME: Need to be optimize with the use of Vertex Array
	 * or even VBO
	 * 
	 */
	@Override
	public void drawMyImages(final boolean picking) {

		if ( picking ) {

			getMyGLRender().gl.glPushMatrix();
			getMyGLRender().gl.glInitNames();
			getMyGLRender().gl.glPushName(0);
			int i = 0;

			Iterator<MyImage> it = this.getImages().iterator();
			while (it.hasNext()) {
				getMyGLRender().gl.glPushMatrix();
				getMyGLRender().gl.glLoadName(i);

				MyImage curImage = it.next();

				if ( getPickedObjectIndex() == i ) {
					if ( curImage.agent != null ) {

						getMyGLRender().gl.glColor3d(0, 0, 0);
						getMyGLRender().gl.glWindowPos2d(2, 5);
						// glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24,
						// curImage.agent.getSpeciesName() + curImage.agent.getIndex());
						setCurrentPicked(i);

						if ( getCurrentPicked() != i ) {
							// Call Agent window inspector
						}
					} else {
						System.out.println("Picking null agent");
					}
					getMyGLRender().DrawTexture(curImage);
				} else {
					getMyGLRender().DrawTexture(curImage);
				}

				getMyGLRender().gl.glPopMatrix();
				i++;
			}
			getMyGLRender().gl.glPopName();
			getMyGLRender().gl.glPopMatrix();

		} else {
			boolean drawImageAsList = false;
			if ( drawImageAsList ) {
				if ( !isListCreated() ) {
					getMyGLRender().graphicsGLUtils.displayListHandler.buildImageDisplayLists(this.getImages());
					setListCreated(true);
				} else {
					getMyGLRender().graphicsGLUtils.displayListHandler.DrawImageDisplayList(this.getImages().size());
				}

			} else {
				Iterator<MyImage> it = this.getImages().iterator();
				while (it.hasNext()) {
					MyImage curImage = it.next();
					getMyGLRender().DrawTexture(curImage);
				}
			}
		}
	}

	/**
	 * Once the list of String has been created, OpenGL display call this method
	 * every framerate.
	 * 
	 */
	@Override
	public void drawMyStrings() {

		Iterator<MyString> it = this.getStrings().iterator();
		while (it.hasNext()) {
			MyString curString = it.next();
			getMyGLRender().graphicsGLUtils.DrawString(curString.string, curString.x, curString.y, curString.z);
		}
	}

	public void drawEnvironmentBounds(final boolean drawData) {
		GamaPoint offSet = new GamaPoint(0, 0);
		if ( drawData ) {
			// Draw Width and height value
			this.getMyGLRender().graphicsGLUtils.DrawString(String.valueOf(this.getEnvWidth()), this.getEnvWidth() / 2,
				this.getEnvHeight() * 0.01f, 0.0f);
			this.getMyGLRender().graphicsGLUtils.DrawString(String.valueOf(this.getEnvHeight()),
				this.getEnvWidth() * 1.01f, -(this.getEnvHeight() / 2), 0.0f);
		}

		// Draw environment rectangle
		Geometry g =
			GamaGeometryType.buildRectangle(getEnvWidth(), getEnvHeight(),
				new GamaPoint(getEnvWidth() / 2, getEnvHeight() / 2)).getInnerGeometry();

		Color c = new Color(225, 225, 225);
		MyJTSGeometry curGeometry =
			new MyJTSGeometry(g, null, -0.01f, -1, c, 1.0f, true, c, false, 0, 0.0f, offSet, false, "environment");
		getMyGLRender().graphicsGLUtils.basicDrawer.DrawJTSGeometry(curGeometry);
	}

	@Override
	public void drawCollection() {

		// FIXME : need to be done for a list of collection
		setDrawCollectionAsList(true);
		if ( isDrawCollectionAsList() ) {
			if ( !isListShapeCreated() ) {
				getMyGLRender().graphicsGLUtils.displayListHandler.buildCollectionDisplayLists(getCollections());
				setListShapeCreated(true);
			} else {
				getMyGLRender().graphicsGLUtils.displayListHandler.drawCollectionDisplayList(getCollections().size());
			}

		} else {

			Iterator<MyCollection> it = this.getCollections().iterator();
			while (it.hasNext()) {
				MyCollection curCol = it.next();
				getMyGLRender().graphicsGLUtils.basicDrawer.drawSimpleFeatureCollection(curCol);
			}
		}

	}

	// ///////////////Clean method /////////////////////////

	/**
	 * Call when updateDisplay() is called
	 */
	@Override
	public void cleanGeometries() {
		if ( isUseDisplayList() ) {
			getMyGLRender().graphicsGLUtils.displayListHandler.DeleteDisplayLists(this.getJTSGeometries().size());
		}
		if ( isUseVertexArray() ) {
			getMyGLRender().graphicsGLUtils.vertexArrayHandler.DeleteVertexArray();
		}
		this.getJTSGeometries().clear();
		setListCreated(false);

	}

	/**
	 * Call every new iteration when updateDisplay() is called Remove only the
	 * texture that has to be redrawn. Keep all the texture coming form a file.
	 * FIXME: Only work for png and jpg/jpeg file.
	 */
	@Override
	public void cleanImages() {
		this.getImages().clear();
		Iterator<MyTexture> it = this.getMyGLRender().myTextures.iterator();
		while (it.hasNext()) {
			MyTexture curtexture = it.next();
			// If the texture is coming from a file keep it
			if ( curtexture.ImageName.indexOf(".png") != -1 || curtexture.ImageName.indexOf(".jpg") != -1 ||
				curtexture.ImageName.indexOf(".jpeg") != -1 ) {

			}// Else remove to recreate a new texture (e.g for GridDisplay).
			else {
				it.remove();
			}
		}
	}

	@Override
	public void cleanCollections() {
		if ( isDrawCollectionAsList() ) {
			getMyGLRender().graphicsGLUtils.displayListHandler.DeleteCollectionDisplayLists(this.getCollections()
				.size());
		}
		this.getCollections().clear();
	}

	/**
	 * Call every new iteration when updateDisplay() is called
	 */
	@Override
	public void cleanStrings() {
		this.getStrings().clear();

	}

	//
	// public void draw(final GL gl) {
	//
	// if ( listID == -1 ) {
	// createDisplayList(gl);
	// }
	// gl.glCallList(listID);
	//
	// }

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
		setCurrentLayerId(0);
		setCurrentZLayer(0.0f);
	}

	/**
	 * Set the value z of the current Layer. If no value is define is defined
	 * set it to 0.
	 * Set the type of the layer weither it's a static layer (refresh:false) or
	 * a dynamic layer (by default or refresh:true)
	 */
	@Override
	public void newLayer(final double zLayerValue, final Boolean refresh) {
		setCurrentZLayer((float) (getMaxEnvDim() * zLayerValue));

		// If refresh: false -> draw static geometry -> currentLayerIsStatic=true
		if ( refresh != null ) {
			setCurrentLayerIsStatic(!refresh);
		} else {
			setCurrentLayerIsStatic(false);
		}
		setCurrentLayerId(getCurrentLayerId() + 1);
	}

	//
	// @Override
	// public boolean isOpenGL() {
	// return true;
	// }

	@Override
	public boolean useTesselation(final boolean useTesselation) {
		return getMyGLRender().useTessellation = useTesselation;
	}

	@Override
	public void setAmbientLightValue(final GamaColor lightValue) {
		getMyGLRender().ambientLightValue = lightValue;
	}

	@Override
	public boolean setPolygonMode(boolean polygonMode) {
		return getMyGLRender().polygonmode = polygonMode;
	}

	@Override
	public void drawDEM(GamaFile demFileName, GamaFile textureFileName) {
		System.out.println("drawDEM in JOGLGraphics " + demFileName.getPath() + "with " + textureFileName.getPath());
		// if ( this.myGLRender.getDem() != null ) {
		getMyGLRender().dem.init(this.getMyGLRender().gl);// , demFileName.getPath(), textureFileName.getPath());
		// }
	}

	@Override
	public void setCameraPosition(final ILocation camPos) {

		if ( camPos.equals(new GamaPoint(-1, -1, -1)) ) {// No change
			System.out.println("no change");
		} else {
			getMyGLRender().camera.updatePosition(camPos.getX(), camPos.getY(), camPos.getZ());
		}
	}

	@Override
	public Collection<MyJTSGeometry> getJTSGeometries() {
		return geometries;
	}

	public void setJTSGeometries(ArrayList<MyJTSGeometry> myJTSGeometries) {
		this.geometries = myJTSGeometries;
	}

	public JOGLAWTGLRenderer getMyGLRender() {
		return GLRender;
	}

	@Override
	public Collection<MyJTSGeometry> getMyJTSStaticGeometries() {
		return staticGeometries;
	}

	public void setMyJTSStaticGeometries(java.util.List<MyJTSGeometry> myJTSStaticGeometries) {
		this.staticGeometries = myJTSStaticGeometries;
	}

	public float getEnvWidth() {
		return envWidth;
	}

	public void setEnvWidth(float envWidth) {
		this.envWidth = envWidth;
	}

	public float getEnvHeight() {
		return envHeight;
	}

	public void setEnvHeight(float envHeight) {
		this.envHeight = envHeight;
	}

	@Override
	public float getMaxEnvDim() {
		return maxEnvDim;
	}

	public void setMaxEnvDim(float maxEnvDim) {
		this.maxEnvDim = maxEnvDim;
	}

	public float getCurrentZLayer() {
		return currentZLayer;
	}

	public void setCurrentZLayer(float currentZLayer) {
		this.currentZLayer = currentZLayer;
	}

	public int getCurrentLayerId() {
		return currentLayerId;
	}

	public void setCurrentLayerId(int currentLayerId) {
		this.currentLayerId = currentLayerId;
	}

	@Override
	public java.util.List<MyImage> getImages() {
		return images;
	}

	public void setImages(java.util.List<MyImage> images) {
		this.images = images;
	}

	@Override
	public java.util.List<MyCollection> getCollections() {
		return collections;
	}

	public void setCollections(java.util.List<MyCollection> collections) {
		this.collections = collections;
	}

	@Override
	public java.util.List<MyString> getStrings() {
		return strings;
	}

	public void setStrings(java.util.List<MyString> strings) {
		this.strings = strings;
	}

	public float getCurrentAlpha() {
		return currentAlpha;
	}

	public void setCurrentAlpha(float currentAlpha) {
		this.currentAlpha = currentAlpha;
	}

	public boolean isCurrentLayerIsStatic() {
		return currentLayerIsStatic;
	}

	public void setCurrentLayerIsStatic(boolean currentLayerIsStatic) {
		this.currentLayerIsStatic = currentLayerIsStatic;
	}

	public boolean isListCreated() {
		return isListCreated;
	}

	public void setListCreated(boolean isListCreated) {
		this.isListCreated = isListCreated;
	}

	public boolean isStaticListCreated() {
		return isStaticListCreated;
	}

	public void setStaticListCreated(boolean isStaticListCreated) {
		this.isStaticListCreated = isStaticListCreated;
	}

	public boolean isListShapeCreated() {
		return isListShapeCreated;
	}

	public void setListShapeCreated(boolean isListShapeCreated) {
		this.isListShapeCreated = isListShapeCreated;
	}

	public boolean isUseDisplayList() {
		return useDisplayList;
	}

	public void setUseDisplayList(boolean useDisplayList) {
		this.useDisplayList = useDisplayList;
	}

	private boolean isDrawCollectionAsList() {
		return drawCollectionAsList;
	}

	private void setDrawCollectionAsList(boolean drawCollectionAsList) {
		this.drawCollectionAsList = drawCollectionAsList;
	}

	public boolean isPolygonTriangulated() {
		return isPolygonTriangulated;
	}

	public void setPolygonTriangulated(boolean isPolygonTriangulated) {
		this.isPolygonTriangulated = isPolygonTriangulated;
	}

	public boolean isUseVertexArray() {
		return useVertexArray;
	}

	public void setUseVertexArray(boolean useVertexArray) {
		this.useVertexArray = useVertexArray;
	}

	int getCurrentPicked() {
		return currentPicked;
	}

	void setCurrentPicked(int currentPicked) {
		this.currentPicked = currentPicked;
	}

	int getPickedObjectIndex() {
		return pickedObjectIndex;
	}

	@Override
	public void setPickedObjectIndex(int pickedObjectIndex) {
		this.pickedObjectIndex = pickedObjectIndex;
	}

}
