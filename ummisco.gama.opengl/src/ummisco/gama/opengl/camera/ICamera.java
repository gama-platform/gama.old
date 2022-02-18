/*******************************************************************************************************
 *
 * ICamera.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.camera;

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;

/**
 * Class ICamera.
 *
 * @author drogoul
 * @since 5 sept. 2013
 *
 */
public interface ICamera extends org.eclipse.swt.events.KeyListener, MouseListener, MouseMoveListener,
		MouseTrackListener, MouseWheelListener, com.jogamp.newt.event.MouseListener, com.jogamp.newt.event.KeyListener {

	/**
	 * The Interface CameraPreset.
	 */
	@FunctionalInterface
	public interface CameraPreset {
		/**
		 * Apply to.
		 *
		 * @param camera
		 *            the camera
		 */
		void applyTo(AbstractCamera camera);
	}

	// Positions

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	GamaPoint getPosition();

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 */
	GamaPoint getOrientation();

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	GamaPoint getTarget();

	/**
	 * Gets the mouse position.
	 *
	 * @return the mouse position
	 */
	GamaPoint getMousePosition();

	/**
	 * Gets the last mouse pressed position.
	 *
	 * @return the last mouse pressed position
	 */
	GamaPoint getLastMousePressedPosition();

	// Commands

	/**
	 * Initialize.
	 */
	void initialize();

	/**
	 * Update.
	 */
	void update();

	/**
	 * Update position.
	 */
	void updatePosition();

	/**
	 * Update target.
	 */
	void updateTarget();

	/**
	 * Update orientation.
	 */
	void updateOrientation();

	/**
	 * Animate.
	 */
	void animate();

	/**
	 * Apply preset.
	 *
	 * @param preset
	 *            the preset
	 */
	void applyPreset(String preset);

	// Zoom

	/**
	 * Zoom level.
	 *
	 * @return the double
	 */
	Double zoomLevel();

	/**
	 * Zoom.
	 *
	 * @param level
	 *            the level
	 */
	void zoom(double level);

	/**
	 * Zoom focus.
	 *
	 * @param env
	 *            the env
	 */
	void zoomFocus(Envelope3D env);

}