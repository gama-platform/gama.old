package ummisco.gama.ui.commands;

import java.util.*;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.*;

public class ShowViewContributionItem extends CompoundContributionItem {

	public ShowViewContributionItem() {}

	public ShowViewContributionItem(final String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		List<IContributionItem> menuContributionList = new ArrayList();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IContributionItem item = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		menuContributionList.add(item); // add the list of views in the menu
		return menuContributionList.toArray(new IContributionItem[menuContributionList.size()]);
	}

}
