/**
 * Created by drogoul, 21 nov. 2014
 *
 */
package msi.gama.gui.swt;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.NewWizardMenu;
import org.eclipse.ui.internal.ActionSetContributionItem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * Class RemoveUnwantedMenus.
 *
 * @author drogoul
 * @since 21 nov. 2014
 *
 */
public class RearrangeMenus {

	public final static String[] MENU_ITEMS_TO_REMOVE = new String[] { "openWorkspace", "helpSearch" };
	public final static Map<String, String> MENU_IMAGES = new HashMap() {

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

		final IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();
		ActionFactory.REFRESH.create(window)
			.setImageDescriptor(GamaIcons.create("navigator/navigator.refresh2").descriptor());
		if ( window instanceof WorkbenchWindow ) {
			final IMenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();
			for ( final IContributionItem item : menuManager.getItems() ) {
				IMenuManager menu = null;
				if ( item instanceof MenuManager ) {
					menu = (MenuManager) item;
				} else if ( item instanceof ActionSetContributionItem ) {
					menu = (MenuManager) ((ActionSetContributionItem) item).getInnerItem();
				}
				if ( menu != null ) {
					// printItemIds(menu);
					removeUnwantedItems(menu);
					changeFileIcons(menu);
				}
			}
			menuManager.updateAll(true);
		}

	}

	/**
	 * @param menu
	 */
	// private static void printItemIds(final MenuManager menu) {
	// StringBuilder sb = new StringBuilder();
	// sb.append("Menu ").append(menu.getId()).append(" :: ");
	// for ( IContributionItem item : menu.getItems() ) {
	// sb.append(item.getId()).append('[').append(item.getClass().getSimpleName()).append("] :: ");
	// }
	// System.out.println(sb.toString());
	// }

	private static void removeUnwantedItems(final IMenuManager menu) {
		if ( menu.getId().equals("file") ) {
			final IContributionItem item = menu.find("new");
			if ( item instanceof MenuManager ) {
				final MenuManager newMenu = (MenuManager) item;
				for ( final IContributionItem subItem : newMenu.getItems() ) {
					if ( subItem instanceof NewWizardMenu ) {
						final NewWizardMenu nw = (NewWizardMenu) subItem;
						newMenu.remove(subItem);
						subItem.dispose();
					}
				}

			}
		}
		for ( final String name : MENU_ITEMS_TO_REMOVE ) {
			final IContributionItem item = menu.find(name);
			if ( item != null ) {
				menu.remove(item);
				item.dispose();
			}
		}
	}

	private static void changeFileIcons(final IMenuManager menu) {
		for ( final String name : MENU_IMAGES.keySet() ) {
			final IContributionItem item = menu.find(name);
			if ( item != null ) {
				changeIcon(menu, item);
			}
		}
	}

	private static void changeIcon(final IMenuManager menu, final IContributionItem item) {
		final String name = item.getId();

		if ( item instanceof ActionContributionItem ) {
			((ActionContributionItem) item).getAction()
				.setImageDescriptor(GamaIcons.create(MENU_IMAGES.get(name)).descriptor());
		} else if ( item instanceof CommandContributionItem ) {
			final CommandContributionItemParameter data = ((CommandContributionItem) item).getData();
			data.commandId = ((CommandContributionItem) item).getCommand().getId();
			// int index = menu.iindexOf(name);
			data.icon = GamaIcons.create(MENU_IMAGES.get(name)).descriptor();
			final CommandContributionItem newItem = new CommandContributionItem(data);
			newItem.setId(name);
			menu.insertAfter(name, newItem);
			menu.remove(item);
			item.dispose();
		} else if ( item instanceof ActionSetContributionItem ) {
			changeIcon(menu, ((ActionSetContributionItem) item).getInnerItem());
		}
	}

}
