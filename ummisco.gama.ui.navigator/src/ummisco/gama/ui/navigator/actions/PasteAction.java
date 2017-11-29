/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation Andrey Loskutov <loskutov@gmx.de> - generified
 * interface, bug 462760
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.actions.CopyProjectOperation;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorMessages;
import org.eclipse.ui.part.ResourceTransfer;

import msi.gama.application.workspace.WorkspaceModelsManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Standard action for pasting resources on the clipboard to the selected resource's location.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 */
public class PasteAction extends SelectionListenerAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".PasteAction";//$NON-NLS-1$

	/**
	 * The shell in which to show any dialogs.
	 */
	private final Shell shell;

	/**
	 * System clipboard
	 */
	private final Clipboard clipboard;

	/**
	 * Creates a new action.
	 *
	 * @param shell
	 *            the shell for any dialogs
	 * @param clipboard
	 *            the clipboard
	 */
	public PasteAction(final Shell shell, final Clipboard clipboard) {
		super(WorkbenchNavigatorMessages.PasteAction_Past_);
		this.shell = shell;
		this.clipboard = clipboard;
		setToolTipText(WorkbenchNavigatorMessages.PasteAction_Paste_selected_resource_s_);
		setId(PasteAction.ID);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, "HelpId"); //$NON-NLS-1$
		// TODO INavigatorHelpContextIds.PASTE_ACTION);
	}

	@Override
	protected List<? extends IResource> getSelectedResources() {
		if (getStructuredSelection().isEmpty()) { return new ArrayList<>(); }
		final Object o = getStructuredSelection().getFirstElement();
		return super.getSelectedResources();
	}

	/**
	 * Returns the actual target of the paste action. Returns null if no valid target is selected.
	 *
	 * @return the actual target of the paste action
	 */
	private IContainer getTarget() {
		final List<? extends IResource> selectedResources = getSelectedResources();
		for (final IResource resource : selectedResources) {
			if (resource instanceof IProject
					&& !((IProject) resource).isOpen()) { return ResourcesPlugin.getWorkspace().getRoot(); }
			if (resource.getType() == IResource.FILE) { return resource.getParent(); }
			return (IContainer) resource;
		}
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Returns whether any of the given resources are linked resources.
	 *
	 * @param resources
	 *            resource to check for linked type. may be null
	 * @return true=one or more resources are linked. false=none of the resources are linked
	 */
	private boolean isLinked(final IResource[] resources) {
		for (int i = 0; i < resources.length; i++) {
			if (resources[i].isLinked()) { return true; }
		}
		return false;
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	@Override
	public void run() {
		// try a resource transfer
		final ResourceTransfer resTransfer = ResourceTransfer.getInstance();
		final IResource[] resourceData = (IResource[]) clipboard.getContents(resTransfer);

		if (resourceData != null && resourceData.length > 0) {
			if (resourceData[0].getType() == IResource.PROJECT) {
				// enablement checks for all projects
				for (int i = 0; i < resourceData.length; i++) {
					final CopyProjectOperation operation = new CopyProjectOperation(shell);
					operation.copyProject((IProject) resourceData[i]);
				}
			} else {
				// enablement should ensure that we always have access to a container
				final IContainer container = getTarget();
				final CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(shell);
				final IResource[] copied = operation.copyResources(resourceData, container);
			}
			return;
		}

		// try a file transfer
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		final String[] fileData = (String[]) clipboard.getContents(fileTransfer);

		if (fileData != null) {
			// enablement should ensure that we always have access to a container
			final IContainer container = getTarget();
			if (container == ResourcesPlugin.getWorkspace().getRoot()) {
				handlePaste(fileData);
				return;
			}
			final CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(shell);
			operation.copyFiles(fileData, container);
		}
	}

	// /**
	// * Returns the container to hold the pasted resources.
	// */
	// private IContainer getContainer() {
	// final List<? extends IResource> selection = getSelectedResources();
	// if (selection.get(0) instanceof IFile) { return ((IFile) selection.get(0)).getParent(); }
	// return (IContainer) selection.get(0);
	// }

	/**
	 * The <code>PasteAction</code> implementation of this <code>SelectionListenerAction</code> method enables this
	 * action if a resource compatible with what is on the clipboard is selected.
	 *
	 * -Clipboard must have IResource or java.io.File -Projects can always be pasted if they are open -Workspace folder
	 * may not be copied into itself -Files and folders may be pasted to a single selected folder in open project or
	 * multiple selected files in the same folder
	 */
	@Override
	protected boolean updateSelection(final IStructuredSelection selection) {
		if (!super.updateSelection(selection)) { return false; }

		final IResource[][] clipboardData = new IResource[1][];
		shell.getDisplay().syncExec(() -> {
			// clipboard must have resources or files
			final ResourceTransfer resTransfer = ResourceTransfer.getInstance();
			clipboardData[0] = (IResource[]) clipboard.getContents(resTransfer);
		});
		final IResource[] resourceData = clipboardData[0];
		final boolean isProjectRes =
				resourceData != null && resourceData.length > 0 && resourceData[0].getType() == IResource.PROJECT;

		if (isProjectRes) {
			for (int i = 0; i < resourceData.length; i++) {
				// make sure all resource data are open projects
				// can paste open projects regardless of selection
				if (resourceData[i].getType() != IResource.PROJECT
						|| ((IProject) resourceData[i]).isOpen() == false) { return false; }
			}
			return true;
		}

		if (getSelectedNonResources().size() > 0) { return false; }

		final IResource targetResource = getTarget();
		// targetResource is null if no valid target is selected (e.g., open project)
		// or selection is empty
		if (targetResource == null) { return false; }

		// can paste files and folders to a single selection (file, folder,
		// open project) or multiple file selection with the same parent
		final List<? extends IResource> selectedResources = getSelectedResources();
		if (selectedResources.size() > 1) {
			for (final IResource resource : selectedResources) {
				if (resource.getType() != IResource.FILE) { return false; }
				if (!targetResource.equals(resource.getParent())) { return false; }
			}
		}
		if (resourceData != null) {
			// linked resources can only be pasted into projects
			if (isLinked(resourceData) && targetResource.getType() != IResource.PROJECT
					&& targetResource.getType() != IResource.FOLDER) { return false; }

			if (targetResource.getType() == IResource.FOLDER) {
				// don't try to copy folder to self
				for (int i = 0; i < resourceData.length; i++) {
					if (targetResource.equals(resourceData[i])) { return false; }
				}
			}
			return true;
		}
		final TransferData[] transfers = clipboard.getAvailableTypes();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		for (int i = 0; i < transfers.length; i++) {
			if (fileTransfer.isSupportedType(transfers[i])) { return true; }
		}
		return false;
	}

	public static void handlePaste(final String[] selection) {
		for (final String name : selection) {
			final File f = new File(name);
			IContainer container;
			// Necessary as the shell can be null from outside Eclipse when dragging resource
			final Shell shell = WorkbenchHelper.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (f.isDirectory()) {
				try {
					if (WorkspaceModelsManager.instance.isGamaProject(f)) {
						container = WorkspaceModelsManager.createOrUpdateProject(f.getName());
						final CopyFilesAndFoldersOperation op = new CopyFilesAndFoldersOperation(shell);
						op.setVirtualFolders(false);
						final List<File> files = Arrays.<File> asList(f.listFiles());
						final List<String> names = new ArrayList<>();
						for (final File toCopy : files) {
							if (toCopy.getName().equals(".project"))
								continue;
							names.add(toCopy.getAbsolutePath());
						}
						op.copyFiles(names.toArray(new String[0]), container);
					} else {
						container = WorkspaceModelsManager.instance.createUnclassifiedModelsProject(new Path(name));
						final CopyFilesAndFoldersOperation op = new CopyFilesAndFoldersOperation(shell);
						op.setVirtualFolders(false);
						op.copyFiles(new String[] { name }, container);
					}
					// RefreshHandler.run(container);
				} catch (final CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					container = WorkspaceModelsManager.instance.createUnclassifiedModelsProject(new Path(name));
					final CopyFilesAndFoldersOperation op = new CopyFilesAndFoldersOperation(shell);
					op.setVirtualFolders(false);
					op.copyFiles(new String[] { name }, container);
					// RefreshHandler.run(container);
				} catch (final CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void handlePasteIntoUserModels() {
		final FileTransfer transfer = FileTransfer.getInstance();
		final String[] selection = (String[]) clipboard.getContents(transfer);
		if (selection != null && selection.length != 0)
			handlePaste(selection);
	}
}
