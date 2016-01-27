/*********************************************************************************************
 *
 *
 * 'CompoundSpatialIndex.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology;

import java.util.*;
import com.vividsolutions.jts.geom.Envelope;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gaml.species.ISpecies;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	boolean disposed = false;
	ISpatialIndex[] all;
	final TObjectIntHashMap<ISpecies> indexes;
	final protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds) {
		indexes = new TObjectIntHashMap(10, 0.75f, -1);
		// noEntryValue is 0 by default
		all = new ISpatialIndex[] { new GamaQuadTree(bounds) };
		final double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest, biggest * Math.sqrt(2) };
	}

	private ISpatialIndex findSpatialIndex(final ISpecies s) {
		if ( disposed ) { return null; }
		int index = indexes.get(s);
		if ( index == -1 ) {
			index = 0;
			indexes.put(s, 0);
		}
		return all[index];
	}

	// Returns the index of the spatial index to use. Return -1 if all spatial indexes are concerned
	private int findSpatialIndexes(final IAgentFilter f) {
		if ( disposed ) { return -1; }
		ISpecies s = f.getSpecies();
		return indexes.get(s);

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
		if ( disposed ) { return null; }
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
		scope.getRandom().shuffle(shapes);
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

	// private IAgent closestToAmongIndexes(final IScope scope, final IShape source, final IAgentFilter filter) {
	// if ( disposed ) { return null; }
	// final List<IAgent> shapes = new ArrayList();
	//
	// for ( final ISpatialIndex si : all ) {
	// // TODO Not optimized as an agent can be found in a spatial index, farther than one in another
	// final IAgent first = si.closestTo(scope, source, filter);
	// if ( first != null ) {
	// shapes.add(first);
	// }
	// }
	//
	// if ( shapes.size() == 1 ) { return shapes.get(0); }
	// // Adresses Issue 722 by shuffling the returned list using GAMA random procedure
	// scope.getRandom().shuffle(shapes);
	// double min_dist = Double.MAX_VALUE;
	// IAgent min_agent = null;
	// for ( final IAgent s : shapes ) {
	// final double dd = source.euclidianDistanceTo(s);
	// if ( dd < min_dist ) {
	// min_dist = dd;
	// min_agent = s;
	// }
	// }
	// return min_agent;
	//
	// }

	// @Override
	// public IAgent closestTo(final IScope scope, final IShape source, final IAgentFilter f) {
	// final int id = findSpatialIndexes(f);
	// if ( id != -1 ) {
	// return all[id].closestTo(scope, source, f);
	// } else {
	// return closestToAmongIndexes(scope, source, f);
	// }
	// }

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
		if ( disposed ) { return Collections.EMPTY_LIST; }
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
		if ( disposed ) { return Collections.EMPTY_LIST; }
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

	// @Override
	// public void drawOn(final Graphics2D g2, final int width, final int height) {
	// // By default, we draw the quadtree
	// if ( !disposed ) {
	// all[0].drawOn(g2, width, height);
	// }
	// }

	@Override
	public void add(final ISpatialIndex index, final ISpecies species) {
		if ( disposed ) { return; }
		all = Arrays.copyOf(all, all.length + 1);
		all[all.length - 1] = index;
		indexes.put(species, all.length - 1);
	}

	@Override
	public void dispose() {
		indexes.clear();
		Arrays.fill(all, null);
		all = null;
		disposed = true;
	}

}
