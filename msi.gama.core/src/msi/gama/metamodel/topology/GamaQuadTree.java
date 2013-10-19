/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.metamodel.topology;

import gnu.trove.set.hash.*;
import gnu.trove.strategy.IdentityHashingStrategy;
import java.awt.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Maths;
import com.vividsolutions.jts.geom.*;

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
public class GamaQuadTree implements ISpatialIndex {

	private final QuadNode root;
	private final static int maxCapacity = 20;
	private double minSize = 10;
	private int totalAgents = 0;
	private int totalNodes = 0;
	// TODO check why we really need it?
	private final ReentrantLock lock = new ReentrantLock();

	public GamaQuadTree(final Envelope bounds) {
		root = new QuadNode(bounds);
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
		totalAgents++;
	}

	@Override
	public void remove(final IShape previous, final IAgent agent) {
		final IShape current = previous == null ? agent : previous;
		if ( current == null ) { return; }
		if ( current.isPoint() ) {
			root.remove((Coordinate) current.getLocation(), agent);
		} else {
			root.remove(current.getEnvelope(), agent);
		}
		totalAgents--;
	}

	protected Set<IAgent> findIntersects(final IScope scope, final IShape source, final Envelope r,
		final IAgentFilter filter) {
		final Set<IAgent> internal_results = new THashSet();
		root.findIntersects(scope, source, r, /* filter, */internal_results);

		filter.filter(scope, source, internal_results);
		return internal_results;
	}

	@Override
	public Set<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {

		// TODO filter result by topology's bounds

		final double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		Set<IAgent> result = findIntersects(scope, source, ENVELOPE, f);
		Iterator<IAgent> it = result.iterator();
		while (it.hasNext()) {
			if ( !(source.euclidianDistanceTo(it.next()) < dist) ) {
				it.remove();
			}
		}
		return result;
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		final double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		final Set<IAgent> in_square = findIntersects(scope, source, ENVELOPE, f);
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
	public Set<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
		final IAgentFilter f, final boolean contained) {
		return findIntersects(scope, source, envelope, f);
	}

	private class QuadNode {

		private final Envelope bounds;
		private final double hw, hh, minx, miny;
		private final Set<IAgent> objects = new TCustomHashSet(IdentityHashingStrategy.INSTANCE);
		private int size = 0;
		boolean isLeaf = true;
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
			canSplit = hw > minSize && hh > minSize;
			totalNodes++;
		}

		public IShape remove(final Coordinate p, final IShape a) {

			if ( size != 0 ) {
				// synchronized (objects) {
				if ( objects.remove(a) ) {
					size--;
					return a;
				}
				// }
			} else if ( !isLeaf ) {
				final boolean north = p.y >= miny && p.y < miny + hh / 2;
				final boolean west = p.x >= minx && p.x < minx + hw / 2;
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
				// synchronized (objects) {
				if ( objects.add(a) ) {
					size++;
				}
				// }
				return true;
			}
			final boolean north = p.y >= miny && p.y < miny + hh / 2;
			final boolean west = p.x >= minx && p.x < minx + hw / 2;
			return north ? west ? nw.add(p, a) : ne.add(p, a) : west ? sw.add(p, a) : se.add(p, a);
		}

		public boolean isEmpty() {
			return isLeaf && size == 0;
		}

		private boolean removeIfPresent(final Envelope bounds, final IShape a) {
			return !(isLeaf && size == 0) && (objects.contains(a) || this.bounds.intersects(bounds)) &&
				remove(bounds, a);
		}

