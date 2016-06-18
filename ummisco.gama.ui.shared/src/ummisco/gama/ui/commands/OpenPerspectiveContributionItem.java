package ummisco.gama.ui.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.actions.ContributionItemFactory;

public class OpenPerspectiveContributionItem extends CompoundContributionItem {

	public OpenPerspectiveContributionItem() {
	}

	public OpenPerspectiveContributionItem(final String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		final List<IContributionItem> menuContributionList = new ArrayList();
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IContributionItem item = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
		menuContributionList.add(item); // add the list of views in the menu
		return menuContributionList.toArray(new IContributionItem[menuContributionList.size()]);
	}

}
