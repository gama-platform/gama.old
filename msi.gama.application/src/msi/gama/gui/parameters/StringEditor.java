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
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.*;

public class StringEditor extends AbstractEditor {

	private Text textBox;
	private boolean asLabel = false;

	StringEditor(final IParameter param) {
		super(param);
	}

	StringEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	StringEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
		// this.asLabel = param.isLabel();
	}

	StringEditor(final Composite parent, final String title, final Object value,
		final EditorListener<String> whenModified, final boolean asLabel) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.asLabel = asLabel;
		this.createComposite(parent);

	}

	StringEditor(final Composite parent, final String title, final String value, final List<String> among,
		final EditorListener<String> whenModified, final boolean asLabel) {
		super(new InputParameter(title, value, Types.get(IType.STRING), among), whenModified);
		this.createComposite(parent);
		this.asLabel = asLabel;
	}

	@Override
	public void modifyText(final ModifyEvent me) {
		if ( internalModification ) { return; }
		modifyValue(StringUtils.toJavaString(textBox.getText()));
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) {
		textBox = new Text(comp, SWT.BORDER);
		textBox.addModifyListener(this);
		return textBox;
	}

	@Override
	protected void displayParameterValue() {
		String s = asLabel ? (String) currentValue : StringUtils.toGaml(currentValue);
		if ( s == null ) {
			s = "Not constant";
		}
		textBox.setText(s);
	}

	@Override
	public Control getEditorControl() {
		return textBox;
	}

	@Override
	public IType getExpectedType() {
		return Types.get(IType.STRING);
	}

}
