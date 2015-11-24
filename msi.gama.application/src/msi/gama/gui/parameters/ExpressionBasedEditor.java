/**
 * Created by drogoul, 30 nov. 2014
 *
 */
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/**
 * Class ExpressionBasedEditor.
 *
 * @author drogoul
 * @since 30 nov. 2014
 *
 */
public abstract class ExpressionBasedEditor<T> extends AbstractEditor<T> {

	protected ExpressionControl expression;

	public ExpressionBasedEditor(final IParameter variable) {
		super(variable);
	}

	public ExpressionBasedEditor(final IParameter variable, final EditorListener l) {
		super(variable, l);
	}

	public ExpressionBasedEditor(final IAgent a, final IParameter variable) {
		super(a, variable);
	}

	public ExpressionBasedEditor(final IAgent a, final IParameter variable, final EditorListener l) {
		super(a, variable, l);
	}

	@Override
	public Text getEditorControl() {
		if ( expression == null ) { return null; }
		return expression.getControl();
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		expression =
			new ExpressionControl(compo, this, getAgent(), this.getExpectedType(), SWT.BORDER, evaluateExpression());
		return expression.getControl();
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		expression.getControl().setText(StringUtils.toGaml(currentValue, false));
		internalModification = false;
	}

}
