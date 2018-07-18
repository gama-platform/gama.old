/*********************************************************************************************
 *
 * 'EditorNavigationControls.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import static org.eclipse.ui.IWorkbenchCommandConstants.NAVIGATE_BACKWARD_HISTORY;
import static org.eclipse.ui.IWorkbenchCommandConstants.NAVIGATE_FORWARD_HISTORY;
import static ummisco.gama.ui.utils.WorkbenchHelper.executeCommand;
import static ummisco.gama.ui.utils.WorkbenchHelper.getCommand;

import java.util.function.Consumer;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.progress.UIJob;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.navigator.NavigatorSearchControl;
import ummisco.gama.ui.utils.PlatformHelper;
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

	ToolItem next, previous;
	EditorSearchControls find;
	final GamlEditor editor;
	volatile boolean searching;

	static SelectionListener selected(final Consumer<SelectionEvent> event) {
		return widgetSelectedAdapter(event);
	}

	private final SelectionListener globalPrevious = selected(e -> executeCommand(NAVIGATE_BACKWARD_HISTORY));
	private final SelectionListener globalNext = selected(e -> executeCommand(NAVIGATE_FORWARD_HISTORY));
	private final SelectionListener searchPrevious = selected(e -> find.findPrevious());
	private final SelectionListener searchNext = selected(e -> find.findNext());

	public EditorToolbar(final GamlEditor editor) {
		this.editor = editor;
	}

	public EditorSearchControls fill(final GamaToolbarSimple toolbar) {

		previous = toolbar.button("editor.lastedit2", null, "Previous edit location", globalPrevious);
		next = toolbar.button("editor.nextedit2", null, "Next edit location", globalNext);
		find = new EditorSearchControls(editor).fill(toolbar);
		toolbar.menu("editor.outline2", null, "Show outline", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// final GamlEditor editor = getEditor();
				if (editor == null) {
					return;
				}
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

	private void hookToCommands(final ToolItem lastEdit, final ToolItem nextEdit) {
		final UIJob job = new UIJob("Hooking to commands") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
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
				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

	GamlEditor getEditor() {
		return (GamlEditor) WorkbenchHelper.getActiveEditor();
	}

	// minus = toolbar.button("editor.decrease2", "", "Decrease font size", new
	// SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// final GamlEditor editor = getEditor();
	// if (editor == null) { return; }
	// editor.setFontAndCheckButtons(-1);
	// minus.setEnabled(editor.getFont().getFontData()[0].height > 6);
	// }
	// });
	// toolbar.button("editor.increase2", "", "Increase font size", new
	// SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// final GamlEditor editor = getEditor();
	// if (editor == null) { return; }
	// editor.setFontAndCheckButtons(1);
	// minus.setEnabled(editor.getFont().getFontData()[0].height > 6);
	// }
	// });
	// toolbar.sep(12);
	// toolbar.sep(10);
	// toolbar.button("editor.format2", null, "Format", new SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// final GamlEditor editor = getEditor();
	// if (editor == null) { return; }
	// editor.getAction("Format").run();
	// }
	// });
	//
	// toolbar.button("editor.comment2", null, "Toggle comment", new
	// SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// final GamlEditor editor = getEditor();
	// if (editor == null) { return; }
	// editor.getAction("ToggleComment").run();
	// }
	// });

}
