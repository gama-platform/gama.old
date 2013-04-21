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
