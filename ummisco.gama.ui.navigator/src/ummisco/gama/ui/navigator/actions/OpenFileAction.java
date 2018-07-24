/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenSystemEditorAction;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.part.FileEditorInput;

import ummisco.gama.ui.navigator.contents.NavigatorRoot;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Standard action for opening an editor on the currently selected file resource(s).
 * <p>
 * Note that there is a different action for opening closed projects: <code>OpenResourceAction</code>.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class OpenFileAction extends OpenSystemEditorAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".OpenFileAction";//$NON-NLS-1$

	/**
	 * The editor to open.
	 */
	private final IEditorDescriptor editorDescriptor;

	/**
	 * Creates a new action that will open editors on the then-selected file resources. Equivalent to
	 * <code>OpenFileAction(page,null)</code>.
	 *
	 * @param page
	 *            the workbench page in which to open the editor
	 */
	public OpenFileAction(final IWorkbenchPage page) {
		this(page, null);
	}

	/**
	 * Creates a new action that will open instances of the specified editor on the then-selected file resources.
	 *
	 * @param page
	 *            the workbench page in which to open the editor
	 * @param descriptor
	 *            the editor descriptor, or <code>null</code> if unspecified
	 */
	public OpenFileAction(final IWorkbenchPage page, final IEditorDescriptor descriptor) {
		super(page);
		setText(descriptor == null ? IDEWorkbenchMessages.OpenFileAction_text : descriptor.getLabel());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IIDEHelpContextIds.OPEN_FILE_ACTION);
		setToolTipText(IDEWorkbenchMessages.OpenFileAction_toolTip);
		setId(ID);
		this.editorDescriptor = descriptor;
	}

	/**
	 * Ensures that the contents of the given file resource are local.
	 *
	 * @param file
	 *            the file resource
	 * @return <code>true</code> if the file is local, and <code>false</code> if it could not be made local for some
	 *         reason
	 */
	boolean ensureFileLocal(final IFile file) {
		return NavigatorRoot.getInstance().getManager().validateLocation(file);
	}

	@Override
	public void run() {
		final Iterator itr = getSelectedResources().iterator();
		while (itr.hasNext()) {
			final IResource resource = (IResource) itr.next();
			if (resource instanceof IFile) {
				openFile((IFile) resource);
			}
		}
	}

	@Override
	protected List<? extends IResource> getSelectedResources() {
		final List<? extends IResource> resources = new ArrayList<>(super.getSelectedResources());
		resources.removeIf((r) -> r instanceof IFile && !NavigatorRoot.getInstance().getManager().validateLocation((IFile) r));
		return resources;
	}

	/**
	 * Opens an editor on the given file resource.
	 *
	 * @param file
	 *            the file resource
	 */
	void openFile(final IFile file) {
		try {
			final boolean activate = OpenStrategy.activateOnOpen();
			if (editorDescriptor == null) {
				IDE.openEditor(WorkbenchHelper.getPage(), file, activate);
			} else {
				if (ensureFileLocal(file)) {
					WorkbenchHelper.getPage().openEditor(new FileEditorInput(file), editorDescriptor.getId(), activate);
				}
			}
		} catch (final PartInitException e) {
			DialogUtil.openError(WorkbenchHelper.getPage().getWorkbenchWindow().getShell(),
					IDEWorkbenchMessages.OpenFileAction_openFileShellTitle, e.getMessage(), e);
		}
	}

}
