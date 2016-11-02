/*********************************************************************************************
 *
 * 'In.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.filter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Iterables;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.ISpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@SuppressWarnings({ "rawtypes" })
public abstract class In implements IAgentFilter {

	public static IAgentFilter list(final IScope scope, final IContainer<?, ? extends IShape> targets) {
		if (targets.isEmpty(scope)) {
			return null;
		}
		if (targets instanceof IPopulationSet) {
			return (IPopulationSet) targets;
		}
		return new InList(targets.listValue(scope, Types.NO_TYPE, false));
	}

	public static IAgentFilter edgesOf(final ISpatialGraph graph) {
		return graph;
	}

	@Override
	public abstract boolean accept(IScope scope, IShape source, IShape a);

	private static class InList extends In {

		final Set<IShape> agents;
		final IType contentType;

		InList(final IList<? extends IShape> list) {
			agents = new LinkedHashSet<IShape>(list);
			contentType = list.getType().getContentType();
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape a) {
			return a.getGeometry() != source.getGeometry() && agents.contains(a);
		}

		@Override
		public IContainer<?, ? extends IAgent> getAgents(final IScope scope) {
			return GamaListFactory.createWithoutCasting(Types.AGENT, Iterables.filter(agents, IAgent.class));
		}

		@Override
		public ISpecies getSpecies() {
			return null;
		}

		@Override
		public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
			agents.remove(source);
			results.retainAll(agents);
		}

	}

}
