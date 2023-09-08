/*******************************************************************************************************
 *
 * IAgentFilter.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.filter;

import java.util.Collection;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.species.ISpecies;

/**
 * The Interface IAgentFilter.
 */
public interface IAgentFilter {

	/**
	 * Checks for agent list.
	 *
	 * @return true, if successful
	 */
	boolean hasAgentList();

	/**
	 * Gets the species.
	 *
	 * @return the species
	 */
	ISpecies getSpecies();

	/**
	 * Gets the population.
	 *
	 * @param scope the scope
	 * @return the population
	 */
	IPopulation<? extends IAgent> getPopulation(IScope scope);

	/**
	 * Gets the agents.
	 *
	 * @param scope the scope
	 * @return the agents
	 */
	IContainer<?, ? extends IAgent> getAgents(IScope scope);

	/**
	 * Accept.
	 *
	 * @param scope the scope
	 * @param source the source
	 * @param a the a
	 * @return true, if successful
	 */
	boolean accept(IScope scope, IShape source, IShape a);

	/**
	 * Filter.
	 *
	 * @param scope the scope
	 * @param source the source
	 * @param results the results
	 */
	void filter(IScope scope, IShape source, Collection<? extends IShape> results);

}