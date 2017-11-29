/*********************************************************************************************
 *
 * 'LayeredDisplayMultiListener.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.displays;

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
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.WorkaroundForIssue1353;

public class LayeredDisplayMultiListener implements MenuDetectListener, MouseListener, MouseMoveListener,
		MouseTrackListener, MouseWheelListener, KeyListener, DragDetectListener, FocusListener {

	final LayeredDisplayDecorator view;
	final IDisplaySurface surface;
	final Control control;
	volatile boolean mouseIsDown;
	volatile boolean inMenu;
	long lastEnterTime;
	Point lastEnterPosition = new Point(0, 0);
	final DisplayKeyListener delegate = new DisplayKeyListener();

	private class DisplayKeyListener implements KeyListener {

		@Override
		public void keyPressed(final KeyEvent e) {}

		@Override
		public void keyReleased(final KeyEvent e) {
			switch (e.keyCode) {
				case 'o':
					if (GamaKeyBindings.ctrl(e)) {
						view.toggleOverlay();
					}
					break;
				case 'l':
					if (GamaKeyBindings.ctrl(e)) {
						view.toggleSideControls();
					}
					break;
				case 'k':
					if (GamaKeyBindings.ctrl(e)) {
						if (view.isFullScreen())
							view.toggleInteractiveConsole();
					}
					break;
				case 't':
					if (GamaKeyBindings.ctrl(e)) {
						if (view.isFullScreen())
							view.toggleSimulationControls();
					}
			}

		}

	}

	public LayeredDisplayMultiListener(final LayeredDisplayDecorator view, final IDisplaySurface surface) {
		this.view = view;
		this.surface = surface;
		control = view.view.getZoomableControls()[0];
		control.addKeyListener(this);
		control.addMouseListener(this);
		control.addMenuDetectListener(this);
		control.addDragDetectListener(this);
		control.addMouseTrackListener(this);
		control.addMouseMoveListener(this);
		control.addFocusListener(this);
	}

	public void dispose() {
		if (control == null || control.isDisposed())
			return;
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
		if (!ok())
			return;
		surface.dispatchKeyEvent(e.character);
		WorkbenchHelper.asyncRun(view.displayOverlay);
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (!ok())
			return;
		delegate.keyReleased(e);
	}

	@Override
	public void mouseScrolled(final MouseEvent e) {
		if (!ok())
			return;
	}

	@Override
	public void mouseEnter(final MouseEvent e) {
		if (!ok())
			return;
		if ((e.stateMask & SWT.MODIFIER_MASK) != 0)
			return;

		setMousePosition(e.x, e.y);
		if (e.button > 0)
			return;
		lastEnterTime = System.currentTimeMillis();
		lastEnterPosition = new Point(e.x, e.y);
		// System.out.println("Mouse entering " + e);
		surface.dispatchMouseEvent(SWT.MouseEnter);
	}

	@Override
	public void mouseExit(final MouseEvent e) {
		if (!ok())
			return;
		final long currentTime = System.currentTimeMillis();
		if (currentTime - lastEnterTime < 100 && lastEnterPosition.x == e.x && lastEnterPosition.y == e.y) { return; }
		setMousePosition(-1, -1);
		if (e.button > 0)
			return;
		// System.out.println("Mouse exiting " + e);
		if (!view.isFullScreen())
			WorkaroundForIssue1353.showShell();
		surface.dispatchMouseEvent(SWT.MouseExit);
	}

	@Override
	public void mouseHover(final MouseEvent e) {
		if (!ok())
			return;
		if (e.button > 0)
			return;
		// System.out.println("Mouse hovering on " + view.getPartName());
		surface.dispatchMouseEvent(SWT.MouseHover);
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if (!ok())
			return;
		WorkbenchHelper.asyncRun(view.displayOverlay);
		if ((e.stateMask & SWT.MODIFIER_MASK) != 0)
			return;
		// System.out.println("Mouse moving on " + view.getPartName());

		if (mouseIsDown) {
			surface.draggedTo(e.x, e.y);
			surface.dispatchMouseEvent(SWT.DragDetect);
		} else {
			setMousePosition(e.x, e.y);
			surface.dispatchMouseEvent(SWT.MouseMove);
		}

	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		if (!ok())
			return;
	}

	@Override
	public void mouseDown(final MouseEvent e) {
		if (!ok())
			return;
		setMousePosition(e.x, e.y);
		if (inMenu) {
			inMenu = false;
			return;
		}
		if ((e.stateMask & SWT.MODIFIER_MASK) != 0)
			return;
		mouseIsDown = true;
		// System.out.println("Mouse down on " + view.getPartName());
		surface.dispatchMouseEvent(SWT.MouseDown);
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		if (!ok())
			return;

		// In case the mouse has moved (for example on a menu)
		if (!mouseIsDown)
			return;
		setMousePosition(e.x, e.y);
		if ((e.stateMask & SWT.MODIFIER_MASK) != 0)
			return;
		mouseIsDown = false;
		// System.out.println("Mouse up on " + view.getPartName());
		if (!view.isFullScreen())
			WorkaroundForIssue1353.showShell();
		surface.dispatchMouseEvent(SWT.MouseUp);
	}

	@Override
	public void menuDetected(final MenuDetectEvent e) {
		if (!ok())
			return;
		if (inMenu) // In case a double event is sent
			return;
		// System.out.println("Menu detected on " + view.getPartName());
		final Point p = control.toControl(e.x, e.y);
		final int x = p.x;
		final int y = p.y;
		inMenu = true;
		setMousePosition(x, y);
		surface.selectAgentsAroundMouse();
	}

	@Override
	public void dragDetected(final DragDetectEvent e) {
		if (!ok())
			return;
		// System.out.println("Mouse drag detected on " + view.getPartName());
		// surface.draggedTo(e.x, e.y);
		surface.dispatchMouseEvent(SWT.DragDetect);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		if (!ok())
			return;
		// System.out.println("Control has gained focus");
		surface.dispatchMouseEvent(SWT.MouseEnter);
		// Thread.dumpStack();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		// if ( !ok() )
		// return;
		// System.out.println("Control has lost focus");
		// Thread.dumpStack();
	}

	private boolean ok() {
		final boolean viewOk = view != null && !view.view.disposed;
		if (!viewOk)
			return false;
		final boolean controlOk = control != null && !control.isDisposed();
		if (!controlOk)
			return false;
		final boolean surfaceOk = surface != null && !surface.isDisposed();
		if (!control.isFocusControl())
			control.forceFocus();
		if (WorkbenchHelper.getActivePart() != view) {
			WorkbenchHelper.getPage().activate(view.view);
		}
		return surfaceOk;
	}

	private void setMousePosition(final int x, final int y) {
		surface.setMousePosition(x, y);
		GAMA.getGui().setMouseLocationInModel(surface.getModelCoordinates());
	}

}
