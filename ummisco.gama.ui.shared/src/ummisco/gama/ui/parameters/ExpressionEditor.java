/*********************************************************************************************
 *
 * 'ExpressionEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import ummisco.gama.ui.interfaces.EditorListener;

public class ExpressionEditor extends GenericEditor<IExpression> {

	private String expressionText;

	ExpressionEditor(final IScope scope, final EditorsGroup parent, final String title, final IExpression value,
			final EditorListener<IExpression> whenModified, final IType<?> expectedType) {
		super(scope, parent, title, value, whenModified);
		this.expectedType = expectedType;
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		expressionText = currentValue.serialize(true);
		return super.createCustomParameterControl(comp);
	}

	@Override
	protected void displayParameterValue() {
		editorControl.setText(expressionText);
	}

	@Override
	public IExpression retrieveValueOfParameter() {
		return (IExpression) param.value(getScope());
	}

	@Override
	public boolean evaluateExpression() {
		return false;
	}

	public void setEditorTextNoPopup(final String s) {
		internalModification = true;
		editorControl.setText(s);
		internalModification = false;
	}

}
