/*********************************************************************************************
 *
 *
 * 'GamaQuadTree.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/

package msi.gama.metamodel.topology;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gaml.operators.*;

/**
 * A QuadTree allows to quickly find an object on a two-dimensional space.
 * <p>
 * QuadTree recursively subdivides a space into four rectangles. Each node of a QuadTree subdivides the space covered by the rectangle of its parent node into four smaller rectangles covering the
 * upper left, upper right, lower left and lower right quadrant of the parent rectangle.
 *
 * @author Werner Randelshofer, adapted by Alexis Drogoul for GAMA
 * @version $Id: QuadTree.java 717 2010-11-21 12:30:57Z rawcoder $
 */
public class GamaQuadTree implements ISpatialIndex {

	// static private Color[] colors;
	//
	// {
	// colors = new Color[32];
	// colors[15] = Color.DARK_GRAY;
	// for ( int i = 14; i >= 0; i-- ) {
	// colors[i] = colors[i + 1].brighter();
	// }
	// for ( int i = 16; i < 32; i++ ) {
	// colors[i] = colors[i - 1].darker();
	// }
	// }

	private final QuadNode root;
	private final static int maxCapacity = 20;
	private double minSize = 10;
	// private int totalAgents = 0;
	// private int totalNodes = 0;
	// private volatile boolean isDrawing;

	// TODO check why we really need it?
	// private final ReentrantLock lock = new ReentrantLock();

	public GamaQuadTree(final Envelope bounds) {
		// AD To address Issue 804, explictely converts the bounds to an Envelope 2D, so that all computations are made
		// in 2D in the QuadTree
		root = new QuadNode(new Envelope(bounds));
		minSize = bounds.getWidth() / 1000d;
	}

	@Override
	public void insert(final IAgent agent) {
		if ( agent == null ) { return; }
		if ( agent.isPoint() ) {
			root.add((Coordinate) agent.getLocation(), agent);
		} else {
			root.add(agent.getEnvelope(), agent);
		}
		// totalAgents++;
	}

	private boolean isPoint(final Envelope env) {
		return env.getArea() == 0.0;
	}

	@Override
	public void remove(final Envelope previous, final IAgent agent) {
		final Envelope current = previous == null ? agent.getEnvelope() : previous;
		if ( current == null ) { return; }
		if ( isPoint(current) ) {
			root.remove(current.centre(), agent);
		} else {
			root.remove(current, agent);
		}
		// totalAgents--;
	}

