/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceAction;
import org.eclipse.ui.ide.undo.MoveResourcesOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.actions.LTKLauncher;

import ummisco.gama.ui.navigator.contents.LinkedFile;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Standard action for renaming the selected resources.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class RenameResourceAction extends WorkspaceAction {

	// private final boolean saving = false;

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".RenameResourceAction";//$NON-NLS-1$

	/**
	 * The new path.
	 */
	private IPath newPath;

	private String[] modelProviderIds;

	private static final String CHECK_RENAME_TITLE = IDEWorkbenchMessages.RenameResourceAction_checkTitle;

	private static final String CHECK_RENAME_MESSAGE = IDEWorkbenchMessages.RenameResourceAction_readOnlyCheck;

	private static String RESOURCE_EXISTS_TITLE = IDEWorkbenchMessages.RenameResourceAction_resourceExists;

	private static String RESOURCE_EXISTS_MESSAGE = IDEWorkbenchMessages.RenameResourceAction_overwriteQuestion;

	private static String PROJECT_EXISTS_MESSAGE = IDEWorkbenchMessages.RenameResourceAction_overwriteProjectQuestion;

	private static String PROJECT_EXISTS_TITLE = IDEWorkbenchMessages.RenameResourceAction_projectExists;

	/**
	 * Creates a new action. Using this constructor directly will rename using a dialog rather than the inline editor of
	 * a ResourceNavigator.
	 *
	 * @param provider
	 *            the IShellProvider for any dialogs
	 * @since 3.4
	 */
	public RenameResourceAction(final IShellProvider provider) {
		super(provider, IDEWorkbenchMessages.RenameResourceAction_text);
		initAction();
	}

	private void initAction() {
		setToolTipText(IDEWorkbenchMessages.RenameResourceAction_toolTip);
		setId(ID);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IIDEHelpContextIds.RENAME_RESOURCE_ACTION);
	}

	/**
	 * Check if the user wishes to overwrite the supplied resource
	 *
	 * @returns true if there is no collision or delete was successful
	 * @param shell
	 *            the shell to create the dialog in
	 * @param destination
	 *            - the resource to be overwritten
	 */
	private boolean checkOverwrite(final Shell shell, final IResource destination) {

		final boolean[] result = new boolean[1];

		// Run it inside of a runnable to make sure we get to parent off of the
		// shell as we are not in the UI thread.

		final Runnable query = () -> {
			final String pathName = destination.getFullPath().makeRelative().toString();
			String message = RESOURCE_EXISTS_MESSAGE;
			String title = RESOURCE_EXISTS_TITLE;
			if (destination.getType() == IResource.PROJECT) {
				message = PROJECT_EXISTS_MESSAGE;
				title = PROJECT_EXISTS_TITLE;
			}
			result[0] =
					MessageDialog.openQuestion(shell, title, MessageFormat.format(message, new Object[] { pathName }));
		};

		shell.getDisplay().syncExec(query);
		return result[0];
	}

	/**
	 * Check if the supplied resource is read only or null. If it is then ask the user if they want to continue. Return
	 * true if the resource is not read only or if the user has given permission.
	 *
	 * @return boolean
	 */
	private boolean checkReadOnlyAndNull(final IResource currentResource) {
		// Do a quick read only and null check
		if (currentResource == null) { return false; }

		// Do a quick read only check
		final ResourceAttributes attributes = currentResource.getResourceAttributes();
		if (attributes != null && attributes
				.isReadOnly()) { return MessageDialog.openQuestion(WorkbenchHelper.getShell(), CHECK_RENAME_TITLE,
						MessageFormat.format(CHECK_RENAME_MESSAGE, new Object[] { currentResource.getName() })); }

		return true;
	}

	/*
	 * (non-Javadoc) Method declared on WorkspaceAction.
	 */
	@Override
	protected String getOperationMessage() {
		return IDEWorkbenchMessages.RenameResourceAction_progress;
	}

	/*
	 * (non-Javadoc) Method declared on WorkspaceAction.
	 */
	@Override
	protected String getProblemsMessage() {
		return IDEWorkbenchMessages.RenameResourceAction_problemMessage;
	}

	/*
	 * (non-Javadoc) Method declared on WorkspaceAction.
	 */
	@Override
	protected String getProblemsTitle() {
		return IDEWorkbenchMessages.RenameResourceAction_problemTitle;
	}

	/**
	 * Return the new name to be given to the target resource.
	 *
	 * @return java.lang.String
	 * @param resource
	 *            the resource to query status on
	 */
	protected String queryNewResourceName(final IResource resource) {
		final IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
		final IPath prefix = resource.getFullPath().removeLastSegments(1);
		final IInputValidator validator = string -> {
			if (resource.getName()
					.equals(string)) { return IDEWorkbenchMessages.RenameResourceAction_nameMustBeDifferent; }
			final IStatus status = workspace.validateName(string, resource.getType());
			if (!status.isOK()) { return status.getMessage(); }
			if (workspace.getRoot()
					.exists(prefix.append(string))) { return IDEWorkbenchMessages.RenameResourceAction_nameExists; }
			return null;
		};

		final InputDialog dialog =
				new InputDialog(WorkbenchHelper.getShell(), IDEWorkbenchMessages.RenameResourceAction_inputDialogTitle,
						IDEWorkbenchMessages.RenameResourceAction_inputDialogMessage, resource.getName(), validator);
		dialog.setBlockOnOpen(true);
		final int result = dialog.open();
		if (result == Window.OK) { return dialog.getValue(); }
		return null;
	}

	/*
	 * (non-Javadoc) Method declared on IAction; overrides method on WorkspaceAction.
	 */
	@Override
	public void run() {
		final IResource currentResource = getCurrentResource();
		if (currentResource == null || !currentResource.exists()) { return; }
		if (LTKLauncher.openRenameWizard(getStructuredSelection())) { return; }
		// Do a quick read only and null check
		if (!checkReadOnlyAndNull(currentResource)) { return; }
		final String newName = queryNewResourceName(currentResource);
		if (newName == null || newName.equals("")) { //$NON-NLS-1$
			return;
		}
		newPath = currentResource.getFullPath().removeLastSegments(1).append(newName);
		super.run();
	}

	/**
	 * Return the currently selected resource. Only return an IResouce if there is one and only one resource selected.
	 *
	 * @return IResource or <code>null</code> if there is zero or more than one resources selected.
	 */
	private IResource getCurrentResource() {
		final List<?> resources = getSelectedResources();
		if (resources.size() == 1) { return (IResource) resources.get(0); }
		return null;

	}

	@Override
	protected List<? extends IResource> getSelectedResources() {
		final IStructuredSelection selection = getStructuredSelection();
		if (selection.toList().stream()
				.anyMatch(each -> (each instanceof LinkedFile))) { return Collections.EMPTY_LIST; }
		return super.getSelectedResources();
	}

	/**
	 * @param path
	 *            the path
	 * @param resource
	 *            the resource
	 */
	protected void runWithNewPath(final IPath path, final IResource resource) {
		this.newPath = path;
		super.run();
	}

	/**
	 * The <code>RenameResourceAction</code> implementation of this <code>SelectionListenerAction</code> method ensures
	 * that this action is disabled if any of the selections are not resources or resources that are not local.
	 */
	@Override
	protected boolean updateSelection(final IStructuredSelection selection) {

		if (selection.size() > 1) { return false; }
		if (!super.updateSelection(selection)) { return false; }

		final IResource currentResource = getCurrentResource();
		if (currentResource == null || !currentResource.exists()) { return false; }

		return true;
	}

	/**
	 * Returns the model provider ids that are known to the client that instantiated this operation.
	 *
	 * @return the model provider ids that are known to the client that instantiated this operation.
	 * @since 3.2
	 */
	public String[] getModelProviderIds() {
		return modelProviderIds;
	}

	/**
	 * Sets the model provider ids that are known to the client that instantiated this operation. Any potential side
	 * effects reported by these models during validation will be ignored.
	 *
	 * @param modelProviderIds
	 *            the model providers known to the client who is using this operation.
	 * @since 3.2
	 */
	public void setModelProviderIds(final String[] modelProviderIds) {
		this.modelProviderIds = modelProviderIds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.actions.WorkspaceAction#createOperation(org.eclipse.core.runtime.IStatus[])
	 *
	 * Overridden to create and execute an undoable operation that performs the rename.
	 * 
	 * @since 3.3
	 */
	@Override
	protected IRunnableWithProgress createOperation(final IStatus[] errorStatus) {
		return monitor -> {
			final IResource[] resources = getActionResources().toArray(new IResource[getActionResources().size()]);
			// Rename is only valid for a single resource. This has already
			// been validated.
			if (resources.length == 1) {
				// check for overwrite
				final IWorkspaceRoot workspaceRoot = resources[0].getWorkspace().getRoot();
				final IResource newResource = workspaceRoot.findMember(newPath);
				boolean go = true;
				if (newResource != null) {
					go = checkOverwrite(WorkbenchHelper.getShell(), newResource);
				}
				if (go) {
					final MoveResourcesOperation op = new MoveResourcesOperation(resources[0], newPath,
							IDEWorkbenchMessages.RenameResourceAction_operationTitle);
					op.setModelProviderIds(getModelProviderIds());
					try {
						PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(op, monitor,
								WorkspaceUndoUtil.getUIInfoAdapter(WorkbenchHelper.getShell()));
					} catch (final ExecutionException e) {
						if (e.getCause() instanceof CoreException) {
							errorStatus[0] = ((CoreException) e.getCause()).getStatus();
						} else {
							errorStatus[0] = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, getProblemsMessage(), e);
						}
					}
				}
			}
		};
	}
}
