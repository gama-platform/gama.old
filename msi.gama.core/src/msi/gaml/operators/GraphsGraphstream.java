package msi.gaml.operators;

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
	public static IGraph primLoadGraphFromFileFromPajek(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),
				new FileSourcePajek()
				);		
		
	}
	
	@operator(value = "load_graph_from_dgs_old")
	public static IGraph primLoadGraphFromFileFromDGSOld(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
	
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope, 
				new GraphGeneratorFromFileParameters(parameters),
				new OldFileSourceDGS()
				);
			
	}	
	
	@operator(value = "load_graph_from_dgs")
	public static IGraph primLoadGraphFromFileFromDGS(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceDGS1And2()
				);		
			
	}
	
	@operator(value = "load_graph_from_lgl")
	public static IGraph primLoadGraphFromFileFromLGL(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceLGL()
				);		
			
	}
	
	@operator(value = "load_graph_from_dot")
	public static IGraph primLoadGraphFromFileFromDot(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceDOT()
				);		
			
	}
	
	@operator(value = "load_graph_from_edge")
	public static IGraph primLoadGraphFromFileFromEdge(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceEdge()
				);		
			
	}
	
	@operator(value = "load_graph_from_gexf")
	public static IGraph primLoadGraphFromFileFromGEFX(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceGEXF()
				);		
			
	}
	
	@operator(value = "load_graph_from_graphml")
	public static IGraph primLoadGraphFromFileFromGraphML(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceGraphML()
				);		
			
	}
	
	@operator(value = "load_graph_from_tlp")
	public static IGraph primLoadGraphFromFileFromTLP(final IScope scope, final GamaMap parameters) throws GamaRuntimeException {		
		
		return GraphAlgorithmsFromGraphstream.loadGraphWithGraphstreamFromFileSourceBase(
				scope,
				new GraphGeneratorFromFileParameters(parameters),	
				new FileSourceTLP()
				);		
			
	}

	@operator(value = "load_graph_from_ncol")
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