	protected Collection<IAgent> findIntersects(final IScope scope, final IShape source, final Envelope r,
		final IAgentFilter filter) {
		// final java.util.List<IAgent> list = new ArrayList();
		// Adresses Issue 722 by explicitly shuffling the results with GAMA random procedures and removing duplicates
		final Set<IAgent> list = new TLinkedHashSet();
		root.findIntersects(scope, source, r, list);
		filter.filter(scope, source, list);
		scope.getRandom().shuffle2(list);
		// CollectionUtils.removeDuplicates(list);
		return list;
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
		final IAgentFilter f) {
		// TODO filter result by topology's bounds
		final double exp = dist * Maths.SQRT2;
		Envelope3D env = new Envelope3D(source.getEnvelope());
		env.expandBy(exp);
		Collection<IAgent> result = findIntersects(scope, source, env, f);
		Iterator<IAgent> it = result.iterator();
		while (it.hasNext()) {
			if ( source.euclidianDistanceTo(it.next()) > dist ) {
				it.remove();
			}
		}
		return result;
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		Envelope3D env = new Envelope3D(source.getEnvelope());
		env.expandBy(exp);
		final Collection<IAgent> in_square = findIntersects(scope, source, env, f);
		double min_distance = dist;
		IAgent min_agent = null;
		for ( final IAgent a : in_square ) {
			final Double dd = source.euclidianDistanceTo(a);
			if ( dd < min_distance ) {
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

	private class QuadNode {

		private final Envelope bounds;
		private final double hw, hh, minx, miny, halfx, halfy;
		// ** Addresses part of Issue 722 -- Need to keep the objects ordered (by insertion order) **
		private final Set<IAgent> objects = /* new TLinkedHashSet(); */new TLinkedHashSet(maxCapacity + 5);
		private volatile int size = 0;
		private volatile boolean isLeaf = true;
		private QuadNode ne;
		private QuadNode nw;
		private QuadNode se;
		private QuadNode sw;
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
			// totalNodes++;
		}

		public IShape remove(final Coordinate p, final IShape a) {

			if ( size > 0 ) {
				boolean removed = false;
				synchronized (objects) {
					// scope.getGui().debug("GamaQuadTree.QuadNode.remove " + a);
					removed = objects.remove(a);
				}
				if ( removed ) {
					size = size - 1;
					return a;
				}

			} else if ( !isLeaf ) {
				final boolean north = p.y >= miny && p.y < halfy;
				final boolean west = p.x >= minx && p.x < halfx;
				return north ? west ? nw.remove(p, a) : ne.remove(p, a) : west ? sw.remove(p, a) : se.remove(p, a);
			}
			return null;
		}

		public boolean add(final Coordinate p, final IAgent a) {
			if ( isLeaf ) {
				if ( canSplit && size >= maxCapacity ) {
					split();
					return add(p, a);
				}
				synchronized (objects) {
					// scope.getGui().debug("GamaQuadTree.QuadNode.add " + a);
					if ( objects.add(a) ) {
						size = size + 1;
					}
				}
				return true;
			}
			final boolean north = p.y >= miny && p.y < halfy;
			final boolean west = p.x >= minx && p.x < halfx;
			return north ? west ? nw.add(p, a) : ne.add(p, a) : west ? sw.add(p, a) : se.add(p, a);
		}

		private boolean removeIfPresent(final Envelope bounds, final IShape a) {
			synchronized (objects) {
				// scope.getGui().debug("GamaQuadTree.QuadNode.removeIfPresent + object.contains " + a);
				return !(isLeaf && size == 0) && (objects.contains(a) || this.bounds.intersects(bounds)) &&
					remove(bounds, a);
			}
		}

		public boolean remove(final Envelope bounds, final IShape a) {
			if ( size != 0 ) {
				boolean removed = false;
				synchronized (objects) {
					// scope.getGui().debug("GamaQuadTree.QuadNode.remove " + a);
					removed = objects.remove(a);
				}
				if ( removed ) {
					size = size - 1;
					return true;
				}
			}
			return !isLeaf && ne.removeIfPresent(bounds, a) | nw.removeIfPresent(bounds, a) |
				se.removeIfPresent(bounds, a) | sw.removeIfPresent(bounds, a);
		}

		// private void join() {
		// if ( isLeaf ) { return; }
		// ne.join();
		// se.join();
		// nw.join();
		// sw.join();
		// if ( !sw.isLeaf ) { return; }
		// if ( !ne.isLeaf ) { return; }
		// if ( !se.isLeaf ) { return; }
		// if ( !nw.isLeaf ) { return; }
		// temp_agents.clear();
		// if ( size != 0 ) {
		// temp_agents.addAll(objects);
		// }
		// if ( ne.size != 0 ) {
		// temp_agents.addAll(ne.objects);
		// }
		// if ( se.size != 0 ) {
		// temp_agents.addAll(se.objects);
		// }
		//
		// if ( sw.size != 0 ) {
		// temp_agents.addAll(sw.objects);
		// }
		//
		// if ( nw.size != 0 ) {
		// temp_agents.addAll(nw.objects);
		// }
		//
		// int total = temp_agents.size();
		// if ( total < maxCapacity ) {
		// objects.clear();
		// objects.addAll(temp_agents);
		// size = total;
		// ne = null;
		// se = null;
		// nw = null;
		// sw = null;
		// isLeaf = true;
		// totalNodes -= 4;
		// }
		// }

		public boolean add(final Envelope env, final IAgent o) {
			if ( canSplit && isLeaf && size >= maxCapacity ) {
				split();
			}
			if ( isLeaf || env.contains(bounds) ) {
				boolean added = false;
				synchronized (objects) {
					// scope.getGui().debug("GamaQuadTree.QuadNode.add " + o);
					added = objects.add(o);
				}
				if ( added ) {
					size = size + 1;
				}
				return added;
			}

			boolean retVal = false;

			if ( ne.bounds.intersects(env) ) {
				retVal = ne.add(env, o);
			}
			if ( nw.bounds.intersects(env) ) {
				retVal = nw.add(env, o) || retVal;
			}
			if ( se.bounds.intersects(env) ) {
				retVal = se.add(env, o) || retVal;
			}
			if ( sw.bounds.intersects(env) ) {
				retVal = sw.add(env, o) || retVal;
			}
			return retVal;
		}

		public void split() {
			if ( isLeaf ) {
				final double maxx = bounds.getMaxX();
				final double maxy = bounds.getMaxY();
				nw = new QuadNode(new Envelope(minx, halfx, miny, halfy));
				ne = new QuadNode(new Envelope(halfx, maxx, miny, halfy));
				sw = new QuadNode(new Envelope(minx, halfx, halfy, maxy));
				se = new QuadNode(new Envelope(halfx, maxx, halfy, maxy));
				IAgent[] tempList;
				synchronized (objects) {
					// scope.getGui().debug("GamaQuadTree.QuadNode.split ");
					tempList = objects.toArray(new IAgent[size]);
					objects.clear();
				}

				size = 0;
				isLeaf = false;

				for ( int i = 0; i < tempList.length; i++ ) {
					final IAgent entry = tempList[i];

					if ( /* entry != null && */!entry.dead() ) {
						final IShape g = entry.getGeometry();
						if ( g.isPoint() ) {
							final Coordinate p = (Coordinate) g.getLocation();
							add(p, entry);
						} else {
							add(g.getEnvelope(), entry);
						}
					} else {
						// System.out.println("QuadTree :: split :: Dead agent : " + System.currentTimeMillis());
						continue;
					}
				}
			}
		}

		@Override
		public String toString() {
			return toString(0);
		}

		public String toString(final int tab) {
			final StringBuilder sb = new StringBuilder();
			for ( int i = 0; i < tab; i++ ) {
				sb.append(Strings.TAB);
			}
			final String tabs = sb.toString();
			String s = "Bounds " + bounds + " holding " + objects;
			if ( !isLeaf ) {
				s = s + Strings.LN + tabs + "NW: " + nw.toString(tab + 1);
				s = s + Strings.LN + tabs + "NE: " + ne.toString(tab + 1);
				s = s + Strings.LN + tabs + "SW: " + sw.toString(tab + 1);
				s = s + Strings.LN + tabs + "SE: " + se.toString(tab + 1);
			}
			return s;
		}

		public void findIntersects(final IScope scope, final IShape source, final Envelope r,
			final Collection<IAgent> result) {
			if ( bounds.intersects(r) ) {
				synchronized (objects) {
					// scope.getGui().debug("GamaQuadTree.QuadNode.findIntersects ");
					for ( IShape entry : objects ) {
						if ( entry.getEnvelope().intersects(r) ) {
							result.add(entry.getAgent());
						}
					}
				}
				if ( !isLeaf ) {
					if ( !(nw.isLeaf && nw.size == 0) ) {
						nw.findIntersects(scope, source, r, result);
					}
					if ( !(ne.isLeaf && ne.size == 0) ) {
						ne.findIntersects(scope, source, r, result);
					}
					if ( !(sw.isLeaf && sw.size == 0) ) {
						sw.findIntersects(scope, source, r, result);
					}
					if ( !(se.isLeaf && se.size == 0) ) {
						se.findIntersects(scope, source, r, result);
					}
				}
			}
		}

		// public void drawOn(final Graphics2D g2, final double xr, final double yr) {
		//
		// // Put size, isLeaf and bounds as volatile to allow removing the reentrant lock ?
		// if ( isLeaf ) {
		// g2.setColor(Color.gray);
		// g2.setStroke(new BasicStroke(0.1f));
		// g2.drawRect(Maths.round(bounds.getMinX() * xr), Maths.round(bounds.getMinY() * yr),
		// Maths.round(bounds.getWidth() * xr), Maths.round(bounds.getHeight() * yr));
		// g2.setColor(colors[Math.min(31, (int) ((double) size / (double) maxCapacity * 32))]);
		// g2.fillRect(Maths.round(bounds.getMinX() * xr) + 1, Maths.round(bounds.getMinY() * yr) + 1,
		// Maths.round(bounds.getWidth() * xr) - 1, Maths.round(bounds.getHeight() * yr) - 1);
		// } else {
		// nw.drawOn(g2, xr, yr);
		// ne.drawOn(g2, xr, yr);
		// sw.drawOn(g2, xr, yr);
		// se.drawOn(g2, xr, yr);
		// }
		//
		// }
	}

	// @Override
	// public void drawOn(final Graphics2D g2, final int width, final int height) {
	// if ( isDrawing ) { return; }
	// isDrawing = true;
	// g2.setColor(Color.white);
	// g2.fillRect(0, 0, width, height);
	//
	// final double x_ratio = width / root.bounds.getWidth();
	// final double y_ratio = height / root.bounds.getHeight();
	//
	// try {
	// root.drawOn(g2, x_ratio, y_ratio);
	// g2.setColor(Color.ORANGE);
	// g2.setFont(new Font("Helvetica", Font.BOLD, height / 75));
	// g2.drawString("Agents: " + totalAgents + "; Nodes: " + totalNodes, 10, 10);
	// } finally {
	// isDrawing = false;
	// }
	// }

	// FIXME NO DISPOSE METHOD ?

	// @Override
	// public void update() {
	// try {
	// lock.lock();
	// root.join();
	// } finally {
	// lock.unlock();
	// }
	// }

}
