/*******************************************************************************************************
 *
 * msi.gama.util.graph.writer.GraphStreamWriterGML.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.writer;

import msi.gama.ext.graphstream.FileSink;
import msi.gama.ext.graphstream.FileSinkGraphML;

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
