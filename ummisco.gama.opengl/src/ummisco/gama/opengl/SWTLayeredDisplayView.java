/**
 * Created by drogoul, 25 mars 2015
 *
 */
package ummisco.gama.opengl;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.gui.swt.SwtGui;
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

	boolean isOverlayTemporaryVisible;

	public static String ID = "msi.gama.application.view.OpenGLDisplayView";

	@Override
	public SWTOpenGLDisplaySurface getDisplaySurface() {
		return (SWTOpenGLDisplaySurface) super.getDisplaySurface();
	}

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {
		final SWTOpenGLDisplaySurface surface = new SWTOpenGLDisplaySurface(parent, getOutput());
		surfaceComposite = surface.renderer.getCanvas();
		surface.outputReloaded();
		return surfaceComposite;
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { surfaceComposite };
	}

	@Override
	public void setFocus() {
		if (surfaceComposite != null && !surfaceComposite.isDisposed() && !surfaceComposite.isFocusControl()) {
			surfaceComposite.forceFocus();
		}
	}

	@Override
	public void close() {

		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				try {
					if (getDisplaySurface() != null) {
						getDisplaySurface().dispose();
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
		if (getDisplaySurface().getROIDimensions() != null) {
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

	/**
	 * Wait for the OpenGL environment to be initialized, preventing a wait when
	 * two or more views are open at the same time. Should be called in the SWT
	 * thread. On MacOS X, for example, it seems necessary to show the view,
	 * even briefly, to make the JOGL Canvas "realized"
	 * 
	 * @see msi.gama.common.interfaces.IGamaView#waitToBeRealized()
	 */

	@Override
	public void waitToBeRealized() {

		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				SwtGui.getPage().bringToTop(SWTLayeredDisplayView.this);

			}
		});
	}
}
