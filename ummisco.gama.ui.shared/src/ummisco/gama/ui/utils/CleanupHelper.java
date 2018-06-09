/*********************************************************************************************
 *
 * 'CleanupHelper.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
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
		ForceMaximizeRestoration.run();
		RemoveActivities.run();
	}

	static class RemoveActivities {
		static void run() {
			final IWorkbenchActivitySupport was = PlatformUI.getWorkbench().getActivitySupport();
			was.setEnabledActivityIds(new HashSet<>());
		}
	}

	static class ForceMaximizeRestoration {
		public static void run() {
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (final IWorkbenchWindow window : windows) {
				final IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					page.addPartListener(new IPartListener2() {

						@Override
						public void partVisible(final IWorkbenchPartReference partRef) {}

						@Override
						public void partOpened(final IWorkbenchPartReference partRef) {}

						@Override
						public void partInputChanged(final IWorkbenchPartReference partRef) {}

						@Override
						public void partHidden(final IWorkbenchPartReference partRef) {}

						@Override
						public void partDeactivated(final IWorkbenchPartReference partRef) {}

						@Override
						public void partClosed(final IWorkbenchPartReference partRef) {}

						@Override
						public void partBroughtToTop(final IWorkbenchPartReference partRef) {}

						@Override
						public void partActivated(final IWorkbenchPartReference partRef) {
							final IViewReference[] refs = page.getViewReferences();
							final IEditorReference[] eds = page.getEditorReferences();
							for (final IViewReference ref : refs) {
								if (!partRef.equals(ref) && page.getPartState(ref) == IWorkbenchPage.STATE_MAXIMIZED) {
									page.toggleZoom(ref);
									break;
								}
							}
							for (final IEditorReference ref : eds) {
								if (!partRef.equals(ref) && page.getPartState(ref) == IWorkbenchPage.STATE_MAXIMIZED) {
									page.toggleZoom(ref);
									break;
								}
							}

						}
					});
				}
			}
		}
	}

	static class RemoveUnwantedActionSets extends PerspectiveAdapter /* implements IStartup */ {

		String[] TOOLBAR_ACTION_SETS_TO_REMOVE = new String[] { "org.eclipse", "msi.gama.lang.gaml.Gaml",
				"org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo" };
		String[] MENUS_TO_REMOVE = new String[] { "org.eclipse.ui.run", "window", "navigate", "project" };

		public static void run() {
			final RemoveUnwantedActionSets remove = new RemoveUnwantedActionSets();
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (final IWorkbenchWindow window : windows) {
				final IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					// Doing the initial cleanup on the default perspective
					// (modeling)
					remove.perspectiveActivated(page, null);
				}
				window.addPerspectiveListener(remove);
			}
		}

		@Override
		public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			final WorkbenchWindow w = (WorkbenchWindow) page.getWorkbenchWindow();
			WorkbenchHelper.asyncRun(() -> {
				// RearrangeMenus.run();
				final IContributionItem[] items = w.getCoolBarManager2().getItems();
				// System.out.println(Arrays.toString(items));
				// We remove all contributions to the toolbar that do not
				// relate
				// to gama
				for (final IContributionItem item : items) {

					for (final String s1 : TOOLBAR_ACTION_SETS_TO_REMOVE) {
						if (item.getId().contains(s1)) {
							try {
								if (w.getCoolBarManager2().find(item.getId()) != null) {
									w.getCoolBarManager2().remove(item);
								}
							} catch (final Exception e) {}
						}
					}
				}

				// exploreMenus(w.getMenuBarManager(), "");
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

		// private void exploreMenus(final IMenuManager m, final String before) {
		// for (final IContributionItem o : m.getItems()) {
		// System.out.println(before + "Item " + o.getClass().getSimpleName() + " " + o.getId());
		// if (o instanceof IMenuManager)
		// exploreMenus((IMenuManager) o, "===");
		// }
		//
		// }

		@Override
		public void perspectiveChanged(final IWorkbenchPage p, final IPerspectiveDescriptor d, final String c) {
			if (c.equals(IWorkbenchPage.CHANGE_RESET_COMPLETE)) {
				perspectiveActivated(p, d);
			}
		}

	}

	static class RemoveUnwantedWizards {

		private static Set<String> CATEGORIES_TO_REMOVE = new HashSet<>(Arrays
				.asList(new String[] { "org.eclipse.pde.PDE", "org.eclipse.emf.codegen.ecore.ui.wizardCategory" }));

		private static Set<String> IDS_TO_REMOVE = new HashSet<>(Arrays.asList(
				new String[] { "org.eclipse.ui.wizards.new.project", "org.eclipse.equinox.p2.replication.import",
						"org.eclipse.equinox.p2.replication.importfrominstallation",
						"org.eclipse.team.ui.ProjectSetImportWizard", "org.eclipse.equinox.p2.replication.export",
						"org.eclipse.team.ui.ProjectSetExportWizard" }));

		static void run() {
			final List<IWizardCategory> cats = new ArrayList<>();
			AbstractExtensionWizardRegistry r =
					(AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
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
			final List<IWizardDescriptor> results = new ArrayList<>();
			for (final IWizardCategory wizardCategory : categories) {

				results.addAll(Arrays.asList(wizardCategory.getWizards()));
				results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
			}
			return results.toArray(new IWizardDescriptor[0]);
		}

	}

	static class RearrangeMenus {

		public final static Set<String> MENU_ITEMS_TO_REMOVE = new HashSet<>(Arrays.asList("openWorkspace",
				"helpSearch", "org.eclipse.search.OpenFileSearchPage", "textSearchSubMenu", "reopenEditors",
				"converstLineDelimitersTo", "org.eclipse.equinox.p2.ui.sdk.update",
				"org.eclipse.equinox.p2.ui.sdk.install", "org.eclipse.equinox.p2.ui.sdk.installationDetails",
				"org.eclipse.e4.ui.importer.openDirectory.menu"));
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
				put("org.eclipse.ui.openLocalFile", "navigator/navigator.open2");
				put("converstLineDelimitersTo", "menu.delimiter2");
			}
		};

		public static void run() {
			WorkbenchHelper.run(() -> {
				final IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
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
							processItems(menu);
						}
					}
					menuManager.updateAll(true);
				}

			});

		}

		private static void processItems(final IMenuManager menu) {
			// final StringBuilder sb = new StringBuilder();
			// sb.append("Menu ").append(menu.getId()).append(" :: ");
			for (final IContributionItem item : menu.getItems()) {
				final String name = item.getId();
				// System.out.println(name);
				if (MENU_ITEMS_TO_REMOVE.contains(name)) {
					item.setVisible(false);
					continue;
				}
				if (item.isGroupMarker() || item.isSeparator() || !item.isVisible()) {
					continue;
				}
				if (MENU_IMAGES.containsKey(name)) {
					changeIcon(menu, item, GamaIcons.create(MENU_IMAGES.get(name)).descriptor());
				}
				// sb.append(Strings.LN).append(Strings.TAB);
				// sb.append(name).append('[').append(item.getClass().getSimpleName()).append("]:: ");
			}
			// System.out.println(sb.toString());
		}

		private static void changeIcon(final IMenuManager menu, final IContributionItem item,
				final ImageDescriptor image) {
			if (item instanceof ActionContributionItem) {
				((ActionContributionItem) item).getAction().setImageDescriptor(image);
			} else if (item instanceof CommandContributionItem) {
				final CommandContributionItemParameter data = ((CommandContributionItem) item).getData();
				data.commandId = ((CommandContributionItem) item).getCommand().getId();
				data.icon = image;
				final CommandContributionItem newItem = new CommandContributionItem(data);
				newItem.setId(item.getId());
				menu.insertAfter(item.getId(), newItem);
				menu.remove(item);
				item.dispose();
			} else if (item instanceof ActionSetContributionItem) {
				changeIcon(menu, ((ActionSetContributionItem) item).getInnerItem(), image);
			} else if (item instanceof MenuManager) {
				((MenuManager) item).setImageDescriptor(image);
			}
		}

	}

}
