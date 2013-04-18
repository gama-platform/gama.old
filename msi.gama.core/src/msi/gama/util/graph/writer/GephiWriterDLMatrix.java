package msi.gama.util.graph.writer;

import org.gephi.io.exporter.plugin.ExporterDL;
import org.gephi.io.exporter.spi.GraphExporter;


public class GephiWriterDLMatrix extends GephiWriterAbstract {

	@Override
	protected void initializeExporter(GraphExporter exporter) {
		((ExporterDL)exporter).setUseMatrixFormat(true);
	}

	@Override
	protected String getFormat() {
		return "dl";
	}

	

}
