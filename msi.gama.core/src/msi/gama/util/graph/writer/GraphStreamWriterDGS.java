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
