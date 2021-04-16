/*********************************************************************************************
 *
 * 'ClassicalSISEquations.java, in plugin ummisco.gaml.extensions.maths, is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology;

import java.util.ArrayList;
import java.util.List;

import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement;

// SIS equation is defined by
// diff(S,t) = -beta * S * I / N + gamma * I;
// diff(I,t) = beta * S * I / N - gamma * I;
//
// It is called using
// equation eqSIS type: SIS vars: [S,I,t] params: [N,beta,gamma]

public class ClassicalSISEquations {
	private final IDescription parentDesc;

	public ClassicalSISEquations(final IDescription p) {
		parentDesc = p;
	}

	public IDescription getDescription() {
		return parentDesc;
	}

	public List<SingleEquationStatement> SIS(final ListExpression with_vars, final ListExpression with_params) {
		if (with_vars == null || with_params == null) return null;
		final ArrayList<SingleEquationStatement> cmd = new ArrayList<>();
		final IExpression[] v = with_vars.getElements();
		final IExpression[] p = with_params.getElements();

		final StatementDescription stm = new StatementDescription("=", getDescription(), false, null, null, null);

		final SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[0].literalValue() + "," + v[2].literalValue() + ")", getDescription()));
		eq1.setExpression(GAML.getExpressionFactory().createExpr(
				"(- " + p[1].literalValue() + " * " + v[0].literalValue() + " * " + v[1].literalValue() + " / "
						+ p[0].literalValue() + ") + (" + p[2].literalValue() + " * " + v[1].literalValue() + ")",
				getDescription()));
		// eq1.establishVar();
		cmd.add(eq1);

		final SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[1].literalValue() + "," + v[2].literalValue() + ")", getDescription()));
		eq2.setExpression(GAML.getExpressionFactory().createExpr(
				"( " + p[1].literalValue() + " * " + v[0].literalValue() + " * " + v[1].literalValue() + " / "
						+ p[0].literalValue() + ") + ( - " + p[2].literalValue() + " * " + v[1].literalValue() + ")",
				getDescription()));
		// eq2.establishVar();
		cmd.add(eq2);

		return cmd;
	}

}
