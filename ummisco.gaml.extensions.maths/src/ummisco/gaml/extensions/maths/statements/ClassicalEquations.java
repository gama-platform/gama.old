package ummisco.gaml.extensions.maths.statements;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;

public class ClassicalEquations {
	private IDescription parentDesc;

	public ClassicalEquations(IDescription p) {
		parentDesc = p;
	}

	public IDescription getDescription() {
		return parentDesc;
	}

	public List SIR(IExpression with_vars, IExpression with_params) {
		if (with_vars == null || with_params == null) {
			return null;
		}
		ArrayList cmd = new ArrayList();
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
