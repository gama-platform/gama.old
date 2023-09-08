/*******************************************************************************************************
 *
 * GamaQuadTree.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.metamodel.topology;

import java.util.Collection;
import java.util.Map;

import org.locationtech.jts.geom.Envelope;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.IIntersectable;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gaml.operators.Maths;
import ummisco.gama.dev.utils.DEBUG;

/**
 * A QuadTree allows to quickly find an object on a two-dimensional space.
 * <p>
 * QuadTree recursively subdivides a space into four rectangles. Each node of a QuadTree subdivides the space covered by
 * the rectangle of its parent node into four smaller rectangles covering the upper left, upper right, lower left and
 * lower right quadrant of the parent rectangle.
 *
 * @author Werner Randelshofer, adapted by Alexis Drogoul for GAMA
 */

/**
 * The Class GamaQuadTree.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaQuadTree implements ISpatialIndex {

	static {
		DEBUG.OFF();
	}

	/** The root. */
	final QuadNode root;

	/** The Constant maxCapacity. */
	final static int maxCapacity = 100;

	/** The min size. */
	double minSize = 10;

	/** The parallel. */
	final boolean parallel;

	/**
	 * Creates the spatial index. Returns a synchronized quadtree if necessary (cf. #3576)
	 *
	 * @param envelope
	 *            the envelope
	 * @param parallel
	 *            the parallel
	 * @return the gama quad tree
	 */
	public static ISpatialIndex create(final Envelope envelope, final boolean parallel) {
		ISpatialIndex qt = new GamaQuadTree(envelope, parallel);
		if (GamaPreferences.Experimental.QUADTREE_SYNCHRONIZATION.getValue())
			return new QuadTreeSynchronizer(qt);
		return qt;
	}

	/**
	 * The Class QuadTreeSynchronizer.
	 */
	static class QuadTreeSynchronizer implements ISpatialIndex {

		/** The quadtree. */
		private final ISpatialIndex quadtree;

		/**
		 * Instantiates a new quad tree synchronizer.
		 *
		 * @param qt
		 *            the qt
		 */
		public QuadTreeSynchronizer(final ISpatialIndex qt) {
			quadtree = qt;
		}

		@Override
		public synchronized void insert(final IAgent agent) {
			quadtree.insert(agent);
		}

		@Override
		public synchronized void remove(final Envelope3D previous, final IAgent agent) {
			quadtree.remove(previous, agent);
		}

		@Override
		public synchronized IAgent firstAtDistance(final IScope scope, final IShape source, final double dist,
				final IAgentFilter f) {
			return quadtree.firstAtDistance(scope, source, dist, f);
		}

		@Override
		public synchronized Collection<IAgent> firstAtDistance(final IScope scope, final IShape source,
				final double dist, final IAgentFilter f, final int number, final Collection<IAgent> alreadyChosen) {
			return quadtree.firstAtDistance(scope, source, dist, f, number, alreadyChosen);
		}

		@Override
		public synchronized Collection<IAgent> allInEnvelope(final IScope scope, final IShape source,
				final Envelope envelope, final IAgentFilter f, final boolean contained) {
			return quadtree.allInEnvelope(scope, source, envelope, f, contained);
		}

		@Override
		public synchronized Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
				final IAgentFilter f) {
			return quadtree.allAtDistance(scope, source, dist, f);
		}

		@Override
		public void dispose() {
			quadtree.dispose();
		}

	}

	/**
	 * Instantiates a new gama quad tree.
	 *
	 * @param bounds
	 *            the bounds
	 * @param sync
	 *            the sync
	 */
	private GamaQuadTree(final Envelope bounds, final boolean sync) {
		// AD To address Issue 804, explictely converts the bounds to an
		// Envelope 2D, so that all computations are made
		// in 2D in the QuadTree
		this.parallel = sync;
		root = new QuadNode(new Envelope(bounds));
		minSize = bounds.getWidth() / 100d;
		// DEBUG.OUT(" ");
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

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		final Envelope3D current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) return;
		if (current.getArea() == 0.0) {
			root.remove(current.centre(), agent);
		} else {
			root.remove(current, agent);
		}
		current.dispose();
	}

	/**
	 * Find intersects.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param r
	 *            the r
	 * @param filter
	 *            the filter
	 * @return the collection
	 */
	protected Collection<IAgent> findIntersects(final IScope scope, final IShape source, final Envelope r,
			final IAgentFilter filter) {
		// Adresses Issue 722 by explicitly shuffling the results with GAMA
		// random procedures and removing duplicates
		try (final ICollector<IAgent> list = Collector.getOrderedSet()) {
			root.findIntersects(r, list);
			if (list.isEmpty()) return GamaListFactory.create();
			filter.filter(scope, source, list);
			// DEBUG.OUT(list.size(), false);
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
		final Envelope3D env = Envelope3D.of(source.getEnvelope());
		env.expandBy(dist * Maths.SQRT2);
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
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		return findIntersects(scope, source, envelope, f);
	}

	/**
	 * The Class QuadNode.
	 */
	private class QuadNode {

		/** The bounds. */
		final Envelope bounds;

		/** The halfy. */
		protected final double halfx, halfy;

		/** The nodes. */
		protected volatile QuadNode nw, ne, sw, se;

		/**
		 * Addresses part of Issue 722 -- Need to keep the agents ordered (by insertion order)
		 **/
		protected final Map<IAgent, IIntersectable> objects =
				parallel ? GamaMapFactory.synchronizedOrderedMap() : GamaMapFactory.create();

		/** The can split. */
		protected final boolean canSplit;

		/**
		 * Instantiates a new quad node.
		 *
		 * @param bounds
		 *            the bounds
		 */
		public QuadNode(final Envelope bounds) {
			this.bounds = bounds;
			final double hw = bounds.getWidth();
			final double hh = bounds.getHeight();
			halfx = bounds.getMinX() + hw / 2;
			halfy = bounds.getMinY() + hh / 2;
			canSplit = hw > minSize && hh > minSize;
		}

		/**
		 * Dispose.
		 */
		public void dispose() {
			objects.forEach((a, e) -> { if (e != null) { e.dispose(); } });
			objects.clear();
			if (nw != null) {
				nw.dispose();
				nw = null;
				sw.dispose();
				sw = null;
				ne.dispose();
				ne = null;
				se.dispose();
				se = null;
			}
		}

		/**
		 * Adds the.
		 *
		 * @param p
		 *            the p
		 * @param a
		 *            the a
		 */
		private void add(final GamaPoint p, final IAgent a) {
			trySplit();
			if (nw == null) {
				objects.put(a, p);
			} else {
				getNode(p).add(p, a);
			}
		}

		/**
		 * Adds the.
		 *
		 * @param e
		 *            the e
		 * @param a
		 *            the a
		 */
		private void add(final Envelope3D e, final IAgent a) {
			trySplit();
			if (nw == null) {
				objects.put(a, e);
			} else {
				if (nw.bounds.intersects(e)) { nw.add(e, a); }
				if (ne.bounds.intersects(e)) { ne.add(e, a); }
				if (sw.bounds.intersects(e)) { sw.add(e, a); }
				if (se.bounds.intersects(e)) { se.add(e, a); }
			}
		}

		/**
		 * Try split.
		 */
		private void trySplit() {
			if (nw == null && canSplit && objects.size() >= maxCapacity) { split(); }
		}

		/**
		 * Removes the.
		 *
		 * @param p
		 *            the p
		 * @param a
		 *            the a
		 */
		private void remove(final GamaPoint p, final IShape a) {
			if (nw == null) {
				final IIntersectable env = objects.remove(a);
				if (env != null) { env.dispose(); }
			} else {
				getNode(p).remove(p, a);
			}
		}

		/**
		 * Removes the.
		 *
		 * @param e
		 *            the e
		 * @param a
		 *            the a
		 */
		private void remove(final Envelope3D e, final IShape a) {
			if (nw == null) {
				final IIntersectable env = objects.remove(a);
				if (env != null) { env.dispose(); }
			} else {
				if (nw.bounds.intersects(e)) { nw.remove(e, a); }
				if (ne.bounds.intersects(e)) { ne.remove(e, a); }
				if (sw.bounds.intersects(e)) { sw.remove(e, a); }
				if (se.bounds.intersects(e)) { se.remove(e, a); }
			}
		}

		/**
		 * Quadrant.
		 *
		 * @param p
		 *            the p
		 * @return the int
		 */
		private QuadNode getNode(final GamaPoint p) {
			final boolean north = p.y >= bounds.getMinY() && p.y < halfy;
			final boolean west = p.x >= bounds.getMinX() && p.x < halfx;
			return north ? west ? nw : ne : west ? sw : se;
		}

		/**
		 * Split.
		 */
		private void split() {
			try {
				final double maxx = bounds.getMaxX();
				final double minx = bounds.getMinX();
				final double miny = bounds.getMinY();
				final double maxy = bounds.getMaxY();
				nw = new QuadNode(new Envelope(minx, halfx, miny, halfy));
				ne = new QuadNode(new Envelope(halfx, maxx, miny, halfy));
				sw = new QuadNode(new Envelope(minx, halfx, halfy, maxy));
				se = new QuadNode(new Envelope(halfx, maxx, halfy, maxy));
				objects.forEach((a, e) -> {
					if (a != null && !a.dead()) {
						final IShape g = a.getGeometry();
						if (g.isPoint()) {
							add(g.getLocation(), a);
						} else {
							add(g.getEnvelope(), a);
						}
					}
				});
			} finally {
				objects.clear();
			}
		}

		/**
		 * Find intersects.
		 *
		 * @param r
		 *            the r
		 * @param result
		 *            the result
		 */
		public void findIntersects(final Envelope r, final Collection<IAgent> result) {
			if (!bounds.intersects(r)) return;
			if (nw == null) {
				objects.forEach((a, e) -> { if (e != null && e.intersects(r)) { result.add(a); } });
			} else {
				nw.findIntersects(r, result);
				ne.findIntersects(r, result);
				sw.findIntersects(r, result);
				se.findIntersects(r, result);
			}

		}

	}

}
