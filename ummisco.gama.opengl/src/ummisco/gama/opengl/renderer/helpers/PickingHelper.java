package ummisco.gama.opengl.renderer.helpers;

import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.JOGLRenderer;

public class PickingHelper extends AbstractRendererHelper {

	protected final IntBuffer selectBuffer = Buffers.newDirectIntBuffer(1024);

	public PickingHelper(final JOGLRenderer r) {
		super(r);
	}

	@Override
	public void initialize() {}

	final static int NONE = -2;
	final static int WORLD = -1;

	volatile boolean isPicking;
	volatile boolean isMenuOn;
	volatile int pickedIndex = NONE;

	public void setPicking(final boolean isPicking) {
		this.isPicking = isPicking;
		if (!isPicking) {
			setPickedIndex(NONE);
			setMenuOn(false);
		}
	}

	public void setMenuOn(final boolean isMenuOn) {
		this.isMenuOn = isMenuOn;
	}

	public void setPickedIndex(final int pickedIndex) {
		this.pickedIndex = pickedIndex;
		if (pickedIndex == WORLD && !isMenuOn) {
			// Selection occured, but no object have been selected
			setMenuOn(true);
			getSurface().selectAgent(null);
		}
	}

	public void tryPick(final DrawingAttributes attributes) {
		attributes.markSelected(pickedIndex);
		if (attributes.isSelected() && !isMenuOn) {
			setMenuOn(true);
			getSurface().selectAgent(attributes);
		}
	}

	public boolean isBeginningPicking() {
		return isPicking && pickedIndex == NONE;
	}

	public boolean isMenuOn() {
		return isMenuOn;
	}

	public boolean isPicking() {
		return isPicking;
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First pass prepare select buffer for select mode by clearing it, prepare openGL to select mode and tell it where
	 * should draw object by using gluPickMatrix() method
	 * 
	 * @return if returned value is true that mean the picking is enabled
	 */
	public void beginPicking() {
		final GL2 gl = getGL();
		final OpenGL openGL = getOpenGL();
		final CameraHelper camera = getRenderer().getCameraHelper();
		final GLU glu = GLU.createGLU();
		// 1. Selecting buffer
		selectBuffer.clear(); // prepare buffer for new objects
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
		// Pass below is very similar to refresh method in GLrenderer
		// 2. Take the viewport attributes,
		final int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		// 3. Prepare openGL for rendering in select mode
		gl.glRenderMode(GL2.GL_SELECT);
		/*
		 * The application must redefine the viewing volume so that it renders only a small area around the place where
		 * the mouse was clicked. In order to do that it is necessary to set the matrix mode to GL_PROJECTION.
		 * Afterwards, the application should push the current matrix to save the normal rendering mode settings. Next
		 * initialise the matrix
		 */
		openGL.pushIdentity(GL2.GL_PROJECTION);
		/*
		 * Define the viewing volume so that rendering is done only in a small area around the cursor. gluPickMatrix
		 * method restrict the area where openGL will drawing objects
		 *
		 */
		glu.gluPickMatrix(camera.getMousePosition().x, viewport[3] - camera.getMousePosition().y, 4, 4, viewport, 0);
		// JOGLRenderer r = getRenderer();
		// FIXME Why do we have to call updatePerspective() here ?
		openGL.updatePerspective();
		openGL.matrixMode(GL2.GL_MODELVIEW);
	}

	public void endPicking() {
		final GL2 gl = getGL();
		final OpenGL openGL = getOpenGL();
		int selectedIndex = PickingHelper.NONE;
		// 5. When you back to Render mode gl.glRenderMode() methods return
		// number of hits
		final int howManyObjects = gl.glRenderMode(GL2.GL_RENDER);
		// 6. Restore to normal settings
		openGL.pop(GL2.GL_PROJECTION);
		openGL.matrixMode(GL2.GL_MODELVIEW);
		// 7. Seach the select buffer to find the nearest object
		// code below derive which objects is nearest from monitor
		//
		if (howManyObjects > 0) {
			// simple searching algorithm
			selectedIndex = selectBuffer.get(3);
			int mindistance = Math.abs(selectBuffer.get(1));
			for (int i = 0; i < howManyObjects; i++) {
				if (mindistance < Math.abs(selectBuffer.get(1 + i * 4))) {
					mindistance = Math.abs(selectBuffer.get(1 + i * 4));
					selectedIndex = selectBuffer.get(3 + i * 4);
				}
			}
			// end of searching
		} else {
			selectedIndex = PickingHelper.WORLD;// return -1 as there was no hits
		}
		setPickedIndex(selectedIndex);
	}

}