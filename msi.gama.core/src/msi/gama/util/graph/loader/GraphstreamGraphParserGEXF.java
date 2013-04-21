package msi.gama.util.graph.loader;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGEXF;

public class GraphstreamGraphParserGEXF extends GraphStreamGraphParserAbstract {

	@Override
	protected FileSource getFileSource() {
		return new FileSourceGEXF();
	}

}
