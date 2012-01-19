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
public class LayersItem extends GamaViewItem {

	/**
	 * @param view
	 */
	LayersItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof LayeredDisplayView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Open/Close layers controls", IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/button_side_controls.png")) {

				@Override
				public void run() {
					((LayeredDisplayView) getView()).toggleControls();
				}
			};
		return new ActionContributionItem(action);
	}
}
