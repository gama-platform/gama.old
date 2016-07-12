/*********************************************************************************************
 *
 *
 * 'AbstractExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Abstract class that defines the structure of all expression classes.
 *
 * @author drogoul
 */

public abstract class AbstractExpression implements IExpression {

	protected IType type = null;
	protected String name = null;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String s) {
		name = s;
	}

	@Override
	public IType getType() {
		return type == null ? Types.NO_TYPE : type;
	}

	@Override
	public String literalValue() {
		return getName();
	}

	@Override
	public void dispose() {
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	protected final void parenthesize(final StringBuilder sb, final IExpression... exp) {
		if (exp.length == 1 && !exp[0].shouldBeParenthesized()) {
			sb.append(exp[0].serialize(false));
		} else {
			surround(sb, '(', ')', exp);
		}
	}

	protected final String surround(final StringBuilder sb, final char first, final char last,
			final IExpression... exp) {
		sb.append(first);
		for (int i = 0; i < exp.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(exp[i] == null ? "nil" : exp[i].serialize(false));
		}
		final int length = sb.length();
		if (length > 2 && sb.charAt(length - 1) == ' ') {
			sb.setLength(length - 1);
		}
		sb.append(last);
		// sb.append(' ');
		return sb.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		return true;
	}

	@Override
	public String getDefiningPlugin() {
		return null;
	}

}
