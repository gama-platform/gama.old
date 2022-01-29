/*******************************************************************************************************
 *
 * ClassicalLVEquations.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.classicalEquations.populationDynamics;

import java.util.ArrayList;
import java.util.List;

import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.ListExpression;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement;

// SI equation is defined by
// diff(x,t) = x * (alpha - beta * y) ;
// diff(y,t) = - y * (delta - gamma * x) ;
//
// It is called using
// equation eqLV type: LV vars: [x,y,t] params: [alpha,beta,delta,gamma] {}

// alpha, prey reproduction rate without predators
// beta, preys mortality rate due to predators
// delta, predators mortality rate without preys
// gamma, predators reproduction rate depending on the number of preys eaten

/**
 * The Class ClassicalLVEquations.
 */
public class ClassicalLVEquations {

	/** The parent desc. */
	private final IDescription parentDesc;

	/**
	 * Instantiates a new classical LV equations.
	 *
	 * @param p the p
	 */
	public ClassicalLVEquations(final IDescription p) {
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
	 * Lv.
	 *
	 * @param with_vars the with vars
	 * @param with_params the with params
	 * @return the list
	 */
	public List<SingleEquationStatement> LV(final ListExpression with_vars, final ListExpression with_params) {
		if (with_vars == null || with_params == null) return null;
		final ArrayList<SingleEquationStatement> cmd = new ArrayList<>();
		final IExpression[] v = with_vars.getElements();
		final IExpression[] p = with_params.getElements();

		final StatementDescription stm = new StatementDescription("=", getDescription(), false, null, null, null);

		final SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[0].literalValue() + "," + v[2].literalValue() + ")", getDescription()));
		eq1.setExpression(
				GAML.getExpressionFactory().createExpr(v[0].literalValue() + " * " + " ( " + p[0].literalValue() + " - "
						+ p[1].literalValue() + " * " + v[1].literalValue() + ")", getDescription()));
		// eq1.establishVar();
		cmd.add(eq1);

		final SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[1].literalValue() + "," + v[2].literalValue() + ")", getDescription()));
		eq2.setExpression(
				GAML.getExpressionFactory().createExpr("- " + v[1].literalValue() + " * " + " ( " + p[2].literalValue()
						+ " - " + p[3].literalValue() + " * " + v[0].literalValue() + ")", getDescription()));
		// eq2.establishVar();
		cmd.add(eq2);

		return cmd;
	}

}
