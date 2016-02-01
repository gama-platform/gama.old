/*********************************************************************************************
 *
 *
 * 'ICamera.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.camera;

import java.awt.Point;
import org.eclipse.swt.events.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import msi.gama.metamodel.shape.GamaPoint;

/**
 * Class ICamera.
 *
 * @author drogoul
 * @since 5 sept. 2013
 *
 */
public interface ICamera extends org.eclipse.swt.events.KeyListener, MouseListener, MouseMoveListener, MouseTrackListener, MouseWheelListener {

	public final static double INIT_Z_FACTOR = 1.5;

	// Position, look & up

	public abstract void updatePosition(final double xPos, final double yPos, final double zPos);

	public abstract void lookPosition(final double xLPos, final double yLPos, final double zLPos);

	public abstract void upPosition(final double xPos, final double yPos, final double zPos);

	public abstract GamaPoint getPosition();

	public abstract GamaPoint getLookPosition();

	public abstract GamaPoint getUpPosition();

	// Update

	// public abstract void updateCamera(final GL2 gl, final int width, final int height);

	public abstract void makeGluLookAt(GLU glu);

	public abstract void resetCamera(final double envWidth, final double envHeight, boolean threeD);

	public abstract void zeroVelocity();

	public abstract void doInertia();

	// Picking

	public abstract boolean beginPicking(final GL2 gl);

	public abstract int endPicking(final GL2 gl);

	// Zoom

	public abstract Double zoomLevel();

	public abstract void zoomFocus(double centerX, double centerY, double centerZ, double extent);

	public abstract void zoom(boolean in);

	// public abstract void setRegionOfInterest(GamaPoint origin, GamaPoint end);

	// Mouse

	public abstract Point getMousePosition();

	public abstract Point getLastMousePressedPosition();

	// Properties

	public abstract double getPhi();

	public abstract boolean isViewIn2DPlan();

	/**
	 *
	 */
	public abstract void animate();

	/**
	 *
	 */
	public abstract void updateSphericalCoordinatesFromLocations();

	// public abstract boolean isEnableROIDrawing();

}