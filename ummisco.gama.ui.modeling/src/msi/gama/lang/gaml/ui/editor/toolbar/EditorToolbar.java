/*******************************************************************************************************
 *
 * EditorToolbar.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import static org.eclipse.ui.IWorkbenchCommandConstants.NAVIGATE_BACKWARD_HISTORY;
import static org.eclipse.ui.IWorkbenchCommandConstants.NAVIGATE_FORWARD_HISTORY;
import static ummisco.gama.ui.utils.WorkbenchHelper.executeCommand;
import static ummisco.gama.ui.utils.WorkbenchHelper.getCommand;

import java.util.function.Consumer;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbarSimple;

/**
 * The class EditorNavigationControls.
 *
 * @author drogoul
 * @since 11 nov. 2016
 *
 */
public class EditorToolbar {

	/** The previous. */
	ToolItem next, previous;

	/** The find. */
	EditorSearchControls find;

	/** The editor. */
	final GamlEditor editor;

	/** The searching. */
	volatile boolean searching;

	/**
	 * Selected.
	 *
	 * @param event
	 *            the event
	 * @return the selection listener
	 */
	static SelectionListener selected(final Consumer<SelectionEvent> event) {
		return widgetSelectedAdapter(event);
	}

	/** The global previous. */
	final SelectionListener globalPrevious = selected(e -> executeCommand(NAVIGATE_BACKWARD_HISTORY));

	/** The global next. */
	final SelectionListener globalNext = selected(e -> executeCommand(NAVIGATE_FORWARD_HISTORY));

	/** The search previous. */
	final SelectionListener searchPrevious = selected(e -> find.findPrevious());

	/** The search next. */
	final SelectionListener searchNext = selected(e -> find.findNext());

	/**
	 * Instantiates a new editor toolbar.
	 *
	 * @param editor
	 *            the editor
	 */
	public EditorToolbar(final GamlEditor editor) {
		this.editor = editor;
	}

	/**
	 * Fill.
	 *
	 * @param toolbar
	 *            the toolbar
	 * @return the editor search controls
	 */
	public EditorSearchControls fill(final GamaToolbarSimple toolbar) {

		previous = toolbar.button("editor.lastedit2", null, "Previous edit location", globalPrevious);
		next = toolbar.button("editor.nextedit2", null, "Next edit location", globalNext);
		find = new EditorSearchControls(editor).fill(toolbar);
		toolbar.menu("editor.outline2", null, "Show outline", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// final GamlEditor editor = getEditor();
				if (editor == null) return;
				editor.openOutlinePopup();
			}
		});

		// Attaching listeners to the global commands in order to enable/disable the
		// toolbar items
		hookToCommands(previous, next);

		// Attaching a focus listener to the search control to
		hookToSearch(previous, next);

		return find;
	}

	/**
	 * Hook to search.
	 *
	 * @param lastEdit
	 *            the last edit
	 * @param nextEdit
	 *            the next edit
	 */
	private void hookToSearch(final ToolItem lastEdit, final ToolItem nextEdit) {
		find.getFindControl().addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				searching = true;
				previous.removeSelectionListener(globalPrevious);
				previous.setToolTipText("Search previous occurence");
				next.removeSelectionListener(globalNext);
				next.setToolTipText("Search next occurence " + GamaKeyBindings.format(SWT.MOD1, 'G'));
				previous.addSelectionListener(searchPrevious);
				next.addSelectionListener(searchNext);
				previous.setEnabled(true);
				next.setEnabled(true);
			}

			@Override
			public void focusLost(final FocusEvent e) {
				searching = false;
				previous.removeSelectionListener(searchPrevious);
				previous.setToolTipText("Previous edit location");
				next.removeSelectionListener(searchNext);
				next.setToolTipText("Next edit location");
				previous.addSelectionListener(globalPrevious);
				next.addSelectionListener(globalNext);
				previous.setEnabled(getCommand(NAVIGATE_BACKWARD_HISTORY).isEnabled());
				next.setEnabled(getCommand(NAVIGATE_FORWARD_HISTORY).isEnabled());
			}
		});
	}

	/**
	 * Hook to commands.
	 *
	 * @param lastEdit
	 *            the last edit
	 * @param nextEdit
	 *            the next edit
	 */
	private void hookToCommands(final ToolItem lastEdit, final ToolItem nextEdit) {
		WorkbenchHelper.runInUI("Hooking to commands", 0, m -> {
			final Command nextCommand = getCommand(NAVIGATE_FORWARD_HISTORY);
			nextEdit.setEnabled(nextCommand.isEnabled());
			final ICommandListener nextListener = e -> nextEdit.setEnabled(searching || nextCommand.isEnabled());
			nextCommand.addCommandListener(nextListener);
			final Command lastCommand = getCommand(NAVIGATE_BACKWARD_HISTORY);
			final ICommandListener lastListener = e -> lastEdit.setEnabled(searching || lastCommand.isEnabled());
			lastEdit.setEnabled(lastCommand.isEnabled());
			lastCommand.addCommandListener(lastListener);
			// Attaching dispose listeners to the toolItems so that they remove the
			// command listeners properly
			lastEdit.addDisposeListener(e -> lastCommand.removeCommandListener(lastListener));
			nextEdit.addDisposeListener(e -> nextCommand.removeCommandListener(nextListener));
		});

	}

}
