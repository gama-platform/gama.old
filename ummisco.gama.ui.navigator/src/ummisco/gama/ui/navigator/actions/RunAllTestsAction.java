package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.actions.WorkspaceAction;

import ummisco.gama.ui.commands.TestsRunner;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.TestModelsFolder;

public class RunAllTestsAction extends WorkspaceAction {

	protected RunAllTestsAction(final IShellProvider provider, final String text) {
		super(provider, text);
	}

	@Override
	protected String getOperationMessage() {
		return "Running all tests";
	}

	@Override
	public boolean updateSelection(final IStructuredSelection event) {
		return event.getFirstElement() instanceof TestModelsFolder;
	}

	@Override
	public void run() {
		ResourceManager.setLastTestResults(TestsRunner.start());
	}

}
