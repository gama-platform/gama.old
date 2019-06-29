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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.locationtech.jts.geom.Envelope;

import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.util.JavaUtils;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
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
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaQuadTree implements ISpatialIndex {
	PoolUtils.ObjectPool<Envelope3D> envelopePool = PoolUtils.create("Envelope3D", true, () -> new Envelope3D(), null);

	public static final int NW = 0;
	public static final int NE = 1;
	public static final int SW = 2;
	public static final int SE = 3;

	final QuadNode root;
	final static int maxCapacity = 100;
	final double minSize;

	public static ISpatialIndex create(final Envelope envelope) {
		return new GamaQuadTree(envelope);
	}

	private GamaQuadTree(final Envelope bounds) {
		// AD To address Issue 804, explictely converts the bounds to an
		// Envelope 2D, so that all computations are made
		// in 2D in the QuadTree
		root = new QuadNode(new Envelope3D(bounds));
		minSize = bounds.getWidth() / 100d;
	}

	@Override
	public void dispose() {
		root.dispose();
	}

	@Override
	public void insert(final IAgent agent) {
		if (agent == null) { return; }
		if (agent.isPoint()) {
			root.add(agent.getLocation(), agent);
		} else {
			root.add(agent.getEnvelope(), agent);
		}
	}

	@Override
	public void remove(final IEnvelope previous, final IAgent agent) {
		final IEnvelope current = previous == null ? agent.getEnvelope() : previous;
		if (current == null) { return; }
		if (current.isPoint()) {
			root.remove(current.getLocation(), agent);
		} else {
			root.remove(current, agent);
		}
	}

	protected void findIntersects(final IScope scope, final IShape source, final IEnvelope r, final IAgentFilter filter,
			final Set<IAgent> accumulator) {
		// Adresses Issue 722 by explicitly shuffling the results with GAMA
		// random procedures and removing duplicates
		root.findIntersects(r, accumulator);
		filter.filter(scope, source, accumulator);
		scope.getRandom().shuffleInPlace(accumulator);
	}

	protected void findIntersects(final IScope scope, final IShape source, final double distance,
			final IAgentFilter filter, final Set<IAgent> accumulator) {
		Envelope3D env = null;
		try {
			env = envelopePool.get();
			env.init(source.getEnvelope());
			env.expandBy(distance * Maths.SQRT2);
			findIntersects(scope, source, env, filter, accumulator);
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
		final Set<IAgent> result = JavaUtils.SET_POOL.get();
		try {
			findIntersects(scope, source, dist, f, result);
			if (result.isEmpty()) { return null; }
			double min_distance = dist;
			IAgent min_agent = null;
			for (final IAgent a : result) {
				final Double dd = source.euclidianDistanceTo(a);
				if (dd < min_distance) {
					min_distance = dd;
					min_agent = a;
				}
			}
			return min_agent;
		} finally {
			JavaUtils.SET_POOL.release(result);
		}
	}

	@Override
	public void allInEnvelope(final IScope scope, final IShape source, final IEnvelope envelope, final IAgentFilter f,
			final boolean contained, final Set<IAgent> accumulator) {
		findIntersects(scope, source, envelope, f, accumulator);
	}

	@Override
	public Collection<IAgent> allAgents() {
		final Set<IAgent> result = new TLinkedHashSet();
		root.findIntersects(root.bounds, result);
		return result;
	}

	private class QuadNode {

		final IEnvelope bounds;
		private final double halfx, halfy;
		private QuadNode[] nodes = null;
		// ** Addresses part of Issue 722 -- Need to keep the agents ordered
		// (by insertion order) **
		private Map<IAgent, IEnvelope> objects = GamaExecutorService.CONCURRENCY_SPECIES.getValue()
				? new ConcurrentHashMap(maxCapacity) : new TOrderedHashMap<>(maxCapacity);
		private final boolean canSplit;

		public QuadNode(final IEnvelope bounds) {
			this.bounds = bounds;
			final double hw = bounds.getEnvWidth();
			final double hh = bounds.getEnvHeight();
			halfx = bounds.getMinX() + hw / 2;
			halfy = bounds.getMinY() + hh / 2;
			canSplit = hw > minSize && hh > minSize;
		}

		public void dispose() {
			if (objects != null) {
				objects.clear();
			}
			if (nodes != null) {
				for (final QuadNode n : nodes) {
					n.dispose();
				}
				nodes = null;
			}
		}

		public void remove(final IEnvelope env, final IShape a) {
			if (nodes == null) {
				objects.remove(a);
			} else {
				for (final QuadNode node : nodes) {
					if (env.intersects(node.bounds)) {
						node.remove(env, a);
					}
				}
			}
		}

		public boolean shouldSplit() {
			return canSplit && nodes == null && objects.size() >= maxCapacity;
		}

		public void add(final IEnvelope env, final IAgent a) {
			if (shouldSplit()) {
				split();
			}
			if (nodes == null) {
				objects.put(a, env);
			} else {
				if (env.isPoint()) {
					nodes[quadrant(env.getLocation())].add(env, a);
				} else {
					for (final QuadNode node : nodes) {
						if (env.intersects(node.bounds)) {
							node.add(env, a);
						}
					}
				}
			}
		}

		int quadrant(final GamaPoint p) {
			final boolean north = p.getY() >= bounds.getMinY() && p.getY() < halfy;
			final boolean west = p.getX() >= bounds.getMinX() && p.getX() < halfx;
			return north ? west ? NW : NE : west ? SW : SE;
		}

		public void split() {
			final double maxx = bounds.getMaxX();
			final double minx = bounds.getMinX();
			final double miny = bounds.getMinY();
			final double maxy = bounds.getMaxY();
			nodes = new QuadNode[] { new QuadNode(new Envelope3D(minx, halfx, miny, halfy, 0, 0)),
					new QuadNode(new Envelope3D(halfx, maxx, miny, halfy, 0, 0)),
					new QuadNode(new Envelope3D(minx, halfx, halfy, maxy, 0, 0)),
					new QuadNode(new Envelope3D(halfx, maxx, halfy, maxy, 0, 0)) };
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
			objects = null;
		}

		public void findIntersects(final IEnvelope r, final Set<IAgent> result) {
			if (bounds.intersects(r)) {
				if (objects != null) {
					objects.forEach((a, env) -> {
						if (env != null && env.intersects(r)) {
							result.add(a);
						}
					});
				}
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
