package msi.gama.gui.views;

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
import msi.gama.gui.swt.WorkaroundForIssue1353;
import msi.gama.runtime.GAMA;

public class LayeredDisplayMultiListener implements MenuDetectListener, MouseListener, MouseMoveListener, MouseTrackListener, MouseWheelListener, KeyListener, DragDetectListener, FocusListener {

	final LayeredDisplayView view;
	final Control control;
	volatile boolean mouseIsDown;
	volatile boolean inMenu;
	long lastEnterTime;
	Point lastEnterPosition = new Point(0, 0);

	public LayeredDisplayMultiListener(final LayeredDisplayView view) {
		this.view = view;
		control = view.getZoomableControls()[0];
		control.addKeyListener(this);
		control.addMouseListener(this);
		control.addMenuDetectListener(this);
		control.addDragDetectListener(this);
		control.addMouseTrackListener(this);
		// control.addMouseWheelListener(this);
		control.addMouseMoveListener(this);
		control.addFocusListener(this);
	}

	public void dispose() {
		if ( control == null || control.isDisposed() )
			return;
		control.removeKeyListener(this);
		control.removeMouseListener(this);
		control.removeMenuDetectListener(this);
		control.removeDragDetectListener(this);
		control.removeMouseTrackListener(this);
		// control.removeMouseWheelListener(this);
		control.removeMouseMoveListener(this);
		control.removeFocusListener(this);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		view.getDisplaySurface().dispatchKeyEvent(e.character);
		GAMA.getGui().asyncRun(view.displayOverlay);
	}

	@Override
	public void keyReleased(final KeyEvent e) {}

	@Override
	public void mouseScrolled(final MouseEvent e) {}

	@Override
	public void mouseEnter(final MouseEvent e) {
		if ( (e.stateMask & SWT.MODIFIER_MASK) != 0 )
			return;
		view.getDisplaySurface().setMousePosition(e.x, e.y);
		if ( e.button > 0 )
			return;
		lastEnterTime = System.currentTimeMillis();
		lastEnterPosition = new Point(e.x, e.y);
		// System.out.println("Mouse entering " + e);
		view.getDisplaySurface().dispatchMouseEvent(SWT.MouseEnter);
	}

	@Override
	public void mouseExit(final MouseEvent e) {
		final long currentTime = System.currentTimeMillis();
		if ( currentTime - lastEnterTime < 100 && lastEnterPosition.x == e.x && lastEnterPosition.y == e.y ) { return; }
		view.getDisplaySurface().setMousePosition(-1, -1);
		if ( e.button > 0 )
			return;
		// System.out.println("Mouse exiting " + e);
		WorkaroundForIssue1353.showShell();
		view.getDisplaySurface().dispatchMouseEvent(SWT.MouseExit);
	}

	@Override
	public void mouseHover(final MouseEvent e) {

		if ( e.button > 0 )
			return;
		// System.out.println("Mouse hovering on " + view.getPartName());
		view.getDisplaySurface().dispatchMouseEvent(SWT.MouseHover);
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if ( (e.stateMask & SWT.MODIFIER_MASK) != 0 )
			return;
		// System.out.println("Mouse moving on " + view.getPartName());
		if ( mouseIsDown ) {
			view.getDisplaySurface().draggedTo(e.x, e.y);
			view.getDisplaySurface().dispatchMouseEvent(SWT.DragDetect);
		} else {
			view.getDisplaySurface().setMousePosition(e.x, e.y);
			view.getDisplaySurface().dispatchMouseEvent(SWT.MouseMove);
		}
		GAMA.getGui().asyncRun(view.displayOverlay);

	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {}

	@Override
	public void mouseDown(final MouseEvent e) {
		view.getDisplaySurface().setMousePosition(e.x, e.y);
		if ( inMenu ) {
			inMenu = false;
			return;
		}
		if ( (e.stateMask & SWT.MODIFIER_MASK) != 0 )
			return;
		mouseIsDown = true;
		// System.out.println("Mouse down on " + view.getPartName());
		view.getDisplaySurface().dispatchMouseEvent(SWT.MouseDown);
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		// In case the mouse has moved (for example on a menu)
		if ( !mouseIsDown )
			return;
		view.getDisplaySurface().setMousePosition(e.x, e.y);
		if ( (e.stateMask & SWT.MODIFIER_MASK) != 0 )
			return;
		mouseIsDown = false;
		// System.out.println("Mouse up on " + view.getPartName());
		view.getDisplaySurface().dispatchMouseEvent(SWT.MouseUp);
	}

	@Override
	public void menuDetected(final MenuDetectEvent e) {
		if ( inMenu ) // In case a double event is sent
			return;
		// System.out.println("Menu detected on " + view.getPartName());
		final Point p = control.toControl(e.x, e.y);
		final int x = p.x;
		final int y = p.y;
		inMenu = true;
		view.getDisplaySurface().setMousePosition(x, y);
		view.getDisplaySurface().selectAgentsAroundMouse();
	}

	@Override
	public void dragDetected(final DragDetectEvent e) {
		view.getDisplaySurface().dispatchMouseEvent(SWT.DragDetect);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		// System.out.println("Control has gained focus");
		view.getDisplaySurface().dispatchMouseEvent(SWT.MouseEnter);
		// Thread.dumpStack();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		// System.out.println("Control has lost focus");
		// Thread.dumpStack();
	}

}
