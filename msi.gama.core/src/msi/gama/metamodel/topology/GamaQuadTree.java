/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.GamaQuadTree.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.metamodel.topology;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.IIntersectable;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IMap;
import msi.gaml.operators.Maths;

/**
 * A QuadTree allows to quickly find an object on a two-dimensional space.
 * <p>
 * QuadTree recursively subdivides a space into four rectangles. Each node of a QuadTree subdivides the space covered by
 * the rectangle of its parent node into four smaller rectangles covering the upper left, upper right, lower left and
 * lower right quadrant of the parent rectangle.
 *
 * @author Werner Randelshofer, adapted by Alexis Drogoul for GAMA
 * @version $Id: QuadTree.java 717 2010-11-21 12:30:57Z rawcoder $
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaQuadTree implements ISpatialIndex {

	public static final int NW = 0;
	public static final int NE = 1;
	public static final int SW = 2;
	public static final int SE = 3;

	final QuadNode root;
	final static int maxCapacity = 100;
	double minSize = 10;
	final boolean parallel;

	public static GamaQuadTree create(final Envelope envelope, final boolean parallel) {
		return new GamaQuadTree(envelope, parallel);
	}

	private GamaQuadTree(final Envelope bounds, final boolean sync) {
		// AD To address Issue 804, explictely converts the bounds to an
		// Envelope 2D, so that all computations are made
		// in 2D in the QuadTree
		this.parallel = sync;
		root = new QuadNode(new Envelope(bounds));
		minSize = bounds.getWidth() / 100d;
	}

	@Override
	public void dispose() {
		root.dispose();
	}

	@Override
	public void insert(final IAgent agent) {
		if (agent == null) return;
		if (agent.isPoint()) {
			root.add(agent.getLocation(), agent);
		} else {
			root.add(agent.getEnvelope(), agent);
		}
	}

	private boolean isPoint(final Envelope env) {
		return env.getArea() == 0.0;
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		final Envelope3D current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) return;
		if (isPoint(current)) {
			root.remove(current.centre(), agent);
		} else {
			root.remove(current, agent);
		}
		current.dispose();
	}

	protected Collection<IAgent> findIntersects(final IScope scope, final IShape source, final Envelope r,
			final IAgentFilter filter) {
		// Adresses Issue 722 by explicitly shuffling the results with GAMA
		// random procedures and removing duplicates
		try (final ICollector<IAgent> list = Collector.getOrderedSet()) {
			root.findIntersects(r, list);
			if (list.isEmpty()) return GamaListFactory.create();
			filter.filter(scope, source, list);
			list.shuffleInPlaceWith(scope.getRandom());
			return list.items();
		}
	}

	protected Collection<IAgent> findIntersectsNew(final IScope scope, final IShape source, final Envelope r,
			final IAgentFilter filter) {
		// Adresses Issue 722 by explicitly shuffling the results with GAMA
		// random procedures and removing duplicates
		try (final ICollector<IAgent> list = Collector.getOrderedSet()) {
			root.visitIntersects(r, a -> {
				if (filter.accept(scope, source, a)) { list.add(a); }
			});
			// root.findIntersects(r, list);
			// if (list.isEmpty()) return GamaListFactory.create();
			// filter.filter(scope, source, list);
			list.shuffleInPlaceWith(scope.getRandom());
			return list.items();
		}
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f) {
		// TODO filter result by topology's bounds
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

	public void visitAllAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f,
			final Consumer<IAgent> action) {
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		env.expandBy(dist * Maths.SQRT2);
		try {
			root.visitIntersects(env, a -> {
				if (f.accept(scope, source, a) && source.euclidianDistanceTo(a) <= dist) { action.accept(a); }
			});
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

	public IAgent firstAtDistanceNew(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		env.expandBy(dist * Maths.SQRT2);
		try (final ICollector<IAgent> visited = Collector.getOrderedSet()) {
			double[] min_distance = { dist };
			IAgent[] min_agent = { null };
			root.visitIntersects(env, a -> {
				if (f.accept(scope, source, a) && !visited.contains(a)) {
					visited.add(a);
					final double dd = source.euclidianDistanceTo(a);
					if (dd < min_distance[0]) {
						min_distance[0] = dd;
						min_agent[0] = a;
					}
				}
			});
			return min_agent[0];
		} finally {
			env.dispose();
		}
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		return findIntersects(scope, source, envelope, f);
	}

	@Override
	public Collection<IAgent> allAgents() {
		try (final ICollector<IAgent> result = Collector.getOrderedSet()) {
			root.findIntersects(root.bounds, result);
			return result.items();
		}
	}

	private class QuadNode {

		final Envelope bounds;
		private final double halfx, halfy;
		private volatile QuadNode[] nodes = null;
		// ** Addresses part of Issue 722 -- Need to keep the agents ordered
		// (by insertion order) **
		private IMap<IAgent, IIntersectable> objects;
		private final boolean canSplit;

		public QuadNode(final Envelope bounds) {
			this.bounds = bounds;
			final double hw = bounds.getWidth();
			final double hh = bounds.getHeight();
			halfx = bounds.getMinX() + hw / 2;
			halfy = bounds.getMinY() + hh / 2;
			canSplit = hw > minSize && hh > minSize;
		}

		private IMap<IAgent, IIntersectable> getOrCreateObjects() {
			if (objects == null) { objects = parallel ? GamaMapFactory.concurrentMap() : GamaMapFactory.create(); }
			return objects;
		}

		public void dispose() {
			if (objects != null) {
				objects.forEach((a, e) -> {
					if (e != null) { e.dispose(); }
				});
				objects.clear();
			}
			objects = null;
			if (nodes != null) {
				for (final QuadNode n : nodes) {
					n.dispose();
				}
				nodes = null;
			}
		}

		public void remove(final Coordinate p, final IShape a) {
			if (nodes == null) {
				if (objects != null) {
					final IIntersectable env = objects.remove(a);
					if (env != null) { env.dispose(); }
				}
			} else {
				nodes[quadrant(p)].remove(p, a);
			}
		}

		public void remove(final Envelope env, final IShape a) {
			if (nodes == null) {
				if (objects != null) { objects.remove(a); }
			} else {
				for (final QuadNode node : nodes) {
					if (node.bounds.intersects(env)) { node.remove(env, a); }
				}
			}
		}

		public boolean shouldSplit() {
			return canSplit && nodes == null && objects != null && objects.size() >= maxCapacity;
		}

		public void add(final GamaPoint p, final IAgent a) {
			if (shouldSplit()) { split(); }
			if (nodes == null) {
				getOrCreateObjects().put(a, p);
			} else {
				nodes[quadrant(p)].add(p, a);
			}
		}

		public void add(final Envelope3D env, final IAgent a) {
			if (shouldSplit()) { split(); }
			if (nodes == null) {
				getOrCreateObjects().put(a, env);
			} else {
				for (final QuadNode node : nodes) {
					if (node.bounds.intersects(env)) { node.add(env, a); }
				}
			}
		}

		int quadrant(final Coordinate p) {
			final boolean north = p.y >= bounds.getMinY() && p.y < halfy;
			final boolean west = p.x >= bounds.getMinX() && p.x < halfx;
			return north ? west ? NW : NE : west ? SW : SE;
		}

		public void split() {
			final double maxx = bounds.getMaxX();
			final double minx = bounds.getMinX();
			final double miny = bounds.getMinY();
			final double maxy = bounds.getMaxY();
			nodes = new QuadNode[] { new QuadNode(new Envelope(minx, halfx, miny, halfy)),
					new QuadNode(new Envelope(halfx, maxx, miny, halfy)),
					new QuadNode(new Envelope(minx, halfx, halfy, maxy)),
					new QuadNode(new Envelope(halfx, maxx, halfy, maxy)) };
			if (objects != null) {
				for (final Map.Entry<IAgent, IIntersectable> entry : objects.entrySet()) {
					final IAgent agent = entry.getKey();
					if (agent != null && !agent.dead()) {
						final IShape g = agent.getGeometry();
						if (g.isPoint()) {
							add(g.getLocation(), agent);
						} else {
							add(g.getEnvelope(), agent);
						}
					}
				}
				objects.clear();
				objects = null;
			}
		}

		public void visitIntersects(final Envelope envelope, final Consumer<IAgent> action) {
			if (!bounds.intersects(envelope)) return;
			if (nodes == null && objects != null) {
				objects.forEach((a, e) -> {
					if (e != null && e.intersects(envelope)) { action.accept(a); }
				});
			} else if (nodes != null) {
				for (final QuadNode node : nodes) {
					node.visitIntersects(envelope, action);
				}
			}
		}

		public void findIntersects(final Envelope r, final Collection<IAgent> result) {
			if (!bounds.intersects(r)) return;
			if (nodes == null && objects != null) {
				objects.forEach((a, e) -> {
					if (e != null && e.intersects(r)) { result.add(a); }
				});
			} else {
				for (final QuadNode node : nodes) {
					node.findIntersects(r, result);
				}
			}

		}

	}

	@Override
	public boolean isParallel() {
		return parallel;
	}

}
