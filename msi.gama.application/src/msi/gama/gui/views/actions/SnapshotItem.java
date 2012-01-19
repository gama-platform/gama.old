/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.*;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.runtime.GAMA;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class SnapshotItem extends GamaViewItem {

	/**
	 * @param view
	 */
	SnapshotItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof LayeredDisplayView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action = new Action("Take a snapshot", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				((LayerDisplayOutput) getView().getOutput()).save(GAMA.getDefaultScope());
			}
		};
		action.setImageDescriptor(getImageDescriptor("icons/button_snapshot.png"));
		return new ActionContributionItem(action);
	}
}
