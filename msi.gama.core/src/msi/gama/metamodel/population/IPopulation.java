/*******************************************************************************************************
 *
 * msi.gama.metamodel.population.IPopulation.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.population;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;

import msi.gama.common.interfaces.IDisposable;
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
import msi.gaml.statements.RemoteSequence;
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
		extends Comparable<IPopulation<T>>, IList<T>, IStepable, IDisposable, IPopulationSet<T> {

	public interface Listener {

		void notifyAgentRemoved(IScope scope, IPopulation<? extends IAgent> pop, IAgent agent);

		void notifyAgentAdded(IScope scope, IPopulation<? extends IAgent> pop, IAgent agent);

		void notifyAgentsAdded(IScope scope, IPopulation<? extends IAgent> pop, Collection<? extends IAgent> agents);

		void notifyAgentsRemoved(IScope scope, IPopulation<? extends IAgent> pop, Collection<? extends IAgent> agents);

		void notifyPopulationCleared(IScope scope, IPopulation<? extends IAgent> pop);

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

	static IPopulation<? extends IAgent> createEmpty(final ISpecies species) {
		return new GamaPopulation<>(null, species);
	}

	void createVariablesFor(IScope scope, T agent) throws GamaRuntimeException;

	boolean hasVar(final String n);

	@Override
	default IPopulation<? extends IAgent> getPopulation(final IScope scope) {
		return this;
	}

	/**
	 * Create agents as members of this population.
	 *
	 * @param scope
	 * @param number
	 *            The number of agent to create.
	 * @param initialValues
	 *            The initial values of agents' variables.
	 * @param isRestored
	 *            Indicates that the agents are newly created or they are restored (on a capture or release). If agents
	 *            are restored on a capture or release then don't run their "init" reflex again
	 * @param toBeScheduled
	 *            Whether the agent should be immediately scheduled or not
	 * @param sequence
	 *            an optional sequence of code to be run after the agent has been scheduled (and then inited). Can be
	 *            null.
	 *
	 * @return
	 * @throws GamaRuntimeException
	 */
	IList<T> createAgents(IScope scope, int number, List<? extends Map<String, Object>> initialValues,
			boolean isRestored, boolean toBeScheduled, RemoteSequence sequence) throws GamaRuntimeException;

	IList<T> createAgents(final IScope scope, final IContainer<?, ? extends IShape> geometries, RemoteSequence sequence)
			throws GamaRuntimeException;

	default IList<T> createAgents(final IScope scope, final IContainer<?, ? extends IShape> geometries)
			throws GamaRuntimeException {
		return this.createAgents(scope, geometries, null);
	}

	default IList<T> createAgents(final IScope scope, final int number,
			final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
			final boolean toBeScheduled) throws GamaRuntimeException {
		return this.createAgents(scope, number, initialValues, isRestored, toBeScheduled, null);
	}

	T createAgentAt(final IScope s, int index, Map<String, Object> initialValues, boolean isRestored,
			boolean toBeScheduled) throws GamaRuntimeException;

	// public abstract Iterator<IAgent> getAgentsList();

	String getName();

	boolean isGrid();

	boolean hasAspect(String default1);

	IExecutable getAspect(String default1);

	Collection<String> getAspectNames();

	@Override
	ISpecies getSpecies();

	// public abstract boolean manages(ISpecies s, boolean direct);

	IVariable getVar(final String s);

	boolean hasUpdatableVariables();

	/**
	 * Returns the topology associated to this population.
	 *
	 * @return
	 */
	ITopology getTopology();

	void initializeFor(IScope scope) throws GamaRuntimeException;

	/**
	 * Returns the macro-agent hosting this population.
	 *
	 * @return
	 */
	IMacroAgent getHost();

	/**
	 * Set the macro-agent hosting this population.
	 *
	 * @return
	 */
	void setHost(IMacroAgent agt);

	/**
	 * @throws GamaRuntimeException
	 *             When the "shape" of host changes, this method is invoked to update the topology.
	 */
	// public abstract void hostChangesShape();

	/**
	 * Kills all the agents managed by this population.
	 *
	 * @throws GamaRuntimeException
	 */
	void killMembers() throws GamaRuntimeException;

	/**
	 * @param obj
	 * @return
	 */
	T getAgent(Integer obj);

	void addListener(IPopulation.Listener listener);

	void removeListener(IPopulation.Listener listener);

	void updateVariables(IScope scope, IAgent a);

	/**
	 * @param scope
	 * @param coord
	 * @return
	 */
	T getAgent(IScope scope, ILocation coord);

	@Override
	T[] toArray();

	boolean isInitOverriden();

	boolean isStepOverriden();

}