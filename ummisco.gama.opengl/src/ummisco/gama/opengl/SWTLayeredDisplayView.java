/**
 * Created by drogoul, 25 mars 2015
 *
 */
package ummisco.gama.opengl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

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
	Composite parentOfSurfaceComposite;
	Shell fullScreenShell;

	public static String ID = "msi.gama.application.view.OpenGLDisplayView";

	@Override
	public SWTOpenGLDisplaySurface getDisplaySurface() {
		return (SWTOpenGLDisplaySurface) super.getDisplaySurface();
	}

	public void toggleFullScreen() {
		final boolean isFullScreen = fullScreenShell != null;
		if (isFullScreen) {
			surfaceComposite.setParent(parentOfSurfaceComposite);
			parentOfSurfaceComposite.layout(true, true);
			destroyFullScreenShell();
			surfaceComposite.setFocus();

		} else {
			createFullScreenShell();
			surfaceComposite.setParent(fullScreenShell);
			fullScreenShell.layout(true, true);
			fullScreenShell.setVisible(true);
			surfaceComposite.setFocus();
		}
	}

	private void createFullScreenShell() {
		if (fullScreenShell != null)
			return;
		fullScreenShell = new Shell(SwtGui.getDisplay(), SWT.ON_TOP | SWT.APPLICATION_MODAL);
		fullScreenShell.setBounds(SwtGui.getDisplay().getBounds());
		final GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		fullScreenShell.setLayout(gl);
		// fullScreenShell.setAlpha(100);
		// fullScreenShell.setFullScreen(true);
	}

	private void destroyFullScreenShell() {
		if (fullScreenShell == null)
			return;
		fullScreenShell.close();
		fullScreenShell.dispose();
		fullScreenShell = null;
	}

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {
		parentOfSurfaceComposite = parent;
		final SWTOpenGLDisplaySurface surface = new SWTOpenGLDisplaySurface(parent, getOutput());
		surfaceComposite = surface.renderer.getCanvas();
		surfaceComposite.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.character == SWT.ESC) {
					toggleFullScreen();
				}

			}

			@Override
			public void keyPressed(final KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		surface.outputReloaded();
		return surfaceComposite;
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		if (getOutput().getData().isFullScreen()) {
			toggleFullScreen();
		}
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
		// if (!Platform.isCocoa()) {
		// return;
		// }
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				SwtGui.getPage().bringToTop(SWTLayeredDisplayView.this);

			}
		});
	}
}
