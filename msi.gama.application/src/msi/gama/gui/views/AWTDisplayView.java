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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.perspectives.ModelingPerspective;
import msi.gama.gui.swt.swing.*;
import msi.gama.runtime.GAMA;

public class AWTDisplayView extends LayeredDisplayView implements ISizeProvider {

	public static final String ID = IGui.LAYER_VIEW_ID;

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

		final String outputName = getOutput().getName();

		OutputSynchronizer.incInitializingViews(outputName, getOutput().isPermanent()); // incremented in the SWT thread
		surfaceComposite = new SwingControl(parent, SWT.NONE) {

			@Override
			protected JComponent createSwingComponent() {
				final JComponent frameAwt = (JComponent) getDisplaySurface();
				frameAwt.addMouseMotionListener(mlAwt2);
				return frameAwt;
			}

			@Override
			public Composite getLayoutAncestor() {
				// Seems necessary to return null for OpenGL displays to show up and call init on the
				// renderer
				return null;
			}

			@Override
			public boolean isSwtTabOrderExtended() {
				return false;
			}

			@Override
			public boolean isAWTPermanentFocusLossForced() {
				return false;
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
				// if ( !isOpenGL ) {
				// Deferred to the OpenGL renderer to signify its initialization
				// see JOGLAWTGLRendered.init()
				OutputSynchronizer.decInitializingViews(outputName);
				// }

				new DisplaySurfaceMenu(getDisplaySurface(), surfaceComposite, AWTDisplayView.this);
			}
		};

		// surfaceComposite.addFocusListener(new FocusAdapter() {
		//
		// /**
		// * Method focusGained()
		// * @see org.eclipse.swt.events.FocusAdapter#focusGained(org.eclipse.swt.events.FocusEvent)
		// */
		// @Override
		// public void focusGained(final FocusEvent e) {
		// System.out.println("Focus gained for display");
		// super.focusGained(e);
		// }
		//
		// /**
		// * Method focusLost()
		// * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
		// */
		// @Override
		// public void focusLost(final FocusEvent e) {
		// System.out.println("Focus lost for display");
		// super.focusLost(e);
		// }
		//
		// });

		perspectiveListener = new IPerspectiveListener() {

			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( perspective.getId().equals(ModelingPerspective.ID) ) {
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
		return surfaceComposite;
	}

	@Override
	public void fixSize() {

		// AD: Reworked to address Issue 535. It seems necessary to read the size of the composite inside an SWT
		// thread and run the sizing inside an AWT thread
		OutputSynchronizer.cleanResize(new Runnable() {

			@Override
			public void run() {
				if ( parent.isDisposed() ) { return; }
				final Rectangle r = parent.getBounds();

				java.awt.EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						if ( surfaceComposite == null ) { return; }
						((SwingControl) surfaceComposite).getFrame().setBounds(r.x, r.y, r.width, r.height);
						getDisplaySurface().resizeImage(r.width, r.height, false);
						getDisplaySurface().updateDisplay(true);

						GAMA.getGui().run(new Runnable() {

							@Override
							public void run() {
								parent.layout(true, true);
							}
						});
					}
				});

			}

		});
	}

	@Override
	public int getSizeFlags(final boolean width) {
		return SWT.MIN;
	}

	@Override
	public int computePreferredSize(final boolean width, final int availableParallel, final int availablePerpendicular,
		final int preferredResult) {
		return 600;
	}

	/**
	 * Method zoomWhenScrolling()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Zoomable#zoomWhenScrolling()
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}
}