/*********************************************************************************************
 * 
 *
 * 'ClearItem.java', in plugin 'msi.gama.application', is part of the source code of the 
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
public class ClearItem extends GamaViewItem {

	/**
	 * @param view
	 */
	ClearItem(final GamaViewPart view) {
		super(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		IAction action =
			new GamaAction("Clear", "Clear the console ", IAction.AS_PUSH_BUTTON, IGamaIcons.ACTION_CLEAR.descriptor()) {

				@Override
				public void run() {
					if ( view instanceof ConsoleView ) {
						((ConsoleView) view).setText("");
					}
				}

			};

		return new ActionContributionItem(action);
	}

}
