/*******************************************************************************************************
 *
 * msi.gama.util.graph.loader.GraphstreamGraphParserNCOL.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.loader;

import msi.gama.ext.graphstream.FileSource;
import msi.gama.ext.graphstream.FileSourceNCol;

public class GraphstreamGraphParserNCOL extends GraphStreamGraphParserAbstract {

	@Override
	protected FileSource getFileSource() {
		return new FileSourceNCol();
	}

}
