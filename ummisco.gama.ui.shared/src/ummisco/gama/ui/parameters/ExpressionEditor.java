/*******************************************************************************************************
 *
 * ExpressionEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.kernel.experiment.InputParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class ExpressionEditor.
 */
public class ExpressionEditor extends ExpressionBasedEditor<IExpression> {

	/** The expression text. */
	private String expressionText;

	/**
	 * Instantiates a new expression editor.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param param
	 *            the param
	 * @param whenModified
	 *            the when modified
	 * @param expectedType
	 *            the expected type
	 */
	public ExpressionEditor(final IAgent agent, final InputParameter param,
			final EditorListener<IExpression> whenModified, final IType expectedType) {
		super(agent, param, whenModified);
		this.expectedType = expectedType;
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		expressionText = currentValue == null ? "" : currentValue.serialize(true);
		return super.createCustomParameterControl(comp);
	}

	@Override
	protected void displayParameterValue() {
		editorControl.setText(expressionText);
	}

	@Override
	public IExpression retrieveValueOfParameter(final boolean retrieveVarValue) {
		return (IExpression) param.value(getScope());
	}

	@Override
	public boolean evaluateExpression() {
		return false;
	}

	/**
	 * Sets the editor text no popup.
	 *
	 * @param s
	 *            the new editor text no popup
	 */
	public void setEditorTextNoPopup(final String s) {
		internalModification = true;
		editorControl.setText(s);
		internalModification = false;
	}

}
