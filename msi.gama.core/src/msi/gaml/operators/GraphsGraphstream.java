package msi.gaml.operators;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.graph.GraphAlgorithmsFromGraphstream;
import msi.gama.util.graph.GraphGeneratorFromAlgorithmParameters;
import msi.gama.util.graph.GraphGeneratorFromFileParameters;
import msi.gama.util.graph.IGraph;
import msi.gaml.species.ISpecies;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;
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

	@operator(value = "load_graph_from_pajek")
	@doc(
		value = "returns a graph loaded from a given file following Pajek file format.",
		comment = "Pajek (Slovene word for Spider) is a program, for Windows, for analysis and visualization of large networks. " +
				"See: http://pajek.imfm.si/doku.php?id=pajek for more details." +
				"The map operand should includes following elements:",
		special_cases = {
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_pajek( [",
				"			\"filename\"::\"example_of_Pajek_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")
	public static IGraph primLoadGraphFromFileFromPajek(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),
				new FileSourcePajek()
				);		
		
	}
	
	@operator(value = "load_graph_from_dgs_old")
	@doc(
		value = "returns a graph loaded from a given file following DGS file format (version 3).",
		comment = "DGS is a file format allowing to store graphs and dynamic graphs in a textual human readable way, yet with a small size allowing to store large graphs. " +
				"Graph dynamics is defined using events like adding, deleting or changing a node or edge. " +
				"With DGS, graphs will therefore be seen as stream of such events. [From GraphStream related page: http://graphstream-project.org/]" + 
				"The map operand should includes following elements:",
		special_cases = {
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},			
		examples = {
			"let my_graph type: graph <- load_graph_from_dgs_old( [",
			"			\"filename\"::\"../includes/BarabasiGenerated.dgs\",",  
			"			\"edges_specy\"::edgeSpecy,",
			"			\"vertices_specy\"::nodeSpecy] );"},
		see = {"load_graph_from_dgs"})                    
	public static IGraph primLoadGraphFromFileFromDGSOld(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
	
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope, 
				new GraphGeneratorFromFileParameters(parameters),
				new OldFileSourceDGS()
				);
			
	}	
	
	@operator(value = "load_graph_from_dgs")
	@doc(
		value = "returns a graph loaded from a given file following DGS graph file format versions 1 and 2",
		comment = "similar to load_graph_from_dgs_old",
		see = "load_graph_from_dgs_old")
	public static IGraph primLoadGraphFromFileFromDGS(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceDGS1And2()
				);		
			
	}
	
	@operator(value = "load_graph_from_lgl")
	@doc(
		value = "returns a graph loaded from a given file following LGL file format.",
		comment = "LGL is a compendium of applications for making the visualization of large networks and trees tractable. " +
				"See: http://lgl.sourceforge.net/ for more details." +
				"The map operand should includes following elements:",
		special_cases = {
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_lgl( [",
				"			\"filename\"::\"example_of_LGL_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")	
	public static IGraph primLoadGraphFromFileFromLGL(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceLGL()
				);		
			
	}
	
	@operator(value = "load_graph_from_dot")
	@doc(
		value = "returns a graph loaded from a given file following DOT file format.",
		comment = "DOT is a plain text graph description language. It is a simple way of describing graphs that both humans and computer programs can use. " +
				"See: http://en.wikipedia.org/wiki/DOT_language for more details." +
				"The map operand should includes following elements:",
		special_cases = {
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_dot( [",
				"			\"filename\"::\"example_of_dot_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")	
	public static IGraph primLoadGraphFromFileFromDot(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceDOT()
				);		
			
	}
	
	@operator(value = "load_graph_from_edge")
	@doc( 
		value = "returns a graph loaded from a given file following Edge file format.",
		comment = "This format is a simple text file with numeric vertex ids defining the edges. " +
				"The map operand should includes following elements:",
		special_cases = {
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_edge( [",
				"			\"filename\"::\"example_of_edge_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")		
	public static IGraph primLoadGraphFromFileFromEdge(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceEdge()
				);		
			
	}
	
	@operator(value = "load_graph_from_gexf")
	@doc(
		value = "returns a graph loaded from a given file following GEXF file format.",
		comment = "GEXF (Graph Exchange XML Format) is a language for describing complex networks structures, their associated data and dynamics. " +
				"Started in 2007 at Gephi project by different actors, deeply involved in graph exchange issues, " +
				"the gexf specifications are mature enough to claim being both extensible and open, and suitable for real specific applications. " +
				"See: http://gexf.net/format/ for more details." +
				"The map operand should includes following elements:",
		special_cases = {
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_gexf( [",
				"			\"filename\"::\"example_of_Gexf_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")	
	public static IGraph primLoadGraphFromFileFromGEFX(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceGEXF()
				);		
			
	}
	
	@operator(value = "load_graph_from_graphml")
	@doc(
		value = "returns a graph loaded from a given file following GEXF file format.",
		comment = "GraphML is a comprehensive and easy-to-use file format for graphs based on XML.  " +
				"See: http://graphml.graphdrawing.org/ for more details." +
				"The map operand should includes following elements:",
		special_cases = { 
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_graphml( [",
				"			\"filename\"::\"example_of_Graphml_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")		
	public static IGraph primLoadGraphFromFileFromGraphML(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceGraphML()
				);		
			
	}
	
	@operator(value = "load_graph_from_tlp")
	@doc(
		value = "returns a graph loaded from a given file following TLP file format.",
		comment = "TLP is the Tulip software graph format.  " +
				"See: http://tulip.labri.fr/TulipDrupal/?q=tlp-file-format for more details." +
				"The map operand should includes following elements:",
		special_cases = { 
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_tlp( [",
				"			\"filename\"::\"example_of_TLP_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")	
	public static IGraph primLoadGraphFromFileFromTLP(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceTLP()
				);		
			
	}

	@operator(value = "load_graph_from_ncol")
	@doc(
		value = "returns a graph loaded from a given file following ncol file format.",
		comment = "This format is used by the Large Graph Layout progra. It is simply a symbolic weighted edge list. " +
				"It is a simple text file with one edge per line. An edge is defined by two symbolic vertex names separated by whitespace. " +
				"(The symbolic vertex names themselves cannot contain whitespace.) They might followed by an optional number, this will be the weight of the edge. " +
				"See: http://bioinformatics.icmb.utexas.edu/lgl for more details." +
				"The map operand should includes following elements:",
		special_cases = { 
			"\"filename\": the filename of the file containing the network",
			"\"edges_specy\": the species of edges",
			"\"vertices_specy\": the species of vertices"},	
		examples = {
				"let my_graph type: graph <- load_graph_from_ncol( [",
				"			\"filename\"::\"example_of_ncol_file\",",  
				"			\"edges_specy\"::edgeSpecy,",
				"			\"vertices_specy\"::nodeSpecy] );"},			
		see = "load_graph_from_dgs_old")		
	public static IGraph primLoadGraphFromFileFromNCol(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceNCol()
				);		
			
	}
	
	/*
	 * ============ Generation functions
	 */
	
	/**
	 * TODO this version of the barabasi albert generator is too simple. Switch to the implementation
	 * of another library. 
	 * 
	 * @param scope
	 * @param parameters
	 * @return
	 */
	@operator(value = "generate_barabasi_albert")
	@doc(
		value = "returns a random scale-free network (following Barabasi–Albert (BA) model).",
		comment = "The Barabasi–Albert (BA) model is an algorithm for generating random scale-free networks using a preferential attachment mechanism. " +
				"A scale-free network is a network whose degree distribution follows a power law, at least asymptotically."+
				"Such networks are widely observed in natural and human-made systems, including the Internet, the world wide web, citation networks, and some social networks. [From Wikipedia article]" +
				"The map operand should includes following elements:",
		special_cases = {
				"\"edges_specy\": the species of edges",
				"\"vertices_specy\": the species of vertices",
				"\"size\": the graph will contain (size + 1) nodes",
				"\"m\": the number of edges added per novel node"},
		examples = {
				"let graphEpidemio type: graph <- generate_barabasi_albert( [",
				"		\"edges_specy\"::edge,",
				"		\"vertices_specy\"::node,",
				"		\"size\"::3,",
				"		\"m\"::5] );"},
		see = {"generate_watts_strogatz"})	
	public static IGraph generateGraphstreamBarabasiAlbert(final IScope scope, final GamaMap parameters) {
		
		GraphGeneratorFromAlgorithmParameters params = new GraphGeneratorFromAlgorithmParameters(parameters);
		
		// the number of edges added per novel node
		int m = 1;
		
		if (parameters.containsKey("m"))
			try {
			m = (Integer)parameters.get("m");
			} catch (ClassCastException e) {
				throw new GamaRuntimeException("parameter m should be an integer value");
			}
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromGeneratorSource(
				scope, 
				params,
				new BarabasiAlbertGenerator(m),
				params.size
				);
		
	}

	private static class GraphGeneratorWattsStrogatzParameters extends GraphGeneratorFromAlgorithmParameters {

		public final static String PARAMETER_K_STR = "k";
		public final static String PARAMETER_P_STR = "p";

		public final Integer k;
		public final Double p;
		
		public GraphGeneratorWattsStrogatzParameters(GamaMap gamaMap)
				throws GamaRuntimeException {
			super(gamaMap);

			this.k = castParamInteger(gamaMap, PARAMETER_K_STR);
			this.p = castParamDouble(gamaMap, PARAMETER_P_STR);
			
			myEnsureIntegrity();
		}
		

		public GraphGeneratorWattsStrogatzParameters(ISpecies specyEdges,
				ISpecies specyVertices, Integer size, Integer k, Double p)
				throws GamaRuntimeException {
			super(specyEdges, specyVertices, size);
			
			this.k = k;
			this.p = p;
			myEnsureIntegrity();
		}

		private final void myEnsureIntegrity() throws GamaRuntimeException {
			
			
			ensureNotNull(PARAMETER_P_STR, p);
			ensurePositive(PARAMETER_P_STR, p);
			ensureLower(PARAMETER_P_STR, 1.0, p);
			
			ensureNotNull(PARAMETER_K_STR, k);
			ensurePositive(PARAMETER_K_STR, k);
			ensureGreaterEq(PARAMETER_K_STR, 1, k);
			
		}

		@Override
		protected void enqueueToString(StringBuffer sb) {
			super.enqueueToString(sb);
			
			sb
				.append(PARAMETER_P_STR).append("=").append(p).append(", ")
				.append(PARAMETER_K_STR).append("=").append(k).append(", ")
				;
		}
		
		@Override
		protected void ensureIntegrity() throws GamaRuntimeException {
		
			super.ensureIntegrity();
			
			myEnsureIntegrity();
			
		}
	}

	@operator(value = "generate_watts_strogatz")
	@doc(
		value = "returns a random small-world network (following Watts-Strogatz model).",
		comment = "The Watts-Strogatz model is a random graph generation model that produces graphs with small-world properties, including short average path lengths and high clustering." +
				"A small-world network is a type of graph in which most nodes are not neighbors of one another, but most nodes can be reached from every other by a small number of hops or steps. [From Wikipedia article]" +
				"The map operand should includes following elements:",
		special_cases = {
				"\"edges_specy\": the species of edges",
				"\"vertices_specy\": the species of vertices",
				"\"size\": the graph will contain (size + 1) nodes. Size must be greater than k.",
				"\"p\": probability to \"rewire\" an edge. So it must be between 0 and 1. The parameter is often called beta in the literature.",
				"\"k\": the base degree of each node. k must be greater than 2 and even."},
		examples = {
			"let graphWatts type: graph <- generate_watts_strogatz( [\"",
			"			\"edges_specy\"::edge,",
			"			\"vertices_specy\"::node,",
			"			\"size\"::2,",
			"			\"p\"::0.3,",
			"			\"k\"::0] );"},
		see = {"generate_barabasi_albert"})		
	public static IGraph generateGraphstreamWattsStrogatz(final IScope scope, final GamaMap parameters) {
		
		GraphGeneratorWattsStrogatzParameters params = new GraphGeneratorWattsStrogatzParameters(parameters);
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromGeneratorSource(
				scope, 
				params,
				new WattsStrogatzGenerator(params.size, params.k, params.p),
				-1
				);
		
	}
	
	
}
