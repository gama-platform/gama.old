package Picking;

import java.awt.Point;
import java.awt.event.*;
import java.nio.IntBuffer;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import com.sun.opengl.util.BufferUtil;

public class Picker implements MouseListener {

	public boolean isPressed = false;
	private final Point mousePosition;
	private final IntBuffer selectBuffer = BufferUtil.newIntBuffer(1024);// will store information

	// about selected objects

	// //////////////////////////////////////////////////////////////////////////////////////
	public Picker(final GLCanvas canvas) {
		canvas.addMouseListener(this);
		mousePosition = new Point();
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// unusefull methods
	@Override
	public void mouseClicked(final MouseEvent e) {}

	@Override
	public void mouseReleased(final MouseEvent e) {}

	@Override
	public void mouseEntered(final MouseEvent e) {}

	@Override
	public void mouseExited(final MouseEvent e) {}

	// //////////////////////////////////////////////////////////////////////////////////////
	// save mouse pressed position
	@Override
	public void mousePressed(final MouseEvent e) {
		isPressed = true;
		mousePosition.x = e.getX();
		mousePosition.y = e.getY();
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First pass perare select buffer for select mode by clearing it,
	 * prepare openGL to select mode and tell it where should draw
	 * object by using gluPickMatrix() method
	 * @return if returned value is true that mean the picking is enabled
	 */
	public boolean beginPicking(final GL gl) {
		if ( !isPressed ) { return false; }
		GLU glu = new GLU();
		// 1. Selecting buffer
		selectBuffer.clear(); // prepare buffer for new objects
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);// add buffer to openGL
		// Pass below is very similar to refresh method in GLrenderer
		// 2. Take the viewport attributes,
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		int width = viewport[2]; // get width and
		int height = viewport[3]; // height from viewport

		// 3. Prepare openGL for rendering in select mode
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glRenderMode(GL.GL_SELECT);
		gl.glLoadIdentity();
		// gluPickMatrix method restrict the area where openGL will drawing objects
		// glu.gluPickMatrix(
		// mousePosition.x, height - mousePosition.y,
		// beam size x, beam size y,
		// viewport, 0);
		glu.gluPickMatrix(mousePosition.x, height - mousePosition.y, 2, 2, viewport, 0);

		float h = width / (float) height;
		glu.gluPerspective(45.0f, h, 1.0, 20.0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		// 4. After this pass you must draw Objects

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and return its index
	 * @return name of selected object
	 */
	public int endPicking(final GL gl) {
		if ( !isPressed ) { return -1; }
		isPressed = false;// no further iterations
		int selectedIndex;

		// 5. When you back to Render mode gl.glRenderMode() methods return number of hits
		int howManyObjects = gl.glRenderMode(GL.GL_RENDER);

		// 6. Restore to normal settings
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// 7. Seach the select buffer to find the nearest object

		// code below derive which ocjects is nearest from monitor
		//
		if ( howManyObjects > 0 ) {
			// simple searching algorithm
			selectedIndex = selectBuffer.get(3);
			int mindistance = Math.abs(selectBuffer.get(1));
			for ( int i = 0; i < howManyObjects; i++ ) {

				if ( mindistance < Math.abs(selectBuffer.get(1 + i * 4)) ) {

					mindistance = Math.abs(selectBuffer.get(1 + i * 4));
					selectedIndex = selectBuffer.get(3 + i * 4);

				}

			}
			// end of searching
		} else {
			selectedIndex = -1;// return -1 of there was no hits
		}

		return selectedIndex;
	}
}