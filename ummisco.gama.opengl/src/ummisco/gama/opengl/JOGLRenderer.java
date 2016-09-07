/*********************************************************************************************
 *
 *
 * 'JOGLAWTGLRenderer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.BufferOverflowException;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.jts.JTSDrawer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.FieldDrawer;
import ummisco.gama.opengl.scene.FieldObject;
import ummisco.gama.opengl.scene.GeometryDrawer;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.scene.ImageDrawer;
import ummisco.gama.opengl.scene.ImageObject;
import ummisco.gama.opengl.scene.ModelScene;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.opengl.scene.ResourceDrawer;
import ummisco.gama.opengl.scene.ResourceObject;
import ummisco.gama.opengl.scene.StringDrawer;
import ummisco.gama.opengl.scene.StringObject;
import ummisco.gama.opengl.utils.FPSDrawer;
import ummisco.gama.opengl.utils.GLUtilLight;
import ummisco.gama.opengl.vaoGenerator.DrawingEntityGenerator;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * This class plays the role of Renderer and IGraphics. Class JOGLRenderer.
 *
 * @author drogoul
 * @since 27 avr. 2015
 *
 */
public class JOGLRenderer extends Abstract3DRenderer {

	private final PickingState pickingState = new PickingState();
	private final GLUT glut = new GLUT();
	private Envelope3D ROIEnvelope = null;
	private volatile boolean inited;

	private final JTSDrawer jtsDrawer;

	public JOGLRenderer(final SWTOpenGLDisplaySurface d) {
		super(d);
		jtsDrawer = new JTSDrawer(this);
	}

	@Override
	public void defineROI(final Point start, final Point end) {
		final GamaPoint startInWorld = getRealWorldPointFromWindowPoint(start);
		final GamaPoint endInWorld = getRealWorldPointFromWindowPoint(end);
		ROIEnvelope = new Envelope3D(new Envelope(startInWorld.x, endInWorld.x, startInWorld.y, endInWorld.y));
	}

	@Override
	public void cancelROI() {
		if (camera.isROISticky())
			return;
		ROIEnvelope = null;
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		WorkbenchHelper.run(new Runnable() {

			@Override
			public void run() {
				getCanvas().setVisible(visible);

			}
		});

		// here, the drawingEntityGenerator is used only when there is a webgl
		// display
		drawingEntityGenerator = new DrawingEntityGenerator(this);

		// see
		// https://jogamp.org/deployment/v2.1.1/javadoc/jogl/javadoc/javax/media/opengl/glu/gl2/GLUgl2.html
		// GLU objects are NOT thread safe...
		glu = new GLU();
		final GL2 gl = drawable.getContext().getGL().getGL2();
		final Color background = data.getBackgroundColor();
		gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		isNonPowerOf2TexturesAvailable = gl.isNPOTTextureAvailable();

		initializeCanvasListeners();

		updateCameraPosition();

		// Putting the swap interval to 0 (instead of 1) seems to cure some of
		// the problems of resizing of views.
		gl.setSwapInterval(0);

		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		GLUtilLight.enableSmooth(gl);
		GLUtilLight.enableDepthTest(gl);
		GLUtilLight.InitializeLighting(gl, data);

		// Perspective correction
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		// PolygonMode (Solid or lines)
		if (data.isPolygonMode()) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		} else {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
		}

