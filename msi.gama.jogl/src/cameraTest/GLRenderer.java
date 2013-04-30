package cameraTest;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import utils.*;

/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 * 
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer implements GLEventListener {

	CameraArcBall camera;
	GoldenSpiral spiral = new GoldenSpiral(500, 5);

	public GLRenderer(final CameraArcBall c) {
		camera = c;
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));

		GL2 gl = drawable.getGL().getGL2();
		System.err.println("INIT GL IS: " + gl.getClass().getName());

		// Enable VSync
		// gl.setSwapInterval(1);

		// Setup the drawing area and shading mode
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL2.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
		GLUtil.enableSmooth(gl);
		GLUtil.enableBlend(gl);
		GLUtil.enableColorMaterial(gl);
		GLUtil.enableDepthTest(gl);
		GLUtil.enableLighting(gl);
		GLUtil.createDiffuseLight(gl, 0,100);
	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width,
		int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if ( height <= 0 ) { // avoid a divide by zero error!

			height = 1;
		}
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 1.0, 500.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(final GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Clear the drawing area
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// Reset the current matrix to the "identity"
		camera.updateCamera(gl);
		spiral.draw(gl);

	}

	public void displayChanged(final GLAutoDrawable drawable, final boolean modeChanged,
		final boolean deviceChanged) {}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

}
