/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.gui.views.*;
import org.eclipse.jface.action.*;

/**
 * The class RenderingItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class RenderingItem extends GamaViewItem {

	/**
	 * @param view
	 */
	RenderingItem(final GamaViewPart view) {
		super(view);
		if ( !(view instanceof LayeredDisplayView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action = new Action("Toggle antialiasing", IAction.AS_CHECK_BOX) {

			@Override
			public void run() {
				IDisplaySurface surface = ((LayeredDisplayView) view).getDisplaySurface();
				surface.setQualityRendering(this.isChecked());
			}
		};
		action
			.setToolTipText("Antialiasing of images is turned off by default, but you can choose to turn it on using this button");
		// action.setChecked(true);
		action.setImageDescriptor(getImageDescriptor("icons/button_rendering.png"));
		action.setChecked(GamaPreferences.CORE_ANTIALIAS.getValue());
		return new ActionContributionItem(action);
	}
}
