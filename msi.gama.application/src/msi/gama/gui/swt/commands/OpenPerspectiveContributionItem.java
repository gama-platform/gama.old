package msi.gama.gui.swt.commands;

import java.util.*;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.*;

public class OpenPerspectiveContributionItem extends CompoundContributionItem {

	public OpenPerspectiveContributionItem() {}

	public OpenPerspectiveContributionItem(final String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		List<IContributionItem> menuContributionList = new ArrayList();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IContributionItem item = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
		menuContributionList.add(item); // add the list of views in the menu
		return menuContributionList.toArray(new IContributionItem[menuContributionList.size()]);
	}

}
