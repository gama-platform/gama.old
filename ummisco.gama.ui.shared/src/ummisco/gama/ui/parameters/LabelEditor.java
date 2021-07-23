/*********************************************************************************************
 *
 * 'LabelEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import msi.gama.kernel.experiment.InputParameter;
import msi.gama.runtime.IScope;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * Edits arbitrary strings, not only GAML ones or well-formed expressions
 *
 * @author A. Drogoul
 *
 */
public class LabelEditor extends AbstractEditor<String> {

	private Text textBox;

	LabelEditor(final IScope scope, final EditorsGroup parent, final String title, final Object value,
			final EditorListener<String> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createControls(parent);

	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if (internalModification) return;
		modifyValue(textBox.getText());
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) {
		textBox = new Text(comp, SWT.BORDER);
		textBox.addModifyListener(this);
		return textBox;
	}

	@Override
	protected void displayParameterValue() {
		String s = currentValue;
		if (s == null) { s = ""; }
		textBox.setText(s);
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
