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
import java.util.*;
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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.*;
import msi.gama.util.GamaColor;
import msi.gama.util.file.*;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.camera.*;
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
public class JOGLRenderer implements IGraphics.OpenGL, GLEventListener {

	private static boolean BLENDING_ENABLED; // blending on/off
	GLCanvas canvas;
	public final LayeredDisplayData data;
	private int width, height;
	public ICamera camera;
	public SWTOpenGLDisplaySurface displaySurface;
	public final SceneBuffer sceneBuffer;
	public int frame = 0;
	private boolean picking = false;
	public int pickedObjectIndex = -1;
	public AbstractObject currentPickedObject;
	int[] viewport = new int[4];
	double mvmatrix[] = new double[16];
	double projmatrix[] = new double[16];
	public boolean colorPicking = false;
	private boolean highlight = false;
	protected final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	protected double currentAlpha = 1;
	protected int widthOfLayerInPixels;
	protected int heightOfLayerInPixels;
	protected int xOffsetInPixels;
	protected int yOffsetInPixels;
	protected double xRatioBetweenPixelsAndModelUnits;
	protected double yRatioBetweenPixelsAndModelUnits;
	private GLU glu;
	// private GL2 gl;
	private final GLUT glut = new GLUT();
	private Envelope3D ROIEnvelope = null;
	private ModelScene currentScene;
	private volatile boolean inited;
	private GeometryCache cache;
	protected static Map<String, Envelope> envelopes = new ConcurrentHashMap<String, Envelope>();
	// Use to inverse y composaant
	public int yFlag;

	// Global text renderers
	// Does not allow renderers to be created for text bigger than 200 pixels

	Map<String, Map<Integer, Map<Integer, TextRenderer>>> textRenderersCache = new LinkedHashMap();

	public TextRenderer get(final Font font) {
		return get(font.getName(), font.getSize(), font.getStyle());
	}

	public TextRenderer get(final String font, final int s, final int style) {
		int size = s > 150 ? 150 : s;
		if ( size < 6 ) { return null; }
		Map<Integer, Map<Integer, TextRenderer>> map1 = textRenderersCache.get(font);
		if ( map1 == null ) {
			map1 = new HashMap();
			textRenderersCache.put(font, map1);
		}
		Map<Integer, TextRenderer> map2 = map1.get(size);
		if ( map2 == null ) {
			map2 = new HashMap();
			map1.put(size, map2);
		}
		TextRenderer r = map2.get(style);
		if ( r == null ) {
			r = new TextRenderer(new Font(font, style, size), true, false, null, true);
			r.setSmoothing(true);
			r.setUseVertexArrays(true);
			map2.put(style, r);
		}
		return r;
	}

	// private final GLModel chairModel = null;

	public JOGLRenderer(final SWTOpenGLDisplaySurface d) {
		displaySurface = d;
		data = d.getData();
		camera = new CameraArcBall(this);
		sceneBuffer = new SceneBuffer(this);
		yFlag = -1;
	}

