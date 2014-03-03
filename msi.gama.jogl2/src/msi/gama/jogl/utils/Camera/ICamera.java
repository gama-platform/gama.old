/**
 * Created by drogoul, 5 sept. 2013
 * 
 */
package msi.gama.jogl.utils.Camera;

import java.awt.Point;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import msi.gama.metamodel.shape.GamaPoint;

/**
 * Class ICamera.
 * 
 * @author drogoul
 * @since 5 sept. 2013
 * 
 */
public interface ICamera extends KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public final static double INIT_Z_FACTOR = 1.5;

	// Position, look & up

	public abstract void updatePosition(final double xPos, final double yPos, final double zPos);

	public abstract void lookPosition(final double xLPos, final double yLPos, final double zLPos);

	public abstract void upPosition(final double xPos, final double yPos, final double zPos);

	public abstract GamaPoint getPosition();

	// Update

	public abstract void updateCamera(final GL gl, final GLU glu, final int width, final int height);

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

	public abstract void setRegionOfInterest(GamaPoint origin, GamaPoint end);

	// Mouse

	public abstract Point getMousePosition();

	public abstract Point getLastMousePressedPosition();

	// Properties

	public abstract double getPhi();

	public abstract boolean isViewIn2DPlan();

	public abstract boolean isEnableROIDrawing();

}