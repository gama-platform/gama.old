/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package msi.gama.util.graph.graphstream_copy;

import java.util.ArrayList;
import java.util.Random;

/**
 * Base graph generator.
 * 
 * <p>
 * This class is a base to implement generators. It it has facilities to
 * generate edges or nodes, and provides services to add attributes on them and
 * to choose if the edge is directed or not.
 * </p>
 * 
 * <p>
 * Indeed, This generator has the ability to add randomly chosen numerical
 * values on arbitrary attributes on edges or nodes of the graph, and to
 * randomly choose a direction for edges.
 * </p>
 * 
 * <p>
 * A list of attributes can be given for nodes and edges. In this case each new
 * node or edge added will have this attribute and the value will be a randomly
 * chosen number. The range in which these numbers are chosen can be specified.
 * </p>
 * 
 * <p>
 * By default, edges are not oriented. It is possible to ask orientation, and in
 * addition to ask that the direction be chosen randomly (by default, if edges
 * must be oriented, the order given for the two nodes to connect is used).
 * </p>
 * 
 * @since 2007
 */
public abstract class BaseGenerator extends SourceBase implements Generator {

	// Attributes

	/**
	 * Are edges directed ?
	 */
	protected boolean directed = false;

	/**
	 * If directed, choose the direction randomly?.
	 */
	protected boolean randomlyDirected = false;

	/**
	 * List of attributes to put on nodes with a randomly chosen numerical
	 * value.
	 */
	protected ArrayList<String> nodeAttributes = new ArrayList<String>();

	/**
	 * List of attributes to put on edges with a randomly chosen numerical
	 * value.
	 */
	protected ArrayList<String> edgeAttributes = new ArrayList<String>();

	/**
	 * If node attributes are added, in which range are the numbers chosen ?.
	 */
	protected double[] nodeAttributeRange = new double[2];

	/**
	 * If edge attributes are added, in which range are the numbers chosen ?.
	 */
	protected double[] edgeAttributeRange = new double[2];

	/**
	 * The random number generator.
	 */
	protected Random random = new Random();

	/**
	 * Set the node label attribute using the identifier?.
	 */
	protected boolean addNodeLabels = false;

	/**
	 * Set the edge label attribute using the identifier?.
	 */
	protected boolean addEdgeLabels = false;

	/**
	 * Flag to know if generator has to use an internal graph. Generator which
	 * want to use this feature have to use the
	 * {@link #setUseInternalGraph(boolean)} method to set this flag.
	 */
	private boolean useInternalGraph;

	/**
	 * When {@link #useInternalGraph} is on, nodes and edges are stored in this
	 * graph.
	 */
	protected Graph internalGraph;

	/**
	 * Used to created unique generatorId.
	 */
	private volatile static int generatorId;

	// Constructors

	/**
	 * New base graph generator. By default no attributes are added to nodes and
	 * edges, and edges are not directed.
	 */
	public BaseGenerator() {
		this(false, false);
	}

	/**
	 * New base graph generator. By default no attributes are added to nodes and
	 * edges. It is possible to make edge randomly directed.
	 * 
	 * @param directed
	 *            If true the edges are directed.
	 * @param randomlyDirectedEdges
	 *            If true edge, are directed and the direction is chosen
	 *            randomly.
	 */
	public BaseGenerator(boolean directed, boolean randomlyDirectedEdges) {
		super(String.format("generator-%08x", generatorId++));
		setDirectedEdges(directed, randomlyDirectedEdges);

		nodeAttributeRange[0] = 0;
		nodeAttributeRange[1] = 1;
		edgeAttributeRange[0] = 0;
		edgeAttributeRange[1] = 1;
	}

	/**
	 * New base graph generator.
	 * 
	 * @param directed
	 *            If true the edges are directed.
	 * @param randomlyDirectedEdges
	 *            It true, edges are directed and the direction is choosed
	 *            randomly.
	 * @param nodeAttribute
	 *            put an attribute by that name on each node with a random
	 *            numeric value.
	 * @param edgeAttribute
	 *            put an attribute by that name on each edge with a random
	 *            numeric value.
	 */
	public BaseGenerator(boolean directed, boolean randomlyDirectedEdges,
			String nodeAttribute, String edgeAttribute) {
		this(directed, randomlyDirectedEdges);

		addNodeAttribute(nodeAttribute);
		addEdgeAttribute(edgeAttribute);
	}

