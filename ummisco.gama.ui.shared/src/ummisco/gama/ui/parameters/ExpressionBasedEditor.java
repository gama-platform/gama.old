/*********************************************************************************************
 *
 * 'ExpressionBasedEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * Class ExpressionBasedEditor.
 *
 * @author drogoul
 * @since 30 nov. 2014
 *
 */
public abstract class ExpressionBasedEditor<T> extends AbstractEditor<T> {

	protected ExpressionControl expression;

	public ExpressionBasedEditor(final IScope scope, final IParameter variable) {
		super(scope, variable);
	}

	public ExpressionBasedEditor(final IScope scope, final IParameter variable, final EditorListener<T> l) {
		super(scope, variable, l);
	}

	public ExpressionBasedEditor(final IScope scope, final IAgent a, final IParameter variable) {
		super(scope, a, variable);
	}

	public ExpressionBasedEditor(final IScope scope, final IAgent a, final IParameter variable,
			final EditorListener<T> l) {
		super(scope, a, variable, l);
	}

	@Override
	public Text getEditorControl() {
		if (expression == null) { return null; }
		return expression.getControl();
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		expression = new ExpressionControl(getScope(), compo, this, getAgent(), this.getExpectedType(), SWT.BORDER,
				evaluateExpression());
		return expression.getControl();
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		expression.displayValue(currentValue);
		// if (getExpectedType() == Types.STRING)
		// expression.getControl()
		// .setText(currentValue == null ? "" : StringUtils.toJavaString(currentValue.toString()));
		// expression.getControl().setText(StringUtils.toGaml(currentValue, false));
		internalModification = false;
	}

}
