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

import java.awt.*;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.BufferOverflowException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.display.AbstractDisplayGraphics;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gama.util.file.*;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.statements.draw.*;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.camera.*;
import ummisco.gama.opengl.jts.JTSDrawer;
import ummisco.gama.opengl.scene.*;
import ummisco.gama.opengl.utils.GLUtilLight;

/**
 * This class plays the role of Renderer and IGraphics.
 * Class JOGLRenderer.
 *
 * @author drogoul
 * @since 27 avr. 2015
 *
 */
public class JOGLRenderer extends AbstractDisplayGraphics implements IGraphics, GLEventListener {

	private static boolean BLENDING_ENABLED; // blending on/off
	GLCanvas canvas;
	public ICamera camera;
	public final SceneBuffer sceneBuffer;
	public double currentZRotation = 0;
	private boolean picking = false;
	private boolean drawRotationHelper = false;
	private GamaPoint rotationHelperPosition = null;
	public int pickedObjectIndex = -1;
	public AbstractObject currentPickedObject;
	int[] viewport = new int[4];
	double mvmatrix[] = new double[16];
	double projmatrix[] = new double[16];
	public boolean colorPicking = false;
	private GLU glu;
	private final GLUT glut = new GLUT();
	private Envelope3D ROIEnvelope = null;
	private ModelScene currentScene;
	private volatile boolean inited;

	public static Boolean isNonPowerOf2TexturesAvailable = false;
	protected static Map<String, Envelope> envelopes = new ConcurrentHashMap<>();
	// Use to inverse y composant
	public int yFlag;
	private final GeometryCache geometryCache = new GeometryCache();
	private final TextRenderersCache textRendererCache = new TextRenderersCache();
	private final TextureCache textureCache =
		GamaPreferences.DISPLAY_SHARED_CONTEXT.getValue() ? TextureCache.getSharedInstance() : new TextureCache();

		// private final GLModel chairModel = null;

		public JOGLRenderer(final SWTOpenGLDisplaySurface d) {
			super(d);
			camera = new CameraArcBall(this);
			sceneBuffer = new SceneBuffer(this);
			yFlag = -1;
		}

		public GLAutoDrawable createDrawable(final Composite parent) {
			boolean useSharedContext = GamaPreferences.DISPLAY_SHARED_CONTEXT.getValue();
			GLProfile profile = useSharedContext ? TextureCache.getSharedContext().getGLProfile() : GLProfile.getDefault();
			GLCapabilities cap = new GLCapabilities(profile);
			cap.setStencilBits(8);
			cap.setDoubleBuffered(true);
			cap.setHardwareAccelerated(true);
			canvas = new GLCanvas(parent, SWT.NONE, cap, null);
			if ( useSharedContext ) {
				canvas.setSharedAutoDrawable(TextureCache.getSharedContext());
			}
			canvas.setAutoSwapBufferMode(true);
			new SWTGLAnimator(canvas);
			canvas.addGLEventListener(this);
			return canvas;
		}

		public ModelScene getCurrentScene() {
			return currentScene;
		}

		public void defineROI(final Point start, final Point end) {
			GamaPoint startInWorld = getRealWorldPointFromWindowPoint(start);
			GamaPoint endInWorld = getRealWorldPointFromWindowPoint(end);
			ROIEnvelope = new Envelope3D(new Envelope(startInWorld.x, endInWorld.x, startInWorld.y, endInWorld.y));
		}

		public void cancelROI() {
			ROIEnvelope = null;
		}

		public GLCanvas getCanvas() {
			return canvas;
		}

		public GLAutoDrawable getDrawable() {
			return canvas;
		}

