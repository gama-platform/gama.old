/*******************************************************************************************************
 *
 * EditorControl.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ummisco.gama.ui.controls.FlatButton;

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
		control.setLayoutData(editor.getParameterGridData());
		control.setBackground(editor.parent.getBackground());
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

	// public void setForeground(final Color color) {
	// if (control.isDisposed()) return;
	// control.setForeground(color);
	// }

	/**
	 * Sets the text.
	 *
	 * @param s
	 *            the new text
	 */
	public void setText(final String s) {
		if (control.isDisposed()) return;
		if (control instanceof Text) {
			((Text) control).setText(s);
		} else if (control instanceof Button) {
			((Button) control).setText(s);
		} else if (control instanceof FlatButton) {
			((FlatButton) control).setText(s);
		} else if (control instanceof Label) {
			((Label) control).setText(s);
		} else if (control instanceof CLabel) { ((CLabel) control).setText(s); }
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
	 * Display parameter value.
	 */
	public void displayParameterValue() {
		// Temporary
		if (control.isDisposed()) return;
		editor.displayParameterValue();
	}

}
