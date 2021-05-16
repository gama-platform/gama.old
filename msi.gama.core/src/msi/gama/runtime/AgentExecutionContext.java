package msi.gama.runtime;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.agent.IAgent;

class AgentExecutionContext implements IDisposable {

	private static final PoolUtils.ObjectPool<AgentExecutionContext> POOL =
			PoolUtils.create("Agent Execution Context", true, () -> new AgentExecutionContext(), null, null);

	public static AgentExecutionContext create(final IAgent agent, final AgentExecutionContext outer) {
		final AgentExecutionContext result = POOL.get();
		result.agent = agent;
		result.outer = outer;
		return result;
	}

	IAgent agent;
	AgentExecutionContext outer;

	private AgentExecutionContext() {}

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

	@Override
	public void dispose() {
		agent = null;
		outer = null;
		POOL.release(this);
	}

	public AgentExecutionContext createCopy() {
		return create(agent, outer);
	}

}