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
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.perspectives.ModelingPerspective;
import msi.gama.gui.swt.swing.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;

public class AWTDisplayView extends LayeredDisplayView implements ISizeProvider {

	public static final String ID = GuiUtils.LAYER_VIEW_ID;

	@Override
	protected Composite createSurfaceComposite() {

		// TODO do a test to know whether or not we are in a "simple" chart environment ?

		// final Runnable forceFocus = new Runnable() {
		//
		// @Override
		// public void run() {
		// if ( surfaceComposite.getDisplay() != null && !surfaceComposite.isFocusControl() ) {
		// surfaceComposite.setFocus();
		// }
		// }
		// };

		final Runnable displayOverlay = new Runnable() {

			@Override
			public void run() {
				overlay.update();
			}
		};

		final java.awt.event.MouseMotionListener mlAwt2 = new java.awt.event.MouseMotionAdapter() {

			@Override
			public void mouseMoved(final java.awt.event.MouseEvent e) {
				GuiUtils.asyncRun(displayOverlay);
			}

			@Override
			public void mouseDragged(final java.awt.event.MouseEvent e) {
				GuiUtils.asyncRun(displayOverlay);
			}
		};

		final boolean isOpenGL = getOutput().isOpenGL();
		final String outputName = getOutput().getName();

		OutputSynchronizer.incInitializingViews(outputName); // incremented in the SWT thread
		surfaceComposite = new SwingControl(parent, SWT.NONE) {

			@Override
			protected JComponent createSwingComponent() {

				final JComponent frameAwt = (JComponent) getOutput().getSurface();
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
					overlay.setHidden(false);
				}

			}

			@Override
			public void afterComponentCreatedAWTThread() {
				if ( !isOpenGL ) {
					// Deferred to the OpenGL renderer to signify its initialization
					// see JOGLAWTGLRendered.init()
					OutputSynchronizer.decInitializingViews(outputName);
				}
				// FIXME Hack to create a menu displayable on SWT
				new DisplaySurfaceMenu(getOutput().getSurface(), surfaceComposite, AWTDisplayView.this);
			}
		};

		perspectiveListener = new IPerspectiveListener() {

			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( perspective.getId().equals(ModelingPerspective.ID) ) {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						previousState = getOutput().getSurface().isPaused();
						getOutput().getSurface().setPaused(true);
					}
					if ( overlay != null && layersOverlay != null ) {
						overlay.hide();
						layersOverlay.hide();
					}
				} else {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						getOutput().getSurface().setPaused(previousState);
					}
					if ( overlay != null && layersOverlay != null ) {
						overlay.update();
						layersOverlay.update();
					}
				}
			}
		};

		SwtGui.getWindow().addPerspectiveListener(perspectiveListener);
		// GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		// data.minimumHeight = 100;
		// surfaceComposite.setLayoutData(data);
		return surfaceComposite;
	}

	@Override
	public void fixSize() {

		// AD: Reworked to address Issue 535. It seems necessary to read the size of the composite inside an SWT
		// thread
		// and run the sizing inside an AWT thread
		OutputSynchronizer.cleanResize(new Runnable() {

			@Override
			public void run() {

				final Rectangle r = parent.getClientArea();
				final int x = r.width;
				final int y = r.height;
				surfaceComposite.setBounds(r);
				// parent.layout();

				java.awt.EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {

						((SwingControl) surfaceComposite).getFrame().setBounds(r.x, r.y, r.width, r.height);
						((SwingControl) surfaceComposite).getFrame().validate();
						getOutput().getSurface().resizeImage(x, y);
						// getOutput().getSurface().setSize(x, y);
						getOutput().getSurface().updateDisplay();

						GuiUtils.asyncRun(new Runnable() {

							@Override
							public void run() {
								// surfaceComposite.setBounds(r);
								// GuiUtils.debug("AWTDisplayView.fixSize(). new bounds for composite:" + r);
								if ( overlay != null ) {
									overlay.relocate();
									overlay.resize();
								}
							}
						});
					}
				});

			}

		});
	}

	/**
	 * Method getSizeFlags()
	 * @see org.eclipse.ui.ISizeProvider#getSizeFlags(boolean)
	 */
	@Override
	public int getSizeFlags(final boolean width) {
		return SWT.MIN;
	}

	/**
	 * Method computePreferredSize()
	 * @see org.eclipse.ui.ISizeProvider#computePreferredSize(boolean, int, int, int)
	 */
	@Override
	public int computePreferredSize(final boolean width, final int availableParallel, final int availablePerpendicular,
		final int preferredResult) {
		return 200;
	}
}