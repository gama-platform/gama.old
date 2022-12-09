/*******************************************************************************************************
 *
 * OperatorExpressionDescription.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.descriptions;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.IExpression;

/**
 * The Class OperatorExpressionDescription.
 */
public class OperatorExpressionDescription extends BasicExpressionDescription {

	/** The operator. */
	String operator;
	
	/** The args. */
	IExpressionDescription[] args;

	/**
	 * Instantiates a new operator expression description.
	 *
	 * @param operator the operator
	 * @param exprs the exprs
	 */
	public OperatorExpressionDescription(final String operator, final IExpressionDescription... exprs) {
		super((EObject) null);
		for (final IExpressionDescription expr : exprs) {
			if (expr.getTarget() != null) {
				setTarget(expr.getTarget());
			} else {
				break;
			}
		}
		args = exprs;
		this.operator = operator;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		final IExpressionDescription[] exprs = new IExpressionDescription[args.length];
		for (int i = 0; i < args.length; i++) {
			exprs[i] = args[i].cleanCopy();
		}
		final OperatorExpressionDescription result = new OperatorExpressionDescription(operator, exprs);
		result.setTarget(target); // Necessary ?
		return result;
	}

	@Override
	public String toOwnString() {
		String result = operator + "(";
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				result += ",";
			}
			result += args[i].toString();
		}
		result += ")";
		return result;
	}

	@Override
	public void dispose() {
		for (final IExpressionDescription arg : args) {
			arg.dispose();
		}
		super.dispose();
	}

	@Override
	public IExpression compile(final IDescription context) {
		if (expression == null) {
			final IExpression[] exprs = new IExpression[args.length];
			for (int i = 0; i < exprs.length; i++) {
				exprs[i] = args[i].compile(context);
			}
			expression = GAML.getExpressionFactory().createOperator(operator, context, target, exprs);
			if (expression == null) {
				// If no operator has been found, we throw an exception
				context.error("Operator " + operator + " does not exist", IGamlIssue.UNKNOWN_OPERATOR,
						getTarget() == null ? context.getUnderlyingElement() : getTarget(), operator);

			}
		}
		return expression;
	}

}
