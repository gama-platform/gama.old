package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import msi.gama.common.util.GuiUtils;
import msi.gama.jogl.JOGLAWTDisplaySurface;
import msi.gama.jogl.scene.*;
import msi.gama.jogl.utils.Camera.Camera;
import msi.gama.jogl.utils.Camera.Arcball.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.jogl.utils.dem.DigitalElevationModelDrawer;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.OutputSynchronizer;
import utils.GLUtil;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

public class JOGLAWTGLRenderer implements GLEventListener {

	// ///Static members//////
	private static final boolean USE_VERTEX_ARRAY = false;
	private static final int REFRESH_FPS = 10;
	public static int CURR_TEXTURE_FILTER = 2; // currently used filter
	private static boolean BLENDING_ENABLED; // blending on/off
	private static boolean IS_LIGHT_ON;
	public GLU glu;
	public GL gl;
	public final FPSAnimator animator;
	private GLContext context;
	public GLCanvas canvas;
	// Event Listener
	public MyListener myListener;
	private int width, height;
	public final double env_width;
	public final double env_height;
	// Camera
	public Camera camera;
	public MyGraphics graphicsGLUtils;
	// Use to test and display basic opengl shape and primitive
	public MyGLToyDrawer myGLDrawer;
	/** The earth texture. */
	// private Texture earthTexture;
	public float textureTop, textureBottom, textureLeft, textureRight;
	public Texture[] textures = new Texture[3];

	// Lighting
	private Color ambientLightValue;
	// Blending

	public JOGLAWTDisplaySurface displaySurface;
	private ModelScene scene;
	// Use multiple view port
	private final boolean multipleViewPort = false;
	// Display model a a 3D Cube
	private final boolean threeDCube = false;
	// Handle Shape file
	public ShapeFileReader myShapeFileReader;
	// Arcball
	private ArcBall arcBall;
	// use glut tesselation or JTS tesselation
	// (can be set in GAML with the boolean facet "tesselation")
	private boolean useTessellation = true;
	// Display or not the triangle when using triangulation (useTessellation = false)
	private boolean polygonMode = true;
	// Show JTS (GAMA) triangulation
	public boolean JTSTriangulation = false;
	// DEM
	public DigitalElevationModelDrawer dem;
	// use to do the triangulation only once per timestep.
	// private boolean isPolygonTriangulated = false;

	public int pickedObjectIndex = -1;
	public ISceneObject currentPickedObject;
	private int antialiasing = GL_NEAREST;

	// hdviet 27/05/2012
	// add new listener for ArcBall
	// public InputHandler arcBallListener;
	// private GLUquadric quadratic; // Used For Our Quadric
	// hdviet 27/05/2012
	// add attribute to ArcBall model
	private final Matrix4f LastRot = new Matrix4f();
	private final Matrix4f ThisRot = new Matrix4f();
	private final Object matrixLock = new Object();
	private final float[] matrix = new float[16];

	public JOGLAWTGLRenderer(JOGLAWTDisplaySurface d) {

		// Enabling the stencil buffer
		GLCapabilities cap = new GLCapabilities();
		cap.setStencilBits(8);
		// Initialize the user camera
		camera = new Camera();
		myGLDrawer = new MyGLToyDrawer();
		canvas = new GLCanvas(cap);
		myListener = new MyListener(camera, this);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(myListener);
		canvas.addMouseListener(myListener);
		canvas.addMouseMotionListener(myListener);
		canvas.addMouseWheelListener(myListener);
		canvas.setFocusable(true); // To receive key event
		canvas.requestFocusInWindow();
		animator = new FPSAnimator(canvas, REFRESH_FPS, true);
		displaySurface = d;
		dem = new DigitalElevationModelDrawer(this);
		env_width = d.getEnvWidth();
		env_height = d.getEnvHeight();

	}

