package Picking;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import utils.GLUtil;
import com.sun.opengl.util.GLUT;

/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 * 
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer implements GLEventListener {

	Picker picker;
	double angle = 0;
	int index = -1;

	public GLRenderer(final GLCanvas canvas) {
		picker = new Picker(canvas);
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));

		GL gl = drawable.getGL();
		System.err.println("INIT GL IS: " + gl.getClass().getName());

		// Enable VSync
		gl.setSwapInterval(4);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
		GLUtil.enableSmooth(gl);
		GLUtil.enableBlend(gl);
		GLUtil.enableColorMaterial(gl);
		GLUtil.enableDepthTest(gl);
		GLUtil.enableLighting(gl);
		GLUtil.createDiffuseLight(gl, 0);
	}

	@Override
	public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width,
		int height) {
		GL gl = drawable.getGL();
		GLU glu = new GLU();

		if ( height <= 0 ) { // avoid a divide by zero error!

			height = 1;
		}
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 1.0, 500.0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(final GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if ( picker.beginPicking(gl) ) {
			drawObjects(gl);
			index = picker.endPicking(gl);
		}

		gl.glLoadIdentity();
		gl.glTranslatef(0, 0.0f, -6.0f);

		drawObjects(gl);

		if ( index != -1 ) {
			gl.glColor3d(1, 1, 1);
			GLUT glut = new GLUT();
			gl.glWindowPos2d(2, 5);
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "Object " + index + " was selected.");
		}

	}

	@Override
	public void displayChanged(final GLAutoDrawable drawable, final boolean modeChanged,
		final boolean deviceChanged) {}

	private void drawObjects(final GL gl) {
		gl.glPushMatrix();
		GLUT glut = new GLUT();
		gl.glInitNames();
		gl.glPushName(0);

		for ( int i = 0; i < 6; i++ ) {

			gl.glPushMatrix();

			gl.glLoadName(i);
			gl.glTranslated(-i * 2 + 5, 0, 0);
			gl.glRotated(angle, i % 2 == 0 ? 1 : -1, i + 1, 0);
			if ( index == i ) {
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
