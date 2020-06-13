/*********************************************************************************************
 *
 * 'ICamera.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
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
		MouseTrackListener, MouseWheelListener {
	@FunctionalInterface
	public interface CameraPreset {
		void applyTo(AbstractCamera camera);
	}

	// Positions

	GamaPoint getPosition();

	GamaPoint getOrientation();

	GamaPoint getTarget();

	GamaPoint getMousePosition();

	GamaPoint getLastMousePressedPosition();

	// Commands

	void setDistance(final double distance);

	void initialize();

	void update();

	void updatePosition();

	void updateTarget();

	void updateOrientation();

	void animate();

	void applyPreset(String preset);

	// Zoom

	Double zoomLevel();

	void zoom(boolean in);

	void zoom(double level);

	void zoomFocus(Envelope3D env);

	void setPosition(double x, double d, double e);

	void setUpVector(double i, double j, double k);

	double getDistance();

	void updateCartesianCoordinatesFromAngles();

	default void updateSphericalCoordinatesFromLocations() {}

	void setInitialZFactorCorrector(double factor);

}