package msi.gama.util.graph.loader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaDynamicLink;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.PostponedWarningList;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.species.ISpecies;

/**
 * Listens for graph parsing events, 
 * and constructs the corresponding GamaGraph and populations of edge and nodes.
 * Principles:
 * <ul>
 * <li>as a general philosophy, warnings are emitted when data was available in the graph, but not used for agents</li>
 * <li>x,y,z attributes are processed as special attributes related to the location</li>
 * </ul>
 * 
 * @author Samuel Thiriot
 *
 */
public class GamaGraphParserListener implements IGraphParserListener {

	IPopulation populationNodes = null;
	IPopulation populationEdges = null;
	GamaGraph gamaGraph = null;
	IScope scope  = null;
	
	private Map<String, IAgent> nodeId2agent = null;
	private Map<String, IAgent> edgeId2agent = null;

	PostponedWarningList warnings = new PostponedWarningList();
	
	private Map<String,String> nodeGraphAttribute2AgentAttribute = null;
	private Map<String,String> edgeGraphAttribute2AgentAttribute = null;
	
	public GamaGraphParserListener(
			IScope scope, 
			ISpecies nodeSpecies,
			ISpecies edgeSpecies,
			Map<String,String> nodeGraphAttribute2AgentAttribute, 
			Map<String,String> edgeGraphAttribute2AgentAttribute
			) {
		
		
		this.scope = scope;
		this.nodeGraphAttribute2AgentAttribute = nodeGraphAttribute2AgentAttribute;
		this.edgeGraphAttribute2AgentAttribute = edgeGraphAttribute2AgentAttribute;
		
		// retrieve IPopulations from species
		if (scope != null) {
			final IAgent executor = scope.getAgentScope();
			this.populationNodes = (nodeSpecies == null?null:executor.getPopulationFor(nodeSpecies));
			this.populationEdges = (edgeSpecies == null?null:executor.getPopulationFor(edgeSpecies));		
		}
	}


	@Override
	public void startOfParsing() {
		
		gamaGraph = new GamaGraph();
		nodeId2agent = new HashMap<String, IAgent>();
		edgeId2agent = new HashMap<String, IAgent>();

		warnings.clear();
	}
	
	public GamaGraph getGraph() {
		return gamaGraph;
	}
	

	@Override
	public void detectedNode(String nodeId) {
		
		if (populationNodes != null) {
			// create an agent of the target specy
			IList<? extends IAgent> createdAgents = populationNodes.createAgents(scope, 1, Collections.EMPTY_LIST, false);
			IAgent createdAgent = createdAgents.get(0);
	
			// update internal mapping
			nodeId2agent.put(nodeId, createdAgent);
	
			// actually add the agent to the graph
			gamaGraph.addVertex(createdAgent);
		} else {
			gamaGraph.addVertex(nodeId);
		}
	}
	
	
	private void agentAttributeNotFound(String attributeName) {
		StringBuffer sb = new StringBuffer();
		sb.append("The agent attribute \"").append(attributeName).append("\" is not declared. The content of the corresponding attribute of the graph will be ignored");
		warnings.addWarning(sb.toString());
	}
	private void edgeAttributeNotFound(String attributeName) {
		StringBuffer sb = new StringBuffer();
		sb.append("The edge attribute \"").append(attributeName).append("\" is not declared. The content of the corresponding attribute of the graph will be ignored");
		warnings.addWarning(sb.toString());
	}
	
	@Override
	public void detectedEdge(String edgeId, String fromNodeId, String toNodeId) {
		
		// check parameter
		/* TODO
		if ( directed != gamaGraph.isDirected() ) { throw new GamaRuntimeException(
			"Attempted to read an " + (directed ? "" : "un") + "directed edge for a " +
				(gamaGraph.isDirected() ? "" : "un") + "directed graph"); }
*/
		
		Object nodeFrom = null;
		Object nodeTo = null;
		
		if (populationNodes != null) {
			// retrieve the agents for this edge
			nodeFrom = nodeId2agent.get(fromNodeId);
			if ( nodeFrom == null ) { throw new GamaRuntimeException(
				"Error while parsing graph: the node " + fromNodeId + " was not declared"); }
			nodeTo = nodeId2agent.get(toNodeId);
			if ( nodeTo == null ) { throw new GamaRuntimeException(
				"Error while parsing graph: the node " + toNodeId + " was not declared");
			// TODO : add support for nodes that were not declared ? (may be supported in some file
			// formats)
			}

		} else {
			nodeFrom = fromNodeId;
			nodeTo = toNodeId;
		}
		
		if (populationEdges != null) {
			// create the agent of the target specy
			IList<? extends IAgent> createdAgents =
						populationEdges.createAgents(scope, 1, Collections.EMPTY_LIST, false);
			
			IAgent createdAgent = createdAgents.get(0);
			
			edgeId2agent.put(edgeId, createdAgent);
			
			// create the shape for this agent
			if (populationNodes != null) {
				GamaDynamicLink dl = new GamaDynamicLink((IShape)nodeFrom, (IShape)nodeTo);
				createdAgent.setGeometry(dl);
			}
			
			// actually add the edge
			gamaGraph.addEdge(nodeFrom, nodeTo, createdAgent);

		} else {
			// actually add the edge
			gamaGraph.addEdge(nodeFrom, nodeTo, edgeId);
	
		}
		
	}
	
