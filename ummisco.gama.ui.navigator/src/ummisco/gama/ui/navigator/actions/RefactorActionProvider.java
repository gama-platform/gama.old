/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation Oakland Software (Francis Upton - francisu@ieee.org)
 * bug 214271 Undo/redo not enabled if nothing selected
 ******************************************************************************/

package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import ummisco.gama.ui.resources.GamaIcons;

/**
 * @since 3.2
 *
 */
public class RefactorActionProvider extends CommonActionProvider {

	private RenameResourceAction renameAction;
	private ShowLocalHistory historyAction;
	private Shell shell;

	@Override
	public void init(final ICommonActionExtensionSite anActionSite) {
		shell = anActionSite.getViewSite().getShell();
		makeActions();
	}

	protected void makeActions() {
		final IShellProvider sp = () -> shell;
		renameAction = new RenameResourceAction(sp);
		renameAction.setImageDescriptor(GamaIcons.create("navigator/navigator.rename2").descriptor());
		renameAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_RENAME);
		historyAction = new ShowLocalHistory(sp);
		historyAction.setImageDescriptor(GamaIcons.create("navigator/navigator.date2").descriptor());
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		updateActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), renameAction);
	}

	public void handleKeyPressed(final KeyEvent event) {
		if (event.keyCode == SWT.F2 && event.stateMask == 0) {
			if (renameAction.isEnabled()) {
				renameAction.run();
			}
			// Swallow the event.
			event.doit = false;
		}
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		final boolean anyResourceSelected = !selection.isEmpty();
		if (anyResourceSelected) {
			renameAction.selectionChanged(selection);
			historyAction.selectionChanged(selection);
			menu.insertBefore(CopyAction.ID, renameAction);
			menu.insertAfter("additions", historyAction);
		}
	}

	@Override
	public void updateActionBars() {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		renameAction.selectionChanged(selection);
		historyAction.selectionChanged(selection);
	}

}
