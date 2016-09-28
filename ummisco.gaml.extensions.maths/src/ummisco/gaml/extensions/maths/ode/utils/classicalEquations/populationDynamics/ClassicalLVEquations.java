/*********************************************************************************************
 * 
 *
 * 'ClassicalLVEquations.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.classicalEquations.populationDynamics;

import java.util.ArrayList;
import java.util.List;

import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.factories.ChildrenProvider;
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

public class ClassicalLVEquations {

	private final IDescription parentDesc;

	public ClassicalLVEquations(final IDescription p) {
		parentDesc = p;
	}

	public IDescription getDescription() {
		return parentDesc;
	}

	public List<SingleEquationStatement> LV(final IExpression with_vars, final IExpression with_params) {
		if (with_vars == null || with_params == null) {
			return null;
		}
		final ArrayList<SingleEquationStatement> cmd = new ArrayList<SingleEquationStatement>();
		final IExpression[] v = ((ListExpression) with_vars).getElements();
		final IExpression[] p = ((ListExpression) with_params).getElements();

		final StatementDescription stm = new StatementDescription("=", getDescription(), new ChildrenProvider(null),
				false, null, null, null);

		final SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[0].literalValue() + "," + v[2].literalValue() + ")", getDescription()));
		eq1.setExpression(
				GAML.getExpressionFactory().createExpr(v[0].literalValue() + " * " + " ( " + p[0].literalValue() + " - "
						+ p[1].literalValue() + " * " + v[1].literalValue() + ")", getDescription()));
//		eq1.establishVar();
		cmd.add(eq1);

		final SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[1].literalValue() + "," + v[2].literalValue() + ")", getDescription()));
		eq2.setExpression(
				GAML.getExpressionFactory().createExpr("- " + v[1].literalValue() + " * " + " ( " + p[2].literalValue()
						+ " - " + p[3].literalValue() + " * " + v[0].literalValue() + ")", getDescription()));
//		eq2.establishVar();
		cmd.add(eq2);

		return cmd;
	}

}
