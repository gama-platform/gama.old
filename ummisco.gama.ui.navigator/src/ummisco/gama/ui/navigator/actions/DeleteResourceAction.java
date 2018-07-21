/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation Benjamin Muskalla <b.muskalla@gmx.net> - Fix for bug
 * 172574 - [IDE] DeleteProjectDialog inconsequent selection behavior Andrey Loskutov <loskutov@gmx.de> - Bug 41431,
 * 462760
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.actions.LTKLauncher;
import org.eclipse.ui.part.FileEditorInput;

import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.navigator.contents.LinkedFile;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Standard action for deleting the currently selected resources.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DeleteResourceAction extends SelectionListenerAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".DeleteResourceAction";//$NON-NLS-1$

	private String[] modelProviderIds;

	/**
	 * Creates a new delete resource action.
	 *
	 * @param provider
	 *            the shell provider to use. Must not be <code>null</code>.
	 * @since 3.4
	 */
	public DeleteResourceAction(final IShellProvider provider) {
		super(IDEWorkbenchMessages.DeleteResourceAction_text);
		Assert.isNotNull(provider);
		initAction();
		// setShellProvider(provider);
	}

	/**
	 * Action initialization.
	 */
	private void initAction() {
		setToolTipText(IDEWorkbenchMessages.DeleteResourceAction_toolTip);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IIDEHelpContextIds.DELETE_RESOURCE_ACTION);
		setId(ID);
	}

	// private void setShellProvider(final IShellProvider provider) {
	// // shellProvider = provider;
	// }

	/**
	 * Returns whether delete can be performed on the current selection.
	 *
	 * @param resources
	 *            the selected resources
	 * @return <code>true</code> if the resources can be deleted, and <code>false</code> if the selection contains
	 *         non-resources or phantom resources
	 */
	private boolean canDelete(final List<? extends IResource> resources) {
		// allow only projects or only non-projects to be selected;
		// note that the selection may contain multiple types of resource
		if (!(containsOnlyProjects(resources) || containsOnlyNonProjects(resources))) { return false; }

		if (resources.isEmpty()) { return false; }
		// Return true if everything in the selection exists.
		for (int i = 0; i < resources.size(); i++) {
			final IResource resource = resources.get(i);
			if (resource.isPhantom()) { return false; }
		}
		return true;
	}

	/**
	 * Returns whether the selection contains linked resources.
	 *
	 * @param resources
	 *            the selected resources
	 * @return <code>true</code> if the resources contain linked resources, and <code>false</code> otherwise
	 */
	private boolean containsLinkedResource(final List<? extends IResource> resources) {
		for (int i = 0; i < resources.size(); i++) {
			final IResource resource = resources.get(i);
			if (resource.isLinked()) { return true; }
		}
		return false;
	}

	/**
	 * Returns whether the selection contains only non-projects.
	 *
	 * @param resources
	 *            the selected resources
	 * @return <code>true</code> if the resources contains only non-projects, and <code>false</code> otherwise
	 */
	private boolean containsOnlyNonProjects(final List<? extends IResource> resources) {
		final int types = getSelectedResourceTypes(resources);
		// check for empty selection
		if (types == 0) { return false; }
		// note that the selection may contain multiple types of resource
		return (types & IResource.PROJECT) == 0;
	}

	/**
	 * Returns whether the selection contains only projects.
	 *
	 * @param resources
	 *            the selected resources
	 * @return <code>true</code> if the resources contains only projects, and <code>false</code> otherwise
	 */
	private boolean containsOnlyProjects(final List<? extends IResource> resources) {
		final int types = getSelectedResourceTypes(resources);
		// note that the selection may contain multiple types of resource
		return types == IResource.PROJECT;
	}

	/**
	 * Returns a bit-mask containing the types of resources in the selection.
	 *
	 * @param resources
	 *            the selected resources
	 */
	private int getSelectedResourceTypes(final List<? extends IResource> resources) {
		int types = 0;
		for (int i = 0; i < resources.size(); i++) {
			types |= resources.get(i).getType();
		}
		return types;
	}

	/**
	 * Returns the elements in the current selection that are <code>IResource</code>s.
	 *
	 * @return list of resource elements (element type: <code>IResource</code>)
	 */
	@Override
	protected List<? extends IResource> getSelectedResources() {
		final IStructuredSelection selection = getStructuredSelection();
		if (selection.toList().stream()
				.anyMatch(each -> (each instanceof LinkedFile))) { return Collections.EMPTY_LIST; }
		final List<IResource> list = new ArrayList<>();
		for (final IResource r : super.getSelectedResources()) {
			list.add(r);
			if (ResourceManager.isFile(r)) {
				list.addAll(FileMetaDataProvider.getInstance().getSupportFilesOf((IFile) r));
			}
		}
		return list;
	}

	@Override
	public void run() {
		final List<? extends IResource> resources = getSelectedResources();
		if (LTKLauncher.openDeleteWizard(new StructuredSelection(resources))) {
			closeMatchingEditors(resources, true);
			return;
		}
	}

	/**
	 * The <code>DeleteResourceAction</code> implementation of this <code>SelectionListenerAction</code> method disables
	 * the action if the selection contains phantom resources or non-resources
	 */
	@Override
	protected boolean updateSelection(final IStructuredSelection selection) {
		return canDelete(getSelectedResources());
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
				final IWorkbenchWindow w = WorkbenchHelper.getWindow();
				if (w != null) {
					final List<IEditorReference> toClose = getMatchingEditors(resourceRoots, w, deletedOnly);
					if (toClose.isEmpty()) { return; }
					closeEditors(toClose, w);
				}
			}
		});
		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), runnable);
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
		final IFile adapter = CloseResourceAction.getAdapter(input, IFile.class);
		if (adapter != null) { return adapter; }
		return CloseResourceAction.getAdapter(input, IResource.class);
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
