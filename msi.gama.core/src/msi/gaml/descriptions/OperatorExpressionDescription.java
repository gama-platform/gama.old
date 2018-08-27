/*******************************************************************************************************
 *
 * msi.gaml.descriptions.OperatorExpressionDescription.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.descriptions;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.util.GAML;
import msi.gaml.expressions.IExpression;
import org.eclipse.emf.ecore.EObject;

public class OperatorExpressionDescription extends BasicExpressionDescription {

	String operator;
	IExpressionDescription[] args;

	public OperatorExpressionDescription(final String operator, final IExpressionDescription ... exprs) {
		super((EObject) null);
		for ( int i = 0; i < exprs.length; i++ ) {
			if ( exprs[i].getTarget() != null ) {
				setTarget(exprs[i].getTarget());
			} else {
				break;
			}
		}
		args = exprs;
		this.operator = operator;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		IExpressionDescription[] exprs = new IExpressionDescription[args.length];
		for ( int i = 0; i < args.length; i++ ) {
			exprs[i] = args[i].cleanCopy();
		}
		OperatorExpressionDescription result = new OperatorExpressionDescription(operator, exprs);
		result.setTarget(target); // Necessary ?
		return result;
	}

	@Override
	public String toOwnString() {
		String result = operator + "(";
		for ( int i = 0; i < args.length; i++ ) {
			if ( i > 0 ) {
				result += ",";
			}
			result += args[i].toString();
		}
		result += ")";
		return result;
	}

	@Override
	public void dispose() {
		for ( IExpressionDescription arg : args ) {
			arg.dispose();
		}
		super.dispose();
	}

	@Override
	public IExpression compile(final IDescription context) {
		if ( expression == null ) {
			IExpression[] exprs = new IExpression[args.length];
			for ( int i = 0; i < exprs.length; i++ ) {
				exprs[i] = args[i].compile(context);
			}
			expression = GAML.getExpressionFactory().createOperator(operator, context, target, exprs);
			if ( expression == null ) {
				// If no operator has been found, we throw an exception
				context.error("Operator " + operator + " does not exist", IGamlIssue.UNKNOWN_UNARY, getTarget() == null
					? context.getUnderlyingElement(null) : getTarget(), operator);

			}
		}
		return expression;
	}

}
