/*******************************************************************************************************
 *
 * PopulationNotifier.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.population;

import java.util.Collection;
import java.util.LinkedList;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * The Class PopulationNotifier.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 24 sept. 2023
 */
public class PopulationNotifier implements IPopulation.Listener {

	/**
	 * Listeners, created in a lazy way
	 */
	private LinkedList<IPopulation.Listener> listeners;

	/**
	 * Checks for listeners.
	 *
	 * @return true, if successful
	 */
	private boolean hasListeners() {
		return listeners != null && !listeners.isEmpty();
	}

	/**
	 * Adds the listener.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param listener
	 *            the listener
	 * @date 24 sept. 2023
	 */
	public void addListener(final IPopulation.Listener listener) {
		if (listeners == null) { listeners = new LinkedList<>(); }
		if (!listeners.contains(listener)) { listeners.add(listener); }
	}

	/**
	 * Removes the listener.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param listener
	 *            the listener
	 * @date 24 sept. 2023
	 */
	public void removeListener(final IPopulation.Listener listener) {
		if (listeners == null) return;
		listeners.remove(listener);
	}

	@Override
	public void notifyAgentRemoved(final IScope scope, final IPopulation<? extends IAgent> pop, final IAgent agent) {
		if (!hasListeners()) return;
		try {
			for (final IPopulation.Listener l : listeners) { l.notifyAgentRemoved(scope, pop, agent); }
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyAgentAdded(final IScope scope, final IPopulation<? extends IAgent> pop, final IAgent agent) {
		if (!hasListeners()) return;
		try {
			for (final IPopulation.Listener l : listeners) { l.notifyAgentAdded(scope, pop, agent); }
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyAgentsAdded(final IScope scope, final IPopulation<? extends IAgent> pop,
			final Collection<? extends IAgent> container) {
		if (!hasListeners()) return;
		// create intermediary list so as to avoid side effects
		final Collection<? extends IAgent> agents = new LinkedList<>(container);
		try {
			for (final IPopulation.Listener l : listeners) { l.notifyAgentsAdded(scope, pop, agents); }
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyAgentsRemoved(final IScope scope, final IPopulation<? extends IAgent> pop,
			final Collection<? extends IAgent> agents) {
		if (!hasListeners()) return;
		try {
			for (final IPopulation.Listener l : listeners) { l.notifyAgentsRemoved(scope, pop, agents); }
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyPopulationCleared(final IScope scope, final IPopulation<? extends IAgent> pop) {
		if (!hasListeners()) return;
		try {
			for (final IPopulation.Listener l : listeners) { l.notifyPopulationCleared(scope, pop); }
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 24 sept. 2023
	 */
	public void clear() {
		listeners = null;
	}

}
