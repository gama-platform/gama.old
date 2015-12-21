/*********************************************************************************************
 *
 *
 * 'GraphsGraphstream.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import java.util.*;
import org.graphstream.algorithm.generator.*;
import org.graphstream.stream.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.graph.*;
import msi.gaml.species.ISpecies;

/**
 * Contains the graph operators based on the graphstream library.
 *
 * @author Samuel Thiriot
 *
 */
public class GraphsGraphstream {

	/*
	 * ====== Loading functions
	 */

	/**
	 * Receives events from a graphstream loader
	 * and updates the GAMA Igraph accordingly
	 *
	 * TODO other events like attributes
	 *
	 * @author Samuel Thiriot
	 *
	 */
	public static class GraphStreamGamaGraphSink extends SinkAdapter {

		private final IGraph gamaGraph;
		private final IPopulation populationNodes;
		private final IPopulation populationEdges;
		private final IScope scope;

		private final List<Map> initialValues;

		private final Map<String, IAgent> nodeId2agent = new HashMap<String, IAgent>();

		public GraphStreamGamaGraphSink(final IGraph gamaGraph, final IScope scope, final IPopulation populationNodes,
			final IPopulation populationEdges) {
			this.gamaGraph = gamaGraph;
			this.scope = scope;
			this.populationNodes = populationNodes;
			this.populationEdges = populationEdges;

			this.initialValues = new LinkedList<Map>();
		}

		@Override
		public void edgeAdded(final String sourceId, final long timeId, final String edgeId, final String fromNodeId,
			final String toNodeId, final boolean directed) {

			// check parameter
			if ( directed != gamaGraph
				.isDirected() ) { throw GamaRuntimeException.error("Attempted to read an " + (directed ? "" : "un") +
					"directed edge for a " + (gamaGraph.isDirected() ? "" : "un") + "directed graph", scope); }

			// retrieve the agents for this edge
			IAgent agentFrom = nodeId2agent.get(fromNodeId);
			if ( agentFrom == null ) { throw GamaRuntimeException
				.error("Error while parsing graph: the node " + fromNodeId + " was not declared", scope); }
			IAgent agentTo = nodeId2agent.get(toNodeId);
			if ( agentTo == null ) { throw GamaRuntimeException
				.error("Error while parsing graph: the node " + toNodeId + " was not declared", scope);
			// TODO : add support for nodes that were not declared ? (may be supported in some file
			// formats)
			}

			// create the agent of the target specy
			IList<? extends IAgent> createdAgents = populationEdges.createAgents(scope, 1, initialValues, false, null);
			IAgent createdAgent = createdAgents.get(0);

			// create the shape for this agent
			GamaDynamicLink dl = new GamaDynamicLink(agentFrom, agentTo);
			createdAgent.setGeometry(dl);

			// actually add the edge
			gamaGraph.addEdge(agentFrom, agentTo, createdAgent);

		}

		@Override
		public void nodeAdded(final String sourceId, final long timeId, final String nodeId) {

			// create an agent of the target specy
			IList<? extends IAgent> createdAgents = populationNodes.createAgents(scope, 1, initialValues, false, null);
			IAgent createdAgent = createdAgents.get(0);

			// update internal mapping
			nodeId2agent.put(nodeId, createdAgent);

			// actually add the agent to the graph
			gamaGraph.addVertex(createdAgent);

		}

	}

	/*
	 * ============ Saving to files
	 */

	/*
	 * ============ Generation functions
	 */

