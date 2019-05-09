/*******************************************************************************************************
 *
 * msi.gama.util.graph.writer.PrefuseWriterGraphML.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package prefuse.writer;

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
