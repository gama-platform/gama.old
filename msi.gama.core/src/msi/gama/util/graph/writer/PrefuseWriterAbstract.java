/*******************************************************************************************************
 *
 * msi.gama.util.graph.writer.PrefuseWriterAbstract.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.writer;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.graph.GraphUtilsPrefuse;
import msi.gama.util.graph.IGraph;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphWriter;

/**
 * @deprecated : other writers provide better support for many formats. Still kept in case of a failure found for other
 *             exporters.
 * @author Samuel Thiriot
 */
@Deprecated
public abstract class PrefuseWriterAbstract implements IGraphWriter {

	private void write(final IScope scope, final Graph prefuseGraph, final GraphWriter writer, final String filename) {

		try {
			writer.writeGraph(prefuseGraph, filename);
		} catch (final DataIOException e) {
			throw GamaRuntimeException.error(
					"error during the exportation of the graph with a prefuse exporter: " + e.getMessage(), scope);
		}

	}

	protected abstract GraphWriter getGraphWriter();

	@Override
	public void writeGraph(final IScope scope, final IGraph gamaGraph, final GamaFile gamaFile, final String filename) {

		write(scope, GraphUtilsPrefuse.getPrefuseGraphFromGamaGraph(scope, gamaGraph), getGraphWriter(), filename);

	}

}
