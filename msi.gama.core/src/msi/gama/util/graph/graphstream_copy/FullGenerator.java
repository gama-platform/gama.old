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

/**
 * Full graph generator.
 *
 * <p>
 * Probably not very useful, still sometimes needed. This generator creates fully connected graphs of any size. Calling
 * {@link #begin()} put one unique node in the graph, then {@link #nextEvents()} will add a new node each time it is
 * called.
 * </p>
 *
 * <p>
 * This generator has the ability to add randomly chosen numerical values on arbitrary attributes on edges or nodes of
 * the graph, and to randomly choose a direction for edges.
 * </p>
 *
 * <p>
 * A list of attributes can be given for nodes and edges. In this case each new node or edge added will have this
 * attribute and the value will be a randomly chosen number. The range in which these numbers are chosen can be
 * specified.
 * </p>
 *
 * <p>
 * By default, edges are not oriented. It is possible to ask orientation, in which case the direction is chosen
 * randomly.
 * </p>
 *
 * @since 2007
 */
public class FullGenerator extends BaseGenerator {

	/**
	 * Used to generate node names.
	 */
	protected int nodeNames = 0;

	/**
	 * New full graph generator. By default no attributes are added to nodes and edges, and edges are not directed.
	 */
	public FullGenerator() {
		super();
		setUseInternalGraph(false);
	}

	/**
	 * Begin the generator by adding a node.
	 *
	 * @see msi.gama.util.graph.graphstream_copy.Generator#begin()
	 */
	@Override
	public void begin() {
		sendNodeAdded(sourceId, Integer.toString(nodeNames++));
	}

	/**
	 * Add a new node and connect it with all others.
	 *
	 * @see msi.gama.util.graph.graphstream_copy.Generator#nextEvents()
	 */
	@Override
	public boolean nextEvents() {
		final String id = Integer.toString(nodeNames++);
		sendNodeAdded(sourceId, id);
		for (int i = 0; i < nodeNames - 1; i++) {
			final String to = Integer.toString(i);
			sendEdgeAdded(sourceId, id + "_" + to, id, to, false);
		}
		return true;
	}
}