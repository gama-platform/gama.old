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

import java.util.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.IEnvironment;
import msi.gama.runtime.GAMA;

/**
 * FloydWarshallStaticOptimizer.
 * 
 * @author drogoul 5 nov. 07
 */
public final class FloydWarshallStaticOptimizer extends AbstractStaticOptimizer<ILocation, IShape> {

	public boolean verbose;

	/**
	 * Instantiates a new agent path optimizer.
	 * 
	 * @param name
	 *            the name which will be reported in the logs, etc.
	 * @param intersections
	 */
	public FloydWarshallStaticOptimizer(final GamaGraph graph, final boolean verbose) {
		super(graph);
		this.verbose = verbose;
		run();
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
	public void run() {
		initializeMatrices();
		floydWarshall(verbose);
	}

	/**
	 * Floyd warshall.
	 * 
	 * @param adj
	 *            the a
	 * @param pred
	 *            the p
	 * @param verbose
	 *            the verbose
	 */
	protected final void floydWarshall(final boolean verbose) {
		float density = 0f;
		int blank = computeBlankNodes();
		final long one_step = number / 10;
		final int number2 = number * number;
		final float one_percent = one_step / 10f;
		if ( verbose ) {
			density = (1 - (float) blank / (float) number2) * 100;
			GuiUtils.debug("--- Initial density of the matrix :" + density + "%");
			GuiUtils.debug("3) Computing optimal paths between all the intersections...");
		}
		int i, j, k;
		final long debut = System.currentTimeMillis();
		for ( k = 0; k < number; k++ ) { // Outer loop
			for ( i = 0; i < k; i++ ) {
				final int distik = adj[i][k]; // distance from i to k
				if ( distik < max ) {
					for ( j = i + 1; j < number; j++ ) {
						final int distkj = adj[k][j]; // distance from k to j
						if ( distkj < max && distkj > 0 ) { // if the path kj is
							// valid
							final int temp_dist = distik + distkj;
							if ( temp_dist < adj[i][j] ) {
								adj[i][j] = adj[j][i] = temp_dist;
								pred[i][j] = pred[k][j];
								pred[j][i] = pred[k][i];
							}
						}

					}
				}

			}
			for ( i = k + 1; i < number; i++ ) {
				final int distik = adj[i][k]; // distance from i to k
				if ( distik < max ) {
					for ( j = i + 1; j < number; j++ ) {
						final int distkj = adj[k][j]; // distance from k to j
						if ( distkj < max ) { // if the path kj is
							// valid
							final int temp_dist = distik + distkj;
							if ( temp_dist < adj[i][j] ) {
								adj[i][j] = adj[j][i] = temp_dist;
								pred[i][j] = pred[k][j];
								pred[j][i] = pred[k][i];
							}
						}

					}
				}

			}

			if ( verbose && one_step != 0 && k % one_step == 0 ) {
				GuiUtils.debug("(optimization process) Completed :" + k / one_percent + "%; ");
				GuiUtils.informConsole("(optimization process) Completed :" + k / one_percent +
					"%; \n");
			}
		}
		if ( verbose ) {
			final long totalTime = System.currentTimeMillis() - debut;
			density = 0f;
			blank = computeBlankNodes();
			density = (1 - (float) blank / (float) number2) * 100;
			GuiUtils.debug("--- Final density of the matrix :" + density + "%");
			GuiUtils.debug("--- Computed in " + (float) totalTime / 1000 + "seconds");
			GuiUtils.informConsole("--- Final density of the matrix :" + density + "% \n");
			GuiUtils.informConsole("--- Computed in " + (float) totalTime / 1000 + "seconds \n");
		}
	}

	protected final void innerLoop(final int i, final int k, final int distik) {
		for ( int j = i + 1; j < number; j++ ) {
			final int distkj = adj[k][j]; // distance from k to j
			if ( distkj < max && distkj > 0 ) { // if the path kj is
				// valid
				final int temp_dist = distik + distkj;
				if ( temp_dist < adj[i][j] ) {
					adj[i][j] = adj[j][i] = temp_dist;
					pred[i][j] = pred[k][j];
					pred[j][i] = pred[k][i];
				}
			}

		}
	}

	protected final void innerLoopNoTest(final int i, final int k, final int distik) {
		for ( int j = i + 1; j < number; j++ ) {
			final int distkj = adj[k][j]; // distance from k to j
			if ( distkj < max ) { // if kj is valid
				final int temp_dist = distik + distkj;
				if ( temp_dist < adj[i][j] ) {
					adj[i][j] = adj[j][i] = temp_dist;
					pred[i][j] = pred[k][j];
					pred[j][i] = pred[k][i];
				}
			}

		}
	}

	protected final void floydWarshallDistributed(final boolean verbose) {
		// float density = 0f;
		// int blank = computeBlankNodes();
		// final long one_step = number / 100;
		// final int number2 = number * number;
		// final float one_percent = one_step;
		if ( verbose ) {
			// density = (1 - (float) blank / (float) number2) * 100;
			// GuiUtils.debug("--- Initial density of the matrix :" + density + "%");
			// GuiUtils.debug("3) Computing optimal paths between all the intersections...");
		}
		int distik, i, k;
		// final long debut = System.currentTimeMillis();
		for ( k = 0; k < number; k++ ) { // Outer loop
			for ( i = 0; i < k; i++ ) {
				distik = adj[i][k]; // distance from i to k
				if ( distik < max ) {
					innerLoopNoTest(i, k, distik);
				}
			}
			for ( i = k + 1; i < number; i++ ) {
				distik = adj[i][k]; // distance from i to k
				if ( distik < max ) {
					innerLoop(i, k, distik);
				}

			}

			// if ( verbose && k % one_step == 0 ) {
			// GuiUtils.debug("(optimization process) Completed :" + k / one_percent + "%; ");
			// }
		}
		// if ( verbose ) {
		// final long totalTime = System.currentTimeMillis() - debut;
		// density = 0f;
		// blank = computeBlankNodes();
		// density = (1 - (float) blank / (float) number2) * 100;
		// GuiUtils.debug("--- Final density of the matrix :" + density + "%");
		// GuiUtils.debug("--- Computed in " + (float) totalTime / 1000 + "seconds");
		// }
	}

	/**
	 * Floyd warshall.
	 * 
	 * @param adj
	 *            the a
	 * @param pred
	 *            the p
	 * @param verbose
	 *            the verbose
	 */
	protected void floydWarshallReverse(final boolean verbose) {
		float density = 0f;
		int blank = computeBlankNodes();
		final long one_step = number / 100;
		final int number2 = number * number;
		final float one_percent = one_step;
		if ( verbose ) {
			density = (1 - (float) blank / (float) number2) * 100;
			GuiUtils.debug("--- Initial density of the matrix :" + density + "%");
			GuiUtils.debug("3) Computing optimal paths between all the intersections...");
		}
		int distij = 0, temp_dist = 0, distkj = 0, distik = 0, i, j, k;
		final long debut = System.currentTimeMillis();
		for ( k = number; --k >= 0; ) { // Outer loop
			for ( i = number; --i >= 0; ) {
				distik = adj[i][k]; // distance from i to k
				if ( distik < max && distik > 0 ) {
					for ( j = number; --j > i; ) {
						distij = adj[i][j]; // distance from i to j
						distkj = adj[k][j]; // distance from k to j
						if ( distkj < max && distkj > 0 ) { // if the path kj is
							// valid
							temp_dist = distik + distkj;
							if ( temp_dist < distij ) {
								adj[i][j] = temp_dist;
								adj[j][i] = temp_dist;
								pred[i][j] = pred[k][j];
								pred[j][i] = pred[k][i];
							}
						}

					}
				}
			}

			if ( verbose && k % one_step == 0 ) {
				GuiUtils.debug("(optimization process) Completed :" + k / one_percent + "%; ");
				GuiUtils.informConsole("(optimization process) Completed :" + k / one_percent +
					"%; ");
			}
		}
		if ( verbose ) {
			final long totalTime = System.currentTimeMillis() - debut;
			density = 0f;
			blank = computeBlankNodes();
			density = (1 - (float) blank / (float) number2) * 100;
			GuiUtils.debug("--- Final density of the matrix :" + density + "%");
			GuiUtils.debug("--- Computed in " + (float) totalTime / 1000 + "seconds");
			GuiUtils.informConsole("--- Final density of the matrix :" + density + "%");
			GuiUtils.informConsole("--- Computed in " + (float) totalTime / 1000 + "seconds");
		}
	}

	@Override
	GamaPoint computeCenterOfIntersections() {
		IEnvironment env = GAMA.getModel().getModelEnvironment();
		center = new GamaPoint(env.getWidth() / 2, env.getHeight() / 2);
		return center;
	}

	/**
	 * The bottom line : the Floyd-Warshall algorithm runs much faster when the
	 * vertices (intersections, in our case) are correctly sorted. Whas is a
	 * "correct sort" ? There are no definite answer, of course, since it is
	 * highly domain-dependent. However, since we deal with geographical data,
	 * mainly roads, the idea is to first process the intersections with few
	 * roads, then the others.The FW algorithm can then, at the same time,
	 * create and optimize the last paths. Among the vertices with the same
	 * number of nodes, we can further optimize the process by processing at the
	 * end the ones closer to the center of the map (where the probability to
	 * have more impact on the paths is higher). From an unsorted set of
	 * intersections to a sorted one, the results in processing time can vary by
	 * a factor of 10 (i.e. on the map of Hanoi, which contains 6700
	 * intersections and ~7500 roads, the time in seconds drop from 580 to
	 * 50...). Subclasses may of course redefine this sorting according to the
	 * density and topology of the underlying road network.
	 * 
	 * This method creates the sorted list of nodes from the list of
	 * intersections, adds a custom comparator to it, sorts it, and then
	 * attributes their index to the intersections.
	 */
	protected void sortFinderData() {
		center = computeCenterOfIntersections();
		Collections.sort(intersections, new Comparator<ILocation>() {

			@Override
			public int compare(final ILocation arg0, final ILocation arg1) {
				int nb = compareRoads(arg1, arg0);
				if ( nb == 0 ) {
					nb = compareToCenter(arg0, arg1);
				}
				if ( nb == 0 ) {
					nb = compareX(arg1, arg0);
				}
				return nb;
			}

			private int compareRoads(final ILocation arg0, final ILocation arg1) {
				int result =
					graph.getVertex(arg0).getEdgesCount() - graph.getVertex(arg0).getEdgesCount();
				return result;
			}

			private int compareToCenter(final ILocation arg0, final ILocation arg1) {
				final double xc = center.x;
				final double yc = center.y;
				final double x0 = arg0.getX();
				final double x1 = arg1.getX();
				final double y0 = arg0.getY();
				final double y1 = arg1.getY();
				final double x0c = (x0 - xc) * multiplier;
				final double x1c = (x1 - xc) * multiplier;
				final double y0c = (y0 - yc) * multiplier;
				final double y1c = (y1 - yc) * multiplier;
				final int dist0 = (int) Math.sqrt(x0c * x0c + y0c * y0c);
				final int dist1 = (int) Math.sqrt(x1c * x1c + y1c * y1c);
				return dist0 - dist1;
			}

			private int compareX(final ILocation arg0, final ILocation arg1) {
				return (int) ((arg1.getX() - arg0.getX()) * 10000);
			}

		});

		for ( int i = 0, stop = intersections.size(); i < stop; i++ ) {
			graph.getVertex(intersections.get(i)).setIndex(i);
		}
	}

	/**
	 * Initialize matrices.
	 * 
	 * @param a
	 *            the a
	 * @param p
	 *            the p
	 */
	@Override
	public void initializeMatrices() {

		GuiUtils.debug("1) Initializing the adjacency and predecessors matrices");
		initMatrices();
		sortFinderData();
		GuiUtils.debug("2) Initializing the distances in the adjacency matrix...");
		ILocation to, from;
		// int roads = 0;
		int p2, length;
		for ( int p1 = 0, n = intersections.size(); p1 < n; p1++ ) {
			from = intersections.get(p1);
			final Object[] r = graph.getVertex(from).getEdges().toArray();
			for ( int i = 0, m = r.length; i < m; i++ ) {
				final Object road = r[i];
				to = (ILocation) graph.getEdge(road).getOther(from);
				p2 = graph.getVertex(to).getIndex();
				length = (int) (graph.getEdgeWeight(road) * multiplier + 0.5);
				if ( adj[p1][p2] > length ) {
					adj[p1][p2] = length;
					adj[p2][p1] = length;
					pred[p1][p2] = p1;
					pred[p2][p1] = p2;
				}
			}
		}
	}
}
