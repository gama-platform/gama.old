/*********************************************************************************************
 *
 * 'GamaGraphParserListener.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.loader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.PostponedWarningList;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Listens for graph parsing events, and constructs the corresponding GamaGraph and populations of edge and nodes.
 * Principles:
 * <ul>
 * <li>as a general philosophy, warnings are emitted when data was available in the graph, but not used for agents</li>
 * <li>x,y,z attributes are processed as special attributes related to the location</li>
 * <li>TODO directionality of the resulting graph is automatically detected: if every edge is not directed, then the
 * graph is not directed.
 * </ul>
 *
 * @author Samuel Thiriot
 *
 */
@SuppressWarnings ({ "rawtypes" })
public class GamaGraphParserListener implements IGraphParserListener {

	IPopulation<? extends IAgent> populationNodes = null;
	IPopulation<? extends IAgent> populationEdges = null;
	GamaGraph gamaGraph = null;
	IScope scope = null;
	boolean isSpatial = false;

	private Map<String, IAgent> nodeId2agent = null;
	private Map<String, IAgent> edgeId2agent = null;

	PostponedWarningList warnings = new PostponedWarningList();

	private Map<String, String> nodeGraphAttribute2AgentAttribute = null;
	private Map<String, String> edgeGraphAttribute2AgentAttribute = null;

	// TODO
	// private final boolean detectedUndirectedEdges = false;
	// private final boolean detectedDirectedEdges = false;

	public GamaGraphParserListener(final IScope scope, final ISpecies nodeSpecies, final ISpecies edgeSpecies,
			final Map<String, String> nodeGraphAttribute2AgentAttribute,
			final Map<String, String> edgeGraphAttribute2AgentAttribute, final boolean spatial) {

		this.scope = scope;
		this.nodeGraphAttribute2AgentAttribute = nodeGraphAttribute2AgentAttribute;
		this.edgeGraphAttribute2AgentAttribute = edgeGraphAttribute2AgentAttribute;

		// retrieve IPopulations from species
		if (scope != null) {
			final IAgent executor = scope.getAgent();
			this.populationNodes = nodeSpecies == null ? null : executor.getPopulationFor(nodeSpecies);
			this.populationEdges = edgeSpecies == null ? null : executor.getPopulationFor(edgeSpecies);
		}
		// AD 29/09/13
		isSpatial = spatial;
	}

	@Override
	public void startOfParsing() {
		final IType nodeType = populationNodes == null ? Types.NO_TYPE : populationNodes.getGamlType().getContentType();
		final IType edgeType = populationEdges == null ? Types.NO_TYPE : populationEdges.getGamlType().getContentType();
		gamaGraph =
				isSpatial ? new GamaSpatialGraph(scope, nodeType, edgeType) : new GamaGraph(scope, nodeType, edgeType);
		nodeId2agent = new HashMap<String, IAgent>();
		edgeId2agent = new HashMap<String, IAgent>();

		warnings.clear();
	}

	public GamaGraph getGraph() {
		return gamaGraph;
	}

	@Override
	public void detectedNode(final IScope scope, final String nodeId) {

		if (populationNodes != null) {
			// create an agent of the target specy
			final IList<? extends IAgent> createdAgents =
					populationNodes.createAgents(scope, 1, Collections.EMPTY_LIST, false, true);
			final IAgent createdAgent = createdAgents.get(0);

			// update internal mapping
			nodeId2agent.put(nodeId, createdAgent);

			// actually add the agent to the graph
			gamaGraph.addVertex(createdAgent);
		} else {
			gamaGraph.addVertex(nodeId);
		}
	}

	private void agentAttributeNotFound(final String attributeName) {
		final StringBuffer sb = new StringBuffer();
		sb.append("The agent attribute \"").append(attributeName)
				.append("\" is not declared. The content of the corresponding attribute of the graph will be ignored");
		warnings.addWarning(sb.toString());
	}

	private void edgeAttributeNotFound(final String attributeName) {
		final StringBuffer sb = new StringBuffer();
		sb.append("The edge attribute \"").append(attributeName)
				.append("\" is not declared. The content of the corresponding attribute of the graph will be ignored");
		warnings.addWarning(sb.toString());
	}

