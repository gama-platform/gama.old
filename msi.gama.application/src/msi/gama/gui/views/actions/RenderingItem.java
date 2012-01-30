/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

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
		IAction action = new Action("Enable quality rendering", IAction.AS_CHECK_BOX) {

			@Override
			public void run() {
				IDisplaySurface surface = ((LayeredDisplayView) view).getDisplaySurface();
				surface.setQualityRendering(this.isChecked());
			}
		};
		action.setImageDescriptor(getImageDescriptor("icons/button_rendering.png"));
		return new ActionContributionItem(action);
	}
}
