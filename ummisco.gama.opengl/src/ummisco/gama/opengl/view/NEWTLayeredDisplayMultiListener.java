/*******************************************************************************************************
 *
 * NEWTLayeredDisplayMultiListener.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import java.util.Objects;
import java.util.function.Supplier;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.runtime.PlatformHelper;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.displays.LayeredDisplayDecorator;
import ummisco.gama.ui.views.displays.LayeredDisplayMultiListener;

/**
 * A listener for NEWT events
 *
 */
public class NEWTLayeredDisplayMultiListener implements MouseListener, KeyListener, WindowListener, IDisposable {

	static {
		DEBUG.OFF();
	}

	/** The delegate. */
	final LayeredDisplayMultiListener delegate;

	/** The control. */
	final Window control;

	/** The ok. */
	final Supplier<Boolean> ok;

	/**
	 * Instantiates a new NEWT layered display multi listener.
	 *
	 * @param deco
	 *            the deco
	 * @param surface
	 *            the surface
	 * @param window
	 *            the window
	 */
	public NEWTLayeredDisplayMultiListener(final LayeredDisplayDecorator deco, final IDisplaySurface surface,
			final Window window) {

		delegate = new LayeredDisplayMultiListener(surface, deco);
		control = window;

		ok = () -> {
			final boolean viewOk = deco.view != null && !deco.view.disposed;
			if (!viewOk) return false;
			final boolean controlOk = control != null /* && !control.isDisposed() */;
			if (!controlOk) return false;
			final boolean surfaceOk = surface != null && !surface.isDisposed();
//			if (!Objects.equals(WorkbenchHelper.getActivePart(), deco.view)) {
//				WorkbenchHelper.getPage().activate(deco.view);
//			}
			return surfaceOk;
		};

		control.addKeyListener(this);
		control.addMouseListener(this);
		control.addWindowListener(this);
	}

	/**
	 * Dispose.
	 */
	@Override
	public void dispose() {
		control.removeKeyListener(this);
		control.removeMouseListener(this);
		control.removeWindowListener(this);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		DEBUG.OUT("Key pressed: " + e);
		if (!ok.get()) return;
		delegate.keyPressed(e.getKeyChar());
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		DEBUG.OUT("Key released: " + e);
		if (!ok.get()) return;
		delegate.keyReleased(e.getKeyCode(),
				PlatformHelper.isMac() ? e.isMetaDown() : e.isControlDown() /* ?? GamaKeyBindings.ctrl(e) */);
	}

	/**
	 * Checks for modifiers.
	 *
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	private boolean hasModifiers(final MouseEvent e) {
		return e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isMetaDown() || e.isShiftDown();
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseEnter(e.getX(), e.getY(), hasModifiers(e), e.getButton());
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseExit(e.getX(), e.getY(), hasModifiers(e), e.getButton());
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseMove(e.getX(), e.getY(), hasModifiers(e));
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (!ok.get()) return;
		DEBUG.OUT("Mouse pressed with button " + e.getButton() + " modifiers " + e.getModifiersString(null));
		delegate.mouseDown(e.getX(), e.getY(), e.getButton(), hasModifiers(e));
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseUp(e.getX(), e.getY(), e.getButton(), hasModifiers(e));
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.dragDetected(e.getX(), e.getY());
	}

	@Override
	public void windowResized(final WindowEvent e) {}

	@Override
	public void windowMoved(final WindowEvent e) {}

	@Override
	public void windowDestroyNotify(final WindowEvent e) {}

	@Override
	public void windowDestroyed(final WindowEvent e) {}

	@Override
	public void windowGainedFocus(final WindowEvent e) {
		if (!ok.get()) return;
		delegate.focusGained();
	}

	@Override
	public void windowLostFocus(final WindowEvent e) {
		if (!ok.get()) return;
		delegate.focusLost();

	}

	@Override
	public void windowRepaint(final WindowUpdateEvent e) {}

	@Override
	public void mouseClicked(final MouseEvent e) {
		this.mouseReleased(e);
	}

	@Override
	public void mouseWheelMoved(final MouseEvent e) {
		this.mouseMoved(e);
	}

}
