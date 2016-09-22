package ummisco.gama.ui.navigator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

public class NavigatorActionprovider extends CommonActionProvider {

	private NavigatorActionGroup editGroup;

	@Override
	public void init(final ICommonActionExtensionSite anActionSite) {
		editGroup = new NavigatorActionGroup();

	}

	@Override
	public void dispose() {
		editGroup.dispose();
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		editGroup.fillActionBars(actionBars);
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		editGroup.fillContextMenu(menu);
	}

	@Override
	public void setContext(final ActionContext context) {
		editGroup.setContext(context);
	}

	@Override
	public void updateActionBars() {
		editGroup.updateActionBars();
	}
}
