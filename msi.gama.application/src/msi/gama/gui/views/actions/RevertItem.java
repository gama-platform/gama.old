/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.views.GamaViewPart;
import msi.gama.kernel.experiment.EditorsList;
import msi.gama.runtime.GAMA;
import org.eclipse.jface.action.*;

/**
 * The class SnapshotItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class RevertItem extends GamaViewItem {

	/**
	 * @param view
	 */
	RevertItem(final GamaViewPart view) {
		super(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Revert parameter values", "Revert parameters to their initial values",
				IAction.AS_PUSH_BUTTON, getImageDescriptor("icons/button_undo.png")) {

				@Override
				public void run() {
					EditorsList eds = (EditorsList) GAMA.getExperiment().getParametersEditors();
					if ( eds != null ) {
						eds.revertToDefaultValue();
					}
				}
			};
		return new ActionContributionItem(action);
	}
}
