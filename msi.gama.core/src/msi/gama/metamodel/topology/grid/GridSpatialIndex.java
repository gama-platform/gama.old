package msi.gama.metamodel.topology.grid;

import java.util.Collection;
import java.util.function.Consumer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ISpatialIndex;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.ICollector;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gaml.operators.Maths;
import msi.gaml.types.Types;

public class GridSpatialIndex extends GamaObjectMatrix implements ISpatialIndex {

	public static GridSpatialIndex create(final Envelope envelope, final boolean parallel) {
		return new GridSpatialIndex(100, 100, new GamaPoint(envelope.getWidth(), envelope.getHeight()));
	}

	GamaPoint worldDimensions = null;
	GamaPoint cellDimensions = null;
	double epsilon;

	class Cell {
		Collector.AsOrderedSet<IAgent> agents = Collector.getOrderedSet();
	}

	public GridSpatialIndex(final int cols, final int rows, final GamaPoint worldDim) {
		super(cols, rows, Types.NO_TYPE);
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Cell();
		}
		worldDimensions = worldDim;
		cellDimensions = new GamaPoint(worldDim.x / this.numCols, worldDim.y / this.numRows);
		epsilon = cellDimensions.x / 10000;
	}

	public void findIntersects(final Envelope env, final Collection<IAgent> result) {
		visitCells(env, c -> {
			for (IAgent a : c.agents) {
				Envelope3D e = a.getEnvelope();
				if (e.intersects(env)) { result.add(a); }
			}
		});
	}

	public void visitCells(final Envelope env, final Consumer<Cell> consumer) {
		double minX = Math.max(0, env.getMinX());
		double minY = Math.max(0, env.getMinY());
		double maxX = Math.min(worldDimensions.x - 1, Math.max(0, env.getMaxX()));
		double maxY = Math.min(worldDimensions.y - 1, Math.max(0, env.getMaxY()));
		for (double i = minX; i < maxX; i += cellDimensions.x) {
			for (double j = minY; j < maxY; j += cellDimensions.y) {
				Cell c = (Cell) matrix[getIndex(i, j)];
				consumer.accept(c);
			}
		}
	}

	int getGridX(final double x) {
		return (int) ((x >= worldDimensions.x ? x - epsilon : x) / cellDimensions.x);
	}

	int getGridY(final double y) {
		return (int) ((y >= worldDimensions.y ? y - epsilon : y) / cellDimensions.y);
	}

	int getIndex(final double x, final double y) {
		return getGridY(y) * numCols + getGridX(x);
	}

	int getIndex(final Coordinate p) {
		return getGridY(p.y) * numCols + getGridX(p.x);
	}

	public Cell findCell(final Coordinate p) {
		return (Cell) matrix[getIndex(p)];
	}

	@Override
	public void insert(final IAgent agent) {
		if (agent == null) return;
		if (agent.isPoint()) {
			add((Coordinate) agent.getLocation(), agent);
		} else {
			add(agent.getEnvelope(), agent);
		}
	}

	private void add(final Coordinate location, final IAgent agent) {
		Cell c = findCell(location);
		if (c == null) return;
		c.agents.add(agent);
	}

	private void add(final Envelope3D env, final IAgent agent) {
		visitCells(env, c -> {
			c.agents.add(agent);
		});
	}

	private boolean isPoint(final Envelope env) {
		return env.getArea() == 0.0;
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		final Envelope3D current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) return;
		if (isPoint(current)) {
			_remove(current.centre(), agent);
		} else {
			_remove(current, agent);
		}
		current.dispose();
	}

	private void _remove(final GamaPoint centre, final IAgent agent) {
		Cell c = findCell(centre);
		if (c == null) return;
		c.agents.remove(agent);
	}

	private void _remove(final Envelope3D env, final IAgent agent) {
		visitCells(env, c -> {
			c.agents.remove(agent);
		});

	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		env.expandBy(exp);
		try {
			final Collection<IAgent> in_square = findIntersects(scope, source, env, f);
			if (in_square.isEmpty()) return null;
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
		} finally {
			env.dispose();
		}
	}

	@Override
	public Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f, final int number, final Collection<IAgent> alreadyChosen) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		env.expandBy(exp);
		try {
			final Collection<IAgent> in_square = findIntersects(scope, source, env, f);
			in_square.removeAll(alreadyChosen);
			if (in_square.isEmpty()) return GamaListFactory.create();

			if (in_square.size() <= number) return in_square;
			final Ordering<IShape> ordering = Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
			return ordering.leastOf(in_square, number);
		} finally {
			env.dispose();
		}
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		return findIntersects(scope, source, envelope, f);
	}

	protected Collection<IAgent> findIntersects(final IScope scope, final IShape source, final Envelope r,
			final IAgentFilter filter) {
		// Adresses Issue 722 by explicitly shuffling the results with GAMA
		// random procedures and removing duplicates
		try (final ICollector<IAgent> list = Collector.getOrderedSet()) {
			findIntersects(r, list);
			if (list.isEmpty()) return GamaListFactory.create();
			filter.filter(scope, source, list);
			list.shuffleInPlaceWith(scope.getRandom());
			return list.items();
		}
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		env.expandBy(exp);
		try {
			final Collection<IAgent> result = findIntersects(scope, source, env, f);
			if (result.isEmpty()) return GamaListFactory.create();
			result.removeIf(each -> source.euclidianDistanceTo(each) > dist);
			return result;
		} finally {
			env.dispose();
		}
	}

	@Override
	public void dispose() {
		super._clear();
	}

	@Override
	public Collection<IAgent> allAgents() {
		try (final ICollector<IAgent> result = Collector.getOrderedSet()) {
			findIntersects(Envelope3D.of(0d, 0d, worldDimensions.x, worldDimensions.y, 0d, 0d), result);
			return result.items();
		}
	}

	@Override
	public boolean isParallel() {
		return false;
	}

}
