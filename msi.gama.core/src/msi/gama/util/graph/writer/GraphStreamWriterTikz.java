package msi.gama.util.graph.writer;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkTikZ;

public class GraphStreamWriterTikz extends GraphStreamWriterAbstract {

	public static final String NAME = "tikz";
	
	@Override
	public FileSink getFileSink() {
		return new FileSinkTikZ();
	}

}
