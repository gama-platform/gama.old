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
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import ummisco.gama.opengl.Abstract3DRenderer;

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

	public final static double INIT_Z_FACTOR = 1.5;
	public final static GamaPoint UNDEFINED = new GamaPoint();

	public static Map<String, CameraPreset> PRESETS = new LinkedHashMap<>();

	// Positions

	public abstract GamaPoint getPosition();

	public abstract GamaPoint getOrientation();

	public abstract GamaPoint getTarget();

	public abstract Point getMousePosition();

	public abstract Point getLastMousePressedPosition();

	// Commands

	public abstract void initialize();

	public abstract void update();

	public abstract void updatePosition();

	public abstract void updateTarget();

	public abstract void updateOrientation();

	// public abstract void reset();

	public abstract void animate();

	public abstract void applyPreset(String preset);

	// Zoom

	public abstract Double zoomLevel();

	public abstract void zoomFocus(IShape shape);

	public abstract void zoom(boolean in);

	public abstract void zoom(double level);

	public abstract void zoomRoi(Envelope3D env);

	public abstract void toggleStickyROI();

	public abstract boolean isROISticky();

	public abstract boolean inKeystoneMode();

	public abstract Abstract3DRenderer getRenderer();

	public abstract void setPosition(double x, double d, double e);

	public abstract void setUpVector(double i, double j, double k);

}