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
public class ZoomFitItem extends GamaViewItem {

	/**
	 * @param view
	 */
	ZoomFitItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Zoom fit", "Zoom to fit view", IAction.AS_PUSH_BUTTON, GamaIcons.action_zoomfit) {

				@Override
				public void run() {
					IViewWithZoom view = (IViewWithZoom) getView();
					if ( view == null ) { return; }
					view.zoomToFit();
				}
			};
		return new ActionContributionItem(action);
	}
}
