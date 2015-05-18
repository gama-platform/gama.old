/*********************************************************************************************
 * 
 * 
 * 'ExpressionEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import org.eclipse.swt.widgets.*;

public class ExpressionEditor extends GenericEditor<IExpression> {

	private String expressionText;

	ExpressionEditor(final Composite parent, final String title, final IExpression value,
		final EditorListener<IExpression> whenModified, final IType expectedType) {
		super(parent, title, value, whenModified);
		this.expectedType = expectedType;
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		// if ( currentValue instanceof String ) {
		// expressionText = (String) currentValue;
		// } else if ( currentValue instanceof IExpression ) {
		expressionText = currentValue.serialize(true);
		// }
		return super.createCustomParameterControl(comp);
	}

	@Override
	protected void displayParameterValue() {
		getEditorControl().setText(expressionText);
	}

	public IExpression getParameterValue() {
		return (IExpression) param.value(null);
	}

	@Override
	protected String typeToDisplay() {
		return "expression";
	}

	@Override
	public boolean evaluateExpression() {
		return false;
	}

	public void setEditorTextNoPopup(final String s) {
		internalModification = true;
		getEditorControl().setText(s);
		internalModification = false;
	}

}
