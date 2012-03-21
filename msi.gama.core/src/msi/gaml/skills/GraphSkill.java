package msi.gaml.skills;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaDynamicLink;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;
import msi.gaml.factories.SpeciesFactory;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.file.dgs.OldFileSourceDGS;



@skill({ IKeyword.GRAPH_SKILL })
public class GraphSkill extends Skill {

	
	/**
	 * Receives events from a graphstream loader 
	 * and updates the GAMA Igraph accordingly
	 * 
	 * TODO other events like attributes
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	class GamaGraphSink extends SinkAdapter {
		
		private IGraph gamaGraph;
		private IPopulation populationNodes;
		private IPopulation populationEdges;
		private IScope scope;
		
		private List<Map<String, Object>> initialValues;
		
		private Map<String,IAgent> nodeId2agent = new HashMap<String, IAgent>();
		
		public GamaGraphSink(IGraph gamaGraph, IScope scope, IPopulation populationNodes, IPopulation populationEdges) {
			this.gamaGraph = gamaGraph;
			this.scope = scope;
			this.populationNodes = populationNodes;
			this.populationEdges = populationEdges;
			
			this.initialValues = new LinkedList<Map<String,Object>>();
		}

		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			
			gamaGraph.addEdge(fromNodeId, toNodeId);
			
			IList<? extends IAgent> createdAgents = populationEdges.createAgents(scope, 1, initialValues, false);
			IAgent createdAgent = createdAgents.get(0);
			createdAgent.setGeometry(new GamaDynamicLink(
					nodeId2agent.get(fromNodeId).getGeometry(),
					nodeId2agent.get(toNodeId).getGeometry()	
					));
			
			System.out.println(fromNodeId+" "+toNodeId);
			System.out.println(nodeId2agent.get(fromNodeId).getGeometry());
			
			if (directed != gamaGraph.isDirected())
				throw new GamaRuntimeException("Attempted to read an "+(directed?"":"un")+"directed edge for a "+(gamaGraph.isDirected()?"":"un")+"directed graph");
			
		}

		@Override
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			
			gamaGraph.addVertex(nodeId);
			//gamaGraph.get(sourceId);
			IList<? extends IAgent> createdAgents = populationNodes.createAgents(scope, 1, initialValues, false);
			IAgent createdAgent = createdAgents.get(0);
			
			nodeId2agent.put(nodeId, createdAgent);
			
			System.out.println(nodeId);
			
		}
		
	}
	
	@action("load_graph_from_pajek")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromPajek(final IScope scope) throws GamaRuntimeException {		
		
		// check parameters
		String filename = (String)scope.getArg("file", IType.STRING);
		
		ISpecies nodeSpecies = (ISpecies)scope.getArg("vertex_species", IType.SPECIES);
		ISpecies edgeSpecies = (ISpecies)scope.getArg("edge_species", IType.SPECIES);
		

		// TODO manage the case of type File

		// init population of edges
		final IAgent executor = scope.getAgentScope();
		IPopulation populationNodes = executor.getPopulationFor(nodeSpecies);
		
		IPopulation populationEdges = executor.getPopulationFor(edgeSpecies);


		// TODO types
		IGraph createdGraph = new GamaGraph(false);
		
		OldFileSourceDGS fileSourceBase = new OldFileSourceDGS();
		
		Sink ourSink = new GamaGraphSink(createdGraph, scope, populationNodes, populationEdges);
		
		fileSourceBase.addSink(ourSink);
		
		// attempt to open the file
		InputStream is;
		try {
			is = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			throw new GamaRuntimeException("Unable to load file from "+filename+" ("+e.getLocalizedMessage()+")");
		}
		
		try {
			fileSourceBase.begin(is);
		} catch (IOException e) {
			throw new GamaRuntimeException("Error while loading graph from "+filename+" ("+e.getLocalizedMessage()+")");
		}
		
		try {
			while (fileSourceBase.nextEvents()) {
				// nothing to do
			}
		} catch (IOException e) {
			throw new GamaRuntimeException("Error while parsing graph from "+filename+" ("+e.getLocalizedMessage()+")");
		}
		
		try {
			fileSourceBase.end();
		} catch (IOException e) {
			throw new GamaRuntimeException("Error while finishing to parse the graph from "+filename+" ("+e.getLocalizedMessage()+")");
		}
		
		System.out.println("total vertices: "+createdGraph.getVertices().size());
		System.out.println("total edges: "+createdGraph.getEdges().size());
		
		return createdGraph;
		
	}
	
	@action("generate_barabasi_graph")
	@args({ "nb_links", "nb_nodes"})
	public IGraph primGenerateBarabasiGraph(final IScope scope) throws GamaRuntimeException {
		Graph graph = new SingleGraph("Barab�si-Albert");
		// Between 1 and 3 new links per node added.
		int v= (Integer) scope.getArg("nb_links", IType.INT);
		int nb_nodes= (Integer) scope.getArg("nb_nodes", IType.INT);	
		Generator gen = new BarabasiAlbertGenerator(v);
		// Generate nb_nodes nodes:
		gen.addSink(graph);
		gen.begin();
		for(int i=0; i<nb_nodes; i++) {
		    gen.nextEvents();
		}
		gen.end();
		graph.display();
		return null;
	}
	
	
	
}
