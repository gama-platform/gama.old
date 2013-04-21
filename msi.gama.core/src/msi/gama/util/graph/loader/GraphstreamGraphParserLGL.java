package msi.gama.util.graph.loader;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceLGL;

public class GraphstreamGraphParserLGL extends GraphStreamGraphParserAbstract {

	@Override
	protected FileSource getFileSource() {
		return new FileSourceLGL();
	}

}
