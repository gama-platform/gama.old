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

import java.io.IOException;
import java.net.URL;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.GAMA;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	private static String HOME_URL = null;

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
		openWelcomePage(true);
		RearrangeMenus.run();
	}

	public static void openWelcomePage(final boolean ifEmpty) {
		if ( ifEmpty && SwtGui.getPage().getActiveEditor() != null ) { return; }
		if ( ifEmpty && !GamaPreferences.CORE_SHOW_PAGE.getValue() ) { return; }
		if ( HOME_URL == null ) {
			Bundle bundle = Platform.getBundle("msi.gama.ext");
			URL url = bundle.getEntry("/images/welcome.html");
			try {
				url = FileLocator.toFileURL(url);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			HOME_URL = url.toString();
		}
		if ( HOME_URL != null ) {
			GuiUtils.showWebEditor(HOME_URL, null);
		}
	}

}
