/*******************************************************************************************************
 *
 * RunAllTestsAction.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.actions.WorkspaceAction;

import ummisco.gama.ui.commands.TestsRunner;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.TestModelsFolder;

/**
 * The Class RunAllTestsAction.
 */
public class RunAllTestsAction extends WorkspaceAction {

	/**
	 * Instantiates a new run all tests action.
	 *
	 * @param provider the provider
	 * @param text the text
	 */
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
		TestsRunner.start();
		ResourceManager.finishTests();
	}

}
