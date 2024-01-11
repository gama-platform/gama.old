/*******************************************************************************************************
 *
 * ExecutionContext.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import msi.gaml.compilation.ISymbol;

/**
 * The Class ExecutionContext.
 */
public class ExecutionContext implements IExecutionContext {

	/**
	 * Creates the.
	 *
	 * @param outer
	 *            the outer
	 * @return the execution context
	 */
	public static ExecutionContext create(final IExecutionContext outer, final ISymbol command) {
		return create(outer.getScope(), outer, command);
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @return the execution context
	 */
	public static ExecutionContext create(final IScope scope, final ISymbol command) {
		return create(scope, null, command);
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param outer
	 *            the outer
	 * @return the execution context
	 */
	public static ExecutionContext create(final IScope scope, final IExecutionContext outer, final ISymbol command) {
		final ExecutionContext result;
		result = new ExecutionContext(command);
		result.scope = scope;
		result.outer = outer;
		return result;
	}

	/** The local. */
	Map<String, Object> local;

	/** The outer. */
	IExecutionContext outer;

	/** The scope. */
	IScope scope;

	/** The command. */
	ISymbol command;

	@Override
	public void dispose() {
		local = null;
		outer = null;
		scope = null;
	}

	@Override
	public IScope getScope() { return scope; }

	/**
	 * Instantiates a new execution context.
	 */
	ExecutionContext(final ISymbol command) {
		this.command = command;
	}

	@Override
	public final IExecutionContext getOuterContext() { return outer; }

	@Override
	public void setTempVar(final String name, final Object value) {
		if (local == null || !local.containsKey(name)) {
			if (outer != null) { outer.setTempVar(name, value); }
		} else {
			local.put(name, value);
		}
	}

	@Override
	public Object getTempVar(final String name) {
		if (local == null || !local.containsKey(name)) return outer == null ? null : outer.getTempVar(name);
		return local.get(name);
	}

	/**
	 * Creates the copy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param command
	 *            the command
	 * @return the execution context
	 * @date 3 août 2023
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public ExecutionContext createCopy(final ISymbol command) {
		final ExecutionContext r = create(scope, outer, command);
		if (local != null) {
			r.local = Collections.synchronizedMap(new HashMap<>());
			r.local.putAll(local);
		}
		return r;
	}

	/**
	 * Creates the child context.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param command
	 *            the command
	 * @return the execution context
	 * @date 3 août 2023
	 */
	@Override
	public ExecutionContext createChildContext(final ISymbol command) {
		return create(this, command);
	}

	@Override
	public Map<? extends String, ? extends Object> getLocalVars() {
		return local == null ? Collections.EMPTY_MAP : local;
	}

	@Override
	public void clearLocalVars() {
		local = null;
	}

	@Override
	public void putLocalVar(final String varName, final Object val) {
		if (local == null) { local = Collections.synchronizedMap(new HashMap<>()); }
		local.put(varName, val);
	}

	@Override
	public Object getLocalVar(final String string) {
		if (local == null) return null;
		return local.get(string);
	}

	@Override
	public boolean hasLocalVar(final String name) {
		if (local == null) return false;
		return local.containsKey(name);
	}

	@Override
	public void removeLocalVar(final String name) {
		if (local == null) return;
		local.remove(name);
	}

	@Override
	public String toString() {
		return "execution context " + local;
	}

	@Override
	public ISymbol getCurrentSymbol() { return command; }

	@Override
	public void setCurrentSymbol(final ISymbol statement) { command = statement; }

}