	// Commands

	/**
	 * End the graph generation by finalizing it. Once the {@link #nextEvents()}
	 * method returned false (or even if you stop before), this method must be
	 * called to finish the graph.
	 * 
	 * In addition, BaseGenerator adds a "clear" operations that removes all the
	 * kept edges and nodes identifiers and the associated data.
	 */
	public void end() {
		clearKeptData();
	}

	/**
	 * Set the random seed used for random number generation.
	 * 
	 * @param seed
	 *            The seed.
	 */
	public void setRandomSeed(long seed) {
		random.setSeed(seed);
	}

	/**
	 * Allow to add label attributes on nodes. The label is the identifier of
	 * the node.
	 * 
	 * @param on
	 *            If true labels are added.
	 */
	public void addNodeLabels(boolean on) {
		addNodeLabels = on;
	}

	/**
	 * Allow to add label attributes on edges. The label is the identifier of
	 * the edge.
	 * 
	 * @param on
	 *            If true labels are added.
	 */
	public void addEdgeLabels(boolean on) {
		addEdgeLabels = on;
	}

	/**
	 * Make each generated edge directed or not. If the new edge created are
	 * directed, the direction is chosen randomly.
	 * 
	 * @param directed
	 *            It true, edge will be directed.
	 * @param randomly
	 *            If true, not only edges are directed, but the direction is
	 *            chosen randomly.
	 */
	public void setDirectedEdges(boolean directed, boolean randomly) {
		this.directed = directed;

		if (directed && randomly)
			randomlyDirected = randomly;
	}

	/**
	 * Add this attribute on all nodes generated. This attribute will have a
	 * numerical value chosen in a range that is by default [0-1].
	 * 
	 * @param name
	 *            The attribute name.
	 * @see #setNodeAttributesRange(double, double)
	 * @see #removeNodeAttribute(String)
	 */
	public void addNodeAttribute(String name) {
		nodeAttributes.add(name);
	}

	/**
	 * Remove an automatic attribute for nodes.
	 * 
	 * @param name
	 *            The attribute name.
	 * @see #addNodeAttribute(String)
	 */
	public void removeNodeAttribute(String name) {
		int pos = nodeAttributes.indexOf(name);

		if (pos >= 0)
			nodeAttributes.remove(pos);
	}

	/**
	 * Add this attribute on all edges generated. This attribute will have a
	 * numerical value chosen in a range that is by default [0-1].
	 * 
	 * @param name
	 *            The attribute name.
	 * @see #setEdgeAttributesRange(double, double)
	 * @see #removeEdgeAttribute(String)
	 */
	public void addEdgeAttribute(String name) {
		edgeAttributes.add(name);
	}

	/**
	 * Remove an automatic attribute for edges.
	 * 
	 * @param name
	 *            The attribute name.
	 * @see #addEdgeAttribute(String)
	 */
	public void removeEdgeAttribute(String name) {
		int pos = edgeAttributes.indexOf(name);

		if (pos >= 0)
			edgeAttributes.remove(pos);
	}

	/**
	 * If node attributes are added automatically, choose in which range the
	 * values are choosed.
	 * 
	 * @see #addNodeAttribute(String)
	 */
	public void setNodeAttributesRange(double low, double hi) {
		nodeAttributeRange[0] = low;
		nodeAttributeRange[1] = hi;
	}

	/**
	 * If edge attributes are added automatically, choose in which range the
	 * values are choosed.
	 * 
	 * @see #addEdgeAttribute(String)
	 */
	public void setEdgeAttributesRange(double low, double hi) {
		edgeAttributeRange[0] = low;
		edgeAttributeRange[1] = hi;
	}