	public void setAntiAliasing(boolean antialias) {
		antialiasing = antialias ? GL_LINEAR : GL_NEAREST;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		width = drawable.getWidth();
		height = drawable.getHeight();
		gl = drawable.getGL();
		glu = new GLU();
		setContext(drawable.getContext());
		arcBall = new ArcBall(width, height);
		// Set background color
		gl.glClearColor(displaySurface.getBgColor().getRed(), displaySurface.getBgColor().getGreen(), displaySurface
			.getBgColor().getBlue(), 1.0f);
		// Enable smooth shading, which blends colors nicely, and smoothes out lighting.
		GLUtil.enableSmooth(gl);
		// Perspective correction
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		GLUtil.enableDepthTest(gl);
		// Set up the lighting for Light-1
		GLUtil.InitializeLighting(gl, glu, width, ambientLightValue);
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
		// hdviet added 28j/05/2012
		// Start Of User Initialization
		LastRot.setIdentity(); // Reset Rotation
		ThisRot.setIdentity(); // Reset Rotation
		ThisRot.get(matrix);

		// FIXME: Need to be place somewhere (triggered by a button in Gama)
		
		 /*if(dem !=null){
			 GuiUtils.debug("init in joglrender");
		     dem.init(gl);
		  }*/
		
		OutputSynchronizer.decInitializingViews(this.displaySurface.getOutputName());
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		if ( displaySurface.canBeUpdated() ) {
			// hdviet added 28/05/2012
			synchronized (matrixLock) {
				ThisRot.get(matrix);
			}

			// Get the OpenGL graphics context
			gl = drawable.getGL();
			setContext(drawable.getContext());

			width = drawable.getWidth();
			height = drawable.getHeight();

			// Clear the screen and the depth buffer
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			gl.glMatrixMode(GL.GL_PROJECTION);
			// Reset the view (x, y, z axes back to normal)
			gl.glLoadIdentity();

			camera.UpdateCamera(gl, glu, width, height);

			if ( IS_LIGHT_ON ) {
				gl.glEnable(GL_LIGHTING);
			} else {
				gl.glDisable(GL_LIGHTING);
			}

			// FIXME: Now the background is not updated but it should to have a night effect.
			// Set background color
			// gl.glClearColor(ambiantLightValue.floatValue(), ambiantLightValue.floatValue(),
			// ambiantLightValue.floatValue(), 1.0f);
			// The ambiant_light is always reset in case of dynamic lighting.
			GLUtil.UpdateAmbiantLight(gl, glu, ambientLightValue);

			// Show triangulated polygon or not (trigger by GAMA)
			if ( !displaySurface.triangulation ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			}

			// Blending control
			if ( BLENDING_ENABLED ) {
				gl.glEnable(GL_BLEND); // Turn blending on
				// FIXME: This has been comment (09/12 r4989) to have the depth testing when image
				// are drawn but need to know why it was initially disabled?
				// Imply strange rendering when using picture (e.g boids)
				// gl.glDisable(GL_DEPTH_TEST); // Turn depth testing off
			} else {
				gl.glDisable(GL_BLEND); // Turn blending off
				gl.glEnable(GL_DEPTH_TEST); // Turn depth testing on
			}

			// hdviet added 02/06/2012
			gl.glPushMatrix();
			gl.glMultMatrixf(matrix, 0);

			// Use polygon offset for a better edges rendering
			// (http://www.glprogramming.com/red/chapter06.html#name4)
			gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1, 1);

			//gl.glDisable(GL_DEPTH_TEST);
			//dem.DisplayDEM(gl);
			if ( dem.isInitialized() == true ) {
				dem.DisplayDEM(gl);
			} else {
				this.drawScene();
				// if ( drawAxes ) {
				// double envMaxDim = getMaxEnvDim();
				// this.graphicsGLUtils.DrawXYZAxis(envMaxDim / 10);
				// this.graphicsGLUtils.DrawZValue(-envMaxDim / 10, (float) camera.zPos);
				// }
			}

			// this.DrawShapeFile();
			// this.DrawCollada();
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

			gl.glPopMatrix();

			// ROI drawer
			if ( this.displaySurface.selectRectangle ) {
				DrawROI();
			}

		}
		