	protected double parseValueAsDouble(Object o) {
		if (o == null)
			throw new NullPointerException();
		if (o instanceof Double) 
			return ((Double)o).doubleValue();
		if (o instanceof Float) 
			return ((Float)o).doubleValue();
		if (o instanceof Integer)
			return ((Integer)o).doubleValue();
		if (o instanceof Long)
			return ((Long)o).doubleValue();
		return Double.parseDouble(o.toString());
	}

	@Override
	public void detectedNodeAttribute(String nodeId, String attributeName,
			Object value) {
		
		if (populationNodes == null) {
			// can't set an attribute for a graph without agents
			warnings.addWarning("a node attribute was ignored, because no specy of agent is associated with nodes: attribute '"+attributeName+"'");
			return;
		}
		
		IAgent agent = nodeId2agent.get(nodeId);
		if ( agent == null ) { throw new GamaRuntimeException(
				"Error while parsing graph: the node " + nodeId + " was not declared"); }
		
		// special case of attributes for position: x,y,z
		if (attributeName.equalsIgnoreCase("X")) {
			agent.getLocation().setX(parseValueAsDouble(value));
			return;
		} 
		if (attributeName.equalsIgnoreCase("Y")) {
			agent.getLocation().setY(parseValueAsDouble(value));
			return;
		}
		if ((attributeName.equalsIgnoreCase("Z"))) {
			agent.getLocation().setZ(parseValueAsDouble(value));
			return;
		}
		if ((attributeName.equalsIgnoreCase("xyz"))) {
			//if (Object instanceof array)
			//agent.getLocation().setZ(parseValueAsDouble(value));
			//return;
			// TODO !!! process xyz values
		}
		
		
		// special case of colors
		// TODO !
		
		//if ((attributeName.equalsIgnoreCase("color"))) {
					
		// standard case: match with attribute
		if (nodeGraphAttribute2AgentAttribute == null) {
			if (agent.getAttributes().containsKey(attributeName))
				agent.setAttribute(attributeName, value);
			else
				agentAttributeNotFound(attributeName);
		} else {
			String agentAttribute = nodeGraphAttribute2AgentAttribute.get(attributeName);
			if (agent.getAttributes().containsKey(agentAttribute))
				agent.setAttribute(agentAttribute, value);
			else
				agentAttributeNotFound(agentAttribute);
		}
		
	}

	@Override
	public void detectedEdgeAttribute(String edgeId, String attributeName,
			Object value) {
		
		if (populationEdges == null) {
			// can't set an attribute for a graph without agents
			warnings.addWarning("an edge attribute was ignored, because no specy of agent is associated with edges: attribute '"+attributeName+"'");
			return;
		}
		
		IAgent agent = edgeId2agent.get(edgeId);
		if ( agent == null ) { throw new GamaRuntimeException(
				"Error while parsing graph: the edge " + edgeId + " was not declared"); }
		
		if (edgeGraphAttribute2AgentAttribute == null) {
			if (agent.getAttributes().containsKey(attributeName)) 
				agent.setAttribute(attributeName, value);
			else
				edgeAttributeNotFound(attributeName);
			
		} else {
			String agentAttribute = edgeGraphAttribute2AgentAttribute.get(attributeName);
			if (agent.getAttributes().containsKey(agentAttribute))
				agent.setAttribute(agentAttribute, value);
			else
				edgeAttributeNotFound(agentAttribute);
			
		}
		
	}

	@Override
	public void endOfParsing() {
		
		warnings.publishAsGAMAWarning("during the interpretation of the graph as species, several warnings were raised:");
	}


}
