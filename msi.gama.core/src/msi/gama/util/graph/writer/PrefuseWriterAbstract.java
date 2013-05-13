package msi.gama.util.graph.writer;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.GraphUtilsPrefuse;
import msi.gama.util.graph.IGraph;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphWriter;

/**
 * @deprecated : other writers provide better support for many formats. Still kept in case of a failure found for other exporters.
 * @author Samuel Thiriot
 */
public abstract class PrefuseWriterAbstract implements IGraphWriter {

	
	private void write(Graph prefuseGraph, GraphWriter writer, String filename) {
		
		try {
			writer.writeGraph(prefuseGraph, filename);
		} catch (DataIOException e) {
			throw GamaRuntimeException.error("error during the exportation of the graph with a prefuse exporter: "+e.getMessage());
		}
		
	}
	
	protected abstract GraphWriter getGraphWriter();
	
	@Override
	public void writeGraph(IScope scope, IGraph<?, ?> gamaGraph, GamaFile<?, ?> gamaFile, String filename) {
	
		write(
				GraphUtilsPrefuse.getPrefuseGraphFromGamaGraph(gamaGraph),
				getGraphWriter(),
				filename
				);
		
	}

}