		//this.displaySurface.snapshot();
	}

	public Point GetRealWorldPointFromWindowPoint(Point windowPoint) {

		int viewport[] = new int[4];
		double mvmatrix[] = new double[16];
		double projmatrix[] = new double[16];
		int realy = 0;// GL y coord pos
		double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords

		int x = windowPoint.x, y = windowPoint.y;

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);
		/* note viewport[3] is height of window in pixels */
		realy = viewport[3] - y - 1;

		FloatBuffer floatBuffer = FloatBuffer.allocate(1);
		gl.glReadPixels(x, realy, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, floatBuffer);
		float z = floatBuffer.get(0);

		glu.gluUnProject(x, realy, z, //
			mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		/*
		 * System.out.println("World coords at z=" + z + "are (" //
		 * + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]
		 * + ")");
		 */

		gl.glFlush();

		Point realWorldPoint = new Point((int) wcoord[0], (int) wcoord[1]);
		return realWorldPoint;
	}

	public void DrawROI() {

		if ( myListener.enableROIDrawing ) {
			Point windowPressedPoint = new Point(myListener.lastxPressed, myListener.lastyPressed);
			Point realPressedPoint = GetRealWorldPointFromWindowPoint(windowPressedPoint);

			Point windowmousePositionPoint = new Point(myListener.mousePosition.x, myListener.mousePosition.y);
			Point realmousePositionPoint = GetRealWorldPointFromWindowPoint(windowmousePositionPoint);

			System.out.println("From" + realPressedPoint.x + "," + realPressedPoint.y);
			System.out.println("To" + realmousePositionPoint.x + "," + realmousePositionPoint.y);

			// System.out.println("World coords are (" //+ realPoint.x + ", " + realPoint.y);

			if ( camera.isModelCentered ) {
				gl.glTranslated(-env_width / 2, env_height / 2, 0.0f);
			}

			myGLDrawer.DrawROI(gl, realPressedPoint.x - env_width / 2, -(realPressedPoint.y - env_height / 2),
				realmousePositionPoint.x - env_width / 2, -(realmousePositionPoint.y - env_height / 2));

		}

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		if ( height == 0 ) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		// Set the viewport (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);
		// Enable the model view - any new transformations will affect the
		// model-view matrix
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
		// perspective view
		gl.glViewport(10, 10, width - 20, height - 20);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getzPos(), camera.getXLPos(), camera.getYLPos(),
			camera.getZLPos(), 0.0, 1.0, 0.0);
		arcBall.setBounds(width, height);
	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {}

	public void drawScene() {
		if ( displaySurface.picking ) {
			// Display the model center on 0,0,0
			if ( camera.isModelCentered ) {
				gl.glTranslated(-env_width / 2, env_height / 2, 0.0f);
			}
			this.drawPickableObjects();
		} else {
			// Display the model center on 0,0,0
			if ( camera.isModelCentered ) {
				gl.glTranslated(-env_width / 2, env_height / 2, 0.0f);
			}
			// FIXME: Need to simplify , give a boolean to DrawModel to know
			// if it's in Picking mode.

			if ( threeDCube ) {
				draw3DCube();

			} else {
				if ( !multipleViewPort ) {
					gl.glViewport(0, 0, width, height); // Reset The Current Viewport
					this.drawModel(false);
				} else {
					// Set The Viewport To The Top Left
					gl.glViewport(0, height / 2, width / 2, height / 2);
					this.drawModel(false);

					// Set The Viewport To The Top Right. It Will Take Up Half The
					// Screen Width And Height
					gl.glViewport(width / 2, height / 2, width / 2, height / 2);
					this.drawModel(false);

					// Set The Viewport To The Bottom Right
					gl.glViewport(width / 2, 0, width / 2, height / 2);
					this.drawModel(false);

					// Set The Viewport To The Bottom Left
					gl.glViewport(0, 0, width / 2, height / 2);
					this.drawModel(false);
				}
			}

		}
	}

	private void draw3DCube() {
		// float envMaxDim = (
		// displaySurface.openGLGraphics).maxEnvDim;
		float envMaxDim = (float) env_width;

		this.drawModel(false);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		this.drawModel(false);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		this.drawModel(false);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		this.drawModel(false);
		gl.glTranslatef(envMaxDim, 0, 0);
		gl.glRotatef(90, 0, 1, 0);

		gl.glRotatef(-90, 1, 0, 0);
		gl.glTranslatef(0, envMaxDim, 0);
		this.drawModel(false);
		gl.glTranslatef(0, -envMaxDim, 0);
		gl.glRotatef(90, 1, 0, 0);

		gl.glRotatef(90, 1, 0, 0);
		gl.glTranslatef(0, 0, envMaxDim);
		this.drawModel(false);

		gl.glTranslatef(0, 0, -envMaxDim);
		gl.glRotatef(-90, 1, 0, 0);
		/*
		 * gl.glTranslatef(0,(
		 * displaySurface.openGLGraphics).envWidth,0);
		 * this.DrawModel(false);
		 * 
		 * gl.glTranslatef(0,-(
		 * displaySurface.openGLGraphics).envWidth,0);
		 * gl.glRotatef(90, 1, 0, 0);
		 * 
		 * gl.glTranslatef(0,-(
		 * displaySurface.openGLGraphics).envWidth,0);
		 * gl.glRotatef(90, 1, 0, 0);
		 * this.DrawModel(false);
		 */
	}

	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * @param picking
	 * 
	 */

	public void drawModel(boolean picking) {
		scene.draw(this, picking, true, true);
	}

	/**
	 * Draw a given shapefile
	 **/
	// public void drawShapeFile() {
	//
	// if ( !displaySurface.getIGraphics().getCollections().isEmpty() ) {
	// SimpleFeatureCollection myCollection =
	// myShapeFileReader.getFeatureCollectionFromShapeFile(myShapeFileReader.store);
	// displaySurface.getIGraphics().drawCollection();
	// // Adjust the size of the display surface according to the bound of the shapefile.
	// displaySurface.setEnvHeight((float) myCollection.getBounds().getHeight());
	// displaySurface.setEnvWidth((float) myCollection.getBounds().getWidth());
	// if ( !updateEnvDim ) {
	// displaySurface.zoomFit();
	// updateEnvDim = true;
	// }
	// }
	// return;
	// }

	// public void DrawCollada() {
	//
	// ColladaReader myColReader = new ColladaReader();
	// return;
	// }

	public MyTexture createTexture(BufferedImage image, boolean isDynamic) {
		// Create a OpenGL Texture object from (URL, mipmap, file suffix)
		// need to have an opengl context valide
		this.getContext().makeCurrent();
		Texture texture = TextureIO.newTexture(image, false);
		texture.setTexParameteri(GL_TEXTURE_MIN_FILTER, antialiasing);
		texture.setTexParameteri(GL_TEXTURE_MAG_FILTER, antialiasing);
		MyTexture curTexture = new MyTexture();
		curTexture.texture = texture;
		curTexture.isDynamic = isDynamic;
		// GuiUtils.debug("JOGLAWTGLRenderer.createTexture for " + image);
		this.getContext().release();
		return curTexture;
	}

	// add function to capture mouse event of ArcBall model
	public void drag(Point mousePoint) {

		Quat4f ThisQuat = new Quat4f();

		arcBall.drag(mousePoint, ThisQuat); // Update End Vector And Get
											// Rotation As Quaternion
		synchronized (matrixLock) {
			ThisRot.setRotation(ThisQuat); // Convert Quaternion Into Matrix3fT
			ThisRot.mul(ThisRot, LastRot); // Accumulate Last Rotation Into This
											// One
		}
	}

	public void startDrag(Point mousePoint) {
		// ArcBall
		synchronized (matrixLock) {
			LastRot.set(ThisRot); // Set Last Static Rotation To Last Dynamic
									// One
		}
		arcBall.click(mousePoint); // Update Start Vector And Prepare For
									// Dragging

	}

	public void reset() {
		synchronized (matrixLock) {
			LastRot.setIdentity(); // Reset Rotation
			ThisRot.setIdentity(); // Reset Rotation
		}
	}

	public void drawPickableObjects() {
		if ( myListener.beginPicking(gl) ) {
			// Need to to do a translation before to draw object and retranslate
			// after.
			// FIXME: need also to apply the arcball matrix to make it work in
			// 3D
			if ( camera.isModelCentered ) {
				gl.glTranslated(-env_width / 2, env_height / 2, 0.0f);
				drawModel(true);

				gl.glTranslated(env_width / 2, -env_height / 2, 0.0f);
			} else {
				drawModel(true);
			}
			setPickedObjectIndex(myListener.endPicking(gl));
		}

		drawModel(true);

	}

	public BufferedImage getScreenShot() {
		BufferedImage img = null;
		if ( getContext() != null ) {
			this.getContext().makeCurrent();
			img = Screenshot.readToBufferedImage(width, height);
			this.getContext().release();
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

	public void setContext(GLContext context) {
		this.context = context;
	}

	public void setAmbientLightValue(Color ambientLightValue) {
		this.ambientLightValue = ambientLightValue;
	}

	public boolean setPolygonMode(boolean polygonMode) {
		return this.polygonMode = polygonMode;
	}

	public boolean getTessellation() {
		return useTessellation;
	}

	public boolean setTessellation(boolean useTessellation) {
		this.useTessellation = useTessellation;
		return useTessellation;
	}

	public void setCameraPosition(ILocation cameraPos) {
		if ( cameraPos.equals(new GamaPoint(-1, -1, -1)) ) {// No change;
		} else {
			camera.updatePosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
		}
	}

	public void setCameraLookPosition(ILocation camLookPos) {
		if ( camLookPos.equals(new GamaPoint(-1, -1, -1)) ) {// No change
		} else {
			camera.lookPosition(camLookPos.getX(), camLookPos.getY(), camLookPos.getZ());
		}
	}

	public void setCameraUpVector(ILocation upVector) {
		camera.setUpVector(upVector);
	}

	public double getMaxEnvDim() {
		return env_width > env_height ? env_width : env_height;
	}

	public void setPickedObjectIndex(int pickedObjectIndex) {
		this.pickedObjectIndex = pickedObjectIndex;
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
	
	public void CalculateFrameRate()
    {

    }

}
