/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation mark.melvin@onsemi.com - bug 288997 [CommonNavigator]
 * Double-clicking an adapted resource in Common Navigator does not open underlying IFile
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorMessages;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import ummisco.gama.ui.navigator.contents.WrappedFile;

/**
 * Provides the open and open with menus for IResources.
 *
 * @since 3.2
 *
 */
public class OpenActionProvider extends CommonActionProvider {

	private OpenFileAction openFileAction;
	private ICommonViewerWorkbenchSite viewSite = null;
	private boolean contribute = false;

	@Override
	public void init(final ICommonActionExtensionSite aConfig) {
		if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			viewSite = (ICommonViewerWorkbenchSite) aConfig.getViewSite();
			openFileAction = new OpenFileAction(viewSite.getPage());
			contribute = true;
		}
	}

	@Override
	public void fillContextMenu(final IMenuManager aMenu) {
		if (!contribute || getContext().getSelection().isEmpty()) { return; }

		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

		openFileAction.selectionChanged(selection);
		if (openFileAction.isEnabled()) {
			aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openFileAction);
		}
		addOpenWithMenu(aMenu);
	}

	@Override
	public void fillActionBars(final IActionBars theActionBars) {
		if (!contribute) { return; }
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		if (selection.size() == 1 && selection.getFirstElement() instanceof WrappedFile) {
			openFileAction.selectionChanged(selection);
			theActionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openFileAction);
		}

	}

	private void addOpenWithMenu(final IMenuManager aMenu) {
		final IStructuredSelection ss = (IStructuredSelection) getContext().getSelection();
		if (ss == null || ss.size() != 1) { return; }
		final Object o = ss.getFirstElement();
		// first try IResource
		IAdaptable openable = CloseResourceAction.getAdapter(o, IResource.class);
		// otherwise try ResourceMapping
		if (openable == null) {
			openable = CloseResourceAction.getAdapter(o, ResourceMapping.class);
		} else if (((IResource) openable).getType() != IResource.FILE) {
			openable = null;
		}
		if (openable != null) {
			// Create a menu flyout.
			final IMenuManager submenu =
					new MenuManager(WorkbenchNavigatorMessages.OpenActionProvider_OpenWithMenu_label,
							ICommonMenuConstants.GROUP_OPEN_WITH);
			submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));
			submenu.add(new OpenWithMenu(viewSite.getPage(), openable));
			submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));

			// Add the submenu.
			if (submenu.getItems().length > 2 && submenu.isEnabled()) {
				aMenu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
			}
		}
	}

}
