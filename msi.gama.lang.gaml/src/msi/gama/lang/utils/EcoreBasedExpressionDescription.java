/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gama.lang.utils;

import msi.gama.lang.gaml.gaml.Expression;
import msi.gaml.descriptions.BasicExpressionDescription;

/**
 * The class EcoreBasedExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class EcoreBasedExpressionDescription extends BasicExpressionDescription {

	private final Expression ast;

	public EcoreBasedExpressionDescription(final Expression exp) {
		super(null);
		ast = exp;
	}

	@Override
	public Expression getAst() {
		return ast;
	}

	@Override
	public String toString() {
		return EGaml.toString(ast);
	}

}
