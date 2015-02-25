/**
 * Created by drogoul, 21 nov. 2014
 * 
 */
package msi.gama.gui.swt;

import java.util.*;
import org.eclipse.jface.action.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.menus.*;

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
		ActionFactory.REFRESH.create(window).setImageDescriptor(GamaIcons.create("navigator/refresh2").descriptor());
		if ( window instanceof WorkbenchWindow ) {
			final MenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();
			for ( IContributionItem item : menuManager.getItems() ) {
				MenuManager menu = null;
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

	private static void removeUnwantedItems(final MenuManager menu) {
		for ( String name : MENU_ITEMS_TO_REMOVE ) {
			IContributionItem item = menu.find(name);
			if ( item != null ) {
				menu.remove(item);
				item.dispose();
			}
		}
	}

	private static void changeFileIcons(final MenuManager menu) {
		for ( String name : MENU_IMAGES.keySet() ) {
			IContributionItem item = menu.find(name);
			if ( item != null ) {
				changeIcon(menu, item);
			}
		}
	}

	private static void changeIcon(final MenuManager menu, final IContributionItem item) {
		String name = item.getId();

		if ( item instanceof ActionContributionItem ) {
			((ActionContributionItem) item).getAction().setImageDescriptor(
				GamaIcons.create(MENU_IMAGES.get(name)).descriptor());
		} else if ( item instanceof CommandContributionItem ) {
			CommandContributionItemParameter data = ((CommandContributionItem) item).getData();
			data.commandId = ((CommandContributionItem) item).getCommand().getId();
			int index = menu.indexOf(name);
			menu.remove(item);
			item.dispose();
			data.icon = GamaIcons.create(MENU_IMAGES.get(name)).descriptor();
			CommandContributionItem newItem = new CommandContributionItem(data);
			newItem.setId(name);
			menu.insert(index, newItem);
		} else if ( item instanceof ActionSetContributionItem ) {
			changeIcon(menu, ((ActionSetContributionItem) item).getInnerItem());
		}
	}

}
