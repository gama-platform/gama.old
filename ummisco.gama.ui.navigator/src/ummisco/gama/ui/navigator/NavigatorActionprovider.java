/*********************************************************************************************
 *
 * 'NavigatorActionprovider.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
