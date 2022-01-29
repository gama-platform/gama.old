/*******************************************************************************************************
 *
 * EditActionProvider.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.TextActionHandler;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import ummisco.gama.ui.resources.GamaIcons;

/**
 * @since 3.2
 *
 */
public class EditActionProvider extends CommonActionProvider {

	/** The clipboard. */
	private Clipboard clipboard;
	
	/** The copy action. */
	private CopyAction copyAction;
	
	/** The delete action. */
	private DeleteResourceAction deleteAction;
	
	/** The paste action. */
	private PasteAction pasteAction;
	
	/** The text action handler. */
	private TextActionHandler textActionHandler;
	
	/** The shell. */
	private Shell shell;

	/** The site. */
	private ICommonActionExtensionSite site;

	@Override
	public void init(final ICommonActionExtensionSite anActionSite) {
		site = anActionSite;
		shell = site.getViewSite().getShell();
		makeActions();

	}

	/**
	 * Make actions.
	 */
	protected void makeActions() {
		clipboard = new Clipboard(shell.getDisplay());

		pasteAction = new PasteAction(shell, clipboard);
		pasteAction.setImageDescriptor(GamaIcons.create("menu.paste2").descriptor());
		pasteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);

		copyAction = new CopyAction(shell, clipboard, pasteAction);
		copyAction.setImageDescriptor(GamaIcons.create("menu.copy2").descriptor());
		copyAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);

		final IShellProvider sp = () -> shell;

		deleteAction = new DeleteResourceAction(sp);
		deleteAction.setImageDescriptor(GamaIcons.create("menu.delete2").descriptor());
		deleteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_DELETE);

	}

	@Override
	public void dispose() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
		super.dispose();
	}

	/**
	 * Handles a key pressed event by invoking the appropriate action.
	 *
	 * @param event
	 *            The Key Event
	 */
	public void handleKeyPressed(final KeyEvent event) {
		if (event.character == SWT.DEL && event.stateMask == 0) {
			if (deleteAction.isEnabled()) {
				deleteAction.run();
			}

			// Swallow the event.
			event.doit = false;
		}
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {

		if (textActionHandler == null) {
			textActionHandler = new TextActionHandler(actionBars); // hook
																	// handlers
		}
		textActionHandler.setCopyAction(copyAction);
		textActionHandler.setPasteAction(pasteAction);
		textActionHandler.setDeleteAction(deleteAction);
		// renameAction.setTextActionHandler(textActionHandler);
		updateActionBars();

		textActionHandler.updateActionBars();
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

		copyAction.selectionChanged(selection);
		menu.appendToGroup("group.copy", copyAction);
		pasteAction.selectionChanged(selection);
		menu.appendToGroup("group.copy", pasteAction);
		deleteAction.selectionChanged(selection);
		menu.appendToGroup("group.copy", deleteAction);
	}

	@Override
	public void updateActionBars() {
		final IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

		copyAction.selectionChanged(selection);
		pasteAction.selectionChanged(selection);
		deleteAction.selectionChanged(selection);

	}
}
