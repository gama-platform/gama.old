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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import org.locationtech.jts.geom.Envelope;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gaml.operators.fastmaths.FastMath;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	boolean disposed = false;
	private final Map<IPopulation<? extends IAgent>, ISpatialIndex> spatialIndexes;
	private final Set<ISpatialIndex> uniqueIndexes;
	private ISpatialIndex rootIndex;
	final protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds) {
		spatialIndexes = new HashMap<>();
		rootIndex = newRootIndex(bounds);
		uniqueIndexes = Sets.newHashSet(rootIndex);
		final double biggest = FastMath.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest, biggest * FastMath.sqrt(2) };
	}

	private ISpatialIndex findSpatialIndex(final IPopulation<? extends IAgent> s) {
		if (disposed) { return null; }
		final ISpatialIndex index = spatialIndexes.get(s);
		return index == null ? rootIndex : index;
	}

	private ISpatialIndex newRootIndex(final Envelope bounds) {
		return GamaPreferences.External.OTHER_SPATIAL_INDEX.getValue() ? FixedGridSpatialIndex.create(bounds)
				: GamaQuadTree.create(bounds);
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
	public void remove(final IEnvelope previous, final IAgent o) {
		final IAgent a = o.getAgent();
		if (a == null) { return; }
		final ISpatialIndex si = findSpatialIndex(a.getPopulation());
		if (si != null) {
			si.remove(previous, o);
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

	private IAgent firstAtDistance(final IScope scope, final IShape source, final IAgentFilter filter) {
		if (disposed) { return null; }
		final List<IAgent> shapes = new ArrayList<>();
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
		if (shapes.size() == 1) { return shapes.get(0); }
		// Adresses Issue 722 by shuffling the returned list using GAMA random
		// procedure
		scope.getRandom().shuffleInPlace(shapes);
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

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		// TODO -- Verify : dist not taken into account here. Normal ?
		final ISpatialIndex id = findSpatialIndex(f.getPopulation(scope));
		if (id != null) { return firstAtDistance(scope, source, f, id); }
		return firstAtDistance(scope, source, f);
	}

	@Override
	public void allAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f,
			final Set<IAgent> accumulator) {
		if (disposed) { return; }
		final ISpatialIndex id = findSpatialIndex(f.getPopulation(scope));
		if (id == rootIndex) {
			for (final ISpatialIndex si : getAllSpatialIndexes()) {
				si.allAtDistance(scope, source, dist, f, accumulator);
			}
		}
		id.allAtDistance(scope, source, dist, f, accumulator);
	}

	@Override
	public void allInEnvelope(final IScope scope, final IShape source, final IEnvelope envelope, final IAgentFilter f,
			final boolean contained, final Set<IAgent> accumulator) {
		if (disposed) { return; }
		final ISpatialIndex id = findSpatialIndex(f.getPopulation(scope));
		if (id == rootIndex) {
			for (final ISpatialIndex si : getAllSpatialIndexes()) {
				si.allInEnvelope(scope, source, envelope, f, contained, accumulator);
			}
			return;
		}
		id.allInEnvelope(scope, source, envelope, f, contained, accumulator);
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
		rootIndex.dispose();
		spatialIndexes.clear();
		uniqueIndexes.clear();
		rootIndex = null;
		disposed = true;
	}

	@Override
	public void updateQuadtree(final Envelope envelope) {
		final Collection<IAgent> agents = rootIndex.allAgents();
		rootIndex.dispose();
		rootIndex = newRootIndex(envelope);
		for (final IAgent a : agents) {
			rootIndex.insert(a);
		}

	}

	@Override
	public Collection<IAgent> allAgents() {
		final Set<IAgent> set = Sets.newLinkedHashSet();
		for (final ISpatialIndex i : getAllSpatialIndexes()) {
			set.addAll(i.allAgents());
		}
		return set;
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

}
