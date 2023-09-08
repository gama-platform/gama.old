/*******************************************************************************************************
 *
 * ShowViewContributionItem.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.actions.ContributionItemFactory;

/**
 * The Class ShowViewContributionItem.
 */
public class ShowViewContributionItem extends CompoundContributionItem {

	/**
	 * Instantiates a new show view contribution item.
	 */
	public ShowViewContributionItem() {
	}

	/**
	 * Instantiates a new show view contribution item.
	 *
	 * @param id the id
	 */
	public ShowViewContributionItem(final String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		final List<IContributionItem> menuContributionList = new ArrayList<>();
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IContributionItem item = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		menuContributionList.add(item); // add the list of views in the menu
		return menuContributionList.toArray(new IContributionItem[menuContributionList.size()]);
	}

}
