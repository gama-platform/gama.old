/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.GamaQuadTree.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.metamodel.topology;

import java.util.Collection;
import java.util.Collections;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.Collector;
import msi.gama.util.ICollector;
import msi.gama.util.TOrderedHashMap;
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

	public static ISpatialIndex create(final Envelope envelope) {
		// if (GamaPreferences.GRID_OPTIMIZATION.getValue())
		// return new GamaParallelQuadTree(envelope);
		// else
		return new GamaQuadTree(envelope);
	}

	private GamaQuadTree(final Envelope bounds) {
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
		if (agent == null) { return; }
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
		if (current == null) { return; }
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
		final ICollector<IAgent> list = new Collector.UniqueOrdered<>();
		root.findIntersects(r, list);
		if (list.isEmpty()) { return Collections.EMPTY_LIST; }
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
		if (result.isEmpty()) { return Collections.EMPTY_LIST; }
		result.removeIf(each -> source.euclidianDistanceTo(each) > dist);
		return result;
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		final Envelope3D env = new Envelope3D(source.getEnvelope());
		env.expandBy(exp);
		final Collection<IAgent> in_square = findIntersects(scope, source, env, f);
		if (in_square.isEmpty()) { return null; }
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
		root.findIntersects(root.bounds, result);
		return result;
	}

	private class QuadNode {

		final Envelope bounds;
		private final double halfx, halfy;
		private volatile QuadNode[] nodes = null;
		// ** Addresses part of Issue 722 -- Need to keep the agents ordered
		// (by insertion order) **
		private final TOrderedHashMap<IAgent, Envelope> objects = new TOrderedHashMap(maxCapacity);
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
			objects.clear();
			if (nodes != null) {
				for (final QuadNode n : nodes) {
					n.dispose();
				}
				nodes = null;
			}
		}

		public void remove(final Coordinate p, final IShape a) {
			if (nodes == null) {
				objects.remove(a);
			} else {
				nodes[quadrant(p)].remove(p, a);
			}
		}

		public void remove(final Envelope env, final IShape a) {
			if (nodes == null) {
				objects.remove(a);
			} else {
				for (final QuadNode node : nodes) {
					if (node.bounds.intersects(env)) {
						node.remove(env, a);
					}
				}
			}
		}

		public boolean shouldSplit() {
			return canSplit && nodes == null && objects.size() >= maxCapacity;
		}

		public void add(final Coordinate p, final IAgent a) {
			if (shouldSplit()) {
				split();
			}
			if (nodes == null) {
				objects.put(a, Envelope3D.of(p));
			} else {
				nodes[quadrant(p)].add(p, a);
			}
		}

		public void add(final Envelope env, final IAgent a) {
			if (shouldSplit()) {
				split();
			}
			if (nodes == null) {
				objects.put(a, env);
			} else {
				for (final QuadNode node : nodes) {
					if (node.bounds.intersects(env)) {
						node.add(env, a);
					}
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

			for (final IAgent entry : objects.keySet()) {
				if (entry != null && !entry.dead()) {
					final IShape g = entry.getGeometry();
					if (g.isPoint()) {
						add((Coordinate) g.getLocation(), entry);
					} else {
						add(g.getEnvelope(), entry);
					}
				}
			}
			objects.clear();
		}

		public void findIntersects(final Envelope r, final Collection<IAgent> result) {
			if (bounds.intersects(r)) {
				objects.forEachEntry((a, env) -> {
					if (env != null && env.intersects(r)) {
						result.add(a);
					}
					return true;
				});
				// }
				if (nodes != null) {
					for (final QuadNode node : nodes) {
						node.findIntersects(r, result);
					}
				}
			}

		}

	}

}
