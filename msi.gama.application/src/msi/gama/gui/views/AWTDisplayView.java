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
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.gui.displays.awt.Java2DDisplaySurface;
import msi.gama.gui.swt.WorkaroundForIssue1353;
import msi.gama.gui.swt.swing.Platform;
import msi.gama.gui.swt.swing.SwingControl;

public class AWTDisplayView extends LayeredDisplayView/* implements ISizeProvider */ {

	public static final String ID = IGui.LAYER_VIEW_ID;
	public static long REALIZATION_TIME_OUT = 1000;
	public boolean isVisible;

	@Override
	public Java2DDisplaySurface getDisplaySurface() {
		return (Java2DDisplaySurface) super.getDisplaySurface();
	}

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {
		// getSite().getService(IPartService.class).addPartListener(new IPartListener2() {
		//
		// @Override
		// public void partActivated(final IWorkbenchPartReference partRef) {
		// if ( partRef.getPart(false).equals(AWTDisplayView.this) )
		// isVisible = true;
		// }
		//
		// @Override
		// public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		// if ( partRef.getPart(false).equals(AWTDisplayView.this) )
		//
		// isVisible = true;
		// }
		//
		// @Override
		// public void partClosed(final IWorkbenchPartReference partRef) {}
		//
		// @Override
		// public void partDeactivated(final IWorkbenchPartReference partRef) {}
		//
		// @Override
		// public void partOpened(final IWorkbenchPartReference partRef) {}
		//
		// @Override
		// public void partHidden(final IWorkbenchPartReference partRef) {
		// if ( partRef.getPart(false).equals(AWTDisplayView.this) )
		//
		// isVisible = false;
		// }
		//
		// @Override
		// public void partVisible(final IWorkbenchPartReference partRef) {
		// if ( partRef.getPart(false).equals(AWTDisplayView.this) )
		//
		// isVisible = true;
		// }
		//
		// @Override
		// public void partInputChanged(final IWorkbenchPartReference partRef) {}
		// });
		if ( getOutput() == null ) { return null; }

		surfaceComposite = new SwingControl(parent, SWT.NO_FOCUS) {

			@Override
			protected JComponent createSwingComponent() {
				return getDisplaySurface();
			}

			@Override
			public Composite getLayoutAncestor() {
				// AD 02/16 Seems necessary to return null for displays to show up and correctly initialize their graphics environment
				return null;
			}

			@Override
			public boolean isSwtTabOrderExtended() {
				return false;
			}

			@Override
			public void afterComponentCreatedSWTThread() {
				if ( GamaPreferences.CORE_OVERLAY.getValue() ) {
					overlay.setVisible(true);
				}
				WorkaroundForIssue1353.install();
			}

			@Override
			public void checkWidget() {

			}

			@Override
			public void afterComponentCreatedAWTThread() {
				// if ( getDisplaySurface() != null )
				// new DisplaySurfaceMenu(getDisplaySurface(), surfaceComposite, AWTDisplayView.this);
			}
		};
		surfaceComposite.setEnabled(false);
		WorkaroundForIssue1594.installOn(AWTDisplayView.this, parent, surfaceComposite, getDisplaySurface());

		return surfaceComposite;
	}

	/**
	 * Wait for the AWT environment to be initialized, preventing a thread lock when two views want to open at the same time. Must not be called in neither the AWT or the SWT thread. A
	 * configurable timeout is applied, so that other views are not blocked. It remains to be seen what to do if this times out, as we should normally cancel the view.
	 * @see msi.gama.common.interfaces.IGamaView#waitToBeRealized()
	 */

	@Override
	public void waitToBeRealized() {
		if ( Platform.isWin32() ) { return; }
		final long start = System.currentTimeMillis();
		long now = start;
		boolean openable = false;

		while (/* isVisible && */ !openable) {
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			now = System.currentTimeMillis();
			openable = now - start > REALIZATION_TIME_OUT || this.getDisplaySurface().isRealized();
		}
		System.out.println("Realized in " + (now - start) + "ms");

	}

}