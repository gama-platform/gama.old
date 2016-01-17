/*********************************************************************************************
 *
 *
 * 'GraphLoader.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.graph.loader;

import java.io.File;
import java.util.Map;
import msi.gama.common.util.FileUtils;
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

	protected static GamaGraph loadAGraph(final IScope scope, final ISpecies nodeSpecies, final ISpecies edgeSpecies,
		final Map<String, String> nodeGraphAttribute2AgentAttribute,
		final Map<String, String> edgeGraphAttribute2AgentAttribute, final IGraphParser parser, final String filename,
		final boolean spatial) {

		// locate the file
		File f = null;
		if ( scope != null ) {
			f = new File(FileUtils.constructAbsoluteFilePath(scope, filename, true));
		} else {
			f = new File(filename);
		}

		if ( !f.exists() ) { throw GamaRuntimeException
			.error("unable to open this file, which does not exists: " + filename, scope); }
		if ( !f.canRead() ) { throw GamaRuntimeException
			.error("unable to open this file, which is not readable: " + filename, scope); }
		if ( !f.isFile() ) { throw GamaRuntimeException.error("this is not a file (probably a directory): " + filename,
			scope); }

		// this listener will receive events
		GamaGraphParserListener list = new GamaGraphParserListener(scope, nodeSpecies, edgeSpecies,
			nodeGraphAttribute2AgentAttribute, edgeGraphAttribute2AgentAttribute, spatial);

		// make the parser parse, so it raises events
		try {
			parser.parseFile(list, f.getAbsolutePath());
		} catch (Throwable t) {
			throw GamaRuntimeException.create(t, scope);
		}

		// and return the corresponding result !
		return list.getGraph();
	}

	public static GamaGraph loadGraph(final IScope scope, final String filename, final ISpecies nodeSpecies,
		final ISpecies edgeSpecies, final Map<String, String> nodeGraphAttribute2AgentAttribute,
		final Map<String, String> edgeGraphAttribute2AgentAttribute, final String format, final boolean spatial) {

		// if format is provided, attempt to load using only this format
		if ( format != null ) { return loadAGraph(scope, nodeSpecies, edgeSpecies, nodeGraphAttribute2AgentAttribute,
			edgeGraphAttribute2AgentAttribute, AvailableGraphParsers.getLoader(format), filename, spatial); }

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
					GamaGraph res = loadAGraph(scope, nodeSpecies, edgeSpecies, nodeGraphAttribute2AgentAttribute,
						edgeGraphAttribute2AgentAttribute, AvailableGraphParsers.getLoader(extension), filename,
						spatial);
					/*
					 * GAMA.reportError(GamaRuntimeException
					 * .warning("Automatically detected the type of this graph from file extension ('" + extension +
					 * "'). Hope this was the relevant type ?"), false);
					 */
					return res;
				} catch (GamaRuntimeException e) {
					e.addContext("attempted to detect the type of this graph from file extension ('" + extension +
						"'), but the parsing failed.");
					throw e;
				}
			}
		}

		// if nothing worked, attempt to load this using all the parsers in order.
		for ( String loaderName : AvailableGraphParsers.getLoadersForAutoDetection() ) {
			try {
				GamaGraph res = loadAGraph(scope, nodeSpecies, edgeSpecies, nodeGraphAttribute2AgentAttribute,
					edgeGraphAttribute2AgentAttribute, AvailableGraphParsers.getLoader(loaderName), filename, spatial);
				GAMA.reportError(scope,
					GamaRuntimeException.warning("Automatically detected the type of this graph :'" + loaderName +
						"'; loaded " + res.vertexSet().size() + " vertices and " + res.edgeSet().size() +
						" edges. Hope this was the relevant type ?", scope),
					false);
				return res;
			} catch (GamaRuntimeException e) {
				// don't display errors here (auto detection) GAMA.reportError(new
				// GamaRuntimeException("attempted to open file with parser '"+loaderName+"'. This failed :-("));

			}
		}

		// raise an error !
		throw GamaRuntimeException.error(
			"attempted to detect the type of this graph automatically; no type detected among the supported parsers: " +
				AvailableGraphParsers.getLoadersForAutoDetection(),
			scope);

	}
}
