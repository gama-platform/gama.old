/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.CompoundSpatialIndex.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	boolean disposed = false;
	private final Map<IPopulation<? extends IAgent>, ISpatialIndex> spatialIndexes;
	private final Set<ISpatialIndex> uniqueIndexes;
	private ISpatialIndex rootIndex;
	final protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds, final boolean parallel) {
		spatialIndexes = new HashMap<>();
		rootIndex = GamaQuadTree.create(bounds, parallel);
		uniqueIndexes = Sets.newHashSet(rootIndex);
		final double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest, biggest * Math.sqrt(2) };
	}

	private ISpatialIndex findSpatialIndex(final IPopulation<? extends IAgent> s) {
		if (disposed) { return null; }
		final ISpatialIndex index = spatialIndexes.get(s);
		return index == null ? rootIndex : index;
	}

	@Override
	public void insert(final IAgent a) {
		if (a == null) { return; }
		final ISpatialIndex si = findSpatialIndex(a.getPopulation());
		if (si != null) {
			si.insert(a);
		}
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent o) {
		final IAgent a = o.getAgent();
		if (a == null) { return; }
		final ISpatialIndex si = findSpatialIndex(a.getPopulation());
		if (si != null) {
			si.remove(previous, o);
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
		scope.getRandom().shuffle(shapes);
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
		final ISpatialIndex id = findSpatialIndex(f.getPopulation(scope));
		if (id == rootIndex) {
			try (final ICollector<IAgent> agents = Collector.getSet()) {
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
		final ISpatialIndex id = findSpatialIndex(f.getPopulation(scope));
		if (id == rootIndex) {
			try (final ICollector<IAgent> agents = Collector.getSet()) {
				for (final ISpatialIndex si : getAllSpatialIndexes()) {
					agents.addAll(si.allInEnvelope(scope, source, envelope, f, contained));
				}
				return agents.items();
			}
		}
		return id.allInEnvelope(scope, source, envelope, f, contained);
	}

	@Override
	public void add(final ISpatialIndex index, final IPopulation<? extends IAgent> species) {
		if (disposed) { return; }
		if (index == null) { return; }
		spatialIndexes.put(species, index);
		uniqueIndexes.add(index);
	}

	@Override
	public void remove(final IPopulation<? extends IAgent> species) {
		if (disposed) { return; }
		final ISpatialIndex index = spatialIndexes.remove(species);
		if (index != null) {
			uniqueIndexes.remove(index);
		}
	}

	@Override
	public void dispose() {
		spatialIndexes.clear();
		uniqueIndexes.clear();
		rootIndex = null;
		disposed = true;
	}

	@Override
	public void updateQuadtree(final Envelope envelope) {
		ISpatialIndex tree = rootIndex;
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
