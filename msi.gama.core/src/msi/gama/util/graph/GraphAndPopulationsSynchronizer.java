package msi.gama.util.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationListener;
import msi.gama.metamodel.shape.GamaDynamicLink;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;

/**
 * Syncs a graph with two populations of agents (one for edges, one for nodes).
 * <ul>
 * <li>When a node agent dies, the corresponding node is removed from the network.</li>
 * <li>When an edge agent dies, the corresponding edge is removed from the network.</li>
 * <li>When an edge is removed, the corresponding edge agent dies.</li>
 * <li>When a node is removed, the corresponding node agent dies.</li>
 * <li>When a novel node agent is created, a novel node is created into the graph</li>
 * <li>When a novel edge agent is created, an exception is thrown (creating an edge without 
 * its targets is meaningless)</li>
 * </ul> 
 * 
 * @author Samuel Thiriot
 */
public class GraphAndPopulationsSynchronizer implements IPopulationListener, IGraphEventListener {

	private IPopulation popVertices;
	private IPopulation popEdges;
	private IGraph graph;
	
	/**
	 * The last vertex and edge for which we sent an event.
	 * Avoids first-order loops between events from graphs and populations
	 */
	private Object currentEventVertex = null;
	private Object currentEventEdge = null;
	

	//private boolean ignoreNextEvent = false;
	
	private List<Map<String, Object>> initialValues = Collections.EMPTY_LIST;

	public GraphAndPopulationsSynchronizer (IPopulation popVertices, IPopulation popEdges, IGraph graph) {
		this.popVertices = popVertices;
		this.popEdges = popEdges;
		this.graph = graph;
	
	}
	
	@Override
	public void notifyAgentRemoved(IPopulation pop, IAgent agent) {
		
		if (pop == popVertices) {
			if (currentEventVertex != agent) {
				try {
					currentEventVertex = agent;
					graph.removeVertex(agent);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			currentEventVertex = null;
		}
		
		if (pop == popEdges) {
			if (currentEventEdge != agent) {
				try {
					currentEventEdge = agent;
					graph.removeEdge(agent);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			currentEventEdge = null;
		}
		
	}

	@Override
	public void notifyAgentAdded(IPopulation pop, IAgent agent) {
		
		if (pop != popVertices)
			throw new GamaRuntimeException("Cannot create edge agents from the population (please add an edge in the graph instead)");
		
		if (currentEventVertex != agent) {
			try {
				currentEventVertex = agent;
				graph.addVertex(agent);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		currentEventVertex = null;

	}

	@Override
	public void notifyAgentsAdded(IPopulation pop, Collection agents) {
		
		if (pop != popVertices)
			throw new GamaRuntimeException("Cannot create edge agents from the population (please add an edge in the graph instead)");
		
		for (Object o : agents) {
			if (currentEventVertex != o) {
				try {
					currentEventVertex = o;
					graph.addVertex((IAgent)o);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			currentEventVertex = null;

		}
		
	}

	@Override
	public void notifyAgentsRemoved(IPopulation pop, Collection agents) {
		
		
		if (pop == popVertices) {
			for (Object o : agents) {
				if (currentEventVertex != o) {
					try {
						currentEventVertex = o;
						graph.removeVertex((IAgent)o);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
				currentEventVertex = null;
			}	
		}
		
		if (pop == popEdges) {
			for (Object o : agents) {
				if (currentEventEdge != o) {
					try {
						currentEventEdge = o;
						graph.removeEdge((IAgent)o);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}	
				}
				currentEventEdge = null;
			}	
		}
					
	}

	@Override
	public void notifyPopulationCleared(IPopulation pop) {
		
		try {
			graph.clear();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void receiveEvent(GraphEvent event) {
		
		switch (event.eventType) {
		case EDGE_REMOVED:
			if (currentEventEdge != event.edge) {
				try {
					currentEventEdge = event.edge;
					((IAgent)event.edge).die();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			currentEventEdge = null;
			break;
		case VERTEX_REMOVED:
			if (currentEventVertex != event.vertex) {
				try {
					currentEventVertex = event.vertex;
					((IAgent)event.vertex).die();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			currentEventVertex = null;
			break;
		case EDGE_ADDED: {
			if (currentEventEdge != event.edge) {
				currentEventEdge = event.edge;
				// create the agent of the target specy
				IList<? extends IAgent> createdAgents = popEdges.createAgents(
						GAMA.getFrontmostSimulation().getExecutionScope(), 
						1, 
						initialValues, 
						false
						);
				IAgent createdAgent = createdAgents.get(0);
				
				// create the shape for this agent
				GamaDynamicLink dl = new GamaDynamicLink(
						(IShape)graph.getEdgeSource(event.edge),
						(IShape)graph.getEdgeTarget(event.edge)	
						);
				createdAgent.setGeometry(dl);
			}
			currentEventEdge = null;
			} break;	
		case VERTEX_ADDED: {
			if (currentEventVertex != event.vertex) {
				currentEventVertex = event.vertex;
				// create an agent of the target specy
				IList<? extends IAgent> createdAgents = popVertices.createAgents(
						GAMA.getFrontmostSimulation().getExecutionScope(), 
						1, 
						initialValues, 
						false
						);
				IAgent createdAgent = createdAgents.get(0);
			}
			currentEventVertex = null;
		}
		default:
			break;
		}
		
	}
	
	/**
	 * Creates a synchronizer which listens for a population 
	 * of vertices and updates the graph accordingly
	 * @param popVertices
	 * @param graph
	 * @return
	 */
	public static GraphAndPopulationsSynchronizer synchronize(IPopulation popVertices, IPopulation popEdges, IGraph graph) {
		
		GraphAndPopulationsSynchronizer res = new GraphAndPopulationsSynchronizer(popVertices, popEdges, graph);
		popVertices.addListener(res);
		popEdges.addListener(res);
		graph.addListener(res);
		return res;
		
	}
	
}
