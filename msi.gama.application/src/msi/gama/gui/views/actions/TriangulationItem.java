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
public class TriangulationItem extends GamaViewItem {

	/**
	 * @param view
	 */
	TriangulationItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Apply triangulation", "Triangle view: Show triangulated shape", IAction.AS_CHECK_BOX,
				GamaIcons.action_triangulate) {

				@Override
				public void run() {
					IViewWithZoom view = (IViewWithZoom) getView();
					if ( view == null ) { return; }
					view.toggleTriangulation();
				}
			};
		return new ActionContributionItem(action);
	}
}
