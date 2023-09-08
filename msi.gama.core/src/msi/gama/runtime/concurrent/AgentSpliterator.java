/*******************************************************************************************************
 *
 * AgentSpliterator.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;

/**
 * The Class AgentSpliterator.
 */
public class AgentSpliterator implements Spliterator<IAgent> {

	/**
	 * Of.
	 *
	 * @param agents the agents
	 * @param threshold the threshold
	 * @return the spliterator
	 */
	public static Spliterator<IAgent> of(final IShape[] agents, final int threshold) {
		if (agents == null || agents.length == 0) { return Spliterators.<IAgent> emptySpliterator(); }
		return new AgentSpliterator(agents, 0, agents.length, threshold);
	}

	/**
	 * Of.
	 *
	 * @param agents the agents
	 * @param threshold the threshold
	 * @return the spliterator
	 */
	public static Spliterator<IAgent> of(final List<? extends IShape> agents, final int threshold) {
		final int size = agents.size();
		return new AgentSpliterator(agents.toArray(new IAgent[size]), 0, size, threshold);
	}

	/** The begin. */
	int begin;
	
	/** The threshold. */
	final int end, threshold;
	
	/** The agents. */
	final IShape[] agents;

	/**
	 * Instantiates a new agent spliterator.
	 *
	 * @param array the array
	 * @param begin the begin
	 * @param end the end
	 * @param threshold the threshold
	 */
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
		if (size <= threshold) { return null; }
		final int mid = begin + size / 2;
		final AgentSpliterator split = new AgentSpliterator(agents, begin, mid, threshold);
		begin = mid;
		return split;
	}

	@Override
	public long estimateSize() {
		return (long) end - begin;
	}

	@Override
	public int characteristics() {
		return Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.SIZED;
	}

}
