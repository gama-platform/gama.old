/*******************************************************************************************************
 *
 * msi.gama.util.graph.writer.IGraphWriter.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.writer;

import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.IGraph;

/**
 * Represents a graph writer, independantly of its implements; it is able to
 * write a gama graph to a file.
 * 
 * @author Samuel Thiriot
 */
@SuppressWarnings({ "rawtypes" })
public interface IGraphWriter {

	public void writeGraph(final IScope scope, IGraph gamaGraph, GamaFile gamaFile, String filename);

}
