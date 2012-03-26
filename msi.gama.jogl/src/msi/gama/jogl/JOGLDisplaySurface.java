package msi.gama.jogl;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_MODELVIEW;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_PROJECTION;
import static javax.media.opengl.GL.GL_SMOOTH;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JPanel;

import com.sun.opengl.util.FPSAnimator;

import msi.gama.common.interfaces.IDisplay;
import msi.gama.common.interfaces.IDisplayManager;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.gui.displays.DisplayManager;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;

public final class JOGLDisplaySurface extends JPanel implements
		IDisplaySurface, GLEventListener {
	
	   private static final int REFRESH_FPS = 60;    // Display refresh frames per second
	   private GLU glu;             // For the GL Utility
	   public final FPSAnimator animator;  // Used to drive display() 

	   protected BufferedImage buffImage;
	   protected IDisplayManager manager;
	   
	JOGLDisplaySurface() {
		GLCanvas canvas = new GLCanvas();
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
		canvas.addGLEventListener(this);
		
		manager = new DisplayManager(this);

		// Run the animation loop using the fixed-rate Frame-per-second
		// animator,
		// which calls back display() at this fixed-rate (FPS).
		animator = new FPSAnimator(canvas, REFRESH_FPS, true);

	}

	@Override
	public BufferedImage getImage() {
		// TODO Auto-generated method stub
		return buffImage;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] computeBoundsFrom(int width, int height) {
		int[] dim = new int[2];
		dim[0] = 100;
		dim[1] = 200;
		return dim;
	}

	@Override
	public boolean resizeImage(int width, int height) {
		// The new IGraphics must be set here
		return false;
	}

	@Override
	public void outputChanged(double env_width, double env_height,
			IDisplayOutput output) {
		/*if (manager == null) {
			manager = new DisplayManager(this);
		}*/

	}

	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoomFit() {
		// TODO Auto-generated method stub

	}

	@Override
	public IDisplayManager getManager() {
		return manager;
	}

	@Override
	public void fireSelectionChanged(Object a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusOn(IShape geometry, IDisplay display) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canBeUpdated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void canBeUpdated(boolean ok) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackgroundColor(Color background) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPaused(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setQualityRendering(boolean quality) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSynchronized(boolean checked) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAutoSave(boolean autosave) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSnapshotFileName(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void snapshot() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNavigator(Object swtNavigationPanel) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getImageWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getImageHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOrigin(int i, int j) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOriginX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOriginY() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* Implements method for GLEventlistener */
	@Override
	public void initialize(double w, double h, IDisplayOutput layerDisplayOutput) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
	      GL gl = drawable.getGL(); // Get the OpenGL graphics context
	      gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear color and depth buffers
	      gl.glLoadIdentity();  // reset the model-view matrix

	      // ----- Render a triangle -----

	      gl.glTranslatef(-1.5f, 0.0f, -6.0f); // translate left and into the screen

	      gl.glBegin(GL_TRIANGLES); // draw using triangles
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glVertex3f(-1.0f, -1.0f, 0.0f);
	      gl.glVertex3f(1.0f, -1.0f, 0.0f);
	      gl.glEnd();

	      // ----- Render a quad -----

	      // translate right, relative to the previous translation
	      gl.glTranslatef(3.0f, 0.0f, 0.0f);

	      gl.glBegin(GL_POLYGON); // draw using quads
	      gl.glVertex3f(-1.0f, 1.0f, 0.0f);
	      gl.glVertex3f(1.0f, 1.0f, 0.0f);
	      gl.glVertex3f(0.0f, 0.0f, 0.0f);
	      gl.glVertex3f(-1.0f, -1.0f, 0.0f);
	      gl.glEnd();

	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL(); // Get the OpenGL graphics context
	      glu = new GLU(); // GL Utilities
	      gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set background (clear) color
	      gl.glClearDepth(1.0f); // Set clear depth value to farthest
	      gl.glEnable(GL_DEPTH_TEST); // Enables depth testing
	      gl.glDepthFunc(GL_LEQUAL); // The type of depth test to do
	      // Do the best perspective correction
	      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	      // Enable smooth shading, which blends colors nicely, and smoothes out lighting.
	      gl.glShadeModel(GL_SMOOTH);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL(); // Get the OpenGL graphics context

	      if (height == 0) {
	         height = 1; // prevent divide by zero
	      }
	      float aspect = (float)width / height;

	      // Set the viewport (display area) to cover the entire window
	      gl.glViewport(0, 0, width, height);

	      // Setup perspective projection, with aspect ratio matches viewport
	      gl.glMatrixMode(GL_PROJECTION); // Choose projection matrix
	      gl.glLoadIdentity(); // Reset projection matrix
	      glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar

	      // Enable the model-view transform
	      gl.glMatrixMode(GL_MODELVIEW);
	      gl.glLoadIdentity(); // reset

	}

}