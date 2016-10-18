/*********************************************************************************************
 * 
 * 
 * 'IPopulationSet.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
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

	Collection<? extends IPopulation<? extends IAgent>> getPopulations(IScope scope);

	@Override
	StreamEx<T> stream(final IScope scope);

}
