/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.application.*;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	//
	// @Override
	// public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
	// return new ApplicationActionBarAdvisor(configurer);
	// }

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(700, 550));
		configurer.setShowFastViewBars(false);
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle("GAMA 1.4");

		// activate drag and drop (DnD) cf:
		// org.eclipse.ui.internal.EditorSiteDragAndDropServiceImpl$1.dragLeave(EditorSiteDragAndDropServiceImpl.java:104)
		// org.eclipse.swt.dnd.DNDListener.handleEvent(DNDListener.java:70)
		configurer
			.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));

		IPreferenceStore apiStore = PlatformUI.getPreferenceStore();
		apiStore.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, "TOP_LEFT");
	}

	@Override
	public void postWindowCreate() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setMaximized(true);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.hideActionSet("org.eclipse.ui.externaltools.ExternalToolsSet");
	}

	@Override
	public boolean preWindowShellClose() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean promptOnExit = preferenceStore.getBoolean(Activator.PROMPT_ON_EXIT);

		if ( !promptOnExit ) {
			MessageDialogWithToggle dlg =
				MessageDialogWithToggle.openOkCancelConfirm(Display.getDefault().getActiveShell(),
					"Confirm Exit", "Exit " + Platform.getProduct().getName() + " ?",
					"Always exit without prompt", false, null, null);

			if ( dlg.getReturnCode() != IDialogConstants.OK_ID ) { return false; }

			Activator.getDefault().getPreferenceStore()
				.setValue(Activator.PROMPT_ON_EXIT, dlg.getToggleState());

			// TODO .. use InstanceScope.getNode(<bundleId>).flush()
			Activator.getDefault().savePluginPreferences();
		}
		return super.preWindowShellClose();
	}

}
