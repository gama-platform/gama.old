package ummisco.gama.ui.navigator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.MoveResourcesDescriptor;
import org.eclipse.ltk.ui.refactoring.RefactoringUI;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.actions.CopyProjectOperation;
import org.eclipse.ui.actions.MoveFilesAndFoldersOperation;
import org.eclipse.ui.actions.ReadOnlyStateChecker;
import org.eclipse.ui.ide.dialogs.ImportTypeDialog;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorMessages;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorPlugin;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.resources.ResourceDropAdapterAssistant;
import org.eclipse.ui.part.ResourceTransfer;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.TopLevelFolder;

public class NavigatorResourceDropAssistant extends ResourceDropAdapterAssistant {

	private static final IResource[] NO_RESOURCES = new IResource[0];

	private RefactoringStatus refactoringStatus;
	private IStatus returnStatus;

	@Override
	public boolean isSupportedType(final TransferData aTransferType) {
		return super.isSupportedType(aTransferType) || FileTransfer.getInstance().isSupportedType(aTransferType);
	}

	@Override
	public IStatus validateDrop(final Object target, final int aDropOperation, final TransferData transferType) {
		final IResource resource = ResourceManager.getResource(target);
		if (!(target instanceof TopLevelFolder)) {
			if (resource == null) { return WorkbenchNavigatorPlugin.createStatus(IStatus.INFO, 0,
					WorkbenchNavigatorMessages.DropAdapter_targetMustBeResource, null); }
			if (!resource.isAccessible()) { return WorkbenchNavigatorPlugin.createErrorStatus(0,
					WorkbenchNavigatorMessages.DropAdapter_canNotDropIntoClosedProject, null); }
			final IContainer destination = getActualTarget(resource);
			if (destination.getType() == IResource.ROOT) { return WorkbenchNavigatorPlugin.createErrorStatus(0,
					WorkbenchNavigatorMessages.DropAdapter_resourcesCanNotBeSiblings, null); }
		}
		String message = null;
		// drag within Eclipse?
		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
			final IResource[] selectedResources = getSelectedResources();

			if (allProjects(selectedResources) && target instanceof TopLevelFolder) { return Status.OK_STATUS; }
			if (anyProjects(selectedResources)) {
				// drop of projects not supported on other IResources
				// "Path for project must have only one segment."
				message = WorkbenchNavigatorMessages.DropAdapter_canNotDropProjectIntoProject;
			} else {
				if (selectedResources.length == 0) {
					message = WorkbenchNavigatorMessages.DropAdapter_dropOperationErrorOther;
				} else {
					CopyFilesAndFoldersOperation operation;
					if (aDropOperation == DND.DROP_COPY) {
						operation = new CopyFilesAndFoldersOperation(getShell());
					} else {
						operation = new MoveFilesAndFoldersOperation(getShell());
					}
					final IContainer destination = getActualTarget(resource);
					if (destination == null)
						message = "Not permitted";
					else if (operation.validateDestination(destination, selectedResources) != null) {
						operation.setVirtualFolders(true);
						message = operation.validateDestination(destination, selectedResources);
					}
				}
			}
		} // file import?
		else if (FileTransfer.getInstance().isSupportedType(transferType)) {
			String[] sourceNames = (String[]) FileTransfer.getInstance().nativeToJava(transferType);
			if (sourceNames == null) {
				// source names will be null on Linux. Use empty names to do
				// destination validation.
				// Fixes bug 29778
				sourceNames = new String[0];
			}
			final CopyFilesAndFoldersOperation copyOperation = new CopyFilesAndFoldersOperation(getShell());
			final IContainer destination = getActualTarget(resource);
			if (destination == null)
				message = "Not permitted";
			else
				message = copyOperation.validateImportDestination(destination, sourceNames);
		}
		if (message != null) { return WorkbenchNavigatorPlugin.createErrorStatus(0, message, null); }
		return Status.OK_STATUS;
	}

	private boolean allProjects(final IResource[] res) {
		if (res == null || res.length == 0)
			return false;
		for (int i = 0; i < res.length; i++) {
			if (res[i].getType() != IResource.PROJECT)
				return false;
		}
		return true;
	}

	private boolean anyProjects(final IResource[] res) {
		if (res == null || res.length == 0)
			return false;
		for (int i = 0; i < res.length; i++) {
			if (res[i].getType() == IResource.PROJECT)
				return true;
		}
		return false;
	}

	@Override
	public IStatus handleDrop(final CommonDropAdapter aDropAdapter, final DropTargetEvent aDropTargetEvent,
			final Object t) {
		IResource[] resources = null;
		IStatus status = null;
		final TransferData currentTransfer = aDropAdapter.getCurrentTransfer();
		if (LocalSelectionTransfer.getTransfer().isSupportedType(currentTransfer)) {
			resources = getSelectedResources();
		} else if (ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
			resources = (IResource[]) aDropTargetEvent.data;
		}

		if (t instanceof TopLevelFolder && allProjects(resources)) {
			status = performProjectCopy(aDropAdapter, getShell(), resources);
		} else {
			final IResource aTarget = ResourceManager.getResource(t);
			// alwaysOverwrite = false;
			if (aTarget == null || aDropTargetEvent.data == null) { return Status.CANCEL_STATUS; }

			if (FileTransfer.getInstance().isSupportedType(currentTransfer)) {
				status = performFileDrop(aDropAdapter, aDropTargetEvent.data);
			} else if (resources != null && resources.length > 0) {
				if (aDropAdapter.getCurrentOperation() == DND.DROP_COPY
						|| aDropAdapter.getCurrentOperation() == DND.DROP_LINK) {
					status = performResourceCopy(aDropAdapter, getShell(), resources);
				} else {
					status = performResourceMove(aDropAdapter, resources);
				}
			}
		}
		openError(status);
		final IResource aTarget = ResourceManager.getResource(t);
		final IContainer target = getActualTarget(aTarget);
		if (target != null && target.isAccessible()) {
			try {
				target.refreshLocal(IResource.DEPTH_ONE, null);
			} catch (final CoreException e) {}
		}
		return status;
	}

	private IStatus performProjectCopy(final CommonDropAdapter aDropAdapter, final Shell shell,
			final IResource[] resources) {
		ResourceManager.setSelectedFolder(aDropAdapter.getCurrentTarget());
		for (int i = 0; i < resources.length; i++) {
			final CopyProjectOperation operation = new CopyProjectOperation(shell);
			operation.copyProject((IProject) resources[i]);
		}
		return null;
	}

	@Override
	public IStatus validatePluginTransferDrop(final IStructuredSelection aDragSelection, final Object aDropTarget) {
		if (!ResourceManager.isResource(aDropTarget)) { return WorkbenchNavigatorPlugin.createStatus(IStatus.INFO, 0,
				WorkbenchNavigatorMessages.DropAdapter_targetMustBeResource, null); }
		final IResource resource = ResourceManager.getResource(aDropTarget);
		if (!resource.isAccessible()) { return WorkbenchNavigatorPlugin.createErrorStatus(0,
				WorkbenchNavigatorMessages.DropAdapter_canNotDropIntoClosedProject, null); }
		final IContainer destination = getActualTarget(resource);
		if (destination.getType() == IResource.ROOT) { return WorkbenchNavigatorPlugin.createErrorStatus(0,
				WorkbenchNavigatorMessages.DropAdapter_resourcesCanNotBeSiblings, null); }

		final IResource[] selectedResources = getSelectedResources(aDragSelection);

		String message = null;
		if (selectedResources.length == 0) {
			message = WorkbenchNavigatorMessages.DropAdapter_dropOperationErrorOther;
		} else {
			MoveFilesAndFoldersOperation operation;

			operation = new MoveFilesAndFoldersOperation(getShell());
			message = operation.validateDestination(destination, selectedResources);
		}
		if (message != null) { return WorkbenchNavigatorPlugin.createErrorStatus(0, message, null); }
		return Status.OK_STATUS;
	}

	@Override
	public IStatus handlePluginTransferDrop(final IStructuredSelection aDragSelection, final Object aDropTarget) {

		final IContainer target = getActualTarget(ResourceManager.getResource(aDropTarget));
		final IResource[] resources = getSelectedResources(aDragSelection);

		final MoveFilesAndFoldersOperation operation = new MoveFilesAndFoldersOperation(getShell());
		operation.copyResources(resources, target);

		if (target != null && target.isAccessible()) {
			try {
				target.refreshLocal(IResource.DEPTH_ONE, null);
			} catch (final CoreException e) {}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns the actual target of the drop, given the resource under the mouse. If the mouse target is a file, then
	 * the drop actually occurs in its parent. If the drop location is before or after the mouse target and feedback is
	 * enabled, the target is also the parent.
	 */
	private IContainer getActualTarget(final IResource mouseTarget) {
		if (mouseTarget == null)
			return null;
		/* if cursor is on a file, return the parent */
		if (mouseTarget.getType() == IResource.FILE) { return mouseTarget.getParent(); }
		/* otherwise the mouseTarget is the real target */
		return (IContainer) mouseTarget;
	}

	/**
	 * Returns the resource selection from the LocalSelectionTransfer.
	 *
	 * @return the resource selection from the LocalSelectionTransfer
	 */
	private IResource[] getSelectedResources() {

		final ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof IStructuredSelection) { return getSelectedResources(
				(IStructuredSelection) selection); }
		return NO_RESOURCES;
	}

	/**
	 * Returns the resource selection from the LocalSelectionTransfer.
	 *
	 * @return the resource selection from the LocalSelectionTransfer
	 */
	private IResource[] getSelectedResources(final IStructuredSelection selection) {
		final ArrayList<IResource> selectedResources = new ArrayList<IResource>();

		for (final Iterator<?> i = selection.iterator(); i.hasNext();) {
			final IResource r = ResourceManager.getResource(i.next());
			if (r != null) {
				selectedResources.add(r);
			}
		}
		return selectedResources.toArray(new IResource[selectedResources.size()]);
	}

	/**
	 * Performs a resource copy
	 */
	private IStatus performResourceCopy(final CommonDropAdapter dropAdapter, final Shell shell,
			final IResource[] sources) {
		final MultiStatus problems =
				new MultiStatus(PlatformUI.PLUGIN_ID, 1, WorkbenchNavigatorMessages.DropAdapter_problemsMoving, null);
		mergeStatus(problems, validateTarget(dropAdapter.getCurrentTarget(), dropAdapter.getCurrentTransfer(),
				dropAdapter.getCurrentOperation()));

		final IContainer target = getActualTarget(ResourceManager.getResource(dropAdapter.getCurrentTarget()));

		boolean shouldLinkAutomatically = false;
		if (target.isVirtual()) {
			shouldLinkAutomatically = true;
			for (int i = 0; i < sources.length; i++) {
				if (sources[i].getType() != IResource.FILE && sources[i].getLocation() != null) {
					// If the source is a folder, but the location is null (a
					// broken link, for example),
					// we still generate a link automatically (the best option).
					shouldLinkAutomatically = false;
					break;
				}
			}
		}

		final CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(shell);
		// if the target is a virtual folder and all sources are files, then
		// automatically create links
		if (shouldLinkAutomatically) {
			operation.setCreateLinks(true);
			operation.copyResources(sources, target);
		} else {
			boolean allSourceAreLinksOrVirtualFolders = true;
			for (int i = 0; i < sources.length; i++) {
				if (!sources[i].isVirtual() && !sources[i].isLinked()) {
					allSourceAreLinksOrVirtualFolders = false;
					break;
				}
			}
			// if all sources are either links or groups, copy then normally,
			// don't show the dialog
			if (!allSourceAreLinksOrVirtualFolders) {
				final IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
				final String dndPreference = store.getString(
						target.isVirtual() ? IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_VIRTUAL_FOLDER_MODE
								: IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_MODE);

				if (dndPreference.equals(IDEInternalPreferences.IMPORT_FILES_AND_FOLDERS_MODE_PROMPT)) {
					final ImportTypeDialog dialog =
							new ImportTypeDialog(getShell(), dropAdapter.getCurrentOperation(), sources, target);
					dialog.setResource(target);
					if (dialog.open() == Window.OK) {
						if (dialog.getSelection() == ImportTypeDialog.IMPORT_VIRTUAL_FOLDERS_AND_LINKS)
							operation.setVirtualFolders(true);
						if (dialog.getSelection() == ImportTypeDialog.IMPORT_LINK)
							operation.setCreateLinks(true);
						if (dialog.getVariable() != null)
							operation.setRelativeVariable(dialog.getVariable());
						operation.copyResources(sources, target);
					} else
						return problems;
				} else
					operation.copyResources(sources, target);
			} else
				operation.copyResources(sources, target);
		}

		return problems;
	}

	/**
	 * Performs a resource move
	 */
	private IStatus performResourceMove(final CommonDropAdapter dropAdapter, IResource[] sources) {
		final MultiStatus problems =
				new MultiStatus(PlatformUI.PLUGIN_ID, 1, WorkbenchNavigatorMessages.DropAdapter_problemsMoving, null);
		mergeStatus(problems, validateTarget(dropAdapter.getCurrentTarget(), dropAdapter.getCurrentTransfer(),
				dropAdapter.getCurrentOperation()));

		final IContainer target = getActualTarget(ResourceManager.getResource(dropAdapter.getCurrentTarget()));

		boolean shouldLinkAutomatically = false;
		if (target.isVirtual()) {
			shouldLinkAutomatically = true;
			for (int i = 0; i < sources.length; i++) {
				if (sources[i].isVirtual() || sources[i].isLinked()) {
					shouldLinkAutomatically = false;
					break;
				}
			}
		}

		if (shouldLinkAutomatically) {
			final CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(getShell());
			operation.setCreateLinks(true);
			operation.copyResources(sources, target);
		} else {
			final ReadOnlyStateChecker checker =
					new ReadOnlyStateChecker(getShell(), WorkbenchNavigatorMessages.MoveResourceAction_title,
							WorkbenchNavigatorMessages.MoveResourceAction_checkMoveMessage);
			sources = checker.checkReadOnlyResources(sources);

			try {
				final RefactoringContribution contribution =
						RefactoringCore.getRefactoringContribution(MoveResourcesDescriptor.ID);
				final MoveResourcesDescriptor descriptor = (MoveResourcesDescriptor) contribution.createDescriptor();
				descriptor.setResourcesToMove(sources);
				descriptor.setDestination(target);
				refactoringStatus = new RefactoringStatus();
				final Refactoring refactoring = descriptor.createRefactoring(refactoringStatus);

				returnStatus = null;
				final IRunnableWithProgress checkOp = monitor -> {
					try {
						refactoringStatus = refactoring.checkAllConditions(monitor);
					} catch (final CoreException ex) {
						returnStatus = WorkbenchNavigatorPlugin.createErrorStatus(0, ex.getLocalizedMessage(), ex);
					}
				};

				if (returnStatus != null)
					return returnStatus;

				try {
					PlatformUI.getWorkbench().getProgressService().run(false, false, checkOp);
				} catch (final InterruptedException e) {
					return Status.CANCEL_STATUS;
				} catch (final InvocationTargetException e) {
					return WorkbenchNavigatorPlugin.createErrorStatus(0, e.getLocalizedMessage(), e);
				}

				if (refactoringStatus.hasEntries()) {
					final Dialog dialog = RefactoringUI.createLightWeightStatusDialog(refactoringStatus, getShell(),
							WorkbenchNavigatorMessages.MoveResourceAction_title);
					final int result = dialog.open();
					if (result != IStatus.OK)
						return Status.CANCEL_STATUS;
				}

				final PerformRefactoringOperation op =
						new PerformRefactoringOperation(refactoring, CheckConditionsOperation.ALL_CONDITIONS);

				final IWorkspaceRunnable r = monitor -> op.run(monitor);

				returnStatus = null;
				final IRunnableWithProgress refactorOp = monitor -> {
					try {
						ResourcesPlugin.getWorkspace().run(r, ResourcesPlugin.getWorkspace().getRoot(),
								IWorkspace.AVOID_UPDATE, monitor);
					} catch (final CoreException ex) {
						returnStatus = WorkbenchNavigatorPlugin.createErrorStatus(0, ex.getLocalizedMessage(), ex);
					}
				};

				if (returnStatus != null)
					return returnStatus;

				try {
					PlatformUI.getWorkbench().getProgressService().run(false, false, refactorOp);
				} catch (final InterruptedException e) {
					return Status.CANCEL_STATUS;
				} catch (final InvocationTargetException e) {
					return WorkbenchNavigatorPlugin.createErrorStatus(0, e.getLocalizedMessage(), e);
				}

			} catch (final CoreException ex) {
				return WorkbenchNavigatorPlugin.createErrorStatus(0, ex.getLocalizedMessage(), ex);
			} catch (final OperationCanceledException e) {}
		}

		return problems;
	}

	/**
	 * Performs a drop using the FileTransfer transfer type.
	 */
	private IStatus performFileDrop(final CommonDropAdapter anAdapter, final Object data) {
		final int currentOperation = anAdapter.getCurrentOperation();
		final MultiStatus problems =
				new MultiStatus(PlatformUI.PLUGIN_ID, 0, WorkbenchNavigatorMessages.DropAdapter_problemImporting, null);
		mergeStatus(problems,
				validateTarget(anAdapter.getCurrentTarget(), anAdapter.getCurrentTransfer(), currentOperation));

		final IContainer target = getActualTarget(ResourceManager.getResource(anAdapter.getCurrentTarget()));
		final String[] names = (String[]) data;
		// Run the import operation asynchronously.
		// Otherwise the drag source (e.g., Windows Explorer) will be blocked
		// while the operation executes. Fixes bug 16478.
		Display.getCurrent().asyncExec(() -> {
			getShell().forceActive();
			new CopyFilesAndFoldersOperation(getShell()).copyOrLinkFiles(names, target, currentOperation);
		});
		return problems;
	}

	/**
	 * Ensures that the drop target meets certain criteria
	 */
	private IStatus validateTarget(final Object target, final TransferData transferType, final int dropOperation) {
		if (!ResourceManager.isResource(target)) { return WorkbenchNavigatorPlugin
				.createInfoStatus(WorkbenchNavigatorMessages.DropAdapter_targetMustBeResource); }
		final IResource resource = ResourceManager.getResource(target);
		if (!resource.isAccessible()) { return WorkbenchNavigatorPlugin
				.createErrorStatus(WorkbenchNavigatorMessages.DropAdapter_canNotDropIntoClosedProject); }
		final IContainer destination = getActualTarget(resource);
		if (destination.getType() == IResource.ROOT) { return WorkbenchNavigatorPlugin
				.createErrorStatus(WorkbenchNavigatorMessages.DropAdapter_resourcesCanNotBeSiblings); }
		String message = null;
		// drag within Eclipse?
		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
			final IResource[] selectedResources = getSelectedResources();

			if (selectedResources.length == 0) {
				message = WorkbenchNavigatorMessages.DropAdapter_dropOperationErrorOther;
			} else {
				CopyFilesAndFoldersOperation operation;
				if (dropOperation == DND.DROP_COPY || dropOperation == DND.DROP_LINK) {
					operation = new CopyFilesAndFoldersOperation(getShell());
					if (operation.validateDestination(destination, selectedResources) != null) {
						operation.setVirtualFolders(true);
						message = operation.validateDestination(destination, selectedResources);
					}
				} else {
					operation = new MoveFilesAndFoldersOperation(getShell());
					if (operation.validateDestination(destination, selectedResources) != null) {
						operation.setVirtualFolders(true);
						message = operation.validateDestination(destination, selectedResources);
					}
				}
			}
		} // file import?
		else if (FileTransfer.getInstance().isSupportedType(transferType)) {
			String[] sourceNames = (String[]) FileTransfer.getInstance().nativeToJava(transferType);
			if (sourceNames == null) {
				// source names will be null on Linux. Use empty names to do
				// destination validation.
				// Fixes bug 29778
				sourceNames = new String[0];
			}
			final CopyFilesAndFoldersOperation copyOperation = new CopyFilesAndFoldersOperation(getShell());
			message = copyOperation.validateImportDestination(destination, sourceNames);
		}
		if (message != null) { return WorkbenchNavigatorPlugin.createErrorStatus(message); }
		return Status.OK_STATUS;
	}

	/**
	 * Adds the given status to the list of problems. Discards OK statuses. If the status is a multi-status, only its
	 * children are added.
	 */
	private void mergeStatus(final MultiStatus status, final IStatus toMerge) {
		if (!toMerge.isOK()) {
			status.merge(toMerge);
		}
	}

	/**
	 * Opens an error dialog if necessary. Takes care of complex rules necessary for making the error dialog look nice.
	 */
	private void openError(final IStatus status) {
		if (status == null) { return; }

		final String genericTitle = WorkbenchNavigatorMessages.DropAdapter_title;
		final int codes = IStatus.ERROR | IStatus.WARNING;

		// simple case: one error, not a multistatus
		if (!status.isMultiStatus()) {
			ErrorDialog.openError(getShell(), genericTitle, null, status, codes);
			return;
		}

		// one error, single child of multistatus
		final IStatus[] children = status.getChildren();
		if (children.length == 1) {
			ErrorDialog.openError(getShell(), status.getMessage(), null, children[0], codes);
			return;
		}
		// several problems
		ErrorDialog.openError(getShell(), genericTitle, null, status, codes);
	}

}
