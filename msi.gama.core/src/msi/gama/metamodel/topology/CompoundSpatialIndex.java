/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.CompoundSpatialIndex.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Ordering;
import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	boolean disposed = false, unique = true;
	private Map<IPopulation<? extends IAgent>, ISpatialIndex> spatialIndexes;
	private final ICollector<ISpatialIndex> uniqueIndexes;
	private GamaQuadTree rootIndex;
	final protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds, final boolean parallel) {
		rootIndex = GamaQuadTree.create(bounds, parallel);
		uniqueIndexes = Collector.getOrderedSet();
		uniqueIndexes.add(rootIndex);
		final double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest, biggest * Math.sqrt(2) };
	}

	private ISpatialIndex findSpatialIndex(final IPopulation<? extends IAgent> s) {
		if (disposed) { return null; }
		if (unique) { return rootIndex; }
		final ISpatialIndex index = spatialIndexes == null ? null : spatialIndexes.get(s);
		return index == null ? rootIndex : index;
	}

	@Override
	public void insert(final IAgent a) {
		if (disposed) { return; }
		if (a == null) { return; }
		if (unique) {
			rootIndex.insert(a);
			return;
		}
		final ISpatialIndex si = findSpatialIndex(a.getPopulation());
		if (si != null) {
			si.insert(a);
		}
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		if (disposed) { return; }
		if (agent == null) { return; }
		if (unique) {
			rootIndex.remove(previous, agent);
			return;
		}
		final ISpatialIndex si = findSpatialIndex(agent.getPopulation());
		if (si != null) {
			si.remove(previous, agent);
		}
	}

	private Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final IAgentFilter filter,
			final ISpatialIndex index, final int number, final Collection<IAgent> alreadyChosen) {
		try (final ICollector<IAgent> closestEnt = Collector.getList()) {
			closestEnt.addAll(alreadyChosen);
			for (final double step : steps) {
				final Collection<IAgent> firsts =
						index.firstAtDistance(scope, source, step, filter, number - closestEnt.size(), closestEnt);
				if (firsts.isEmpty()) {
					continue;
				}
				closestEnt.addAll(firsts);
				if (closestEnt.size() == number) { return closestEnt.items(); }
			}
			return closestEnt.items();
		}
	}

	private IAgent firstAtDistance(final IScope scope, final IShape source, final IAgentFilter filter,
			final ISpatialIndex index) {
		for (final double step : steps) {
			final IAgent first = index.firstAtDistance(scope, source, step, filter);
			if (first != null) { return first; }
		}
		return null;
	}

	private Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final IAgentFilter filter,
			final int number, final Collection<IAgent> alreadyChosen) {
		if (disposed) { return null; }
		final List<IAgent> shapes = new ArrayList<>(alreadyChosen);
		for (final double step : steps) {
			for (final ISpatialIndex si : getAllSpatialIndexes()) {
				final Collection<IAgent> firsts = si.firstAtDistance(scope, source, step, filter, number, shapes);
				shapes.addAll(firsts);
			}
			if (shapes.size() >= number) {
				break;
			}
		}

		if (shapes.size() <= number) { return shapes; }
		scope.getRandom().shuffleInPlace(shapes);
		final Ordering<IShape> ordering = Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
		return ordering.leastOf(shapes, number);
	}

	private IAgent firstAtDistance(final IScope scope, final IShape source, final IAgentFilter filter) {
		if (disposed) { return null; }
		try (final Collector.AsList<IAgent> shapes = Collector.getList()) {
			for (final double step : steps) {
				for (final ISpatialIndex si : getAllSpatialIndexes()) {
					final IAgent first = si.firstAtDistance(scope, source, step, filter);
					if (first != null) {
						shapes.add(first);
					}
				}
				if (!shapes.isEmpty()) {
					break;
				}
			}
			if (shapes.items().size() == 1) { return shapes.items().get(0); }
			// Adresses Issue 722 by shuffling the returned list using GAMA random
			// procedure
			shapes.shuffleInPlaceWith(scope.getRandom());
			double min_dist = Double.MAX_VALUE;
			IAgent min_agent = null;
			for (final IAgent s : shapes) {
				final double dd = source.euclidianDistanceTo(s);
				if (dd < min_dist) {
					min_dist = dd;
					min_agent = s;
				}
			}
			return min_agent;
		}

	}

	@Override
	public Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f, final int number, final Collection<IAgent> alreadyChosen) {
		// TODO -- Verify : dist not taken into account here. Normal ?
		final IPopulation<? extends IAgent> pop = f.getPopulation(scope);
		if (pop == null) { return firstAtDistance(scope, source, f, number, alreadyChosen); }
		final ISpatialIndex id = findSpatialIndex(pop);
		if (id != null) { return firstAtDistance(scope, source, f, id, number, alreadyChosen); }
		return firstAtDistance(scope, source, f, number, alreadyChosen);
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		// TODO -- Verify : dist not taken into account here. Normal ?
		final IPopulation<? extends IAgent> pop = f.getPopulation(scope);
		if (pop == null) { return firstAtDistance(scope, source, f); }
		final ISpatialIndex id = findSpatialIndex(pop);
		if (id != null) { return firstAtDistance(scope, source, f, id); }
		return firstAtDistance(scope, source, f);
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f) {
		if (disposed) { return Collections.EMPTY_LIST; }
		if (unique) { return rootIndex.allAtDistance(scope, source, dist, f); }
		final ISpatialIndex id = findSpatialIndex(f.getPopulation(scope));
		if (id == rootIndex) {
			try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
				for (final ISpatialIndex si : getAllSpatialIndexes()) {
					agents.addAll(si.allAtDistance(scope, source, dist, f));
				}
				return agents.items();
			}
		}
		return id.allAtDistance(scope, source, dist, f);
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		if (disposed) { return Collections.EMPTY_LIST; }
		if (unique) { return rootIndex.allInEnvelope(scope, source, envelope, f, contained); }
		final ISpatialIndex id = findSpatialIndex(f.getPopulation(scope));
		if (id == rootIndex) {
			try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
				for (final ISpatialIndex si : getAllSpatialIndexes()) {
					agents.addAll(si.allInEnvelope(scope, source, envelope, f, contained));
				}
				agents.shuffleInPlaceWith(scope.getRandom());
				return agents.items();
			}
		}
		return id.allInEnvelope(scope, source, envelope, f, contained);
	}

	@Override
	public void add(final ISpatialIndex index, final IPopulation<? extends IAgent> species) {
		if (disposed) { return; }
		if (index == null) { return; }
		if (spatialIndexes == null) {
			spatialIndexes = GamaMapFactory.create();
		}
		spatialIndexes.put(species, index);
		uniqueIndexes.add(index);
		unique = false;
	}

	@Override
	public void remove(final IPopulation<? extends IAgent> species) {
		if (disposed) { return; }
		final ISpatialIndex index = spatialIndexes != null ? spatialIndexes.remove(species) : null;
		if (index != null) {
			uniqueIndexes.remove(index);
		}
	}

	@Override
	public void dispose() {
		if (spatialIndexes != null) {
			spatialIndexes.clear();
		}
		uniqueIndexes.clear();
		rootIndex = null;
		disposed = true;
	}

	@Override
	public void updateQuadtree(final Envelope envelope) {
		GamaQuadTree tree = rootIndex;
		final Collection<IAgent> agents = tree.allAgents();
		final boolean parallel = tree.isParallel();
		tree.dispose();
		tree = GamaQuadTree.create(envelope, parallel);
		rootIndex = tree;
		for (final IAgent a : agents) {
			tree.insert(a);
		}

	}

	@Override
	public Collection<IAgent> allAgents() {
		if (unique) { return rootIndex.allAgents(); }
		try (final ICollector<IAgent> set = Collector.getOrderedSet()) {
			for (final ISpatialIndex i : getAllSpatialIndexes()) {
				set.addAll(i.allAgents());
			}
			return set.items();
		}
	}

	public Collection<ISpatialIndex> getAllSpatialIndexes() {
		return uniqueIndexes;
	}

	@Override
	public void mergeWith(final Compound comp) {
		final CompoundSpatialIndex other = (CompoundSpatialIndex) comp;
		if(null==other) return;
		if(null==other.spatialIndexes) { 
			other.spatialIndexes = GamaMapFactory.create();  
//			return;
		}
		other.spatialIndexes.forEach((species, index) -> {
			if (index != other.rootIndex) {
				add(index, species);
			}
		});
		other.dispose();
	}

	@Override
	public boolean isParallel() {
		return rootIndex.isParallel();
	}

}
