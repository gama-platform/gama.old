/*******************************************************************************************************
 *
 * ApplicationWorkbenchWindowAdvisor.java, in msi.gama.application, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workbench;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;

/**
 * The Class ApplicationWorkbenchWindowAdvisor.
 */
public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	static {
		DEBUG.OFF();
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new GamaActionBarAdvisor(configurer);
	}

	/**
	 * Instantiates a new application workbench window advisor.
	 *
	 * @param adv
	 *            the adv
	 * @param configurer
	 *            the configurer
	 */
	public ApplicationWorkbenchWindowAdvisor(final ApplicationWorkbenchAdvisor adv,
			final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);
		DEBUG.OUT("Instantiation of ApplicationWorkbenchWindowAdvisor begins");
		// Hack and workaround for the inability to find launcher icons...

		final Bundle bundle = Platform.getBundle("msi.gama.application");

		final ImageDescriptor myImage =
				ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("branding_icons/icon256.png"), null));
		configurer.getWindow().getShell().setImage(myImage.createImage());
		DEBUG.OUT("Instantiation of ApplicationWorkbenchWindowAdvisor finished");
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
				if (PerspectiveHelper.isSimulationPerspective()) {
					// DEBUG.OUT("Running the perspective listener to automatically launch modeling");
					final IPerspectiveDescriptor desc = page.getPerspective();
					page.closePerspective(desc, false, false);
					PerspectiveHelper.openModelingPerspective(true, false);
				}
				configurer.getWindow().removePerspectiveListener(this);

			}
		});
		configurer.getWindow().addPageListener(new IPageListener() {

			@Override
			public void pageActivated(final IWorkbenchPage page) {
				configurer.getWindow().removePageListener(this);
				PerspectiveHelper.openModelingPerspective(true, false);
			}

			@Override
			public void pageClosed(final IWorkbenchPage page) {}

			@Override
			public void pageOpened(final IWorkbenchPage page) {}
		});
		// See #3187 -
		if (FLAGS.USE_OLD_TABS) {
			ThemeHelper.injectCSS(".MPartStack {\n" + " swt-tab-renderer: null;\n" + " swt-simple: true;\n" + "}");
		}
		// ThemeHelper.injectCSS(".MPartSashContainer{ jsash-width: 0px; } ");
		ThemeHelper.restoreSashBackground();
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle(GAMA.VERSION);
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);
		Resource.setNonDisposeHandler(null);
	}

	@Override
	public void postWindowRestore() throws WorkbenchException {}

	@Override
	public void postWindowCreate() {
		final IWorkbenchWindow window = getWindowConfigurer().getWindow();
		window.getShell().setMaximized(GamaPreferences.Interface.CORE_SHOW_MAXIMIZED.getValue());
		if (FLAGS.USE_DELAYED_RESIZE) {
			window.getShell().addControlListener(new ControlAdapter() {

				@Override
				public void controlResized(final ControlEvent e) {
					// window.getShell().layout(true, true);
					window.getShell().requestLayout();
				}

			});
		}
	}

	@Override
	public void postWindowOpen() {
		PerspectiveHelper.cleanPerspectives();
		GAMA.getGui().openWelcomePage(true);
		GAMA.getGui().updateExperimentState(null, IGui.NONE);
	}

}
