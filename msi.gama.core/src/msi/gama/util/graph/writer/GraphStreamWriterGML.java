/*******************************************************************************************************
 *
 * msi.gama.util.graph.writer.GraphStreamWriterGML.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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
