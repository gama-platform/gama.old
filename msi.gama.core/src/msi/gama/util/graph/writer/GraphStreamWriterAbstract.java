/*******************************************************************************************************
 *
 * msi.gama.util.graph.writer.GraphStreamWriterAbstract.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.writer;

import java.io.IOException;

import org.graphstream.stream.file.FileSink;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.GraphUtilsGraphStream;
import msi.gama.util.graph.IGraph;

/**
 * 
 * @author Samuel Thiriot
 */
@SuppressWarnings ({ "rawtypes" })
public abstract class GraphStreamWriterAbstract implements IGraphWriter {

	/**
	 * Saves a graph using Graphstream....
	 */
	private void saveGraphWithGraphstreamToFile(final IScope scope, final IGraph thegraph, final GamaFile gamaFile,
			final String outputFilename, final FileSink sink) {

		try {
			sink.writeAll(GraphUtilsGraphStream.getGraphstreamGraphFromGamaGraph(thegraph), outputFilename);
		} catch (final IOException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("error during the exportation of the graph to file " + outputFilename,
					scope);
		}

	}

	public abstract FileSink getFileSink();

	@Override
	public void writeGraph(final IScope scope, final IGraph gamaGraph, final GamaFile gamaFile, final String filename) {
		saveGraphWithGraphstreamToFile(scope, gamaGraph, gamaFile, filename, getFileSink());
	}

}
