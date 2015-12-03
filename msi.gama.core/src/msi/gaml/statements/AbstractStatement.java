/*********************************************************************************************
 *
 *
 * 'AbstractStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 */

public abstract class AbstractStatement extends Symbol implements IStatement {

	public AbstractStatement(final IDescription desc) {
		super(desc);
		final String k = getLiteral(IKeyword.KEYWORD);
		final String n = getLiteral(IKeyword.NAME);
		setName(k == null ? "" : k + " " + n == null ? "" : n);
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		Object result = null;
		try {
			scope.setStatement(this);
			result = privateExecuteIn(scope);
		} catch (final GamaRuntimeException e) {
			e.addContext(this);
			GAMA.reportAndThrowIfNeeded(scope, e, true);
		}
		return result;
	}

	protected abstract Object privateExecuteIn(IScope scope) throws GamaRuntimeException;

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	@Override
	public String toString() {
		return name + description.getFacets();
	}

	@Override
	public String getTrace(final IScope scope) {
		final String n = getLiteral(IKeyword.NAME);
		final String k = getLiteral(IKeyword.KEYWORD);
		final StringBuilder sb = new StringBuilder(100);
		// if ( n != null ) {
		// sb.append('[').append(' ').append(n).append(' ').append(']').append(' ');
		// }
		sb.append(k).append(' ');
		for ( final Map.Entry<String, IExpressionDescription> e : description.getFacets().entrySet() ) {
			if ( e == null || e.getKey() == null ) {
				continue;
			}
			if ( e.getKey().equals(IKeyword.KEYWORD) ) {
				continue;
			}
			if ( e.getKey().equals(IKeyword.NAME) && n.startsWith("internal_") ) {
				continue;
			}
			IExpressionDescription ed = e.getValue();
			IExpression expr = null;
			if ( ed != null ) {
				expr = ed.getExpression();
			}
			String exprString = expr == null ? "N/A" : expr.serialize(false);
			String exprValue = expr == null ? "nil" : Cast.toGaml(expr.value(scope));
			sb.append(e.getKey()).append(": [ ").append(exprString).append(" ] ").append(exprValue).append(" ");
		}
		return sb.toString();
	}

	@Override
	public StatementDescription getDescription() {
		return (StatementDescription) super.getDescription();
	}

}
