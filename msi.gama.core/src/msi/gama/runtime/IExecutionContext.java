package msi.gama.runtime;

import java.util.Map;

import msi.gama.metamodel.agent.IAgent;

public interface IExecutionContext {

	public interface Agent extends IExecutionContext {
		public abstract IAgent getAgent();

		@Override
		public abstract IExecutionContext.Agent copy();

		@Override
		public abstract IExecutionContext.Agent getOuter();

		public abstract IExecutionContext.Agent createChild(IAgent agent);
	}

	public interface Statement extends IExecutionContext {
		public abstract Map<? extends String, ? extends Object> getAllOwnVars();

		public abstract void clearOwnVars();

		public abstract void putOwnVar(String varName, Object val);

		public abstract Object getOwnVar(String string);

		public abstract boolean hasOwnVar(String name);

		@Override
		public abstract IExecutionContext.Statement getOuter();

		@Override
		public abstract IExecutionContext.Statement copy();

		public abstract IExecutionContext.Statement createChild();

		public abstract void removeOwnVar(String name);

	}

	public default int depth() {
		if (getOuter() == null)
			return 0;
		return 1 + getOuter().depth();
	}

	public abstract void setVar(String name, Object value);

	public abstract Object getVar(String name);

	public abstract boolean hasVar(String name);

	public abstract IExecutionContext copy();

	public abstract IExecutionContext getOuter();

}