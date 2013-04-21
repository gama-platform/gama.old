package msi.gama.util.graph.loader;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;

public class GraphstreamGraphParserDGS extends GraphStreamGraphParserAbstract {

	@Override
	protected FileSource getFileSource() {
		return new FileSourceDGS();
	}

}
