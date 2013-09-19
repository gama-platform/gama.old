/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.views.*;
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

		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action = new Action("Take a snapshot", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				((IViewWithZoom) getView()).snapshot();
			}
		};
		action.setImageDescriptor(IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT.descriptor());
		return new ActionContributionItem(action);
	}
}
