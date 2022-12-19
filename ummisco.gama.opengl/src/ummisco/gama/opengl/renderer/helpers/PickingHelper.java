/*******************************************************************************************************
 *
 * PickingHelper.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.helpers;

import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

/**
 * The Class PickingHelper.
 */
public class PickingHelper extends AbstractRendererHelper {

	static {
		DEBUG.ON();
	}

	/** The select buffer. */
	protected final IntBuffer selectBuffer = Buffers.newDirectIntBuffer(1024);

	/**
	 * Instantiates a new picking helper.
	 *
	 * @param r
	 *            the r
	 */
	public PickingHelper(final IOpenGLRenderer r) {
		super(r);
	}

	@Override
	public void initialize() {}

	/** The Constant NONE. */
	final static int NONE = -2;

	/** The Constant WORLD. */
	final static int WORLD = -1;

	/** The is picking. */
	volatile boolean isPicking;

	/** The is menu on. */
	volatile boolean isMenuOn;

	/** The picked index. */
	volatile int pickedIndex = NONE;

	/**
	 * Sets the picking.
	 *
	 * @param isPicking
	 *            the new picking
	 */
	public void setPicking(final boolean isPicking) {
		this.isPicking = isPicking;
		if (!isPicking) {
			setPickedIndex(NONE);
			setMenuOn(false);
		}
	}

	/**
	 * Sets the menu on.
	 *
	 * @param isMenuOn
	 *            the new menu on
	 */
	public void setMenuOn(final boolean isMenuOn) { this.isMenuOn = isMenuOn; }

	/**
	 * Sets the picked index.
	 *
	 * @param pickedIndex
	 *            the new picked index
	 */
	public void setPickedIndex(final int pickedIndex) {
		this.pickedIndex = pickedIndex;
		if (pickedIndex == WORLD && !isMenuOn) {
			// Selection occured, but no object have been selected
			setMenuOn(true);
			getSurface().selectAgent(null);
		}
	}

	/**
	 * Try pick.
	 *
	 * @param attributes
	 *            the attributes
	 */
	public void tryPick(final DrawingAttributes attributes) {
		attributes.markSelected(pickedIndex);
		if (attributes.isSelected() && !isMenuOn) {
			setMenuOn(true);
			getSurface().selectAgent(attributes);
		}
	}

	/**
	 * Checks for picked.
	 *
	 * @return true, if successful
	 */
	// public boolean hasPicked() {
	// return isPicking && pickedIndex != NONE;
	// }

	/**
	 * Checks if is beginning picking.
	 *
	 * @return true, if is beginning picking
	 */
	public boolean isBeginningPicking() { return isPicking && pickedIndex == NONE; }

	/**
	 * Checks if is menu on.
	 *
	 * @return true, if is menu on
	 */
	public boolean isMenuOn() { return isMenuOn; }

	/**
	 * Checks if is picking.
	 *
	 * @return true, if is picking
	 */
	public boolean isPicking() { return isPicking; }

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
		openGL.pushIdentity(GLMatrixFunc.GL_PROJECTION);
		/*
		 * Define the viewing volume so that rendering is done only in a small area around the cursor. gluPickMatrix
		 * method restrict the area where openGL will drawing objects
		 *
		 */
		glu.gluPickMatrix(camera.getMousePosition().x, viewport[3] - camera.getMousePosition().y, 4, 4, viewport, 0);
		// JOGLRenderer r = getRenderer();
		// FIXME Why do we have to call updatePerspective() here ?
		openGL.updatePerspective(gl);
		openGL.matrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	/**
	 * End picking.
	 */
	public void endPicking() {
		final GL2 gl = getGL();
		final OpenGL openGL = getOpenGL();
		int selectedIndex = PickingHelper.NONE;
		// 5. When you back to Render mode gl.glRenderMode() methods return
		// number of hits
		final int howManyObjects = gl.glRenderMode(GL2.GL_RENDER);
		// 6. Restore to normal settings
		openGL.pop(GLMatrixFunc.GL_PROJECTION);
		openGL.matrixMode(GLMatrixFunc.GL_MODELVIEW);
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