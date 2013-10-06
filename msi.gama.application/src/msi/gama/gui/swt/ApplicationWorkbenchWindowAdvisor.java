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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt;

import java.io.IOException;
import java.net.*;
import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaList;
import org.eclipse.jface.action.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.wizards.*;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	// private static final String PROMPT_ON_EXIT = "PROMPT_ON_EXIT";

	public ApplicationWorkbenchWindowAdvisor(final ApplicationWorkbenchAdvisor adv,
		final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);
	}

	@Override
	public void preWindowOpen() {
		super.preWindowOpen();
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(700, 550));
		configurer.setShowFastViewBars(false);
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle(GAMA.VERSION);
		configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));

	}

	@Override
	public void postWindowCreate() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setMaximized(true);
	}

	// @Override
	// public boolean preWindowShellClose() {
	// final IEclipsePreferences preferenceStore = InstanceScope.INSTANCE.getNode(IGui.PLUGIN_ID);
	// final boolean promptOnExit = preferenceStore.getBoolean(PROMPT_ON_EXIT, false);
	//
	// if ( !promptOnExit ) {
	// final MessageDialogWithToggle dlg =
	// MessageDialogWithToggle.openOkCancelConfirm(Display.getDefault().getActiveShell(), "Confirm Exit",
	// "Exit " + Platform.getProduct().getName() + " ?", "Always exit without prompt", false, null, null);
	//
	// if ( dlg.getReturnCode() != IDialogConstants.OK_ID ) { return false; }
	//
	// InstanceScope.INSTANCE.getNode(IGui.PLUGIN_ID).putBoolean(PROMPT_ON_EXIT, dlg.getToggleState());
	// try {
	// InstanceScope.INSTANCE.getNode(IGui.PLUGIN_ID).flush();
	// } catch (final BackingStoreException e) {
	// System.out.println("Preferences can not be saved");
	// e.printStackTrace();
	// }
	// }
	// return super.preWindowShellClose();
	// }

	private static List<String> CATEGORIES_TO_REMOVE = new GamaList(new String[] { "org.eclipse.jdt.debug.ui.java",
		"org.eclipse.jdt.junit", "org.eclipse.pde.PDE", /* "org.eclipse.ui.Basic", */
		"org.eclipse.emf.codegen.ecore.ui.wizardCategory", "org.eclipse.jdt.ui.java" });

	@Override
	public void postWindowOpen() {

		// Removing unwanted wizards

		final AbstractExtensionWizardRegistry wizardRegistry =
			(AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
		final IWizardCategory[] categories =
			PlatformUI.getWorkbench().getNewWizardRegistry().getRootCategory().getCategories();
		for ( final IWizardDescriptor wizard : getAllWizards(categories) ) {
			final String id = wizard.getCategory().getId();
			if ( CATEGORIES_TO_REMOVE.contains(id) ) {
				final WorkbenchWizardElement wizardElement = (WorkbenchWizardElement) wizard;
				wizardRegistry.removeExtension(wizardElement.getConfigurationElement().getDeclaringExtension(),
					new Object[] { wizardElement });
			}
		}

		// Removing unwanted menus and menu items, moving around some menu items

		final IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();

		if ( window instanceof WorkbenchWindow ) {
			final MenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();
			// Removing "Window"
			menuManager.remove("window");
			menuManager.remove("navigate");
			// Setting the views and perspective submenu
			final MenuManager views = (MenuManager) menuManager.find("viewMenu");
			MenuManager viewMenu = new MenuManager("Open View");
			IContributionItem viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
			viewMenu.add(viewList);
			views.add(viewMenu);
			IContributionItem perspList = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
			MenuManager perspMenu = new MenuManager("Open Perspective");
			perspMenu.add(perspList);
			views.add(perspMenu);
			// Finding the original "Switch Workspace ... " item
			final MenuManager item = (MenuManager) menuManager.find("file");
			final ActionContributionItem ws = (ActionContributionItem) item.find("openWorkspace");

			if ( ws != null ) {
				ws.setVisible(false);
				// clean old one
				menuManager.remove(ws);
				// refresh menu gui
				menuManager.update();
			}

		}
		openGamaWebPage(false);

	}

	public static void openGamaWebPage(final boolean force) {
		if ( isInternetReachable(force) ) {
			try {
				PlatformUI.getWorkbench().getBrowserSupport().createBrowser("GAMA Web Page")
					.openURL(new URL("https://code.google.com/p/gama-platform/"));
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isInternetReachable(final boolean force) {
		if ( !force && !GamaPreferences.CORE_SHOW_PAGE.getValue() ) { return false; }
		try {
			URL url = new URL("https://code.google.com/p/gama-platform/");
			// open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.setConnectTimeout(2000);
			Object objData = urlConnect.getContent();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private IWizardDescriptor[] getAllWizards(final IWizardCategory[] categories) {
		final List<IWizardDescriptor> results = new ArrayList<IWizardDescriptor>();
		for ( final IWizardCategory wizardCategory : categories ) {
			System.out.println("Category:" + wizardCategory.getId());
			results.addAll(Arrays.asList(wizardCategory.getWizards()));
			results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
		}
		return results.toArray(new IWizardDescriptor[0]);
	}

}
