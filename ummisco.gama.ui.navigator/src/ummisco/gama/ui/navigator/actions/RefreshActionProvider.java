/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/

package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import ummisco.gama.ui.resources.GamaIcons;

public class RefreshActionProvider extends CommonActionProvider {

	private RefreshAction refreshAction;
	private Shell shell;

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		super.init(aSite);
		shell = aSite.getViewSite().getShell();
		makeActions();
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
		updateActionBars();
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		refreshAction.selectionChanged(selection);
		menu.appendToGroup("group.refresh", refreshAction);
	}

	protected void makeActions() {
		final IShellProvider sp = () -> shell;
		refreshAction = new RefreshAction(sp);
		refreshAction.setImageDescriptor(GamaIcons.create("navigator/navigator.refresh2").descriptor());//$NON-NLS-1$
		refreshAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_REFRESH);
	}

	@Override
	public void updateActionBars() {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		refreshAction.selectionChanged(selection);
	}

}
