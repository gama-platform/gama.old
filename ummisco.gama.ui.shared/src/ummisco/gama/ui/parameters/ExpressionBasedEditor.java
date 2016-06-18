/**
 * Created by drogoul, 30 nov. 2014
 *
 */
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

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

	public ExpressionBasedEditor(final IScope scope, final IParameter variable, final EditorListener l) {
		super(scope, variable, l);
	}

	public ExpressionBasedEditor(final IScope scope, final IAgent a, final IParameter variable) {
		super(scope, a, variable);
	}

	public ExpressionBasedEditor(final IScope scope, final IAgent a, final IParameter variable,
		final EditorListener l) {
		super(scope, a, variable, l);
	}

	@Override
	public Text getEditorControl() {
		if ( expression == null ) { return null; }
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
		expression.getControl().setText(StringUtils.toGaml(currentValue, false));
		internalModification = false;
	}

}
