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
package msi.gama.ext.graphstream;

import java.util.HashMap;

public class GraphSpells implements Sink {

	CumulativeSpells graph;
	CumulativeAttributes graphAttributes;

	HashMap<String, CumulativeSpells> nodes;
	HashMap<String, CumulativeAttributes> nodesAttributes;

	HashMap<String, CumulativeSpells> edges;
	HashMap<String, CumulativeAttributes> edgesAttributes;
	HashMap<String, EdgeData> edgesData;

	double date;

	public GraphSpells() {
		graph = new CumulativeSpells();
		graphAttributes = new CumulativeAttributes(0);

		nodes = new HashMap<>();
		nodesAttributes = new HashMap<>();

		edges = new HashMap<>();
		edgesAttributes = new HashMap<>();
		edgesData = new HashMap<>();

		date = Double.NaN;
	}

	public static class EdgeData {
		String source;
		String target;
		boolean directed;

		public String getSource() {
			return source;
		}

		public String getTarget() {
			return target;
		}

		public boolean isDirected() {
			return directed;
		}
	}

	public Iterable<String> getNodes() {
		return nodes.keySet();
	}

	public Iterable<String> getEdges() {
		return edges.keySet();
	}

	public CumulativeSpells getNodeSpells(final String nodeId) {
		return nodes.get(nodeId);
	}

	public CumulativeAttributes getNodeAttributes(final String nodeId) {
		return nodesAttributes.get(nodeId);
	}

	public CumulativeSpells getEdgeSpells(final String edgeId) {
		return edges.get(edgeId);
	}

	public CumulativeAttributes getEdgeAttributes(final String edgeId) {
		return edgesAttributes.get(edgeId);
	}

	public EdgeData getEdgeData(final String edgeId) {
		return edgesData.get(edgeId);
	}

	@Override
	public void stepBegins(final String sourceId, final long timeId, final double step) {
		this.date = step;

		graphAttributes.updateDate(step);
		graph.updateCurrentSpell(step);

		for (final String id : nodes.keySet()) {
			nodes.get(id).updateCurrentSpell(step);
			nodesAttributes.get(id).updateDate(step);
		}

		for (final String id : edges.keySet()) {
			edges.get(id).updateCurrentSpell(step);
			edgesAttributes.get(id).updateDate(step);
		}
	}

	@Override
	public void nodeAdded(final String sourceId, final long timeId, final String nodeId) {
		if (!nodes.containsKey(nodeId)) {
			nodes.put(nodeId, new CumulativeSpells());
			nodesAttributes.put(nodeId, new CumulativeAttributes(date));
		}

		nodes.get(nodeId).startSpell(date);
	}

	@Override
	public void nodeRemoved(final String sourceId, final long timeId, final String nodeId) {
		if (nodes.containsKey(nodeId)) {
			nodes.get(nodeId).closeSpell();
			nodesAttributes.get(nodeId).remove();
		}
	}

	@Override
	public void edgeAdded(final String sourceId, final long timeId, final String edgeId, final String fromNodeId,
			final String toNodeId, final boolean directed) {
		if (!edges.containsKey(edgeId)) {
			edges.put(edgeId, new CumulativeSpells());
			edgesAttributes.put(edgeId, new CumulativeAttributes(date));

			final EdgeData data = new EdgeData();
			data.source = fromNodeId;
			data.target = toNodeId;
			data.directed = directed;

			edgesData.put(edgeId, data);
		}

		edges.get(edgeId).startSpell(date);

	}

	@Override
	public void edgeRemoved(final String sourceId, final long timeId, final String edgeId) {
		if (edges.containsKey(edgeId)) {
			edges.get(edgeId).closeSpell();
			edgesAttributes.get(edgeId).remove();
		}
	}

	@Override
	public void graphCleared(final String sourceId, final long timeId) {
		for (final String id : nodes.keySet()) {
			nodes.get(id).closeSpell();
			nodesAttributes.get(id).remove();
		}

		for (final String id : edges.keySet()) {
			edges.get(id).closeSpell();
			edgesAttributes.get(id).remove();
		}
	}

	@Override
	public void graphAttributeAdded(final String sourceId, final long timeId, final String attribute,
			final Object value) {
		graphAttributes.set(attribute, value);
	}

	@Override
	public void graphAttributeChanged(final String sourceId, final long timeId, final String attribute,
			final Object oldValue, final Object newValue) {
		graphAttributes.set(attribute, newValue);
	}

	@Override
	public void graphAttributeRemoved(final String sourceId, final long timeId, final String attribute) {
		graphAttributes.remove(attribute);
	}

	@Override
	public void nodeAttributeAdded(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object value) {
		nodesAttributes.get(nodeId).set(attribute, value);
	}

	@Override
	public void nodeAttributeChanged(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object oldValue, final Object newValue) {
		nodesAttributes.get(nodeId).set(attribute, newValue);
	}

	@Override
	public void nodeAttributeRemoved(final String sourceId, final long timeId, final String nodeId,
			final String attribute) {
		nodesAttributes.get(nodeId).remove(attribute);
	}

	@Override
	public void edgeAttributeAdded(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object value) {
		edgesAttributes.get(edgeId).set(attribute, value);
	}

	@Override
	public void edgeAttributeChanged(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object oldValue, final Object newValue) {
		edgesAttributes.get(edgeId).set(attribute, newValue);
	}

	@Override
	public void edgeAttributeRemoved(final String sourceId, final long timeId, final String edgeId,
			final String attribute) {
		edgesAttributes.get(edgeId).remove(attribute);
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();

		for (final String id : nodes.keySet()) {
			buffer.append("node#\"").append(id).append("\" ").append(nodes.get(id)).append(" ")
					.append(nodesAttributes.get(id)).append("\n");
		}

		for (final String id : edges.keySet()) {
			buffer.append("edge#\"").append(id).append("\" ").append(edges.get(id)).append("\n");
		}

		return buffer.toString();
	}

}
