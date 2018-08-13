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

import java.util.Objects;
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
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class SWTLayeredDisplayMultiListener implements MenuDetectListener, MouseListener, MouseMoveListener,
		MouseTrackListener, MouseWheelListener, KeyListener, DragDetectListener, FocusListener {

	final LayeredDisplayMultiListener delegate;
	final Control control;
	final Supplier<Boolean> ok;

	public SWTLayeredDisplayMultiListener(final LayeredDisplayDecorator deco, final IDisplaySurface surface) {

		delegate = new LayeredDisplayMultiListener(surface, deco);
		control = deco.view.getZoomableControls()[0];

		ok = () -> {
			final boolean viewOk = deco.view != null && !deco.view.disposed;
			if (!viewOk) { return false; }
			final boolean controlOk = control != null && !control.isDisposed();
			if (!controlOk) { return false; }
			final boolean surfaceOk = surface != null && !surface.isDisposed();
			if (!control.isFocusControl()) {
				control.forceFocus();
			}
			if (!Objects.equals(WorkbenchHelper.getActivePart(), deco.view)) {
				WorkbenchHelper.getPage().activate(deco.view);
			}
			return surfaceOk;
		};

		control.addKeyListener(this);
		control.addMouseListener(this);
		control.addMenuDetectListener(this);
		control.addDragDetectListener(this);
		control.addMouseTrackListener(this);
		control.addMouseMoveListener(this);
		control.addFocusListener(this);
	}

	public void dispose() {
		if (control == null || control.isDisposed()) { return; }
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
		if (!ok.get()) { return; }
		delegate.keyPressed(e.character);
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (!ok.get()) { return; }
		delegate.keyReleased(e.keyCode, GamaKeyBindings.ctrl(e));
	}

	@Override
	public void mouseScrolled(final MouseEvent e) {
		if (!ok.get()) { return; }
	}

	@Override
	public void mouseEnter(final MouseEvent e) {
		if (!ok.get()) { return; }
		delegate.mouseEnter(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0, e.button);
	}

	@Override
	public void mouseExit(final MouseEvent e) {
		if (!ok.get()) { return; }
		delegate.mouseExit(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0, e.button);
	}

	@Override
	public void mouseHover(final MouseEvent e) {
		if (!ok.get()) { return; }
		delegate.mouseHover(e.button);
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if (!ok.get()) { return; }
		delegate.mouseMove(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0);
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		if (!ok.get()) { return; }
	}

	@Override
	public void mouseDown(final MouseEvent e) {
		if (!ok.get()) { return; }
		delegate.mouseDown(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0);
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		if (!ok.get()) { return; }
		delegate.mouseUp(e.x, e.y, (e.stateMask & SWT.MODIFIER_MASK) != 0);
	}

	@Override
	public void menuDetected(final MenuDetectEvent e) {
		if (!ok.get()) { return; }
		// DEBUG.LOG("Menu detected on " + view.getPartName());
		final Point p = control.toControl(e.x, e.y);
		delegate.menuDetected(p.x, p.y);
	}

	@Override
	public void dragDetected(final DragDetectEvent e) {
		if (!ok.get()) { return; }
		delegate.dragDetected();
	}

	@Override
	public void focusGained(final FocusEvent e) {
		delegate.focusGained();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		delegate.focusLost();
	}

}
