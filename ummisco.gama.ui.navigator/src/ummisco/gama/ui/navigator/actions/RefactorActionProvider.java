/*******************************************************************************************************
 *
 * RefactorActionProvider.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * @since 3.2
 *
 */
public class RefactorActionProvider extends CommonActionProvider {

	/** The rename action. */
	private RenameResourceAction renameAction;

	/** The history action. */
	private ShowLocalHistory historyAction;

	/** The compare action. */
	private CompareWithEachOtherAction compareAction;

	/** The shell. */
	private Shell shell;

	@Override
	public void init(final ICommonActionExtensionSite anActionSite) {
		shell = anActionSite.getViewSite().getShell();
		makeActions();
	}

	/**
	 * Make actions.
	 */
	protected void makeActions() {
		final IShellProvider sp = () -> shell;
		renameAction = new RenameResourceAction(sp);
		renameAction.setImageDescriptor(GamaIcon.named(IGamaIcons.FILE_RENAME).descriptor());
		renameAction.setDisabledImageDescriptor(GamaIcon.named(IGamaIcons.FILE_RENAME).disabledDescriptor());

		renameAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_RENAME);
		historyAction = new ShowLocalHistory(sp);
		historyAction.setImageDescriptor(GamaIcon.named(IGamaIcons.LOCAL_HISTORY).descriptor());
		historyAction.setDisabledImageDescriptor(GamaIcon.named(IGamaIcons.LOCAL_HISTORY).disabledDescriptor());
		compareAction = new CompareWithEachOtherAction(sp);
		compareAction.setImageDescriptor(GamaIcon.named(IGamaIcons.LAYOUT_HORIZONTAL).descriptor());
		compareAction.setDisabledImageDescriptor(GamaIcon.named(IGamaIcons.LAYOUT_HORIZONTAL).disabledDescriptor());
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		updateActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), renameAction);
	}

	/**
	 * Handle key pressed.
	 *
	 * @param event
	 *            the event
	 */
	public void handleKeyPressed(final KeyEvent event) {
		if (event.keyCode == SWT.F2 && event.stateMask == 0) {
			if (renameAction.isEnabled()) { renameAction.run(); }
			// Swallow the event.
			event.doit = false;
		}
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		final boolean anyResourceSelected = !selection.isEmpty();
		if (anyResourceSelected) {
			renameAction.selectionChanged(selection);
			historyAction.selectionChanged(selection);
			compareAction.selectionChanged(selection);
			menu.insertBefore(CopyAction.ID, renameAction);
			menu.insertAfter("additions", historyAction);
			if (selection.size() == 2) { menu.insertAfter("additions", compareAction); }
		}
	}

	@Override
	public void updateActionBars() {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		renameAction.selectionChanged(selection);
		historyAction.selectionChanged(selection);
		compareAction.selectionChanged(selection);
	}

}
