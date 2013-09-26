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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;

public abstract class In implements IAgentFilter {

	public static In list(final IScope scope, final IContainer<?, ? extends IShape> targets)
		throws GamaRuntimeException {
		return new InList(targets);
	}

	public static In list(final IScope scope, final IList<? extends IShape> targets) {
		return new InList(targets);
	}

	public static In edgesOf(final ISpatialGraph graph) {
		return new InGraph((GamaSpatialGraph) graph, true);
	}

	public static In verticesOf(final ISpatialGraph graph) {
		return new InGraph((GamaSpatialGraph) graph, false);
	}

	public static In population(final IPopulation species) {
		return new InSpecies(species);
	}

	@Override
	public abstract boolean accept(IScope scope, IShape source, IShape a);

	@Override
	public abstract boolean accept(IScope scope, ILocation source, IShape a);

	private static class InList extends In {

		final IContainer<?, ? extends IShape> agents;

		InList(final IContainer<?, ? extends IShape> list) {
			agents = list;
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape a) {
			return a.getGeometry() != source.getGeometry() && agents.contains(scope, a);
		}

		@Override
		public boolean accept(final IScope scope, final ILocation source, final IShape a) {
			return agents.contains(scope, a);
		}

		@Override
		public boolean filterSpecies(final ISpecies s) {
			return false;
		}

		/**
		 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getShapes()
		 */
		@Override
		public IContainer<?, ? extends IShape> getShapes(final IScope scope) {
			return agents;
		}

		@Override
		public ISpecies speciesFiltered() {
			return null;
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
		public boolean accept(final IScope scope, final ILocation source, final IShape a) {
			return byEdges ? graph.containsEdge(a) : graph.containsVertex(a);
		}

		@Override
		public boolean filterSpecies(final ISpecies s) {
			return false;
		}

		/**
		 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getShapes()
		 */
		@Override
		public IContainer<?, ? extends IShape> getShapes(final IScope scope) {
			return byEdges ? graph.getEdges() : graph.getVertices();
		}

		@Override
		public ISpecies speciesFiltered() {
			return null; // See if we can identify the species of edges / vertices
		}
	}

	private static class InSpecies extends In {

		final IPopulation pop;

		InSpecies(final IPopulation s) {
			pop = s;
		}

		@Override
		public boolean accept(final IScope scope, final IShape source, final IShape a) {
			final IAgent agent = a.getAgent();
			if ( agent == null ) { return false; }
			if ( agent.getPopulation() != pop ) { return false; }
			final IAgent as = source.getAgent();
			if ( as != null && as.getPopulation() != pop ) {
				if ( agent == as ) { return false; }
			}
			return true;
		}

		@Override
		public boolean filterSpecies(final ISpecies s) {
			return pop.getSpecies() == s;
		}

		/**
		 * @see msi.gama.metamodel.topology.filter.In#accept(msi.gama.metamodel.shape.GamaPoint,
		 *      msi.gama.metamodel.shape.IShape)
		 */
		@Override
		public boolean accept(final IScope scope, final ILocation source, final IShape a) {
			return a.getAgent() != null && a.getAgent().getPopulation() == pop;
		}

		/**
		 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getShapes()
		 */
		@Override
		public IContainer<?, ? extends IShape> getShapes(final IScope scope) {
			return pop;
		}

		@Override
		public ISpecies speciesFiltered() {
			return pop.getSpecies();
		}
	}

}
