package msi.gama.util.graph.writer;

import prefuse.data.io.GraphMLWriter;
import prefuse.data.io.GraphWriter;

/**
 * @deprecated : other writers provide better support. Still kept in case of a failure found for other exporters.
 * @author Samuel Thiriot
 */
public class PrefuseWriterGraphML extends PrefuseWriterAbstract {

	@Override
	protected GraphWriter getGraphWriter() {
		return new GraphMLWriter();
	}

}
