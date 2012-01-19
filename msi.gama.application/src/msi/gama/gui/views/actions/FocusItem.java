/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.commands.FocusMenu;
import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class FocusItem extends GamaViewItem implements IMenuCreator {

	/**
	 * @param view
	 */

	Menu menu;

	FocusItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof LayeredDisplayView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Focus on...", IAction.AS_DROP_DOWN_MENU,
				getImageDescriptor("icons/button_focus.png")) {

				@Override
				public void run() {}
			};
		action.setMenuCreator(this);
		return new ActionContributionItem(action);
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	@Override
	public Menu getMenu(final Control parent) {
		if ( menu != null ) {
			menu.dispose();
		}
		menu = new Menu(parent);
		new FocusMenu().fill(menu, -1);
		return menu;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public Menu getMenu(final Menu parent) {
		return null;
	}
}
