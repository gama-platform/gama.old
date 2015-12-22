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

import java.util.*;
import com.google.common.base.Predicate;
import msi.gama.common.interfaces.IStepable;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
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
public interface IPopulation extends Comparable<IPopulation>, IList<IAgent>, IStepable, IPopulationSet {

	public interface Listener {

		public void notifyAgentRemoved(IPopulation pop, IAgent agent);

		public void notifyAgentAdded(IPopulation pop, IAgent agent);

		public void notifyAgentsAdded(IPopulation pop, Collection agents);

		public void notifyAgentsRemoved(IPopulation pop, Collection agents);

		public void notifyPopulationCleared(IPopulation pop);

	}

	public static class IsLiving implements Predicate<IAgent> {

		/**
		 * Method apply()
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		@Override
		public boolean apply(final IAgent input) {
			return input != null && !input.dead();
		}

	}

	public abstract void createVariablesFor(IScope scope, IAgent agent) throws GamaRuntimeException;

	public abstract boolean hasVar(final String n);

	/**
	 * Create agents as members of this population.
	 *
	 * @param scope
	 * @param number The number of agent to create.
	 * @param initialValues The initial values of agents' variables.
	 * @param isRestored Indicates that the agents are newly created or they are restored (on a
	 * capture or release).
	 * If agents are restored on a capture or release then don't run their "init" reflex
	 * again.
	 *
	 * @return
	 * @throws GamaRuntimeException
	 */
	public abstract IList<? extends IAgent> createAgents(IScope scope, int number, List<? extends Map> initialValues,
		boolean isRestored) throws GamaRuntimeException;

	public abstract IList<? extends IAgent> createAgents(final IScope scope, final IContainer<?, IShape> geometries)
		throws GamaRuntimeException;

	// public abstract Iterator<IAgent> getAgentsList();

	public abstract String getName();

	public abstract boolean isGrid();

	public abstract boolean hasAspect(String default1);

	public abstract IExecutable getAspect(String default1);

	public abstract Collection<String> getAspectNames();

	@Override
	public abstract ISpecies getSpecies();

	// public abstract boolean manages(ISpecies s, boolean direct);

	public abstract IVariable getVar(final IAgent a, final String s);

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
	 * When the "shape" of host changes, this method is invoked to update the topology.
	 */
	public abstract void hostChangesShape();

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
	public abstract IAgent getAgent(Integer obj);

	public void addListener(IPopulation.Listener listener);

	public void removeListener(IPopulation.Listener listener);

	public abstract void updateVariables(IScope scope, IAgent a);

	/**
	 * @param scope
	 * @param coord
	 * @return
	 */
	IAgent getAgent(IScope scope, ILocation coord);

	@Override
		IAgent[] toArray();

}