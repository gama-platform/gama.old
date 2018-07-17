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

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.progress.UIJob;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
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

	ToolItem minus;

	public EditorToolbar() {}

	public EditorToolbar fill(final GamaToolbarSimple toolbar) {
		minus = toolbar.button("editor.decrease2", "", "Decrease font size", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final GamlEditor editor = getEditor();
				if (editor == null) { return; }
				editor.setFontAndCheckButtons(-1);
				minus.setEnabled(editor.getFont().getFontData()[0].height > 6);
			}
		});
		toolbar.button("editor.increase2", "", "Increase font size", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final GamlEditor editor = getEditor();
				if (editor == null) { return; }
				editor.setFontAndCheckButtons(1);
				minus.setEnabled(editor.getFont().getFontData()[0].height > 6);
			}
		});
		toolbar.sep(12);

		final ToolItem lastEdit = toolbar.button("editor.lastedit2", null, "Previous location", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					WorkbenchHelper.runCommand(IWorkbenchCommandConstants.NAVIGATE_BACKWARD_HISTORY);
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		final ToolItem nextEdit = toolbar.button("editor.nextedit2", null, "Next location", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					WorkbenchHelper.runCommand(IWorkbenchCommandConstants.NAVIGATE_FORWARD_HISTORY);
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		// Attaching listeners to the global commands in order to enable/disable
		// the toolbar items
		hookToCommands(lastEdit, nextEdit);
		toolbar.sep(10);
		toolbar.button("editor.format2", null, "Format", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final GamlEditor editor = getEditor();
				if (editor == null) { return; }
				editor.getAction("Format").run();
			}
		});

		toolbar.button("editor.comment2", null, "Toggle comment", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final GamlEditor editor = getEditor();
				if (editor == null) { return; }
				editor.getAction("ToggleComment").run();
			}
		});
		toolbar.sep(10);

		toolbar.menu("editor.outline2", null, "Show outline", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final GamlEditor editor = getEditor();
				if (editor == null) { return; }
				editor.openOutlinePopup();
			}
		});
		toolbar.sep(16);
		return this;
	}

	private void hookToCommands(final ToolItem lastEdit, final ToolItem nextEdit) {
		final UIJob job = new UIJob("Hooking to commands") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				final Command nextCommand =
						WorkbenchHelper.getCommand(IWorkbenchCommandConstants.NAVIGATE_FORWARD_HISTORY);
				nextEdit.setEnabled(nextCommand.isEnabled());
				final ICommandListener nextListener = e -> nextEdit.setEnabled(nextCommand.isEnabled());
				nextCommand.addCommandListener(nextListener);
				final Command lastCommand =
						WorkbenchHelper.getCommand(IWorkbenchCommandConstants.NAVIGATE_BACKWARD_HISTORY);
				final ICommandListener lastListener = e -> lastEdit.setEnabled(lastCommand.isEnabled());
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

}