		// Blending & alpha control
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL2.GL_GREATER, 0.01f);
		// FIXME : should be turn on only if need (if we draw image)
		// problem when true with glutBitmapString
		updatePerspective(gl);
		// We mark the renderer as inited
		inited = true;

	}

	@Override
	public Integer getGeometryListFor(final GL2 gl, final GamaGeometryFile file) {
		return geometryCache.get(gl, this, file);
	}

	@Override
	public TextRenderer getTextRendererFor(final Font font) {
		return textRendererCache.get(font);
	}

	private boolean visible;
	private final FPSDrawer fpsDrawer = new FPSDrawer();

	@Override
	public void display(final GLAutoDrawable drawable) {

		currentScene = sceneBuffer.getSceneToRender();
		if (currentScene == null) {
			return;
		}
		gl = drawable.getContext().getGL().getGL2();
		// We preload any geometry, textures, etc. that are used in layers
		currentScene.preload(gl);

		// if () != null && animator.isPaused() ) { return; }

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);
		gl.glClearDepth(1.0f);
		final Color background = data.getBackgroundColor();
		gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();

		// TODO Is this line necessary ? The changes are made in init and
		// reshape
		updateCameraPosition();
		updatePerspective(gl);
		if (data.isLightOn()) {
			gl.glEnable(GLLightingFunc.GL_LIGHTING);
		} else {
			gl.glDisable(GLLightingFunc.GL_LIGHTING);
		}

		if (data.isLightOn()) {
			GLUtilLight.UpdateAmbiantLightValue(gl, getGlu(), data.getAmbientLightColor());
			GLUtilLight.UpdateDiffuseLightValue(gl, data.getDiffuseLights(), getMaxEnvDim() / 20, data.getEnvWidth(),
					data.getEnvHeight());
		}

		// Line width ? Disable line smoothing seems to improve rendering time
		GLUtilLight.setLineWidth(gl, getLineWidth(), false);
		//

		if (!data.isTriangulation()) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		} else {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
		}

		this.rotateModel(gl);
		if (data.isShowfps())
			fpsDrawer.draw(gl, this);
		drawScene(gl);

		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

		if (ROIEnvelope != null) {
			drawROI(gl);
		}
		if (drawRotationHelper) {
			drawRotationHelper(gl);
		}
		if (!visible) {
			// We make the canvas visible only after a first display has occured
			visible = true;
			WorkbenchHelper.asyncRun(new Runnable() {

				@Override
				public void run() {
					getCanvas().setVisible(true);

				}
			});

		}

	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width,
			final int height) {
		// Get the OpenGL graphics context
		if (width <= 0 || height <= 0) {
			return;
		}
		final GL2 gl = drawable.getContext().getGL().getGL2();
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Enable the model view - any new transformations will affect the
		// model-view matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
		// perspective view
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		// Only if zoomFit... camera.resetCamera(data.getEnvWidth(),
		// data.getEnvHeight(), data.isOutput3D());
		updatePerspective(gl);
	}

	public final void updatePerspective(final GL2 gl) {
		final int height = getDrawable().getSurfaceHeight();
		final double aspect = (double) getDrawable().getSurfaceWidth() / (double) (height == 0 ? 1 : height);

		final double maxDim = getMaxEnvDim();

		if (!data.isOrtho()) {
			try {
				final double zNear = maxDim / 1000;
				double fW, fH;
				// final double fovY = 45.0d;
				final double fovY = this.data.getCameralens();
				if (aspect > 1.0) {
					fH = FastMath.tan(fovY / 360 * Math.PI) * zNear;
					fW = fH * aspect;
				} else {
					fW = FastMath.tan(fovY / 360 * Math.PI) * zNear;
					fH = fW / aspect;
				}
				gl.glFrustum(-fW, fW, -fH, fH, zNear, maxDim * 10);
			} catch (final BufferOverflowException e) {
				System.out.println("Buffer overflow exception");
			}
		} else {
			if (aspect >= 1.0) {
				((GL2ES1) gl).glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
			} else {
				((GL2ES1) gl).glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim, -maxDim);
			}
			gl.glTranslated(0d, 0d, maxDim * 0.05);
		}

		camera.animate();
	}

	public void drawScene(final GL2 gl) {
		currentScene = sceneBuffer.getSceneToRender();
		if (currentScene == null) {
			return;
		}
		// Do some garbage collecting in model scenes
		sceneBuffer.garbageCollect(gl);
		// if picking, we draw a first pass to pick the color
		if (pickingState.isBeginningPicking()) {
			beginPicking(gl);
			currentScene.draw(gl);
			endPicking(gl);
		}
		// we draw the scene on screen
		currentScene.draw(gl);
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First pass prepare select buffer for select mode by clearing it, prepare
	 * openGL to select mode and tell it where should draw object by using
	 * gluPickMatrix() method
	 * 
	 * @return if returned value is true that mean the picking is enabled
	 */
	public void beginPicking(final GL2 gl) {

		final GLU glu = getGlu();

		// 1. Selecting buffer
		selectBuffer.clear(); // prepare buffer for new objects
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);// add buffer
																	// to openGL

		// Pass below is very similar to refresh method in GLrenderer
		// 2. Take the viewport attributes,
		final int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		// final int width = viewport[2]; // get width and
		// final int height = viewport[3]; // height from viewport

		// 3. Prepare openGL for rendering in select mode
		gl.glRenderMode(GL2.GL_SELECT);

		/*
		 * The application must redefine the viewing volume so that it renders
		 * only a small area around the place where the mouse was clicked. In
		 * order to do that it is necessary to set the matrix mode to
		 * GL_PROJECTION. Afterwards, the application should push the current
		 * matrix to save the normal rendering mode settings. Next initialise
		 * the matrix
		 */

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		/*
		 * Define the viewing volume so that rendering is done only in a small
		 * area around the cursor. gluPickMatrix method restrict the area where
		 * openGL will drawing objects
		 *
		 * OpenGL has a different origin for its window coordinates than the
		 * operation system. The second parameter provides for the conversion
		 * between the two systems, i.e. it transforms the origin from the upper
		 * left corner, into the bottom left corner
		 */
		glu.gluPickMatrix(camera.getMousePosition().x, viewport[3] - camera.getMousePosition().y, 4, 4, viewport, 0);

		// FIXME Why do we have to call updatePerspective() here ?
		updatePerspective(gl);
		// Comment GL_MODELVIEW to debug3D picking (redraw the model when
		// clicking)
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// 4. After this pass you must draw Objects

	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and
	 * return its index
	 * 
	 * @return name of selected object
	 */
	public void endPicking(final GL2 gl) {

		// this.setPickedPressed(false);// no further iterations
		int selectedIndex = PickingState.NONE;

		// 5. When you back to Render mode gl.glRenderMode() methods return
		// number of hits
		final int howManyObjects = gl.glRenderMode(GL2.GL_RENDER);

		// 6. Restore to normal settings
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		// 7. Seach the select buffer to find the nearest object

		// code below derive which objects is nearest from monitor
		//
		if (howManyObjects > 0) {
			// simple searching algorithm
			selectedIndex = selectBuffer.get(3);
			int mindistance = CmnFastMath.abs(selectBuffer.get(1));
			for (int i = 0; i < howManyObjects; i++) {

				if (mindistance < CmnFastMath.abs(selectBuffer.get(1 + i * 4))) {

					mindistance = CmnFastMath.abs(selectBuffer.get(1 + i * 4));
					selectedIndex = selectBuffer.get(3 + i * 4);

				}

			}
			// end of searching
		} else {
			selectedIndex = PickingState.WORLD;// return -2 of there was no hits
		}

		pickingState.setPickedIndex(selectedIndex);
	}

	@Override
	public void dispose(final GLAutoDrawable drawable) {
		sceneBuffer.garbageCollect((GL2) drawable.getGL());
		sceneBuffer.dispose();

		textureCache.dispose(drawable.getGL());
		geometryCache.dispose(drawable.getGL().getGL2());
		textRendererCache.dispose(drawable.getGL());
		this.canvas = null;
		this.camera = null;
		this.currentLayer = null;
		// this.setCurrentPickedObject(null);
		this.currentScene = null;
		drawable.removeGLEventListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		dispose(getDrawable());
	}

	// Use when the rotation button is on.
	public void rotateModel(final GL2 gl) {
		if (data.isRotationOn()) {
			currentZRotation++;
		}
		if (currentZRotation != 0) {
			final double env_width = data.getEnvWidth();
			final double env_height = data.getEnvHeight();
			gl.glTranslated(env_width / 2, -env_height / 2, 0);
			gl.glRotated(currentZRotation, 0, 0, 1);
			gl.glTranslated(-env_width / 2, +env_height / 2, 0);
		}
	}

	@Override
	public void drawROI(final GL2 gl) {
		jtsDrawer.drawROIHelper(gl, ROIEnvelope);
	}

	@Override
	public Envelope3D getROIEnvelope() {
		return ROIEnvelope;
	}

	@Override
	public PickingState getPickingState() {
		return pickingState;
	}

	// This method is normally called either when the graphics is created or
	// when the output is changed
	// @Override
	@Override
	public void initScene() {
		if (sceneBuffer != null) {
			final ModelScene scene = sceneBuffer.getSceneToRender();
			if (scene != null) {
				scene.reload();
			}
		}
	}

	/**
	 * Method drawGeometry. Add a given JTS Geometry in the list of all the
	 * existing geometry that will be displayed by openGl.
	 */
	@Override
	public Rectangle2D drawShape(final IShape shape, final ShapeDrawingAttributes attributes) {
		if (shape == null) {
			return null;
		}
		if (sceneBuffer.getSceneToUpdate() == null) {
			return null;
		}
		// IShape.Type type = shape.getGeometricalType();
		if (highlight) {
			attributes.color = GamaColor.getInt(data.getHighlightColor().getRGB());
		}
		sceneBuffer.getSceneToUpdate().addGeometry(shape.getInnerGeometry(), attributes);

		return rect;

	}

	@Override
	public void startDrawRotationHelper(final GamaPoint pos) {
		rotationHelperPosition = pos;
		drawRotationHelper = true;
		final double distance = Math.sqrt(Math.pow(camera.getPosition().x - rotationHelperPosition.x, 2)
				+ Math.pow(camera.getPosition().y - rotationHelperPosition.y, 2)
				+ Math.pow(camera.getPosition().z - rotationHelperPosition.z, 2));
		final double size = distance / 10; // the size of the displayed axis
		if (currentScene != null)
			currentScene.startDrawRotationHelper(pos, size);
	}

	@Override
	public void stopDrawRotationHelper() {
		rotationHelperPosition = null;
		drawRotationHelper = false;
		if (currentScene != null)
			currentScene.stopDrawRotationHelper();
	}

	@Override
	public void drawRotationHelper(final GL2 gl) {
		final double distance = Math.sqrt(Math.pow(camera.getPosition().x - rotationHelperPosition.x, 2)
				+ Math.pow(camera.getPosition().y - rotationHelperPosition.y, 2)
				+ Math.pow(camera.getPosition().z - rotationHelperPosition.z, 2));
		jtsDrawer.drawRotationHelper(gl, rotationHelperPosition, distance);
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
	public Rectangle2D drawImage(final BufferedImage img, final FileDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) {
			return null;
		}
		if (attributes.size == null) {
			attributes.size = new GamaPoint(data.getEnvWidth(), data.getEnvHeight());
		}
		sceneBuffer.getSceneToUpdate().addImage(img, attributes);

		if (attributes.border != null) {
			drawGridLine(new GamaPoint(img.getWidth(), img.getHeight()), attributes.border);
		}
		return rect;
	}

	@Override
	public Rectangle2D drawFile(final GamaFile file, final FileDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) {
			return null;
		}

		if (file instanceof GamaGeometryFile && !envelopes.containsKey(file.getPath())) {
			envelopes.put(file.getPath(), file.computeEnvelope(surface.getScope()));
		}
		sceneBuffer.getSceneToUpdate().addFile(file, attributes);
		return rect;
	}

	@Override
	public Rectangle2D drawField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
		if (sceneBuffer.getSceneToUpdate() == null) {
			return null;
		}

		sceneBuffer.getSceneToUpdate().addField(fieldValues, attributes);
		/*
		 * This line has been removed to fix the issue 1174 if ( gridColor !=
		 * null ) { drawGridLine(img, gridColor); }
		 */
		return rect;
	}

	public void drawGridLine(final GamaPoint dimensions, final Color lineColor) {
		if (sceneBuffer.getSceneToUpdate() == null) {
			return;
		}
		double stepX, stepY;
		final double cellWidth = this.data.getEnvWidth() / dimensions.x;
		final double cellHeight = this.data.getEnvHeight() / dimensions.y;
		final GamaColor color = GamaColor.getInt(lineColor.getRGB());
		final ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(null, color, color, IShape.Type.GRIDLINE);
		for (double i = 0; i < dimensions.x; i++) {
			for (double j = 0; j < dimensions.y; j++) {
				stepX = i + 0.5;
				stepY = j + 0.5;
				final Geometry g = GamaGeometryType
						.buildRectangle(cellWidth, cellHeight, new GamaPoint(stepX * cellWidth, stepY * cellHeight))
						.getInnerGeometry();
				sceneBuffer.getSceneToUpdate().addGeometry(g, attributes);
			}
		}
	}

	@Override
	public Rectangle2D drawString(final String string, final TextDrawingAttributes attributes) {
		// Multiline: Issue #780
		if (sceneBuffer.getSceneToUpdate() == null) {
			return null;
		}
		if (string.contains("\n")) {
			for (final String s : string.split("\n")) {
				attributes.location.setY(attributes.location.getY()
						+ attributes.font.getSize() * this.getyRatioBetweenPixelsAndModelUnits());
				drawString(s, attributes);
			}
			return null;
		}
		attributes.location.setY(-attributes.location.getY());
		sceneBuffer.getSceneToUpdate().addString(string, attributes);
		return null;
	}

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		setOpacity(opacity);
	}

	/**
	 * Each new step the Z value of the first layer is set to 0.
	 */
	@Override
	public boolean beginDrawingLayers() {
		while (!inited) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				return false;
			}
		}
		return sceneBuffer.beginUpdatingScene();

	}

	/**
	 * Set the value z of the current Layer. If no value is define is defined
	 * set it to 0. Set the type of the layer weither it's a static layer
	 * (refresh:false) or a dynamic layer (by default or refresh:true)
	 */
	@Override
	public void beginDrawingLayer(final ILayer layer) {
		super.beginDrawingLayer(layer);
		GamaPoint currentOffset, currentScale;
		if (!(layer instanceof OverlayLayer)) {
			final double currentZLayer = getMaxEnvDim() * layer.getPosition().getZ();

			// get the value of the z scale if positive otherwise set it to 1.
			double z_scale;
			if (layer.getExtent().getZ() > 0) {
				z_scale = layer.getExtent().getZ();
			} else {
				z_scale = 1;
			}

			currentOffset = new GamaPoint(getXOffsetInPixels() / (getWidth() / data.getEnvWidth()),
					getYOffsetInPixels() / (getHeight() / data.getEnvHeight()), currentZLayer);
			currentScale = new GamaPoint(getLayerWidth() / getWidth(), getLayerHeight() / getHeight(), z_scale);
		} else {
			currentOffset = new GamaPoint(getXOffsetInPixels(), getYOffsetInPixels());
			currentScale = new GamaPoint(1, 1, 1);
		}
		final ModelScene scene = sceneBuffer.getSceneToUpdate();
		if (scene != null) {
			scene.beginDrawingLayer(layer, currentOffset, currentScale, currentAlpha);
		}
	}

	/**
	 * Method endDrawingLayers()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayers()
	 */
	@Override
	public void endDrawingLayers() {
		sceneBuffer.endUpdatingScene();
		getSurface().invalidateVisibleRegions();
	}

	@Override
	public GamaPoint getRealWorldPointFromWindowPoint(final Point windowPoint) {
		if (glu == null) {
			return null;
		}
		int realy = 0;// GL y coord pos
		final double[] wcoord = new double[4];// wx, wy, wz;// returned xyz
												// coords

		final int x = (int) windowPoint.getX(), y = (int) windowPoint.getY();

		realy = viewport[3] - y;
		glu.gluUnProject(x, realy, 0.1, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v1 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

		glu.gluUnProject(x, realy, 0.9, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v2 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

		final GamaPoint v3 = v2.minus(v1).normalized();
		final float distance = (float) (camera.getPosition().getZ()
				/ GamaPoint.dotProduct(new GamaPoint(0.0, 0.0, -1.0), v3));
		final GamaPoint worldCoordinates = camera.getPosition().plus(v3.times(distance));

		return new GamaPoint(worldCoordinates.x, worldCoordinates.y);
	}

	/**
	 * Method beginOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#beginOverlay(msi.gama.outputs.layers.OverlayLayer)
	 */
	@Override
	public void beginOverlay(final OverlayLayer layer) {
		final ModelScene scene = sceneBuffer.getSceneToUpdate();
		if (scene != null) {
			scene.beginOverlay();
		}

	}

	/**
	 * Method endOverlay()
	 * 
	 * @see msi.gama.common.interfaces.IGraphics#endOverlay()
	 */
	@Override
	public void endOverlay() {

	}

	@Override
	public boolean mouseInROI(final Point mousePosition) {
		final Envelope3D env = getROIEnvelope();
		if (env == null)
			return false;
		final GamaPoint p = getRealWorldPointFromWindowPoint(mousePosition);
		return env.contains(p);
	}

	public JTSDrawer getJTSDrawer() {
		return jtsDrawer;
	}

	public GLUT getGlut() {
		return glut;
	}

	@Override
	public boolean cannotDraw() {
		return sceneBuffer.getSceneToUpdate() != null && sceneBuffer.getSceneToUpdate().cannotAdd();
	}

	Map<Class, ObjectDrawer> drawers;

	@Override
	public ObjectDrawer getDrawerFor(final Class<? extends AbstractObject> class1) {
		if (drawers == null) {
			drawers = new HashMap();
			drawers.put(GeometryObject.class, new GeometryDrawer(this));
			drawers.put(ImageObject.class, new ImageDrawer(this));
			drawers.put(FieldObject.class, new FieldDrawer(this));
			drawers.put(StringObject.class, new StringDrawer(this));
			drawers.put(ResourceObject.class, new ResourceDrawer(this));
		}
		return drawers.get(class1);
	}

}
