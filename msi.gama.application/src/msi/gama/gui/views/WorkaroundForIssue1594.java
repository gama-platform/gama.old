package msi.gama.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPartReference;
import msi.gama.gui.displays.awt.Java2DDisplaySurface;
import msi.gama.runtime.GAMA;

public class WorkaroundForIssue1594 {

	public static void installOn(final AWTDisplayView view, final Composite parent, final Composite surfaceComposite,
		final Java2DDisplaySurface displaySurface) {
		// Install only on Windows

		final IPartService ps = view.getSite().getService(IPartService.class);
		ps.addPartListener(new IPartListener2() {

			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {}

			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {}

			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {}

			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {
				if ( !msi.gama.gui.swt.swing.Platform.isWin32() ) { return; }
				// Fix for Issue #1594
				if ( partRef.getPart(false).equals(view) ) {
					final IPartListener2 listener = this;
					// AD: Reworked to address Issue 535. It seems necessary to read the size of the composite inside an SWT
					// thread and run the sizing inside an AWT thread
					GAMA.getGui().asyncRun(new Runnable() {

						@Override
						public void run() {
							if ( parent.isDisposed() ) { return; }
							final org.eclipse.swt.graphics.Rectangle r = parent.getBounds();
							java.awt.EventQueue.invokeLater(new Runnable() {

								@Override
								public void run() {
									if ( surfaceComposite == null ) { return; }
									displaySurface.setBounds(r.x, r.y, r.width, r.height);
									GAMA.getGui().asyncRun(new Runnable() {

										@Override
										public void run() {
											parent.layout(true, true);
											displaySurface.zoomFit();
											ps.removePartListener(listener);
										}
									});
								}
							});

						}

					});
				}
			}

			@Override
			public void partBroughtToTop(final IWorkbenchPartReference part) {}

			/**
			 * Method partHidden()
			 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
			 */
			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {}

			/**
			 * Method partVisible()
			 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
			 */
			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {}

			/**
			 * Method partInputChanged()
			 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
			 */
			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {}
		});

	}

}