		protected void initializeCanvasWithListeners() {

			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					if ( getCanvas() == null || getCanvas().isDisposed() ) { return; }
					getCanvas().addKeyListener(camera);
					getCanvas().addMouseListener(camera);
					// getCanvas().addMouseListener(displaySurface.getEventMouse());
					getCanvas().addMouseMoveListener(camera);
					getCanvas().addMouseWheelListener(camera);
					getCanvas().addMouseTrackListener(camera);
					getCanvas().setVisible(true);

				}
			});

		}

		@Override
		public void init(final GLAutoDrawable drawable) {
			// see https://jogamp.org/deployment/v2.1.1/javadoc/jogl/javadoc/javax/media/opengl/glu/gl2/GLUgl2.html
			// GLU objects are NOT thread safe...
			glu = new GLU();
			GL2 gl = drawable.getContext().getGL().getGL2();
			isNonPowerOf2TexturesAvailable = gl.isNPOTTextureAvailable();

			// GL2 gl = GLContext.getCurrentGL().getGL2();

			initializeCanvasWithListeners();

			updateCameraPosition();

			// Putting the swap interval to 0 (instead of 1) seems to cure some of the problems of resizing of views.
			gl.setSwapInterval(0);

			// Enable smooth shading, which blends colors nicely, and smoothes out lighting.
			GLUtilLight.enableSmooth(gl);
			GLUtilLight.enableDepthTest(gl);
			GLUtilLight.InitializeLighting(gl, (float) data.getEnvWidth(), (float) data.getEnvHeight(),
				data.getAmbientLightColor(), data.getDiffuseLightColor());

			// Perspective correction
			gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

			// PolygonMode (Solid or lines)
			if ( data.isPolygonMode() ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			}

			// Blending control
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL.GL_BLEND);
			// FIXME : should be turn on only if need (if we draw image)
			// problem when true with glutBitmapString
			BLENDING_ENABLED = true;
			updatePerspective(gl);
			// We mark the renderer as inited
			inited = true;

		}

		public Integer getGeometryListFor(final GL2 gl, final GamaGeometryFile file) {
			return geometryCache.get(gl, this, file);
		}

		public TextRenderer getTextRendererFor(final Font font) {
			return textRendererCache.get(font);
		}

		public boolean getDrawNormal() {
			return data.isDraw_norm();
		}

		public boolean getComputeNormal() {
			return data.isComputingNormals;
		}

		@Override
		public void display(final GLAutoDrawable drawable) {
			// fail fast
			if ( GAMA.getSimulation() == null ) { return; }
			currentScene = sceneBuffer.getSceneToRender();
			if ( currentScene == null ) { return; }
			GL2 gl = drawable.getContext().getGL().getGL2();
			// We preload any geometry, textures, etc. that are used in layers
			currentScene.preload(gl);

			// if () != null && animator.isPaused() ) { return; }

			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
			gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
			gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);
			gl.glClearDepth(1.0f);
			Color background = data.getBackgroundColor();
			gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f, background.getBlue() / 255.0f,
				1.0f);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glLoadIdentity();

			// TODO Is this line necessary ? The changes are made in init and reshape
			updateCameraPosition();
			updatePerspective(gl);
			if ( data.isLightOn() ) {
				gl.glEnable(GLLightingFunc.GL_LIGHTING);
			} else {
				gl.glDisable(GLLightingFunc.GL_LIGHTING);
			}

			GLUtilLight.UpdateAmbiantLightValue(gl, getGlu(), data.getAmbientLightColor());
			GLUtilLight.UpdateDiffuseLightValue(gl, getGlu(), data.getDiffuseLightColor());

			float[] light0Position = new float[4];
			ILocation p1 = data.getDiffuseLightPosition();
			if ( p1.equals(LayeredDisplayData.noChange) ) {
				p1 = new GamaPoint(data.getEnvWidth() / 2, data.getEnvHeight() / 2, data.getEnvWidth() * 2);
			}
			ILocation p = p1;
			light0Position[0] = (float) p.getX();
			light0Position[1] = -(float) p.getY();
			light0Position[2] = (float) p.getZ();
			light0Position[3] = 0.0f;

			if ( data.isDrawDiffLight() ) {
				GLUtilLight.DrawDiffuseLight0(light0Position, gl, getGlu(), getMaxEnvDim() / 10,
					data.getDiffuseLightColor());
			}

			GLUtilLight.UpdateDiffuseLightPosition(gl, getGlu(), light0Position);

			// Blending control
			if ( BLENDING_ENABLED ) {
				gl.glEnable(GL.GL_BLEND); // Turn blending on

			} else {
				gl.glDisable(GL.GL_BLEND); // Turn blending off
				gl.glEnable(GL.GL_DEPTH_TEST);
			}

			// Line width ? Disable line smoothing seems to improve rendering time
			GLUtilLight.setLineWidth(gl, getLineWidth(), false);
			//

			if ( !data.isTriangulation() ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			}

			this.rotateModel(gl);
			drawScene(gl);

			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

			if ( ROIEnvelope != null ) {
				drawROI(gl);
			}
			if ( drawRotationHelper ) {
				drawRotationHelper(gl);
			}

		}

		@Override
		public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width,
			final int height) {
			// Get the OpenGL graphics context
			if ( width <= 0 || height <= 0 ) { return; }
			GL2 gl = drawable.getContext().getGL().getGL2();
			// Set the viewport (display area) to cover the entire window
			gl.glViewport(0, 0, width, height);
			// Enable the model view - any new transformations will affect the model-view matrix
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			gl.glLoadIdentity(); // reset
			// perspective view
			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
			gl.glLoadIdentity();
			// Only if zoomFit... camera.resetCamera(data.getEnvWidth(), data.getEnvHeight(), data.isOutput3D());
			updatePerspective(gl);
		}

		public final void updatePerspective(final GL2 gl) {
			int height = getDrawable().getSurfaceHeight();
			double aspect = (double) getDrawable().getSurfaceWidth() / (double) (height == 0 ? 1 : height);

			double maxDim = getMaxEnvDim();

			if ( !data.isOrtho() ) {
				try {
					double zNear = maxDim / 1000;
					double fW, fH;
					double fovY = 45.0d;
					if ( aspect > 1.0 ) {
						fH = FastMath.tan(fovY / 360 * Math.PI) * zNear;
						fW = fH * aspect;
					} else {
						fW = FastMath.tan(fovY / 360 * Math.PI) * zNear;
						fH = fW / aspect;
					}
					gl.glFrustum(-fW, fW, -fH, fH, zNear, maxDim * 10);
				} catch (BufferOverflowException e) {
					System.out.println("Buffer overflow exception");
				}
			} else {
				if ( aspect >= 1.0 ) {
					((GL2ES1) gl).glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
				} else {
					((GL2ES1) gl).glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim, -maxDim);
				}
				gl.glTranslated(0d, 0d, maxDim * 0.05);
			}

			camera.animate();
		}

		public double getMaxEnvDim() {
			// built dynamically to prepare for the changes in size of the environment
			double env_width = data.getEnvWidth();
			double env_height = data.getEnvHeight();
			return env_width > env_height ? env_width : env_height;
		}

		public void drawScene(final GL2 gl) {
			currentScene = sceneBuffer.getSceneToRender();
			if ( currentScene == null ) { return; }
			// Do some garbage collecting in model scenes
			sceneBuffer.garbageCollect(gl);
			// if picking, we draw a first pass to pick the color
			if ( picking && camera.beginPicking(gl) ) {
				currentScene.draw(gl, true);
				setPickedObjectIndex(camera.endPicking(gl));
			}
			// we draw the scene on screen
			currentScene.draw(gl, currentPickedObject != null);

		}

		public void switchCamera() {
			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					getCanvas().removeKeyListener(camera);
					getCanvas().removeMouseListener(camera);
					getCanvas().removeMouseMoveListener(camera);
					getCanvas().removeMouseWheelListener(camera);
					getCanvas().removeMouseTrackListener(camera);
				}
			});

			if ( !data.isArcBallCamera() ) {
				camera = new FreeFlyCamera(this);
			} else {
				camera = new CameraArcBall(this);
			}

			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					getCanvas().addKeyListener(camera);
					getCanvas().addMouseListener(camera);
					getCanvas().addMouseMoveListener(camera);
					getCanvas().addMouseWheelListener(camera);
					getCanvas().addMouseTrackListener(camera);
				}
			});

		}

		public double getWidth() {
			return getDrawable().getSurfaceWidth() * surface.getZoomLevel();
		}

		public double getHeight() {
			return getDrawable().getSurfaceHeight() * surface.getZoomLevel();
		}

		public void updateCameraPosition() {
			camera.update();
		}

		public void setPickedObjectIndex(final int pickedObjectIndex) {
			this.pickedObjectIndex = pickedObjectIndex;
			if ( pickedObjectIndex == -1 ) {
				setPicking(false);
			} else if ( pickedObjectIndex == -2 ) {
				getSurface().selectAgent(null);
				setPicking(false);
			}
		}

		@Override
		public void dispose(final GLAutoDrawable drawable) {
			sceneBuffer.dispose();
			textureCache.dispose(drawable.getGL());
			geometryCache.dispose(drawable.getGL().getGL2());
			textRendererCache.dispose(drawable.getGL());
			this.canvas = null;
			this.camera = null;
			this.currentLayer = null;
			this.currentPickedObject = null;
			this.currentScene = null;
		}

		// Use when the rotation button is on.
		public void rotateModel(final GL2 gl) {
			if ( data.isRotationOn() ) {
				currentZRotation++;
			}
			if ( currentZRotation != 0 ) {
				double env_width = data.getEnvWidth();
				double env_height = data.getEnvHeight();
				gl.glTranslated(env_width / 2, -env_height / 2, 0);
				gl.glRotated(currentZRotation, 0, 0, 1);
				gl.glTranslated(-env_width / 2, +env_height / 2, 0);
			}
		}

		public void drawROI(final GL2 gl) {
			double x1 = ROIEnvelope.getMinX();
			double y1 = -ROIEnvelope.getMinY();
			double x2 = ROIEnvelope.getMaxX();
			double y2 = -ROIEnvelope.getMaxY();

			Double distance = FastMath.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			gl.glRasterPos3d(x2, -y1, 0.1);
			gl.glColor3d(0.0, 0.0, 0.0);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "  d: " + distance.toString());
			if ( this.data.isZ_fighting() ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
				// Draw on top of everything
				gl.glPolygonOffset(0.0f, (float) -this.getMaxEnvDim());
				gl.glBegin(GL2.GL_POLYGON);

				gl.glVertex3d(x1, -y1, 0.0f);
				gl.glVertex3d(x2, -y1, 0.0f);

				gl.glVertex3d(x2, -y1, 0.0f);
				gl.glVertex3d(x2, -y2, 0.0f);

				gl.glVertex3d(x2, -y2, 0.0f);
				gl.glVertex3d(x1, -y2, 0.0f);

				gl.glVertex3d(x1, -y2, 0.0f);
				gl.glVertex3d(x1, -y1, 0.0f);
				gl.glEnd();
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			} else {
				gl.glBegin(GL.GL_LINES);

				gl.glVertex3d(x1, -y1, 0.0f);
				gl.glVertex3d(x2, -y1, 0.0f);

				gl.glVertex3d(x2, -y1, 0.0f);
				gl.glVertex3d(x2, -y2, 0.0f);

				gl.glVertex3d(x2, -y2, 0.0f);
				gl.glVertex3d(x1, -y2, 0.0f);

				gl.glVertex3d(x1, -y2, 0.0f);
				gl.glVertex3d(x1, -y1, 0.0f);
				gl.glEnd();
			}
		}

		public Envelope3D getROIEnvelope() {
			return ROIEnvelope;
		}

		public void setPicking(final boolean value) {
			picking = value;
			if ( !value ) {
				if ( currentPickedObject != null ) {
					currentPickedObject.unpick();
					currentPickedObject = null;
				}
				pickedObjectIndex = -1;
			}
		}

		// This method is normally called either when the graphics is created or when the output is changed
		// @Override
		public void initScene() {
			if ( sceneBuffer != null ) {
				ModelScene scene = sceneBuffer.getSceneToRender();
				if ( scene != null ) {
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
			if ( shape == null ) { return null; }
			if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
			// IShape.Type type = shape.getGeometricalType();
			if ( highlight ) {
				attributes.color = GamaColor.getInt(data.getHighlightColor().getRGB());
			}
			sceneBuffer.getSceneToUpdate().addGeometry(shape.getInnerGeometry(), attributes);

			return rect;

		}
		
		public void startDrawRotationHelper(GamaPoint pos) {
			rotationHelperPosition = pos;
			drawRotationHelper = true;
		}
		public void stopDrawRotationHelper() {
			rotationHelperPosition = null;
			drawRotationHelper = false;
		}
		
		public void drawRotationHelper(final GL2 gl) {
			JTSDrawer jtsDrawer = new JTSDrawer(this);
			jtsDrawer.drawRotationHelper(gl, rotationHelperPosition);
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
			if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
			if ( attributes.size == null ) {
				attributes.size = new GamaPoint(data.getEnvWidth(), data.getEnvHeight());
			}
			sceneBuffer.getSceneToUpdate().addImage(img, attributes);

			if ( attributes.border != null ) {
				drawGridLine(new GamaPoint(img.getWidth(), img.getHeight()), attributes.border);
			}
			return rect;
		}

		@Override
		public Rectangle2D drawFile(final GamaFile file, final FileDrawingAttributes attributes) {
			if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }

			if ( file instanceof GamaGeometryFile && !envelopes.containsKey(file.getPath()) ) {
				envelopes.put(file.getPath(), file.computeEnvelope(surface.getDisplayScope()));
			}
			sceneBuffer.getSceneToUpdate().addFile(file, attributes);
			return rect;
		}

		@Override
		public Rectangle2D drawField(final double[] fieldValues, final FieldDrawingAttributes attributes) {
			if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }

			sceneBuffer.getSceneToUpdate().addField(fieldValues, attributes);
			/*
			 * This line has been removed to fix the issue 1174
			 * if ( gridColor != null ) {
			 * drawGridLine(img, gridColor);
			 * }
			 */
			return rect;
		}

		public void drawGridLine(final GamaPoint dimensions, final Color lineColor) {
			if ( sceneBuffer.getSceneToUpdate() == null ) { return; }
			double stepX, stepY;
			double cellWidth = this.data.getEnvWidth() / dimensions.x;
			double cellHeight = this.data.getEnvHeight() / dimensions.y;
			GamaColor color = GamaColor.getInt(lineColor.getRGB());
			ShapeDrawingAttributes attributes = new ShapeDrawingAttributes(null, color, color, IShape.Type.GRIDLINE);
			for ( double i = 0; i < dimensions.x; i++ ) {
				for ( double j = 0; j < dimensions.y; j++ ) {
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
			if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
			if ( string.contains("\n") ) {
				for ( String s : string.split("\n") ) {
					attributes.location.setY(attributes.location.getY() +
						attributes.font.getSize() * this.getyRatioBetweenPixelsAndModelUnits());
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
		public void beginDrawingLayers() {
			while (!inited) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					return;
				}
			}
			sceneBuffer.beginUpdatingScene();

		}

		/**
		 * Set the value z of the current Layer. If no value is define is defined
		 * set it to 0. Set the type of the layer weither it's a static layer (refresh:false) or
		 * a dynamic layer (by default or refresh:true)
		 */
		@Override
		public void beginDrawingLayer(final ILayer layer) {
			super.beginDrawingLayer(layer);
			GamaPoint currentOffset, currentScale;
			if ( !(layer instanceof OverlayLayer) ) {
				double currentZLayer = getMaxEnvDim() * layer.getPosition().getZ();

				// get the value of the z scale if positive otherwise set it to 1.
				double z_scale;
				if ( layer.getExtent().getZ() > 0 ) {
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
			ModelScene scene = sceneBuffer.getSceneToUpdate();
			if ( scene != null ) {
				scene.beginDrawingLayer(layer, currentOffset, currentScale, currentAlpha);
			}
		}

		/**
		 * Method endDrawingLayers()
		 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayers()
		 */
		@Override
		public void endDrawingLayers() {
			sceneBuffer.endUpdatingScene();
			getSurface().invalidateVisibleRegions();
		}

		/**
		 * Method getDisplayWidthInPixels()
		 * @see msi.gama.common.interfaces.IGraphics#getDisplayWidthInPixels()
		 */
		@Override
		public int getDisplayWidth() {
			return (int) FastMath.round(getWidth());
		}

		/**
		 * Method getDisplayHeightInPixels()
		 * @see msi.gama.common.interfaces.IGraphics#getDisplayHeightInPixels()
		 */
		@Override
		public int getDisplayHeight() {
			return (int) FastMath.round(getHeight());
		}

		/**
		 * Method setOpacity()
		 * @see msi.gama.common.interfaces.IGraphics#setOpacity(double)
		 */
		@Override
		public void setOpacity(final double alpha) {
			currentAlpha = alpha;
		}

		public GLU getGlu() {
			return glu;
		}

		public GamaPoint getIntWorldPointFromWindowPoint(final Point windowPoint) {
			GamaPoint p = getRealWorldPointFromWindowPoint(windowPoint);
			return new GamaPoint((int) p.x, (int) p.y);
		}

		public GamaPoint getRealWorldPointFromWindowPoint(final Point windowPoint) {
			if ( glu == null ) { return null; }
			int realy = 0;// GL y coord pos
			double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords

			int x = (int) windowPoint.getX(), y = (int) windowPoint.getY();

			realy = viewport[3] - y;
			glu.gluUnProject(x, realy, 0.1, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
			GamaPoint v1 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

			glu.gluUnProject(x, realy, 0.9, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
			GamaPoint v2 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);

			GamaPoint v3 = v2.minus(v1).normalized();
			float distance =
				(float) (camera.getPosition().getZ() / GamaPoint.dotProduct(new GamaPoint(0.0, 0.0, -1.0), v3));
			GamaPoint worldCoordinates = camera.getPosition().plus(v3.times(distance));

			return new GamaPoint(worldCoordinates.x, worldCoordinates.y);
		}

		/**
		 * Method getZoomLevel()
		 * @see msi.gama.common.interfaces.IGraphics#getZoomLevel()
		 */
		@Override
		public Double getZoomLevel() {
			return data.getZoomLevel();
		}

		/**
		 * Useful for drawing fonts
		 * @return
		 */
		public double getGlobalYRatioBetweenPixelsAndModelUnits() {
			return getHeight() / data.getEnvHeight();
		}

		/**
		 * Method is2D()
		 * @see msi.gama.common.interfaces.IGraphics#is2D()
		 */
		@Override
		public boolean is2D() {
			return false;
		}

		/**
		 * @param path
		 * @return
		 */
		public static Envelope getEnvelopeFor(final String path) {
			return envelopes.get(path);
		}

		/**
		 * @return
		 */
		public static float getLineWidth() {
			return GamaPreferences.CORE_LINE_WIDTH.getValue().floatValue();
		}

		@Override
		public SWTOpenGLDisplaySurface getSurface() {
			return (SWTOpenGLDisplaySurface) surface;
		}

		/**
		 * Method beginOverlay()
		 * @see msi.gama.common.interfaces.IGraphics#beginOverlay(msi.gama.outputs.layers.OverlayLayer)
		 */
		@Override
		public void beginOverlay(final OverlayLayer layer) {
			ModelScene scene = sceneBuffer.getSceneToUpdate();
			if ( scene != null ) {
				scene.beginOverlay();
			}

		}

		/**
		 * Method endOverlay()
		 * @see msi.gama.common.interfaces.IGraphics#endOverlay()
		 */
		@Override
		public void endOverlay() {

		}

		public TextureCache getSharedTextureCache() {
			return textureCache;
		}

}
