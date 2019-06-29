/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.GamaQuadTree.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.metamodel.topology;

import static msi.gama.runtime.concurrent.GamaExecutorService.CONCURRENCY_SPECIES;
import static one.util.streamex.StreamEx.of;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.util.JavaUtils;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.operators.Maths;

/**
 * A Simple grid spatial index that allows to quickly find an object on a two-dimensional space.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class FixedGridSpatialIndex implements ISpatialIndex {

	PoolUtils.ObjectPool<Envelope3D> envelopePool = PoolUtils.create("Envelope3D", true, () -> new Envelope3D(), null);

	final Map<IAgent, IEnvelope>[] grid; // x = cols * y = rows
	final double cellWidth, cellHeight;
	int size = 40; // GamaPreferences.External.SPATIAL_INDEX_SIZE.getValue();

	public static ISpatialIndex create(final Envelope envelope) {
		return new FixedGridSpatialIndex(envelope);
	}

	private Map<IAgent, IEnvelope> newMap() {
		return CONCURRENCY_SPECIES.getValue() ? new ConcurrentHashMap<>() : new TOrderedHashMap<>();
	}

	private FixedGridSpatialIndex(final Envelope bounds) {
		grid = new Map[size * size];
		cellWidth = bounds.getWidth() / size;
		cellHeight = bounds.getHeight() / size;
	}

	@Override
	public void dispose() {
		for (final Map<IAgent, IEnvelope> map : grid) {
			if (map != null) {
				map.clear();
			}
		}
		Arrays.fill(grid, null);
	}

	@Override
	public void insert(final IAgent agent) {
		if (agent == null) { return; }
		if (agent.isPoint()) {
			final GamaPoint coord = agent.getLocation();
			findCell(coord.x, coord.y, true).put(agent, coord);
		} else {
			final IEnvelope e = agent.getEnvelope();
			visitEachCellIn(e, true, (cell) -> cell.put(agent, e));
		}
	}

	private Map<IAgent, IEnvelope> findCell(final double px, final double py, final boolean createIt) {
		final int i = getIndex(px, py);
		if (createIt && grid[i] == null) {
			grid[i] = newMap();
		}
		return grid[i];
	}

	private int getIndex(final double px, final double py) {
		final double xx = px / cellWidth;
		final double yy = py / cellHeight;
		final int x = xx < 0 ? 0 : xx > size - 1 ? size - 1 : (int) xx;
		final int y = yy < 0 ? 0 : yy > size - 1 ? size - 1 : (int) yy;
		return y * size + x;
	}

	private void visitEachCellIn(final IEnvelope e, final boolean createThem,
			final Consumer<Map<IAgent, IEnvelope>> visitor) {
		final double maxX = e.getMaxX();
		final double maxY = e.getMaxY();
		for (double x = e.getMinX(); x < maxX; x += cellWidth) {
			for (double y = e.getMinY(); y < maxY; y += cellHeight) {
				final Map<IAgent, IEnvelope> cell = findCell(x, y, createThem);
				if (cell != null) {
					visitor.accept(cell);
				}
			}
		}
	}

	private void visitEachCellIn(final IEnvelope e, final boolean createThem,
			final BiConsumer<IAgent, IEnvelope> visitor) {
		final double maxX = e.getMaxX();
		final double maxY = e.getMaxY();
		for (double x = e.getMinX(); x < maxX; x += cellWidth) {
			for (double y = e.getMinY(); y < maxY; y += cellHeight) {
				final Map<IAgent, IEnvelope> cell = findCell(x, y, createThem);
				if (cell != null) {
					cell.forEach(visitor);
				}
			}
		}
	}

	@Override
	public void remove(final IEnvelope previous, final IAgent agent) {
		if (agent == null) { return; }
		final IEnvelope current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) { return; }
		if (current.isPoint()) {
			final GamaPoint coord = current.getLocation();
			final Map<IAgent, IEnvelope> cell = findCell(coord.getX(), coord.getY(), false);
			if (cell != null) {
				cell.remove(agent);
			}
		} else {
			visitEachCellIn(current, false, c -> c.remove(agent));
		}
	}

	protected void findIntersects(final IScope scope, final IShape source, final IEnvelope r, final IAgentFilter filter,
			final Set<IAgent> list) {

		visitEachCellIn(r, false, (a, env) -> {
			if (r.intersects(env)) {
				list.add(a);
			}
		});
		filter.filter(scope, source, list);
		scope.getRandom().shuffleInPlace(list);
	}

	protected void findIntersects(final IScope scope, final IShape source, final double distance,
			final IAgentFilter filter, final Set<IAgent> list) {
		Envelope3D env = null;
		try {
			env = envelopePool.get();
			env.init(source.getEnvelope());
			env.expandBy(distance * Maths.SQRT2);
			findIntersects(scope, source, env, filter, list);
		} finally {
			envelopePool.release(env);
		}
	}

	@Override
	public void allAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f,
			final Set<IAgent> accumulator) {
		findIntersects(scope, source, dist, f, accumulator);
		accumulator.removeIf(each -> source.euclidianDistanceTo(each) > dist);
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final Set<IAgent> list = JavaUtils.SET_POOL.get();
		try {
			findIntersects(scope, source, dist, f, list);
			if (list.isEmpty()) { return null; }
			double min_distance = dist;
			IAgent min_agent = null;
			for (final IAgent a : list) {
				final Double dd = source.euclidianDistanceTo(a);
				if (dd < min_distance) {
					min_distance = dd;
					min_agent = a;
				}
			}
			return min_agent;
		} finally {
			JavaUtils.SET_POOL.release(list);
		}
	}

	@Override
	public void allInEnvelope(final IScope scope, final IShape source, final IEnvelope envelope, final IAgentFilter f,
			final boolean contained, final Set<IAgent> accumulator) {
		findIntersects(scope, source, envelope, f, accumulator);
	}

	@Override
	public Collection<IAgent> allAgents() {
		return of(grid).toFlatList((each) -> each == null ? Collections.EMPTY_SET : each.keySet());
	}

}
