package msi.gama.util.graph;

import java.io.*;
import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaDynamicLink;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.stream.*;
import org.graphstream.stream.file.FileSource;

/**
 * Various utilites for graph manipulation that come from graphstream
 * or are required for the use of Graphstream algorithms in GAMA.
 * 
 * @author Samuel Thiriot
 * 
 */
public class GraphAlgorithmsFromGraphstream {

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

		public GraphStreamGamaGraphSink(IGraph gamaGraph, IScope scope,
			IPopulation populationNodes, IPopulation populationEdges) {
			this.gamaGraph = gamaGraph;
			this.scope = scope;
			this.populationNodes = populationNodes;
			this.populationEdges = populationEdges;

			this.initialValues = new LinkedList<Map>();
		}

		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId,
			String toNodeId, boolean directed) {

			// check parameter
			if ( directed != gamaGraph.isDirected() ) { throw new GamaRuntimeException(
				"Attempted to read an " + (directed ? "" : "un") + "directed edge for a " +
					(gamaGraph.isDirected() ? "" : "un") + "directed graph"); }

			// retrieve the agents for this edge
			IAgent agentFrom = nodeId2agent.get(fromNodeId);
			if ( agentFrom == null ) { throw new GamaRuntimeException(
				"Error while parsing graph: the node " + fromNodeId + " was not declared"); }
			IAgent agentTo = nodeId2agent.get(toNodeId);
			if ( agentTo == null ) { throw new GamaRuntimeException(
				"Error while parsing graph: the node " + toNodeId + " was not declared");
			// TODO : add support for nodes that were not declared ? (may be supported in some file
			// formats)
			}

			// create the agent of the target specy
			IList<? extends IAgent> createdAgents =
				populationEdges.createAgents(scope, 1, initialValues, false);
			IAgent createdAgent = createdAgents.get(0);

			// create the shape for this agent
			GamaDynamicLink dl = new GamaDynamicLink(agentFrom, agentTo);
			createdAgent.setGeometry(dl);

			// actually add the edge
			gamaGraph.addEdge(agentFrom, agentTo, createdAgent);

		}

		@Override
		public void nodeAdded(String sourceId, long timeId, String nodeId) {

			// create an agent of the target specy
			IList<? extends IAgent> createdAgents =
				populationNodes.createAgents(scope, 1, initialValues, false);
			IAgent createdAgent = createdAgents.get(0);

			// update internal mapping
			nodeId2agent.put(nodeId, createdAgent);

			// actually add the agent to the graph
			gamaGraph.addVertex(createdAgent);

		}

	}

	/**
	 * Loads a graph from the given Graphstream filesource.
	 * @param scope
	 * @param params
	 * @param fileSourceBase
	 * @return
	 */
	public static IGraph loadGraphWithGraphstreamFromFileSourceBase(final IScope scope,
		GraphGeneratorFromFileParameters params, FileSource fileSourceBase) {

		// check parameters

		File file = params.file;
		if ( file != null ) {
			// first attempt to parse parameter as file
			try {
				file = (File) scope.getArg("file", IType.FILE);
			} catch (GamaRuntimeException e) {
				// unable to load it as a file
				// attempting to parse a string and open the corresponding file

			}
		}
		if ( file == null ) {
			String filename = params.filename;
			file =
				new File(scope.getSimulationScope().getModel().getRelativeFilePath(filename, false));
		}

		ISpecies nodeSpecies = params.specyVertices;
		ISpecies edgeSpecies = params.specyEdges;

		// init population of edges
		final IAgent executor = scope.getAgentScope();
		IPopulation populationNodes = executor.getPopulationFor(nodeSpecies);
		IPopulation populationEdges = executor.getPopulationFor(edgeSpecies);

		// creates the graph to be filled
		IGraph createdGraph = new GamaGraph(false);

		Sink ourSink =
			new GraphStreamGamaGraphSink(createdGraph, scope, populationNodes, populationEdges);

		fileSourceBase.addSink(ourSink);

		// attempt to open the file
		InputStream is;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new GamaRuntimeException("Unable to load file from " + file.getAbsolutePath() +
				" (" + e.getLocalizedMessage() + ")");
		}

		// load the graph

		try {
			fileSourceBase.begin(is);
		} catch (IOException e) {
			throw new GamaRuntimeException("Error while loading graph from " +
				file.getAbsolutePath() + " (" + e.getLocalizedMessage() + ")");
		}
		try {
			while (fileSourceBase.nextEvents()) {
				// nothing to do
			}
		} catch (IOException e) {
			throw new GamaRuntimeException("Error while parsing graph from " +
				file.getAbsolutePath() + " (" + e.getLocalizedMessage() + ")");
		}
		try {
			fileSourceBase.end();
		} catch (IOException e) {
			throw new GamaRuntimeException("Error while finishing to parse the graph from " +
				file.getAbsolutePath() + " (" + e.getLocalizedMessage() + ")");
		}

		// synchronize agents and graph
		GraphAndPopulationsSynchronizer.synchronize(populationNodes, populationEdges, createdGraph);

		return createdGraph;

	}

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
	public static IGraph loadGraphWithGraphstreamFromGeneratorSource(final IScope scope,
		GraphGeneratorParameters params, BaseGenerator generator, int maxLinks) {

		// check parameters

		ISpecies nodeSpecies = params.specyVertices;
		ISpecies edgeSpecies = params.specyEdges;

		// init population of edges
		final IAgent executor = scope.getAgentScope();
		IPopulation populationNodes = executor.getPopulationFor(nodeSpecies);
		IPopulation populationEdges = executor.getPopulationFor(edgeSpecies);

		// creates the graph to be filled
		IGraph createdGraph = new GamaGraph(false);

		Sink ourSink =
			new GraphStreamGamaGraphSink(createdGraph, scope, populationNodes, populationEdges);

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
		GraphAndPopulationsSynchronizer.synchronize(populationNodes, populationEdges, createdGraph);

		return createdGraph;

	}

}
