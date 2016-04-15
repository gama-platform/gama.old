/**
 * Created by drogoul, 25 mars 2015
 *
 */
package ummisco.gama.opengl;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.runtime.GAMA;

/**
 * Class OpenGLLayeredDisplayView.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
public class SWTLayeredDisplayView extends LayeredDisplayView
		implements /* ControlListener, */MouseMoveListener, KeyListener {

	SWTOpenGLDisplaySurface surface;

	public static String ID = "msi.gama.application.view.OpenGLDisplayView";

	@Override
	protected Composite createSurfaceComposite() {
		surface = new SWTOpenGLDisplaySurface(parent, getOutput());
		surfaceComposite = surface.renderer.getCanvas();
		surfaceComposite.addMouseMoveListener(this);
		surfaceComposite.addKeyListener(this);
		surface.setSWTMenuManager(new DisplaySurfaceMenu(surface, surfaceComposite, this));
		surface.outputReloaded();
		return surfaceComposite;
	}

	@Override
	public void setFocus() {
		if (surfaceComposite != null) {
			surfaceComposite.setFocus();
		}
	}

	@Override
	public void close() {

		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				try {
					if (surface != null) {
						surface.dispose();
					}
					getSite().getPage().hideView(SWTLayeredDisplayView.this);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	boolean isOverlayTemporaryVisible;

	@Override
	public void mouseMove(final MouseEvent e) {
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				if (surface.getROIDimensions() != null) {
					if (!overlay.isVisible()) {
						isOverlayTemporaryVisible = true;
						overlay.setVisible(true);
					}
				} else {
					if (isOverlayTemporaryVisible) {
						isOverlayTemporaryVisible = false;
						overlay.setVisible(false);
					}
				}
				overlay.update();
			}
		});
	}

	@Override
	public IDisplaySurface getDisplaySurface() {
		return surface;
	}

	/**
	 * Method zoomWhenScrolling()
	 * 
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Zoomable#zoomWhenScrolling()
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				overlay.update();
			}
		});
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				overlay.update();
			}
		});
	}

}
