/*
 * Copyright 2006 - 2016 Stefan Balev <stefan.balev@graphstream-project.org> Julien Baudry
 * <julien.baudry@graphstream-project.org> Antoine Dutot <antoine.dutot@graphstream-project.org> Yoann Pigné
 * <yoann.pigne@graphstream-project.org> Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 *
 * This file is part of GraphStream <http://graphstream-project.org>.
 *
 * GraphStream is a library whose purpose is to handle static or dynamic graph, create them from scratch, file or any
 * source and display them.
 *
 * This program is free software distributed under the terms of two licenses, the CeCILL-C license that fits European
 * law, and the GNU Lesser General Public License. You can use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following URL <http://www.cecill.info> or under
 * the terms of the GNU LGPL as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C and LGPL licenses and
 * that you accept their terms.
 */
package msi.gama.util.graph.graphstream_copy;

import msi.gama.runtime.GAMA;

/**
 * A generator following the small-world model of Watts and Strogatz.
 *
 * <p>
 * This generator creates small-world graphs of arbitrary size.
 * </p>
 *
 * <p>
 * This model generates a ring of n nodes where each node is connected to its k nearest neighbours in the ring (k/2 on
 * each side, which means k must be even). Then it process each node of the ring in order following the ring, and
 * "rewiring" each of their edges toward the not yet processed nodes with randomly chosen nodes with a probability beta.
 * </p>
 *
 * <h2>Usage</h2>
 *
 * <p>
 * You must provide values for n, k and beta at construction time. You must ensure that k is event, that n >> k >>
 * log(n) >> 1. Furthermore, beta being a probability it must be between 0 and 1.
 * </p>
 *
 * <p>
 * By default, the generator will produce a placement for nodes using the ``xyz`` attribute.
 * </p>
 *
 * <p>
 * This generator will produce the ring of nodes once {@link #begin()} has been called. Then calling
 * {@link #nextEvents()} will rewire one node at a time return true until each node is processed, in which case it
 * returns false. You must then call {@link #end()}.
 * </p>
 *
 * <h2>Example</h2>
 *
 * <pre>
 * Graph graph = new SingleGraph("This is a small world!");
 * Generator gen = new WattsStrogatzGenerator(20, 2, 0.5);
 *
 * gen.addSink(graph);
 * gen.begin();
 * while (gen.nextEvents()) {}
 * gen.end();
 *
 * graph.display(false); // Node position is provided.
 * </pre>
 *
 * <h2>Reference</h2>
 *
 * <p>
 * This generator is based on the Watts-Strogatz model.
 * </p>
 *
 * @reference Watts, D.J. and Strogatz, S.H. "Collective dynamics of 'small-world' networks". Nature 393 (6684): 409–10.
 *            doi:10.1038/30918. PMID 9623998. 1998.
 */
public class WattsStrogatzGenerator extends BaseGenerator {
	/** The number of nodes to generate. */
	protected int n;

	/** Base degree of each node. */
	protected int k;

	/** Probability to "rewire" an edge. */
	protected double beta;

	/** Current rewired node, used to allo nextEvents() iteration. */
	protected int current;

	/**
	 * New Watts-Strogatz generator.
	 *
	 * @param n
	 *            The number of nodes to generate.
	 * @param k
	 *            The base degree of each node.
	 * @param beta
	 *            Probability to "rewire" an edge.
	 */
	public WattsStrogatzGenerator(final int n, final int k, final double beta) {
		setUseInternalGraph(true);

		if (n <= k) { throw new RuntimeException("parameter n must be >> k"); }
		if (beta < 0 || beta > 1) { throw new RuntimeException("parameter beta must be between 0 and 1"); }
		if (k % 2 != 0) { throw new RuntimeException("parameter k must be even"); }
		if (k < 2) { throw new RuntimeException("parameter k must be >= 2"); }

		this.n = n;
		this.k = k;
		this.beta = beta;
	}

	@Override
	public void begin() {
		// final double step = 2 * PI / n;
		// double x = 0;

		for (int i = 0; i < n; i++) {
			// addNode(nodeId(i), cos(x), sin(x));
			addNode(nodeId(i));
			// x += step;
		}

		// Add the circle links.

		final int kk = k / 2;

		for (int i = 0; i < n; i++) {
			for (int j = 1; j <= kk; j++) {
				final int jj = (i + j) % n;
				addEdge(edgeId(i, jj), nodeId(i), nodeId(jj));
			}
		}

		current = 0;
	}

	@Override
	public boolean nextEvents() {
		final int kk = k / 2;

		if (current < n) {
			for (int j = 1; j <= kk; j++) {
				final int jj = (current + j) % n;

				if (GAMA.getCurrentRandom().next() < beta) {
					delEdge(edgeId(current, jj));
					final int newTarget = chooseNewNode(current, jj);
					final String edgeId = edgeId(current, newTarget);

					if (internalGraph.getEdge(edgeId) == null) {
						addEdge(edgeId, nodeId(current), nodeId(newTarget));
					}
				}
			}

			current += 1;

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void end() {
		super.end();
	}

	protected String nodeId(final int id) {
		return String.format("%d", id);
	}

	protected String edgeId(final int f, final int t) {
		int from = f;
		int to = t;
		if (from > to) {
			to += from;
			from = to - from;
			to -= from;
		}

		return String.format("%d_%d", from, to);
	}

	protected int chooseNewNode(final int avoid, final int old) {
		int newId = 0;
		boolean exists = true;

		do {
			newId = GAMA.getCurrentRandom().between(0, n - 1);
			exists = internalGraph.getEdge(edgeId(avoid, newId)) != null;
		} while (newId == avoid || exists);

		return newId;
	}
}