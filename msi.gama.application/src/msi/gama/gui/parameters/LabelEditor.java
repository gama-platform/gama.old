/*********************************************************************************************
 * 
 * 
 * 'StringEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.List;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.*;

public class LabelEditor extends AbstractEditor {

	private Text textBox;

	LabelEditor(final IParameter param) {
		super(param);
	}

	LabelEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	LabelEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	LabelEditor(final Composite parent, final String title, final Object value,
		final EditorListener<String> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);

	}

	LabelEditor(final Composite parent, final String title, final String value, final List<String> among,
		final EditorListener<String> whenModified) {
		super(new InputParameter(title, value, Types.STRING, among), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if ( internalModification ) { return; }
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
		String s = (String) currentValue;
		if ( s == null ) {
			s = "";
		}
		textBox.setText(s);
	}

	@Override
	public Control getEditorControl() {
		return textBox;
	}

	// @Override
	// public IType getExpectedType() {
	// return Types.STRING;
	// }

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
