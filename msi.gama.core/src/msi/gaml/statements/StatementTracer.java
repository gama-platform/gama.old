package msi.gaml.statements;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class StatementTracer {

	public String trace(final IScope scope, final IStatement statement) {

		final String n = statement.getFacet(IKeyword.NAME).literalValue();
		final String k = statement.getFacet(IKeyword.KEYWORD).literalValue();
		final StringBuilder sb = new StringBuilder(100);
		sb.append(k).append(' ');
		for (final Map.Entry<String, IExpressionDescription> e : statement.getDescription().getFacets().entrySet()) {
			if (e == null || e.getKey() == null) {
				continue;
			}
			if (e.getKey().equals(IKeyword.KEYWORD)) {
				continue;
			}
			if (e.getKey().equals(IKeyword.NAME) && n.startsWith("internal_")) {
				continue;
			}
			final IExpressionDescription ed = e.getValue();
			IExpression expr = null;
			if (ed != null) {
				expr = ed.getExpression();
			}
			final String exprString = expr == null ? "N/A" : expr.serialize(false);
			final String exprValue = expr == null ? "nil" : Cast.toGaml(expr.value(scope));
			sb.append(e.getKey()).append(": [ ").append(exprString).append(" ] ").append(exprValue).append(" ");
		}
		return sb.toString();

	}

}
