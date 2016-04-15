/*********************************************************************************************
 *
 *
 * 'ApplicationWorkbenchWindowAdvisor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt;

import org.eclipse.ui.*;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import msi.gama.common.GamaPreferences;
import msi.gama.gui.viewers.html.HtmlViewer;
import msi.gama.runtime.GAMA;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	final IPerspectiveDescriptor simul = Workbench.getInstance().getPerspectiveRegistry()
		.findPerspectiveWithId("msi.gama.application.perspectives.SimulationPerspective");
	IPerspectiveDescriptor model = Workbench.getInstance().getPerspectiveRegistry()
		.findPerspectiveWithId("msi.gama.application.perspectives.ModelingPerspective");

	IPerspectiveListener pl = new IPerspectiveListener() {

		@Override
		public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
			final String changeId) {}

		@Override
		public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			System.out.println("Running the perspective listener to automatically launch modeling");
			if ( perspective == simul ) {
				page.closePerspective(simul, false, false);
				page.setPerspective(model);
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				window.removePerspectiveListener(this);
			}
		}
	};

	public ApplicationWorkbenchWindowAdvisor(final ApplicationWorkbenchAdvisor adv,
		final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);
		System.out.println("Attaching the perspective listener to automatically launch modeling");
		configurer.getWindow().addPerspectiveListener(pl);
		configurer.getWindow().addPageListener(new IPageListener() {

			@Override
			public void pageActivated(final IWorkbenchPage page) {
				page.setPerspective(model);
				configurer.getWindow().removePageListener(this);
			}

			@Override
			public void pageClosed(final IWorkbenchPage page) {}

			@Override
			public void pageOpened(final IWorkbenchPage page) {}
		});
	}

	@Override
	public void preWindowOpen() {
		super.preWindowOpen();
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(700, 550));
		configurer.setShowFastViewBars(false);
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle(GAMA.VERSION);

	}

	@Override
	public void postWindowRestore() throws WorkbenchException {

	}

	@Override
	public void postWindowCreate() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		window.getShell().setMaximized(GamaPreferences.CORE_SHOW_MAXIMIZED.getValue());
		RemoveUnwantedWizards.run();
		RemoveUnwantedActionSets.run();

	}

	@Override
	public void postWindowOpen() {
		HtmlViewer.openWelcomePage(true);
		RearrangeMenus.run();
	}

}
