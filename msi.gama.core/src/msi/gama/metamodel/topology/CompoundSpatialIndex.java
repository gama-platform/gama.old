
/*******************************************************************************************************
 *
 * CompoundSpatialIndex.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static java.util.Collections.singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import org.locationtech.jts.geom.Envelope;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;

/**
 * The Class CompoundSpatialIndex.
 */
public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	/** The disposed. */
	boolean disposed = false;

	/** The spatial indexes. */

	private final WeakHashMap<ISpecies, ISpatialIndex> spatialIndexes = new WeakHashMap<>();
	// private final Cache<IPopulation<? extends IAgent>, ISpatialIndex> spatialIndexes =
	// newBuilder().weakKeys().weakValues().build();

	/** The bounds. */
	private Envelope bounds;

	/** The parallel. */
	private boolean parallel;

	/** The steps. */
	final protected double[] steps;

	/**
	 * The cached species indices. Keeps a correspondance between species and the spatial indices to use to look for
	 * agents. Used when passing a list of agents with a common species (and not a population)
	 */
	private final WeakHashMap<ISpecies, Iterable<ISpatialIndex>> cachedSpeciesIndices = new WeakHashMap<>();

	/**
	 * Instantiates a new compound spatial index.
	 *
	 * @param bounds
	 *            the bounds
	 * @param parallel
	 *            the parallel
	 */
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
		ISpatialIndex index = spatialIndexes.getOrDefault(pop.getSpecies(), null);
		if (index == null && !GamaPreferences.Experimental.QUADTREE_OPTIMIZATION.getValue()) {
			index = add(pop, false);
		}
		if (index != null) { index.insert(agent); }
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		if (disposed || agent == null) return;
		ISpatialIndex index = spatialIndexes.getOrDefault(agent.getSpecies(), null);
		if (index != null) { index.remove(previous, agent); }
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		if (disposed) return null;
		Iterable<ISpatialIndex> indices = add(scope, f);
		try (final Collector.AsList<IAgent> shapes = Collector.getList()) {
			for (final double step : steps) {
				for (final ISpatialIndex si : indices) {
					if (si == null) { continue; }
					final IAgent first = si.firstAtDistance(scope, source, step, f);
					if (first != null) { shapes.add(first); }
				}
				if (!shapes.isEmpty()) { break; }
			}
			int size = shapes.items().size();
			if (size == 0) return null;
			if (size == 1) return shapes.items().get(0);
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

	/**
	 * N first at distance in all spatial indexes.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param filter
	 *            the filter
	 * @param number
	 *            the number
	 * @param alreadyChosen
	 *            the already chosen
	 * @return the collection
	 */
	private Collection<IAgent> nFirstAtDistanceInSpatialIndexes(final IScope scope, final IShape source,
			final IAgentFilter filter, final int number, final Collection<IAgent> alreadyChosen,
			final Iterable<ISpatialIndex> indices) {
		if (disposed) return null;
		final List<IAgent> shapes = new ArrayList<>(alreadyChosen);
		for (final double step : steps) {
			for (final ISpatialIndex si : indices) {
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

	@Override
	public Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f, final int number, final Collection<IAgent> alreadyChosen) {
		if (disposed) return null;
		Iterable<ISpatialIndex> indices = add(scope, f);
		// if (index != null) return nFirstAtDistanceInSpatialIndex(scope, source, f, number, alreadyChosen, index);
		return nFirstAtDistanceInSpatialIndexes(scope, source, f, number, alreadyChosen, indices);
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		if (disposed) return Collections.EMPTY_LIST;
		Iterable<ISpatialIndex> indices = add(scope, f);
		try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
			for (final ISpatialIndex si : indices) {
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
		Iterable<ISpatialIndex> indices = add(scope, f);
		try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
			for (final ISpatialIndex si : indices) {
				if (si != null) { agents.addAll(si.allAtDistance(scope, source, dist, f)); }
			}
			agents.shuffleInPlaceWith(scope.getRandom());
			return agents.items();
		}
	}

	@Override
	public void dispose() {
		if (disposed) return;
		disposed = true;
		spatialIndexes.clear();
	}

	/**
	 * Adds the.
	 *
	 * @param species
	 *            the pop
	 * @param insertAgents
	 *            the insert agents
	 * @return the i spatial index
	 */
	private ISpatialIndex add(final IScope scope, final ISpecies species, final boolean insertAgents) {
		if (disposed || species == null) return null;
		return add(species.getPopulation(scope), insertAgents);
	}

	/**
	 * Adds the.
	 *
	 * @param pop
	 *            the pop
	 * @param insertAgents
	 *            the insert agents
	 * @return the i spatial index
	 */
	private ISpatialIndex add(final IPopulation<? extends IAgent> pop, final boolean insertAgents) {
		if (disposed || pop == null) return null;
		ISpecies spec = pop.getSpecies();
		ISpatialIndex index = spatialIndexes.getOrDefault(spec, null);
		if (index == null) {
			if (pop.isGrid()) {
				index = ((GridPopulation) pop).getTopology().getPlaces();
			} else {
				index = GamaQuadTree.create(bounds, parallel);
			}
			spatialIndexes.put(spec, index);
			if (insertAgents) { for (final IAgent ag : pop) { index.insert(ag); } }
		}
		return index;
	}

	/**
	 * Verifies that all the populations covered by the filter have been added to the index and returns the list of
	 * corresponding i
	 *
	 * @param scope
	 *            the scope
	 * @param filter
	 *            the filter
	 * @return the iterable
	 */
	private Iterable<ISpatialIndex> add(final IScope scope, final IAgentFilter filter) {
		if (filter instanceof IPopulationSet) return transform(
				(Collection<IPopulation<? extends IAgent>>) ((IPopulationSet) filter).getPopulations(scope),
				each -> add(each, true));
		ISpecies species = filter.getSpecies();
		if (species == null || species.getDescription() == Types.AGENT.getSpecies()) return spatialIndexes.values();
		if (!cachedSpeciesIndices.containsKey(species)) {
			cachedSpeciesIndices.put(species,
					transform(concat(singleton(species), species.getSubSpecies(scope)), s -> add(scope, s, true)));
		}
		return cachedSpeciesIndices.get(species);
	}

	/**
	 * Removes the.
	 *
	 * @param species
	 *            the species
	 */
	@Override
	public void remove(final ISpecies species) {
		spatialIndexes.remove(species);
	}

	@Override
	public void update(final IScope scope, final Envelope envelope, final boolean parallel) {
		this.bounds = envelope;
		this.parallel = parallel;

		final WeakHashMap<ISpecies, ISpatialIndex> spatialIndexesTmp = new WeakHashMap<>();
		spatialIndexesTmp.putAll(spatialIndexes);

		for (ISpecies species : spatialIndexesTmp.keySet()) {
			remove(species);
			add(scope, species, true);
		}
	}

	@Override
	public void mergeWith(final Compound spatialIndex) {
		final CompoundSpatialIndex other = (CompoundSpatialIndex) spatialIndex;
		if (null == other) return;
		other.spatialIndexes.forEach((species, index) -> { spatialIndexes.put(species, index); });
		spatialIndex.dispose();
	}

}