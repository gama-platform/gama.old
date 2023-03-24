/*******************************************************************************************************
 *
 * SWTLayeredDisplayMultiListener.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.outputs.layers.IEventLayerListener;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.bindings.GamaKeyBindings;

/**
 * The listener interface for receiving mouse and key events. When an event occurs, that object's appropriate method is
 * invoked.
 *
 */
public class SWTLayeredDisplayMultiListener implements MenuDetectListener, MouseListener, MouseMoveListener,
		MouseTrackListener, MouseWheelListener, KeyListener, DragDetectListener, FocusListener, IDisposable {

	static {
		DEBUG.ON();
	}

	/** The delegate. */
	final LayeredDisplayMultiListener delegate;

	/** The control. */
	Control control;

	/** The ok. */
	final Supplier<Boolean> ok;

	/** See issue #3308 */
	// final boolean isJava2DOnWindows;

	/** The UI zoom level. */
	// final double UIZoomLevel;

	/** The zoom levels with issues. */
	// final Set<Integer> zoomLevelsWithIssues = Set.of(175, 225, 250);

	/**
	 * Instantiates a new SWT layered display multi listener.
	 *
	 * @param deco
	 *            the deco
	 * @param surface
	 *            the surface
	 */
	public SWTLayeredDisplayMultiListener(final LayeredDisplayDecorator deco, final IDisplaySurface surface) {
		// int zoom = ViewsHelper.getMonitorOf(deco.view).getZoom();
		// isJava2DOnWindows = surface != null && !surface.getData().is3D() && PlatformHelper.isWindows()
		// && zoomLevelsWithIssues.contains(zoom);
		// UIZoomLevel = zoom / 100d;

		delegate = new LayeredDisplayMultiListener(surface, deco);
		control = deco.view.getInteractionControl();

		ok = () -> {
			final boolean viewOk = deco.view != null && !deco.view.disposed;
			if (!viewOk) return false;
			final boolean controlOk = control != null && !control.isDisposed();
			if (!controlOk) return false;
			// Removed to prevent views from stealing the focus w/o control
			// if (!control.isFocusControl()) { control.forceFocus(); }
			// if (!Objects.equals(WorkbenchHelper.getActivePart(), deco.view)) {
			// WorkbenchHelper.getPage().activate(deco.view);
			// }
			return surface != null && !surface.isDisposed();
		};

		control.addKeyListener(this);
		control.addMouseListener(this);
		control.addMenuDetectListener(this);
		control.addDragDetectListener(this);
		control.addMouseTrackListener(this);
		control.addMouseMoveListener(this);
		control.addFocusListener(this);

	}

	/**
	 * Dispose.
	 */
	@Override
	public void dispose() {
		if (control == null || control.isDisposed()) return;
		control.removeKeyListener(this);
		control.removeMouseListener(this);
		control.removeMenuDetectListener(this);
		control.removeDragDetectListener(this);
		control.removeMouseTrackListener(this);
		control.removeMouseMoveListener(this);
		control.removeFocusListener(this);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		if (!ok.get() || e.stateMask != 0) return;
		// DEBUG.OUT("Key pressed " + e);
		switch (e.keyCode) {
			case SWT.ARROW_DOWN:
				delegate.specialKeyPressed(IEventLayerListener.ARROW_DOWN);
				return;
			case SWT.ARROW_UP:
				delegate.specialKeyPressed(IEventLayerListener.ARROW_UP);
				return;
			case SWT.ARROW_LEFT:
				delegate.specialKeyPressed(IEventLayerListener.ARROW_LEFT);
				return;
			case SWT.ARROW_RIGHT:
				delegate.specialKeyPressed(IEventLayerListener.ARROW_RIGHT);
				return;
			case SWT.ESC:
				delegate.specialKeyPressed(IEventLayerListener.KEY_ESC);
				return;
			case SWT.TAB:
				delegate.specialKeyPressed(IEventLayerListener.KEY_TAB);
				return;
			case SWT.PAGE_DOWN:
				delegate.specialKeyPressed(IEventLayerListener.KEY_PAGE_DOWN);
				return;
			case SWT.PAGE_UP:
				delegate.specialKeyPressed(IEventLayerListener.KEY_PAGE_UP);
				return;
			case SWT.CR, SWT.KEYPAD_CR:
				delegate.specialKeyPressed(IEventLayerListener.KEY_RETURN);
				return;

		}
		delegate.keyPressed(e.character, GamaKeyBindings.ctrl(e));
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (!ok.get()) return;
		// DEBUG.OUT("Key released " + e);
		delegate.keyReleased(e.character, GamaKeyBindings.ctrl(e));
	}

	/**
	 * Filter.
	 *
	 * @param e
	 *            the e
	 */
	private void filter(final MouseEvent e) {
		// if (isJava2DOnWindows) {
		// e.x = (int) (e.x / UIZoomLevel);
		// e.y = (int) (e.y / UIZoomLevel);
		// }
	}

	@Override
	public void mouseScrolled(final MouseEvent e) {
		filter(e);
		// if (!ok.get()) {}
	}

	@Override
	public void mouseEnter(final MouseEvent e) {
		if (!ok.get()) return;
		filter(e);
		delegate.mouseEnter(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0, e.button);
	}

	@Override
	public void mouseExit(final MouseEvent e) {
		if (!ok.get()) return;
		filter(e);
		delegate.mouseExit(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0, e.button);
	}

	@Override
	public void mouseHover(final MouseEvent e) {
		if (!ok.get()) return;
		filter(e);
		delegate.mouseHover(e.x, e.y, e.button);
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if (!ok.get()) return;
		filter(e);
		// DEBUG.OUT("Mouse move " + e);
		delegate.mouseMove(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0);
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		// if (!ok.get()) {}
		// filter(e);
	}

	@Override
	public void mouseDown(final MouseEvent e) {
		if (!ok.get()) return;
		filter(e);
		DEBUG.OUT("Mouse down " + e);
		delegate.mouseDown(e.x, e.y, e.button, (e.stateMask & SWT.MODIFIER_MASK) != 0);
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		if (!ok.get()) return;
		filter(e);
		DEBUG.OUT("Mouse up " + e);
		delegate.mouseUp(e.x, e.y, e.button, (e.stateMask & SWT.MODIFIER_MASK) != 0);
	}

	@Override
	public void menuDetected(final MenuDetectEvent e) {
		if (!ok.get()) return;
		// Verify if the same "filter" is not needed here too.
		DEBUG.LOG("Menu detected ");
		final Point p = control.toControl(e.x, e.y);
		delegate.menuDetected(p.x, p.y);
	}

	@Override
	public void dragDetected(final DragDetectEvent e) {
		if (!ok.get()) return;
		filter(e);
		delegate.dragDetected(e.x, e.y);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		delegate.focusGained();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		delegate.focusLost();
	}

	/**
	 * Gets the key adapter for AWT. See Issue #3426
	 *
	 * @return the key adapter for AWT
	 */
	public java.awt.event.KeyListener getKeyAdapterForAWT() {
		return new java.awt.event.KeyListener() {

			long previous;

			@Override
			public void keyTyped(final java.awt.event.KeyEvent e) {
				// Necessary to filter by the time to avoid repetitions
				if (e.getWhen() == previous) return;
				previous = e.getWhen();
				DEBUG.LOG("Key received by the AWT listener. Code " + e.getKeyCode() + " Action ? " + e.isActionKey());
				if (!e.isActionKey()) {
					delegate.keyPressed(e.getKeyChar(), e.isControlDown());
				} else if (e.getModifiersEx() == 0) {
					delegate.specialKeyPressed(switch (e.getKeyCode()) {
						case java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_KP_UP -> IEventLayerListener.ARROW_UP;
						case java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.VK_KP_DOWN -> IEventLayerListener.ARROW_DOWN;
						case java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_KP_LEFT -> IEventLayerListener.ARROW_LEFT;
						case java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.KeyEvent.VK_KP_RIGHT -> IEventLayerListener.ARROW_RIGHT;
						case java.awt.event.KeyEvent.VK_PAGE_UP -> IEventLayerListener.KEY_PAGE_UP;
						case java.awt.event.KeyEvent.VK_PAGE_DOWN -> IEventLayerListener.KEY_PAGE_DOWN;
						case java.awt.event.KeyEvent.VK_ESCAPE -> IEventLayerListener.KEY_ESC;
						case java.awt.event.KeyEvent.VK_ENTER -> IEventLayerListener.KEY_RETURN;
						case java.awt.event.KeyEvent.VK_TAB -> IEventLayerListener.KEY_TAB;
						default -> 0;
					});
				}
			}

			@Override
			public void keyPressed(final java.awt.event.KeyEvent e) {}

			@Override
			public void keyReleased(final java.awt.event.KeyEvent e) {}
		};
	}

	/**
	 * Gets the mouse adapter for AWT.
	 *
	 * @return the mouse adapter for AWT
	 */
	public java.awt.event.MouseMotionListener getMouseAdapterForAWT() {
		return new java.awt.event.MouseMotionListener() {

			@Override
			public void mouseDragged(final java.awt.event.MouseEvent e) {
				delegate.dragDetected(e.getX(), e.getY());
			}

			@Override
			public void mouseMoved(final java.awt.event.MouseEvent e) {
				delegate.mouseMove(e.getX(), e.getY(), e.getModifiers() != 0);
			}
		};
	}

}
