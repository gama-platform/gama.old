package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import msi.gama.common.util.GuiUtils;
import msi.gama.jogl.JOGLAWTDisplaySurface;
import msi.gama.jogl.scene.*;
import msi.gama.jogl.utils.Camera.*;
import msi.gama.jogl.utils.Camera.Arcball.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.OutputSynchronizer;
import utils.GLUtil;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

public class JOGLAWTGLRenderer implements GLEventListener {

	public GLU glu;
	public GL gl;
	public GLUT glut;

	// ///Static members//////
	private static final boolean USE_VERTEX_ARRAY = false;
	private static final int REFRESH_FPS = 30;

	private static boolean BLENDING_ENABLED; // blending on/off
	private static boolean IS_LIGHT_ON;

	public final FPSAnimator animator;
	private GLContext context;
	public GLCanvas canvas;

	private int width, height;
	public final double env_width;
	public final double env_height;

	// Camera
	public AbstractCamera camera;
	public MyGraphics graphicsGLUtils;

	// Use to test and display basic opengl shape and primitive
	public MyGLToyDrawer myGLDrawer;

	// Lighting
	private Color ambientLightValue;
	private Color diffuseLightValue;
	// Blending

	public JOGLAWTDisplaySurface displaySurface;
	private ModelScene scene;

	// Use multiple view port
	public final boolean multipleViewPort = false;
	// Display model a a 3D Cube
	private final boolean CubeDisplay = false;
	// Handle Shape file
	public ShapeFileReader myShapeFileReader;
	// Arcball
	private ArcBall arcBall;
	// use glut tesselation or JTS tesselation
	// facet "tesselation"
	private boolean useTessellation = true;
	// facet "inertia"
	public boolean inertia = false;
	// facet "inertia"
	private boolean stencil = false;
	// facet "drawEnv"
	private boolean drawEnv = false;
	// facet "show_fps"
	private boolean showFPS = false;
	// facet "z_fighting"
	private boolean z_fighting = false;

	public boolean triangulation = false;

	public boolean drawAxes = true;
	// Display or not the triangle when using triangulation (useTessellation = false)
	private boolean polygonMode = true;
	// Show JTS (GAMA) triangulation
	public boolean JTSTriangulation = false;
	// is in picking mode ?
	private boolean picking = false;

	// ROI Coordionates (x1,y1,x2,y2)
	public ArrayList<Integer> roi_List = new ArrayList<Integer>();

	public int pickedObjectIndex = -1;
	public ISceneObject currentPickedObject;
	// private int antialiasing = GL_NEAREST;

	public int frame = 0;

	int[] viewport = new int[4];
	double mvmatrix[] = new double[16];
	double projmatrix[] = new double[16];
	public Vector3D worldCoordinates = new Vector3D();
	public GamaPoint roiCenter = new GamaPoint(0, 0);

	private double startTime = 0;
	private int frameCount = 0;
	private double currentTime = 0;
	private double previousTime = 0;
	public float fps = 00.00f;

	public JOGLAWTGLRenderer(final JOGLAWTDisplaySurface d) {
		// Enabling the stencil buffer
		final GLCapabilities cap = new GLCapabilities();
		cap.setStencilBits(8);
		// Initialize the user camera
		displaySurface = d;
		env_width = d.getEnvWidth();
		env_height = d.getEnvHeight();
		camera = new CameraArcBall(this);
		myGLDrawer = new MyGLToyDrawer();
		canvas = new GLCanvas(cap);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(camera);
		canvas.addMouseListener(camera);
		canvas.addMouseMotionListener(camera);
		canvas.addMouseWheelListener(camera);
		canvas.setVisible(true);
		canvas.setFocusable(true); // To receive key event
		canvas.requestFocusInWindow();
		animator = new FPSAnimator(canvas, REFRESH_FPS, true);
		// displaySurface = d;
		// env_width = d.getEnvWidth();
		// env_height = d.getEnvHeight();
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		startTime = System.currentTimeMillis();

		width = drawable.getWidth();
		height = drawable.getHeight();
		gl = drawable.getGL();
		glu = new GLU();
		glut = new GLUT();
		setContext(drawable.getContext());
		arcBall = new ArcBall(width, height);

		// Set background color

		gl.glClearColor(displaySurface.getBgColor().getRed() / 255.0f, displaySurface.getBgColor().getGreen() / 255.0f,
			displaySurface.getBgColor().getBlue() / 255.0f, 1.0f);
		// Enable smooth shading, which blends colors nicely, and smoothes out lighting.
		GLUtil.enableSmooth(gl);

		// Perspective correction
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		GLUtil.enableDepthTest(gl);

		// Set up the lighting for Light-1
		GLUtil.InitializeLighting(gl, glu, (float) displaySurface.getEnvWidth(), (float) displaySurface.getEnvHeight(),
			ambientLightValue, diffuseLightValue);

		// PolygonMode (Solid or lines)
		if ( polygonMode ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		} else {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		}
		// Blending control
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL_BLEND);
		// gl.glDisable(GL_DEPTH_TEST);
		// FIXME : should be turn on only if need (if we draw image)
		// problem when true with glutBitmapString
		JOGLAWTGLRenderer.BLENDING_ENABLED = true;
		IS_LIGHT_ON = true;

