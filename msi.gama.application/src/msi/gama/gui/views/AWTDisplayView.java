/*********************************************************************************************
 *
 *
 * 'AWTDisplayView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views;

import javax.swing.JComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.displays.awt.Java2DDisplaySurface;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.WorkaroundForIssue1353;
import msi.gama.gui.swt.swing.Platform;
import msi.gama.gui.swt.swing.SwingControl;
import msi.gama.runtime.GAMA;

public class AWTDisplayView extends LayeredDisplayView/* implements ISizeProvider */ {

	public static final String ID = IGui.LAYER_VIEW_ID;

	@Override
	public Java2DDisplaySurface getDisplaySurface() {
		return (Java2DDisplaySurface) super.getDisplaySurface();
	}

	// protected Composite createSurfaceCompositeSimple() {
	// if ( getOutput() == null ) { return null; }
	// surfaceComposite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	// Frame frame = SWT_AWT.new_Frame(surfaceComposite);
	// frame.setVisible(true);
	// frame.add(getDisplaySurface());
	// return surfaceComposite;
	// }

	// @Override
	@Override
	protected Composite createSurfaceComposite() {
		if ( getOutput() == null ) { return null; }

		final Runnable displayOverlay = new Runnable() {

			@Override
			public void run() {
				if ( !overlay.isVisible() ) { return; }
				overlay.update();
			}
		};

		final java.awt.event.MouseMotionListener mlAwt2 = new java.awt.event.MouseMotionAdapter() {

			@Override
			public void mouseMoved(final java.awt.event.MouseEvent e) {
				// System.out.println("We move inside the AWT component");
				GAMA.getGui().asyncRun(displayOverlay);
			}

			@Override
			public void mouseDragged(final java.awt.event.MouseEvent e) {
				GAMA.getGui().asyncRun(displayOverlay);
			}
		};

		// final String outputName = getOutput().getName();

		// OutputSynchronizer.incInitializingViews(outputName, getOutput().isPermanent()); // incremented in the SWT thread
		surfaceComposite = new SwingControl(parent, SWT.NONE) {

			@Override
			protected JComponent createSwingComponent() {
				final JComponent component = getDisplaySurface();
				if ( component != null ) // can happen if the view has not been realized yet
					component.addMouseMotionListener(mlAwt2);
				return component;
			}

			@Override
			public Composite getLayoutAncestor() {
				// AD 02/16 Seems necessary to return null for displays to show up and correctly initialize their graphics environment
				return null;
				// return parent;
			}

			@Override
			public boolean isSwtTabOrderExtended() {
				return false;
			}

			@Override
			public boolean isAWTPermanentFocusLossForced() {
				return true;
			}

			@Override
			public void afterComponentCreatedSWTThread() {
				if ( GamaPreferences.CORE_OVERLAY.getValue() ) {
					overlay.setVisible(true);
				}
				WorkaroundForIssue1353.installOn(surfaceComposite, AWTDisplayView.this);

			}

			@Override
			public void afterComponentCreatedAWTThread() {
				if ( getDisplaySurface() != null )
					new DisplaySurfaceMenu(getDisplaySurface(), surfaceComposite, AWTDisplayView.this);
			}
		};

		perspectiveListener = new IPerspectiveListener() {

			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( perspective.getId().equals(IGui.PERSPECTIVE_MODELING_ID) ) {
					if ( getOutput() != null && getDisplaySurface() != null ) {
						if ( !GamaPreferences.CORE_DISPLAY_PERSPECTIVE.getValue() ) {
							previousState = getOutput().isPaused();
							getOutput().setPaused(true);
						}
					}
					if ( overlay != null ) {
						overlay.hide();
					}
				} else {
					if ( !GamaPreferences.CORE_DISPLAY_PERSPECTIVE.getValue() ) {
						if ( getOutput() != null && getDisplaySurface() != null ) {
							getOutput().setPaused(previousState);
						}
					}
					if ( overlay != null ) {
						overlay.update();
					}
				}
			}
		};

		SwtGui.getWindow().addPerspectiveListener(perspectiveListener);
		WorkaroundForIssue1594.installOn(AWTDisplayView.this, parent, surfaceComposite, getDisplaySurface());
		return surfaceComposite;
	}

	/**
	 * Method zoomWhenScrolling()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Zoomable#zoomWhenScrolling()
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

	/**
	 * Wait for the AWT environment is completely initialized, preventing a thread lock when two views want to open at the same time. Must not be called in neither the AWT or the SWT thread. A
	 * configurable timeout is applied, so that other views are not blocked. It remains to be seen what to do if this times out, as we should normally cancel the view.
	 * @see msi.gama.common.interfaces.IGamaView#waitToBeRealized()
	 */

	public static long REALIZATION_TIME_OUT = 2000;

	@Override
	public void waitToBeRealized() {
		if ( Platform.isWin32() ) { return; }
		final long start = System.currentTimeMillis();
		boolean openable = false;
		while (!openable) {
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			final long now = System.currentTimeMillis();
			openable = now - start > REALIZATION_TIME_OUT || this.getDisplaySurface().isRealized();
		}

	}
}