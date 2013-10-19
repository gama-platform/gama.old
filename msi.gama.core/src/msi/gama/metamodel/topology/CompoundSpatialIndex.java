package msi.gama.metamodel.topology;

import gnu.trove.set.hash.THashSet;
import java.awt.Graphics2D;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.runtime.IScope;
import msi.gaml.species.ISpecies;
import com.vividsolutions.jts.geom.Envelope;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	Set<ISpatialIndex> all;
	ISpatialIndex quadtree;
	Map<ISpecies, ISpatialIndex> indexes;
	protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds) {
		quadtree = new GamaQuadTree(bounds);
		indexes = new HashMap();
		all = new THashSet();
		all.add(quadtree);
		final double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest };
	}

	private ISpatialIndex findSpatialIndex(final IAgent a) {
		final ISpecies s = a.getSpecies();
		ISpatialIndex si = indexes.get(s);
		if ( si == null ) {
			if ( s.isGrid() ) {
				si = (IGrid) a.getTopology().getPlaces();
			} else {
				si = quadtree;
			}
			all.add(si);
			indexes.put(s, si);
		}
		return si;
	}

	private Collection<ISpatialIndex> findSpatialIndexes(final IAgentFilter f) {
		final ISpatialIndex si = indexes.get(f.speciesFiltered());
		return si == null ? all : Collections.singleton(si);
	}

	private IAgent findClosest(final IShape source, final Set<IAgent> shapes) {
		if ( shapes.size() == 1 ) { return shapes.iterator().next(); }
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
	public void insert(final IAgent a) {
		if ( a == null ) { return; }
		final ISpatialIndex si = findSpatialIndex(a);
		if ( si != null ) {
			si.insert(a);
		}
	}

	@Override
	public void remove(final IShape previous, final IAgent o) {
		final IAgent a = o.getAgent();
		if ( a == null ) { return; }
		final ISpatialIndex si = findSpatialIndex(a);
		if ( si != null ) {
			si.remove(previous, o);
		}
	}

	@Override
	public Set<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		Set<IAgent> agents = new THashSet();
		for ( ISpatialIndex si : findSpatialIndexes(f) ) {
			agents.addAll(si.allAtDistance(scope, source, dist, f));
		}

		return agents;
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		// TODO -- Verify : dist not taken into account here. Normal ?
		final Collection<ISpatialIndex> list = findSpatialIndexes(f);
		final Set<IAgent> shapes = new THashSet();
		for ( int i = 0; i < steps.length; i++ ) {
			for ( final ISpatialIndex si : list ) {
				final IAgent first = si.firstAtDistance(scope, source, steps[i], f);
				if ( first != null ) {
					shapes.add(first);
				}
			}
			if ( !shapes.isEmpty() ) {
				break;
			}
		}
		return findClosest(source, shapes);
	}

	@Override
	public Set<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
		final IAgentFilter f, final boolean contained) {
		Set<IAgent> result = new THashSet();
		for ( ISpatialIndex si : findSpatialIndexes(f) ) {
			result.addAll(si.allInEnvelope(scope, source, envelope, f, contained));
		}
		return result;
	}

	@Override
	public void drawOn(final Graphics2D g2, final int width, final int height) {
		// By default, we draw the quadtree
		quadtree.drawOn(g2, width, height);
	}

	@Override
	public void add(final ISpatialIndex index, final ISpecies species) {
		all.add(index);
		indexes.put(species, index);
	}

	@Override
	public void dispose() {
		// GuiUtils.debug("CompoundSpatialIndex.dispose");
		quadtree = null;
		indexes.clear();
		all.clear();
	}

}
