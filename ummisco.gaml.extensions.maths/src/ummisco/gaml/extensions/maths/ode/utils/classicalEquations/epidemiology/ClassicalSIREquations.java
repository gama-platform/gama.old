package ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology;

import java.util.ArrayList;
import java.util.List;

import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement;

import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;

//SIR equation is defined by 
// diff(S,t) = (- beta * S * I / N);
// diff(I,t) = (beta * S * I / N) - (gamma * I);
// diff(R,t) = (gamma * I);
//
//It is called using
//equation eqSIR type: SIR vars: [S,I,R,t] params: [N,beta,gamma]

public class ClassicalSIREquations {
	private IDescription parentDesc;

	public ClassicalSIREquations(IDescription p) {
		parentDesc = p;
	}

	public IDescription getDescription() {
		return parentDesc;
	}

	public List<SingleEquationStatement> SIR(IExpression with_vars, IExpression with_params) {
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
				"(- " + p[1].literalValue() + " * " + v[0].literalValue()
						+ " * " + v[1].literalValue() + " / "
						+ p[0].literalValue() + ")", getDescription());
		eq1.etablishVar();
		cmd.add(eq1);

		SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.function = GAML.getExpressionFactory().createExpr(
				"diff(" + v[1].literalValue() + "," + v[3].literalValue() + ")", getDescription());
		eq2.expression = GAML.getExpressionFactory().createExpr(
				"(" + p[1].literalValue() + " * " + v[0].literalValue() + " * "
						+ v[1].literalValue() + " / " + p[0].literalValue()
						+ ") - ("+p[2].literalValue()+" * " + v[1].literalValue() + ")",
				getDescription());
		eq2.etablishVar();
		cmd.add(eq2);

		SingleEquationStatement eq3 = new SingleEquationStatement(stm);
		eq3.function = GAML.getExpressionFactory().createExpr(
				"diff(" + v[2].literalValue() + "," + v[3].literalValue() + ")", getDescription());
		eq3.expression = GAML.getExpressionFactory().createExpr(
				"("+p[2].literalValue()+" * " + v[1].literalValue() + ")", getDescription());
		eq3.etablishVar();
		cmd.add(eq3);
		return cmd;
	}

}
