/*********************************************************************************************
 * 
 *
 * 'IGraphWriter.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
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
