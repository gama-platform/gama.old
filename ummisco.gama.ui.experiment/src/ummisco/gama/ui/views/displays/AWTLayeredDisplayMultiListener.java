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

import static ummisco.gama.ui.bindings.GamaKeyBindings.ctrl;
import static ummisco.gama.ui.bindings.GamaKeyBindings.shift;

import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Objects;
import java.util.function.Supplier;

import javax.swing.JComponent;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.access.GamlSearchField;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class AWTLayeredDisplayMultiListener
		implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener {

	final LayeredDisplayMultiListener delegate;
	final JComponent component;
	final Supplier<Boolean> ok;

	public AWTLayeredDisplayMultiListener(final LayeredDisplayDecorator deco, final JComponent component,
			final IDisplaySurface surface) {
		this.component = component;

		delegate = new LayeredDisplayMultiListener(surface, deco);

		ok = () -> {
			final boolean viewOk = deco.view != null && !deco.view.disposed;
			if (!viewOk) { return false; }
			final boolean controlOk = surface != null && !surface.isDisposed();
			if (!controlOk) { return false; }
			if (!component.isFocusOwner()) {
				component.requestFocus();
			}
			if (!Objects.equals(WorkbenchHelper.getActivePart(), deco.view)) {
				WorkbenchHelper.asyncRun(() -> WorkbenchHelper.getPage().activate(deco.view));
			}
			return true;
		};

		component.addKeyListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addFocusListener(this);
	}

	public void dispose() {
		if (component == null) { return; }
		component.removeKeyListener(this);
		component.removeMouseListener(this);
		component.removeMouseMotionListener(this);
		component.removeFocusListener(this);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.keyPressed()" + e);
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.keyReleased()" + e);
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.keyTyped()" + e);
		if (!ok.get()) { return; }
		switch (e.getKeyCode()) {

			case 'h':
				if (ctrl(e) && shift(e)) {
					GamlSearchField.INSTANCE.search();
				}
				return;
			// Handles START & RELOAD
			case 'p':
				if (ctrl(e) && shift(e)) {
					GAMA.stepFrontmostExperiment();
				} else if (ctrl(e)) {
					GAMA.startPauseFrontmostExperiment();
				}
				return;
			// Handles PAUSE & STEP
			case 'r':
				if (ctrl(e) && shift(e)) {
					GAMA.relaunchFrontmostExperiment();
				} else if (ctrl(e)) {
					GAMA.reloadFrontmostExperiment();
				}
				return;
			// Handles CLOSE
			case 'x':
				if (ctrl(e) && shift(e)) {
					GAMA.closeAllExperiments(true, false);
				}
				return;
		}
		delegate.keyPressed(e.getKeyChar());
		delegate.keyReleased(e.getKeyCode(), GamaKeyBindings.ctrl(e));
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseWheelMoved()" + e);
		if (!ok.get()) { return; }
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseEntered()" + e);
		if (!ok.get()) { return; }
		delegate.mouseEnter(e.getX(), e.getY(), e.getModifiers() == 0, e.getButton());
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseExited()" + e);
		if (!ok.get()) { return; }
		delegate.mouseExit(e.getX(), e.getY(), e.getModifiers() == 0, e.getButton());
	}

	// @Override
	public void mouseHover(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseHover()" + e);
		if (!ok.get()) { return; }
		delegate.mouseHover(e.getButton());
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseMoved()" + e);
		if (!ok.get()) { return; }
		delegate.mouseMove(e.getX(), e.getY(), e.getModifiers() == 0);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mousePressed()" + e);
		if (!ok.get()) { return; }
		if ((e.getModifiers() & MouseEvent.META_DOWN_MASK) != 0) {
			menuDetected(e);
		} else {
			delegate.mouseDown(e.getX(), e.getY(), e.getModifiers() == 0);
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseReleased()" + e);
		if (!ok.get()) { return; }
		delegate.mouseUp(e.getX(), e.getY(), e.getModifiers() == 0);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseClicked()" + e);
		mouseReleased(e);
	}

	public void menuDetected(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.menuDetected()" + e);
		if (!ok.get()) { return; }
		// System.out.println("Menu detected on " + view.getPartName());
		final Point p = component.getMousePosition();
		delegate.menuDetected(p.x, p.y);
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.mouseDragged()" + e);
		if (!ok.get()) { return; }
		delegate.dragDetected();
	}

	@Override
	public void focusGained(final FocusEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.focusGained()" + e);
		delegate.focusGained();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		System.out.println("AWTLayeredDisplayMultiListener.focusLost()" + e);
		delegate.focusLost();
	}

}
