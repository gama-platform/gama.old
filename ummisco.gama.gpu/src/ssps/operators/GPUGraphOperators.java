/*********************************************************************************************
 * 
 * 
 * 'GPUGraphOperators.java', in plugin 'ummisco.gama.gpu', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ssps.operators;

import java.util.Set;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.*;
import msi.gaml.types.Types;
import ssps.graph.Graph;

public class GPUGraphOperators {

	@operator(value = "GPU_path_between", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
		IOperatorCategory.GRAPH, IOperatorCategory.PATH },
			concept = { IConcept.SHORTEST_PATH, IConcept.SYSTEM, IConcept.OPTIMIZATION, IConcept.GRAPH })
	@doc(value = "The shortest path between a list of two objects in a graph computed with GPU",
		examples = { @example(value = "my_graph GPU_path_between (ag1:: ag2)",
			equals = "A path between ag1 and ag2",
			isExecutable = false) })
	public static IPath GPUpath_between(final IScope scope, final GamaGraph graph, final IShape source,
		final IShape target) throws GamaRuntimeException {
		return path_between_GPU(scope, graph, source, target);
	}

	@operator(value = "CPU_path_between", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
		IOperatorCategory.GRAPH, IOperatorCategory.PATH },
			concept = { IConcept.SHORTEST_PATH, IConcept.SYSTEM, IConcept.OPTIMIZATION, IConcept.GRAPH })
	@doc(value = "The shortest path between a list of two objects in a graph computed with CPU",
		examples = { @example(value = "my_graph CPU_path_between (ag1:: ag2)",
			equals = "A path between ag1 and ag2",
			isExecutable = false) })
	public static IPath CPUpath_between(final IScope scope, final GamaGraph graph, final IShape source,
		final IShape target) throws GamaRuntimeException {
		return path_between_CPU(scope, graph, source, target);
	}

	static Graph toGPUGraph(final GamaGraph graph) {
		int nbVertices = graph.getVertices().size();
		int nbEdges = graph.getEdges().size() * (graph.isDirected() ? 1 : 2);
		int[] vertices = new int[nbVertices];
		int[] edges = new int[nbEdges];
		float[] weights = new float[nbEdges];
		int i = 0;
		int k = 0;
		GamaMap<Object, Integer> vertexId = GamaMapFactory.create(Types.NO_TYPE, Types.INT);
		int id = 0;
		for ( Object v : graph.getVertices() ) {
			vertexId.put(v, id);
			id++;
		}
		for ( Object v : graph.getVertices() ) {
			vertices[i] = k;
			i++;
			Set edgs = graph.isDirected() ? graph.outgoingEdgesOf(v) : graph.edgesOf(v);
			for ( Object e : edgs ) {
				if ( graph.isDirected() ) {
					edges[k] = vertexId.get(graph.getEdgeTarget(e));
				} else {
					Object tt = graph.getEdgeTarget(e);
					if ( tt == v ) {
						tt = graph.getEdgeSource(e);
					}
					edges[k] = vertexId.get(tt);
				}
				weights[k] = new Float(graph.getWeightOf(e));
				k++;
			}
		}
		return new Graph(vertices, edges, weights, nbVertices, nbEdges, vertexId);
	}

	static IPath path_between_GPU(final IScope scope, final GamaGraph graph, final IShape source, final IShape target) {
		IList edges = graph.getShortestPath(source, target);
		if ( edges == null ) {
			// /Commencer a construire des vertexs et des arcs
			Graph GPUgraph = graph.getLinkedGraph() instanceof Graph ? (Graph) graph.getLinkedGraph() : null;
			if ( GPUgraph == null ) {
				GPUgraph = toGPUGraph(graph);
				graph.setLinkedGraph(GPUgraph);
				GPUgraph.getDijkstraGPU().init(GPUgraph);
			}
			GamaMap<Object, Integer> vertexId = GPUgraph.getVertexId();

			// For GPU
			int idS = vertexId.get(source);
			int idT = vertexId.get(target);
			int M[] = GPUgraph.getDijkstraGPU().searchSPGPU(GPUgraph, idS);
			edges =
				graph.isSaveComputedShortestPaths() ? graph.savePaths(M, (GamaList) graph.getVertices(), graph
					.getVertices().size(), source, idS, idT) : graph.getPath(M, (GamaList) graph.getVertices(), graph
					.getVertices().size(), source, target, idS, vertexId.get(target));

		}
		if ( graph instanceof GamaSpatialGraph ) { return PathFactory.newInstance(graph, source, target, edges); }
		return PathFactory.newInstance(graph, source, target, edges);
	}

	static IPath path_between_CPU(final IScope scope, final GamaGraph graph, final IShape source, final IShape target) {
		// /Commencer a construire des vertexs et des arcs
		IList edges = graph.getShortestPath(source, target);
		if ( edges == null ) {

			Graph GPUgraph = graph.getLinkedGraph() instanceof Graph ? (Graph) graph.getLinkedGraph() : null;
			if ( GPUgraph == null ) {
				GPUgraph = toGPUGraph(graph);
				graph.setLinkedGraph(GPUgraph);
				GPUgraph.getDijkstraGPU().init(GPUgraph);
			}
			GamaMap<Object, Integer> vertexId = GPUgraph.getVertexId();

			// For CPU
			int idS = vertexId.get(source);
			int idT = vertexId.get(target);
			int M[] = GPUgraph.getDijkstraGPU().searchSPCPU(GPUgraph, idS);
			edges =
				graph.isSaveComputedShortestPaths() ? graph.savePaths(M, (GamaList) graph.getVertices(), graph
					.getVertices().size(), source, idS, idT) : graph.getPath(M, (GamaList) graph.getVertices(), graph
					.getVertices().size(), source, target, idS, vertexId.get(target));
		}
		if ( graph instanceof GamaSpatialGraph ) { return PathFactory.newInstance(graph, source, target, edges); }
		return PathFactory.newInstance(graph, source, target, edges);
	}

}
