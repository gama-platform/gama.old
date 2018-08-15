/*********************************************************************************************
 *
 * 'ICamera.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.camera;

import java.awt.Point;

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
	public static interface CameraPreset {
		void applyTo(AbstractCamera camera);
	}

	// Positions

	public abstract GamaPoint getPosition();

	public abstract GamaPoint getOrientation();

	public abstract GamaPoint getTarget();

	public abstract Point getMousePosition();

	public abstract Point getLastMousePressedPosition();

	// Commands

	public void setDistance(final double distance);

	public abstract void initialize();

	public abstract void update();

	public abstract void updatePosition();

	public abstract void updateTarget();

	public abstract void updateOrientation();

	public abstract void animate();

	public abstract void applyPreset(String preset);

	// Zoom

	public abstract Double zoomLevel();

	public abstract void zoom(boolean in);

	public abstract void zoom(double level);

	public abstract void zoomFocus(Envelope3D env);

	public abstract void setPosition(double x, double d, double e);

	public abstract void setUpVector(double i, double j, double k);

	public abstract double getDistance();

	public void updateCartesianCoordinatesFromAngles();

	default void updateSphericalCoordinatesFromLocations() {}

	public abstract void setInitialZFactorCorrector(double factor);

}