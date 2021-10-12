/*******************************************************************************************************
 *
 * ICamera.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
  * @param camera the camera
  */
 void applyTo(AbstractCamera camera); }

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
	 * Sets the distance.
	 *
	 * @param distance the new distance
	 */
	void setDistance(final double distance);

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
	 * @param preset the preset
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
	 * @param in the in
	 */
	void zoom(boolean in);

	/**
	 * Zoom.
	 *
	 * @param level the level
	 */
	void zoom(double level);

	/**
	 * Zoom focus.
	 *
	 * @param env the env
	 */
	void zoomFocus(Envelope3D env);

	/**
	 * Sets the position.
	 *
	 * @param x the x
	 * @param d the d
	 * @param e the e
	 */
	void setPosition(double x, double d, double e);

	/**
	 * Sets the up vector.
	 *
	 * @param i the i
	 * @param j the j
	 * @param k the k
	 */
	void setUpVector(double i, double j, double k);

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	double getDistance();

	/**
	 * Update cartesian coordinates from angles.
	 */
	void updateCartesianCoordinatesFromAngles();

	/**
	 * Update spherical coordinates from locations.
	 */
	default void updateSphericalCoordinatesFromLocations() {}

	/**
	 * Sets the initial Z factor corrector.
	 *
	 * @param factor the new initial Z factor corrector
	 */
	void setInitialZFactorCorrector(double factor);

}