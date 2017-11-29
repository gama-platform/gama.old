/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.internal.AddFromHistoryAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.team.internal.ui.TeamUIMessages;
import org.eclipse.team.internal.ui.TeamUIPlugin;
import org.eclipse.team.internal.ui.history.LocalHistoryPage;
import org.eclipse.team.internal.ui.history.LocalHistoryPageSource;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.history.IHistoryPage;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceAction;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class ShowLocalHistory extends WorkspaceAction {

	boolean isFile;
	AddFromHistoryAction projectAction = new AddFromHistoryAction();

	protected ShowLocalHistory(final IShellProvider provider) {
		super(provider, "Local history...");
	}

	private IStructuredSelection fSelection;

	@Override
	public void run() {
		if (!isFile) {
			projectAction.run(null);
			return;
		}
		final IFileState states[] = getLocalHistory();
		if (states == null || states.length == 0)
			return;
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(monitor -> {
				final IResource resource = (IResource) fSelection.getFirstElement();
				final Runnable r = () -> {
					final IHistoryView view = TeamUI.showHistoryFor(TeamUIPlugin.getActivePage(), resource,
							LocalHistoryPageSource.getInstance());
					final IHistoryPage page = view.getHistoryPage();
					if (page instanceof LocalHistoryPage) {
						final LocalHistoryPage historyPage = (LocalHistoryPage) page;
						historyPage.setClickAction(isCompare());
					}
				};
				WorkbenchHelper.asyncRun(r);
			});
		} catch (final InvocationTargetException exception) {
			ErrorDialog.openError(WorkbenchHelper.getShell(), null, null,
					new Status(IStatus.ERROR, TeamUIPlugin.PLUGIN_ID, IStatus.ERROR, TeamUIMessages.ShowLocalHistory_1,
							exception.getTargetException()));
		} catch (final InterruptedException exception) {}
	}

	@Override
	protected boolean updateSelection(final IStructuredSelection sel) {
		fSelection = sel;
		projectAction.selectionChanged(null, sel);
		isFile = selectionIsOfType(IResource.FILE);
		if (!isFile) {
			return sel.size() == 1 && selectionIsOfType(IResource.FOLDER | IResource.PROJECT);
		} else
			return true;
	}

	protected boolean isCompare() {
		return false;
	}

	public IStructuredSelection getSelection() {
		return fSelection;
	}

	protected IFileState[] getLocalHistory() {
		final IFile file = ResourceManager.getFile(getSelection().getFirstElement());
		IFileState states[] = null;
		try {
			if (file != null)
				states = file.getHistory(null);
		} catch (final CoreException ex) {
			MessageDialog.openError(WorkbenchHelper.getShell(), getPromptTitle(), ex.getMessage());
			return null;
		}

		if (states == null || states.length <= 0) {
			MessageDialog.openInformation(WorkbenchHelper.getShell(), getPromptTitle(),
					TeamUIMessages.ShowLocalHistory_0);
			return states;
		}
		return states;
	}

	protected String getPromptTitle() {
		return TeamUIMessages.ShowLocalHistory_2;
	}

	@Override
	protected String getOperationMessage() {
		return TeamUIMessages.ShowLocalHistory_2;
	}

}
