/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package ummisco.gama.ui.navigator.actions;

import org.eclipse.compare.internal.CompareAction;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.actions.WorkspaceAction;

import ummisco.gama.ui.utils.WorkbenchHelper;

public class CompareWithEachOtherAction extends WorkspaceAction {

	boolean isEnabled;
	protected CompareAction action;

	protected CompareWithEachOtherAction(final IShellProvider provider) {
		super(provider, "Compare");
		action = new CompareAction();
		action.setActivePart(this, WorkbenchHelper.getActivePart());
	}

	private IStructuredSelection fSelection;

	@Override
	public void run() {
		action.run(fSelection);
	}

	@Override
	protected boolean updateSelection(final IStructuredSelection sel) {
		fSelection = sel;
		isEnabled = sel.size() == 2 && selectionIsOfType(IResource.FILE);
		action.selectionChanged(this, sel);
		return isEnabled;
	}

	public IStructuredSelection getSelection() {
		return fSelection;
	}

	@Override
	protected String getOperationMessage() {
		return "Compare";
	}

}
