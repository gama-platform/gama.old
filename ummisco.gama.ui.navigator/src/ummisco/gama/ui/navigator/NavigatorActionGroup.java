/*********************************************************************************************
 *
 * 'NavigatorActionGroup.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import ummisco.gama.ui.resources.GamaIcons;

public class NavigatorActionGroup extends ActionGroup {

	private PasteAction pasteAction;

	public NavigatorActionGroup() {
		makeActions();
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		pasteAction.selectionChanged(selection);
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, pasteAction);
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		updateActionBars();
	}

	protected void makeActions() {
		pasteAction = PasteAction.INSTANCE;
		final ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		pasteAction.setImageDescriptor(GamaIcons.create("menu.paste2").descriptor());
		pasteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);
	}

	@Override
	public void updateActionBars() {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		pasteAction.selectionChanged(selection);
	}

}