	public GLAutoDrawable createDrawable(final Composite parent) {
		GLProfile profile = TextureCache.getSharedContext().getGLProfile();
		GLCapabilities cap = new GLCapabilities(profile);
		cap.setStencilBits(8);
		cap.setDoubleBuffered(true);
		cap.setHardwareAccelerated(true);
		canvas = new GLCanvas(parent, SWT.NONE, cap, null);
		canvas.setSharedAutoDrawable(TextureCache.getSharedContext());
		canvas.setAutoSwapBufferMode(true);
		SWTGLAnimator animator = new SWTGLAnimator(canvas);
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
		// GL2 gl = GLContext.getCurrentGL().getGL2();

		initializeCanvasWithListeners();

		width = drawable.getSurfaceWidth();
		height = drawable.getSurfaceHeight();
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

	public GeometryCache getGeometryCache() {
		if ( cache == null ) {
			cache = new GeometryCache(this);
		}
		return cache;
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
		if ( p1.getX() == -1 && p1.getY() == -1 && p1.getZ() == 1 ) {
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

		if ( data.isInertia() ) {
			camera.doInertia();
		}

		drawScene(gl);

		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

		if ( ROIEnvelope != null ) {
			drawROI(gl);
		}

	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width,
		final int height) {
		// System.out.println("Renderer reshaping to " + arg1 + "," + arg2 + "," + width + " , " + height);
		// Get the OpenGL graphics context
		if ( width <= 0 || height <= 0 ) { return; }
		this.width = width;
		this.height = height;
		GL2 gl = drawable.getContext().getGL().getGL2();
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Enable the model view - any new transformations will affect the model-view matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
		// System.out.println(" Renderer reshaping:" + "model view matrix reset");
		// perspective view
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		// System.out.println(" Renderer reshaping:" + "projection matrix reset");
		// FIXME Update camera as well ??
		// Only if zoomFit... camera.resetCamera(data.getEnvWidth(), data.getEnvHeight(), data.isOutput3D());
		updatePerspective(gl);
		// gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	public final void updatePerspective(final GL2 gl) {
		double aspect = (double) width / (double) (height == 0 ? 1 : height);

		double maxDim = getMaxEnvDim();

		// System.out.println("Aspect = " + aspect);
		if ( !data.isOrtho() ) {
			try {
				double zNear = maxDim / 1000;
				double fW, fH;
				double fovY = 45.0d;
				if ( aspect > 1.0 ) {
					fH = Math.tan(fovY / 360 * Math.PI) * zNear;
					fW = fH * aspect;
				} else {
					fW = Math.tan(fovY / 360 * Math.PI) * zNear;
					fH = fW / aspect;
				}
				gl.glFrustum(-fW, fW, -fH, fH, zNear, maxDim * 10);
			} catch (BufferOverflowException e) {
				System.out.println("Buffer overflow exception");
			}
		} else {
			if ( aspect >= 1.0 ) {
				// maxDim = maxDim/10;
				((GL2ES1) gl).glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
			} else {
				// maxDim = maxDim/10;
				((GL2ES1) gl).glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim, -maxDim);
			}
			gl.glTranslated(0d, 0d, maxDim * 0.05);
		}
		camera.makeGluLookAt(glu);
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
		currentScene.draw(gl, false);

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
		return width * displaySurface.getZoomLevel();
	}

	public double getHeight() {
		return height * displaySurface.getZoomLevel();
	}

	public void updateCameraPosition() {
		if ( data.isCameraLock() ) {
			ILocation cameraPos = data.getCameraPos();
			if ( cameraPos != LayeredDisplayData.getNoChange() ) {
				camera.updatePosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
			}
			ILocation camLookPos = data.getCameraLookPos();
			if ( camLookPos != LayeredDisplayData.getNoChange() ) {
				camera.lookPosition(camLookPos.getX(), camLookPos.getY(), camLookPos.getZ());
			}
			ILocation upVector = data.getCameraUpVector();
			if ( camera.getPhi() < 360 && camera.getPhi() > 180 ) {
				camera.upPosition(0, -1, 0);
			} else {
				camera.upPosition(upVector.getX(), upVector.getY(), upVector.getZ());
			}
			camera.updateSphericalCoordinatesFromLocations();
		}
	}

	public void setPickedObjectIndex(final int pickedObjectIndex) {
		this.pickedObjectIndex = pickedObjectIndex;
		if ( pickedObjectIndex == -1 ) {
			setPicking(false);
		} else if ( pickedObjectIndex == -2 ) {
			displaySurface.selectAgent(null);
			setPicking(false);
		}
	}

	@Override
	public void dispose(final GLAutoDrawable drawable) {
		sceneBuffer.dispose();
	}

	// Use when the rotation button is on.
	public void rotateModel(final GL2 gl) {
		if ( data.isRotationOn() ) {
			frame++;
		}
		if ( frame != 0 ) {
			double env_width = data.getEnvWidth();
			double env_height = data.getEnvHeight();
			gl.glTranslated(env_width / 2, -env_height / 2, 0);
			gl.glRotatef(frame, 0, 0, 1);
			gl.glTranslated(-env_width / 2, +env_height / 2, 0);
		}
	}

	public void drawROI(final GL2 gl) {
		double x1 = ROIEnvelope.getMinX();
		double y1 = -ROIEnvelope.getMinY();
		double x2 = ROIEnvelope.getMaxX();
		double y2 = -ROIEnvelope.getMaxY();

		Double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
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
	@Override
	public void initFor(final IDisplaySurface surface) {
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
	public Rectangle2D drawShape(final IShape shape, final DrawingAttributes attributes) {
		if ( shape == null ) { return null; }
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
		IShape.Type type = shape.getGeometricalType();
		if ( highlight ) {
			attributes.color = GamaColor.getInt(data.getHighlightColor().getRGB());
		}
		sceneBuffer.getSceneToUpdate().addGeometry(shape.getInnerGeometry(), attributes);

		return rect;

	}

	/**
	 * Method drawImage.
	 *
	 * @param img
	 * Image
	 * @param angle
	 * Integer
	 */
	@Override
	public Rectangle2D drawImage(final BufferedImage img, final DrawingAttributes attributes) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
		if ( attributes.size == null ) {
			attributes.size = new GamaPoint();
			attributes.size.x = widthOfLayerInPixels / xRatioBetweenPixelsAndModelUnits;
			attributes.size.y = heightOfLayerInPixels / yRatioBetweenPixelsAndModelUnits;
		}
		sceneBuffer.getSceneToUpdate().addImage(img, attributes);

		if ( attributes.border != null ) {
			drawGridLine(img, attributes.border);
		}
		return rect;
	}

	@Override
	public Rectangle2D drawFile(final GamaFile file, final DrawingAttributes attributes) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }

		if ( file instanceof GamaGeometryFile && !envelopes.containsKey(file.getPath()) ) {
			envelopes.put(file.getPath(), file.computeEnvelope(displaySurface.getDisplayScope()));
		}
		sceneBuffer.getSceneToUpdate().addFile(file, attributes);
		return rect;
	}
	//
	// @Override
	// public Rectangle2D drawFile(final IScope scope, final GamaFile fileName, final Color color,
	// final ILocation locationInModelUnits, final ILocation sizeInModelUnits,
	// final GamaPair<Double, GamaPoint> rotate3D, final GamaPair<Double, GamaPoint> rotate3DInit) {
	// if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
	// GamaPoint location = new GamaPoint(locationInModelUnits);
	// Envelope env = envelopes.get(fileName.getPath());
	// if ( env == null ) {
	// envelopes.put(fileName.getPath(), fileName.computeEnvelope(null));
	// }
	// GamaPoint dimensions = new GamaPoint(sizeInModelUnits);
	// sceneBuffer.getSceneToUpdate().addFile(fileName, scope == null ? null : scope.getAgentScope(), color, 1.0,
	// location, dimensions, rotate3D, rotate3DInit, env);
	// return rect;
	// }

