/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation Andrey Loskutov <loskutov@gmx.de> - generified
 * interface, bug 462760
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceAction;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;

import ummisco.gama.ui.interfaces.IRefreshHandler;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Standard action for refreshing the workspace from the local file system for the selected resources and all of their
 * descendants.
 * <p>
 * This class may be instantiated; it may also subclass to extend:
 * <ul>
 * <li>getSelectedResources - A list containing 0 or more resources to be refreshed</li>
 * <li>updateSelection - controls when this action is enabled</li>
 * <li>refreshResource - can be extended to refresh model objects related to the resource</li>
 * <ul>
 * </p>
 */
public class RefreshAction extends WorkspaceAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".RefreshAction";//$NON-NLS-1$

	public List<? extends IResource> resources;

	/**
	 * Creates a new action.
	 *
	 * @param provider
	 *            the IShellProvider for any dialogs.
	 * @since 3.4
	 */
	public RefreshAction(final IShellProvider provider) {
		super(provider, IDEWorkbenchMessages.RefreshAction_text);
		initAction();
	}

	/**
	 * Initializes for the constructor.
	 */
	private void initAction() {
		setToolTipText(IDEWorkbenchMessages.RefreshAction_toolTip);
		setId(ID);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IIDEHelpContextIds.REFRESH_ACTION);
	}

	/**
	 * Checks whether the given project's location has been deleted. If so, prompts the user with whether to delete the
	 * project or not.
	 */
	void checkLocationDeleted(final IProject project) throws CoreException {
		if (!project.exists()) { return; }
		final IFileInfo location = IDEResourceInfoUtils.getFileInfo(project.getLocationURI());
		if (!location.exists()) {
			final String message = NLS.bind(IDEWorkbenchMessages.RefreshAction_locationDeletedMessage,
					project.getName(), location.toString());

			final MessageDialog dialog = new MessageDialog(WorkbenchHelper.getShell(),
					IDEWorkbenchMessages.RefreshAction_dialogTitle, null, message, MessageDialog.QUESTION,
					new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0) {
				@Override
				protected int getShellStyle() {
					return super.getShellStyle() | SWT.SHEET;
				}
			};
			WorkbenchHelper.run(() -> dialog.open());

			// Do the deletion back in the operation thread
			if (dialog.getReturnCode() == 0) { // yes was chosen
				project.delete(true, true, null);
			}
		}
	}

	@Override
	protected String getOperationMessage() {
		return IDEWorkbenchMessages.RefreshAction_progressMessage;
	}

	@Override
	protected String getProblemsMessage() {
		return IDEWorkbenchMessages.RefreshAction_problemMessage;
	}

	@Override
	protected String getProblemsTitle() {
		return IDEWorkbenchMessages.RefreshAction_problemTitle;
	}

	/**
	 * Returns a list containing the workspace root if the selection would otherwise be empty.
	 */
	@Override
	protected List<? extends IResource> getSelectedResources() {
		final List<IResource> resources1 = new ArrayList<>();
		for (final IResource r : super.getSelectedResources()) {
			if (r.isAccessible()) {
				resources1.add(r);
			}
		}
		if (resources1.isEmpty()) {
			resources1.add(ResourcesPlugin.getWorkspace().getRoot());
		}
		return resources1;
	}

	/**
	 * The <code>RefreshAction</code> implementation of this <code>SelectionListenerAction</code> method ensures that
	 * this action is enabled if the selection is empty, but is disabled if any of the selected elements are not
	 * resources.
	 */
	@Override
	protected boolean updateSelection(final IStructuredSelection s) {
		resources = getSelectedResources();
		return true;
	}

	/**
	 * Handle the key release.
	 *
	 * @param event
	 *            the event
	 */
	public void handleKeyReleased(final KeyEvent event) {
		if (event.keyCode == SWT.F5 && event.stateMask == 0) {
			refreshAll();
		}
	}

	/**
	 * Refreshes the entire workspace.
	 */
	final public void refreshAll() {
		final IStructuredSelection currentSelection = getStructuredSelection();
		selectionChanged(StructuredSelection.EMPTY);
		run();
		selectionChanged(currentSelection);
	}

	/**
	 * Returns whether the given resource is a descendent of any of the resources in the given list.
	 *
	 * @param resources1
	 *            the list of resources (element type: <code>IResource</code>)
	 * @param child
	 *            the resource to check
	 * @return <code>true</code> if <code>child</code> is a descendent of any of the elements of <code>resources</code>
	 */
	// private boolean isDescendent2(final List<IResource> resources1, final IResource child) {
	// final IResource parent = child.getParent();
	// return parent != null && (resources1.contains(parent) || isDescendent2(resources1, parent));
	// }

	/**
	 * Performs pruning on the given list of resources, as described in <code>shouldPerformResourcePruning</code>.
	 *
	 * @param resourceCollection
	 *            the list of resources (element type: <code>IResource</code>)
	 * @return the list of resources (element type: <code>IResource</code>) after pruning.
	 * @see #shouldPerformResourcePruning
	 */
	// private List<IResource> pruneResources2(final List<? extends IResource> resourceCollection) {
	// final List<IResource> prunedList = new ArrayList<>(resourceCollection);
	// final Iterator<IResource> elementsEnum = prunedList.iterator();
	// while (elementsEnum.hasNext()) {
	// final IResource currentResource = elementsEnum.next();
	// if (isDescendent2(prunedList, currentResource)) {
	// elementsEnum.remove(); // Removes currentResource
	// }
	// }
	// return prunedList;
	// }

	@Override
	final protected IRunnableWithProgress createOperation(final IStatus[] errorStatus) {
		// final ISchedulingRule rule = null;
		// final IResourceRuleFactory factory = ResourcesPlugin.getWorkspace().getRuleFactory();

		// List<? extends IResource> actionResources = new ArrayList<>(resources);
		// if (shouldPerformResourcePruning()) {
		// actionResources = pruneResources2(actionResources);
		// }
		// final List<? extends IResource> resources = actionResources;

		// final Iterator<? extends IResource> res = resources.iterator();
		// while (res.hasNext()) {
		// rule = MultiRule.combine(rule, factory.refreshRule(res.next()));
		// }
		return new WorkspaceModifyOperation() {
			@Override
			public void execute(final IProgressMonitor monitor) {
				final Iterator<? extends IResource> resourcesEnum = resources.iterator();
				try {
					while (resourcesEnum.hasNext()) {
						try {
							final IResource resource = resourcesEnum.next();
							refreshResource(resource, null);
						} catch (final CoreException e) {}
						if (monitor.isCanceled()) { throw new OperationCanceledException(); }
					}
				} finally {
					monitor.done();
				}
			}
		};
	}

	/**
	 * Refresh the resource (with a check for deleted projects).
	 * <p>
	 * This method may be extended to refresh model objects related to the resource.
	 * </p>
	 *
	 * @param resource
	 *            the resource to refresh. Must not be <code>null</code>.
	 * @param monitor
	 *            progress monitor
	 * @throws CoreException
	 *             if things go wrong
	 * @since 3.4
	 */
	protected void refreshResource(final IResource resource, final IProgressMonitor monitor) throws CoreException {
		if (resource.getType() == IResource.PROJECT) {
			checkLocationDeleted((IProject) resource);
		} else if (resource.getType() == IResource.ROOT) {
			final IProject[] projects = ((IWorkspaceRoot) resource).getProjects();
			for (final IProject project : projects) {
				checkLocationDeleted(project);
			}
		}
		resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	@Override
	public void run() {
		final WorkspaceJob job = new WorkspaceJob("Refreshing the GAMA Workspace") {

			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				final IRefreshHandler refresh = WorkbenchHelper.getService(IRefreshHandler.class);
				if (refresh != null) {
					refresh.completeRefresh(resources);
				}
				return Status.OK_STATUS;
			};
		};
		job.setUser(true);
		job.schedule();
	}

}
