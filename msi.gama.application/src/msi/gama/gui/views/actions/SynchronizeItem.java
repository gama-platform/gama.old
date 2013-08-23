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
public class SynchronizeItem extends GamaViewItem {

	/**
	 * @param view
	 */
	SynchronizeItem(final GamaViewPart view) {
		super(view);

		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }

	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		final IAction action =
			new GamaAction("Synchronize with simulation", "Synchronize " + view.getTitle() +
				" and the execution of the model", IAction.AS_CHECK_BOX, getImageDescriptor("icons/button_sync.png")) {

				@Override
				public void run() {
					((IViewWithZoom) view).setSynchronized(isChecked());
				}

			};

		return new ActionContributionItem(action);
	}

}