		camera.UpdateCamera(gl, glu, width, height);
		scene = new ModelScene(this);
		graphicsGLUtils = new MyGraphics(this);

		OutputSynchronizer.decInitializingViews(this.displaySurface.getOutputName());

	}

	@Override
	public void display(final GLAutoDrawable drawable) {

		if ( !displaySurface.isPaused() ) {
			gl = drawable.getGL();
			setContext(drawable.getContext());

			width = drawable.getWidth();
			height = drawable.getHeight();

			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
			gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);

			// Clear the screen and the depth buffer
			gl.glClearDepth(1.0f);
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

			gl.glMatrixMode(GL.GL_PROJECTION);
			// Reset the view (x, y, z axes back to normal)
			gl.glLoadIdentity();

			camera.UpdateCamera(gl, glu, width, height);

			if ( IS_LIGHT_ON ) {
				gl.glEnable(GL_LIGHTING);
			} else {
				gl.glDisable(GL_LIGHTING);
			}

			// Draw Diffuse light as yellow sphere
			// GLUtil.DrawDiffuseLights(gl, glu,getMaxEnvDim()/10);

			// FIXME: Now the background is not updated but it should to have a night effect.
			// Set background color
			// gl.glClearColor(ambiantLightValue.floatValue(), ambiantLightValue.floatValue(),
			// ambiantLightValue.floatValue(), 1.0f);
			// The ambiant_light is always reset in case of dynamic lighting.
			GLUtil.UpdateAmbiantLight(gl, glu, ambientLightValue);
			GLUtil.UpdateDiffuseLight(gl, glu, diffuseLightValue);

			// Show triangulated polygon or not (trigger by GAMA)
			/*
			 * if ( !triangulation ) {
			 * gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			 * } else {
			 * gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			 * }
			 */

			// Blending control

			if ( BLENDING_ENABLED ) {
				gl.glEnable(GL_BLEND); // Turn blending on
			} else {
				gl.glDisable(GL_BLEND); // Turn blending off
				if ( !getStencil() ) {
					gl.glEnable(GL_DEPTH_TEST);
				} else {
					gl.glEnable(GL_STENCIL_TEST);
				}
			}

			// Use polygon offset for a better edges rendering
			// (http://www.glprogramming.com/red/chapter06.html#name4)
			// gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			// gl.glPolygonOffset(1, 1);

			// gl.glDisable(GL_DEPTH_TEST);

			this.rotateModel();

			if ( getInertia() ) {
				camera.arcBallInertia();
				camera.moveInertia();
			}

			this.drawScene();

			// this.DrawShapeFile();
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glPopMatrix();

			// ROI drawer
			if ( this.displaySurface.selectRectangle ) {
				DrawROI();
			}

			// Show fps for performance mesures
			if ( this.getShowFPS() ) {
				CalculateFrameRate();
				gl.glDisable(GL_BLEND);
				gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
				gl.glRasterPos3d(-30, 30, 0);
				gl.glScaled(8.0d, 8.0d, 8.0d);
				glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "fps : " + fps);
				gl.glScaled(0.125d, 0.125d, 0.125d);
				gl.glEnable(GL_BLEND);
			}

			// Show triangulated polygon or not (trigger by GAMA)
			if ( !triangulation ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			}
		}
	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int arg3, final int arg4) {
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		if ( height == 0 ) {
			height = 1; // prevent divide by zero
		}
		final float aspect = (float) width / height;
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Enable the model view - any new transformations will affect the
		// model-view matrix
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
		// perspective view
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, aspect, 0.1f, camera.getMaxDim() * 100);
		if ( camera != null ) {
			glu.gluLookAt(camera.getPosition().getX(), camera.getPosition().getY(), camera.getPosition().getZ(), camera
				.getTarget().getX(), camera.getTarget().getY(), camera.getTarget().getZ(), camera.getUpVector().getX(),
				camera.getUpVector().getY(), camera.getUpVector().getZ());
		}
		arcBall.setBounds(width, height);
	}

	@Override
	public void displayChanged(final GLAutoDrawable arg0, final boolean arg1, final boolean arg2) {}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * @param picking
	 * 
	 */
	public void drawModel() {
		scene.draw(this, isPicking() || currentPickedObject != null, drawAxes, drawEnv);
	}

	public void drawScene() {
		gl.glViewport(0, 0, width, height);
		if ( isPicking() ) {
			this.drawPickableObjects();
		} else {
			if ( CubeDisplay ) {
				drawCubeDisplay((float) env_width);

			} else {
				this.drawModel();
			}
		}
	}

	private void drawCubeDisplay(final float width) {
		final float envMaxDim = width;
		this.drawModel();
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		this.drawModel();
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		this.drawModel();
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		this.drawModel();
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		gl.glRotatef(-90, 1, 0, 0);
		gl.glTranslatef(0, envMaxDim, 0);
		this.drawModel();
		gl.glTranslatef(0, -envMaxDim, 0);
		gl.glRotatef(90, 1, 0, 0);
		gl.glRotatef(90, 1, 0, 0);
		gl.glTranslatef(0, 0, envMaxDim);
		this.drawModel();
		gl.glTranslatef(0, 0, -envMaxDim);
		gl.glRotatef(-90, 1, 0, 0);

	}

	public void switchCamera() {
		canvas.removeKeyListener(camera);
		canvas.removeMouseListener(camera);
		canvas.removeMouseMotionListener(camera);
		canvas.removeMouseWheelListener(camera);

		if ( displaySurface.switchCamera ) {
			camera = new FreeFlyCamera(this);
		} else {
			camera = new CameraArcBall(this);
		}

		canvas.addKeyListener(camera);
		canvas.addMouseListener(camera);
		canvas.addMouseMotionListener(camera);
		canvas.addMouseWheelListener(camera);

	}

	int minAntiAliasing = GL_NEAREST; /* GL_NEAREST_MIPMAP_NEAREST; */
	int magAntiAliasing = GL_NEAREST;

	public void setAntiAliasing(final boolean antialias) {
		// antialiasing = antialias ? GL_LINEAR : GL_NEAREST;
		minAntiAliasing = antialias ? GL_LINEAR : GL_NEAREST; /* GL_LINEAR_MIPMAP_LINEAR : GL_NEAREST_MIPMAP_NEAREST; */
		magAntiAliasing = antialias ? GL_LINEAR : GL_NEAREST;
	}

	public MyTexture createTexture(final BufferedImage image, final boolean isDynamic) {
		// Create a OpenGL Texture object from (URL, mipmap, file suffix)
		// need to have an opengl context valide
		this.getContext().makeCurrent();
		Texture texture;
		try {
			texture = TextureIO.newTexture(image, false /* true for mipmapping */);
		} catch (final GLException e) {
			return null;
		}
		texture.setTexParameteri(GL_TEXTURE_MIN_FILTER, minAntiAliasing);
		texture.setTexParameteri(GL_TEXTURE_MAG_FILTER, magAntiAliasing);
		final MyTexture curTexture = new MyTexture();
		curTexture.texture = texture;
		curTexture.isDynamic = isDynamic;
		// GuiUtils.debug("JOGLAWTGLRenderer.createTexture for " + image);
		this.getContext().release();
		return curTexture;
	}

	public void drawPickableObjects() {
		if ( camera.beginPicking(gl) ) {
			drawModel();
			setPickedObjectIndex(camera.endPicking(gl));
		}
		drawModel();
	}

	public BufferedImage getScreenShot() {
		BufferedImage img = null;
		if ( getContext() != null ) {
			try {
				this.getContext().makeCurrent();
				img = Screenshot.readToBufferedImage(width, height);
				this.getContext().release();
			} catch (GLException e) {
				GuiUtils.debug("Warning: No OpenGL context available");
			}
		} else {}
		return img;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public GLContext getContext() {
		return context;
	}

	public void setContext(final GLContext context) {
		this.context = context;
	}

	public void setAmbientLightValue(final Color ambientLightValue) {
		this.ambientLightValue = ambientLightValue;
	}

	public void setDiffuseLightValue(final Color diffuseLightValue) {
		this.diffuseLightValue = diffuseLightValue;
	}

	public void setPolygonMode(final boolean polygonMode) {
		this.polygonMode = polygonMode;
	}

	public boolean getTessellation() {
		return useTessellation;
	}

	public void setTessellation(final boolean tess) {
		this.useTessellation = tess;
	}

	public void setInertia(final boolean iner) {
		this.inertia = iner;
	}

	public boolean getInertia() {
		return inertia;
	}

	public void setStencil(final boolean st) {
		this.stencil = st;
	}

	public boolean getStencil() {
		return stencil;
	}

	public void setZFighting(final boolean z) {
		this.z_fighting = z;
	}

	public boolean getZFighting() {
		return z_fighting;
	}

	public void setShowFPS(final boolean fps) {
		this.showFPS = fps;
	}

	public boolean getShowFPS() {
		return showFPS;
	}

	public void setDrawEnv(final boolean denv) {
		this.drawEnv = denv;
	}

	public boolean getDrawEnv() {
		return drawEnv;
	}

	public void setCameraPosition(final ILocation cameraPos) {
		if ( cameraPos.equals(new GamaPoint(-1, -1, -1)) ) {// No change;
		} else {
			camera.updatePosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
		}
	}

	public void setCameraLookPosition(final ILocation camLookPos) {
		if ( camLookPos.equals(new GamaPoint(-1, -1, -1)) ) {// No change
		} else {
			camera.lookPosition(camLookPos.getX(), camLookPos.getY(), camLookPos.getZ());
		}
	}

	public void setCameraUpVector(final ILocation upVector) {
		camera.setUpVector(upVector);
	}

	public double getMaxEnvDim() {
		return env_width > env_height ? env_width : env_height;
	}

	public void setPickedObjectIndex(final int pickedObjectIndex) {
		this.pickedObjectIndex = pickedObjectIndex;
		if ( pickedObjectIndex == -1 ) {
			setPicking(false);
		} else if ( pickedObjectIndex == -2 ) {
			displaySurface.selectAgents(null, 0);
			setPicking(false);
		}
	}

	public void cleanListsAndVertices() {
		if ( USE_VERTEX_ARRAY ) {
			graphicsGLUtils.vertexArrayHandler.DeleteVertexArray();
		}
	}

	public ModelScene getScene() {
		return scene;
	}

	public void dispose() {
		scene.dispose();
	}

	public void CalculateFrameRate() {

		// Increase frame count
		frameCount++;

		// Get the number of milliseconds since display started
		currentTime = System.currentTimeMillis() - startTime;

		// Calculate time passed
		int timeInterval = (int) (currentTime - previousTime);
		if ( timeInterval > 1000 ) {
			// calculate the number of frames per second
			fps = frameCount / (timeInterval / 1000.0f);

			// Set time
			previousTime = currentTime;

			// Reset frame count
			frameCount = 0;
		}

	}

	// Use when the rotation button is on.
	public void rotateModel() {
		if ( this.displaySurface.rotation ) {
			frame++;
		}
		if ( frame != 0 ) {
			gl.glTranslated(env_width / 2, -env_height / 2, 0);
			gl.glRotatef(frame, 0, 0, 1);
			gl.glTranslated(-env_width / 2, +env_height / 2, 0);
		}
	}

	// ////////////////////////ROI HANDLER ////////////////////////////////////
	public Point2D.Double getRealWorldPointFromWindowPoint(final Point windowPoint) {
		if ( glu == null ) { return null; }
		int realy = 0;// GL y coord pos
		double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords

		int x = (int) windowPoint.getX(), y = (int) windowPoint.getY();

		realy = viewport[3] - y;

		glu.gluUnProject(x, realy, 0.1, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		Vector3D v1 = new Vector3D(wcoord[0], wcoord[1], wcoord[2]);

		glu.gluUnProject(x, realy, 0.9, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		Vector3D v2 = new Vector3D(wcoord[0], wcoord[1], wcoord[2]);

		Vector3D v3 = v2.subtract(v1);
		v3.normalize();
		float distance = (float) (camera.getPosition().getZ() / Vector3D.dotProduct(new Vector3D(0.0, 0.0, -1.0), v3));
		worldCoordinates = camera.getPosition().add(v3.scalarMultiply(distance));

		final Point2D.Double realWorldPoint = new Point2D.Double(worldCoordinates.x, worldCoordinates.y);
		return realWorldPoint;
	}

	public Point getIntWorldPointFromWindowPoint(final Point windowPoint) {
		Point2D.Double p = getRealWorldPointFromWindowPoint(windowPoint);
		return new Point((int) p.x, (int) p.y);
	}

	public ArrayList<Integer> DrawROI() {
		if ( camera.enableROIDrawing ) {
			roi_List.clear();
			Point windowPressedPoint = new Point(camera.lastxPressed, camera.lastyPressed);
			Point realPressedPoint = getIntWorldPointFromWindowPoint(windowPressedPoint);

			Point windowmousePositionPoint = new Point(camera.mousePosition.x, camera.mousePosition.y);
			Point realmousePositionPoint = getIntWorldPointFromWindowPoint(windowmousePositionPoint);

			myGLDrawer.DrawROI(gl, realPressedPoint.x, -realPressedPoint.y, realmousePositionPoint.x,
				-realmousePositionPoint.y);

			roi_List.add(0, realPressedPoint.x);
			roi_List.add(1, realPressedPoint.y);
			roi_List.add(2, realmousePositionPoint.x);
			roi_List.add(3, realmousePositionPoint.y);

			int roiWidth = (int) Math.abs(roi_List.get(0) + env_width / 2 - (roi_List.get(2) + env_width / 2));
			int roiHeight = (int) Math.abs(roi_List.get(1) - env_height / 2 - (roi_List.get(3) - env_height / 2));

			if ( !this.displaySurface.switchCamera ) {

				if ( roi_List.get(0) < roi_List.get(2) && roi_List.get(1) > roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x - roiWidth / 2, worldCoordinates.y + roiHeight / 2);
				} else if ( roi_List.get(0) < roi_List.get(2) && roi_List.get(1) < roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x - roiWidth / 2, worldCoordinates.y - roiHeight / 2);
				} else if ( roi_List.get(0) > roi_List.get(2) && roi_List.get(1) < roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x + roiWidth / 2, worldCoordinates.y - roiHeight / 2);
				} else if ( roi_List.get(0) > roi_List.get(2) && roi_List.get(1) > roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x + roiWidth / 2, worldCoordinates.y + roiHeight / 2);
				}
			} else {
				if ( roi_List.get(0) == roi_List.get(2) && roi_List.get(1) == roi_List.get(3) ) {
					System.out.println(" passe par le mouse click");
					roiCenter.setLocation(worldCoordinates.x, worldCoordinates.y);

				} else if ( roi_List.get(0) < roi_List.get(2) && roi_List.get(1) > roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x - roiWidth / 2, worldCoordinates.y + roiHeight / 2);
				} else if ( roi_List.get(0) < roi_List.get(2) && roi_List.get(1) < roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x - roiWidth / 2, worldCoordinates.y - roiHeight / 2);
				} else if ( roi_List.get(0) > roi_List.get(2) && roi_List.get(1) < roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x + roiWidth / 2, worldCoordinates.y - roiHeight / 2);
				} else if ( roi_List.get(0) > roi_List.get(2) && roi_List.get(1) > roi_List.get(3) ) {
					roiCenter.setLocation(worldCoordinates.x + roiWidth / 2, worldCoordinates.y + roiHeight / 2);
				}
			}

		}

		return roi_List;

	}

	public void ROIZoom() {
		int roiWidth = (int) Math.abs(roi_List.get(0) + env_width / 2 - (roi_List.get(2) + env_width / 2));
		int roiHeight = (int) Math.abs(roi_List.get(1) - env_height / 2 - (roi_List.get(3) - env_height / 2));

		double maxDim;

		if ( !this.displaySurface.switchCamera ) {
			if ( roiWidth > roiHeight ) {
				camera.setRadius(roiWidth * 1.5);
			} else {
				camera.setRadius(roiHeight * 1.5);
			}

			camera.setTarget(new Vector3D(roiCenter.x, roiCenter.y, 0.0));

			camera.rotation();
		} else {
			if ( roiWidth > roiHeight ) {
				maxDim = roiWidth * 1.5;
			} else {
				maxDim = roiHeight * 1.5;
			}

			camera.setPosition(new Vector3D(roiCenter.x, roiCenter.y, maxDim));

			camera.vectorsFromAngles();
		}
	}

	public void setPicking(final boolean value) {
		picking = value;
		// GuiUtils.debug("JOGLAWTDisplaySurface.setPicking " + value);
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
}
