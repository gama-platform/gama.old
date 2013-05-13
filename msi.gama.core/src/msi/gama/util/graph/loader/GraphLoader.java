package msi.gama.util.graph.loader;

import java.io.File;
import java.util.Map;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.species.ISpecies;

/**
 * Entry point for graphs loading.
 * Used to load a graph for a given format (or even automatic detection of the format).
 * 
 * @author Samuel Thiriot
 * 
 */
public class GraphLoader {

	protected static GamaGraph loadAGraph(IScope scope, ISpecies nodeSpecies, ISpecies edgeSpecies,
		Map<String, String> nodeGraphAttribute2AgentAttribute, Map<String, String> edgeGraphAttribute2AgentAttribute,
		IGraphParser parser, String filename) {

		// locate the file
		File f = null;
		if ( scope != null ) {
			f = new File(scope.getSimulationScope().getModel().getRelativeFilePath(filename, true));
		} else {
			f = new File(filename);
		}

		if ( !f.exists() ) { throw GamaRuntimeException.error("unable to open this file, which does not exists: " +
			filename); }
		if ( !f.canRead() ) { throw GamaRuntimeException.error("unable to open this file, which is not readable: " +
			filename); }
		if ( !f.isFile() ) { throw GamaRuntimeException.error("this is not a file (probably a directory): " + filename); }

		// this listener will receive events
		GamaGraphParserListener list =
			new GamaGraphParserListener(scope, nodeSpecies, edgeSpecies, nodeGraphAttribute2AgentAttribute,
				edgeGraphAttribute2AgentAttribute);

		// make the parser parse, so it raises events
		try {
			parser.parseFile(list, f.getAbsolutePath());
		} catch (Throwable t) {
			throw GamaRuntimeException.create(t);
		}

		// and return the corresponding result !
		return list.getGraph();
	}

	public static GamaGraph loadGraph(IScope scope, String filename, ISpecies nodeSpecies, ISpecies edgeSpecies,
		Map<String, String> nodeGraphAttribute2AgentAttribute, Map<String, String> edgeGraphAttribute2AgentAttribute,
		String format) {

		// if format is provided, attempt to load using only this format
		if ( format != null ) { return loadAGraph(scope, nodeSpecies, edgeSpecies, nodeGraphAttribute2AgentAttribute,
			edgeGraphAttribute2AgentAttribute, AvailableGraphParsers.getLoader(format), filename); }

		// else,

		// if format is not provided, read the extension, and test these extensions first
		String extension = null;
		{
			int i = filename.lastIndexOf('.');
			if ( i > 0 ) {
				extension = filename.substring(i + 1).trim().toLowerCase();
			}
		}
		if ( extension != null ) {
			// extension available: attempt to open the graph with this extension
			if ( AvailableGraphParsers.getAvailableLoaders().contains(extension) ) {
				// fine, found this extension !
				// opening with the default loader for this extension
				try {
					GamaGraph res =
						loadAGraph(scope, nodeSpecies, edgeSpecies, nodeGraphAttribute2AgentAttribute,
							edgeGraphAttribute2AgentAttribute, AvailableGraphParsers.getLoader(extension), filename);
					GAMA.reportError(GamaRuntimeException.error("Automatically detected the type of this graph from file extension ('" + extension +
						"'). Hope this was the relevant type ?"));
					return res;
				} catch (GamaRuntimeException e) {
					throw GamaRuntimeException.error("attempted to detect the type of this graph from file extension ('" +
						extension + "'), but the parsing failed.");
				}
			}
		}

		// if nothing worked, attempt to load this using all the parsers in order.
		for ( String loaderName : AvailableGraphParsers.getLoadersForAutoDetection() ) {
			try {
				GamaGraph res =
					loadAGraph(scope, nodeSpecies, edgeSpecies, nodeGraphAttribute2AgentAttribute,
						edgeGraphAttribute2AgentAttribute, AvailableGraphParsers.getLoader(loaderName), filename);
				GAMA.reportError(GamaRuntimeException.error("Automatically detected the type of this graph :'" +
					loaderName + "'; loaded " + res.vertexSet().size() + " vertices and " + res.edgeSet().size() +
					" edges. Hope this was the relevant type ?"));
				return res;
			} catch (GamaRuntimeException e) {
				// don't display errors here (auto detection) GAMA.reportError(new
				// GamaRuntimeException("attempted to open file with parser '"+loaderName+"'. This failed :-("));

			}
		}

		// raise an error !
		throw GamaRuntimeException.error("attempted to detect the type of this graph automatically; no type detected among the supported parsers: " +
			AvailableGraphParsers.getLoadersForAutoDetection());

	}
}
