/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt;

import msi.gama.common.util.FileUtils;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gaml.compilation.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.application.*;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;
import org.osgi.service.prefs.BackingStoreException;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public static final String PROMPT_ON_EXIT = "PROMPT_ON_EXIT";

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
		try {
			System.out.println("Configuring file access through SWT");
			FileUtils.setFileAccess(new SwtIO());
			GamaBundleLoader.preBuildContributions();
			GamlJavaValidator.canRun(true);
		} catch (GamlException e1) {
			e1.printStackTrace();
		}
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setMaximized(true);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.hideActionSet("org.eclipse.ui.externaltools.ExternalToolsSet");
	}

	@Override
	public boolean preWindowShellClose() {
		IEclipsePreferences preferenceStore = InstanceScope.INSTANCE.getNode(SwtGui.PLUGIN_ID);
		boolean promptOnExit = preferenceStore.getBoolean(PROMPT_ON_EXIT, false);

		if ( !promptOnExit ) {
			MessageDialogWithToggle dlg =
				MessageDialogWithToggle.openOkCancelConfirm(Display.getDefault().getActiveShell(),
					"Confirm Exit", "Exit " + Platform.getProduct().getName() + " ?",
					"Always exit without prompt", false, null, null);

			if ( dlg.getReturnCode() != IDialogConstants.OK_ID ) { return false; }

			InstanceScope.INSTANCE.getNode(SwtGui.PLUGIN_ID).putBoolean(PROMPT_ON_EXIT,
				dlg.getToggleState());
			try {
				InstanceScope.INSTANCE.getNode(SwtGui.PLUGIN_ID).flush();
			} catch (BackingStoreException e) {
				System.out.println("Preferences can not be saved");
				e.printStackTrace();
			}
		}
		return super.preWindowShellClose();
	}

}
