package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.GLU;
import msi.gama.gui.swt.swing.OutputSynchronizer;
import msi.gama.jogl.JOGLAWTDisplaySurface;
import msi.gama.jogl.scene.*;
import msi.gama.jogl.utils.Camera.*;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.GAMA;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;


public class JOGLAWTGLRenderer implements GLEventListener {

	public GLU glu;
	public GL2 gl;
	public GLUT glut;

	// ///Static members//////
	// private static final boolean USE_VERTEX_ARRAY = false;
	private static final int REFRESH_FPS = 30;

	private static boolean BLENDING_ENABLED; // blending on/off
	private static boolean IS_LIGHT_ON;

	public final FPSAnimator animator;
	private GLContext context;
	public GLCanvas canvas;
	public GLProfile profile;

	private int width, height;

	// Camera
	public ICamera camera;

	// Lighting
	private Color ambientLightValue;
	private Color diffuseLightValue;
	private GamaPoint diffuseLightPosition;

	public JOGLAWTDisplaySurface displaySurface;
	private ModelScene scene;
	public int frame = 0;
	// facet "tesselation"
	private boolean useTessellation = true;
	// facet "inertia"
	private boolean inertia = false;
	// facet "drawEnv"
	private boolean drawEnv = false;
	// facet "show_fps"
	private boolean showFPS = false;
	// facet "z_fighting"
	private boolean z_fighting = false;
	// preference "drawNormal"
	private boolean drawNormal = false;
	// Display model a a 3D Cube
	private boolean cubeDisplay = true;

	public boolean triangulation = false;

	public boolean computeNormal = true;

	public boolean drawDiffuseLight = true;
	
	public boolean isLightOn = true;

	public boolean drawAxes = true;
	// Display or not the triangle when using triangulation (useTessellation = false)
	private boolean polygonMode = true;
	// Show JTS (GAMA) triangulation
	public boolean JTSTriangulation = false;
	// is in picking mode ?
	private boolean picking = false;

	public int pickedObjectIndex = -1;
	public ISceneObject currentPickedObject;

	int[] viewport = new int[4];
	double mvmatrix[] = new double[16];
	double projmatrix[] = new double[16];

	public boolean autoSwapBuffers = false;
	public boolean disableManualBufferSwapping;
	public boolean colorPicking = false;

	public JOGLAWTGLRenderer(final JOGLAWTDisplaySurface d) {
		// Enabling the stencil buffer
		profile = GLProfile.getDefault();
		GLCapabilities cap = new GLCapabilities(profile);
		cap.setStencilBits(8);
		displaySurface = d;
		camera = new CameraArcBall(this);
		canvas = new GLCanvas(cap);
		canvas.setAutoSwapBufferMode(autoSwapBuffers);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(camera);
		canvas.addMouseListener(camera);
		canvas.addMouseListener(d.getEventMouse());
		canvas.addMouseMotionListener(camera);
		canvas.addMouseWheelListener(camera);
		canvas.setVisible(true);
		canvas.setFocusable(true); // To receive key event
		canvas.requestFocusInWindow();
		animator = new FPSAnimator(canvas, REFRESH_FPS, true);

	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		width = drawable.getWidth();
		height = drawable.getHeight();
		gl = drawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();
		setContext(drawable.getContext());

		// Set background color
		setBackground(displaySurface.getBackgroundColor());
		
		// Enable smooth shading, which blends colors nicely, and smoothes out lighting.
		GLUtilLight.enableSmooth(gl);

		// Perspective correction
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		GLUtilLight.enableDepthTest(gl);

		// Set up the lighting for Light-1
		GLUtilLight.InitializeLighting(gl, glu, (float) displaySurface.getEnvWidth(),
			(float) displaySurface.getEnvHeight(), ambientLightValue, diffuseLightValue);
	
		// PolygonMode (Solid or lines)
		if ( polygonMode ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		} else {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
		}
		// Blending control
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		//gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
		gl.glEnable(GL_BLEND);
		// gl.glDisable(GL_DEPTH_TEST);
		// FIXME : should be turn on only if need (if we draw image)
		// problem when true with glutBitmapString
		BLENDING_ENABLED = true;
		IS_LIGHT_ON = false;

		camera.updateCamera(gl, glu, width, height);
		scene = new ModelScene(this);

		OutputSynchronizer.decInitializingViews(this.displaySurface.getOutputName());

	}

	private Color background = Color.white;

	public void setBackground(final Color c) {
		background = c;
		canvas.setBackground(c);
	}

