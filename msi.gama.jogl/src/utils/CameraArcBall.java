package utils;

import java.awt.Point;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

// implement all necessery Listeners to have control on the wolrd :)
public class CameraArcBall implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final Point mousePosition; // keep your cunrent mouse position
	private final Vector cameraPosition; // determine where camare should be located
	private final double mouseSensitivity = 1;// I think that this is clear :)
	private int mouseButton = -1; // save number of mouse button which was pressed,
	// you can add different option to each button
	private Vector r = new Vector(), r1 = new Vector(), r2 = new Vector(), axis = new Vector();
	// r - its length value keep ArcBall sphere radius
	// r1,r2 - temporary variables
	// axis keep current rotation axis,this variable can be
	// very usefull I will show you later how
	private float m_v[] = { //
		1, 0, 0, 0, //
			0, 1, 0, 0, // this matrix will accumulate every rotation
			0, 0, 1, 0, // we will use this variable to multiply model-
			0, 0, 0, 1 // view matrix by it to achiev rotation effect
		}; //
	private boolean mousePressed = false;//
	private boolean mouseDragged = false;//
	// /////////////////////////////////////////////////////////////////////////////////////////

	public CameraArcBall(final GLCanvas canvas) {// we will send to constructor our canvas reference

		canvas.addMouseListener(this); // make your mouse buttons working
		canvas.addMouseMotionListener(this);// mouse movement
		canvas.addMouseWheelListener(this); // and wheel

		mousePosition = new Point(); //
		cameraPosition = new Vector(0, 0, -15);// default camera translation
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		// NOT IMPORTANT
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		// NOT IMPORTANT
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		// NOT IMPORTANT
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mousePressed(final MouseEvent e) {
		updateMousePosition(e); // update mouse position
		mouseButton = e.getButton(); // save mouse pressed button

		mousePressed = true;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseReleased(final MouseEvent e) {
		updateMousePosition(e); // not important but I've added this
		mousePressed = false;
		mouseDragged = false;
		mouseButton = -1; // nothing is pressed value
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseDragged(final MouseEvent e) {
		updateMousePosition(e);
		if ( mouseButton == 1 ) {
			mouseDragged = true; // set mouse dragged only for one button
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseMoved(final MouseEvent e) {
		updateMousePosition(e);

	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		if ( e.getWheelRotation() < 0 ) {
			cameraPosition.z += 1; // increase z-distance of camera
		} else {
			cameraPosition.z -= 1; // decreasing
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	public void updateCamera(final GL gl) {

		GLU glu = new GLU();
		GLUquadric quadric = glu.gluNewQuadric();

		r = updateRadius(-500, -500, gl);

		if ( mousePressed ) {
			r1 = updateRadius(mousePosition.x, mousePosition.y, gl);
			// calcualate z component from spere equation
			Double z = Math.sqrt(r.length * r.length - r1.x * r1.x - r1.y * r1.y);
			r1.z = z;
		}
		if ( mouseDragged ) {

			mousePressed = false;
			mouseDragged = false;

			r2 = updateRadius(mousePosition.x, mousePosition.y, gl);
			// calcualate z component from spere equation
			Double z = Math.sqrt(r.length * r.length - r2.x * r2.x - r2.y * r2.y);
			if ( z.isNaN() ) {
				z = 0.0;
			}
			r2.z = z;
			// calculate rotation axis and normalize it
			axis = r1.cross(r2);
			axis.normalize();
			// change old r1 vector with new r2
			r1 = r2;

			// rotate world
			gl.glRotated(mouseSensitivity, axis.x, axis.y, axis.z);
			// get new modelview matrix
			float m[] = new float[16];
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, m, 0);

			// mutliply rotation matrix by current rotation
			m_v = mutliplyMatrixAByMatrixB(transpose(m_v), transpose(m));
			m_v = transpose(m_v);
		}

		// /////////////////////uncomment those lines and see what happens/////////////////
		gl.glLoadIdentity();
		axis = axis.scale(.9);
		gl.glRotated(mouseSensitivity * axis.length, axis.x, axis.y, axis.z);
		float m[] = new float[16];
		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, m, 0);
		m_v = mutliplyMatrixAByMatrixB(transpose(m_v), transpose(m));
		m_v = transpose(m_v);
		// ////////////////////////////////////////////////////////////////////////////////

		gl.glLoadIdentity();
		gl.glTranslated(cameraPosition.x, cameraPosition.y, cameraPosition.z);
		gl.glMultMatrixf(m_v, 0);
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_LINE);
		glu.gluSphere(quadric, r.length, 20, 20);

	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	private void updateMousePosition(final MouseEvent e) {
		mousePosition.x = e.getX();
		mousePosition.y = e.getY();
	}

	private float[] transpose(final float m[]) {
		float matrix[] =
			{ m[e(0, 0)], m[e(1, 0)], m[e(2, 0)], m[e(3, 0)], m[e(0, 1)], m[e(1, 1)], m[e(2, 1)],
				m[e(3, 1)], m[e(0, 2)], m[e(1, 2)], m[e(2, 2)], m[e(3, 2)], m[e(0, 3)], m[e(1, 3)],
				m[e(2, 3)], m[e(3, 3)] };
		return matrix;
	}

	// auxiliary method
	private int e(final int row, final int col) {
		return row * 4 + col;
	}

	private float[] mutliplyMatrixAByMatrixB(final float ma[], final float mb[]) {
		float m[] = new float[16];

		for ( int rowB = 0; rowB < 4; rowB++ ) {// row of matrix B

			for ( int colA = 0; colA < 4; colA++ ) { // column of matrix A

				for ( int i = 0; i < 4; i++ ) {

					m[rowB * 4 + colA] += mb[rowB * 4 + i] * ma[i * 4 + colA];

				}

			}

		}
		return m; // return matrix C
	}

	/**
	 * px , py - are point od the screen if you use px = 0 and py = 0 you will point on
	 * left-up corner of screen
	 */
	private Vector updateRadius(final int px, final int py, final GL gl) {

		GLU glu = new GLU();

		gl.glLoadIdentity();// reset the model-view matrix to identity
		gl.glTranslated(cameraPosition.x, cameraPosition.y, cameraPosition.z);// zoom out-in camera

		// get viewport
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		// get modelview matrix
		double modelViewM[] = new double[16];
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelViewM, 0);

		// get projection matrix
		double projectionM[] = new double[16];
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionM, 0);

		// get coordinates of point screen using gluProject function
		// mp[0],mp[1] contains x,y components , mp[2]-contains value from 0-1 and mean what is the
		// distance between point on screen (z-dist)
		// and in this case point [0,0,0]
		double mp[] = new double[3];
		glu.gluProject(0, 0, 0, modelViewM, 0, projectionM, 0, viewport, 0, mp, 0);

		// now when you know z-dist you can find coordinates of each point on the same depth
		// what centre of ArcBall sphere
		double coordinates[] = new double[3];
		glu.gluUnProject(px, viewport[3] - py, mp[2], modelViewM, 0, projectionM, 0, viewport, 0,
			coordinates, 0);
		// this is the ArcBall sphere radius vector
		Vector r0 = new Vector(coordinates[0], coordinates[1], coordinates[2]);

		gl.glLoadIdentity();
		return r0;
	}
}
