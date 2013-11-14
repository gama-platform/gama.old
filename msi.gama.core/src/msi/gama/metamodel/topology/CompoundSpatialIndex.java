package msi.gama.metamodel.topology;

import gnu.trove.set.hash.THashSet;
import java.awt.Graphics2D;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.*;
import msi.gaml.species.ISpecies;
import com.vividsolutions.jts.geom.Envelope;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	ISpatialIndex[] all;
	final Map<ISpecies, Integer> indexes;
	final protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds) {
		indexes = new HashMap();
		all = new ISpatialIndex[] { new GamaQuadTree(bounds) };
		final double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest };
	}

	private ISpatialIndex findSpatialIndex(final ISpecies s) {
		if ( all == null ) { return null; }
		Integer index = indexes.get(s);
		if ( index == null ) {
			indexes.put(s, 0);
			return all[0];
		} else {
			return all[index];
		}
	}

	// Returns the index of the spatial index to use. Return -1 if all spatial indexes are concerned
	private int findSpatialIndexes(final IAgentFilter f) {
		final Integer si = indexes.get(f.speciesFiltered());
		return si == null ? -1 : si;
	}

	@Override
	public void insert(final IAgent a) {
		if ( a == null ) { return; }
		final ISpatialIndex si = findSpatialIndex(a.getSpecies());
		if ( si != null ) {
			si.insert(a);
		}
	}

	@Override
	public void remove(final IShape previous, final IAgent o) {
		final IAgent a = o.getAgent();
		if ( a == null ) { return; }
		final ISpatialIndex si = findSpatialIndex(a.getSpecies());
		if ( si != null ) {
			si.remove(previous, o);
		}
	}

	private IAgent firstAtDistance(final IScope scope, final IShape source, final IAgentFilter filter,
		final ISpatialIndex index) {
		for ( int i = 0; i < steps.length; i++ ) {
			IAgent first = index.firstAtDistance(scope, source, steps[i], filter);
			if ( first != null ) { return first; }
		}
		return null;
	}

	private IAgent firstAtDistance(final IScope scope, final IShape source, final IAgentFilter filter) {
		final List<IAgent> shapes = new ArrayList();
		for ( int i = 0; i < steps.length; i++ ) {
			for ( final ISpatialIndex si : all ) {
				final IAgent first = si.firstAtDistance(scope, source, steps[i], filter);
				if ( first != null ) {
					shapes.add(first);
				}
			}
			if ( !shapes.isEmpty() ) {
				break;
			}
		}
		if ( shapes.size() == 1 ) { return shapes.get(0); }
		// Adresses Issue 722 by shuffling the returned list using GAMA random procedure
		GAMA.getRandom().shuffle(shapes);
		double min_dist = Double.MAX_VALUE;
		IAgent min_agent = null;
		for ( final IAgent s : shapes ) {
			final double dd = source.euclidianDistanceTo(s);
			if ( dd < min_dist ) {
				min_dist = dd;
				min_agent = s;
			}
		}
		return min_agent;

	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		// TODO -- Verify : dist not taken into account here. Normal ?
		final int id = findSpatialIndexes(f);
		if ( id != -1 ) {
			return firstAtDistance(scope, source, f, all[id]);
		} else {
			return firstAtDistance(scope, source, f);
		}
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
		final IAgentFilter f) {
		int id = findSpatialIndexes(f);
		if ( id == -1 ) {
			Set<IAgent> agents = new THashSet();
			for ( ISpatialIndex si : all ) {
				agents.addAll(si.allAtDistance(scope, source, dist, f));
			}
			return agents;
		}
		return all[id].allAtDistance(scope, source, dist, f);
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
		final IAgentFilter f, final boolean contained) {
		int id = findSpatialIndexes(f);
		if ( id == -1 ) {
			Set<IAgent> agents = new THashSet();
			for ( ISpatialIndex si : all ) {
				agents.addAll(si.allInEnvelope(scope, source, envelope, f, contained));
			}
			return agents;
		}
		return all[id].allInEnvelope(scope, source, envelope, f, contained);
	}

	@Override
	public void drawOn(final Graphics2D g2, final int width, final int height) {
		// By default, we draw the quadtree
		all[0].drawOn(g2, width, height);
	}

	@Override
	public void add(final ISpatialIndex index, final ISpecies species) {
		all = Arrays.copyOf(all, all.length + 1);
		all[all.length - 1] = index;
		indexes.put(species, all.length - 1);
	}

	@Override
	public void dispose() {
		indexes.clear();
		Arrays.fill(all, null);
		all = null;
	}

}
