/*******************************************************************************************************
 *
 * AbstractExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.benchmark.StopWatch;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gaml.compilation.GAML;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Abstract class that defines the structure of all expression classes.
 *
 * @author drogoul
 */
@SuppressWarnings ("rawtypes")
public abstract class AbstractExpression implements IExpression {

	/** The type. */
	protected IType type = null;

	@Override
	public IType<?> getGamlType() { return type == null ? Types.NO_TYPE : type; }

	/**
	 * Parenthesize.
	 *
	 * @param sb
	 *            the sb
	 * @param exp
	 *            the exp
	 */
	public static void parenthesize(final StringBuilder sb, final IExpression... exp) {
		if (exp.length == 1 && !exp[0].shouldBeParenthesized()) {
			sb.append(exp[0].serializeToGaml(false));
		} else {
			surround(sb, '(', ')', exp);
		}
	}

	/**
	 * Surround.
	 *
	 * @param sb
	 *            the sb
	 * @param first
	 *            the first
	 * @param last
	 *            the last
	 * @param exp
	 *            the exp
	 * @return the string
	 */
	public static String surround(final StringBuilder sb, final char first, final char last, final IExpression... exp) {
		sb.append(first);
		for (int i = 0; i < exp.length; i++) {
			if (i > 0) { sb.append(','); }
			sb.append(exp[i] == null ? "nil" : exp[i].serializeToGaml(false));
		}
		final int length = sb.length();
		if (length > 2 && sb.charAt(length - 1) == ' ') { sb.setLength(length - 1); }
		sb.append(last);
		// sb.append(' ');
		return sb.toString();
	}

	@Override
	public String getTitle() {
		// Serialized version by default
		return serializeToGaml(false);
	}

	@Override
	public final Object value(final IScope scope) {
		try (StopWatch w = GAMA.benchmark(scope, this)) {
			return _value(scope);
		} catch (final OutOfMemoryError e) {
			GamaExecutorService.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e);
			return null;
		}
	}

	/**
	 * Optimized. Return an optimized version of the expression if the preference is true
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 11 janv. 2024
	 */
	protected IExpression optimized() {
		return GamaPreferences.Experimental.CONSTANT_OPTIMIZATION.getValue() && isConst()
				? GAML.getExpressionFactory().createConst(getConstValue(), getGamlType(), serializeToGaml(false))
				: this;
	}

	/**
	 * Value.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 */
	protected abstract Object _value(IScope scope);

}