	/**
	 * Loads the graph from the graphstream generator passed
	 * as parameter and according to the parameters passed from GAMA.
	 * Note that the generator is supposed to be already configured.
	 * @param scope
	 * @param params
	 * @param generator
	 * @param maxLinks if provided, no more events than this int will be processed
	 * @return
	 */
	public static IGraph loadGraphWithGraphstreamFromGeneratorSource(final IScope scope, final ISpecies nodeSpecies,
		final ISpecies edgeSpecies, final BaseGenerator generator, final int maxLinks, final Boolean isSynchronized) {

		// init population of edges
		final IAgent executor = scope.getAgentScope();
		IPopulation populationNodes = executor.getPopulationFor(nodeSpecies);
		IPopulation populationEdges = executor.getPopulationFor(edgeSpecies);

		// creates the graph to be filled
		IGraph createdGraph = new GamaGraph(scope, false, populationNodes.getType().getContentType(),
			populationEdges.getType().getContentType());

		Sink ourSink = new GraphStreamGamaGraphSink(createdGraph, scope, populationNodes, populationEdges);

		generator.addSink(ourSink);

		// load the graph

		if ( maxLinks < 0 ) {
			generator.begin();
			while (generator.nextEvents()) {
				// nothing to do
			}
			generator.end();
		} else {
			generator.begin();
			for ( int i = 0; i < maxLinks; i++ ) {
				generator.nextEvents();
			}
			generator.end();
		}

		// synchronize agents and graph
		if ( isSynchronized ) {
			GraphAndPopulationsSynchronizer.synchronize(populationNodes, populationEdges, createdGraph);
		}

		return createdGraph;

	}

	/**
	 * TODO this version of the barabasi albert generator is too simple. Switch to the implementation
	 * of another library.
	 *
	 * @param scope
	 * @param parameters
	 * @return
	 */
	@operator(value = "generate_barabasi_albert")
	@doc(value = "returns a random scale-free network (following Barabasi-Albert (BA) model).",
		comment = "The Barabasi-Albert (BA) model is an algorithm for generating random scale-free networks using a preferential attachment mechanism. " +
			"A scale-free network is a network whose degree distribution follows a power law, at least asymptotically." +
			"Such networks are widely observed in natural and human-made systems, including the Internet, the world wide web, citation networks, and some social networks. [From Wikipedia article]" +
			"The map operand should includes following elements:",
		usages = { @usage("\"edges_specy\": the species of edges"),
			@usage("\"vertices_specy\": the species of vertices"),
			@usage("\"size\": the graph will contain (size + 1) nodes"),
			@usage("\"m\": the number of edges added per novel node"),
			@usage("\"synchronized\": is the graph and the species of vertices and edges synchronized?") },
		examples = {
			@example(value = "graph<yourNodeSpecy,yourEdgeSpecy> graphEpidemio <- generate_barabasi_albert(",
				isExecutable = false),
			@example(value = "		yourNodeSpecy,", isExecutable = false),
			@example(value = "		yourEdgeSpecy,", isExecutable = false),
			@example(value = "		3,", isExecutable = false), @example(value = "		5,", isExecutable = false),
			@example(value = "		true);", isExecutable = false) },
		see = { "generate_watts_strogatz" })
	public static IGraph generateGraphstreamBarabasiAlbert(final IScope scope, final ISpecies vertices_specy,
		final ISpecies edges_specy, final Integer size, final Integer m, final Boolean isSychronized) {

		return loadGraphWithGraphstreamFromGeneratorSource(scope, vertices_specy, edges_specy,
			new BarabasiAlbertGenerator(m), size - 2 // nota: in graphstream, two nodes are already created by default.,
				,
			isSychronized);

	}

