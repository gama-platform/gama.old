/*******************************************************************************************************
 *
 * IOpenGLRenderer.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer;

import com.jogamp.opengl.GLEventListener;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.helpers.CameraHelper;
import ummisco.gama.opengl.renderer.helpers.KeystoneHelper;
import ummisco.gama.opengl.renderer.helpers.LightHelper;
import ummisco.gama.opengl.renderer.helpers.PickingHelper;
import ummisco.gama.opengl.renderer.helpers.SceneHelper;
import ummisco.gama.opengl.view.GamaGLCanvas;
import ummisco.gama.opengl.view.SWTOpenGLDisplaySurface;

/**
 * The Interface IOpenGLRenderer.
 */
public interface IOpenGLRenderer extends GLEventListener, IGraphics.ThreeD {

	/**
	 * Sets the canvas.
	 *
	 * @param canvas
	 *            the new canvas
	 */
	void setCanvas(GamaGLCanvas canvas);

	/**
	 * Gets the canvas.
	 *
	 * @return the canvas
	 */
	GamaGLCanvas getCanvas();

	/**
	 * Inits the scene.
	 */
	void initScene();

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	double getWidth();

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	double getHeight();

	/**
	 * Gets the real world point from window point.
	 *
	 * @param mouse
	 *            the mouse
	 * @return the real world point from window point
	 */
	GamaPoint getRealWorldPointFromWindowPoint(final GamaPoint mouse);

	/**
	 * Gets the surface.
	 *
	 * @return the surface
	 */
	@Override
	SWTOpenGLDisplaySurface getSurface();

	/**
	 * Gets the camera helper.
	 *
	 * @return the camera helper
	 */
	CameraHelper getCameraHelper();

	/**
	 * Gets the keystone helper.
	 *
	 * @return the keystone helper
	 */
	KeystoneHelper getKeystoneHelper();

	/**
	 * Gets the picking helper.
	 *
	 * @return the picking helper
	 */
	PickingHelper getPickingHelper();

	/**
	 * Gets the open GL helper.
	 *
	 * @return the open GL helper
	 */
	OpenGL getOpenGLHelper();

	/**
	 * Gets the light helper.
	 *
	 * @return the light helper
	 */
	LightHelper getLightHelper();

	/**
	 * Gets the scene helper.
	 *
	 * @return the scene helper
	 */
	SceneHelper getSceneHelper();

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	default LayeredDisplayData getData() { return getSurface().getData(); }

	/**
	 * Gets the layer width.
	 *
	 * @return the layer width
	 */
	int getLayerWidth();

	/**
	 * Gets the layer height.
	 *
	 * @return the layer height
	 */
	int getLayerHeight();

	/**
	 * Use shader.
	 *
	 * @return true, if successful
	 */
	default boolean useShader() {
		return false;
	}

	/**
	 * Checks if is disposed.
	 *
	 * @return true, if is disposed
	 */
	boolean isDisposed();

	/**
	 * Checks for drawn once.
	 *
	 * @return true, if successful
	 */
	boolean hasDrawnOnce();

}