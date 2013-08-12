package ummisco.gaml.extensions.maths.utils.classicalEquations;

import java.util.ArrayList;
import java.util.List;

import ummisco.gaml.extensions.maths.statements.SingleEquationStatement;

import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;

//SIRS (with demography) equation is defined by 
// diff(S,t) = mu * N + omega * R + - beta * S * I / N - mu * S ;
// diff(I,t) = beta * S * I / N - gamma * I - mu * I ;
// diff(R,t) = gamma * I - omega * R - mu * R ;
//
//It is called using
//equation eqSIRS type: SIRS with_vars: [S,I,R,t] with_params: [N,beta,gamma,omega,mu]

public class ClassicalSIRSEquations {
	private IDescription parentDesc;

	public ClassicalSIRSEquations(IDescription p) {
		parentDesc = p;
	}

	public IDescription getDescription() {
		return parentDesc;
	}

	public List<SingleEquationStatement> SIRS(IExpression with_vars, IExpression with_params) {
		if (with_vars == null || with_params == null) {
			return null;
		}
		ArrayList<SingleEquationStatement> cmd = new ArrayList<SingleEquationStatement>();
		IExpression[] v = ((ListExpression) with_vars).getElements();
		IExpression[] p = ((ListExpression) with_params).getElements();

		StatementDescription stm = new StatementDescription("=",
				getDescription(), new ChildrenProvider(null), false, false,
				null, new Facets("keyword", "="));
	
		SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.function = GAML.getExpressionFactory().createExpr(
				"diff(" + v[0].literalValue() + "," + v[3].literalValue() + ")", getDescription());
		eq1.expression = GAML.getExpressionFactory().createExpr(
				"( " + p[4].literalValue() + " * " + p[0].literalValue() + " ) + " +
				"( " + p[3].literalValue() + " * " + v[2].literalValue() + " ) + " + 
				"(- " + p[1].literalValue() + " * " + v[0].literalValue()
						+ " * " + v[1].literalValue() + " / " + p[0].literalValue() + ") + " +
				"(- " + p[4].literalValue() + " * " + v[0].literalValue() + " )", 
				getDescription());
		eq1.etablishVar();
		cmd.add(eq1);

		SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.function = GAML.getExpressionFactory().createExpr(
				"diff(" + v[1].literalValue() + "," + v[3].literalValue() + ")", getDescription());
		eq2.expression = GAML.getExpressionFactory().createExpr(
				"(" + p[1].literalValue() + " * " + v[0].literalValue() + " * "
						+ v[1].literalValue() + " / " + p[0].literalValue() + ") + " + 
				"( - " + p[2].literalValue() + " * " + v[1].literalValue() + ")" +
				"( - " + p[4].literalValue() + " * " + v[1].literalValue() + ")",
				getDescription());
		eq2.etablishVar();
		cmd.add(eq2);

		SingleEquationStatement eq3 = new SingleEquationStatement(stm);
		eq3.function = GAML.getExpressionFactory().createExpr(
				"diff(" + v[2].literalValue() + "," + v[3].literalValue() + ")", getDescription());
		eq3.expression = GAML.getExpressionFactory().createExpr(
				"("   + p[2].literalValue() + " * " + v[1].literalValue() + ")" +
				"(- " + p[3].literalValue() + " * " + v[2].literalValue() + ")" +
				"(- " + p[4].literalValue() + " * " + v[2].literalValue() + " )", 
		getDescription());
		eq3.etablishVar();
		cmd.add(eq3);
		return cmd;
	}

}
