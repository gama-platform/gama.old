package msi.gama.util.graph;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSinkGML;
import org.graphstream.stream.file.FileSinkSVG;
import org.graphstream.stream.file.FileSinkTikZ;

/**
 * This class groups the graph exportations features provided by the graphstream library.
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphExportersFromGraphStream {

	
	/**
	 * Maps the string for the format to a graphstream file parser (add the corresponding comments for the declared types below !)
	 */
	static private Map<String, Class<? extends FileSink>> typestr2fileSink = new HashMap<String, Class<? extends FileSink>>() {{
				
		put("dgs",	FileSinkDGS.class);
		//put("dot",	FileSinkDOT.class);
		put("gml",	FileSinkGML.class);
		//put("svg",	FileSinkSVG.class);
		put("tikz",	FileSinkTikZ.class);
		
	}};
	
	/**
	 * Returns the list of all the possible exportation formats 
	 * @return
	 */
	static public Collection<String> getAvailableExportationFormats() {
		return typestr2fileSink.keySet();
	}
	

	/**
	 * Saves a graph using Graphstream....
	 */
	public static void saveGraphWithGraphstreamToFile(
			final IScope scope,
			IGraph thegraph,
			GamaFile<?,?>  gamaFile,
			String outputFilename,
			String format
			) {
	
		Class<? extends FileSink> sinkClass = typestr2fileSink.get(format.toLowerCase());
		FileSink sink = null;
		try {
			sink = sinkClass.newInstance();
		} catch (InstantiationException e1) {
			new GamaRuntimeException(e1);
		} catch (IllegalAccessException e1) {
			new GamaRuntimeException(e1);
		}
		
		try {
			sink.writeAll(getGraphstreamGraphFromGamaGraph(thegraph), outputFilename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("error during the exportation of the graph to file "+outputFilename);
		}
		
			
	}

	
	public static Graph getGraphstreamGraphFromGamaGraph(IGraph gamaGraph) {
		
		Graph g = new DefaultGraph("tmpGraph");
		
		Map<Object,Node> gamaNode2graphStreamNode = new HashMap<Object, Node>(gamaGraph._internalNodesSet().size());
		
		// add nodes		
		for (Object v : gamaGraph._internalVertexMap().keySet() ) {
			_Vertex vertex = (_Vertex)gamaGraph._internalVertexMap().get(v);
				
			Node n = g.addNode(v.toString());
			gamaNode2graphStreamNode.put(
					v,
					n
					);
			
			if (v instanceof IAgent) {
				IAgent a = (IAgent)v;
				for (Object key : a.getAttributes().keySet()) {
					Object value = a.getAttributes().get(key);
					n.setAttribute(key.toString(), value.toString());
				}
			}

			if (v instanceof IShape) {
				IShape sh = (IShape)v;
		
				n.setAttribute("x", sh.getLocation().getX());
				n.setAttribute("y", sh.getLocation().getY());
				n.setAttribute("z", sh.getLocation().getZ());
			
			}
		
		}
		
		// add edges
		for (Object edgeObj : gamaGraph._internalEdgeMap().keySet() ) {
			_Edge edge = (_Edge)gamaGraph._internalEdgeMap().get(edgeObj);
			
			Edge e = g.addEdge(
					edgeObj.toString(), 
					gamaNode2graphStreamNode.get(edge.getSource()),
					gamaNode2graphStreamNode.get(edge.getTarget())
					);
			
			if (edgeObj instanceof IAgent) {
				IAgent a = (IAgent)edgeObj;
				for (Object key : a.getAttributes().keySet()) {
					Object value = a.getAttributes().get(key);
					e.setAttribute(key.toString(), value.toString());
				}
			}

			
		}

		// some basic tests for integrity
		if (
				gamaGraph.getVertices().size() != g.getNodeCount()
				||
				gamaGraph.getEdges().size() != g.getEdgeCount()
				) {
			throw new GamaRuntimeException(
					"The exportation ran without error, but integrity tests failed (graph size)", 
					true 
					); 
		}
		
		return g;
	}

	
}
