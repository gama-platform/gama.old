/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class ZoomOutItem extends GamaViewItem {

	/**
	 * @param view
	 */
	ZoomOutItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof LayeredDisplayView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Zoom out", IAction.AS_PUSH_BUTTON,
				getImageDescriptor("icons/button_zoomout.png")) {

				@Override
				public void run() {
					final LayeredDisplayView view = (LayeredDisplayView) getView();
					if ( view == null ) { return; }
					new Thread(new Runnable() {

						@Override
						public void run() {
							IDisplaySurface imageCanvas = view.getDisplaySurface();
							while (!imageCanvas.canBeUpdated()) {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {

								}
							}
							imageCanvas.zoomOut();

						}
					}).start();
				}
			};
		return new ActionContributionItem(action);
	}
}
