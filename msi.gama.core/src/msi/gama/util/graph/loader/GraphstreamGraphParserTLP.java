/*******************************************************************************************************
 *
 * msi.gama.util.graph.loader.GraphstreamGraphParserTLP.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.loader;

import msi.gama.ext.graphstream.FileSource;
import msi.gama.ext.graphstream.FileSourceTLP;

public class GraphstreamGraphParserTLP extends GraphStreamGraphParserAbstract {

	@Override
	protected FileSource getFileSource() {
		return new FileSourceTLP();
	}

}
