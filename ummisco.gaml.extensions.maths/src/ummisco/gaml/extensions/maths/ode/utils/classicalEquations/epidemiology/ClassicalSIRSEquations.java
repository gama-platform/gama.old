/*******************************************************************************************************
 *
 * ClassicalSIRSEquations.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology;

import java.util.ArrayList;
import java.util.List;

import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.ListExpression;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement;

// SIRS (with demography) equation is defined by
// diff(S,t) = mu * N + omega * R + - beta * S * I / N - mu * S ;
// diff(I,t) = beta * S * I / N - gamma * I - mu * I ;
// diff(R,t) = gamma * I - omega * R - mu * R ;
//
// It is called using
// equation eqSIRS type: SIRS vars: [S,I,R,t] params: [N,beta,gamma,omega,mu]

/**
 * The Class ClassicalSIRSEquations.
 */
public class ClassicalSIRSEquations {
	
	/** The parent desc. */
	private final IDescription parentDesc;

	/**
	 * Instantiates a new classical SIRS equations.
	 *
	 * @param p the p
	 */
	public ClassicalSIRSEquations(final IDescription p) {
		parentDesc = p;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public IDescription getDescription() {
		return parentDesc;
	}

	/**
	 * Sirs.
	 *
	 * @param with_vars the with vars
	 * @param with_params the with params
	 * @return the list
	 */
	public List<SingleEquationStatement> SIRS(final ListExpression with_vars, final ListExpression with_params) {
		if (with_vars == null || with_params == null) return null;
		final ArrayList<SingleEquationStatement> cmd = new ArrayList<>();
		final IExpression[] v = with_vars.getElements();
		final IExpression[] p = with_params.getElements();

		final StatementDescription stm = new StatementDescription("=", getDescription(), false, null, null, null);

		final SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[0].literalValue() + "," + v[3].literalValue() + ")", getDescription()));
		eq1.setExpression(
				GAML.getExpressionFactory().createExpr(
						"( " + p[4].literalValue() + " * " + p[0].literalValue() + " ) + " + "( " + p[3].literalValue()
								+ " * " + v[2].literalValue() + " ) + " + "(- " + p[1].literalValue() + " * "
								+ v[0].literalValue() + " * " + v[1].literalValue() + " / " + p[0].literalValue()
								+ ") + " + "(- " + p[4].literalValue() + " * " + v[0].literalValue() + " )",
						getDescription()));
		// eq1.establishVar();
		cmd.add(eq1);

		final SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[1].literalValue() + "," + v[3].literalValue() + ")", getDescription()));
		eq2.setExpression(GAML.getExpressionFactory()
				.createExpr("(" + p[1].literalValue() + " * " + v[0].literalValue() + " * " + v[1].literalValue()
						+ " / " + p[0].literalValue() + ") + " + "( - " + p[2].literalValue() + " * "
						+ v[1].literalValue() + ") + " + "( - " + p[4].literalValue() + " * " + v[1].literalValue()
						+ ")", getDescription()));
		// eq2.establishVar();
		cmd.add(eq2);

		final SingleEquationStatement eq3 = new SingleEquationStatement(stm);
		eq3.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[2].literalValue() + "," + v[3].literalValue() + ")", getDescription()));
		eq3.setExpression(GAML.getExpressionFactory()
				.createExpr("(" + p[2].literalValue() + " * " + v[1].literalValue() + ") + " + "(- "
						+ p[3].literalValue() + " * " + v[2].literalValue() + ") + " + "(- " + p[4].literalValue()
						+ " * " + v[2].literalValue() + ")", getDescription()));
		// eq3.establishVar();
		cmd.add(eq3);
		return cmd;
	}

}
