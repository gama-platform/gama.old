package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.List;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import msi.gama.common.util.GuiUtils;
import msi.gama.jogl.JOGLAWTDisplaySurface;
import msi.gama.jogl.utils.Camera.Camera;
import msi.gama.jogl.utils.Camera.Arcball.*;
import msi.gama.jogl.utils.GraphicDataType.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.jogl.utils.collada.ColladaReader;
import msi.gama.jogl.utils.dem.DigitalElevationModelDrawer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.OutputSynchronizer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.geotools.data.simple.SimpleFeatureCollection;
import utils.GLUtil;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;
import com.vividsolutions.jts.geom.Geometry;

public class JOGLAWTGLRenderer implements GLEventListener {

	// ///OpenGL member//////
	private static final int REFRESH_FPS = 30;
	public GLU glu;
	public GL gl;
	public final FPSAnimator animator;
	private GLContext context;
	public GLCanvas canvas;

	public boolean opengl = true;

	public volatile boolean isInitialized = false;

	public boolean enableGlRenderAnimator = true;

	// Event Listener
	public MyListener myListener;

	private int width, height;
	private final double env_width, env_height;
	// Camera
	public Camera camera;

	public MyGraphics graphicsGLUtils;

	// Use to test and display basic opengl shape and primitive
	public MyGLToyDrawer myGLDrawer;

	// Textures list to store all the texture.
	public Map<BufferedImage, MyTexture> myTextures = new LinkedHashMap();

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
	private Color ambientLightValue;

	// Blending
	private static boolean blendingEnabled; // blending on/off

	public JOGLAWTDisplaySurface displaySurface;

	private boolean highlight = false;

	// picking
	double angle = 0;

	private final boolean drawAxes = true;

	// Use multiple view port
	private final boolean multipleViewPort = false;

	// Display model a a 3D Cube
	private final boolean threeDCube = false;
	// Handle Shape file
	public ShapeFileReader myShapeFileReader;
	private final boolean updateEnvDim = false;

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
	// List of all the dynamic JTS geometry.
	private final java.util.List<MyJTSGeometry> geometries = new ArrayList();
	// List of all the static JTS geometry.
	private final java.util.List<MyJTSGeometry> staticGeometries = new ArrayList();
	// each Image is stored in a list
	private final java.util.List<MyImage> images = new ArrayList<MyImage>();
	// each Collection is stored in a list
	private final java.util.List<MyCollection> collections = new ArrayList<MyCollection>();
	// List of all the String
	private final java.util.List<MyString> strings = new ArrayList<MyString>();
	private boolean isListCreated = false;
	private boolean isStaticListCreated = false;
	private boolean isListShapeCreated = false;
	private final boolean useDisplayList = false;
	private boolean drawCollectionAsList = false;
	// use to do the triangulation only once per timestep.
	private boolean isPolygonTriangulated = false;
	private final boolean useVertexArray = false;

	// Picked (to trigg when a new object has been picked)
	private int currentPicked = -1;
	private int pickedObjectIndex = -1;
	private int antialiasing = GL_NEAREST;

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
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		// GL Utilities
		glu = new GLU();

		setContext(drawable.getContext());

		arcBall = new ArcBall(width, height);

		// Set background color
		gl.glClearColor(displaySurface.getBgColor().getRed(), displaySurface.getBgColor().getGreen(), displaySurface
			.getBgColor().getBlue(), 1.0f);

		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		GLUtil.enableSmooth(gl);

		// Perspective correction
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		GLUtil.enableDepthTest(gl);

		// Set up the lighting for Light-1
		GLUtil.InitializeLighting(gl, glu, width, getAmbientLightValue());

