/*********************************************************************************************
 *
 * 'ApplicationWorkbenchWindowAdvisor.java, in plugin msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.application.workbench;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;

public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new GamaActionBarAdvisor(configurer);
	}

	public ApplicationWorkbenchWindowAdvisor(final ApplicationWorkbenchAdvisor adv,
		final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);

		// Hack and workaround for the inability to find launcher icons...

		final Bundle bundle = Platform.getBundle("msi.gama.application");

		final ImageDescriptor myImage =
			ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("branding_icons/icon256.png"), null));
		configurer.getWindow().getShell().setImage(myImage.createImage());
	}

	@Override
	public void preWindowOpen() {
		super.preWindowOpen();
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		configurer.getWindow().addPerspectiveListener(new IPerspectiveListener() {

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( PerspectiveHelper.isSimulationPerspective() ) {
					// System.out.println("Running the perspective listener to automatically launch modeling");
					final IPerspectiveDescriptor desc = page.getPerspective();
					page.closePerspective(desc, false, false);
					PerspectiveHelper.openModelingPerspective(true);
				}
				configurer.getWindow().removePerspectiveListener(this);

			}
		});
		configurer.getWindow().addPageListener(new IPageListener() {

			@Override
			public void pageActivated(final IWorkbenchPage page) {
				configurer.getWindow().removePageListener(this);
				PerspectiveHelper.openModelingPerspective(true);
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
	public void postWindowRestore() throws WorkbenchException {}

	@Override
	public void postWindowCreate() {
		final IWorkbenchWindow window = getWindowConfigurer().getWindow();
		window.getShell().setMaximized(GamaPreferences.Interface.CORE_SHOW_MAXIMIZED.getValue());
	}

	@Override
	public void postWindowOpen() {
		PerspectiveHelper.cleanPerspectives();
		GAMA.getGui().openWelcomePage(true);
	}

}