	@operator(value = "generate_watts_strogatz")
	@doc(value = "returns a random small-world network (following Watts-Strogatz model).",
		comment = "The Watts-Strogatz model is a random graph generation model that produces graphs with small-world properties, including short average path lengths and high clustering." +
			"A small-world network is a type of graph in which most nodes are not neighbors of one another, but most nodes can be reached from every other by a small number of hops or steps. [From Wikipedia article]" +
			"The map operand should includes following elements:",
		usages = { @usage("\"vertices_specy\": the species of vertices"),
			@usage("\"edges_specy\": the species of edges"),
			@usage("\"size\": the graph will contain (size + 1) nodes. Size must be greater than k."),
			@usage("\"p\": probability to \"rewire\" an edge. So it must be between 0 and 1. The parameter is often called beta in the literature."),
			@usage("\"k\": the base degree of each node. k must be greater than 2 and even."),
			@usage("\"synchronized\": is the graph and the species of vertices and edges synchronized?") },
		examples = {
			@example(value = "graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_watts_strogatz(",
				isExecutable = false),
			@example(value = "			myVertexSpecy,", isExecutable = false),
			@example(value = "			myEdgeSpecy,", isExecutable = false),
			@example(value = "			2,", isExecutable = false),
			@example(value = "			0.3,", isExecutable = false),
			@example(value = "			2,", isExecutable = false),
			@example(value = "		true);", isExecutable = false) },
		see = { "generate_barabasi_albert" })
	public static IGraph generateGraphstreamWattsStrogatz(final IScope scope, final ISpecies vertices_specy,
		final ISpecies edges_specy, final Integer size, final Double p, final Integer k, final Boolean isSychronized) {

		return loadGraphWithGraphstreamFromGeneratorSource(scope, vertices_specy, edges_specy,
			new WattsStrogatzGenerator(size, k, p), -1, isSychronized);
	}

	@operator(value = "generate_complete_graph")
	@doc(value = "returns a fully connected graph.",
		comment = "Arguments should include following elements:",
		usages = { @usage("\"vertices_specy\": the species of vertices"),
			@usage("\"edges_specy\": the species of edges"), @usage("\"size\": the graph will contain size nodes."),
			@usage("\"layoutRadius\": nodes of the graph will be located on a circle with radius layoutRadius and centered in the environment."),
			@usage("\"synchronized\": is the graph and the species of vertices and edges synchronized?") },
		examples = {
			@example(value = "graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_complete_graph(",
				isExecutable = false),
			@example(value = "			myVertexSpecy,", isExecutable = false),
			@example(value = "			myEdgeSpecy,", isExecutable = false),
			@example(value = "			10, 25,", isExecutable = false),
			@example(value = "		true);", isExecutable = false) },
		see = { "generate_barabasi_albert", "generate_watts_strogatz" })
	public static IGraph generateGraphstreamComplete(final IScope scope, final ISpecies vertices_specy,
		final ISpecies edges_specy, final Integer size, final double layoutRadius, final Boolean isSychronized) {

		IGraph g = loadGraphWithGraphstreamFromGeneratorSource(scope, vertices_specy, edges_specy, new FullGenerator(),
			size - 1, isSychronized);

		double THETA = 2 * Math.PI / size;
		int i = 0;
		IList<GamlAgent> listVertex = g.getVertices();
		ILocation locEnv = scope.getSimulationScope().getGeometry().getLocation();
		for ( GamlAgent e : listVertex ) {
			e.setLocation(new GamaPoint(locEnv.getX() + layoutRadius * Math.cos(THETA * i),
				locEnv.getY() + layoutRadius * Math.sin(THETA * i), locEnv.getZ()));
			GuiUtils.informConsole("Graph " + e.getLocation() + " " + i + " THETA " + THETA);
			i++;
		}
		return g;
	}

	@operator(value = "generate_complete_graph")
	@doc(value = "returns a fully connected graph.",
		comment = "Arguments should include following elements:",
		usages = { @usage("\"vertices_specy\": the species of vertices"),
			@usage("\"edges_specy\": the species of edges"), @usage("\"size\": the graph will contain size nodes."),
			@usage("\"synchronized\": is the graph and the species of vertices and edges synchronized?") },
		examples = {
			@example(value = "graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_complete_graph(",
				isExecutable = false),
			@example(value = "			myVertexSpecy,", isExecutable = false),
			@example(value = "			myEdgeSpecy,", isExecutable = false),
			@example(value = "			10,", isExecutable = false),
			@example(value = "		true);", isExecutable = false) },
		see = { "generate_barabasi_albert", "generate_watts_strogatz" })
	public static IGraph generateGraphstreamComplete(final IScope scope, final ISpecies vertices_specy,
		final ISpecies edges_specy, final Integer size, final Boolean isSychronized) {

		return loadGraphWithGraphstreamFromGeneratorSource(scope, vertices_specy, edges_specy, new FullGenerator(),
			size - 1, isSychronized);
	}

}
