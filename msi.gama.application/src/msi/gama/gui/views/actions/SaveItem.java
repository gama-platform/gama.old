/*********************************************************************************************
 * 
 *
 * 'SaveItem.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
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
public class SaveItem extends GamaViewItem {

	/**
	 * @param view
	 */
	SaveItem(final GamaViewPart view) {
		super(view);

		if ( !(view instanceof PopulationInspectView) ) { throw new IllegalArgumentException(); }
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action = new Action("Save as CSV", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				((PopulationInspectView) getView()).saveAsCSV();
			}
		};
		action.setToolTipText("Save the current selection of agents and attributes into a CSV file");
		action.setImageDescriptor(IGamaIcons.ACTION_SAVE.descriptor());
		return new ActionContributionItem(action);
	}
}
