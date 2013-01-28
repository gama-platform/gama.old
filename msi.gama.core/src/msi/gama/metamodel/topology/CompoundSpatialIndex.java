package msi.gama.metamodel.topology;

import java.awt.Graphics2D;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;
import com.vividsolutions.jts.geom.*;

public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	Set<ISpatialIndex> all;
	ISpatialIndex quadtree;
	Map<ISpecies, ISpatialIndex> indexes;

	public CompoundSpatialIndex(final Envelope bounds) {
		quadtree = new GamaQuadTree(bounds);
		indexes = new HashMap();
		all = new HashSet();
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
			ISpatialIndex si = indexes.get(s);
			if ( si != null ) { return new HashSet(Arrays.asList(si)); }
			return GamaList.EMPTY_SET;
		}
		return all;

	}

	private IShape findClosest(final IShape source, final double dist, final Set<IShape> result) {
		if ( result.size() == 1 ) { return (IShape) result.toArray()[0]; }
		return null;
	}

	@Override
	public void insert(final Envelope bounds, final IShape o) {
		IAgent a = o.getAgent();
		if ( a == null ) { return; }
		ISpatialIndex si = findSpatialIndex(a);
		si.insert(bounds, o);
	}

	@Override
	public void insert(final Coordinate location, final IShape o) {
		IAgent a = o.getAgent();
		if ( a == null ) { return; }
		ISpatialIndex si = findSpatialIndex(a);
		si.insert(location, o);
	}

	@Override
	public void remove(final Envelope bounds, final IShape o) {
		IAgent a = o.getAgent();
		if ( a == null ) { return; }
		ISpatialIndex si = findSpatialIndex(a);
		si.remove(bounds, o);
	}

	@Override
	public void remove(final Coordinate location, final IShape o) {
		IAgent a = o.getAgent();
		if ( a == null ) { return; }
		ISpatialIndex si = findSpatialIndex(a);
		si.remove(location, o);
	}

	@Override
	public IList<IShape> allAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return new GamaList(); }
		Set<IShape> result = new HashSet();
		for ( ISpatialIndex si : sis ) {
			result.addAll(si.allAtDistance(source, dist, f));
		}
		return new GamaList(result);

	}

	@Override
	public IList<IShape> allAtDistance(final ILocation source, final double dist,
		final IAgentFilter f) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return new GamaList(); }
		Set<IShape> result = new HashSet();
		for ( ISpatialIndex si : sis ) {
			result.addAll(si.allAtDistance(source, dist, f));
		}
		return new GamaList(result);
	}

	@Override
	public IShape firstAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return null; }
		Set<IShape> result = new HashSet();
		for ( ISpatialIndex si : sis ) {
			result.add(si.firstAtDistance(source, dist, f));
		}
		return findClosest(source, dist, result);
	}

	@Override
	public IShape firstAtDistance(final ILocation source, final double dist, final IAgentFilter f) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return null; }
		Set<IShape> result = new HashSet();
		for ( ISpatialIndex si : sis ) {
			result.add(si.firstAtDistance(source, dist, f));
		}
		return findClosest(source, dist, result);
	}

	@Override
	public IList<IShape> allInEnvelope(final IShape source, final Envelope envelope,
		final IAgentFilter f, final boolean contained) {
		Set<ISpatialIndex> sis = findSpatialIndexes(f);
		if ( sis.isEmpty() ) { return new GamaList(); }
		Set<IShape> result = new HashSet();
		for ( ISpatialIndex si : sis ) {
			result.addAll(si.allInEnvelope(source, envelope, f, contained));
		}
		return new GamaList(result);
	}

	@Override
	public void drawOn(final Graphics2D g2, final int width, final int height) {
		// NOTHING TO DO
	}

	@Override
	public void update() {}

	@Override
	public void cleanCache() {
		indexes.clear();
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
