/*******************************************************************************************************
 *
 * EditorControl.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.List;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.resources.GamaColors;

/**
 * The Class EditorControl.
 *
 * @param <T>
 *            the generic type
 */
public class EditorControl<T extends Control> {

	/** The control. */
	final T control;

	/** The editor. */
	final AbstractEditor<?> editor;

	/**
	 * Instantiates a new editor control.
	 *
	 * @param editor
	 *            the editor
	 * @param control
	 *            the control
	 */
	EditorControl(final AbstractEditor<?> editor, final T control) {
		this.editor = editor;
		this.control = control;
		control.setLayoutData(editor.getEditorControlGridData());
		Color back = editor.getEditorControlBackground();
		Color front = editor.getEditorControlForeground();
		if (control instanceof FlatButton button) {
			button.setColor(GamaColors.get(back));
		} else {
			GamaColors.setBackAndForeground(back, front, control);
		}
	}

	/**
	 * Gets the control.
	 *
	 * @return the control
	 */
	T getControl() { return control; }

	/**
	 * Sets the layout data.
	 *
	 * @param data
	 *            the new layout data
	 */
	public void setLayoutData(final GridData data) {
		if (control.isDisposed()) return;
		control.setLayoutData(data);
	}

	/**
	 * Sets the text.
	 *
	 * @param s
	 *            the new text
	 */
	public void setText(final String s) {
		if (control.isDisposed()) return;
		if (control instanceof Text t) {
			t.setText(s);
		} else if (control instanceof Button b) {
			b.setText(s);
		} else if (control instanceof FlatButton f) {
			f.setText(s);
		} else if (control instanceof Label l) {
			l.setText(s);
		} else if (control instanceof CLabel c) { c.setText(s); }
	}

	/**
	 * Sets the active.
	 *
	 * @param b
	 *            the new active
	 */
	public void setActive(final boolean b) {
		if (control.isDisposed()) return;
		control.setEnabled(b);
	}

	/**
	 * Checks if is disposed.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is disposed
	 * @date 21 févr. 2024
	 */
	public boolean isDisposed() { return control != null && control.isDisposed(); }

	/**
	 * Display parameter value.
	 */
	public void displayParameterValue() {
		// Temporary
		if (control.isDisposed()) return;
		editor.displayParameterValue();
	}

	/**
	 * Update among values.
	 */
	public void updateAmongValues(final List possibleValues) {}

}
