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
public class ZoomInItem extends GamaViewItem {

	/**
	 * @param view
	 */
	ZoomInItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Zoom in", "Zoom in", IAction.AS_PUSH_BUTTON, IGamaIcons.DISPLAY_TOOLBAR_ZOOMIN.descriptor()) {

				@Override
				public void run() {

					IViewWithZoom view = (IViewWithZoom) getView();
					if ( view == null ) { return; }
					view.zoomIn();

				}
			};
		return new ActionContributionItem(action);
	}
}
