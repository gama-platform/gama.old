package msi.gama.runtime.concurrent;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;

public class AgentSpliterator implements Spliterator<IAgent> {

	public static Spliterator<IAgent> of(final IShape[] agents, final int threshold) {
		if (agents == null || agents.length == 0)
			return Spliterators.<IAgent> emptySpliterator();
		return new AgentSpliterator(agents, 0, agents.length, threshold);
	}

	public static Spliterator<IAgent> of(final List<? extends IAgent> agents, final int threshold) {
		return new AgentSpliterator(agents.toArray(new IAgent[0]), 0, agents.size(), threshold);
	}

	int begin;
	final int end, threshold;
	final IShape[] agents;

	private AgentSpliterator(final IShape[] array, final int begin, final int end, final int threshold) {
		this.begin = begin;
		this.end = end;
		this.threshold = threshold;
		agents = array;
	}

	@Override
	public void forEachRemaining(final Consumer<? super IAgent> action) {
		for (int i = begin; i < end; ++i) {
			action.accept((IAgent) agents[i]);
		}
	}

	@Override
	public boolean tryAdvance(final Consumer<? super IAgent> action) {
		return true;
	}

	@Override
	public AgentSpliterator trySplit() {
		final int size = end - begin;
		if (size <= threshold) {
			return null;
		}
		final int mid = begin + size / 2;
		final AgentSpliterator split = new AgentSpliterator(agents, begin, mid, threshold);
		begin = mid;
		return split;
	}

	@Override
	public long estimateSize() {
		return end - begin;
	}

	@Override
	public int characteristics() {
		return Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.SIZED;
	}

}
