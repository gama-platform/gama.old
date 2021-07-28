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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.geom.Envelope;

import com.google.common.collect.Ordering;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.ICollector;
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
public class SimpleGamaQuadTree implements ISpatialIndex {

	final QuadNode root;
	final static int maxCapacity = 100;
	double minSize = 10;
	boolean parallel;

	public static SimpleGamaQuadTree create(final Envelope envelope, final boolean parallel) {
		return new SimpleGamaQuadTree(envelope, parallel);
	}

	private SimpleGamaQuadTree(final Envelope bounds, final boolean sync) {
		// AD To address Issue 804, explictely converts the bounds to an
		// Envelope 2D, so that all computations are made
		// in 2D in the QuadTree
		parallel = sync;
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
		root.add(agent.getEnvelope(), agent);
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		final Envelope3D current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) return;
		root.remove(current, agent);
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
		private List<IAgent> objects = new ArrayList<>(maxCapacity);
		private final boolean canSplit;

		public QuadNode(final Envelope bounds) {
			this.bounds = bounds;
			final double hw = bounds.getWidth();
			final double hh = bounds.getHeight();
			halfx = bounds.getMinX() + hw / 2;
			halfy = bounds.getMinY() + hh / 2;
			canSplit = hw > minSize && hh > minSize;
		}

		public void dispose() {
			if (nodes != null) {
				for (final QuadNode n : nodes) {
					n.dispose();
				}
				nodes = null;
			}
		}

		public void remove(final Envelope env, final IShape a) {
			if (nodes == null) {
				objects.remove(a);
			} else {
				for (final QuadNode node : nodes) {
					if (node.bounds.intersects(env)) { node.remove(env, a); }
				}
			}
		}

		public boolean shouldSplit() {
			return canSplit && nodes == null && objects.size() >= maxCapacity;
		}

		public void add(final Envelope3D env, final IAgent a) {
			if (shouldSplit()) { split(); }
			if (nodes == null) {
				if (!objects.contains(a)) { objects.add(a); }
			} else {
				for (final QuadNode node : nodes) {
					if (node.bounds.intersects(env)) { node.add(env, a); }
				}
			}
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
				for (final IAgent agent : objects) {
					if (agent != null && !agent.dead()) {
						final IShape g = agent.getGeometry();
						add(g.getEnvelope(), agent);
					}
				}
				objects.clear();
				objects = null;
			}
		}

		public void findIntersects(final Envelope r, final Collection<IAgent> result) {
			if (bounds.intersects(r)) {
				if (objects != null) {
					for (IAgent agent : objects) {
						final Envelope3D env = agent.getEnvelope();
						if (env != null && env.intersects(r)) { result.add(agent); }
					}
				}

				if (nodes != null) {
					for (final QuadNode node : nodes) {
						node.findIntersects(r, result);
					}
				}
			}

		}

	}

	@Override
	public boolean isParallel() {
		return parallel;
	}

}
