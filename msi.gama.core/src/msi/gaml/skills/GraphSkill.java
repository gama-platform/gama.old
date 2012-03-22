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
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceBase;
import org.graphstream.stream.file.FileSourceDGS1And2;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceEdge;
import org.graphstream.stream.file.FileSourceGEXF;
import org.graphstream.stream.file.FileSourceGraphML;
import org.graphstream.stream.file.FileSourceLGL;
import org.graphstream.stream.file.FileSourceNCol;
import org.graphstream.stream.file.FileSourcePajek;
import org.graphstream.stream.file.FileSourceTLP;
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
	class GraphStreamGamaGraphSink extends SinkAdapter {
		
		private IGraph gamaGraph;
		private IPopulation populationNodes;
		private IPopulation populationEdges;
		private IScope scope;
		
		private List<Map<String, Object>> initialValues;
		
		private Map<String,IAgent> nodeId2agent = new HashMap<String, IAgent>();
		
		public GraphStreamGamaGraphSink(IGraph gamaGraph, IScope scope, IPopulation populationNodes, IPopulation populationEdges) {
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
			GamaDynamicLink dl = new GamaDynamicLink(
					nodeId2agent.get(fromNodeId),
					nodeId2agent.get(toNodeId)	
					);
			createdAgent.setGeometry(dl);
			
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
			
			
		}
		
	}
	
	/**
	 * Checks GAMA parameters from the scope.
	 * 
	 * @param scope
	 * @return
	 */
	protected IGraph loadGraphWithGraphstreamFromFileSourceBase(final IScope scope, FileSource fileSourceBase) {

		// check parameters
		String filename = (String)scope.getArg("file", IType.STRING);

		// TODO manage the case of type File
		
		
		ISpecies nodeSpecies = (ISpecies)scope.getArg("vertex_species", IType.SPECIES);
		ISpecies edgeSpecies = (ISpecies)scope.getArg("edge_species", IType.SPECIES);

		// init population of edges
		final IAgent executor = scope.getAgentScope();
		IPopulation populationNodes = executor.getPopulationFor(nodeSpecies);
		
		IPopulation populationEdges = executor.getPopulationFor(edgeSpecies);

		// creates the graph to be filled 
		IGraph createdGraph = new GamaGraph(false);

		Sink ourSink = new GraphStreamGamaGraphSink(createdGraph, scope, populationNodes, populationEdges);
		
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
		
		
		return createdGraph;
	

	}
	
	@action("load_graph_from_dgs")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromDGS(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceDGS1And2());		
		
			
	}
	
	@action("load_graph_from_lgl")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromLGL(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceLGL());		
		
	}
	

	@action("load_graph_from_dot")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromDot(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceDOT());		
		
	}
	
	@action("load_graph_from_edge")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromEdge(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceEdge());		
		
	}

	@action("load_graph_from_gexf")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromGEXF(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceGEXF());		
		
	}
	
	@action("load_graph_from_graphml")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromGraphML(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceGraphML());		
		
	}

	@action("load_graph_from_tlp")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromTLP(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceTLP());		
		
	}

	@action("load_graph_from_ncol")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromNCol(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourceNCol());		
		
	}	
	
	@action("load_graph_from_pajek")
	@args({ "edge_species", "vertex_species", "file" })
	public IGraph primLoadGraphFromFileFromPajek(final IScope scope) throws GamaRuntimeException {		
		
		return loadGraphWithGraphstreamFromFileSourceBase(scope, new FileSourcePajek());		
		
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
