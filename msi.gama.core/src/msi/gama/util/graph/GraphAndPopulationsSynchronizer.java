/*******************************************************************************************************
 *
 * msi.gama.util.graph.GraphAndPopulationsSynchronizer.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.types.GamaGeometryType;

/**
 * Syncs a graph with two populations of agents (one for edges, one for nodes).
 * <ul>
 * <li>When a node agent dies, the corresponding node is removed from the network.</li>
 * <li>When an edge agent dies, the corresponding edge is removed from the network.</li>
 * <li>When an edge is removed, the corresponding edge agent dies.</li>
 * <li>When a node is removed, the corresponding node agent dies.</li>
 * <li>When a novel node agent is created, a novel node is created into the graph</li>
 * <li>When a novel edge agent is created, an exception is thrown (creating an edge without its targets is meaningless)
 * </li>
 * </ul>
 *
 * @author Samuel Thiriot
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GraphAndPopulationsSynchronizer implements IPopulation.Listener, IGraphEventListener {

	private final IPopulation popVertices;
	private final IPopulation popEdges;
	private final IGraph graph;

	/**
	 * The last vertex and edge for which we sent an event. Avoids first-order loops between events from graphs and
	 * populations
	 */
	private Object currentEventVertex = null;
	private Object currentEventEdge = null;

	// private boolean ignoreNextEvent = false;

	private final List<Map> initialValues = Collections.EMPTY_LIST;

	public GraphAndPopulationsSynchronizer(final IPopulation popVertices, final IPopulation popEdges,
			final IGraph graph) {
		this.popVertices = popVertices;
		this.popEdges = popEdges;
		this.graph = graph;

	}

	@Override
	public void notifyAgentRemoved(final IScope scope, final IPopulation pop, final IAgent agent) {

		if (pop == popVertices) {
			if (currentEventVertex != agent) {
				try {
					currentEventVertex = agent;
					graph.removeVertex(agent);
				} catch (final RuntimeException e) {
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
				} catch (final RuntimeException e) {
					e.printStackTrace();
				}
			}
			currentEventEdge = null;
		}

	}

	@Override
	public void notifyAgentAdded(final IScope scope, final IPopulation pop, final IAgent agent) {

		if (pop != popVertices) { throw GamaRuntimeException.error(
				"Cannot create edge agents from the population (please add an edge in the graph instead)", scope); }

		if (currentEventVertex != agent) {
			try {
				currentEventVertex = agent;
				graph.addVertex(agent);
			} catch (final RuntimeException e) {
				e.printStackTrace();
			}
		}
		currentEventVertex = null;

	}

	@Override
	public void notifyAgentsAdded(final IScope scope, final IPopulation pop, final Collection agents) {

		/*
		 * if (pop == popEdges) { for (Object o : agents) { if (!graph.containsEdge(o)) { throw new
		 * GamaRuntimeException(
		 * "Cannot create edge agents from the population (please add an edge in the graph instead)" ); }
		 *
		 * }
		 *
		 * }
		 */
		if (pop == popVertices) {
			for (final Object o : agents) {
				if (currentEventVertex != o) {
					try {
						currentEventVertex = o;
						graph.addVertex(o);
					} catch (final RuntimeException e) {
						e.printStackTrace();
					}
				}
				currentEventVertex = null;

			}
		}

	}

	@Override
	public void notifyAgentsRemoved(final IScope scope, final IPopulation pop, final Collection agents) {

		if (pop == popVertices) {
			for (final Object o : agents) {
				if (currentEventVertex != o) {
					try {
						currentEventVertex = o;
						graph.removeVertex(o);
					} catch (final RuntimeException e) {
						e.printStackTrace();
					}
				}
				currentEventVertex = null;
			}
		}

		if (pop == popEdges) {
			for (final Object o : agents) {
				if (currentEventEdge != o) {
					try {
						currentEventEdge = o;
						graph.removeEdge(o);
					} catch (final RuntimeException e) {
						e.printStackTrace();
					}
				}
				currentEventEdge = null;
			}
		}

	}

	@Override
	public void notifyPopulationCleared(final IScope scope, final IPopulation pop) {

		try {
			graph.removeAllVertices(graph.vertexSet());
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void receiveEvent(final IScope scope, final GraphEvent event) {

		switch (event.eventType) {
			case EDGE_REMOVED:
				if (currentEventEdge != event.edge) {
					try {
						currentEventEdge = event.edge;
						((IAgent) event.edge).dispose();
					} catch (final RuntimeException e) {
						e.printStackTrace();
					}
				}
				currentEventEdge = null;
				break;
			case VERTEX_REMOVED:
				if (currentEventVertex != event.vertex) {
					try {
						currentEventVertex = event.vertex;
						((IAgent) event.vertex).dispose();
					} catch (final RuntimeException e) {
						e.printStackTrace();
					}
				}
				currentEventVertex = null;
				break;
			case EDGE_ADDED: {
				if (currentEventEdge != event.edge) {
					currentEventEdge = event.edge;
					// create the agent of the target specy
					final IList<? extends IAgent> createdAgents =
							popEdges.createAgents(event.scope, 1, initialValues, false, true);
					final IAgent createdAgent = createdAgents.get(0);

					// create the shape for this agent
					final GamaShape dl = GamaGeometryType.buildLink(event.scope,
							(IShape) graph.getEdgeSource(event.edge), (IShape) graph.getEdgeTarget(event.edge));
					createdAgent.setGeometry(dl);
				}
				currentEventEdge = null;
			}
				break;
			case VERTEX_ADDED: {
				if (currentEventVertex != event.vertex) {
					currentEventVertex = event.vertex;
					// create an agent of the target specy
					/* IList<? extends IAgent> createdAgents = */
					popVertices.createAgents(event.scope, 1, initialValues, false, true);
					// IAgent createdAgent = createdAgents.get(0);
				}
				currentEventVertex = null;
				break;
			}
			default:
				break;
		}

	}

	/**
	 * Creates a synchronizer which listens for a population of vertices and updates the graph accordingly
	 * 
	 * @param popVertices
	 * @param graph
	 * @return
	 */
	public static GraphAndPopulationsSynchronizer synchronize(final IPopulation popVertices, final IPopulation popEdges,
			final IGraph graph) {

		final GraphAndPopulationsSynchronizer res = new GraphAndPopulationsSynchronizer(popVertices, popEdges, graph);
		popVertices.addListener(res);
		popEdges.addListener(res);
		graph.addListener(res);
		return res;

	}

}
