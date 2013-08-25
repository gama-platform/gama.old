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
public class InertiaItem extends GamaViewItem {

	/**
	 * @param view
	 */
	InertiaItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Activate inertia mode", "", IAction.AS_CHECK_BOX,
				getImageDescriptor("icons/inertia.png")) {

				@Override
				public void run() {
					IViewWithZoom view = (IViewWithZoom) getView();
					if ( view == null ) { return; }
					view.toggleInertia();
				}
			};
		return new ActionContributionItem(action);
	}
}
