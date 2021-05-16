/*******************************************************************************************************
 *
 * msi.gama.runtime.ExecutionContext.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

public class ExecutionContext implements IExecutionContext {

	private static final PoolUtils.ObjectPool<ExecutionContext> POOL =
			PoolUtils.create("Execution Context", true, () -> new ExecutionContext(), null, null);

	public static ExecutionContext create(final IExecutionContext outer) {
		return create(outer.getScope(), outer);
	}

	public static ExecutionContext create(final IScope scope) {
		return create(scope, null);
	}

	public static ExecutionContext create(final IScope scope, final IExecutionContext outer) {
		final ExecutionContext result = POOL.get();
		result.scope = scope;
		result.outer = outer;
		return result;
	}

	Map<String, Object> local;
	IExecutionContext outer;
	IScope scope;

	@Override
	public void dispose() {
		local = null;
		outer = null;
		scope = null;
		POOL.release(this);
	}

	@Override
	public IScope getScope() {
		return scope;
	}

	ExecutionContext() {}

	@Override
	public final IExecutionContext getOuterContext() {
		return outer;
	}

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
			r.local = GamaMapFactory.createWithoutCasting(Types.NO_TYPE, Types.NO_TYPE, local, false);
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
		local = null;
	}

	@Override
	public void putLocalVar(final String varName, final Object val) {
		if (local == null) { local = GamaMapFactory.createUnordered(); }
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