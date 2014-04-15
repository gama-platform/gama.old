/*********************************************************************************************
 * 
 *
 * 'GenericEditor.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.types.*;
import org.eclipse.swt.widgets.*;

public class GenericEditor extends AbstractEditor {

	ExpressionControl control;
	IType expectedType;

	GenericEditor(final IParameter param) {
		super(param);
		expectedType = param.getType();
	}

	GenericEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	GenericEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
		expectedType = param.getType();
	}

	GenericEditor(final Composite parent, final String title, final Object value,
		final EditorListener whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		expectedType = value == null ? Types.NO_TYPE : Types.get(value.getClass());
		this.createComposite(parent);
	}

	@Override
	protected Control createCustomParameterControl(final Composite comp) {
		control = new ExpressionControl(comp, this);
		return control.getControl();

	}

	@Override
	protected void displayParameterValue() {
		control.getControl().setText(StringUtils.toGaml(currentValue));
	}

	@Override
	public Control getEditorControl() {
		return control.getControl();
	}

	@Override
	public IType getExpectedType() {
		return expectedType;
	}

}
