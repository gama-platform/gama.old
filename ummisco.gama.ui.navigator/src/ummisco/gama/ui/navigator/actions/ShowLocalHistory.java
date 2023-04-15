/*******************************************************************************************************
 *
 * ShowLocalHistory.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.internal.AddFromHistoryAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.team.internal.ui.TeamUIMessages;
import org.eclipse.team.internal.ui.TeamUIPlugin;
import org.eclipse.team.internal.ui.history.LocalHistoryPage;
import org.eclipse.team.internal.ui.history.LocalHistoryPageSource;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.history.IHistoryPage;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceAction;

import ummisco.gama.ui.dialogs.Messages;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class ShowLocalHistory.
 */
public class ShowLocalHistory extends WorkspaceAction {

	/** The is file. */
	boolean isFile;

	/** The project action. */
	AddFromHistoryAction projectAction = new AddFromHistoryAction();

	/**
	 * Instantiates a new show local history.
	 *
	 * @param provider
	 *            the provider
	 */
	protected ShowLocalHistory(final IShellProvider provider) {
		super(provider, "Local history...");
	}

	/** The selection. */
	private IStructuredSelection fSelection;

	@Override
	public void run() {
		if (!isFile) {
			projectAction.run(null);
			return;
		}
		final IFileState states[] = getLocalHistory();
		if (states == null || states.length == 0) return;
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(monitor -> {

				final IResource resource = this.getSelectedResources().get(0);
				final Runnable r = () -> {
					final IHistoryView view = TeamUI.showHistoryFor(TeamUIPlugin.getActivePage(), resource,
							LocalHistoryPageSource.getInstance());
					final IHistoryPage page = view.getHistoryPage();
					if (page instanceof LocalHistoryPage) {
						final LocalHistoryPage historyPage = (LocalHistoryPage) page;
						historyPage.setClickAction(isCompare());
					}
				};
				WorkbenchHelper.asyncRun(r);
			});
		} catch (final InvocationTargetException exception) {
			Messages.error(TeamUIMessages.ShowLocalHistory_1);
		} catch (final InterruptedException exception) {}
	}

	@Override
	protected boolean updateSelection(final IStructuredSelection sel) {
		fSelection = sel;
		projectAction.selectionChanged(null, sel);
		isFile = selectionIsOfType(IResource.FILE);
		if (!isFile) return sel.size() == 1 && selectionIsOfType(IResource.FOLDER | IResource.PROJECT);
		return true;
	}

	/**
	 * Checks if is compare.
	 *
	 * @return true, if is compare
	 */
	protected boolean isCompare() { return false; }

	/**
	 * Gets the selection.
	 *
	 * @return the selection
	 */
	public IStructuredSelection getSelection() { return fSelection; }

	/**
	 * Gets the local history.
	 *
	 * @return the local history
	 */
	protected IFileState[] getLocalHistory() {
		final IFile file = ResourceManager.getFile(getSelection().getFirstElement());
		IFileState states[] = null;
		try {
			if (file != null) { states = file.getHistory(null); }
		} catch (final CoreException ex) {
			Messages.error(ex.getMessage());
			return null;
		}

		if (states == null || states.length <= 0) { Messages.tell(TeamUIMessages.ShowLocalHistory_0); }
		return states;
	}

	/**
	 * Gets the prompt title.
	 *
	 * @return the prompt title
	 */
	protected String getPromptTitle() { return TeamUIMessages.ShowLocalHistory_2; }

	@Override
	protected String getOperationMessage() { return TeamUIMessages.ShowLocalHistory_2; }

}
