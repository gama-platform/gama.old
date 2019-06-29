/*******************************************************************************************************
 *
 * msi.gama.runtime.ExecutionContext.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import java.util.Collections;
import java.util.Map;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.util.PoolUtils;

public class ExecutionContext implements IExecutionContext {

	Map<String, Object> local;
	private IExecutionContext outer;
	private IScope scope;
	private PoolUtils.ObjectPool<IExecutionContext> pool;

	public static IExecutionContext create(final IScope scope, final IExecutionContext outer,
			final PoolUtils.ObjectPool<IExecutionContext> pool) {
		// return new ExecutionContext().init(scope, outer);
		return pool.get().init(scope, outer, pool);
	}

	@Override
	public void dispose() {
		local = null;
		outer = null;
		scope = null;
		pool.release(this);
	}

	@Override
	public IScope getScope() {
		return scope;
	}

	@Override
	public IExecutionContext init(final IScope scope, final IExecutionContext outer,
			final PoolUtils.ObjectPool<IExecutionContext> pool) {
		this.scope = scope;
		this.outer = outer;
		this.pool = pool;
		return this;
	}

	ExecutionContext() {}

	@Override
	public final IExecutionContext getOuterContext() {
		return outer;
	}

	@Override
	public void setTempVar(final String name, final Object value) {
		if (local == null || !local.containsKey(name)) {
			if (outer != null) {
				outer.setTempVar(name, value);
			}
		} else {
			local.put(name, value);
		}

	}

	@Override
	public Object getTempVar(final String name) {
		if (local == null || !local.containsKey(name)) { return outer == null ? null : outer.getTempVar(name); }
		return local.get(name);
	}

	@Override
	public IExecutionContext createCopyContext() {
		final ExecutionContext r = (ExecutionContext) pool.get().init(scope, outer, pool);
		if (local != null) {
			r.local = new THashMap<>(local); // JavaUtils.MAP_POOL.get();
			r.local.putAll(local);
		}
		return r;
	}

	@Override
	public IExecutionContext createChildContext() {
		return pool.get().init(scope, this, pool);
	}

	@Override
	public Map<? extends String, ? extends Object> getLocalVars() {
		return local == null ? Collections.EMPTY_MAP : local;
	}

	@Override
	public void putLocalVar(final String varName, final Object val) {
		if (local == null) {
			local = new THashMap<>();
		}
		local.put(varName, val);
	}

	@Override
	public Object getLocalVar(final String string) {
		if (local == null) { return null; }
		return local.get(string);
	}

	@Override
	public boolean hasLocalVar(final String name) {
		if (local == null) { return false; }
		return local.containsKey(name);
	}

	@Override
	public void removeLocalVar(final String name) {
		if (local == null) { return; }
		local.remove(name);
	}

	@Override
	public String toString() {
		return "execution context " + local;
	}

}