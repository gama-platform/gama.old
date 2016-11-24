/*********************************************************************************************
 *
 * 'PrefuseWriterAbstract.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.writer;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.*;
import prefuse.data.Graph;
import prefuse.data.io.*;

/**
 * @deprecated : other writers provide better support for many formats. Still kept in case of a failure found for other
 *             exporters.
 * @author Samuel Thiriot
 */
public abstract class PrefuseWriterAbstract implements IGraphWriter {

	private void write(final Graph prefuseGraph, final GraphWriter writer, final String filename) {

		try {
			writer.writeGraph(prefuseGraph, filename);
		} catch (DataIOException e) {
			throw GamaRuntimeException.error("error during the exportation of the graph with a prefuse exporter: " +
				e.getMessage());
		}

	}

	protected abstract GraphWriter getGraphWriter();

	@Override
	public void writeGraph(final IScope scope, final IGraph gamaGraph, final GamaFile gamaFile, final String filename) {

		write(GraphUtilsPrefuse.getPrefuseGraphFromGamaGraph(gamaGraph), getGraphWriter(), filename);

	}

}
