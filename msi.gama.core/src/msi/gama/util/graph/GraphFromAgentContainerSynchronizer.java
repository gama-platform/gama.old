/*******************************************************************************************************
 *
 * msi.gama.util.graph.GraphFromAgentContainerSynchronizer.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.Collection;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;

/**
 * Syncs a graph with two populations of agents (one for edges, one for nodes).
 * <ul>
 * <li>When a node agent dies, the corresponding node is removed from the network.</li>
 * <li>When an edge agent dies, the corresponding edge is removed from the network.</li>
 * </ul>
 *
 * @author Benoit Gaudou, from Samuel Thiriot (GraphAndPopulationSynchronize.java)
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GraphFromAgentContainerSynchronizer implements IPopulation.Listener {

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

	// private final List<Map> initialValues = Collections.EMPTY_LIST;

	public GraphFromAgentContainerSynchronizer(final IScope scope, final IContainer setVertices,
			final IContainer setEdges, final IGraph graph) {
		this.popVertices = setVertices == null ? null : ((IAgent) setVertices.firstValue(scope)).getPopulation();
		this.popEdges = setEdges == null ? null : ((IAgent) setEdges.firstValue(scope)).getPopulation();
		this.graph = graph;

	}

	IPopulation getVerticesPopulation() {
		return popVertices;
	}

	IPopulation getEdgesPopulation() {
		return popEdges;
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
	public void notifyAgentAdded(final IScope scope, final IPopulation pop, final IAgent agent) {}

	@Override
	public void notifyAgentsAdded(final IScope scope, final IPopulation pop, final Collection agents) {}

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

	/**
	 * Creates a synchronizer which listens for a population of vertices and updates the graph accordingly
	 * 
	 * @param popVertices
	 * @param graph
	 * @return
	 */
	public static GraphFromAgentContainerSynchronizer synchronize(final IScope scope, final IContainer popVertices,
			final IContainer popEdges, final IGraph graph) {

		final GraphFromAgentContainerSynchronizer res =
				new GraphFromAgentContainerSynchronizer(scope, popVertices, popEdges, graph);

		if (res.getEdgesPopulation() != null) {
			res.getEdgesPopulation().addListener(res);
		}
		if (res.getVerticesPopulation() != null) {
			res.getVerticesPopulation().addListener(res);
		}
		return res;
	}
}
