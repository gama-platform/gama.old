/*******************************************************************************************************
 *
 * EditorToolbar.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import static ummisco.gama.ui.interfaces.IParameterEditor.BROWSE;
import static ummisco.gama.ui.interfaces.IParameterEditor.CHANGE;
import static ummisco.gama.ui.interfaces.IParameterEditor.DEFINE;
import static ummisco.gama.ui.interfaces.IParameterEditor.EDIT;
import static ummisco.gama.ui.interfaces.IParameterEditor.INSPECT;
import static ummisco.gama.ui.interfaces.IParameterEditor.MINUS;
import static ummisco.gama.ui.interfaces.IParameterEditor.PLUS;
import static ummisco.gama.ui.interfaces.IParameterEditor.REVERT;
import static ummisco.gama.ui.interfaces.IParameterEditor.VALUE;
import static ummisco.gama.ui.resources.GamaIcons.create;
import static ummisco.gama.ui.resources.IGamaIcons.SMALL_MINUS;
import static ummisco.gama.ui.resources.IGamaIcons.SMALL_PLUS;
import static ummisco.gama.ui.views.toolbar.GamaCommand.build;

import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import msi.gama.kernel.experiment.IParameter;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.views.toolbar.GamaCommand;

/**
 * The Class EditorToolbar.
 */
public class EditorToolbar {

	/**
	 * The Class Item.
	 */
	static class Item {

		/** The label. */
		final Label label;

		/** The listener. */
		final MouseListener listener;

		/** The command. */
		final GamaCommand command;

		/**
		 * Instantiates a new item.
		 *
		 * @param parent
		 *            the parent
		 * @param c
		 *            the c
		 * @param l
		 *            the l
		 */
		Item(final Composite parent, final GamaCommand c, final MouseListener l) {
			command = c;
			listener = l;
			label = new Label(parent, SWT.NONE);
			if (c.getText() != null) { label.setText(c.getText()); }
			label.setToolTipText(c.getTooltip());
			enable(true);
		}

		/**
		 * Enable.
		 *
		 * @param enable
		 *            the enable
		 */
		void enable(final boolean enable) {

			if (command.getImage() != null) {
				label.setImage(enable ? create(command.getImage()).image() : create(command.getImage()).disabled());
			}
			label.removeMouseListener(listener);
			if (enable) { label.addMouseListener(listener); }
		}

		/**
		 * Checks if is disposed.
		 *
		 * @return true, if is disposed
		 */
		public boolean isDisposed() { return label != null && label.isDisposed(); }
	}

	/** The editor. */
	final AbstractEditor editor;

	/** The active. */
	boolean active;

	/** The Constant commands. */
	protected static final GamaCommand[] commands = new GamaCommand[9];

	/** The items. */
	protected final Item[] items = new Item[9];

	static {
		commands[REVERT] = build("small.revert", null, "Revert to original value", null);
		commands[PLUS] = build(SMALL_PLUS, null, "Increment the value", null);
		commands[MINUS] = build(SMALL_MINUS, null, "Decrement the value ", null);
		commands[EDIT] = build("small.edit", null, "Edit the parameter", null);
		commands[INSPECT] = build("small.inspect", null, "Inspect the agent", null);
		commands[BROWSE] = build("small.browse", null, "Browse the list of agents", null);
		commands[CHANGE] = build("small.change", null, "Choose another agent", null);
		commands[DEFINE] = build("small.undefine", null, "Set the parameter to undefined", null);
		commands[VALUE] = build(null, "", "Value of the parameter", null);
	}

	/**
	 * Instantiates a new editor toolbar.
	 *
	 * @param editor
	 *            the editor
	 * @param composite
	 *            the composite
	 */
	EditorToolbar(final AbstractEditor editor, final Composite composite) {
		this.editor = editor;
		final Composite t = new Composite(composite, SWT.NONE);
		final GridData d = new GridData(SWT.END, SWT.CENTER, false, false);
		t.setLayoutData(d);
		final RowLayout id = RowLayoutFactory.fillDefaults().center(true).spacing(2).margins(0, 0).type(SWT.HORIZONTAL)
				.fill(true).pack(true).extendedMargins(0, 0, 0, 0).justify(false).wrap(false).create();
		final RowData gd = RowDataFactory.swtDefaults().create();
		t.setLayout(id);
		IParameter p = editor.getParam();
		if (p != null && p.isEditable()) {
			for (final int i : editor.getToolItems()) {
				MouseListener listener = new MouseAdapter() {

					@Override
					public void mouseDown(final MouseEvent e) {
						execute(i, 0);
					}
				};
				items[i] = new Item(t, commands[i], listener);
				items[i].label.setLayoutData(RowDataFactory.copyData(gd));
			}
			Color color = editor.parent.getBackground();
			t.setBackground(color);
			for (final Control c : t.getChildren()) { c.setBackground(color); }
		}
		Color color = editor.parent.getBackground();
		t.setBackground(color);
		for (final Control c : t.getChildren()) { c.setBackground(color); }
	}

	/**
	 * Execute.
	 *
	 * @param code
	 *            the code
	 * @param detail
	 *            the detail
	 */
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
				// if (detail != SWT.ARROW) return;
				editor.applyChange();
				break;
			case IParameterEditor.DEFINE:
				editor.applyDefine();
				break;
		}
	}

	/**
	 * Enable.
	 *
	 * @param i
	 *            the i
	 * @param enable
	 *            the enable
	 */
	public void enable(final int i, final boolean enable) {
		if (!active && enable) return;
		final var c = items[i];
		if (c == null) return;
		c.enable(enable);
	}

	/**
	 * Update.
	 */
	protected void update() {
		final var c = items[IParameterEditor.REVERT];
		if (c != null && !c.isDisposed()) { c.enable(editor.isValueModified()); }
	}

	/**
	 * Update value.
	 *
	 * @param s
	 *            the s
	 */
	public void updateValue(final String s) {
		final var c = items[IParameterEditor.VALUE];
		if (c != null && !c.isDisposed()) { c.label.setText(s); }
	}

	/**
	 * Sets the active.
	 *
	 * @param active
	 *            the new active
	 */
	public void setActive(final Boolean active) {
		this.active = active;
		for (final Item t : items) {
			if (t == null) { continue; }
			t.enable(active);
		}
		if (active) { update(); }
	}

	/**
	 * Gets the item.
	 *
	 * @param item
	 *            the item
	 * @return the item
	 */
	public Label getItem(final int item) {
		final var c = items[item];
		if (c == null) return null;
		return c.label;
	}

}
