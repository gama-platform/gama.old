/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.GamaIcons;
import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class ClearItem extends GamaViewItem {

	/**
	 * @param view
	 */
	ClearItem(final GamaViewPart view) {
		super(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action = new GamaAction("Clear", "Clear the console ", IAction.AS_PUSH_BUTTON, GamaIcons.action_erase) {

			@Override
			public void run() {
				if ( view instanceof ConsoleView ) {
					((ConsoleView) view).setText("");
				}
			}

		};

		return new ActionContributionItem(action);
	}

}
