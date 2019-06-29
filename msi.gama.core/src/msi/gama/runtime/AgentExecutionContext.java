package msi.gama.runtime;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.agent.IAgent;

class AgentExecutionContext implements IDisposable {

	public static AgentExecutionContext create(final IAgent agent, final AgentExecutionContext outer,
			final PoolUtils.ObjectPool<AgentExecutionContext> pool) {
		return pool.get().init(agent, outer, pool);
	}

	IAgent agent;
	AgentExecutionContext outer;
	PoolUtils.ObjectPool<AgentExecutionContext> pool;

	public AgentExecutionContext() {}

	@Override
	public void dispose() {
		agent = null;
		outer = null;
		pool.release(this);
	}

	public AgentExecutionContext init(final IAgent agent, final AgentExecutionContext outer,
			final PoolUtils.ObjectPool<AgentExecutionContext> pool) {
		this.outer = outer;
		this.agent = agent;
		this.pool = pool;
		return this;
	}

	public IAgent getAgent() {
		return agent;
	}

	@Override
	public String toString() {
		return "context of " + agent;
	}

	public AgentExecutionContext getOuterContext() {
		return outer;
	}

}