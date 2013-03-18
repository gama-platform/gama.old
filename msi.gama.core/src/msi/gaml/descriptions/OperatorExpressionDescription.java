package msi.gaml.descriptions;

import msi.gama.runtime.GAMA;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import org.eclipse.emf.ecore.EObject;

public class OperatorExpressionDescription extends BasicExpressionDescription {

	String operator;
	IExpressionDescription[] args;

	public OperatorExpressionDescription(String operator, IExpressionDescription ... exprs) {
		super((EObject) null);
		for ( int i = 0; i < exprs.length; i++ ) {
			if ( exprs[i].getTarget() != null ) {
				setTarget(exprs[i].getTarget());
			}
		}
		args = exprs;
		this.operator = operator;
	}

	@Override
	public String toString() {
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
	public IExpression compile(IDescription context) {
		if ( expression == null ) {
			IExpression[] exprs = new IExpression[args.length];
			for ( int i = 0; i < exprs.length; i++ ) {
				exprs[i] = args[i].compile(context);
			}
			expression = GAMA.getExpressionFactory().createOperator(operator, context, exprs);
		}
		return expression;
	}

}
