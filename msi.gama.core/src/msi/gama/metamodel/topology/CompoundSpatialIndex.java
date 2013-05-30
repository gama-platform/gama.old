package msi.gama.metamodel.topology;

import java.awt.Graphics2D;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.vividsolutions.jts.geom.Envelope;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	Set<ISpatialIndex> all;
	ISpatialIndex quadtree;
	Map<ISpecies, ISpatialIndex> indexes;
	protected double[] steps;

	public CompoundSpatialIndex(final Envelope bounds) {
		quadtree = new GamaQuadTree(bounds);
		indexes = new HashMap();
		all = new HashSet();
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

	private Iterator<ISpatialIndex> findSpatialIndexes(final IAgentFilter f) {
		final ISpatialIndex si = indexes.get(f.speciesFiltered());
		return si == null ? all.iterator() : Iterators.singletonIterator(si);
	}

	private IShape findClosest(final IShape source, final List<IShape> shapes) {
		if ( shapes.size() == 1 ) { return shapes.get(0); }
		double min_dist = Double.MAX_VALUE;
		IShape min_agent = null;
		for ( final IShape s : shapes ) {
			final double dd = source.euclidianDistanceTo(s);
			if ( dd < min_dist ) {
				min_dist = dd;
				min_agent = s;
			}
		}
		return min_agent;
	}

	@Override
	public void insert(final IShape o) {
		final IAgent a = o.getAgent();
		if ( a == null ) { return; }
		final ISpatialIndex si = findSpatialIndex(a);
		if ( si != null ) {
			si.insert(o);
		}
	}

	@Override
	public void remove(final IShape previous, final IShape o) {
		final IAgent a = o.getAgent();
		if ( a == null ) { return; }
		final ISpatialIndex si = findSpatialIndex(a);
		if ( si != null ) {
			si.remove(previous, o);
		}
	}

	@Override
	public Iterator<IShape> allAtDistance(final IShape source, final double dist, final IAgentFilter f) {

		return Iterators.concat(Iterators.transform(findSpatialIndexes(f),
			new Function<ISpatialIndex, Iterator<IShape>>() {

				@Override
				public Iterator<IShape> apply(final ISpatialIndex input) {
					return input.allAtDistance(source, dist, f);
				}
			}));
	}

	@Override
	public IShape firstAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		// TODO -- Verify : dist not taken into account here. Normal ?
		final Iterator<ISpatialIndex> sis = findSpatialIndexes(f);
		final List<ISpatialIndex> list = ImmutableList.copyOf(sis);
		final IList<IShape> shapes = new GamaList();
		for ( int i = 0; i < steps.length; i++ ) {
			for ( final ISpatialIndex si : list ) {
				final IShape first = si.firstAtDistance(source, steps[i], f);
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
	public Iterator<IShape> allInEnvelope(final IShape source, final Envelope envelope, final IAgentFilter f,
		final boolean contained) {
		return Iterators.concat(Iterators.transform(findSpatialIndexes(f),
			new Function<ISpatialIndex, Iterator<IShape>>() {

				@Override
				public Iterator<IShape> apply(final ISpatialIndex input) {
					return input.allInEnvelope(source, envelope, f, contained);
				}
			}));

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
