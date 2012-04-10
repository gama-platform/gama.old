/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import msi.gaml.expressions.JavaConstExpression;

/**
 * The class LabelExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class LabelExpressionDescription extends BasicExpressionDescription {

	public LabelExpressionDescription(final String label) {
		super(new JavaConstExpression(label));
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#getAst()
	 */
	@Override
	public Object getAst() {
		return null;
	}

	/**
	 * @see msi.gaml.descriptions.IExpressionDescription#compileAsLabel()
	 */
	@Override
	public IExpressionDescription compileAsLabel() {
		return this;
	}

	@Override
	public String toString() {
		return expression.literalValue();
	}

}
