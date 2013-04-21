package msi.gama.util.graph.loader;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceNCol;

public class GraphstreamGraphParserNCOL extends GraphStreamGraphParserAbstract {

	@Override
	protected FileSource getFileSource() {
		return new FileSourceNCol();
	}

}
