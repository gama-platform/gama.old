/*********************************************************************************************
 *
 * 'GraphStreamWriterTikz.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.writer;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkTikZ;

/**
 * 
 * @author Samuel Thiriot
 */
public class GraphStreamWriterTikz extends GraphStreamWriterAbstract {

	public static final String NAME = "tikz";
	
	@Override
	public FileSink getFileSink() {
		return new FileSinkTikZ();
	}

}
