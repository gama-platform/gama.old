package ummisco.gama.ui.parameters;

import static ummisco.gama.ui.resources.GamaIcons.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.resources.IGamaIcons;

public class EditorToolbar {

	final AbstractEditor editor;
	protected final ToolItem[] items;

	EditorToolbar(final AbstractEditor editor) {
		this.editor = editor;
		this.items = new ToolItem[9];
	}

	void build(final Composite composite) {
		final var t = new ToolBar(composite, SWT.NONE);
		final var d = new GridData(SWT.END, SWT.FILL, false, false);
		t.setLayoutData(d);
		if (editor.isEditable) {
			final var codes = editor.getToolItems();
			for (final int i : codes) {
				ToolItem item = null;
				switch (i) {
					case IParameterEditor.REVERT:
						item = createItem(t, "Revert to original value", create("small.revert").image());
						break;
					case IParameterEditor.PLUS:
						item = createItem(t, "Increment " + (editor.stepValue == null ? "" : " by " + editor.stepValue),
								create(IGamaIcons.SMALL_PLUS).image());
						break;
					case IParameterEditor.MINUS:
						item = createItem(t, "Decrement " + (editor.stepValue == null ? "" : " by " + editor.stepValue),
								create(IGamaIcons.SMALL_MINUS).image());
						break;
					case IParameterEditor.EDIT:
						item = createItem(t, "Edit the parameter", create("small.edit").image());
						break;
					case IParameterEditor.INSPECT:
						item = createItem(t, "Inspect the agent", create("small.inspect").image());
						break;
					case IParameterEditor.BROWSE:
						item = createItem(t, "Browse the list of agents", create("small.browse").image());
						break;
					case IParameterEditor.CHANGE:
						item = createItem(t, "Choose another agent", create("small.change").image());
						break;
					case IParameterEditor.DEFINE:
						item = createItem(t, "Set the parameter to undefined", create("small.undefine").image());
						break;
					case IParameterEditor.VALUE:
						item = createItem(t, "Value of the parameter", null);
				}
				if (item != null) {
					items[i] = item;
					item.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							execute(i, e.detail);
						}

					});

				}
			}
			Color color = editor.parent.getBackground();
			t.setBackground(color);
			for (final Control c : t.getChildren()) {
				c.setBackground(color);
			}
		}

	}

	private ToolItem createItem(final ToolBar t, final String string, final Image image) {
		final var i = new ToolItem(t, SWT.FLAT | SWT.PUSH);
		i.setToolTipText(string);
		i.setImage(image);
		return i;
	}

	private void execute(final int code, final int detail) {
		switch (code) {
			case IParameterEditor.REVERT:
				editor.modifyAndDisplayValue(editor.applyRevert());
				break;
			case IParameterEditor.PLUS:
				editor.modifyAndDisplayValue(editor.applyPlus());
				break;
			case IParameterEditor.MINUS:
				editor.modifyAndDisplayValue(editor.applyMinus());
				break;
			case IParameterEditor.EDIT:
				editor.applyEdit();
				break;
			case IParameterEditor.INSPECT:
				editor.applyInspect();
				break;
			case IParameterEditor.BROWSE:
				editor.applyBrowse();
				break;
			case IParameterEditor.CHANGE:
				if (detail != SWT.ARROW) return;
				editor.applyChange();
				break;
			case IParameterEditor.DEFINE:
				editor.applyDefine();
				break;
		}
	}

	protected void update() {
		final var revert = items[IParameterEditor.REVERT];
		if (revert == null || revert.isDisposed()) return;
		revert.setEnabled(editor.isValueModified());
	}

	public void updateValue(final String s) {
		final var value = items[IParameterEditor.VALUE];
		if (value == null || value.isDisposed()) return;
		value.setText(s);
	}

	public void setActive(final Boolean active) {
		for (final ToolItem t : items) {
			if (t == null) { continue; }
			t.setEnabled(active);
		}
	}

	public ToolItem getItem(final int item) {
		return items[item];
	}

}
