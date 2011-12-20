/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.metamodel.topology;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.util.GamaList;
import msi.gaml.operators.Maths;
import com.vividsolutions.jts.geom.*;

/**
 * A QuadTree allows to quickly find an object on a two-dimensional space.
 * <p>
 * QuadTree recursively subdivides a space into four rectangles. Each node of a QuadTree subdivides
 * the space covered by the rectangle of its parent node into four smaller rectangles covering the
 * upper left, upper right, lower left and lower right quadrant of the parent rectangle.
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
	private final static Envelope ENVELOPE = new Envelope();

	// TODO check why we really need it?
	private final ReentrantLock lock = new ReentrantLock();

	public GamaQuadTree(final Envelope bounds) {
		root = new QuadNode(bounds);
		minSize = bounds.getWidth() / 100d;
	}

	@Override
	public void insert(final Envelope bounds, final IAgent o) {
		try {
			// lock.lock();

			root.add(bounds, o);
			totalAgents++;
		} finally {
			// lock.unlock();
		}
	}

	@Override
	public void insert(final Coordinate point, final IAgent a) {
		try {
			// lock.lock();

			root.add(point, a);
			totalAgents++;
		} finally {
			// lock.unlock();
		}
	}

	@Override
	public void remove(final Envelope bounds, final IAgent o) {
		try {
			// lock.lock();

			root.remove(bounds, o);
			totalAgents--;
		} finally {
			// lock.unlock();
		}
	}

	@Override
	public void remove(final Coordinate point, final IAgent o) {
		try {
			// lock.lock();
			root.remove(point, o);
			totalAgents--;
		} finally {
			// lock.unlock();
		}
	}

	protected Collection<IAgent> findIntersects(final IShape source, final Envelope r,
		final IAgentFilter filter) {
		HashSet<IAgent> internal_results = new HashSet();
		root.findIntersects(source, r, filter, internal_results);
		return internal_results;
	}

	@Override
	public GamaList<IAgent> allAtDistance(final IShape source, final double dist,
		final IAgentFilter f) {

		// TODO filter result by topology's bounds

		double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		Collection<IAgent> set = findIntersects(source, ENVELOPE, f);
		GamaList external_results = new GamaList();
		for ( IAgent a : set ) {
			if ( source.euclidianDistanceTo(a) < dist ) {
				external_results.add(a);
			}
		}
		return external_results;
	}

	@Override
	public IAgent firstAtDistance(final IShape source, final double dist, final IAgentFilter f) {
		double exp = dist * Maths.SQRT2;
		ENVELOPE.init(source.getEnvelope());
		ENVELOPE.expandBy(exp);
		Collection<IAgent> in_square = findIntersects(source, ENVELOPE, f);
		double min_distance = dist;
		IAgent min_agent = null;
		for ( IAgent a : in_square ) {
			Double dd = source.euclidianDistanceTo(a);
			if ( dd < min_distance ) {
				min_distance = dd;
				min_agent = a;
			}
		}
		return min_agent;
	}

	@Override
	public GamaList<IAgent> allInEnvelope(final IShape source, final Envelope envelope,
		final IAgentFilter f, final boolean contained) {
		return new GamaList(findIntersects(source, envelope, f));
	}

	private class QuadNode implements Serializable {

		private final Envelope bounds;
		private final double hw, hh, minx, miny;
		private final ArrayList<IAgent> objects = new GamaList();
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
			// OutputManager.debug("Are " + hw + " and " + hh + " greater than " + minSize);
			// OutputManager.debug("Create node with width:" + hw + " and height:" + hh +
			// "; can split:" + canSplit);
			totalNodes++;
		}

		public IAgent remove(final Coordinate p, final IAgent a) {
			if ( size != 0 && objects.remove(a) ) {
				size--;
				return a;
			} else if ( !isLeaf ) {
				boolean north = p.y >= miny && p.y < miny + hh / 2;
				boolean west = p.x >= minx && p.x < minx + hw / 2;
				return north ? west ? nw.remove(p, a) : ne.remove(p, a) : west ? sw.remove(p, a)
					: se.remove(p, a);
			}
			return null;
		}

		public boolean add(final Coordinate p, final IAgent a) {
			if ( isLeaf ) {
				if ( canSplit && size >= maxCapacity ) {
					split();
					return add(p, a);
				}
				objects.add(a);
				size++;
				return true;
			}
			boolean north = p.y >= miny && p.y < miny + hh / 2;
			boolean west = p.x >= minx && p.x < minx + hw / 2;
			return north ? west ? nw.add(p, a) : ne.add(p, a) : west ? sw.add(p, a) : se.add(p, a);
		}

		public boolean isEmpty() {
			return isLeaf && size == 0;
		}

		private boolean removeIfPresent(final Envelope bounds, final IAgent a) {
			return !(isLeaf && size == 0) &&
				(objects.contains(a) || this.bounds.intersects(bounds)) && remove(bounds, a);
		}

		public boolean remove(final Envelope bounds, final IAgent a) {
			if ( size != 0 && objects.remove(a) ) {
				size--;
				return true;
			}
			return !isLeaf && ne.removeIfPresent(bounds, a) | nw.removeIfPresent(bounds, a) |
				se.removeIfPresent(bounds, a) | sw.removeIfPresent(bounds, a);
		}

		private final Set<IAgent> temp_agents = new HashSet<IAgent>();

		private void join() {
			if ( isLeaf ) { return; }
			ne.join();
			se.join();
			nw.join();
			sw.join();
			if ( !sw.isLeaf ) { return; }
			if ( !ne.isLeaf ) { return; }
			if ( !se.isLeaf ) { return; }
			if ( !nw.isLeaf ) { return; }
			temp_agents.clear();
			if ( size != 0 ) {
				temp_agents.addAll(objects);
			}
			if ( ne.size != 0 ) {
				temp_agents.addAll(ne.objects);
			}
			if ( se.size != 0 ) {
				temp_agents.addAll(se.objects);
			}

			if ( sw.size != 0 ) {
				temp_agents.addAll(sw.objects);
			}

			if ( nw.size != 0 ) {
				temp_agents.addAll(nw.objects);
			}

			int total = temp_agents.size();
			if ( total < maxCapacity ) {
				objects.clear();
				objects.addAll(temp_agents);
				size = total;
				ne = null;
				se = null;
				nw = null;
				sw = null;
				isLeaf = true;
				totalNodes -= 4;
			}
		}

		public boolean add(final Envelope env, final IAgent o) {
			if ( canSplit && isLeaf && size >= maxCapacity ) {
				split();
			}

			if ( isLeaf || env.contains(bounds) ) {
				objects.add(o);
				size++;
				return true;
			}
			boolean retVal = false;

			if ( ne.bounds.intersects(env) ) {
				retVal = retVal || ne.add(env, o);
			}
			if ( nw.bounds.intersects(env) ) {
				retVal = retVal || nw.add(env, o);
			}
			if ( se.bounds.intersects(env) ) {
				retVal = retVal || se.add(env, o);
			}
			if ( sw.bounds.intersects(env) ) {
				retVal = retVal || sw.add(env, o);
			}

			if ( retVal ) { return true; }

			System.out.println("agent " + o.getName() + " is not added to QuadTree; " +
				o.getLocation());
			return false;
		}

		private IAgent[] tempList = new IAgent[0];

		public void split() {
			if ( isLeaf ) {
				double hw = this.hw / 2;
				double hh = this.hh / 2;
				double maxx = bounds.getMaxX();
				double maxy = bounds.getMaxY();
				nw = new QuadNode(new Envelope(minx, minx + hw, miny, miny + hh));
				ne = new QuadNode(new Envelope(minx + hw, maxx, miny, miny + hh));
				sw = new QuadNode(new Envelope(minx, minx + hw, miny + hh, maxy));
				se = new QuadNode(new Envelope(minx + hw, maxx, miny + hh, maxy));
				tempList = objects.toArray(tempList);

				int tempNumber = size;

				objects.clear();
				size = 0;
				isLeaf = false;

				int addedNumber = 0;
				for ( int i = 0; i < tempNumber; i++ ) {
					IAgent entry = tempList[i];

					if ( !entry.dead() ) {
						IShape g = entry.getGeometry();

						if ( g.isPoint() ) {
							Coordinate p = g.getLocation().toCoordinate();
							if ( add(p, entry) ) {
								addedNumber++;
							} else {
								System.out.println(entry.getName() +
									" is not re-added to sub-node on splitting");
							}
						} else {
							if ( add(g.getEnvelope(), entry) ) {
								addedNumber++;
							} else {
								System.out.println(entry.getName() +
									" is not re-added to sub-node on splitting");
							}
						}
					} else {
						System.out.println("QuadTree :: split :: Death agent : " +
							System.currentTimeMillis());
						continue;
					}
				}

				if ( addedNumber < tempNumber ) {
					System.out.println("tempNumber = " + tempNumber + "; addedNumber = " +
						addedNumber);
				}
			}
		}

		/*
		 * public boolean contains(final IAgent a) {
		 * return objects.contains(a);
		 * }
		 */

		@Override
		public String toString() {
			return toString(0);
		}

		public String toString(final int tab) {
			StringBuilder sb = new StringBuilder();
			for ( int i = 0; i < tab; i++ ) {
				sb.append("\t");
			}
			String tabs = sb.toString();
			String s = "Bounds " + bounds + " holding " + objects;
			if ( !isLeaf ) {
				s = s + "\n" + tabs + "NW: " + nw.toString(tab + 1);
				s = s + "\n" + tabs + "NE: " + ne.toString(tab + 1);
				s = s + "\n" + tabs + "SW: " + sw.toString(tab + 1);
				s = s + "\n" + tabs + "SE: " + se.toString(tab + 1);
			}
			return s;
		}

		public void findIntersects(final IShape source, final Envelope r, final IAgentFilter f,
			final HashSet<IAgent> result) {
			if ( bounds.intersects(r) ) {
				for ( int i = 0, n = size; i < n; i++ ) {
					IAgent entry = objects.get(i);
					if ( entry.getEnvelope().intersects(r) ) {
						if ( f == null || f.accept(source, entry) ) {
							result.add(entry);
						}
					}
				}
				if ( !isLeaf ) {
					if ( !nw.isEmpty() ) {
						nw.findIntersects(source, r, f, result);
					}
					if ( !ne.isEmpty() ) {
						ne.findIntersects(source, r, f, result);
					}
					if ( !sw.isEmpty() ) {
						sw.findIntersects(source, r, f, result);
					}
					if ( !se.isEmpty() ) {
						se.findIntersects(source, r, f, result);
					}
				}
			}
		}

		// public void findInside(final Envelope r, final HashSet<IAgent> result) {
		// if ( bounds.intersects(r) ) {
		// for ( IAgent entry : objects ) {
		// if ( r.contains(entry.getGeometry().getEnvelope()) ) {
		// result.add(entry);
		// }
		// }
		// if ( !isLeaf ) {
		// nw.findInside(r, result);
		// ne.findInside(r, result);
		// sw.findInside(r, result);
		// se.findInside(r, result);
		// }
		// }
		// }

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
				g2.fillRect(Maths.round(bounds.getMinX() * xr) + 1,
					Maths.round(bounds.getMinY() * yr) + 1,
					Maths.round(bounds.getWidth() * xr) - 1,
					Maths.round(bounds.getHeight() * yr) - 1);
				// g2.setColor(Color.red);
				// g2.drawString(String.valueOf(objects_number), (int) (bounds.centre().x * xr),
				// (int) (bounds.centre().y * yr));
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

		double x_ratio = width / root.bounds.getWidth();
		double y_ratio = height / root.bounds.getHeight();

		try {
			lock.lock();

			root.drawOn(g2, x_ratio, y_ratio);
			g2.setColor(Color.red);
			g2.setFont(new Font("Helvetica", Font.BOLD, 9));
			g2.drawString("Agents: " + totalAgents + "; Nodes: " + totalNodes, 10, 10);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void update() {
		try {
			lock.lock();

			root.join();
		} finally {
			lock.unlock();
		}
	}
	//
	// @Override
	// public void dispose() {
	// isAlive = false;
	// }
}
