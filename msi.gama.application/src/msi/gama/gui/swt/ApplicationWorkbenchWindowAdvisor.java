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

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;

public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	// final IPerspectiveDescriptor simul = Workbench.getInstance().getPerspectiveRegistry()
	// .findPerspectiveWithId("msi.gama.application.perspectives.SimulationPerspective");
	// IPerspectiveDescriptor model = Workbench.getInstance().getPerspectiveRegistry()
	// .findPerspectiveWithId("msi.gama.application.perspectives.ModelingPerspective");

	public ApplicationWorkbenchWindowAdvisor(final ApplicationWorkbenchAdvisor adv,
		final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);
	}

	@Override
	public void preWindowOpen() {
		super.preWindowOpen();
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(700, 550));
		// configurer.setShowFastViewBars(false);
		System.out.println("Attaching the perspective listener to automatically launch modeling");

		configurer.getWindow().addPerspectiveListener(new IPerspectiveListener() {

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( perspective.getId().contains(IGui.PERSPECTIVE_SIMULATION_FRAGMENT) ) {
					System.out.println("Running the perspective listener to automatically launch modeling");
					final IPerspectiveDescriptor desc = page.getPerspective();
					page.closePerspective(desc, false, false);
					GAMA.getGui().openModelingPerspective(true);
				}
				configurer.getWindow().removePerspectiveListener(this);

			}
		});
		configurer.getWindow().addPageListener(new IPageListener() {

			@Override
			public void pageActivated(final IWorkbenchPage page) {
				configurer.getWindow().removePageListener(this);
				page.setPerspective(Workbench.getInstance().getPerspectiveRegistry()
					.findPerspectiveWithId(IGui.PERSPECTIVE_MODELING_ID));
			}

			@Override
			public void pageClosed(final IWorkbenchPage page) {}

			@Override
			public void pageOpened(final IWorkbenchPage page) {}
		});
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
		final IWorkbenchWindow window = getWindowConfigurer().getWindow();
		window.getShell().setMaximized(GamaPreferences.CORE_SHOW_MAXIMIZED.getValue());
		RemoveUnwantedWizards.run();
		RemoveUnwantedActionSets.run();
		GamaKeyBindings.install();

	}

	@Override
	public void postWindowOpen() {
		SwtGui.openWelcomePage(true);
		RearrangeMenus.run();
	}

}
