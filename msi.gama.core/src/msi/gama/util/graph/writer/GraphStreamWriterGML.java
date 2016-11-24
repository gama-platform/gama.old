/*********************************************************************************************
 *
 * 'GraphStreamWriterGML.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.writer;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkGraphML;

/**
 * 
 * @author Samuel Thiriot
 */
public class GraphStreamWriterGML extends GraphStreamWriterAbstract {

	public static final String NAME = "gml";
	
	@Override
	public FileSink getFileSink() {
		return new FileSinkGraphML();
	}

}
