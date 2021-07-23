package ummisco.gama.ui.parameters;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ummisco.gama.ui.controls.FlatButton;

public class EditorControl<T extends Control> {

	final T control;
	final AbstractEditor<?> editor;

	EditorControl(final AbstractEditor<?> editor, final T control) {
		this.editor = editor;
		this.control = control;
		control.setLayoutData(editor.getParameterGridData());
		control.setBackground(editor.parent.getBackground());
	}

	T getControl() {
		return control;
	}

	public void setLayoutData(final GridData data) {
		if (control.isDisposed()) return;
		control.setLayoutData(data);
	}

	public void setBackground(final Color color) {
		if (control.isDisposed()) return;
		control.setBackground(color);
	}

	public void setForeground(final Color color) {
		if (control.isDisposed()) return;
		control.setForeground(color);
	}

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

	public void setActive(final boolean b) {
		if (control.isDisposed()) return;
		control.setEnabled(b);
	}

	public void displayParameterValue() {
		// Temporary
		if (control.isDisposed()) return;
		editor.displayParameterValue();
	}

}
