package msi.gaml.compilation;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription.FacetVisitor;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class SymbolTracer {

	public String trace(final IScope scope, final ISymbol statement) {

		final String k = statement.getKeyword(); // getFacet(IKeyword.KEYWORD).literalValue();
		final StringBuilder sb = new StringBuilder(100);
		sb.append(k).append(' ');
		if (statement.getDescription() != null)
			statement.getDescription().visitFacets(new FacetVisitor() {

				@Override
				public boolean visit(final String name, final IExpressionDescription ed) {
					if (name.equals(IKeyword.NAME)) {
						final String n = statement.getFacet(IKeyword.NAME).literalValue();
						if (n.startsWith("internal_"))
							return true;
					}
					IExpression expr = null;
					if (ed != null) {
						expr = ed.getExpression();
					}
					final String exprString = expr == null ? "N/A" : expr.serialize(false);
					final String exprValue = expr == null ? "nil" : Cast.toGaml(expr.value(scope));
					sb.append(name).append(": [ ").append(exprString).append(" ] ").append(exprValue).append(" ");

					return true;
				}

			});

		return sb.toString();

	}

}
