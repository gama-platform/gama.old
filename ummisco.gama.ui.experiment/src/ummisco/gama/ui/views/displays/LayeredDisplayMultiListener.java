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

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.WorkaroundForIssue1353;

public class LayeredDisplayMultiListener {

	private final LayeredDisplayDecorator view;
	private final IDisplaySurface surface;
	volatile boolean mouseIsDown;
	volatile boolean inMenu;
	volatile long lastEnterTime;
	volatile Point lastEnterPosition = new Point(0, 0);
	volatile boolean suppressNextEnter;
	final Consumer<Integer> keyListener;

	public LayeredDisplayMultiListener(final IDisplaySurface surface, final LayeredDisplayDecorator deco) {
		this.view = deco;
		this.surface = surface;

		keyListener = (keyCode) -> {
			switch (keyCode.intValue()) {
				case 'o':
					deco.toggleOverlay();
					break;
				case 'l':
					deco.toggleSideControls();
					break;
				case 'k':
					if (deco.isFullScreen()) {
						deco.toggleInteractiveConsole();
					}
					break;
				case 't':
					deco.toggleToolbar();
			}
		};
	}

	public void keyPressed(final char e) {
		surface.dispatchKeyEvent(e);
		WorkbenchHelper.asyncRun(view.displayOverlay);
	}

	public void keyReleased(final int e, final boolean command) {
		if (!command) { return; }
		keyListener.accept(e);
	}

	public void mouseEnter(final int x, final int y, final boolean modifier, final int button) {
		if (suppressNextEnter) {
			// System.out.println("One mouse enter suppressed");
			suppressNextEnter = false;
			return;
		}
		if (modifier) { return; }

		setMousePosition(x, y);
		if (button > 0) { return; }
		final long currentTime = System.currentTimeMillis();
		if (currentTime - lastEnterTime < 100 && lastEnterPosition.x == x && lastEnterPosition.y == y) { return; }
		lastEnterTime = System.currentTimeMillis();
		lastEnterPosition = new Point(x, y);
		// System.out.println("Mouse entering " + e);
		surface.dispatchMouseEvent(SWT.MouseEnter);
	}

	public void mouseExit(final int x, final int y, final boolean modifier, final int button) {
		final long currentTime = System.currentTimeMillis();
		if (currentTime - lastEnterTime < 100 && lastEnterPosition.x == x && lastEnterPosition.y == y) { return; }
		setMousePosition(-1, -1);
		if (button > 0) { return; }
		// System.out.println("Mouse exiting " + e);
		surface.dispatchMouseEvent(SWT.MouseExit);
		if (!view.isFullScreen() && WorkaroundForIssue1353.isInstalled()) {
			// suppressNextEnter = true;
			// System.out.println("Invoking WorkaroundForIssue1353");
			WorkaroundForIssue1353.showShell();
		}

	}

	public void mouseHover(final int button) {
		if (button > 0) { return; }
		// System.out.println("Mouse hovering on " + view.getPartName());
		surface.dispatchMouseEvent(SWT.MouseHover);
	}

	public void mouseMove(final int x, final int y, final boolean modifier) {
		WorkbenchHelper.asyncRun(view.displayOverlay);
		if (modifier) { return;
		// System.out.println("Mouse moving on " + view.getPartName());
		}

		if (mouseIsDown) {
			surface.draggedTo(x, y);
			surface.dispatchMouseEvent(SWT.DragDetect);
		} else {
			setMousePosition(x, y);
			surface.dispatchMouseEvent(SWT.MouseMove);
		}

	}

	public void mouseDown(final int x, final int y, final boolean modifier) {
		setMousePosition(x, y);
		if (inMenu) {
			inMenu = false;
			return;
		}
		if (modifier) { return; }
		mouseIsDown = true;
		// System.out.println("Mouse down on " + view.getPartName());
		surface.dispatchMouseEvent(SWT.MouseDown);
	}

	public void mouseUp(final int x, final int y, final boolean modifier) {
		// In case the mouse has moved (for example on a menu)
		if (!mouseIsDown) { return; }
		setMousePosition(x, y);
		if (modifier) { return; }
		mouseIsDown = false;
		// System.out.println("Mouse up on " + view.getPartName());
		if (!view.isFullScreen()) {
			WorkaroundForIssue1353.showShell();
		}
		surface.dispatchMouseEvent(SWT.MouseUp);
	}

	public void menuDetected(final int x, final int y) {
		if (inMenu) { return; }
		// System.out.println("Menu detected on " + view.getPartName());
		inMenu = true;
		setMousePosition(x, y);
		surface.selectAgentsAroundMouse();
	}

	public void dragDetected() {
		// System.out.println("Mouse drag detected on " + view.getPartName());
		// surface.draggedTo(e.x, e.y);
		surface.dispatchMouseEvent(SWT.DragDetect);
	}

	public void focusGained() {
		// if (!ok()) { return; }
		// if (suppressNextEnter) {
		// System.out.println("One mouse enter suppressed");
		// suppressNextEnter = false;
		// return;
		// }
		// System.out.println("Control has gained focus");
		// surface.dispatchMouseEvent(SWT.MouseEnter);
		// Thread.dumpStack();
	}

	public void focusLost() {
		// if (!ok()) { return; }
		// surface.dispatchMouseEvent(SWT.MouseExit);

		// System.out.println("Control has lost focus");
		// Thread.dumpStack();
	}

	private void setMousePosition(final int x, final int y) {
		surface.setMousePosition(x, y);
		GAMA.getGui().setMouseLocationInModel(surface.getModelCoordinates());
	}

}
