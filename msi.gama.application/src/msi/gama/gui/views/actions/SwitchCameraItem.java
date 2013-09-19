package msi.gama.gui.views.actions;

/**
 * Created by drogoul, 19 janv. 2012
 * 
 */

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
public class SwitchCameraItem extends GamaViewItem {

	/**
	 * @param view
	 */
	SwitchCameraItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof IViewWithZoom) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Switch camera", "Switch camera between Arcball and FreeFly", IAction.AS_PUSH_BUTTON,
				IGamaIcons.DISPLAY_TOOLBAR_CAMERA.descriptor()) {

				@Override
				public void run() {
					IViewWithZoom view = (IViewWithZoom) getView();
					if ( view == null ) { return; }
					view.toggleCamera();
				}
			};
		return new ActionContributionItem(action);
	}
}
