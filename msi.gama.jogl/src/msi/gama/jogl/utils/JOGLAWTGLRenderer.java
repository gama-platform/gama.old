package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import msi.gama.jogl.*;
import msi.gama.jogl.utils.Camera.Camera;
import msi.gama.jogl.utils.Camera.Arcball.*;
import msi.gama.jogl.utils.GraphicDataType.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.jogl.utils.collada.ColladaReader;
import msi.gama.jogl.utils.dem.DigitalElevationModelDrawer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.geotools.data.simple.SimpleFeatureCollection;
import utils.GLUtil;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

public class JOGLAWTGLRenderer implements GLEventListener {

	// ///OpenGL member//////
	private static final int REFRESH_FPS = 30;
	public GLU glu;
	public GL gl;
	public final FPSAnimator animator;
	public GLContext context;
	public GLCanvas canvas;

	public boolean opengl = true;

	public boolean isInitialized = false;

	public boolean enableGlRenderAnimator = true;

	// Event Listener
	public MyListener myListener;

	private int width, height;
	// Camera
	public Camera camera;

	public MyGraphics graphicsGLUtils;

	// Use to test and siaply basic opengl shape and primitive
	public MyGLToyDrawer myGLDrawer;

	// Textures list to store all the texture.
	public ArrayList<MyTexture> myTextures = new ArrayList<MyTexture>();

	/** The earth texture. */
	private Texture earthTexture;

	public float textureTop;
	public float textureBottom;
	public float textureLeft;
	public float textureRight;
	public Texture[] textures = new Texture[3];
	public static int currTextureFilter = 2; // currently used filter

	// Lighting
	private static boolean isLightOn;
	public float ambiantLightValue;

	// Blending
	private static boolean blendingEnabled; // blending on/off

	public JOGLAWTDisplaySurface displaySurface;

	// picking
	double angle = 0;

	private final boolean drawAxes = true;

	// Use multiple view port
	private final boolean multipleViewPort = false;

	// Display model a a 3D Cube
	private final boolean threeDCube = false;
	// Handle Shape file
	public ShapeFileReader myShapeFileReader;
	private boolean updateEnvDim = false;

	// Arcball
	private ArcBall arcBall;

	// use glut tesselation or JTS tesselation
	// (can be set in GAML with the boolean facet "tesselation")
	public boolean useTessellation = true;

	// Display or not the triangle when using triangulation (useTessellation = false)
	public boolean polygonmode = true;

	// Show JTS (GAMA) triangulation
	public boolean JTSTriangulation = false;

	// DEM
	public DigitalElevationModelDrawer dem;

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

