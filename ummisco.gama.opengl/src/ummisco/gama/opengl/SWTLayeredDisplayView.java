/**
 * Created by drogoul, 25 mars 2015
 *
 */
package ummisco.gama.opengl;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.runtime.GAMA;

/**
 * Class OpenGLLayeredDisplayView.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
public class SWTLayeredDisplayView extends LayeredDisplayView {

	SWTOpenGLDisplaySurface surface;

	public static String ID = "msi.gama.application.view.OpenGLDisplayView";

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {
		surface = new SWTOpenGLDisplaySurface(parent, getOutput());
		surfaceComposite = surface.renderer.getCanvas();
		// new DisplaySurfaceMenu(surface, surfaceComposite, this);
		surface.outputReloaded();
		return surfaceComposite;
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { surfaceComposite };
	}

	@Override
	public void setFocus() {
		if (surfaceComposite != null && !surfaceComposite.isFocusControl()) {
			surfaceComposite.forceFocus();
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

	@Override
	protected void updateOverlay() {
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

	boolean isOverlayTemporaryVisible;

	@Override
	public IDisplaySurface getDisplaySurface() {
		return surface;
	}

	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

}
