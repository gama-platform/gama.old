/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;

/**
 * The class PickingItem.
 * 
 * @author agrignard
 * @since 08 Aug 2012
 * 
 */
public class ShapeFileItem extends GamaViewItem {

	/**
	 * @param view
	 */
	ShapeFileItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Choose a .shp file", IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/button_shp.png")) {

				@Override
				public void run() {
					IViewWithZoom view = (IViewWithZoom) getView();
					if ( view == null ) { return; }
					view.addShapeFile();
				}
			};
		return new ActionContributionItem(action);
	}
}