	private Envelope3D getWorldEnvelopeWithZ(final double z) {
		return new Envelope3D(0, data.getEnvWidth(), 0, data.getEnvHeight(), 0, z);
	}

	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] valueMatrix,
		final boolean triangulated, final boolean isGrayScaled, final boolean showText, final GamaColor gridColor,
		final Envelope3D cellSize, final String name) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
		Envelope3D env = getWorldEnvelopeWithZ(1);
		IAgent a = scope.getAgentScope();
		sceneBuffer.getSceneToUpdate().addDEM(valueMatrix, img, a, triangulated, isGrayScaled, showText, env, cellSize,
			name, gridColor);
		/*
		 * This line has been removed to fix the issue 1174
		 * if ( gridColor != null ) {
		 * drawGridLine(img, gridColor);
		 * }
		 */
		return rect;
	}

	public void drawGridLine(final BufferedImage image, final Color lineColor) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return; }
		double stepX, stepY;
		double wRatio = this.data.getEnvWidth() / image.getWidth();
		double hRatio = this.data.getEnvHeight() / image.getHeight();
		GamaColor color = GamaColor.getInt(lineColor.getRGB());
		DrawingAttributes attributes = new DrawingAttributes(null, color, color);
		attributes.setShapeType(IShape.Type.GRIDLINE);
		for ( int i = 0; i < image.getWidth(); i++ ) {
			for ( int j = 0; j < image.getHeight(); j++ ) {
				stepX = (i + 0.5) / image.getWidth() * image.getWidth();
				stepY = (j + 0.5) / image.getHeight() * image.getHeight();
				final Geometry g = GamaGeometryType
					.buildRectangle(wRatio, hRatio, new GamaPoint(stepX * wRatio, stepY * hRatio)).getInnerGeometry();
				sceneBuffer.getSceneToUpdate().addGeometry(g, attributes);
			}
		}
	}

	// Build a dem from a dem.png and a texture.png (used when using the operator dem)
	@Override
	public Rectangle2D drawDEM(final IScope scope, final BufferedImage dem, final BufferedImage texture,
		final Double z_factor) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }

		sceneBuffer.getSceneToUpdate().addDEMFromPNG(texture, dem, getWorldEnvelopeWithZ(z_factor));
		return null;
	}

	@Override
	public Rectangle2D drawString(final String string, final DrawingAttributes attributes) {
		// Multiline: Issue #780
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
		if ( string.contains("\n") ) {
			for ( String s : string.split("\n") ) {
				attributes.location.setY(
					attributes.location.getY() + attributes.font.getSize() * this.yRatioBetweenPixelsAndModelUnits);
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
		xOffsetInPixels = layer.getPositionInPixels().x;
		yOffsetInPixels = layer.getPositionInPixels().y;
		widthOfLayerInPixels = layer.getSizeInPixels().x;
		heightOfLayerInPixels = layer.getSizeInPixels().y;
		xRatioBetweenPixelsAndModelUnits = widthOfLayerInPixels / data.getEnvWidth();
		yRatioBetweenPixelsAndModelUnits = heightOfLayerInPixels / data.getEnvHeight();
		// TODO Correct if and only if the z is given as a percentage
		double currentZLayer = getMaxEnvDim() * layer.getPosition().getZ();

		// get the value of the z scale if positive otherwise set it to 1.
		double z_scale;
		if ( layer.getExtent().getZ() > 0 ) {
			z_scale = layer.getExtent().getZ();
		} else {
			z_scale = 1;
		}

		GamaPoint currentOffset = new GamaPoint(xOffsetInPixels / (getWidth() / data.getEnvWidth()),
			yOffsetInPixels / (getHeight() / data.getEnvHeight()), currentZLayer);
		GamaPoint currentScale =
			new GamaPoint(widthOfLayerInPixels / getWidth(), heightOfLayerInPixels / getHeight(), z_scale);

		ModelScene scene = sceneBuffer.getSceneToUpdate();
		if ( scene != null ) {
			scene.beginDrawingLayer(layer, currentOffset, currentScale, currentAlpha);
		}
	}

	@Override
	public void beginHighlight() {
		highlight = true;
	}

	@Override
	public void endHighlight() {
		highlight = false;
	}

	/**
	 * Method endDrawingLayers()
	 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayers()
	 */
	@Override
	public void endDrawingLayers() {
		sceneBuffer.endUpdatingScene();
	}

	@Override
	public void endDrawingLayer(final ILayer layer) {
		xRatioBetweenPixelsAndModelUnits = getWidth() / data.getEnvWidth();
		yRatioBetweenPixelsAndModelUnits = getHeight() / data.getEnvHeight();
	}

	/**
	 * Method getyRatioBetweenPixelsAndModelUnits()
	 * @see msi.gama.common.interfaces.IGraphics#getyRatioBetweenPixelsAndModelUnits()
	 */
	@Override
	public double getyRatioBetweenPixelsAndModelUnits() {
		return yRatioBetweenPixelsAndModelUnits;
	}

	/**
	 * Method getxRatioBetweenPixelsAndModelUnits()
	 * @see msi.gama.common.interfaces.IGraphics#getxRatioBetweenPixelsAndModelUnits()
	 */
	@Override
	public double getxRatioBetweenPixelsAndModelUnits() {
		return xRatioBetweenPixelsAndModelUnits;
	}

	/**
	 * Method getDisplayWidthInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getDisplayWidthInPixels()
	 */
	@Override
	public int getDisplayWidthInPixels() {
		return (int) Math.round(getWidth());
	}

	/**
	 * Method getDisplayHeightInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getDisplayHeightInPixels()
	 */
	@Override
	public int getDisplayHeightInPixels() {
		return (int) Math.round(getHeight());
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
	//
	// public GL2 getGL() {
	// return gl;
	// }

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
	 * Method getXOffsetInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getXOffsetInPixels()
	 */
	@Override
	public double getXOffsetInPixels() {
		return xOffsetInPixels;
	}

	/**
	 * Method getYOffsetInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getYOffsetInPixels()
	 */
	@Override
	public double getYOffsetInPixels() {
		return yOffsetInPixels;
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
	public Envelope getEnvelopeFor(final String path) {
		return envelopes.get(path);
	}

	/**
	 * @return
	 */
	public float getLineWidth() {
		return GamaPreferences.CORE_LINE_WIDTH.getValue().floatValue();
	}

}