	@Override
	public void display(final GLAutoDrawable drawable) {
		// AD : 10/09/13 Addition of the second condition to addres Issue 607.
		// TODO : Understand why some OpenGL operations are triggered even when the simulation is gone.
		if ( !displaySurface.isPaused() && GAMA.getSimulation() != null ) {
			gl = drawable.getGL().getGL2();
			setContext(drawable.getContext());
			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
			gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
			gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);

			gl.glClearDepth(1.0f);
			gl.glClearColor(background.getRed() / 255.0f, background.getGreen() / 255.0f,
				background.getBlue() / 255.0f, 1.0f);
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

			gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

			gl.glLoadIdentity();

			camera.updateCamera(gl, glu, width, height);

			if ( isLightOn ) {
				gl.glEnable(GLLightingFunc.GL_LIGHTING);
			} else {
				gl.glDisable(GLLightingFunc.GL_LIGHTING);
			}

			GLUtilLight.UpdateAmbiantLightValue(gl, glu, ambientLightValue);
			GLUtilLight.UpdateDiffuseLightValue(gl, glu, diffuseLightValue);

			float[] light0Position = new float[4];
			light0Position[0] = (float) diffuseLightPosition.x;
			light0Position[1] = -(float) diffuseLightPosition.y;
			light0Position[2] = (float) diffuseLightPosition.z;
			light0Position[3] = 0.0f;

			
			if(drawDiffuseLight){
				GLUtilLight.DrawDiffuseLight0(light0Position, gl, glu,getMaxEnvDim()/10, diffuseLightValue);
			}

			// System.out.println("x:" + light0Position[0] + "y:" + light0Position[1] + "z:" + light0Position[2] );
			GLUtilLight.UpdateDiffuseLightPosition(gl, glu, light0Position);

			// Blending control
			if ( BLENDING_ENABLED ) {
				gl.glEnable(GL_BLEND); // Turn blending on

			} else {
				gl.glDisable(GL_BLEND); // Turn blending off
				gl.glEnable(GL_DEPTH_TEST);
			}

			if ( !triangulation ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			}

			this.rotateModel();

			if ( getInertia() ) {
				camera.doInertia();
			}

			this.drawScene();

			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

			if ( this.displaySurface.selectRectangle ) {
				drawROI();
			}

			if ( !autoSwapBuffers ) {
				if ( disableManualBufferSwapping ) {
					disableManualBufferSwapping = false;
				} else {
					canvas.swapBuffers();
				}
			}
		}
	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int arg1, final int arg2, final int width, final int height) {
		// Get the OpenGL graphics context
		gl = drawable.getGL().getGL2();
		this.width = width;
		this.height = height == 0 ? 1 : height;

		// final float aspect = (float) width / height;
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Enable the model view - any new transformations will affect the model-view matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
		// perspective view
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		// glu.gluPerspective(45.0f, aspect, 0.1f, getMaxEnvDim() * 100);
		// FIXME Update camera as well ??
		camera.updateCamera(gl, glu, width, height);
	}


	//public void displayChanged(final GLAutoDrawable arg0, final boolean arg1, final boolean arg2) {}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * @param picking
	 * 
	 */
	public void drawModel() {
		scene.draw(isPicking() || currentPickedObject != null);
	}

	public void drawScene() {
		// gl.glViewport(0, 0, width, height);
		if ( isPicking() ) {
			this.drawPickableObjects();
		} else {
			if ( cubeDisplay ) {
				GLUtil.drawCubeDisplay(this, (float) displaySurface.getEnvWidth());

			} else {
				this.drawModel();
			}
		}
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

	public int minAntiAliasing = GL_NEAREST; /* GL_NEAREST_MIPMAP_NEAREST; */
	public int magAntiAliasing = GL_NEAREST;

	public void setAntiAliasing(final boolean antialias) {
		// antialiasing = antialias ? GL_LINEAR : GL_NEAREST;
		minAntiAliasing = antialias ? GL_LINEAR : GL_NEAREST; /* GL_LINEAR_MIPMAP_LINEAR : GL_NEAREST_MIPMAP_NEAREST; */
		magAntiAliasing = antialias ? GL_LINEAR : GL_NEAREST;
	}

	public void drawPickableObjects() {
		if ( camera.beginPicking(gl) ) {
			drawModel();
			setPickedObjectIndex(camera.endPicking(gl));
		}
		drawModel();
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
		// context.makeCurrent();
	}

	public void setAmbientLightValue(final Color ambientLightValue) {
		this.ambientLightValue = ambientLightValue;
	}

	public void setDiffuseLightValue(final Color diffuseLightValue) {
		this.diffuseLightValue = diffuseLightValue;
	}

	public void setDiffuseLightPosition(final GamaPoint diffuseLightPosition) {
		if ( diffuseLightPosition.equals(new GamaPoint(-1, -1, -1)) ) {
			this.diffuseLightPosition = new GamaPoint(0, 0, 0);
			this.diffuseLightPosition.x = (float) displaySurface.getEnvWidth() / 2;
			this.diffuseLightPosition.y = (float) displaySurface.getEnvHeight() / 2;
			this.diffuseLightPosition.z = (float) displaySurface.getEnvWidth() * 2;
		} else {
			this.diffuseLightPosition = diffuseLightPosition;

		}
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

	public void setZFighting(final boolean z) {
		this.z_fighting = z;
	}

	public boolean getZFighting() {
		return z_fighting;
	}

	public void setDrawNorm(final boolean d) {
		this.drawNormal = d;
	}

	public boolean getDrawNorm() {
		return drawNormal;
	}

	public void setCubeDisplay(final boolean d) {
		this.cubeDisplay = d;
	}

	public boolean getCubeDisplay() {
		return this.cubeDisplay;
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

	public boolean getDrawDiffuseLight() {
		return drawDiffuseLight;
	}

	public void setDrawDiffuseLight(final boolean ddiff) {
		this.drawDiffuseLight = ddiff;
	}
	
	public boolean getIsLightOn() {
		return isLightOn;
	}

	public void setIsLightOn(final boolean islightOn) {
		this.isLightOn = islightOn;
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
		if ( camera.getPhi() < 360 && camera.getPhi() > 180 ) {
			camera.upPosition(0, -1, 0);
		} else {
			camera.upPosition(upVector.getX(), upVector.getY(), upVector.getZ());
		}
	}

	public double getMaxEnvDim() {
		double env_width = displaySurface.getEnvWidth();
		double env_height = displaySurface.getEnvHeight();
		return env_width > env_height ? env_width : env_height;
	}

	public void setPickedObjectIndex(final int pickedObjectIndex) {
		this.pickedObjectIndex = pickedObjectIndex;
		if ( pickedObjectIndex == -1 ) {
			setPicking(false);
		} else if ( pickedObjectIndex == -2 ) {
			displaySurface.selectAgents(null);
			setPicking(false);
		}
	}

	public ModelScene getScene() {
		return scene;
	}

	public void dispose() {
		if ( scene != null ) {
			scene.dispose();
		}
	}

	// Use when the rotation button is on.
	public void rotateModel() {
		if ( this.displaySurface.rotation ) {
			frame++;
		}
		if ( frame != 0 ) {
			double env_width = displaySurface.getEnvWidth();
			double env_height = displaySurface.getEnvHeight();
			//gl.glPushMatrix();
			gl.glTranslated(env_width / 2, -env_height / 2, 0);
			gl.glRotatef(frame, 0, 0, 1);
			gl.glTranslated(-env_width / 2, +env_height / 2, 0);
			//gl.glPopMatrix();
		}
	}

	public double GetEnvWidthOnScreen() {
		Point realWorld = new Point(0, 0);
		Point2D.Double WindowPoint = GLUtil.getWindowPointPointFromRealWorld(this, realWorld);

		Point realWorld2 = new Point((int) displaySurface.getEnvWidth(), -(int) displaySurface.getEnvHeight());
		Point2D.Double WindowPoint2 = GLUtil.getWindowPointPointFromRealWorld(this, realWorld2);
		if ( WindowPoint2 == null || WindowPoint == null ) { return 0.0; }
		return WindowPoint2.x - WindowPoint.x;
	}

	public double GetEnvHeightOnScreen() {
		Point realWorld = new Point(0, 0);
		Point2D.Double WindowPoint = GLUtil.getWindowPointPointFromRealWorld(this, realWorld);

		Point realWorld2 = new Point((int) displaySurface.getEnvWidth(), -(int) displaySurface.getEnvHeight());
		Point2D.Double WindowPoint2 = GLUtil.getWindowPointPointFromRealWorld(this, realWorld2);

		return WindowPoint2.y - WindowPoint.y;
	}

	public void drawROI() {
		if ( camera.isEnableROIDrawing() ) {
			GamaPoint realPressedPoint =
				GLUtil.getIntWorldPointFromWindowPoint(this, camera.getLastMousePressedPosition());
			GamaPoint realMousePositionPoint = GLUtil.getIntWorldPointFromWindowPoint(this, camera.getMousePosition());
			GLUtil.drawROI(gl, realPressedPoint.x, -realPressedPoint.y, realMousePositionPoint.x,
				-realMousePositionPoint.y, this.getZFighting(), this.getMaxEnvDim());
			camera.setRegionOfInterest(realPressedPoint, realMousePositionPoint);
		}

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

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
}
