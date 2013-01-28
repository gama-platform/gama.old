package msi.gama.metamodel.topology;

import java.awt.Graphics2D;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.util.*;
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
		all = new HashSet();
		all.add(quadtree);
		double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 20, biggest / 10, biggest / 2, biggest };
	}

	private ISpatialIndex findSpatialIndex(final IAgent a) {
		ISpecies s = a.getSpecies();
		ISpatialIndex si = indexes.get(s);
		if ( si == null ) {
			if ( s.isGrid() ) {
				si = (GamaSpatialMatrix) a.getTopology().getPlaces();
			} else {
				si = quadtree;
			}
			all.add(si);
			indexes.put(s, si);
		}
		return si;
	}

	private Set<ISpatialIndex> findSpatialIndexes(final IAgentFilter f) {
		ISpecies s = f.speciesFiltered();
		if ( s != null ) {
			_INDEXES.clear();
			ISpatialIndex si = indexes.get(s);
			if ( si != null ) {
				_INDEXES.add(si);
			}
			return _INDEXES;
		}
		return all;

	}

	private IShape findClosest(final IShape source) {
		if ( _SHAPES.size() == 1 ) { return _SHAPES.get(0); }
		double min_dist = Double.MAX_VALUE;
		IShape min_agent = null;
		for ( IShape s : _SHAPES ) {
			double dd = source.euclidianDistanceTo(s);
			if ( dd < min_dist ) {
				min_dist = dd;
				min_agent = s;
			}
		}
		return min_agent;
	}

	@Override
	public void insert(final IShape o) {
		IAgent a = o.getAgent();
		if ( a == null ) { return; }
		findSpatialIndex(a).insert(o);
	}

	// @Override
	// public void insert(final Envelope bounds, final IShape o) {
	// IAgent a = o.getAgent();
	// if ( a == null ) { return; }
	// ISpatialIndex si = findSpatialIndex(a);
	// si.insert(bounds, o);
	// }
	//
	// @Override
	// public void insert(final Coordinate location, final IShape o) {
	// IAgent a = o.getAgent();
	// if ( a == null ) { return; }
	// ISpatialIndex si = findSpatialIndex(a);
	// si.insert(location, o);
	// }

	@Override
	public void remove(final IShape previous, final IShape o) {
		IAgent a = o.getAgent();
		if ( a == null ) { return; }
		findSpatialIndex(a).remove(previous, o);
	}

	// @Override
	// public void remove(final Envelope bounds, final IShape o) {
	// IAgent a = o.getAgent();
	// if ( a == null ) { return; }
	// ISpatialIndex si = findSpatialIndex(a);
	// si.remove(bounds, o);
	// }
	//
	// @Override
	// public void remove(final Coordinate location, final IShape o) {
	// IAgent a = o.getAgent();
	// if ( a == null ) { return; }
	// ISpatialIndex si = findSpatialIndex(a);
	// si.remove(location, o);
	// }

	static void _INIT() {
		_SHAPES.clear();
	}

	static void _STORE(final IShape a) {
		if ( a != null ) {
			_SHAPES.add(a);
		}
	}

	static void _STORE(final Collection<IShape> a) {
		_SHAPES.addAll(a);
	}

	@Override
	public IList<IShape> allAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return new GamaList(); }
		_INIT();
		for ( ISpatialIndex si : sis ) {
			_STORE(si.allAtDistance(source, dist, f));
		}
		return new GamaList(_SHAPES);

	}

	// @Override
	// public IList<IShape> allAtDistance(final ILocation source, final double dist,
	// final IAgentFilter f) {
	// Set<ISpatialIndex> sis = findSpatialIndexes(f);
	// if ( sis.isEmpty() ) { return new GamaList(); }
	// _INIT();
	// for ( ISpatialIndex si : sis ) {
	// _STORE(si.allAtDistance(source, dist, f));
	// }
	// return new GamaList(_SHAPES);
	// }

	@Override
	public IShape firstAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return null; }
		_INIT();
		for ( int i = 0; i < steps.length; i++ ) {
			for ( ISpatialIndex si : sis ) {
				_STORE(si.firstAtDistance(source, steps[i], f));
			}
			if ( !_SHAPES.isEmpty() ) {
				break;
			}
		}
		return findClosest(source);
	}

	// @Override
	// public IShape firstAtDistance(final ILocation source, final double dist, final IAgentFilter
	// f) {
	// Set<ISpatialIndex> sis = findSpatialIndexes(f);
	// if ( sis.isEmpty() ) { return null; }
	// _INIT();
	// for ( int i = 0; i < steps.length; i++ ) {
	// for ( ISpatialIndex si : sis ) {
	// _STORE(si.firstAtDistance(source, steps[i], f));
	// }
	// if ( !_SHAPES.isEmpty() ) {
	// break;
	// }
	// }
	// return findClosest(source);
	// }

	@Override
	public IList<IShape> allInEnvelope(final IShape source, final Envelope envelope,
		final IAgentFilter f, final boolean contained) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return new GamaList(); }
		_INIT();
		for ( ISpatialIndex si : sis ) {
			_STORE(si.allInEnvelope(source, envelope, f, contained));
		}
		return new GamaList(_SHAPES);
	}

	@Override
	public void drawOn(final Graphics2D g2, final int width, final int height) {
		// NOTHING TO DO
	}

	@Override
	public void update() {}

	@Override
	public void cleanCache() {
		// indexes.clear();
	}

	@Override
	public void add(final ISpatialIndex index, final ISpecies species) {
		all.add(index);
		indexes.put(species, index);
	}

	@Override
	public void remove(final ISpatialIndex index) {
		if ( all.remove(index) ) {
			for ( Map.Entry<ISpecies, ISpatialIndex> entry : indexes.entrySet() ) {
				if ( entry.getValue() == index ) {
					indexes.remove(entry.getKey());
				}
			}
		}
	}

	@Override
	public void dispose() {
		quadtree = null;
		indexes.clear();
		all.clear();
	}

}
