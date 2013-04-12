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

import java.util.*;
import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.wizards.*;
import org.osgi.service.prefs.BackingStoreException;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	public static final String PROMPT_ON_EXIT = "PROMPT_ON_EXIT";

	public ApplicationWorkbenchWindowAdvisor(final ApplicationWorkbenchAdvisor adv,
		final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);
	}

	@Override
	public void preWindowOpen() {
		super.preWindowOpen();
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(700, 550));
		configurer.setShowFastViewBars(false);
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle(GAMA.VERSION);
		configurer
			.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));

	}

	@Override
	public void postWindowCreate() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setMaximized(true);
	}

	@Override
	public boolean preWindowShellClose() {
		IEclipsePreferences preferenceStore = InstanceScope.INSTANCE.getNode(IGui.PLUGIN_ID);
		boolean promptOnExit = preferenceStore.getBoolean(PROMPT_ON_EXIT, false);

		if ( !promptOnExit ) {
			MessageDialogWithToggle dlg =
				MessageDialogWithToggle.openOkCancelConfirm(Display.getDefault().getActiveShell(),
					"Confirm Exit", "Exit " + Platform.getProduct().getName() + " ?",
					"Always exit without prompt", false, null, null);

			if ( dlg.getReturnCode() != IDialogConstants.OK_ID ) { return false; }

			InstanceScope.INSTANCE.getNode(IGui.PLUGIN_ID).putBoolean(PROMPT_ON_EXIT,
				dlg.getToggleState());
			try {
				InstanceScope.INSTANCE.getNode(IGui.PLUGIN_ID).flush();
			} catch (BackingStoreException e) {
				System.out.println("Preferences can not be saved");
				e.printStackTrace();
			}
		}
		return super.preWindowShellClose();
	}

	static List<String> CATEGORIES_TO_REMOVE = new GamaList(new String[] {
		"org.eclipse.jdt.debug.ui.java", "org.eclipse.jdt.junit", "org.eclipse.pde.PDE",
		"org.eclipse.ui.Basic", "org.eclipse.emf.codegen.ecore.ui.wizardCategory",
		"org.eclipse.jdt.ui.java" });

	@Override
	public void postWindowOpen() {
		AbstractExtensionWizardRegistry wizardRegistry =
			(AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
		IWizardCategory[] categories =
			PlatformUI.getWorkbench().getNewWizardRegistry().getRootCategory().getCategories();
		for ( IWizardDescriptor wizard : getAllWizards(categories) ) {
			String id = wizard.getCategory().getId();
			if ( CATEGORIES_TO_REMOVE.contains(id) ) {
				WorkbenchWizardElement wizardElement = (WorkbenchWizardElement) wizard;
				wizardRegistry.removeExtension(wizardElement.getConfigurationElement()
					.getDeclaringExtension(), new Object[] { wizardElement });
			}
		}

		// Menu manager

		IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();

		if ( window instanceof WorkbenchWindow ) {
			MenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();

			// Finding the original "Switch Workspace ... " item
			MenuManager item = (MenuManager) menuManager.find("file");
			ActionContributionItem ws = (ActionContributionItem) item.find("openWorkspace");

			if ( ws != null ) {
				ws.setVisible(false);
				// clean old one
				menuManager.remove(ws);
				// refresh menu gui
				menuManager.update();
			}

		}

	}

	private IWizardDescriptor[] getAllWizards(IWizardCategory[] categories) {
		List<IWizardDescriptor> results = new ArrayList<IWizardDescriptor>();
		for ( IWizardCategory wizardCategory : categories ) {
			System.out.println("Category:" + wizardCategory.getId());
			results.addAll(Arrays.asList(wizardCategory.getWizards()));
			results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
		}
		return results.toArray(new IWizardDescriptor[0]);
	}

}
