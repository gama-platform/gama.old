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

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import com.jogamp.opengl.GL2;

import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;

/**
 * Class ICamera.
 *
 * @author drogoul
 * @since 5 sept. 2013
 *
 */
public interface ICamera extends org.eclipse.swt.events.KeyListener, MouseListener, MouseMoveListener,
		MouseTrackListener, MouseWheelListener {

	public final static double INIT_Z_FACTOR = 1.5;

	// Positions

	public abstract GamaPoint getPosition();

	public abstract Point getMousePosition();

	public abstract Point getLastMousePressedPosition();

	// Commands

	public abstract void update();

	public abstract void reset();

	public abstract void animate();

	// Picking

	public abstract boolean beginPicking(final GL2 gl);

	public abstract int endPicking(final GL2 gl);

	// Zoom

	public abstract Double zoomLevel();

	public abstract void zoomFocus(IShape shape);

	public abstract void zoom(boolean in);

	public abstract void zoomRoi(Envelope3D env);

}