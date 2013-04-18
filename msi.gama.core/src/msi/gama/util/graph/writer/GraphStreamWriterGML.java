package msi.gama.util.graph.writer;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkGraphML;

public class GraphStreamWriterGML extends GraphStreamWriterAbstract {

	public static final String NAME = "gml";
	
	@Override
	public FileSink getFileSink() {
		return new FileSinkGraphML();
	}

}
