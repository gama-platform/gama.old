package msi.gama.util.graph.writer;

import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.IGraph;

public interface IGraphWriter {

	public void writeGraph(final IScope scope, IGraph<?, ?> gamaGraph, GamaFile<?,?>  gamaFile, String filename);
	
}