		// dem = new DigitalElevationModelDrawer();

	}

	@Override
	public void init(GLAutoDrawable drawable) {

		width = drawable.getWidth();
		height = drawable.getHeight();
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		// GL Utilities
		glu = new GLU();

		context = drawable.getContext();

		arcBall = new ArcBall(width, height);

		// Set background color
		gl.glClearColor(displaySurface.getBgColor().getRed(), displaySurface.getBgColor()
			.getGreen(), displaySurface.getBgColor().getBlue(), 1.0f);

		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		GLUtil.enableSmooth(gl);

		// Perspective correction
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		GLUtil.enableDepthTest(gl);

		// Set up the lighting for Light-1
		GLUtil.InitializeLighting(gl, glu, width, ambiantLightValue);

		// PolygonMode (Solid or lines)
		if ( polygonmode ) {
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
		blendingEnabled = true;
		isLightOn = true;

		camera.UpdateCamera(gl, glu, width, height);

		graphicsGLUtils = new MyGraphics(this);

		isInitialized = true;
		System.out.println("openGL init ok");

		// hdviet added 28j/05/2012
		// Start Of User Initialization
		LastRot.setIdentity(); // Reset Rotation
		ThisRot.setIdentity(); // Reset Rotation
		ThisRot.get(matrix);

		// FIXME: Need to be place somewhere (triggered by a button in Gama)
		if ( dem != null ) {
			DigitalElevationModelDrawer.InitDEM(gl);
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
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getZPos(), camera.getXLPos(),
			camera.getYLPos(), camera.getZLPos(), 0.0, 1.0, 0.0);
		arcBall.setBounds(width, height);

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		if ( enableGlRenderAnimator ) {

			// hdviet added 28/05/2012
			synchronized (matrixLock) {
				ThisRot.get(matrix);
			}

			// Get the OpenGL graphics context
			gl = drawable.getGL();
			context = drawable.getContext();

			width = drawable.getWidth();
			height = drawable.getHeight();

			// Clear the screen and the depth buffer
			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			gl.glMatrixMode(GL.GL_PROJECTION);
			// Reset the view (x, y, z axes back to normal)
			gl.glLoadIdentity();

			camera.UpdateCamera(gl, glu, width, height);

			if ( isLightOn ) {
				gl.glEnable(GL_LIGHTING);
			} else {
				gl.glDisable(GL_LIGHTING);
			}

			// Blending control
			if ( blendingEnabled ) {
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

			if ( dem != null ) {
				DigitalElevationModelDrawer.DisplayDEM(gl);
			} else {
				this.DrawScene();
			}

			if ( drawAxes ) {
				float envMaxDim =
					((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).maxEnvDim;
				((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myGLRender.graphicsGLUtils
					.DrawXYZAxis(envMaxDim / 10);
				((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myGLRender.graphicsGLUtils
					.DrawZValue(-envMaxDim / 10, (float) camera.zPos);
			}

			// this.DrawShapeFile();
			// this.DrawCollada();
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

			gl.glPopMatrix();

			// ROI drawer
			if ( this.displaySurface.SelectRectangle ) {
				DrawROI();
			}

		} else {
			// System.out.println("I stop the display");
		}
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

			Point windowmousePositionPoint =
				new Point(myListener.mousePosition.x, myListener.mousePosition.y);
			Point realmousePositionPoint =
				GetRealWorldPointFromWindowPoint(windowmousePositionPoint);

			System.out.println("From" + realPressedPoint.x + "," + realPressedPoint.y);
			System.out.println("To" + realmousePositionPoint.x + "," + realmousePositionPoint.y);

			// System.out.println("World coords are (" //+ realPoint.x + ", " + realPoint.y);

			if ( camera.isModelCentered ) {
				gl.glTranslatef(
					-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
					((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2, 0.0f); // translate
																									// right
																									// and
																									// into
																									// the
																									// screen
			}

			myGLDrawer
				.DrawROI(
					gl,
					realPressedPoint.x -
						((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
					-(realPressedPoint.y - ((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2),
					realmousePositionPoint.x -
						((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
					-(realmousePositionPoint.y - ((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2));

		}

	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {}

	public void DrawScene() {
		if ( displaySurface.Picking ) {
			// Display the model center on 0,0,0
			if ( camera.isModelCentered ) {
				gl.glTranslatef(
					-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
					((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2, 0.0f); // translate
																									// right
																									// and
																									// into
																									// the
																									// screen
			}
			this.DrawPickableObject();
		} else {
			// Display the model center on 0,0,0
			if ( camera.isModelCentered ) {
				gl.glTranslatef(
					-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
					((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2, 0.0f); // translate
																									// right
																									// and
																									// into
																									// the
																									// screen
			}
			// FIXME: Need to simplify , give a boolean to DrawModel to know
			// if it's in Picking mode.

			if ( threeDCube ) {
				// float envMaxDim = ((JOGLAWTDisplayGraphics)
				// displaySurface.openGLGraphics).maxEnvDim;
				float envMaxDim = ((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth;

				this.DrawModel(false);
				gl.glTranslatef(envMaxDim, 0, 0);
				gl.glRotatef(90, 0, 1, 0);
				this.DrawModel(false);
				gl.glTranslatef(envMaxDim, 0, 0);
				gl.glRotatef(90, 0, 1, 0);
				this.DrawModel(false);
				gl.glTranslatef(envMaxDim, 0, 0);
				gl.glRotatef(90, 0, 1, 0);
				this.DrawModel(false);
				gl.glTranslatef(envMaxDim, 0, 0);
				gl.glRotatef(90, 0, 1, 0);

				gl.glRotatef(-90, 1, 0, 0);
				gl.glTranslatef(0, envMaxDim, 0);
				this.DrawModel(false);
				gl.glTranslatef(0, -envMaxDim, 0);
				gl.glRotatef(90, 1, 0, 0);

				gl.glRotatef(90, 1, 0, 0);
				gl.glTranslatef(0, 0, envMaxDim);
				this.DrawModel(false);

				gl.glTranslatef(0, 0, -envMaxDim);
				gl.glRotatef(-90, 1, 0, 0);
				/*
				 * gl.glTranslatef(0,((JOGLAWTDisplayGraphics)
				 * displaySurface.openGLGraphics).envWidth,0);
				 * this.DrawModel(false);
				 * 
				 * gl.glTranslatef(0,-((JOGLAWTDisplayGraphics)
				 * displaySurface.openGLGraphics).envWidth,0);
				 * gl.glRotatef(90, 1, 0, 0);
				 * 
				 * gl.glTranslatef(0,-((JOGLAWTDisplayGraphics)
				 * displaySurface.openGLGraphics).envWidth,0);
				 * gl.glRotatef(90, 1, 0, 0);
				 * this.DrawModel(false);
				 */

			} else {
				if ( !multipleViewPort ) {
					gl.glViewport(0, 0, width, height); // Reset The Current Viewport
					this.DrawModel(false);
				} else {
					// Set The Viewport To The Top Left
					gl.glViewport(0, height / 2, width / 2, height / 2);
					this.DrawModel(false);

					// Set The Viewport To The Top Right. It Will Take Up Half The
					// Screen Width And Height
					gl.glViewport(width / 2, height / 2, width / 2, height / 2);
					this.DrawModel(false);

					// Set The Viewport To The Bottom Right
					gl.glViewport(width / 2, 0, width / 2, height / 2);
					this.DrawModel(false);

					// Set The Viewport To The Bottom Left
					gl.glViewport(0, 0, width / 2, height / 2);
					this.DrawModel(false);
				}
			}

		}
	}

	public void DrawModel(boolean picking) {

		// ((JOGLAWTDisplayGraphics)displaySurface.openGLGraphics).DrawEnvironmentBounds(false);

		// Draw Geometry
		if ( !((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myJTSGeometries.isEmpty() ) {
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawMyJTSGeometries(picking);
		}

		// Draw Static Geometry
		if ( !((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myJTSStaticGeometries
			.isEmpty() ) {
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
				.DrawMyJTSStaticGeometries(picking);
		}

		// Draw Image
		if ( !((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myImages.isEmpty() ) {
			blendingEnabled = true;
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawMyImages(picking);
		}

		// FIXME: When picking = true produes a glitch when clicking on obejt
		if ( !picking ) {
			// Draw String
			if ( !((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myStrings.isEmpty() ) {
				((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawMyStrings();
			}

		}
	}

	/**
	 * Draw a given shapefile
	 **/
	public void DrawShapeFile() {

		if ( !((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myCollections.isEmpty() ) {
			SimpleFeatureCollection myCollection =
				myShapeFileReader.getFeatureCollectionFromShapeFile(myShapeFileReader.store);
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawCollection();
			// Adjust the size of the display surface according to the bound of the shapefile.
			displaySurface.envHeight = (float) myCollection.getBounds().getHeight();
			displaySurface.envWidth = (float) myCollection.getBounds().getWidth();
			if ( !updateEnvDim ) {
				displaySurface.zoomFit();
				updateEnvDim = true;
			}
		}
		return;
	}

	public void DrawCollada() {

		ColladaReader myColReader = new ColladaReader();
		return;
	}

	public void DrawTexture(MyImage img) {

		gl.glTranslated(img.offSet.x, -img.offSet.y, img.offSet.z);
		if ( this.myTextures.size() > 0 ) {

			Iterator<MyTexture> it = this.myTextures.iterator();
			while (it.hasNext()) {
				MyTexture curTexture = it.next();

				if ( img.name.equals(curTexture.ImageName) ) {

					// Enable the texture
					gl.glEnable(GL_TEXTURE_2D);
					Texture t = curTexture.texture;

					t.enable();
					t.bind();

					// Reset opengl color. Set the transparency of the image to
					// 1 (opaque).
					gl.glColor4f(1.0f, 1.0f, 1.0f, img.alpha);
					TextureCoords textureCoords;
					textureCoords = t.getImageTexCoords();
					textureTop = textureCoords.top();
					textureBottom = textureCoords.bottom();
					textureLeft = textureCoords.left();
					textureRight = textureCoords.right();

					if ( img.angle != 0 ) {

						gl.glTranslatef((img.x + img.width / 2), -(img.y + img.height / 2), 0.0f);
						// FIXME:Check counterwise or not, and do we rotate
						// around the center or around a point.
						gl.glRotatef(-img.angle, 0.0f, 0.0f, 1.0f);
						gl.glTranslatef(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0f);

						gl.glBegin(GL_QUADS);
						// bottom-left of the texture and quad
						gl.glTexCoord2f(textureLeft, textureBottom);
						gl.glVertex3f(img.x, -(img.y + img.height), img.z);
						// bottom-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureBottom);
						gl.glVertex3f(img.x + img.width, -(img.y + img.height), img.z);
						// top-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureTop);
						gl.glVertex3f(img.x + img.width, -img.y, img.z);
						// top-left of the texture and quad
						gl.glTexCoord2f(textureLeft, textureTop);
						gl.glVertex3f(img.x, -img.y, img.z);
						gl.glEnd();
						gl.glTranslatef((img.x + img.width / 2), -(img.y + img.height / 2), 0.0f);
						gl.glRotatef(img.angle, 0.0f, 0.0f, 1.0f);
						gl.glTranslatef(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0f);

					} else {
						gl.glBegin(GL_QUADS);
						// bottom-left of the texture and quad
						gl.glTexCoord2f(textureLeft, textureBottom);
						gl.glVertex3f(img.x, -(img.y + img.height), img.z);
						// bottom-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureBottom);
						gl.glVertex3f(img.x + img.width, -(img.y + img.height), img.z);
						// top-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureTop);
						gl.glVertex3f(img.x + img.width, -img.y, img.z);
						// top-left of the texture and quad
						gl.glTexCoord2f(textureLeft, textureTop);
						gl.glVertex3f(img.x, -img.y, img.z);
						gl.glEnd();
					}
					gl.glDisable(GL_TEXTURE_2D);
					break;
				}
			}
		}
		gl.glTranslated(-img.offSet.x, img.offSet.y, -img.offSet.z);
	}

	public void InitTexture(BufferedImage image, String name) {

		// Create a OpenGL Texture object from (URL, mipmap, file suffix)
		// need to have an opengl context valide
		if ( this.context != null ) {
			this.context.makeCurrent();
			Texture texture = TextureIO.newTexture(image, false);
			MyTexture curTexture = new MyTexture();
			curTexture.texture = texture;
			curTexture.ImageName = name;
			this.myTextures.add(curTexture);
		} else {
			// FIXME: See issue 310
			throw new GamaRuntimeException("JOGLRenderer context is null");
		}

	}

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

	public void DrawPickableObject() {
		if ( myListener.beginPicking(gl) ) {
			// Need to to do a translation before to draw object and retranslate
			// after.
			// FIXME: need also to apply the arcball matrix to make it work in
			// 3D
			if ( camera.isModelCentered ) {
				gl.glTranslatef(
					-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
					((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2, 0.0f);
				DrawModel(true);

				gl.glTranslatef(
					((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
					-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2, 0.0f); // translate
																									// right
																									// and
																									// into
																									// the
																									// screen
			} else {
				DrawModel(true);
			}
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).pickedObjectIndex =
				myListener.endPicking(gl);
		}

		DrawModel(true);

	}

	public BufferedImage getScreenShot() {
		BufferedImage img = null;
		if ( context != null ) {
			this.context.makeCurrent();
			img = Screenshot.readToBufferedImage(width, height);
			this.context.release();
		} else {}
		return img;

	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
