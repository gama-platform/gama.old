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
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_POSITION;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_SMOOTH;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.*;

import msi.gama.common.util.ImageUtils;
import msi.gama.jogl.JOGLAWTDisplayGraphics;
import msi.gama.jogl.JOGLAWTDisplaySurface;

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

	// Textures list to store all the texture.
	public ArrayList<Texture> myTextures = new ArrayList<Texture>();
	float textureTop, textureBottom, textureLeft, textureRight;
	private Texture[] textures = new Texture[3];
	private static int currTextureFilter = 2; // currently used filter

	// FIXME: has to be remove. Texture (use only to test the texture mapping
	// from a .png file)
	private Texture texture;
	private String textureFileName = "/Users/macbookpro/Projects/Gama/Sources/branches/GAMA_CURRENT/msi.gama.jogl/src/textures/arnoi.png";

	// Lighting
	private static boolean isLightOn;

	// Blending
	private static boolean blendingEnabled; // blending on/off

	public JOGLAWTDisplaySurface displaySurface;

	public JOGLAWTGLRenderer(JOGLAWTDisplaySurface d) {
		// Initialize the user camera
		camera = new Camera();

		canvas = new GLCanvas();
		myListener = new MyListener(camera);
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
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		// We want the best perspective correction to be done
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		gl.glShadeModel(GL_SMOOTH);

		// FIXME: This is only done for testing the mapping stuff.
		this.LoadTextureFromImage();

		// Set up the lighting for Light-1
		// Ambient light does not come from a particular direction. Need some
		// ambient
		// light to light up the scene. Ambient's value in RGBA
		float[] lightAmbientValue = { 0.5f, 0.5f, 0.5f, 1.0f };
		// Diffuse light comes from a particular location. Diffuse's value in
		// RGBA
		float[] lightDiffuseValue = { 1.0f, 1.0f, 1.0f, 1.0f };
		// Diffuse light location xyz (in front of the screen at width
		// position).
		float lightDiffusePosition[] = { 0.0f, 0.0f, width, 1.0f };

		gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue, 0);
		gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
		gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
		gl.glEnable(GL_LIGHT1); // Enable Light-1
		gl.glDisable(GL_LIGHTING); // But disable lighting
		isLightOn = true;

		// enable color tracking
		gl.glEnable(GL_COLOR_MATERIAL);
		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

		// Blending control
		// Full Brightness with specific alpha (1 for opaque, 0 for transparent)
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		// Used blending function based On source alpha value
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		gl.glEnable(GL_BLEND);
		gl.glDisable(GL_DEPTH_TEST);
		blendingEnabled = false;

		camera.UpdateCamera(gl, width, height);
		System.out.println("openGL init ok");

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

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// System.out.println("opengl display");
		// Get the OpenGL graphics context
		gl = drawable.getGL();
		context = drawable.getContext();

		width = drawable.getWidth();
		height = drawable.getHeight();

		// Clear the screen and the depth buffer
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gl.glViewport(0, 0, width, height); // Reset The Current Viewport

		gl.glMatrixMode(GL.GL_PROJECTION);
		// Reset the view (x, y, z axes back to normal)
		gl.glLoadIdentity();

		camera.UpdateCamera(gl, width, height);

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

		//this.DrawTexturedQuadWithNormal(width / 4);
		this.DrawModel();
		// this.DrawOpenGLHelloWorldShape(width, height);
		// this.DrawColorTriangle(0.0f, 0.0f, 0.0f, width);

		// this.DrawZValue();
		// this.Draw2DShape();
		// this.Draw3DynamicShape(100);

	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	public void DrawZValue() {
		GLUT glut = new GLUT();
		// X Axis
		gl.glRasterPos3f(
				0.0f,
				((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myHeight * 0.01f,
				0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				"z = " + String.valueOf((int) camera.zPos));
	}

	public void DrawTexture(int index, MyImage img) {

		if (this.myTextures.size() > 0) {
			// Enable the texture
			gl.glEnable(GL_TEXTURE_2D);
			Texture t = this.myTextures.get(index);

			t.enable();
			t.bind();

			// Reset opengl color.
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			TextureCoords textureCoords;
			textureCoords = t.getImageTexCoords();
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();

			gl.glBegin(GL_QUADS);
			// bottom-left of the texture and quad
			gl.glTexCoord2f(textureLeft, textureBottom);
			gl.glVertex3f(img.x, -(img.y + img.image.getHeight()), 0);
			// bottom-right of the texture and quad
			gl.glTexCoord2f(textureRight, textureBottom);
			gl.glVertex3f((img.x + img.image.getWidth()),
					-(img.y + img.image.getHeight()), 0);
			// top-right of the texture and quad
			gl.glTexCoord2f(textureRight, textureTop);
			gl.glVertex3f((img.x + img.image.getWidth()), -(img.y), 0);
			// top-left of the texture and quad
			gl.glTexCoord2f(textureLeft, textureTop);
			gl.glVertex3f(img.x, -img.y, 0);
			gl.glEnd();
		}
		gl.glDisable(GL_TEXTURE_2D);
	}

	public void InitTexture(BufferedImage image) {

		// Create a OpenGL Texture object from (URL, mipmap, file suffix)

		// need to have an opengl context valide.
		this.context.makeCurrent();

		Texture curTexture = TextureIO.newTexture(image, false);
		this.myTextures.add(curTexture);
		System.out.println(this.myTextures.size());
	}

	public void Draw2DShape() {
		// gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear color
		// and depth buffers

		// ----- Render a triangle -----
		gl.glTranslatef(-1.5f, 0.0f, -6.0f); // translate left and into the
												// screen
		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f);
		gl.glEnd();

		// ----- Render a Quad -----
		// Translate right, relative to the previous translation
		gl.glTranslatef(3.0f, 0.0f, 0.0f);
		gl.glColor3f(0.5f, 0.5f, 1.0f); // Light-blue
		gl.glBegin(GL_QUADS); // draw using quads
		gl.glVertex3f(-1.0f, 1.0f, 0.0f);
		gl.glVertex3f(1.0f, 1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();

	}

	public void Draw3DynamicShape(float size) {
		// System.out.println("ca zaaaaaaaaaa");
		float anglePyramid = 0; // rotational angle in degree for pyramid
		float angleCube = 0; // rotational angle in degree for cube
		float speedPyramid = 2.0f; // rotational speed for pyramid
		float speedCube = -1.5f; // rotational speed for cube
		// gl.glLoadIdentity(); // reset the model-view matrix
		gl.glTranslatef(-1.5f, 0.0f, -6.0f); // translate left and into the
												// screen
		gl.glRotatef(anglePyramid, 0.1f, 1.0f, -0.1f); // rotate about the
														// y-axis

		gl.glTranslatef(-1.5f, 0.0f, -6.0f); // translate left and into the
												// screen
		gl.glRotatef(anglePyramid, 0.1f, 1.0f, -0.1f); // rotate about the
														// y-axis

		gl.glBegin(GL_TRIANGLES); // of the pyramid

		// Font-face triangle
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		gl.glVertex3f(1.0f, -1.0f, 1.0f);

		// Right-face triangle
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		// Back-face triangle
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);

		// Left-face triangle
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
		gl.glVertex3f(0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);

		gl.glEnd(); // of the pyramid

		// ----- Render the Color Cube -----
		gl.glLoadIdentity(); // reset the current model-view matrix
		gl.glTranslatef(1.5f, 0.0f, -7.0f); // translate right and into the
											// screen
		gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and
													// z-axes

		gl.glBegin(GL_QUADS); // of the color cube

		// Top-face
		gl.glColor3f(0.0f, 1.0f, 0.0f); // green
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);

		// Bottom-face
		gl.glColor3f(1.0f, 0.5f, 0.0f); // orange
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		// Front-face
		gl.glColor3f(1.0f, 0.0f, 0.0f); // red
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);

		// Back-face
		gl.glColor3f(1.0f, 1.0f, 0.0f); // yellow
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);

		// Left-face
		gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);

		// Right-face
		gl.glColor3f(1.0f, 0.0f, 1.0f); // violet
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		gl.glEnd(); // of the color cube

		// Update the rotational angle after each refresh.
		anglePyramid += speedPyramid;
		angleCube += speedCube;
	}

	public void DrawModel() {
		// ((JOGLAWTDisplayGraphics)displaySurface.openGLGraphics).DrawEnvironmentBounds();
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawMyImages();
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
				.DrawMyJTSGeometries();
		// ((JOGLAWTDisplayGraphics)
		// displaySurface.openGLGraphics).DrawMyGeometries();

		float envMaxDim = ((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).myMaxDim;
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics)
				.DrawXYZAxis(envMaxDim / 10);
		((JOGLAWTDisplayGraphics) displaySurface.openGLGraphics).DrawZValue(
				-envMaxDim / 10, (float) camera.zPos);
	}

	public void DrawOpenGLHelloWorldShape(float width, float height) {
		float red = (float) (Math.random()) * 1;
		float green = (float) (Math.random()) * 1;
		float blue = (float) (Math.random()) * 1;

		// gl.glColor3f(red, green, blue);
		gl.glColor3f(0.0f, 1.0f, 1.0f);
		// ----- Render a quad -----

		gl.glBegin(GL_POLYGON); // draw using quads
		gl.glVertex3f(-width / 2, height / 2, -10.0f);
		gl.glVertex3f(width / 2, height / 2, -10.0f);
		gl.glVertex3f(width / 2, -height / 2, -10.f);
		gl.glVertex3f(-width / 2, -height / 2, -10.0f);
		gl.glEnd();
	}

	public void DrawColorTriangle(float x, float y, float z, float size) {
		// ----- Render a triangle -----
		gl.glTranslatef(x, y, z); // translate left and into the screen
		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
		gl.glVertex3f(0.0f, size, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
		gl.glVertex3f(-size, -size, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		gl.glVertex3f(size, -size, 0.0f);
		gl.glEnd();
	}

	public void DrawTexturedQuad(float width) {

		this.LoadTextureFromImage();

		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
		// context's state.
		texture.enable();
		// gl.glEnable(GL_TEXTURE_2D);
		// gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
		// GL.GL_REPLACE);
		// Binds this texture to the current GL context.
		texture.bind();

		gl.glBegin(GL_QUADS);

		// Front Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, width); // bottom-left of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, width); // bottom-right of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, width); // top-right of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, width); // top-left of the texture and quad

		// Back Face
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, -width);

		// Top Face
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);

		// Bottom Face
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);

		// Right face
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);

		// Left Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);

		gl.glEnd();

	}

	public void DrawTexturedQuadWithNormal(float width) {

		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
		// context's state.
		textures[currTextureFilter].enable();
		// Bind the texture with the currently chosen filter to the current
		// OpenGL
		// graphics context.
		textures[currTextureFilter].bind();

		gl.glBegin(GL_QUADS); // of the color cube

		// Front Face
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, width); // bottom-left of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, width); // bottom-right of the texture and
												// quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, width); // top-right of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, width); // top-left of the texture and quad

		// Back Face
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, -width);

		// Top Face
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);

		// Bottom Face
		gl.glNormal3f(0.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);

		// Right face
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(width, -width, -width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(width, width, -width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(width, width, width);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(width, -width, width);

		// Left Face
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-width, -width, -width);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-width, -width, width);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-width, width, width);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-width, width, -width);

		gl.glEnd();
	}

	public void LoadTextureFromImage() {

		// Load textures from image
		try {
			// Use URL so that can read from JAR and disk file.
			BufferedImage image = ImageUtils.getInstance().getImageFromFile(
					textureFileName);

			// Create a OpenGL Texture object from (URL, mipmap, file suffix)
			textures[0] = TextureIO.newTexture(image, false);
			// Nearest filter is least compute-intensive
			// Use nearer filter if image is larger than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			// Use nearer filter if image is smaller than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

			textures[1] = TextureIO.newTexture(image, false);
			// Linear filter is more compute-intensive
			// Use linear filter if image is larger than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			// Use linear filter if image is smaller than the original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			textures[2] = TextureIO.newTexture(image, true); // mipmap is true
			// Use mipmap filter is the image is smaller than the texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
					GL_LINEAR_MIPMAP_NEAREST);

			// Get the top and bottom coordinates of the textures. Image flips
			// vertically.
			TextureCoords textureCoords;
			textureCoords = textures[0].getImageTexCoords();
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();

		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
