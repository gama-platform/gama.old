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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Nodes used with {@link MultiGraph}
 *
 */
public class MultiNode extends AdjacencyListNode {
	protected HashMap<AbstractNode, List<AbstractEdge>> neighborMap;

	// *** Constructor ***

	public MultiNode(final AbstractGraph graph, final String id) {
		super(graph, id);
		neighborMap = new HashMap<>(4 * INITIAL_EDGE_CAPACITY / 3 + 1);
	}

	// *** Helpers ***

	@SuppressWarnings ("unchecked")
	@Override
	protected <T extends Edge> T locateEdge(final Node opposite, final char type) {
		final List<AbstractEdge> l = neighborMap.get(opposite);
		if (l == null) { return null; }

		for (final AbstractEdge e : l) {
			final char etype = edgeType(e);
			if ((type != I_EDGE || etype != O_EDGE) && (type != O_EDGE || etype != I_EDGE)) { return (T) e; }
		}
		return null;
	}

	@Override
	protected void removeEdge(final int i) {
		final AbstractNode opposite = edges[i].getOpposite(this);
		final List<AbstractEdge> l = neighborMap.get(opposite);
		l.remove(edges[i]);
		if (l.isEmpty()) {
			neighborMap.remove(opposite);
		}
		super.removeEdge(i);
	}

	// *** Callbacks ***

	@Override
	protected boolean addEdgeCallback(final AbstractEdge edge) {
		final AbstractNode opposite = edge.getOpposite(this);
		List<AbstractEdge> l = neighborMap.get(opposite);
		if (l == null) {
			l = new LinkedList<>();
			neighborMap.put(opposite, l);
		}
		l.add(edge);
		return super.addEdgeCallback(edge);
	}

	@Override
	protected void clearCallback() {
		neighborMap.clear();
		super.clearCallback();
	}

	// *** Others ***

	@SuppressWarnings ("unchecked")
	@Override
	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return (Iterator<T>) Collections.unmodifiableSet(neighborMap.keySet()).iterator();
	}

	@SuppressWarnings ("unchecked")
	public <T extends Edge> Collection<T> getEdgeSetBetweenNode(final Node node) {
		final List<AbstractEdge> l = neighborMap.get(node);
		if (l == null) { return Collections.emptyList(); }
		return (Collection<T>) Collections.unmodifiableList(l);
	}

	public <T extends Edge> Collection<T> getEdgeSetBetween(final String id) {
		return getEdgeSetBetweenNode(graph.getNode(id));
	}

	public <T extends Edge> Collection<T> getEdgeSetBetween(final int index) {
		return getEdgeSetBetweenNode(graph.getNode(index));
	}
}
