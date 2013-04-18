/*
 * GAMA - V1.2 alpha
 * Generic Agent-based Modelling Architecture
 * 
 * (c) 2007-2008 IRD-UR GEODES (France) & IFI-MSI (Vietnam)
 * (c) 2009-2010 UMI 209 UMMISCO IRD/UPMC - MSI (Vietnam)
 * 
 * Developpers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, GAML)
 * - Edouard Amouroux, IRD (C++ initial porting)
 * - Chu Thanh Quang, IRD (OpenMap integration)
 * - Francois Sempe, IRD & AUF (EMF behavioral model, Batch framework)
 * - Vo Duc An, IRD & AUF (SWT integration, GUI, FIPA extension)
 * - Guillaume Cherel, IRD (Batch framework)
 * - Patrick Taillandier, AUF & CNRS (Batch framework, GeoTools integration)
 */
package msi.gama.util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.util.GamaList;
import msi.gama.util.IList;

/**
 * AbstractStaticOptimizer
 * 
 * @author drogoul 10 nov. 07
 * 
 */
public abstract class AbstractStaticOptimizer<V, E> implements PathFinder<V, E> {

	GamaPoint center = null;
	GamaGraph<V, E> graph;

	protected List<ILocation> intersections;

	/** The matrix of adjacency between intersections. */
	public int[][] adj;

	/** The matrix of predecessors between intersections. */
	public int[][] pred;

	/** The number of intersections to process. */
	protected int number = 0;

	/**
	 * The max. value (representing the absence of path between two
	 * intersections)
	 */
	protected static final int max = Integer.MAX_VALUE;

	/**
	 * Instantiates a new path optimizer.
	 * 
	 * @param name
	 *            the name which will be reported in the logs, etc.
	 * @param intersections
	 */
	public AbstractStaticOptimizer(final GamaGraph graph) {
		this.graph = graph;
		setIntersections(new ArrayList(graph.vertexSet()));
	}

	/**
	 * 
	 * @see msi.gama.gis.pathfinder.Optimizer#initialize(java.util.Map, java.util.Set)
	 */
	@Override
	public void setIntersections(final List<V> vertices) {
		intersections = (List<ILocation>) vertices;
		number = intersections.size();
	}

	/**
	 * Initialize matrices.
	 * 
	 * @param a
	 *            the a
	 * @param p
	 *            the p
	 */
	protected abstract void initializeMatrices();

	public void initMatrices() {
		adj = new int[number][number];
		pred = new int[number][number];
		for ( int i = 0; i < number; i++ ) {
			Arrays.fill(adj[i], max);
			Arrays.fill(pred[i], -1);
			adj[i][i] = 0;
			pred[i][i] = i;
		}
	}

	/**
	 * Since this optimiser is static, it returns the best route by computing it
	 * directly from the matrix. The optimization needs to be finished, however,
	 * for it to return something interesting.
	 * 
	 * @param from
	 * @param to
	 * @return
	 * @see msi.gama.gis.pathfinder.Optimizer#buildBestRouteBetween(msi.gama.gis.pathfinder.PathIntersection,
	 *      msi.gama.gis.pathfinder.PathIntersection)
	 */
	@Override
	public IList bestRouteBetween(final V from, final V to) {
		final int indexFrom = graph.getVertex(from).getIndex();
		final int indexTo = graph.getVertex(to).getIndex();
		if ( adj[indexFrom][indexTo] == max ) { return null; }
		final IList<E> theRoads = getAllRoadsBetween(indexFrom, indexTo);
		// final boolean startWithFirstintersection =
		// from.equals(graph.getEdgeSource(theRoads.get(0)));
		return theRoads;
	}

