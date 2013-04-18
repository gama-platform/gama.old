package msi.gama.util.graph.writer;

import prefuse.data.io.GraphMLWriter;
import prefuse.data.io.GraphWriter;

public class PrefuseWriterGraphML extends PrefuseWriterAbstract {

	@Override
	protected GraphWriter getGraphWriter() {
		return new GraphMLWriter();
	}

}