		public boolean remove(final Envelope bounds, final IShape a) {
			// synchronized (objects) {
			if ( size != 0 && objects.remove(a) ) {
				size--;
				return true;
			}
			// }
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
			// synchronized (objects) {
			if ( isLeaf || env.contains(bounds) ) {
				if ( objects.add(o) ) {
					size++;
					return true;
				}
				return false;
			}
			// }
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

		IAgent[] tempList = new IAgent[0];

		public void split() {
			if ( isLeaf ) {
				final double hw = this.hw / 2;
				final double hh = this.hh / 2;
				final double maxx = bounds.getMaxX();
				final double maxy = bounds.getMaxY();
				nw = new QuadNode(new Envelope(minx, minx + hw, miny, miny + hh));
				ne = new QuadNode(new Envelope(minx + hw, maxx, miny, miny + hh));
				sw = new QuadNode(new Envelope(minx, minx + hw, miny + hh, maxy));
				se = new QuadNode(new Envelope(minx + hw, maxx, miny + hh, maxy));
				tempList = objects.toArray(tempList);
				final int tempNumber = size;

				objects.clear();
				size = 0;
				isLeaf = false;

				for ( int i = 0; i < tempNumber; i++ ) {
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
				sb.append("\t");
			}
			final String tabs = sb.toString();
			String s = "Bounds " + bounds + " holding " + objects;
			if ( !isLeaf ) {
				s = s + "\n" + tabs + "NW: " + nw.toString(tab + 1);
				s = s + "\n" + tabs + "NE: " + ne.toString(tab + 1);
				s = s + "\n" + tabs + "SW: " + sw.toString(tab + 1);
				s = s + "\n" + tabs + "SE: " + se.toString(tab + 1);
			}
			return s;
		}

		public void findIntersects(final IScope scope, final IShape source, final Envelope r, final Set<IAgent> result) {
			if ( bounds.intersects(r) ) {
				// synchronized (objects) {
				for ( IShape entry : objects ) {
					// final IShape entry = objects.get(i);
					if ( /* (f == null || f.accept(scope, source, entry)) && */entry.getEnvelope().intersects(r) ) {
						result.add(entry.getAgent());
					}
				}
				// }
				if ( !isLeaf ) {
					if ( !nw.isEmpty() ) {
						nw.findIntersects(scope, source, r,/* f, */result);
					}
					if ( !ne.isEmpty() ) {
						ne.findIntersects(scope, source, r/* , f */, result);
					}
					if ( !sw.isEmpty() ) {
						sw.findIntersects(scope, source, r/* , f */, result);
					}
					if ( !se.isEmpty() ) {
						se.findIntersects(scope, source, r/* , f */, result);
					}
				}
			}
		}

		private Color[] colors;
		{
			colors = new Color[32];
			colors[15] = Color.DARK_GRAY;
			for ( int i = 14; i >= 0; i-- ) {
				colors[i] = colors[i + 1].brighter();
			}
			for ( int i = 16; i < 32; i++ ) {
				colors[i] = colors[i - 1].darker();
			}
		}

		public void drawOn(final Graphics2D g2, final double xr, final double yr) {
			if ( isLeaf ) {
				g2.setColor(Color.gray);
				g2.setStroke(new BasicStroke(0.1f));
				g2.drawRect(Maths.round(bounds.getMinX() * xr), Maths.round(bounds.getMinY() * yr),
					Maths.round(bounds.getWidth() * xr), Maths.round(bounds.getHeight() * yr));
				g2.setColor(colors[Math.min(31, (int) ((double) size / (double) maxCapacity * 32))]);
				g2.fillRect(Maths.round(bounds.getMinX() * xr) + 1, Maths.round(bounds.getMinY() * yr) + 1,
					Maths.round(bounds.getWidth() * xr) - 1, Maths.round(bounds.getHeight() * yr) - 1);
			} else {
				nw.drawOn(g2, xr, yr);
				ne.drawOn(g2, xr, yr);
				sw.drawOn(g2, xr, yr);
				se.drawOn(g2, xr, yr);
			}

		}
	}

	@Override
	public void drawOn(final Graphics2D g2, final int width, final int height) {
		g2.setColor(Color.white);
		g2.fillRect(0, 0, width, height);

		final double x_ratio = width / root.bounds.getWidth();
		final double y_ratio = height / root.bounds.getHeight();

		try {
			lock.lock();
			root.drawOn(g2, x_ratio, y_ratio);
			g2.setColor(Color.ORANGE);
			g2.setFont(new Font("Helvetica", Font.BOLD, height / 75));
			g2.drawString("Agents: " + totalAgents + "; Nodes: " + totalNodes, 10, 10);
		} finally {
			lock.unlock();
		}
	}

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
