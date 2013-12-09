/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.filter;

import java.util.*;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;
import com.google.common.collect.Sets;

public abstract class In implements IAgentFilter {

	public static IAgentFilter list(final IScope scope, final IContainer<?, ? extends IShape> targets) {
		if ( targets instanceof IPopulationSet ) { return (IPopulationSet) targets; }
		return new InList(targets.listValue(scope));
	}

	public static IAgentFilter edgesOf(final ISpatialGraph graph) {
		return new InGraph((GamaSpatialGraph) graph, true);
	}

	@Override
	public abstract boolean accept(IScope scope, IShape source, IShape a);

	private static class InList extends In {

		final Set<IShape> agents;

		InList(final IList<? extends IShape> list) {
			agents = new LinkedHashSet<IShape>(list);
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape a) {
			return a.getGeometry() != source.getGeometry() && agents.contains(a);
		}

		@Override
		public IContainer<?, ? extends IShape> getAgents() {
			return new GamaList(agents);
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

	private static class InGraph extends In {

		final GamaSpatialGraph graph;
		final boolean byEdges;

		InGraph(final GamaSpatialGraph g, final boolean edges) {
			graph = g;
			byEdges = edges;
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape a) {
			return a.getGeometry() != source.getGeometry() && byEdges ? graph.containsEdge(a) : graph.containsVertex(a);

		}

		@Override
		public IContainer<?, ? extends IShape> getAgents() {
			return byEdges ? graph.getEdges() : graph.getVertices();
		}

		@Override
		public ISpecies getSpecies() {
			return null; // See if we can identify the species of edges / vertices
		}

		@Override
		public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
			Set<IShape> agents = Sets.newHashSet(byEdges ? graph.getEdges() : graph.getVertices());
			results.retainAll(agents);
		}
	}

}