	/**
	 * Enable or disable the use of an internal graph. If enable, nodes, edges
	 * and their attributes are stored in an internal graph.
	 * 
	 * This is useful if the generator needs to remember informations like node
	 * id.
	 * 
	 * @param on
	 *            true if the internal graph has to be enable.
	 */
	public void setUseInternalGraph(boolean on) {
		useInternalGraph = on;

		if (!on && internalGraph != null) {
			internalGraph.clear();
			internalGraph = null;
		}

		if (on && internalGraph == null) {
			internalGraph = new AdjacencyListGraph(getClass().getName()
					+ "-internal_graph");
			internalGraph.setStrict(false);
		}
	}

	/**
	 * Flag to know if an internal graph is in use.
	 * 
	 * @return true if nodes and edges are stored in an internal graph.
	 */
	public boolean isUsingInternalGraph() {
		return useInternalGraph;
	}

	/**
	 * Same as {@link #addNode(String)} but specify attributes to position the
	 * node on a plane.
	 * 
	 * @param id
	 *            The node identifier.
	 * @param x
	 *            The node abscissa.
	 * @param y
	 *            The node ordinate.
	 */
	protected void addNode(String id, double x, double y) {
		addNode(id);
		sendNodeAttributeAdded(sourceId, id, "xy", new Double[] {
				new Double(x), new Double(y) });

		if (useInternalGraph)
			internalGraph.getNode(id).addAttribute("xy",
					(Object) (new Double[] { new Double(x), new Double(y) }));
	}

	/**
	 * Add a node and put attributes on it if needed.
	 * 
	 * @param id
	 *            The new node identifier.
	 */
	protected void addNode(String id) {
		sendNodeAdded(sourceId, id);

		if (addNodeLabels)
			sendNodeAttributeAdded(sourceId, id, "label", id);

		if (useInternalGraph)
			internalGraph.addNode(id);

		double value;

		for (String attr : nodeAttributes) {
			value = (random.nextDouble() * (nodeAttributeRange[1] - nodeAttributeRange[0]))
					+ nodeAttributeRange[0];
			sendNodeAttributeAdded(sourceId, id, attr, value);

			if (useInternalGraph)
				internalGraph.getNode(id).addAttribute(attr, value);
		}
	}

	/**
	 * Remove a node.
	 * 
	 * @param id
	 *            id of the node to remove
	 */
	protected void delNode(String id) {
		if (useInternalGraph)
			internalGraph.removeNode(id);

		sendNodeRemoved(sourceId, id);
	}

	/**
	 * Add an edge, choosing randomly its orientation if needed and putting
	 * attribute on it if needed.
	 * 
	 * @param id
	 *            The edge identifier, if null, the identifier is created from
	 *            the nodes identifiers.
	 * @param from
	 *            The source node (can be inverted randomly with the target
	 *            node).
	 */
	protected void addEdge(String id, String from, String to) {
		if (directed && randomlyDirected && (random.nextFloat() > 0.5f)) {
			String tmp = from;
			from = to;
			to = tmp;
		}

		if (id == null)
			id = from + "_" + to;

		sendEdgeAdded(sourceId, id, from, to, directed);

		if (useInternalGraph)
			internalGraph.addEdge(id, from, to, directed);

		if (addEdgeLabels)
			sendEdgeAttributeAdded(sourceId, id, "label", id);

		for (String attr : edgeAttributes) {
			double value = (random.nextDouble() * (edgeAttributeRange[1] - edgeAttributeRange[0]))
					+ edgeAttributeRange[0];
			sendEdgeAttributeAdded(sourceId, id, attr, value);

			if (useInternalGraph)
				internalGraph.getEdge(id).addAttribute(attr, value);
		}
	}

	/**
	 * Remove an edge.
	 * 
	 * @param edgeId
	 *            id of the edge to remove
	 */
	protected void delEdge(String edgeId) {
		sendEdgeRemoved(sourceId, edgeId);

		if (useInternalGraph)
			internalGraph.removeEdge(edgeId);
	}

	/**
	 * Clear the internal graph if {@link #useInternalGraph} is enable.
	 * 
	 * This method is called in {@link #end()} to ensure the next generation
	 * will start freshly anew.
	 */
	protected void clearKeptData() {
		if (useInternalGraph)
			internalGraph.clear();
	}
}
