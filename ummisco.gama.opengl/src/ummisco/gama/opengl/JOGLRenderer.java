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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.*;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaGeometryType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import ummisco.gama.opengl.camera.*;
import ummisco.gama.opengl.scene.*;
import ummisco.gama.opengl.utils.GLUtilLight;
import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.swt.GLCanvas;
import com.vividsolutions.jts.geom.Geometry;

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
	public ISceneObject currentPickedObject;
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
	private GL2 gl;
	private Envelope3D ROIEnvelope = null;
	private ModelScene currentScene;
	private volatile boolean inited;

	public JOGLRenderer(final SWTOpenGLDisplaySurface d) {
		displaySurface = d;
		data = d.getData();
		camera = new CameraArcBall(this);
		sceneBuffer = new SceneBuffer(this);
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

	public void defineROI(final Point extent) {
		GamaPoint end = getRealWorldPointFromWindowPoint(extent);
		// end.y = -end.y;
		if ( ROIEnvelope != null ) {
			ROIEnvelope.expandToInclude(end);
		} else {
			ROIEnvelope = new Envelope3D();
			ROIEnvelope.init(end);
		}
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

		GuiUtils.asyncRun(new Runnable() {

			@Override
			public void run() {
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
		gl = GLContext.getCurrentGL().getGL2();
		initializeCanvasWithListeners();

		width = drawable.getSurfaceWidth();
		height = drawable.getSurfaceHeight();
		updateCameraPosition();
		System.out.println("Renderer initializing to " + width + " , " + height + " with drawable: " + drawable);

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
		updatePerspective();
		// We mark the renderer as inited
		inited = true;
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
		updatePerspective();
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
	public void
		reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width, final int height) {
		System.out.println("Renderer reshaping to " + arg1 + "," + arg2 + "," + width + " , " + height);
		// Get the OpenGL graphics context
		if ( width <= 0 || height <= 0 ) { return; }
		this.width = width;
		this.height = height;
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Enable the model view - any new transformations will affect the model-view matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
		// System.out.println("	Renderer reshaping:" + "model view matrix reset");
		// perspective view
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		// System.out.println("	Renderer reshaping:" + "projection matrix reset");
		// FIXME Update camera as well ??
		// Only if zoomFit... camera.resetCamera(data.getEnvWidth(), data.getEnvHeight(), data.isOutput3D());
		updatePerspective();
		// gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	public final void updatePerspective() {
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
				((GL2ES1) gl).glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim, -maxDim);
			} else {
				((GL2ES1) gl).glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim, -maxDim);
			}
			gl.glTranslated(0d, 0d, maxDim * 1.5);
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
		if ( isPicking() ) {
			this.drawPickableObjects(gl, currentScene);
		} else {
			if ( data.isCubeDisplay() ) {
				drawCubeDisplay(gl, currentScene);

			} else {
				this.drawModel(gl, currentScene);
			}
		}

	}

	public void drawModel(final GL2 gl, final ModelScene scene) {
		scene.draw(gl, isPicking() || currentPickedObject != null);
	}

	public void switchCamera() {
		GuiUtils.asyncRun(new Runnable() {

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

		GuiUtils.asyncRun(new Runnable() {

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

	public void drawPickableObjects(final GL2 gl, final ModelScene scene) {
		if ( camera.beginPicking(gl) ) {
			drawModel(gl, scene);
			setPickedObjectIndex(camera.endPicking(gl));
		}
		drawModel(gl, scene);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void updateCameraPosition() {
		if(data.isCameraLock()){
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

	public boolean isPicking() {
		return picking;
	}

	// This method is normally called either when the graphics is created or when the output is changed
	@Override
	public void initFor(final IDisplaySurface surface) {
		System.out.println("Call of initFor on renderer");
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
	public Rectangle2D drawGamaShape(final IScope scope, final IShape shape, final Color c, final boolean fill,
		final Color border, final boolean rounded) {
		if ( shape == null ) { return null; }
		Double depth = 0d;
		java.util.List<BufferedImage> textures = null;
		IShape.Type type = shape.getGeometricalType();
		java.util.List<Double> ratio = new ArrayList<Double>();
		java.util.List<GamaColor> colors = new ArrayList<GamaColor>();
		final ITopology topo = scope.getTopology();
		if ( shape.hasAttribute(IShape.DEPTH_ATTRIBUTE) ) {
			depth = Cast.asFloat(scope, shape.getAttribute(IShape.DEPTH_ATTRIBUTE));
		}
		if ( shape.hasAttribute(IShape.TEXTURE_ATTRIBUTE) ) {
			java.util.List<String> textureNames = Cast.asList(scope, shape.getAttribute(IShape.TEXTURE_ATTRIBUTE));
			textures = new ArrayList();
			for ( String s : textureNames ) {
				BufferedImage image;
				try {
					image = ImageUtils.getInstance().getImageFromFile(scope, s);
					textures.add(image);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
		if ( shape.hasAttribute(IShape.RATIO_ATTRIBUTE) ) {
			ratio = Cast.asList(scope, shape.getAttribute(IShape.RATIO_ATTRIBUTE));
		}
		if ( shape.hasAttribute(IShape.COLOR_LIST_ATTRIBUTE) ) {
			colors = Cast.asList(scope, shape.getAttribute(IShape.COLOR_LIST_ATTRIBUTE));
		}
		final Color color = highlight ? data.getHighlightColor() : c;
		if ( topo != null && topo.isTorus() ) {
			java.util.List<Geometry> geoms = topo.listToroidalGeometries(shape.getInnerGeometry());
			Geometry world = scope.getSimulationScope().getInnerGeometry();
			for ( Geometry g : geoms ) {
				Geometry intersect = world.intersection(g);
				if ( !intersect.isEmpty() ) {
					drawSingleShape(scope, intersect, color, fill, border, null, rounded, depth,
						msi.gama.common.util.GeometryUtils.getTypeOf(intersect), textures, ratio, colors);
				}
			}
		} else {
			drawSingleShape(scope, shape.getInnerGeometry(), color, fill, border, null, rounded, depth, type, textures,
				ratio, colors);
		}

		// Add a geometry with a depth and type coming from Attributes
		return rect;
	}

	private void drawSingleShape(final IScope scope, final Geometry geom, final Color color, final boolean fill,
		final Color border, final Integer angle, final boolean rounded, final Double depth, final IShape.Type type,
		final java.util.List<BufferedImage> textures, final java.util.List<Double> ratio,
		final java.util.List<GamaColor> colors) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return; }
		sceneBuffer.getSceneToUpdate().addGeometry(geom, scope.getAgentScope(), color, fill, border,
			textures == null || textures.isEmpty() ? false : true, textures, angle, depth.doubleValue(), rounded, type,
			ratio, colors);

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
	public Rectangle2D drawImage(final IScope scope, final BufferedImage img, final ILocation locationInModelUnits,
		final ILocation sizeInModelUnits, final Color gridColor, final Double angle, final boolean isDynamic,
		final String name) {

		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
		GamaPoint location = new GamaPoint(locationInModelUnits);
		GamaPoint dimensions = new GamaPoint(sizeInModelUnits);
		if ( sizeInModelUnits == null ) {
			dimensions.x = widthOfLayerInPixels / xRatioBetweenPixelsAndModelUnits;
			dimensions.y = heightOfLayerInPixels / yRatioBetweenPixelsAndModelUnits;
		}
		sceneBuffer.getSceneToUpdate().addImage(img, scope == null ? null : scope.getAgentScope(), location,
			dimensions, angle, isDynamic, name);

		if ( gridColor != null ) {
			drawGridLine(img, gridColor/* , name */);
		}
		return rect;
	}

	private Envelope3D getWorldEnvelopeWithZ(final double z) {
		return new Envelope3D(0, data.getEnvWidth(), 0, data.getEnvHeight(), 0, z);
	}

	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] valueMatrix,
		final boolean textured, final boolean triangulated, final boolean isGrayScaled, final boolean showText,
		final Color gridColor, final double cellSize, final String name) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
		Envelope3D env = getWorldEnvelopeWithZ(1);
		IAgent a = scope.getAgentScope();
		sceneBuffer.getSceneToUpdate().addDEM(valueMatrix, img, a, textured, triangulated, isGrayScaled, showText, env,
			cellSize, name, gridColor);
		/* This line has been removed to fix the issue 1174
		 * if ( gridColor != null ) {
			drawGridLine(img, gridColor);
		}*/
		return rect;
	}

	public void drawGridLine(final BufferedImage image, final Color lineColor) {
		if ( sceneBuffer.getSceneToUpdate() == null ) { return; }
		double stepX, stepY;
		double wRatio = this.data.getEnvWidth() / image.getWidth();
		double hRatio = this.data.getEnvHeight() / image.getHeight();
		for ( int i = 0; i < image.getWidth(); i++ ) {
			for ( int j = 0; j < image.getHeight(); j++ ) {
				stepX = (i + 0.5) / image.getWidth() * image.getWidth();
				stepY = (j + 0.5) / image.getHeight() * image.getHeight();
				final Geometry g =
					GamaGeometryType.buildRectangle(wRatio, hRatio, new GamaPoint(stepX * wRatio, stepY * hRatio))
						.getInnerGeometry();
				sceneBuffer.getSceneToUpdate().addGeometry(g, null, lineColor, false, lineColor, false, null, 0, 0,
					false, IShape.Type.GRIDLINE, null, null);
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

	/**
	 * Method drawChart.
	 * 
	 * @param chart
	 *            JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final IScope scope, final BufferedImage chart, final Double z) {
		return drawImage(scope, chart, new GamaPoint(0, 0), null, null, 0d, true, "chart");
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
	public Rectangle2D drawString(final String string, final Color stringColor, final ILocation locationInModelUnits,
		final Double heightInModelUnits, final Font font, final Double angle, final Boolean bitmap) {
		GamaPoint location = new GamaPoint(locationInModelUnits).yNegated();
		Integer size;
		Double sizeInModelUnits;
		if ( sceneBuffer.getSceneToUpdate() == null ) { return null; }
		if ( heightInModelUnits == null ) {
			size = heightOfLayerInPixels;
			sizeInModelUnits = getHeight() / data.getEnvHeight() * size;
		} else {
			sizeInModelUnits = heightInModelUnits;
			size = (int) (getHeight() / data.getEnvHeight() * sizeInModelUnits);
		}
		sceneBuffer.getSceneToUpdate().addString(string, location, size, sizeInModelUnits, stringColor, font.getName(),
			font.getStyle(), angle, bitmap);
		return null;
	}

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		// setBackground(bgColor);
		setOpacity(opacity);
	}

	/**
	 * Each new step the Z value of the first layer is set to 0.
	 */
	@Override
	public void beginDrawingLayers() {
		// System.out.println("====> UPDATING BACK SCENE Thread " + Thread.currentThread().getName());
		while (!inited) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
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

		GamaPoint currentOffset =
			new GamaPoint(xOffsetInPixels / (getWidth() / data.getEnvWidth()), yOffsetInPixels /
				(getHeight() / data.getEnvHeight()), currentZLayer);
		GamaPoint currentScale =
			new GamaPoint(widthOfLayerInPixels / (double) getWidth(), heightOfLayerInPixels / (double) getHeight(),
				z_scale);

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
		return getWidth();
	}

	/**
	 * Method getDisplayHeightInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getDisplayHeightInPixels()
	 */
	@Override
	public int getDisplayHeightInPixels() {
		return getHeight();
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

	public GL2 getGL() {
		return gl;
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

	public void drawCubeDisplay(final GL2 gl, final ModelScene scene) {
		final float envMaxDim = (float) data.getEnvWidth();
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		drawModel(gl, scene);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		drawModel(gl, scene);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		drawModel(gl, scene);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		drawModel(gl, scene);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		gl.glRotatef(-90, 1, 0, 0);
		gl.glTranslatef(0, envMaxDim, 0);
		drawModel(gl, scene);
		gl.glTranslatef(0, -envMaxDim, 0);
		gl.glRotatef(90, 1, 0, 0);
		gl.glRotatef(90, 1, 0, 0);
		gl.glTranslatef(0, 0, envMaxDim);
		drawModel(gl, scene);
		gl.glTranslatef(0, 0, -envMaxDim);
		gl.glRotatef(-90, 1, 0, 0);
	}

}
