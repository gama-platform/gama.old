/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/

package ummisco.gama.ui.navigator.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import ummisco.gama.ui.resources.GamaIcons;

/**
 * @since 3.2
 *
 */
public class OpenCloseActionProvider extends CommonActionProvider {
	private OpenResourceAction openProjectAction;
	private CloseResourceAction closeProjectAction;
	private Shell shell;

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		super.init(aSite);
		shell = aSite.getViewSite().getShell();
		makeActions();
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		actionBars.setGlobalActionHandler(IDEActionFactory.OPEN_PROJECT.getId(), openProjectAction);
		actionBars.setGlobalActionHandler(IDEActionFactory.CLOSE_PROJECT.getId(), closeProjectAction);
		updateActionBars();
	}

	/**
	 * Adds the build, open project, close project and refresh resource actions to the context menu.
	 * <p>
	 * The following conditions apply: build-only projects selected, auto build disabled, at least one builder present
	 * open project-only projects selected, at least one closed project close project-only projects selected, at least
	 * one open project refresh-no closed project selected
	 * </p>
	 * <p>
	 * Both the open project and close project action may be on the menu at the same time.
	 * </p>
	 * <p>
	 * No disabled action should be on the context menu.
	 * </p>
	 *
	 * @param menu
	 *            context menu to add actions to
	 */
	@Override
	public void fillContextMenu(final IMenuManager menu) {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		boolean isProjectSelection = true;
		boolean hasOpenProjects = false;
		boolean hasClosedProjects = false;
		final Iterator<Object> resources = selection.iterator();

		while (resources.hasNext() && (!hasOpenProjects || !hasClosedProjects || isProjectSelection)) {
			final Object next = resources.next();
			IProject project = null;

			if (next instanceof IProject) {
				project = (IProject) next;
			} else if (next instanceof IAdaptable) {
				project = ((IAdaptable) next).getAdapter(IProject.class);
			}

			if (project == null) {
				isProjectSelection = false;
				continue;
			}
			if (project.isOpen()) {
				hasOpenProjects = true;
			} else {
				hasClosedProjects = true;
			}
		}

		if (isProjectSelection) {
			if (hasClosedProjects) {
				openProjectAction.selectionChanged(selection);
				menu.appendToGroup("group.refresh", openProjectAction);
			}
			if (hasOpenProjects) {
				closeProjectAction.selectionChanged(selection);
				menu.appendToGroup("group.refresh", closeProjectAction);
			}
		}
	}

	protected void makeActions() {
		final IShellProvider sp = () -> shell;

		openProjectAction = new OpenResourceAction(sp);
		openProjectAction.setImageDescriptor(GamaIcons.create("navigator/project.open2").descriptor());

		closeProjectAction = new CloseResourceAction(sp);
		closeProjectAction.setImageDescriptor(GamaIcons.create("navigator/project.close2").descriptor());

	}

	@Override
	public void updateActionBars() {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		openProjectAction.selectionChanged(selection);
		closeProjectAction.selectionChanged(selection);
	}

}
