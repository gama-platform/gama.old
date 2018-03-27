/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation Andrey Loskutov <loskutov@gmx.de> - Bug 41431, 462760,
 * 461786
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.resources.mapping.ResourceChangeValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceAction;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.util.Util;
import org.eclipse.ui.part.FileEditorInput;

import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Standard action for closing the currently selected project(s).
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CloseResourceAction extends WorkspaceAction implements IResourceChangeListener {
	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".CloseResourceAction"; //$NON-NLS-1$

	private String[] modelProviderIds;

	/**
	 * Create the new action.
	 *
	 * @param provider
	 *            the shell provider for any dialogs
	 * @since 3.4
	 */
	public CloseResourceAction(final IShellProvider provider) {
		super(provider, IDEWorkbenchMessages.CloseResourceAction_text);
		initAction();
	}

	/**
	 * Provide text to the action.
	 *
	 * @param provider
	 *            the shell provider for any dialogs
	 * @param text
	 *            label
	 * @since 3.4
	 */
	protected CloseResourceAction(final IShellProvider provider, final String text) {
		super(provider, text);
	}

	private void initAction() {
		setId(ID);
		setToolTipText(IDEWorkbenchMessages.CloseResourceAction_toolTip);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IIDEHelpContextIds.CLOSE_RESOURCE_ACTION);
	}

	@Override
	protected String getOperationMessage() {
		return IDEWorkbenchMessages.CloseResourceAction_operationMessage;
	}

	@Override
	protected String getProblemsMessage() {
		return IDEWorkbenchMessages.CloseResourceAction_problemMessage;
	}

	@Override
	protected String getProblemsTitle() {
		return IDEWorkbenchMessages.CloseResourceAction_title;
	}

	@SuppressWarnings ("deprecation")
	@Override
	protected void invokeOperation(final IResource resource, final IProgressMonitor monitor) throws CoreException {
		((IProject) resource).close(monitor);
	}

	/**
	 * The implementation of this <code>WorkspaceAction</code> method method saves and closes the resource's dirty
	 * editors before closing it.
	 */
	@Override
	public void run() {
		// Get the items to close.
		final List<? extends IResource> projects = getSelectedResources();
		if (projects == null || projects.isEmpty()) {
			// no action needs to be taken since no projects are selected
			return;
		}

		final IResource[] projectArray = projects.toArray(new IResource[projects.size()]);

		if (!IDE.saveAllEditors(projectArray, true)) { return; }
		if (!validateClose()) { return; }

		closeMatchingEditors(projects, false);

		// be conservative and include all projects in the selection - projects
		// can change state between now and when the job starts
		ISchedulingRule rule = null;
		final IResourceRuleFactory factory = ResourcesPlugin.getWorkspace().getRuleFactory();
		for (final IResource element : projectArray) {
			final IProject project = (IProject) element;
			rule = MultiRule.combine(rule, factory.modifyRule(project));
		}
		runInBackground(rule);
	}

	@Override
	protected boolean shouldPerformResourcePruning() {
		return false;
	}

	/**
	 * The <code>CloseResourceAction</code> implementation of this <code>SelectionListenerAction</code> method ensures
	 * that this action is enabled only if one of the selections is an open project.
	 */
	@Override
	protected boolean updateSelection(final IStructuredSelection s) {
		// don't call super since we want to enable if open project is selected.
		if (!selectionIsOfType(IResource.PROJECT)) { return false; }

		final Iterator<? extends IResource> resources = getSelectedResources().iterator();
		while (resources.hasNext()) {
			final IProject currentResource = (IProject) resources.next();
			if (currentResource.isOpen()) { return true; }
		}
		return false;
	}

	/**
	 * Handles a resource changed event by updating the enablement if one of the selected projects is opened or closed.
	 */
	@Override
	public synchronized void resourceChanged(final IResourceChangeEvent event) {
		// Warning: code duplicated in OpenResourceAction
		final List<? extends IResource> sel = getSelectedResources();
		// don't bother looking at delta if selection not applicable
		if (selectionIsOfType(IResource.PROJECT)) {
			final IResourceDelta delta = event.getDelta();
			if (delta != null) {
				final IResourceDelta[] projDeltas = delta.getAffectedChildren(IResourceDelta.CHANGED);
				for (final IResourceDelta projDelta : projDeltas) {
					if ((projDelta.getFlags() & IResourceDelta.OPEN) != 0) {
						if (sel.contains(projDelta.getResource())) {
							selectionChanged(getStructuredSelection());
							return;
						}
					}
				}
			}
		}
	}

	@Override
	protected synchronized List<? extends IResource> getSelectedResources() {
		return super.getSelectedResources();
	}

	@Override
	protected synchronized List<?> getSelectedNonResources() {
		return super.getSelectedNonResources();
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

	/**
	 * Validates the operation against the model providers.
	 *
	 * @return whether the operation should proceed
	 */
	private boolean validateClose() {
		final IResourceChangeDescriptionFactory factory = ResourceChangeValidator.getValidator().createDeltaFactory();
		final List<? extends IResource> resources = getActionResources();
		for (final IResource resource : resources) {
			if (resource instanceof IProject) {
				final IProject project = (IProject) resource;
				factory.close(project);
			}
		}
		String message;
		if (resources.size() == 1) {
			message = NLS.bind(IDEWorkbenchMessages.CloseResourceAction_warningForOne, resources.get(0).getName());
		} else {
			message = IDEWorkbenchMessages.CloseResourceAction_warningForMultiple;
		}
		return IDE.promptToConfirm(WorkbenchHelper.getShell(), IDEWorkbenchMessages.CloseResourceAction_confirm,
				message, factory.getDelta(), getModelProviderIds(), false /* no need to syncExec */);
	}

	/**
	 * Tries to find opened editors matching given resource roots. The editors will be closed without confirmation and
	 * only if the editor resource does not exists anymore.
	 *
	 * @param resourceRoots
	 *            non null array with deleted resource tree roots
	 * @param deletedOnly
	 *            true to close only editors on resources which do not exist
	 */
	static void closeMatchingEditors(final List<? extends IResource> resourceRoots, final boolean deletedOnly) {
		if (resourceRoots.isEmpty()) { return; }
		final Runnable runnable = () -> SafeRunner.run(new SafeRunnable(IDEWorkbenchMessages.ErrorOnCloseEditors) {
			@Override
			public void run() {
				final IWorkbenchWindow w = getActiveWindow();
				if (w != null) {
					final List<IEditorReference> toClose = getMatchingEditors(resourceRoots, w, deletedOnly);
					if (toClose.isEmpty()) { return; }
					closeEditors(toClose, w);
				}
			}
		});
		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), runnable);
	}

	private static IWorkbenchWindow getActiveWindow() {
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (w == null) {
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0) {
				w = windows[0];
			}
		}
		return w;
	}

	private static List<IEditorReference> getMatchingEditors(final List<? extends IResource> resourceRoots,
			final IWorkbenchWindow w, final boolean deletedOnly) {
		final List<IEditorReference> toClose = new ArrayList<>();
		final IEditorReference[] editors = getEditors(w);
		for (final IEditorReference ref : editors) {
			final IResource resource = getAdapter(ref);
			// only collect editors for non existing resources
			if (resource != null && belongsTo(resourceRoots, resource)) {
				if (deletedOnly && resource.exists()) {
					continue;
				}
				toClose.add(ref);
			}
		}
		return toClose;
	}

	private static IEditorReference[] getEditors(final IWorkbenchWindow w) {
		if (w != null) {
			final IWorkbenchPage page = w.getActivePage();
			if (page != null) { return page.getEditorReferences(); }
		}
		return new IEditorReference[0];
	}

	private static IResource getAdapter(final IEditorReference ref) {
		IEditorInput input;
		try {
			input = ref.getEditorInput();
		} catch (final PartInitException e) {
			// ignore if factory can't restore input, see bug 461786
			return null;
		}
		if (input instanceof FileEditorInput) {
			final FileEditorInput fi = (FileEditorInput) input;
			final IFile file = fi.getFile();
			if (file != null) { return file; }
		}
		// here we can only guess how the input might be related to a resource
		final IFile adapter = getAdapter(input, IFile.class);
		if (adapter != null) { return adapter; }
		return getAdapter(input, IResource.class);
	}
	
	public final static <T> T getAdapter(Object sourceObject, Class<T> adapterType) {
		Assert.isNotNull(adapterType);
		if (sourceObject == null) {
			return null;
		}
		if (adapterType.isInstance(sourceObject)) {
			return adapterType.cast(sourceObject);
		}

		if (sourceObject instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) sourceObject;

			T result = adaptable.getAdapter(adapterType);
			if (result != null) {
				// Sanity-check
				Assert.isTrue(adapterType.isInstance(result));
				return result;
			}
		}

		if (!(sourceObject instanceof PlatformObject)) {
			T result = Platform.getAdapterManager().getAdapter(sourceObject, adapterType);
			if (result != null) {
				return result;
			}
		}

		return null;
	}
	
	private static boolean belongsTo(final List<? extends IResource> roots, final IResource leaf) {
		for (final IResource resource : roots) {
			if (resource.contains(leaf)) { return true; }
		}
		return false;
	}

	private static void closeEditors(final List<IEditorReference> toClose, final IWorkbenchWindow w) {
		final IWorkbenchPage page = w.getActivePage();
		if (page == null) { return; }
		page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), false);
	}
}
