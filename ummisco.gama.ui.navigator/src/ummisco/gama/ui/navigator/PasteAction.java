/*********************************************************************************************
 *
 * 'PasteAction.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import ummisco.gama.ui.commands.PasteIntoModelsHandler;

public class PasteAction extends BaseSelectionListenerAction {

	public static final PasteAction INSTANCE = new PasteAction();

	private PasteAction() {
		super("Paste in User Models");
	}

	@Override
	protected boolean updateSelection(final IStructuredSelection selection) {
		if (selection != null && selection.getFirstElement() instanceof UserProjectsFolder)
			return true;
		return super.updateSelection(selection);
	}

	@Override
	public void run() {
		PasteIntoModelsHandler.handlePaste();
	}

}
