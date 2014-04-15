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
import java.net.*;
import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.BrowserView;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.browser.*;
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

	private static List<String> CATEGORIES_TO_REMOVE = new GamaList(new String[] { "org.eclipse.jdt.debug.ui.java",
		"org.eclipse.jdt.junit", "org.eclipse.pde.PDE", /* "org.eclipse.ui.Basic", */
		"org.eclipse.emf.codegen.ecore.ui.wizardCategory", "org.eclipse.jdt.ui.java" });

	@Override
	public void postWindowOpen() {

		// Removing unwanted wizards

		AbstractExtensionWizardRegistry wizardRegistry =
			(AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
		IWizardCategory[] categories =
			PlatformUI.getWorkbench().getNewWizardRegistry().getRootCategory().getCategories();
		for ( final IWizardDescriptor wizard : getAllWizards(categories) ) {
			final String id = wizard.getCategory().getId();
			// System.out.println("Wizard " + wizard.getId());
			if ( CATEGORIES_TO_REMOVE.contains(id) ) {
				final WorkbenchWizardElement wizardElement = (WorkbenchWizardElement) wizard;
				wizardRegistry.removeExtension(wizardElement.getConfigurationElement().getDeclaringExtension(),
					new Object[] { wizardElement });
			}
		}
		wizardRegistry = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getImportWizardRegistry();
		categories = PlatformUI.getWorkbench().getImportWizardRegistry().getRootCategory().getCategories();
		for ( final IWizardDescriptor wizard : getAllWizards(categories) ) {
			final String id = wizard.getCategory().getId();
			// System.out.println("Wizard " + wizard.getId());
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
		openWelcomePage(true);
		// openGamaWebPage(false);

	}

	/**
	 * 
	 */

	public static void openGamaWebPage(final String address, final boolean force) {
		if ( isInternetReachable(force) ) {
			BrowserView bv = (BrowserView) GuiUtils.showView("gama.browser.view", null, IWorkbenchPage.VIEW_VISIBLE);
			if ( address != null && bv != null ) {
				bv.getBrowser().setUrl(address);
			}
			return;
		}
	}

	public void openWelcomePage(final boolean force) {
		if ( this.getWindowConfigurer().getWindow().getActivePage().getActiveEditor() == null ) {
			if ( isInternetReachable(force) ) {
				try {
					IWebBrowser wb =
						PlatformUI.getWorkbench().getBrowserSupport()
							.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR, "", "Welcome", "");
					wb.openURL(new URL(
						"http://gama-platform.googlecode.com/svn/branches/GAMA_CURRENT/msi.gama.application/welcome.html"));
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} finally {
					// GamaPreferences.CORE_SHOW_PAGE.set(false).save();
				}
			}
		}
	}

	// if ( isInternetReachable(force) ) {
	//
	// try {
	// IWebBrowser wb =
	// PlatformUI
	// .getWorkbench()
	// .getBrowserSupport()
	// .createBrowser(
	// IWorkbenchBrowserSupport.AS_VIEW ,"", "Welcome", "");
	// wb.openURL(new URL("https://code.google.com/p/gama-platform/"));
	// } catch (PartInitException e) {
	// e.printStackTrace();
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	public static boolean isInternetReachable(final boolean force) {
		if ( !force && !GamaPreferences.CORE_SHOW_PAGE.getValue() ) { return false; }
		// AD 11/10/13 : see Issue 679
		// Too many problems with Linux for the moment. Reverse this if a definitive solution is found.
		if ( Platform.getOS().equals(Platform.OS_LINUX) || Platform.getWS().equals(Platform.WS_GTK) ) { return false; }

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
