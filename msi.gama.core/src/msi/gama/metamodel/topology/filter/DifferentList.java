/*********************************************************************************************
 *
 * 'DifferentList.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.filter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;

public class DifferentList implements IAgentFilter {

	final Set<IShape> agents;

	public DifferentList(final IList<? extends IShape> list) {
		agents = new LinkedHashSet<IShape>(list);
	}

	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		return a.getGeometry() != source.getGeometry() && !agents.contains(a);
	}

	@Override
	public IContainer<?, ? extends IAgent> getAgents(final IScope scope) {
		return GamaListFactory.create();
	}

	@Override
	public ISpecies getSpecies() {
		return null;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulation(final IScope scope) {
		return null;
	}

	@Override
	public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
		agents.remove(source);
		results.removeAll(agents);
	}

}
