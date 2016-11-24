/*********************************************************************************************
 *
 * 'GamaParallelQuadTree.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package msi.gama.metamodel.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;
import msi.gaml.operators.Maths;

/**
 * A QuadTree allows to quickly find an object on a two-dimensional space.
 * <p>
 * QuadTree recursively subdivides a space into four rectangles. Each node of a
 * QuadTree subdivides the space covered by the rectangle of its parent node
 * into four smaller rectangles covering the upper left, upper right, lower left
 * and lower right quadrant of the parent rectangle.
 *
 * @author Werner Randelshofer, adapted by Alexis Drogoul for GAMA
 * @version $Id: QuadTree.java 717 2010-11-21 12:30:57Z rawcoder $
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaParallelQuadTree implements ISpatialIndex {

	public static final Integer NW = 0;
	public static final Integer NE = 1;
	public static final Integer SW = 2;
	public static final Integer SE = 3;

	private final QuadNode root;
	private final static int maxCapacity = 40;
	private double minSize = 10;

	public GamaParallelQuadTree(final Envelope bounds) {
		// AD To address Issue 804, explictely converts the bounds to an
		// Envelope 2D, so that all computations are made
		// in 2D in the QuadTree
		root = new QuadNode(new Envelope(bounds));
		minSize = bounds.getWidth() / 1000d;
	}

	@Override
	public void dispose() {
		root.dispose();
	}

	@Override
	public void insert(final IAgent agent) {
		if (agent == null) {
			return;
		}
		if (agent.isPoint()) {
			root.add((Coordinate) agent.getLocation(), agent);
		} else {
			root.add(agent.getEnvelope(), agent);
		}
	}

	private boolean isPoint(final Envelope env) {
		return env.getArea() == 0.0;
	}

	@Override
	public void remove(final Envelope previous, final IAgent agent) {
		final Envelope current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) {
			return;
		}
		if (isPoint(current)) {
			root.remove(current.centre(), agent);
		} else {
			root.remove(current, agent);
		}
	}

	protected Collection<IAgent> findIntersects(final IScope scope, final IShape source, final Envelope r,
			final IAgentFilter filter) {
		// Adresses Issue 722 by explicitly shuffling the results with GAMA
		// random procedures and removing duplicates
		final ICollector<IAgent> list = new Collector.Unique.Concurrent<>();
		root.findIntersects(r, list);
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
		// return result.parallelStream().filter(each ->
		// each.euclidianDistanceTo(source) <= dist)
		// .collect(Collectors.toList());
		if (result.isEmpty())
			return Collections.EMPTY_LIST;
		final Iterator<IAgent> it = result.iterator();
		while (it.hasNext()) {
			if (source.euclidianDistanceTo(it.next()) > dist) {
				it.remove();
			}
		}
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
		// return findIntersects(scope, source, env, f).parallelStream()
		// .min((a, b) -> Double.compare(source.euclidianDistanceTo(a),
		// source.euclidianDistanceTo(b))).get();
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		return findIntersects(scope, source, envelope, f);
	}

	@Override
	public Collection<IAgent> allAgents() {
		final ICollector<IAgent> result = new Collector.Unique.Concurrent<>();
		root.findIntersects(root.bounds, result);
		return result.items();
	}

	private class QuadNode {

		private final Envelope bounds;
		private final double hw, hh, minx, miny, halfx, halfy;
		// ** Addresses part of Issue 722 -- Need to keep the objects ordered
		// (by insertion order) **
		private final ConcurrentLinkedQueue<IAgent> objects = new ConcurrentLinkedQueue<IAgent>();
		private int size = 0;
		private volatile boolean isLeaf = true;
		private List<QuadNode> nodes;
		private final boolean canSplit;

		public QuadNode(final Envelope bounds) {
			this.bounds = bounds;
			hw = bounds.getWidth();
			hh = bounds.getHeight();
			minx = bounds.getMinX();
			miny = bounds.getMinY();
			halfx = minx + hw / 2;
			halfy = miny + hh / 2;
			canSplit = hw > minSize && hh > minSize;
		}

		public void dispose() {
			objects.clear();
			if (!isLeaf) {
				nodes.parallelStream().forEach(each -> each.dispose());
				nodes.clear();
			}

		}

		public IShape remove(final Coordinate p, final IShape a) {
			if (size > 0) {
				boolean removed = false;
				synchronized (objects) {
					// System.out.println(myIndex + "
					// GamaQuadTree.QuadNode.remove " + a);
					removed = objects.remove(a);
				}
				if (removed) {
					size = size - 1;
					return a;
				}

			} else if (!isLeaf) {
				final boolean north = p.y >= miny && p.y < halfy;
				final boolean west = p.x >= minx && p.x < halfx;
				return north ? west ? nodes.get(NW).remove(p, a) : nodes.get(NE).remove(p, a)
						: west ? nodes.get(SW).remove(p, a) : nodes.get(SE).remove(p, a);
			}
			return null;
		}

		public boolean add(final Coordinate p, final IAgent a) {
			if (isLeaf) {
				if (canSplit && size >= maxCapacity) {
					split();
					return add(p, a);
				}
				// synchronized (objects) {
				if (objects.add(a)) {
					size++;
					// }
				}
				return true;
			}
			final boolean north = p.y >= miny && p.y < halfy;
			final boolean west = p.x >= minx && p.x < halfx;
			return north ? west ? nodes.get(NW).add(p, a) : nodes.get(NE).add(p, a)
					: west ? nodes.get(SW).add(p, a) : nodes.get(SE).add(p, a);
		}

		private boolean removeIfPresent(final Envelope bounds, final IShape a) {
			// synchronized (objects) {
			return !(isLeaf && size == 0) && this.bounds.intersects(bounds) && remove(bounds, a);
			// }
		}

		public boolean remove(final Envelope bounds, final IShape a) {
			if (size != 0) {
				boolean removed = false;
				// synchronized (objects) {
				removed = objects.remove(a);
				// }
				if (removed) {
					size--;
					return true;
				}
			}
			return !isLeaf && nodes.get(NE).removeIfPresent(bounds, a) | nodes.get(NW).removeIfPresent(bounds, a)
					| nodes.get(SE).removeIfPresent(bounds, a) | nodes.get(SW).removeIfPresent(bounds, a);
		}

		public boolean add(final Envelope env, final IAgent o) {
			if (canSplit && isLeaf && size >= maxCapacity) {
				split();
			}
			if (isLeaf || env.contains(bounds)) {
				boolean added = false;
				synchronized (objects) {
					added = objects.add(o);
				}
				if (added) {
					size++;
				}
				return added;
			}
			boolean retVal = false;

			QuadNode node = nodes.get(NE);
			if (node.bounds.intersects(env)) {
				retVal = node.add(env, o);
			}
			node = nodes.get(NW);
			if (node.bounds.intersects(env)) {
				retVal = node.add(env, o);
			}
			node = nodes.get(SE);
			if (node.bounds.intersects(env)) {
				retVal = node.add(env, o);
			}
			node = nodes.get(SW);
			if (node.bounds.intersects(env)) {
				retVal = node.add(env, o);
			}
			return retVal;

			// final boolean[] retVal = new boolean[1];
			//
			// nodes.forEachValue(4, each -> {
			// if (each.bounds.intersects(env)) {
			// retVal[0] = each.add(env, o) || retVal[0];
			// }
			// });
			// return retVal[0];
		}

		public void split() {
			if (isLeaf) {
				final double maxx = bounds.getMaxX();
				final double maxy = bounds.getMaxY();
				nodes = new ArrayList(4);
				nodes.add(new QuadNode(new Envelope(minx, halfx, miny, halfy)));
				nodes.add(new QuadNode(new Envelope(halfx, maxx, miny, halfy)));
				nodes.add(new QuadNode(new Envelope(minx, halfx, halfy, maxy)));
				nodes.add(new QuadNode(new Envelope(halfx, maxx, halfy, maxy)));
				final IAgent[] tempList;
				synchronized (objects) {
					tempList = objects.toArray(new IAgent[size]);
					objects.clear();
				}

				size = 0;
				isLeaf = false;

				for (int i = 0; i < tempList.length; i++) {
					final IAgent entry = tempList[i];

					if ( /* entry != null && */!entry.dead()) {
						final IShape g = entry.getGeometry();
						if (g.isPoint()) {
							final Coordinate p = (Coordinate) g.getLocation();
							add(p, entry);
						} else {
							add(g.getEnvelope(), entry);
						}
					} else {
						continue;
					}
				}
			}
		}

		public void findIntersects(final Envelope r, final ICollector<IAgent> list) {
			if (bounds.intersects(r)) {
				// synchronized (objects) {
				objects.parallelStream().filter(each -> each.getEnvelope().intersects(r))
						.forEach(each -> list.add(each));
				// }
				if (!isLeaf) {
					nodes.parallelStream().forEach(each -> {
						if (!(each.isLeaf && each.size == 0)) {
							each.findIntersects(r, list);
						}
					});
				}
			}
		}

	}

}
