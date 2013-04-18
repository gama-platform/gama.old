package msi.gama.util.graph.writer;

import java.io.IOException;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.GraphUtilsGraphStream;
import msi.gama.util.graph.IGraph;

import org.graphstream.stream.file.FileSink;

public abstract class GraphStreamWriterAbstract implements IGraphWriter {

	/**
	 * Saves a graph using Graphstream....
	 */
	private void saveGraphWithGraphstreamToFile(
			final IScope scope,
			IGraph thegraph,
			GamaFile<?,?>  gamaFile,
			String outputFilename,
			FileSink sink
			) {
	
		try {
			sink.writeAll(GraphUtilsGraphStream.getGraphstreamGraphFromGamaGraph(thegraph), outputFilename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("error during the exportation of the graph to file "+outputFilename);
		}
		
			
	}
	
	public abstract FileSink getFileSink();

	@Override
	public void writeGraph(final IScope scope, IGraph<?, ?> gamaGraph, GamaFile<?,?>  gamaFile, String filename) {
		saveGraphWithGraphstreamToFile(scope, gamaGraph, gamaFile, filename, getFileSink());
	}

}
