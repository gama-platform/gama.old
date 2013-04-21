package msi.gama.util.graph.loader;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourcePajek;

public class GraphstreamGraphParserPajek extends GraphStreamGraphParserAbstract {

	@Override
	protected FileSource getFileSource() {
		return new FileSourcePajek();
	}

}
