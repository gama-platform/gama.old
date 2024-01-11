/*******************************************************************************************************
 *
 * Arguments.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;

/**
 * @author drogoul
 */
public class Arguments extends Facets {

	/** The null. */
	public static Arguments NULL = new Arguments();

	/** The keys. */
	List<String> keys;

	/** The caller. */
	/*
	 * The caller represents the agent in the context of which the arguments need to be evaluated.
	 */
	ThreadLocal<IAgent> caller = new ThreadLocal<>();

	/**
	 * Instantiates a new arguments.
	 *
	 * @param args
	 *            the args
	 */
	public Arguments(final Arguments args) {
		super(args);
		if (args != null) { setCaller(args.caller.get()); }
	}

	/**
	 * Instantiates a new arguments.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param myArgs
	 *            the my args
	 * @date 26 nov. 2023
	 */
	public Arguments(final Map<String, Object> myArgs) {
		myArgs.forEach((k, v) -> put(k, ConstantExpressionDescription.create(v)));
	}

	/**
	 * Instantiates a new arguments.
	 *
	 * @param caller
	 *            the caller
	 * @param args
	 *            the args
	 */
	/*
	 * A constructor that takes a caller and arguments defined as a map <string, values>. Values are then transformed
	 * into a constant expression
	 */
	public Arguments(final IAgent caller, final Map<String, Object> args) {
		this(args);
		setCaller(caller);
	}

	/**
	 * Instantiates a new arguments.
	 */
	public Arguments() {}

	@Override
	public Arguments cleanCopy() {
		final Arguments result = new Arguments();
		result.setCaller(caller.get());
		forEach((s, e) -> result.put(s, e.cleanCopy()));
		return result;
	}

	/**
	 * Resolve against.
	 *
	 * @param scope
	 *            the scope
	 * @return the arguments
	 */
	public Arguments resolveAgainst(final IScope scope) {
		final Arguments result = new Arguments();
		result.setCaller(caller.get());
		forEach((s, e) -> {
			final IExpression exp = getExpr(s);
			if (exp != null) { result.putExpression(s, exp.resolveAgainst(scope)); }
		});

		return result;
	}

	@Override
	public IExpressionDescription put(final String s, final IExpressionDescription e) {
		if (keys == null || !keys.contains(s)) {
			if (keys == null) { keys = new ArrayList<>(); }
			keys.add(s);
		}
		return super.put(s, e);
	}

	/**
	 * Removes the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param s
	 *            the s
	 * @return the i expression description
	 * @date 27 déc. 2023
	 */
	public IExpressionDescription remove(final String s) {
		if (keys != null) { keys.remove(s); }
		return super.remove(s);
	}

	/**
	 * Sets the caller.
	 *
	 * @param caller
	 *            the new caller
	 */
	public void setCaller(final IAgent caller) {
		this.caller.set(caller);
	}

	/**
	 * Gets the caller.
	 *
	 * @return the caller
	 */
	public IAgent getCaller() { return caller.get(); }

	@Override
	public void dispose() {
		super.dispose();
		caller.set(null);
	}

	/**
	 * Gets the expr.
	 *
	 * @param index
	 *            the index
	 * @return the expr
	 */
	public IExpression getExpr(final int index) {
		if (index > size() || index < 0) return null;
		String key = keys.get(index);
		return get(key).getExpression();
	}
}
