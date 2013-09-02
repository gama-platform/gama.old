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
public class OverlayItem extends GamaViewItem {

	/**
	 * @param view
	 */
	OverlayItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof LayeredDisplayView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Toggle bottom overlay", "Show/Hide overlay", IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/application-dock-270.png")) {

				@Override
				public void run() {
					((LayeredDisplayView) getView()).toogleOverlay();
				}
			};
		return new ActionContributionItem(action);
	}
}
