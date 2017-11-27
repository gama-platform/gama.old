/*********************************************************************************************
 *
 * 'GamaQuadTree.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package msi.gama.metamodel.topology.experimental;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ISpatialIndex;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.operators.Maths;

/**
 * A simplified grid spatial index
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGridIndex implements ISpatialIndex {

	final static int detail = 100;

	private class Cell {
		private final TOrderedHashMap<IAgent, Envelope> objects = new TOrderedHashMap();

		void add(final Envelope env, final IAgent agent) {
			objects.put(agent, env);
		}

		void remove(final Envelope env, final IAgent agent) {
			objects.remove(agent);
		}

		void findIntersects(final Envelope r, final Collection<IAgent> result) {

			objects.forEachEntry((a, env) -> {
				if (env.intersects(r))
					result.add(a);
				return true;
			});

		}

	}

	private final Cell[][] cells = new Cell[detail][detail];
	private final Envelope bounds;
	private final double cellWidth, cellHeight;

	public static ISpatialIndex create(final Envelope envelope) {
		return new GamaGridIndex(envelope);
	}

	private GamaGridIndex(final Envelope bounds) {
		this.bounds = bounds;
		cellWidth = bounds.getWidth() / detail;
		cellHeight = bounds.getHeight() / detail;
	}

	@Override
	public void dispose() {
		Arrays.fill(cells, null);
	}

	public Cell getCell(final double x, final double y) {
		final int xx = (int) (x / detail);
		final int yy = (int) (y / detail);
		if (xx < 0 || xx >= 100 || yy < 0 || y >= 100) { return null; }
		return cells[xx][yy];
	}

	@Override
	public void insert(final IAgent agent) {
		if (agent == null) { return; }
		if (agent.isPoint()) {
			final GamaPoint l = (GamaPoint) agent.getLocation();
			addToCell(l.x, l.y, Envelope3D.of(l), agent);
		} else {
			final Envelope e = agent.getEnvelope();
			for (double x = e.getMinX(); x < e.getMaxX(); x++)
				for (double y = e.getMinY(); y < e.getMaxY(); y++) {
					addToCell(x, y, e, agent);
				}
		}
	}

	private void addToCell(final double x, final double y, final Envelope e, final IAgent agent) {
		final Cell cell = getCell(x, y);
		if (cell != null)
			cell.add(e, agent);
	}

	private void removeFromCell(final double x, final double y, final Envelope e, final IAgent agent) {
		final Cell cell = getCell(x, y);
		if (cell != null)
			cell.remove(e, agent);
	}

	private void findInCell(final double x, final double y, final Envelope e, final ICollector<IAgent> list) {
		final Cell cell = getCell(x, y);
		if (cell != null)
			cell.findIntersects(e, list);

	}

	private boolean isPoint(final Envelope env) {
		return env.getArea() == 0.0;
	}

	@Override
	public void remove(final Envelope previous, final IAgent agent) {
		final Envelope current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) { return; }
		if (isPoint(current)) {
			final Coordinate l = current.centre();
			removeFromCell(l.x, l.y, current, agent);
		} else {
			for (double x = current.getMinX(); x < current.getMaxX(); x++)
				for (double y = current.getMinY(); y < current.getMaxY(); y++) {
					removeFromCell(x, y, current, agent);
				}

		}
	}

	protected Collection<IAgent> findIntersects(final IScope scope, final IShape source, final Envelope e,
			final IAgentFilter filter) {
		// Adresses Issue 722 by explicitly shuffling the results with GAMA
		// random procedures and removing duplicates
		final ICollector<IAgent> list = new Collector.UniqueOrdered<>();
		for (double x = e.getMinX(); x < e.getMaxX(); x++)
			for (double y = e.getMinY(); y < e.getMaxY(); y++) {
				findInCell(x, y, e, list);
			}
		if (list.isEmpty())
			return Collections.EMPTY_LIST;
		filter.filter(scope, source, list);
		scope.getRandom().shuffle2(list);
		return list;
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f) {
		// TODO filter result by topology's bounds
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = new Envelope3D(source.getEnvelope());
		env.expandBy(exp);
		final Collection<IAgent> result = findIntersects(scope, source, env, f);
		if (result.isEmpty())
			return Collections.EMPTY_LIST;
		result.removeIf(each -> source.euclidianDistanceTo(each) > dist);
		return result;
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = new Envelope3D(source.getEnvelope());
		env.expandBy(exp);
		final Collection<IAgent> in_square = findIntersects(scope, source, env, f);
		if (in_square.isEmpty())
			return null;
		double min_distance = dist;
		IAgent min_agent = null;
		for (final IAgent a : in_square) {
			final Double dd = source.euclidianDistanceTo(a);
			if (dd < min_distance) {
				min_distance = dd;
				min_agent = a;
			}
		}
		return min_agent;
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		return findIntersects(scope, source, envelope, f);
	}

	@Override
	public Collection<IAgent> allAgents() {
		final Collection<IAgent> result = new TLinkedHashSet();
		for (int x = 0; x < detail; x++)
			for (int y = 0; y < detail; y++) {
				cells[x][y].findIntersects(bounds, result);
			}
		return result;
	}

}
