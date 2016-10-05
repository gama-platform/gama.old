package ummisco.gama.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.NewWizardMenu;
import org.eclipse.ui.internal.ActionSetContributionItem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import ummisco.gama.ui.resources.GamaIcons;

public class CleanupHelper {

	public static void run() {
		RemoveUnwantedWizards.run();
		RemoveUnwantedActionSets.run();
		RearrangeMenus.run();
	}

	static class RemoveUnwantedActionSets
			extends PerspectiveAdapter /* implements IStartup */ {

		String[] TOOLBAR_ACTION_SETS_TO_REMOVE = new String[] { "org.eclipse", "msi.gama.lang.gaml.Gaml" };
		String[] MENUS_TO_REMOVE = new String[] { "org.eclipse.ui.run", "window", "navigate", "project" };

		public static void run() {
			final RemoveUnwantedActionSets remove = new RemoveUnwantedActionSets();
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++) {
				final IWorkbenchPage page = windows[i].getActivePage();
				if (page != null) {
					// Doing the initial cleanup on the default perspective
					// (modeling)
					remove.perspectiveActivated(page, null);
				}
				windows[i].addPerspectiveListener(remove);
			}
		}

		@Override
		public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			final WorkbenchWindow w = (WorkbenchWindow) page.getWorkbenchWindow();
			Display.getDefault().asyncExec(() -> {
				// RearrangeMenus.run();
				final IContributionItem[] items = w.getCoolBarManager2().getItems();
				// We remove all contributions to the toolbar that do not
				// relate
				// to gama
				for (final IContributionItem item : items) {

					for (final String s1 : TOOLBAR_ACTION_SETS_TO_REMOVE) {
						if (item.getId().contains(s1)) {
							// System.out.println("Removed perspective
							// contribution to toolbar:" + item.getId());
							try {
								if (w.getCoolBarManager2().find(item.getId()) != null)
									w.getCoolBarManager2().remove(item);
							} catch (final Exception e) {
							}
						}
					}
				}

				for (final String s2 : MENUS_TO_REMOVE) {
					w.getMenuBarManager().remove(s2);
					w.getMenuManager().remove(s2);
				}
				// Update the tool and menu bars
				w.getCoolBarManager2().update(true);
				w.getMenuManager().update(true);
				w.getMenuBarManager().update(true);
			});
		}

		@Override
		public void perspectiveChanged(final IWorkbenchPage p, final IPerspectiveDescriptor d, final String c) {
			if (c.equals(IWorkbenchPage.CHANGE_RESET_COMPLETE)) {
				perspectiveActivated(p, d);
			}
		}

	}

	static class RemoveUnwantedWizards {

		private static Set<String> CATEGORIES_TO_REMOVE = new HashSet<String>(Arrays
				.asList(new String[] { "org.eclipse.pde.PDE", "org.eclipse.emf.codegen.ecore.ui.wizardCategory" }));

		private static Set<String> IDS_TO_REMOVE = new HashSet<String>(Arrays.asList(
				new String[] { "org.eclipse.ui.wizards.new.project", "org.eclipse.equinox.p2.replication.import",
						"org.eclipse.equinox.p2.replication.importfrominstallation",
						"org.eclipse.team.ui.ProjectSetImportWizard", "org.eclipse.equinox.p2.replication.export",
						"org.eclipse.team.ui.ProjectSetExportWizard" }));

		static void run() {
			final List<IWizardCategory> cats = new ArrayList<>();
			AbstractExtensionWizardRegistry r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench()
					.getNewWizardRegistry();
			cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
			r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getImportWizardRegistry();
			cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
			r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getExportWizardRegistry();
			cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
			for (final IWizardDescriptor wizard : getAllWizards(cats.toArray(new IWizardCategory[0]))) {
				final String id = wizard.getCategory().getId();
				if (CATEGORIES_TO_REMOVE.contains(id) || IDS_TO_REMOVE.contains(wizard.getId())) {
					// System.out.println("Removing wizard " + wizard.getId() +
					// " in category " + id);
					final WorkbenchWizardElement element = (WorkbenchWizardElement) wizard;
					r.removeExtension(element.getConfigurationElement().getDeclaringExtension(),
							new Object[] { element });
				}
			}

		}

		static private IWizardDescriptor[] getAllWizards(final IWizardCategory[] categories) {
			final List<IWizardDescriptor> results = new ArrayList<IWizardDescriptor>();
			for (final IWizardCategory wizardCategory : categories) {

				results.addAll(Arrays.asList(wizardCategory.getWizards()));
				results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
			}
			return results.toArray(new IWizardDescriptor[0]);
		}

	}

	static class RearrangeMenus {

		public final static String[] MENU_ITEMS_TO_REMOVE = new String[] { "openWorkspace", "helpSearch" };
		public final static Map<String, String> MENU_IMAGES = new HashMap<String, String>() {

			{
				put("print", "menu.print2");
				put("save", "menu.save2");
				put("saveAs", "menu.saveas2");
				put("saveAll", "menu.saveall2");
				put("revert", "menu.revert2");
				put("refresh", "navigator/navigator.refresh2");
				put("new", "navigator/navigator.new2");
				put("import", "menu.import2");
				put("export", "menu.export2");
				put("undo", "menu.undo2");
				put("redo", "menu.redo2");
				put("cut", "menu.cut2");
				put("copy", "menu.copy2");
				put("paste", "menu.paste2");
				put("delete", "menu.delete2");
				put("helpContents", "menu.help2");
				put("org.eclipse.search.OpenSearchDialog", "menu.search2");
				put("org.eclipse.search.OpenFileSearchPage", "menu.searchfile2");
			}
		};

		public static void run() {
			Display.getDefault().asyncExec(() -> {
				final IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();

				if (window instanceof WorkbenchWindow) {
					ActionFactory.REFRESH.create(window)
							.setImageDescriptor(GamaIcons.create("navigator/navigator.refresh2").descriptor());
					final IMenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();
					for (final IContributionItem item : menuManager.getItems()) {
						IMenuManager menu = null;
						if (item instanceof MenuManager) {
							menu = (MenuManager) item;
						} else if (item instanceof ActionSetContributionItem) {
							if (((ActionSetContributionItem) item).getInnerItem() instanceof MenuManager) {
								menu = (MenuManager) ((ActionSetContributionItem) item).getInnerItem();
							}
						}
						if (menu != null) {
							// printItemIds(menu);
							removeUnwantedItems(menu);
							changeFileIcons(menu);
						}
					}
					menuManager.updateAll(true);
				}

			});

		}

		/**
		 * @param menu
		 */
		// private static void printItemIds(final MenuManager menu) {
		// StringBuilder sb = new StringBuilder();
		// sb.append("Menu ").append(menu.getId()).append(" :: ");
		// for ( IContributionItem item : menu.getItems() ) {
		// sb.append(item.getId()).append('[').append(item.getClass().getSimpleName()).append("]
		// :: ");
		// }
		// System.out.println(sb.toString());
		// }

		private static void removeUnwantedItems(final IMenuManager menu) {
			if (menu.getId().equals("file")) {
				final IContributionItem item = menu.find("new");
				if (item instanceof MenuManager) {
					final MenuManager newMenu = (MenuManager) item;
					for (final IContributionItem subItem : newMenu.getItems()) {
						if (subItem instanceof NewWizardMenu) {
							// final NewWizardMenu nw = (NewWizardMenu) subItem;
							newMenu.remove(subItem);
							subItem.dispose();
						}
					}

				}
			}
			for (final String name : MENU_ITEMS_TO_REMOVE) {
				final IContributionItem item = menu.find(name);
				if (item != null) {
					menu.remove(item);
					item.dispose();
				}
			}
		}

		private static void changeFileIcons(final IMenuManager menu) {
			for (final String name : MENU_IMAGES.keySet()) {
				final IContributionItem item = menu.find(name);
				if (item != null) {
					changeIcon(menu, item);
				}
			}
		}

		private static void changeIcon(final IMenuManager menu, final IContributionItem item) {
			final String name = item.getId();

			if (item instanceof ActionContributionItem) {
				((ActionContributionItem) item).getAction()
						.setImageDescriptor(GamaIcons.create(MENU_IMAGES.get(name)).descriptor());
			} else if (item instanceof CommandContributionItem) {
				final CommandContributionItemParameter data = ((CommandContributionItem) item).getData();
				data.commandId = ((CommandContributionItem) item).getCommand().getId();
				// int index = menu.iindexOf(name);
				data.icon = GamaIcons.create(MENU_IMAGES.get(name)).descriptor();
				final CommandContributionItem newItem = new CommandContributionItem(data);
				newItem.setId(name);
				menu.insertAfter(name, newItem);
				menu.remove(item);
				item.dispose();
			} else if (item instanceof ActionSetContributionItem) {
				changeIcon(menu, ((ActionSetContributionItem) item).getInnerItem());
			}
		}

	}

}
