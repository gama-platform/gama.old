
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
import java.util.concurrent.TimeUnit;

import org.locationtech.jts.geom.Envelope;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	boolean disposed = false;
	private final Cache<IPopulation<? extends IAgent>, ISpatialIndex> spatialIndexes =
			CacheBuilder.newBuilder().expireAfterAccess(180, TimeUnit.SECONDS).build();
	private Envelope bounds;
	private boolean parallel;
	final protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds, final boolean parallel) {
		this.bounds = bounds;
		this.parallel = parallel;
		final double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 100, biggest / 50, biggest / 20, biggest / 10, biggest / 2, biggest,
				biggest * Math.sqrt(2) };
	}

	@Override
	public void insert(final IAgent agent) {
		if (disposed || agent == null) return;
		IPopulation<? extends IAgent> pop = agent.getPopulation();
		ISpatialIndex index = spatialIndexes.getIfPresent(pop);
		if (index == null && !GamaPreferences.External.QUADTREE_OPTIMIZATION.getValue()) { index = add(pop, false); }
		if (index != null) { index.insert(agent); }
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		if (disposed || agent == null) return;
		ISpatialIndex index = spatialIndexes.getIfPresent(agent.getPopulation());
		if (index != null) { index.remove(previous, agent); }
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		if (disposed) return null;
		ISpatialIndex index = add(scope, f);
		if (index == null) {
			try (final Collector.AsList<IAgent> shapes = Collector.getList()) {
				for (final double step : steps) {
					for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
						final IAgent first = si.firstAtDistance(scope, source, step, f);
						if (first != null) { shapes.add(first); }
					}
					if (!shapes.isEmpty()) { break; }
				}
				if (shapes.items().size() == 1) return shapes.items().get(0);
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
		for (final double step : steps) {
			IAgent first = index.firstAtDistance(scope, source, step, f);
			if (first != null) return first;
		}
		return null;
	}

	private Collection<IAgent> nFirstAtDistanceInAllSpatialIndexes(final IScope scope, final IShape source,
			final IAgentFilter filter, final int number, final Collection<IAgent> alreadyChosen) {
		if (disposed) return null;
		final List<IAgent> shapes = new ArrayList<>(alreadyChosen);
		for (final double step : steps) {
			for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
				final Collection<IAgent> firsts = si.firstAtDistance(scope, source, step, filter, number, shapes);
				shapes.addAll(firsts);
			}
			if (shapes.size() >= number) { break; }
		}

		if (shapes.size() <= number) return shapes;
		scope.getRandom().shuffleInPlace(shapes);
		final Ordering<IShape> ordering = Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
		return ordering.leastOf(shapes, number);
	}

	private Collection<IAgent> nFirstAtDistanceInSpatialIndex(final IScope scope, final IShape source,
			final IAgentFilter filter, final int number, final Collection<IAgent> alreadyChosen,
			final ISpatialIndex index) {
		try (final ICollector<IAgent> closestEnt = Collector.getList()) {
			closestEnt.addAll(alreadyChosen);
			for (final double step : steps) {
				final Collection<IAgent> firsts =
						index.firstAtDistance(scope, source, step, filter, number - closestEnt.size(), closestEnt);
				if (firsts.isEmpty()) { continue; }
				closestEnt.addAll(firsts);
				if (closestEnt.size() == number) return closestEnt.items();
			}
			return closestEnt.items();
		}
	}

	@Override
	public Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f, final int number, final Collection<IAgent> alreadyChosen) {
		if (disposed) return null;
		ISpatialIndex index = add(scope, f);
		if (index != null) return nFirstAtDistanceInSpatialIndex(scope, source, f, number, alreadyChosen, index);
		return nFirstAtDistanceInAllSpatialIndexes(scope, source, f, number, alreadyChosen);
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		if (disposed) return Collections.EMPTY_LIST;
		ISpatialIndex index = add(scope, f);
		if (index != null) return index.allInEnvelope(scope, source, envelope, f, contained);
		try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
			for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
				agents.addAll(si.allInEnvelope(scope, source, envelope, f, contained));
			}
			agents.shuffleInPlaceWith(scope.getRandom());
			return agents.items();
		}
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f) {
		if (disposed) return Collections.EMPTY_LIST;
		ISpatialIndex index = add(scope, f);
		if (index != null) return index.allAtDistance(scope, source, dist, f);
		try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
			for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
				agents.addAll(si.allAtDistance(scope, source, dist, f));
			}
			agents.shuffleInPlaceWith(scope.getRandom());
			return agents.items();
		}
	}

	@Override
	public void dispose() {
		if (disposed) return;
		disposed = true;
		spatialIndexes.invalidateAll();
	}

	private ISpatialIndex add(final IPopulation<? extends IAgent> pop, final boolean insertAgents) {
		if (disposed) return null;
		ISpatialIndex index = spatialIndexes.getIfPresent(pop);
		if (index == null) {
			if (pop.isGrid()) {
				index = ((GridPopulation) pop).getTopology().getPlaces();
			} else {
				index = GamaQuadTree.create(bounds, parallel);
			}
			spatialIndexes.put(pop, index);
			if (insertAgents) {
				for (final IAgent ag : pop) {
					index.insert(ag);
				}
			}
		}
		return index;
	}

	private ISpatialIndex add(final IScope scope, final IAgentFilter filter) {
		if (filter == null) return null;
		IPopulation<? extends IAgent> pop = filter.getPopulation(scope);
		if (pop == null ||
				// spatial indices only work for species with no subspecies
				!filter.getSpecies().getSubSpecies(scope).isEmpty()) {  
			return null;
		}
		return add(pop, true);
	}

	@Override
	public void remove(final IPopulation<? extends IAgent> pop) {
		spatialIndexes.invalidate(pop);
	}

	@Override
	public void update(final Envelope envelope, final boolean parallel) {
		this.bounds = envelope;
		this.parallel = parallel;
		for (IPopulation<? extends IAgent> pop : spatialIndexes.asMap().keySet()) {
			remove(pop);
			add(pop, true);
		}
	}

	@Override
	public void mergeWith(final Compound spatialIndex) {
		final CompoundSpatialIndex other = (CompoundSpatialIndex) spatialIndex;
		if (null == other) return;
		other.spatialIndexes.asMap().forEach((species, index) -> {
			spatialIndexes.put(species, index);
		});
		spatialIndex.dispose();
	}

}
