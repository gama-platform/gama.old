/*******************************************************************************************************
 *
 * ExecutionContext.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import java.util.Collections;
import java.util.Map;

import msi.gama.common.util.PoolUtils;
import msi.gama.util.GamaMapFactory;
import msi.gaml.types.Types;

/**
 * The Class ExecutionContext.
 */
public class ExecutionContext implements IExecutionContext {

	/** The Constant POOL. */
	// Disactivated for the moment as it doesnt seem to make a significant difference and might actually create problems
	// in concurrent setups
	private static final PoolUtils.ObjectPool<ExecutionContext> POOL =
			PoolUtils.create("Execution Context", true, ExecutionContext::new, null, null);

	/** The Constant POOL_ACTIVE. */
	private static final boolean POOL_ACTIVE = false;

	/**
	 * Creates the.
	 *
	 * @param outer
	 *            the outer
	 * @return the execution context
	 */
	public static ExecutionContext create(final IExecutionContext outer) {
		return create(outer.getScope(), outer);
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @return the execution context
	 */
	public static ExecutionContext create(final IScope scope) {
		return create(scope, null);
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
	public static ExecutionContext create(final IScope scope, final IExecutionContext outer) {
		final ExecutionContext result;
		if (POOL_ACTIVE) {
			result = POOL.get();
		} else {
			result = new ExecutionContext();
		}
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

	@Override
	public void dispose() {
		local = null;
		outer = null;
		scope = null;
		if (POOL_ACTIVE) { POOL.release(this); }
	}

	@Override
	public IScope getScope() { return scope; }

	/**
	 * Instantiates a new execution context.
	 */
	ExecutionContext() {}

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

	@SuppressWarnings ("unchecked")
	@Override
	public ExecutionContext createCopy() {
		final ExecutionContext r = create(scope, outer);
		if (local != null) {
			synchronized (local) {
				r.local = Collections.synchronizedMap(
						GamaMapFactory.createWithoutCasting(Types.NO_TYPE, Types.NO_TYPE, local, false));
			}
		}
		return r;
	}

	@Override
	public ExecutionContext createChildContext() {
		return create(this);
	}

	@Override
	public Map<? extends String, ? extends Object> getLocalVars() {
		return local == null ? Collections.EMPTY_MAP : local;
	}

	@Override
	public void clearLocalVars() {
		if (local != null) {
			synchronized (local) {
				local = null;
			}
		}
	}

	@Override
	public void putLocalVar(final String varName, final Object val) {
		if (local == null) { local = GamaMapFactory.createSynchronizedUnordered(); }
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

}