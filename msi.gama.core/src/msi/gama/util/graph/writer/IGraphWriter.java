package msi.gama.util.graph.writer;

import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.IGraph;

/**
 * Represents a graph writer, independantly of its implements; it is able to write
 * a gama graph to a file.
 * 
 * @author Samuel Thiriot
 */
public interface IGraphWriter {

	public void writeGraph(final IScope scope, IGraph gamaGraph, GamaFile gamaFile, String filename);

}
