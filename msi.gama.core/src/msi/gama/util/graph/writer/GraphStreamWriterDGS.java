/*********************************************************************************************
 *
 * 'GraphStreamWriterDGS.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.writer;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;

/**
 * @see http://graphstream-project.org/doc/Advanced-Concepts/The-DGS-File-Format/
 * @author Samuel Thiriot
 *
 */
public class GraphStreamWriterDGS extends GraphStreamWriterAbstract {

	public static final String NAME = "dgs";
	
	@Override
	public FileSink getFileSink() {
		return new FileSinkDGS();
	}

}
