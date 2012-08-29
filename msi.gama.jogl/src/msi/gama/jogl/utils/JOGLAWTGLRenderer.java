package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_AMBIENT;
import static javax.media.opengl.GL.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_COLOR_MATERIAL;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_DIFFUSE;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_LIGHT1;
import static javax.media.opengl.GL.GL_LIGHTING;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_LINEAR_MIPMAP_NEAREST;
import static javax.media.opengl.GL.GL_MODELVIEW;
import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL.GL_POSITION;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_SMOOTH;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.geotools.data.shapefile.ShapefileDataStore;

import utils.GLUtil;

import Picking.Picker;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;

import com.sun.opengl.util.texture.*;

import msi.gama.jogl.JOGLAWTDisplayGraphics;
import msi.gama.jogl.JOGLAWTDisplaySurface;
import msi.gama.jogl.utils.Camera.Camera;
import msi.gama.jogl.utils.GraphicDataType.MyImage;
import msi.gama.jogl.utils.GraphicDataType.MyTexture;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.jogl.utils.Camera.Arcball.ArcBall;
import msi.gama.jogl.utils.Camera.Arcball.Matrix4f;
import msi.gama.jogl.utils.Camera.Arcball.Quat4f;

public class JOGLAWTGLRenderer implements GLEventListener {

	// ///OpenGL member//////
	private static final int REFRESH_FPS = 30;
	private GLU glu;
	private GL gl;
	public final FPSAnimator animator;
	public GLContext context;
	public GLCanvas canvas;

	public boolean opengl = true;
	// Event Listener
	public MyListener myListener;

	private int width, height;
	// Camera
	public Camera camera;

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

	// Blending
	private static boolean blendingEnabled; // blending on/off

	public JOGLAWTDisplaySurface displaySurface;

	// picking
	double angle = 0;
	// index of the picked object
	public int index = -1;

	// Use multiple view port
	private boolean multipleViewPort = false;
	
	//Handle Shape file
	private ShapeFileReader myShapeFileReader;