		// PolygonMode (Solid or lines)
		if ( getPolygonMode() ) {
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
		setBlendingEnabled(true);
		isLightOn = true;

		camera.UpdateCamera(gl, glu, width, height);

		graphicsGLUtils = new MyGraphics(this);

		// hdviet added 28j/05/2012
		// Start Of User Initialization
		LastRot.setIdentity(); // Reset Rotation
		ThisRot.setIdentity(); // Reset Rotation
		ThisRot.get(matrix);

		// FIXME: Need to be place somewhere (triggered by a button in Gama)
		/*
		 * if(dem !=null){
		 * dem.InitDEM(gl);
		 * }
		 */

		isInitialized = true;
		GuiUtils.debug("JOGLAWTGLRenderer.init: " + this.displaySurface.getOutputName());
		OutputSynchronizer.decInitializingViews(this.displaySurface.getOutputName());
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		if ( enableGlRenderAnimator && displaySurface.canBeUpdated() ) {

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

			if ( isLightOn ) {
				gl.glEnable(GL_LIGHTING);
			} else {
				gl.glDisable(GL_LIGHTING);
			}

			// FIXME: Now the background is not updated but it should to have a night effect.
			// Set background color
			// gl.glClearColor(ambiantLightValue.floatValue(), ambiantLightValue.floatValue(),
			// ambiantLightValue.floatValue(), 1.0f);
			// The ambiant_light is always reset in case of dynamic lighting.
			GLUtil.UpdateAmbiantLight(gl, glu, getAmbientLightValue());

			// Show triangulated polygon or not (trigger by GAMA)
			if ( !displaySurface.triangulation ) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			}

			// Blending control
			if ( isBlendingEnabled() ) {
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

			if ( dem.isInitialized() == true ) {
				dem.DisplayDEM(gl);
			} else {
				this.drawScene();
				if ( drawAxes ) {
					double envMaxDim = getMaxEnvDim();
					this.graphicsGLUtils.DrawXYZAxis(envMaxDim / 10);
					this.graphicsGLUtils.DrawZValue(-envMaxDim / 10, (float) camera.zPos);
				}
			}

			// this.DrawShapeFile();
			// this.DrawCollada();
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

			gl.glPopMatrix();

			// ROI drawer
			if ( this.displaySurface.selectRectangle ) {
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
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getZPos(), camera.getXLPos(), camera.getYLPos(),
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
			this.DrawPickableObject();
		} else {
			// Display the model center on 0,0,0
			if ( camera.isModelCentered ) {
				gl.glTranslated(-env_width / 2, env_height / 2, 0.0f);
			}
			// FIXME: Need to simplify , give a boolean to DrawModel to know
			// if it's in Picking mode.

			if ( threeDCube ) {
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

	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * @param picking
	 * 
	 */

	public void drawModel(boolean picking) {

		// Draw Geometry
		if ( !getJTSGeometries().isEmpty() ) {
			drawMyJTSGeometries(picking);
		}

		// Draw Static Geometry
		if ( !getMyJTSStaticGeometries().isEmpty() ) {
			drawMyJTSStaticGeometries(picking);
		}

		// Draw Image
		if ( !images.isEmpty() ) {
			JOGLAWTGLRenderer.setBlendingEnabled(true);
			drawMyImages(picking);
		}

		// FIXME: When picking = true produes a glitch when clicking on obejt
		if ( !picking ) {
			// Draw String
			if ( !strings.isEmpty() ) {
				drawMyStrings();
			}
		}

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

	public void DrawCollada() {

		ColladaReader myColReader = new ColladaReader();
		return;
	}

	public void drawTexture(MyImage img) {
		gl.glTranslated(img.offSet.x, -img.offSet.y, img.offSet.z);
		// TODO Scale en Z
		gl.glScaled(img.scale.x, img.scale.y, 1);
		MyTexture curTexture = myTextures.get(img.image);
		if ( curTexture == null ) { return; }
		// Enable the texture
		gl.glEnable(GL_TEXTURE_2D);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, antialiasing);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, antialiasing);
		Texture t = curTexture.texture;
		t.enable();
		t.bind();
		// Reset opengl color. Set the transparency of the image to
		// 1 (opaque).
		gl.glColor4d(1.0d, 1.0d, 1.0d, img.alpha);
		TextureCoords textureCoords;
		textureCoords = t.getImageTexCoords();
		textureTop = textureCoords.top();
		textureBottom = textureCoords.bottom();
		textureLeft = textureCoords.left();
		textureRight = textureCoords.right();
		if ( img.angle != 0 ) {
			gl.glTranslated(img.x + img.width / 2, -(img.y + img.height / 2), 0.0f);
			// FIXME:Check counterwise or not, and do we rotate
			// around the center or around a point.
			gl.glRotatef(-img.angle, 0.0f, 0.0f, 1.0f);
			gl.glTranslated(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0f);

			gl.glBegin(GL_QUADS);
			// bottom-left of the texture and quad
			gl.glTexCoord2f(textureLeft, textureBottom);
			gl.glVertex3d(img.x, -(img.y + img.height), img.z);
			// bottom-right of the texture and quad
			gl.glTexCoord2f(textureRight, textureBottom);
			gl.glVertex3d(img.x + img.width, -(img.y + img.height), img.z);
			// top-right of the texture and quad
			gl.glTexCoord2f(textureRight, textureTop);
			gl.glVertex3d(img.x + img.width, -img.y, img.z);
			// top-left of the texture and quad
			gl.glTexCoord2f(textureLeft, textureTop);
			gl.glVertex3d(img.x, -img.y, img.z);
			gl.glEnd();
			gl.glTranslated(img.x + img.width / 2, -(img.y + img.height / 2), 0.0f);
			gl.glRotatef(img.angle, 0.0f, 0.0f, 1.0f);
			gl.glTranslated(-(img.x + img.width / 2), +(img.y + img.height / 2), 0.0f);
		} else {
			gl.glBegin(GL_QUADS);
			// bottom-left of the texture and quad
			gl.glTexCoord2f(textureLeft, textureBottom);
			gl.glVertex3d(img.x, -(img.y + img.height), img.z);
			// bottom-right of the texture and quad
			gl.glTexCoord2f(textureRight, textureBottom);
			gl.glVertex3d(img.x + img.width, -(img.y + img.height), img.z);
			// top-right of the texture and quad
			gl.glTexCoord2f(textureRight, textureTop);
			gl.glVertex3d(img.x + img.width, -img.y, img.z);
			// top-left of the texture and quad
			gl.glTexCoord2f(textureLeft, textureTop);
			gl.glVertex3d(img.x, -img.y, img.z);
			gl.glEnd();
		}
		gl.glDisable(GL_TEXTURE_2D);
		gl.glScaled(1 / img.scale.x, 1 / img.scale.y, 1);
		gl.glTranslated(-img.offSet.x, img.offSet.y, -img.offSet.z);
	}

	public void InitTexture(BufferedImage image, boolean isDynamic) {

		// Create a OpenGL Texture object from (URL, mipmap, file suffix)
		// need to have an opengl context valide
		// if ( this.context != null ) {
		this.getContext().makeCurrent();
		Texture texture = TextureIO.newTexture(image, false);
		MyTexture curTexture = new MyTexture();
		curTexture.texture = texture;
		curTexture.isDynamic = isDynamic;
		this.myTextures.put(image, curTexture);
		// }
		// else {
		// // FIXME: See issue 310
		// throw new GamaRuntimeException("JOGLRenderer context is null");
		// }

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
		// while (!isInitialized) {
		// GuiUtils.debug("JOGLAWTGLRenderer.getContext: waiting");
		// Thread.dumpStack();
		// try {
		// Thread.sleep(10);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		return context;
	}

	public void setContext(GLContext context) {
		this.context = context;
	}

	public Color getAmbientLightValue() {
		return ambientLightValue;
	}

	public void setAmbientLightValue(Color ambientLightValue) {
		this.ambientLightValue = ambientLightValue;
	}

	public boolean getPolygonMode() {
		return polygonMode;
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

	private static boolean isBlendingEnabled() {
		return blendingEnabled;
	}

	public static void setBlendingEnabled(boolean blendingEnabled) {
		JOGLAWTGLRenderer.blendingEnabled = blendingEnabled;
	}

	public double getEnvWidth() {
		return env_width;
	}

	public double getEnvHeight() {
		return env_height;
	}

	public double getMaxEnvDim() {
		return env_width > env_height ? env_width : env_height;
	}

	// Added 02/05 AD

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
	public void addImages(final BufferedImage img, final IAgent agent, final double curX, final double curY,
		final Double z, final double widthInModel, final double heightInModel, final Integer angle,
		final GamaPoint offSet, final GamaPoint scale, final boolean isDynamic, double alpha) {
		images.add(new MyImage(img, agent, curX, curY, Double.isNaN(z) ? 0 : z, alpha, widthInModel, heightInModel,
			angle == null ? 0 : angle, offSet, scale));
		if ( isDynamic || !myTextures.containsKey(img) ) {
			InitTexture(img, isDynamic);
		}
	}

	/**
	 * Call every new iteration when updateDisplay() is called Remove only the
	 * texture that has to be redrawn. Keep all the texture coming form a file.
	 */
	public void cleanImages() {
		this.images.clear();
		for ( Iterator<BufferedImage> it = myTextures.keySet().iterator(); it.hasNext(); ) {
			BufferedImage im = it.next();
			// FIXME: if an image is not declared as dynamic, it will be kept in memory (even if it is not used)
			if ( myTextures.get(im).isDynamic ) {
				it.remove();
			}
		}
	}

	/**
	 * Once the list of Images has been created, OpenGL display call this method
	 * every framerate. FIXME: Need to be optimize with the use of Vertex Array
	 * or even VBO
	 * 
	 */
	public void drawMyImages(final boolean picking) {

		if ( picking ) {
			gl.glPushMatrix();
			gl.glInitNames();
			gl.glPushName(0);
			int i = 0;

			Iterator<MyImage> it = this.images.iterator();
			while (it.hasNext()) {
				gl.glPushMatrix();
				gl.glLoadName(i);

				MyImage curImage = it.next();

				if ( pickedObjectIndex == i ) {
					if ( curImage.agent != null ) {

						gl.glColor3d(0, 0, 0);
						gl.glWindowPos2d(2, 5);
						// glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24,
						// curImage.agent.getSpeciesName() + curImage.agent.getIndex());
						currentPicked = i;

						if ( currentPicked != i ) {
							// Call Agent window inspector
						}
					} else {
						System.out.println("Picking null agent");
					}
					drawTexture(curImage);
				} else {
					drawTexture(curImage);
				}

				gl.glPopMatrix();
				i++;
			}
			gl.glPopName();
			gl.glPopMatrix();

		} else {
			boolean drawImageAsList = false;
			if ( drawImageAsList ) {
				if ( !isListCreated ) {
					graphicsGLUtils.displayListHandler.buildImageDisplayLists(this.images);
					this.isListCreated = true;
				} else {
					graphicsGLUtils.displayListHandler.DrawImageDisplayList(this.images.size());
				}

			} else {
				Iterator<MyImage> it = this.images.iterator();
				while (it.hasNext()) {
					MyImage curImage = it.next();
					drawTexture(curImage);
				}
			}
		}
	}

	/**
	 * @param curHeight
	 * @param currentOffset
	 * @param currentScale
	 * @param stringColor
	 * @param fontName
	 * @param styleName
	 * @param angle2
	 *            Add string and its postion in the list of String that are drawn by Opengl
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addString(final String string, final double x, final double y, final double z, double height,
		GamaPoint offset, GamaPoint scale, Color color, String fontName, Integer styleName, Integer angle) {
		// FIXME Add Font information like GLUT.BITMAP_TIMES_ROMAN_24;
		strings.add(new MyString(string, fontName, styleName, offset, scale, color, angle, x, y, z, 0, height));
	}

	/**
	 * Once the list of String has been created, OpenGL display call this method
	 * every framerate.
	 * 
	 */
	public void drawMyStrings() {
		for ( MyString s : strings ) {
			graphicsGLUtils.drawString(s.string, s.x, s.y, s.z, s.z_layer, s.offset, s.scale, s.color);
		}
	}

	/**
	 * Call every new iteration when updateDisplay() is called
	 */
	public void cleanStrings() {
		this.strings.clear();
	}

	/**
	 * Add collection and its associated parameter in the list of Image that are
	 * drawn by Opengl
	 * 
	 * @param collection
	 * @param color
	 */
	public void addCollections(final SimpleFeatureCollection collection, final Color color) {
		collections.add(new MyCollection(collection, color));
	}

	public java.util.List<MyCollection> getCollections() {
		return collections;
	}

	public void cleanCollections() {
		if ( drawCollectionAsList ) {
			graphicsGLUtils.displayListHandler.DeleteCollectionDisplayLists(this.getCollections().size());
		}
		collections.clear();
	}

	public void drawCollection() {

		// FIXME : need to be done for a list of collection
		this.drawCollectionAsList = true;
		if ( drawCollectionAsList ) {
			if ( !isListShapeCreated ) {
				graphicsGLUtils.displayListHandler.buildCollectionDisplayLists(getCollections());
				this.isListShapeCreated = true;
			} else {
				graphicsGLUtils.displayListHandler.drawCollectionDisplayList(getCollections().size());
			}

		} else {
			for ( MyCollection curCol : collections ) {
				graphicsGLUtils.basicDrawer.drawSimpleFeatureCollection(curCol);
			}
		}

	}

	/**
	 * Call when updateDisplay() is called
	 */
	public void cleanGeometries() {
		if ( useDisplayList ) {
			graphicsGLUtils.displayListHandler.DeleteDisplayLists(this.getJTSGeometries().size());
		}
		if ( useVertexArray ) {
			graphicsGLUtils.vertexArrayHandler.DeleteVertexArray();
		}
		geometries.clear();
		this.isListCreated = false;

	}

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
	public void addJTSGeometry(final Geometry geometry, final IAgent agent, final double z_layer,
		final int currentLayerId, final Color color, final boolean fill, final Color border, final boolean isTextured,
		final Integer angle, final double height, final GamaPoint offSet, GamaPoint scale, final boolean roundCorner,
		final String type, boolean currentLayerIsStatic, double alpha) {
		MyJTSGeometry curJTSGeometry;
		if ( angle != null ) {
			curJTSGeometry =
				new MyJTSGeometry(geometry, agent, z_layer, currentLayerId, color, alpha, fill, border, isTextured,
					angle, height, offSet, scale, roundCorner, type);
		} else {
			curJTSGeometry =
				new MyJTSGeometry(geometry, agent, z_layer, currentLayerId, color, alpha, fill, border, isTextured, 0,
					height, offSet, scale, roundCorner, type);
		}

		// Add the geometry either in the static list or in the dynamic one.
		if ( currentLayerIsStatic ) {
			// only once (if isStaticListCreated =false)
			if ( !isStaticListCreated ) {
				getMyJTSStaticGeometries().add(curJTSGeometry);
			}
		} else {
			getJTSGeometries().add(curJTSGeometry);
		}
	}

	public void drawMyJTSGeometries(final boolean picking) {

		if ( picking ) {
			gl.glPushMatrix();
			gl.glInitNames();
			gl.glPushName(0);
			int i = 0;
			Iterator<MyJTSGeometry> it = this.getJTSGeometries().iterator();
			while (it.hasNext()) {
				gl.glPushMatrix();
				gl.glLoadName(i);
				MyJTSGeometry curGeometry = it.next();

				if ( pickedObjectIndex == i ) {
					MyJTSGeometry pickedGeometry = (MyJTSGeometry) curGeometry.clone();
					pickedGeometry.color = Color.red;
					if ( pickedGeometry.agent != null ) {} else {
						throw new GamaRuntimeException("Picking null agent");
					}

					if ( pickedGeometry.agent != null && currentPicked != i ) {
						displaySurface.selectAgents(0, 0, pickedGeometry.agent, pickedGeometry.layerId - 1);
						this.currentPicked = i;
					}

					graphicsGLUtils.basicDrawer.drawJTSGeometry(pickedGeometry);
				} else {
					graphicsGLUtils.basicDrawer.drawJTSGeometry(curGeometry);
				}

				gl.glPopMatrix();
				i++;
			}
			gl.glPopName();
			gl.glPopMatrix();
		} else {
			// System.out.println("isListCreated="+isListCreated);
			if ( useDisplayList ) {
				// System.out.println("Geometries are build with displayList");
				if ( !isListCreated ) {
					System.out.println("Create" + this.getJTSGeometries().size() + "list");
					graphicsGLUtils.displayListHandler.buildDisplayLists((ArrayList<MyJTSGeometry>) this
						.getJTSGeometries());
					System.out.println("Create" + this.getJTSGeometries().size() + "list ok");
					this.isListCreated = true;
				} else {
					// System.out.println("Call" + this.myJTSGeometries.size() +
					// "list");
					graphicsGLUtils.displayListHandler.DrawDisplayList(this.getJTSGeometries().size());
				}
			} else {

				if ( !useVertexArray ) {
					// System.out.println(this.myJTSGeometries.size() +
					// " geometries are build with basicDrawer ");
					Iterator<MyJTSGeometry> it = this.getJTSGeometries().iterator();
					while (it.hasNext()) {
						MyJTSGeometry curGeometry = it.next();
						graphicsGLUtils.basicDrawer.drawJTSGeometry(curGeometry);
					}
				}
				// use vertex array
				else {
					// triangulate all the geometries
					if ( !isPolygonTriangulated ) {
						graphicsGLUtils.vertexArrayHandler.buildVertexArray((ArrayList<MyJTSGeometry>) this
							.getJTSGeometries());
						setPolygonTriangulated(true);
					} else {
						graphicsGLUtils.vertexArrayHandler.drawVertexArray();
					}
				}
			}
		}

	}

	public Collection<MyJTSGeometry> getJTSGeometries() {
		return geometries;
	}

	public Collection<MyJTSGeometry> getMyJTSStaticGeometries() {
		return staticGeometries;
	}

	/**
	 * Once the list of JTSGeometries has been created, OpenGL display call this
	 * method every framerate. FIXME: Need to be optimize with the use of Vertex
	 * Array or even VBO
	 * 
	 */
	public void drawMyJTSStaticGeometries(final boolean picking) {
		if ( picking ) {
			// TODO
		} else {
			if ( !isStaticListCreated ) {
				graphicsGLUtils.displayListHandler.buildDisplayLists((List<MyJTSGeometry>) this
					.getMyJTSStaticGeometries());
				isStaticListCreated = true;
				System.out.println("Create" + getMyJTSStaticGeometries().size() + "list static ok");
			} else {
				graphicsGLUtils.displayListHandler.DrawDisplayList(this.getMyJTSStaticGeometries().size());
			}
		}
	}

	// public void drawEnvironmentBounds(final boolean drawData) {
	// GamaPoint offSet = new GamaPoint(0, 0);
	// GamaPoint scale = new GamaPoint(0, 0);
	// if ( drawData ) {
	// // Draw Width and height value
	// graphicsGLUtils.drawString(String.valueOf(this.getEnvWidth()), this.getEnvWidth() / 2,
	// this.getEnvHeight() * 0.01f, 0.0f, 0.0);
	// graphicsGLUtils.drawString(String.valueOf(this.getEnvHeight()), this.getEnvWidth() * 1.01f,
	// -(this.getEnvHeight() / 2), 0.0f, 0.0);
	// }
	//
	// // Draw environment rectangle
	// Geometry g =
	// GamaGeometryType.buildRectangle(getEnvWidth(), getEnvHeight(),
	// new GamaPoint(getEnvWidth() / 2, getEnvHeight() / 2)).getInnerGeometry();
	//
	// Color c = new Color(225, 225, 225);
	// MyJTSGeometry curGeometry =
	// new MyJTSGeometry(g, null, -0.01f, -1, c, 1.0f, true, c, false, 0, 0.0f, offSet, scale, false,
	// "environment");
	// graphicsGLUtils.basicDrawer.drawJTSGeometry(curGeometry);
	// }

	public void setPolygonTriangulated(boolean isPolygonTriangulated) {
		this.isPolygonTriangulated = isPolygonTriangulated;
	}

	public void setPickedObjectIndex(int pickedObjectIndex) {
		this.pickedObjectIndex = pickedObjectIndex;
	}

	public void turnHighlight(boolean b) {
		highlight = b;
	}

	public boolean isHighlightTurnedOn() {
		return highlight;
	}

}
