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
package ummisco.gama.ui.parameters;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class LabelEditor extends AbstractEditor<String> {

	private Text textBox;

	LabelEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	LabelEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	LabelEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<String> l) {
		super(scope, agent, param, l);
	}

	LabelEditor(final IScope scope, final Composite parent, final String title, final Object value,
		final EditorListener<String> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);

	}

	LabelEditor(final IScope scope, final Composite parent, final String title, final String value,
		final List<String> among, final EditorListener<String> whenModified) {
		super(scope, new InputParameter(title, value, Types.STRING, among), whenModified);
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
		String s = currentValue;
		if ( s == null ) {
			s = "";
		}
		textBox.setText(s);
	}

	@Override
	public Control getEditorControl() {
		return textBox;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