	public JOGLAWTGLRenderer(JOGLAWTDisplaySurface d) {
		// Initialize the user camera
		camera = new Camera();

		myGLDrawer = new MyGLToyDrawer();
		myShapeFileReader = new ShapeFileReader("/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT/msi.gama.models/models/3D/road_traffic/includes/building.shp");

		canvas = new GLCanvas();

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

		// Initialize the IGraphics (FIXME: Should we initialize it here??)
		displaySurface.openGLGraphics = new JOGLAWTDisplayGraphics(gl, glu,
				this, displaySurface.envWidth, displaySurface.envHeight);

		// Set background color (in RGBA). Alpha of 0 for total transparency
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		GLUtil.enableSmooth(gl);

		// We want the best perspective correction to be done
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		GLUtil.enableDepthTest(gl);

		GLUtil.InitializeLighting(gl, glu, width);

		// Blending control
		// Full Brightness with specific alpha (1 for opaque, 0 for transparent)
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		// Used blending function based On source alpha value
		// gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnable(GL_BLEND);
		gl.glDisable(GL_DEPTH_TEST);
		// FIXME : should be turn on only if need (if we draw image)
		// problem when true with glutBitmapString
		blendingEnabled = false;
		isLightOn = true;

		camera.UpdateCamera(gl, glu, width, height);

		// FIXME: This is only done for testing the mapping and displaylist
		// feature.
		// myGLDrawer.LoadTextureFromImage(gl,"/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT/msi.gama.jogl/src/textures/earth.jpg");
		// myGLDrawer.buildDisplayLists(gl, width / 4);

		// Load earth texture.
		/*
		 * try { InputStream stream = getClass().getResourceAsStream(
		 * "/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT/msi.gama.jogl/src/textures/arnoi.png"
		 * ); TextureData data = TextureIO.newTextureData(stream, false, "png");
		 * earthTexture = TextureIO.newTexture(data); } catch (IOException exc)
		 * { exc.printStackTrace(); System.exit(1); }
		 */

		System.out.println("openGL init ok");

		// hdviet added 28j/05/2012
		// Start Of User Initialization
		LastRot.setIdentity(); // Reset Rotation
		ThisRot.setIdentity(); // Reset Rotation
		ThisRot.get(matrix);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3,
			int arg4) {

		// Get the OpenGL graphics context
		gl = drawable.getGL();

		if (height == 0) {
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
		glu.gluLookAt(camera.getXPos(), camera.getYPos(), camera.getZPos(),
				camera.getXLPos(), camera.getYLPos(), camera.getZLPos(), 0.0,
				1.0, 0.0);
		arcBall.setBounds(width, height);

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		// System.out.println("display");
		// hdviet added 28/05/2012
		synchronized (matrixLock) {
			ThisRot.get(matrix);
		}

		// System.out.println("opengl display");
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

		if (isLightOn) {
			gl.glEnable(GL_LIGHTING);
		} else {
			gl.glDisable(GL_LIGHTING);
		}

		// Blending control
		if (blendingEnabled) {
			gl.glEnable(GL_BLEND); // Turn blending on
			gl.glDisable(GL_DEPTH_TEST); // Turn depth testing off
		} else {
			gl.glDisable(GL_BLEND); // Turn blending off
			gl.glEnable(GL_DEPTH_TEST); // Turn depth testing on
		}

		// hdviet added 02/06/2012
		gl.glPushMatrix();
		gl.glMultMatrixf(matrix, 0);

		if (displaySurface.Picking) {
			// Display the model center on 0,0,0
			if (camera.isModelCentered) {
				gl.glTranslatef(
						-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
						((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2,
						0.0f); // translate right and into the screen
			}
			this.DrawPickableObject();
		} else {
			// Display the model center on 0,0,0
			if (camera.isModelCentered) {
				gl.glTranslatef(
						-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
						((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2,
						0.0f); // translate right and into the screen
			}
			// FIXME: Need to simplify , give a boolean to DrawModel to know
			// if it's in Picking mode.
			if (!multipleViewPort) {

				gl.glViewport(0, 0, width, height); // Reset The Current
													// Viewport
				this.DrawModel();
				//DrawShapeFile();
			} else {

				// Set The Viewport To The Top Left
				gl.glViewport(0, height / 2, width / 2, height / 2);
				this.DrawModel();

				// Set The Viewport To The Top Right. It Will Take Up Half The
				// Screen Width And Height
				gl.glViewport(width / 2, height / 2, width / 2, height / 2);
				this.DrawModel();

				// Set The Viewport To The Bottom Right
				gl.glViewport(width / 2, 0, width / 2, height / 2);
				this.DrawModel();

				// Set The Viewport To The Bottom Left
				gl.glViewport(0, 0, width / 2, height / 2);
				this.DrawModel();
			}

		}

		// GLUtil.InitializeLighting(gl,glu,((JOGLAWTDisplayGraphics)
		// displaySurface.openGLGraphics).envWidth);

		// GLUtil.DrawLight(gl,glu);
		// myGLDrawer.DrawTexturedSphere(gl, glu);

		// ((JOGLAWTDisplayGraphics)
		// displaySurface.openGLGraphics).graphicsGLUtils.DrawArcBall();

		// myGLDrawer.Draw3DOpenGLHelloWorldShape(gl, width/4);
		// myGLDrawer.DrawSphere(gl, glu,0.0f,0.0f,0.0f,width/4);

		// WARNING: Be sure to have call LoadTextureFromImage() in the init
		// method og the GLRenderer
		// myGLDrawer.DrawColorTriangle(gl, 0.0f, 0.0f, 0.0f, 1.0f, width/4);
		// myGLDrawer.DrawTexturedQuadWithNormal(gl,width/100);
		// myGLDrawer.DrawTexture(gl, width / 4);

		// WARNING: Be sure to call buildDisplayLists() in the init method of
		// the GLRenderer
		// myGLDrawer.DrawTexturedDisplayList(gl,width);
		gl.glPopMatrix();
	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		// hdviet added 28/05/2012
		// init(arg0);
	}

	public void DrawModel() {
		// ((JOGLAWTDisplayGraphics)displaySurface.openGLGraphics).DrawEnvironmentBounds(false);

		// Draw Image
		if (!((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myImages
				.isEmpty()) {
			blendingEnabled = true;
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
					.DrawMyImages();
		}

		// Draw Geometry
		if (!((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myJTSGeometries
				.isEmpty()) {
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
					.DrawMyJTSGeometries(false);
		}

		// Draw Static Geometry
		if (!((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myJTSStaticGeometries
				.isEmpty()) {
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
					.DrawMyJTSStaticGeometries(false);
		}

		// Draw String
		if (!((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myStrings
				.isEmpty()) {
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
					.DrawMyStrings();
		}

		boolean drawAxes = true;
		if (drawAxes) {
			float envMaxDim = ((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).maxEnvDim;

			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).graphicsGLUtils
					.DrawXYZAxis(envMaxDim / 10);
			((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).graphicsGLUtils
					.DrawZValue(-envMaxDim / 10, (float) camera.zPos);
		}

	}
	
	public void DrawShapeFile(){
		
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).graphicsGLUtils.basicDrawer.
		DrawSimpleFeatureCollection(myShapeFileReader.getFeatureCollectionFromShapeFile(myShapeFileReader.store));
		return;
	}

	public void DrawTexture(MyImage img) {

		gl.glTranslated(img.offSet.x, -img.offSet.y, 0.0f);
		if (this.myTextures.size() > 0) {

			Iterator<MyTexture> it = this.myTextures.iterator();
			while (it.hasNext()) {
				MyTexture curTexture = it.next();

				if ((img.name).equals(curTexture.ImageName)) {

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

					if (img.angle != 0) {

						gl.glTranslatef((float) (img.x + img.width / 2),
								-(float) (img.y + img.height / 2), 0.0f);
						// FIXME:Check counterwise or not, and do we rotate
						// around the center or around a point.
						gl.glRotatef(-img.angle, 0.0f, 0.0f, 1.0f);
						gl.glTranslatef(-(float) (img.x + img.width / 2),
								+(float) (img.y + img.height / 2), 0.0f);

						gl.glBegin(GL_QUADS);
						// bottom-left of the texture and quad
						gl.glTexCoord2f(textureLeft, textureBottom);
						gl.glVertex3f(img.x, -(img.y + img.height), img.z);
						// bottom-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureBottom);
						gl.glVertex3f((img.x + img.width),
								-(img.y + img.height), img.z);
						// top-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureTop);
						gl.glVertex3f((img.x + img.width), -(img.y), img.z);
						// top-left of the texture and quad
						gl.glTexCoord2f(textureLeft, textureTop);
						gl.glVertex3f(img.x, -img.y, img.z);
						gl.glEnd();
						gl.glTranslatef((float) (img.x + img.width / 2),
								-(float) (img.y + img.height / 2), 0.0f);
						gl.glRotatef(img.angle, 0.0f, 0.0f, 1.0f);
						gl.glTranslatef(-(float) (img.x + img.width / 2),
								+(float) (img.y + img.height / 2), 0.0f);

					} else {
						gl.glBegin(GL_QUADS);
						// bottom-left of the texture and quad
						gl.glTexCoord2f(textureLeft, textureBottom);
						gl.glVertex3f(img.x, -(img.y + img.height), img.z);
						// bottom-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureBottom);
						gl.glVertex3f((img.x + img.width),
								-(img.y + img.height), img.z);
						// top-right of the texture and quad
						gl.glTexCoord2f(textureRight, textureTop);
						gl.glVertex3f((img.x + img.width), -(img.y), img.z);
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
		gl.glTranslated(-img.offSet.x, img.offSet.y, 0.0f);
	}

	public void InitTexture(BufferedImage image, String name) {

		// Create a OpenGL Texture object from (URL, mipmap, file suffix)
		// need to have an opengl context valide.
		this.context.makeCurrent();
		Texture texture = TextureIO.newTexture(image, false);
		MyTexture curTexture = new MyTexture();
		curTexture.texture = texture;
		curTexture.ImageName = name;
		this.myTextures.add(curTexture);
	}

	// hdviet 27/05/2012
	// add new listener for ArcBall
	// public InputHandler arcBallListener;
	// private GLUquadric quadratic; // Used For Our Quadric
	// hdviet 27/05/2012
	// add attribute to ArcBall model
	private Matrix4f LastRot = new Matrix4f();
	private Matrix4f ThisRot = new Matrix4f();
	private final Object matrixLock = new Object();
	private float[] matrix = new float[16];

	// FIXME: Need to set the width and height of the displaysurface.
	private ArcBall arcBall = new ArcBall(width, height);

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
		if (myListener.beginPicking(gl)) {
			// Need to to do a translation before to draw object and retranslate
			// after.
			// FIXME: need also to apply the arcball matrix to make it work in
			// 3D
			if (camera.isModelCentered) {
				gl.glTranslatef(
						-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
						((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2,
						0.0f);
				((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
						.DrawMyJTSGeometries(true);
				gl.glTranslatef(
						((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envWidth / 2,
						-((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).envHeight / 2,
						0.0f); // translate right and into the screen
			} else {
				((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
						.DrawMyJTSGeometries(true);
			}
			index = myListener.endPicking(gl);
		}

		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
				.DrawMyJTSGeometries(true);
		if (index != -1) {
			gl.glColor3d(0, 0, 0);
			gl.glWindowPos2d(2, 5);
			GLUT glut = new GLUT();
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "Object " + index
					+ " was selected.");
		}
	}

	private void drawObjects(final GL gl) {
		gl.glPushMatrix();
		GLUT glut = new GLUT();
		gl.glInitNames();
		gl.glPushName(0);

		for (int i = 0; i < 6; i++) {

			gl.glPushMatrix();

			gl.glLoadName(i);
			gl.glTranslated(-i * 2 + 5, 0, 0);
			gl.glRotated(angle, i % 2 == 0 ? 1 : -1, i + 1, 0);
			if (index == i) {
				gl.glColor3f(1, 0, 0);
			} else {
				gl.glColor3f(1, 1, 1);
			}

			switch (i) {
			case 0:
				glut.glutSolidCone(1, 1, 30, 30);
				break;
			case 1:
				glut.glutSolidCube(1);
				break;
			case 2:
				glut.glutSolidTorus(.2, 1, 30, 100);
				break;
			case 3:
				glut.glutSolidTeapot(1);
				break;
			case 4:
				glut.glutSolidRhombicDodecahedron();
				break;
			case 5:
				glut.glutSolidSphere(1, 30, 30);
				break;
			}

			gl.glPopMatrix();

		}
		gl.glPopName();
		angle += 1;
		gl.glPopMatrix();
	}
}