	@Override
	public void detectedEdge(final IScope scope, final String edgeId, final String fromNodeId, final String toNodeId) {

		// check parameter
		/*
		 * TODO if ( directed != gamaGraph.isDirected() ) { throw new GamaRuntimeException( "Attempted to read an " +
		 * (directed ? "" : "un") + "directed edge for a " + (gamaGraph.isDirected() ? "" : "un") + "directed graph"); }
		 */

		Object nodeFrom = null;
		Object nodeTo = null;

		if (populationNodes != null) {
			// retrieve the agents for this edge
			nodeFrom = nodeId2agent.get(fromNodeId);
			if (nodeFrom == null) { throw GamaRuntimeException
					.error("Error while parsing graph: the node " + fromNodeId + " was not declared", scope); }
			nodeTo = nodeId2agent.get(toNodeId);
			if (nodeTo == null) { throw GamaRuntimeException
					.error("Error while parsing graph: the node " + toNodeId + " was not declared", scope);
			// TODO : add support for nodes that were not declared ? (may be
			// supported in some file
			// formats)
			}

		} else {
			nodeFrom = fromNodeId;
			nodeTo = toNodeId;
		}

		if (populationEdges != null) {
			// create the agent of the target specy
			final IList<? extends IAgent> createdAgents =
					populationEdges.createAgents(scope, 1, Collections.EMPTY_LIST, false, true);

			final IAgent createdAgent = createdAgents.get(0);

			edgeId2agent.put(edgeId, createdAgent);

			// create the shape for this agent
			if (populationNodes != null) {
				final GamaShape dl = GamaGeometryType.buildLink(scope, (IShape) nodeFrom, (IShape) nodeTo);
				createdAgent.setGeometry(dl);
			}

			// actually add the edge
			gamaGraph.addEdge(nodeFrom, nodeTo, createdAgent);

		} else {
			// actually add the edge
			gamaGraph.addEdge(nodeFrom, nodeTo, edgeId);

		}

	}

	protected double parseValueAsDouble(final Object o) {
		if (o == null) { throw new NullPointerException(); }
		if (o instanceof Double) { return ((Double) o).doubleValue(); }
		if (o instanceof Float) { return ((Float) o).doubleValue(); }
		if (o instanceof Integer) { return ((Integer) o).doubleValue(); }
		if (o instanceof Long) { return ((Long) o).doubleValue(); }
		return Double.parseDouble(o.toString());
	}

	@Override
	public void detectedNodeAttribute(final IScope scope, final String nodeId, final String attributeName,
			final Object value) {

		if (populationNodes == null) {
			// can't set an attribute for a graph without agents
			warnings.addWarning(
					"a node attribute was ignored, because no specy of agent is associated with nodes: attribute '"
							+ attributeName + "'");
			return;
		}

		final IAgent agent = nodeId2agent.get(nodeId);
		if (agent == null) { throw GamaRuntimeException
				.error("Error while parsing graph: the node " + nodeId + " was not declared", scope); }

		// special case of attributes for position: x,y,z
		if (attributeName.equalsIgnoreCase("X")) {
			agent.getLocation().setX(parseValueAsDouble(value));
			return;
		}
		if (attributeName.equalsIgnoreCase("Y")) {
			agent.getLocation().setY(parseValueAsDouble(value));
			return;
		}
		if (attributeName.equalsIgnoreCase("Z")) {
			agent.getLocation().setZ(parseValueAsDouble(value));
			return;
		}
		if (attributeName.equalsIgnoreCase("XYZ")) {
			try {
				final Object[] values = (Object[]) value;
				agent.getLocation().setX(parseValueAsDouble(values[0]));
				agent.getLocation().setY(parseValueAsDouble(values[1]));
				agent.getLocation().setY(parseValueAsDouble(values[2]));
			} catch (final ClassCastException e) {
				warnings.addWarning(
						"unable to process node attribute 'xyz': expected an array of locations, but this was not the case.");
			} catch (final IndexOutOfBoundsException e) {
				warnings.addWarning(
						"unable to process node attribute 'xyz': expected an array of 3 locations, but the array was not big enough.");
			}
			return;
		}

		// special case of colors
		// TODO !

		// if ((attributeName.equalsIgnoreCase("color"))) {

		// standard case: match with attribute
		if (nodeGraphAttribute2AgentAttribute == null) {
			if (agent.getAttributes().containsKey(attributeName)) {
				agent.setAttribute(attributeName, value);
			} else {
				agentAttributeNotFound(attributeName);
			}
		} else {
			final String agentAttribute = nodeGraphAttribute2AgentAttribute.get(attributeName);
			if (agent.getAttributes().containsKey(agentAttribute)) {
				agent.setAttribute(agentAttribute, value);
			} else {
				agentAttributeNotFound(agentAttribute);
			}
		}

	}

	@Override
	public void detectedEdgeAttribute(final IScope scope, final String edgeId, final String attributeName,
			final Object value) {

		if (populationEdges == null) {
			// can't set an attribute for a graph without agents
			warnings.addWarning(
					"an edge attribute was ignored, because no specy of agent is associated with edges: attribute '"
							+ attributeName + "'");
			return;
		}

		final IAgent agent = edgeId2agent.get(edgeId);
		if (agent == null) { throw GamaRuntimeException
				.error("Error while parsing graph: the edge " + edgeId + " was not declared", scope); }

		if (edgeGraphAttribute2AgentAttribute == null) {
			if (agent.getAttributes().containsKey(attributeName)) {
				agent.setAttribute(attributeName, value);
			} else {
				edgeAttributeNotFound(attributeName);
			}

		} else {
			final String agentAttribute = edgeGraphAttribute2AgentAttribute.get(attributeName);
			if (agent.getAttributes().containsKey(agentAttribute)) {
				agent.setAttribute(agentAttribute, value);
			} else {
				edgeAttributeNotFound(agentAttribute);
			}

		}

	}

	@Override
	public void endOfParsing(final IScope scope) {

		warnings.publishAsGAMAWarning(scope,
				"during the interpretation of the graph as species, several warnings were raised:");
	}

}
