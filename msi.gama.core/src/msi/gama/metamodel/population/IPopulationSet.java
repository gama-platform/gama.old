/*******************************************************************************************************
 *
 * IPopulationSet.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.population;

import java.util.Collection;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import one.util.streamex.StreamEx;

/**
 * Class IPopulationSet. An interface common to ISpecies, IPopulation and
 * MetaPopulation
 * 
 * @author drogoul
 * @since 9 déc. 2013
 * 
 */
public interface IPopulationSet<T extends IAgent> extends IContainer<Integer, T>, IAgentFilter {

	/**
	 * Gets the populations.
	 *
	 * @param scope the scope
	 * @return the populations
	 */
	Collection<? extends IPopulation<? extends IAgent>> getPopulations(IScope scope);

	/**
	 * Stream.
	 *
	 * @param scope the scope
	 * @return the stream ex
	 */
	@Override
	StreamEx<T> stream(final IScope scope);

}
