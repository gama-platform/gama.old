package msi.gama.runtime;

import java.util.Map;

import gnu.trove.map.hash.THashMap;

public class ExecutionContext extends THashMap<String, Object> implements IExecutionContext.Statement {

	IExecutionContext outer;

	public ExecutionContext() {
		this(null);
	}

	private ExecutionContext(final IExecutionContext previous) {
		super(5);
		this.outer = previous;
	}

	@Override
	public void setVar(final String name, final Object value) {
		final int i = index(name);
		if (i == -1) {
			if (outer != null)
				outer.setVar(name, value);
		} else {
			_values[i] = value;
		}
	}

	@Override
	public Object getVar(final String name) {
		final int i = index(name);
		if (i < 0) {
			if (outer == null)
				return null;
			return outer.getVar(name);
		}
		return _values[i];
	}

	@Override
	public boolean hasVar(final String name) {
		return index(name) >= 0 || outer != null && outer.hasVar(name);
	}

	@Override
	public ExecutionContext copy() {
		final ExecutionContext r = new ExecutionContext(outer);
		r.putAll(this);
		return r;
	}

	@Override
	public IExecutionContext.Statement getOuter() {
		return (Statement) outer;
	}

	@Override
	public Map<? extends String, ? extends Object> getAllOwnVars() {
		return this;
	}

	@Override
	public void clearOwnVars() {
		clear();
	}

	@Override
	public void putOwnVar(final String varName, final Object val) {
		put(varName, val);
	}

	@Override
	public Object getOwnVar(final String string) {
		return get(string);
	}

	@Override
	public boolean hasOwnVar(final String name) {
		return contains(name);
	}

	@Override
	public Statement createChild() {
		return new ExecutionContext(this);
	}

	@Override
	public void removeOwnVar(final String name) {
		remove(name);
	}

}