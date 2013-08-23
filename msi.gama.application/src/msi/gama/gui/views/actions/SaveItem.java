/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class SaveItem extends GamaViewItem {

	/**
	 * @param view
	 */
	SaveItem(final GamaViewPart view) {
		super(view);

		if ( !(view instanceof PopulationInspectView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action = new Action("Save as CSV", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				((PopulationInspectView) getView()).saveAsCSV();
			}
		};
		action.setToolTipText("Save the current selection of agents and attributes into a CSV file");
		action.setImageDescriptor(getImageDescriptor("icons/button_save.png"));
		return new ActionContributionItem(action);
	}
}
