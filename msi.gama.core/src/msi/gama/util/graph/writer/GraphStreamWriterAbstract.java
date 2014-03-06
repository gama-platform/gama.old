package msi.gama.util.graph.writer;

import java.io.IOException;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.*;
import org.graphstream.stream.file.FileSink;

/**
 * 
 * @author Samuel Thiriot
 */
public abstract class GraphStreamWriterAbstract implements IGraphWriter {

	/**
	 * Saves a graph using Graphstream....
	 */
	private void saveGraphWithGraphstreamToFile(final IScope scope, final IGraph thegraph, final GamaFile gamaFile,
		final String outputFilename, final FileSink sink) {

		try {
			sink.writeAll(GraphUtilsGraphStream.getGraphstreamGraphFromGamaGraph(thegraph), outputFilename);
		} catch (IOException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("error during the exportation of the graph to file " + outputFilename);
		}

	}

	public abstract FileSink getFileSink();

	@Override
	public void writeGraph(final IScope scope, final IGraph gamaGraph, final GamaFile gamaFile, final String filename) {
		saveGraphWithGraphstreamToFile(scope, gamaGraph, gamaFile, filename, getFileSink());
	}

}