	/**
	 * 
	 * @see msi.gama.gis.pathfinder.Optimizer#bestDistanceBetween(msi.gama.gis.pathfinder.PathIntersection,
	 *      msi.gama.gis.pathfinder.PathIntersection)
	 */
	@Override
	public double bestDistanceBetween(final V from, final V to) {
		final int i1 = graph.getVertex(from).getIndex();
		final int i2 = graph.getVertex(to).getIndex();
		double value;
		if ( i1 > number - 1 || i2 > number - 1 || i1 < 0 || i2 < 0 ) {
			value = max;
		} else {
			value = (double) adj[i1][i2] / (double) multiplier;
		}
		// Output.debug("Valeur calculée :" + value);
		// ATTENTION ? C'est un int qui est retourné et qui ne correspond pas a
		// une "vraie" valeur de distance... (mais a la valeur utilisée dans la
		// pathfinder). En la divisant par multiplier on retrouve en gros la
		// distance en kilomètres.
		return value;
	}

	GamaPoint computeCenterOfIntersections() {
		if ( center == null ) {
			final GamaPoint bottomLeft = new GamaPoint(0f, 0f);
			final GamaPoint topRight = new GamaPoint(0f, 0f);
			for ( final ILocation i : intersections ) {
				if ( i.getX() < bottomLeft.getX() ) {
					bottomLeft.x = i.getX();
				}
				if ( i.getY() < bottomLeft.getY() ) {
					bottomLeft.y = i.getY();
				}
				if ( i.getX() > topRight.getX() ) {
					topRight.x = i.getX();
				}
				if ( i.getY() > topRight.getY() ) {
					topRight.y = i.getY();
				}
			}
			center =
				new GamaPoint((topRight.x - bottomLeft.x) / 2, (topRight.y - bottomLeft.y) / 2);
		}
		return center;
	}

	/**
	 * Returns all the roads between two intersections known by their indices.
	 * Does it by walking, from the last point, the predecessors matrix and
	 * adding the nodes found until the beginning is found.Then the collection
	 * is reversed and the actual rodes are computed (by looking into the
	 * intersections). There are no guarantee, however, in this procedure, that
	 * the best road from one point to another is chosen (in case there are
	 * several).
	 * 
	 * @param indexFrom
	 * @param indexTo
	 * @return
	 * 
	 * 
	 */
	IList<E> getAllRoadsBetween(final int first, final int indexTo) {
		final IList<E> theRoads = new GamaList<E>();
		int last = indexTo;
		final List<Integer> path = new ArrayList<Integer>();
		path.add(last);
		// Output.debug("Asking all roads between " + first + " and " + last);
		while (first != last) {
			last = pred[first][last];
			if ( path.contains(last) ) {
				GuiUtils.debug("Loop ! Between " + first + " and " + last);
			}
			path.add(last);
			// Output.debug("Adding segment " + last + ";" + previous);
			// Output.debug("First location : " + intersections.get(last)
			// + " and " + intersections.get(previous));
			// Output.debug("Distance between them (road): "
			// + Math.round(getDirectRoadBetween(last, previous)
			// .getLengthInKilometers() * 100000));
			// Output.debug("Distance between them (matrix): "
			// + adj[last][previous]);
		}
		Collections.reverse(path);
		for ( int i = 0, stop = path.size() - 1; i < stop; i++ ) {
			final E r = getDirectRoadBetween(path.get(i), path.get(i + 1));
			theRoads.add(r);
		}
		return theRoads;
	}

	/**
	 * @param i
	 * @param last
	 * @return
	 */
	E getDirectRoadBetween(final int first, final int last) {
		final ILocation from = intersections.get(first);
		final ILocation to = intersections.get(last);
		return (E) graph.getEdge(from, to);
	}

	/**
	 * Main method (and only public method). All the operations needed for
	 * optimizing the paths of the GamaPathFinder are executed in order. A
	 * subclass may redefine this order if needed, or call other optimization
	 * techniques.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public abstract void run();

	/**
	 * Compute all the blank nodes (i.e. the nodes in the matrix that indicate
	 * that no path has been computed).
	 * 
	 * @param a
	 *            the matrix of adjacency
	 * 
	 * @return a float
	 */
	protected int computeBlankNodes() {
		int blank = 0;
		for ( int i = 0; i < number; i++ ) {
			for ( int j = 0; j < number; j++ ) {
				if ( adj[i][j] == max ) {
					blank++;
				}
			}
		}
		return blank;
	}

	public void dispose() {

	}

}