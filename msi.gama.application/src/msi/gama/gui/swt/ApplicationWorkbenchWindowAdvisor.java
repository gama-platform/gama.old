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

import msi.gama.gui.viewers.html.HtmlViewer;
import msi.gama.runtime.GAMA;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(final ApplicationWorkbenchAdvisor adv,
		final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);
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
		// configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));

	}

	@Override
	public void postWindowCreate() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setMaximized(true);
		RemoveUnwantedWizards.run();
		RemoveUnwantedActionSets.run();

	}

	@Override
	public void postWindowOpen() {
		HtmlViewer.openWelcomePage(true);
		RearrangeMenus.run();
		// This code below is necessary because it happens that if an editor is already opened when launching GAMA,
		// the keystrokes (like command-S) do not work on it until it has been deactivated and reactivated (at least on
		// MacOS X)
		// IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// IEditorPart editor = page.getActiveEditor();
		// if ( editor != null ) {
		// IViewPart nav = page.findView("msi.gama.gui.view.GamaNavigator");
		// if ( nav != null ) {
		// page.activate(nav);
		// page.activate(editor);
		// }
		// }
	}

}
