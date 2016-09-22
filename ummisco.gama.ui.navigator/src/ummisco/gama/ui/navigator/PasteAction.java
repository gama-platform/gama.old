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
