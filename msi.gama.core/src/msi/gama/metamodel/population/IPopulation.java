/*********************************************************************************************
 *
 *
 * 'IPopulation.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.population;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;

import msi.gama.common.interfaces.IStepable;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IExecutable;
import msi.gaml.variables.IVariable;

/**
 * A population is a collection of agents of a species.
 *
 * Written by drogoul Modified on 24 juin 2010
 *
 * @todo Description
 *
 */
public interface IPopulation<T extends IAgent>
		extends Comparable<IPopulation<T>>, IList<T>, IStepable, IPopulationSet<T> {

	public interface Listener {

		public void notifyAgentRemoved(IPopulation<? extends IAgent> pop, IAgent agent);

		public void notifyAgentAdded(IPopulation<? extends IAgent> pop, IAgent agent);

		public void notifyAgentsAdded(IPopulation<? extends IAgent> pop, Collection<? extends IAgent> agents);

		public void notifyAgentsRemoved(IPopulation<? extends IAgent> pop, Collection<? extends IAgent> agents);

		public void notifyPopulationCleared(IPopulation<? extends IAgent> pop);

	}

	public static class IsLiving implements Predicate<IAgent> {

		/**
		 * Method apply()
		 * 
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		@Override
		public boolean apply(final IAgent input) {
			return input != null && !input.dead();
		}

	}

	public abstract void createVariablesFor(IScope scope, T agent) throws GamaRuntimeException;

	public abstract boolean hasVar(final String n);

	/**
	 * Create agents as members of this population.
	 *
	 * @param scope
	 * @param number
	 *            The number of agent to create.
	 * @param initialValues
	 *            The initial values of agents' variables.
	 * @param isRestored
	 *            Indicates that the agents are newly created or they are
	 *            restored (on a capture or release). If agents are restored on
	 *            a capture or release then don't run their "init" reflex again.
	 *
	 * @return
	 * @throws GamaRuntimeException
	 */
	public abstract IList<T> createAgents(IScope scope, int number, List<? extends Map<String, Object>> initialValues,
			boolean isRestored, boolean toBeScheduled) throws GamaRuntimeException;

	public abstract IList<T> createAgents(final IScope scope, final IContainer<?, ? extends IShape> geometries)
			throws GamaRuntimeException;

	public abstract T createAgentAt(final IScope s, int index, Map<String, Object> initialValues, boolean isRestored,
			boolean toBeScheduled) throws GamaRuntimeException;

	// public abstract Iterator<IAgent> getAgentsList();

	public abstract String getName();

	public abstract boolean isGrid();

	public abstract boolean hasAspect(String default1);

	public abstract IExecutable getAspect(String default1);

	public abstract Collection<String> getAspectNames();

	@Override
	public abstract ISpecies getSpecies();

	// public abstract boolean manages(ISpecies s, boolean direct);

	public abstract IVariable getVar(final String s);

	boolean hasUpdatableVariables();

	/**
	 * Returns the topology associated to this population.
	 *
	 * @return
	 */
	public abstract ITopology getTopology();

	public abstract void initializeFor(IScope scope) throws GamaRuntimeException;

	/**
	 * Returns the macro-agent hosting this population.
	 *
	 * @return
	 */
	public abstract IMacroAgent getHost();

	/**
	 * @throws GamaRuntimeException
	 *             When the "shape" of host changes, this method is invoked to
	 *             update the topology.
	 */
	// public abstract void hostChangesShape();

	/**
	 * Kills all the agents managed by this population.
	 *
	 * @throws GamaRuntimeException
	 */
	public abstract void killMembers() throws GamaRuntimeException;

	/**
	 * @param obj
	 * @return
	 */
	public abstract T getAgent(Integer obj);

	public void addListener(IPopulation.Listener listener);

	public void removeListener(IPopulation.Listener listener);

	public abstract void updateVariables(IScope scope, IAgent a);

	/**
	 * @param scope
	 * @param coord
	 * @return
	 */
	T getAgent(IScope scope, ILocation coord);

	@Override
	T[] toArray();

}