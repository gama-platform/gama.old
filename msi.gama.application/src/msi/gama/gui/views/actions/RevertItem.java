/*********************************************************************************************
 * 
 *
 * 'RevertItem.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.IGamaIcons;
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
				IAction.AS_PUSH_BUTTON, IGamaIcons.ACTION_REVERT.descriptor()) {

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
