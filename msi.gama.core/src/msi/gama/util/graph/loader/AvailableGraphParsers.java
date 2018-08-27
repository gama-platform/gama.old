/*******************************************************************************************************
 *
 * msi.gama.util.graph.loader.AvailableGraphParsers.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.loader;

import java.util.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Lists all the layouts available, independently of the underlying libraries.
 * If you add a parser, add it there.
 *
 * @author Samuel Thiriot
 *
 */
public class AvailableGraphParsers {

	private static final Map<String, Class<? extends IGraphParser>> name2parser =
		new HashMap<String, Class<? extends IGraphParser>>() {

			{
				// we store both the default version (ex. forcedirected is implemented by default by prefuse,
				// but also a prefixed version for disambiguation (like "prefuse.forcedirected")

				// default
				put("pajek", GraphstreamGraphParserPajek.class);
				put("net", GraphstreamGraphParserPajek.class);
				put("lgl", GraphstreamGraphParserLGL.class);
				put("dot", GraphstreamGraphParserDOT.class);
				put("gexf", GraphstreamGraphParserGEXF.class);
				put("graphml", GraphstreamGraphParserGraphML.class);
				put("gml", GraphstreamGraphParserGraphML.class);

				put("tlp", GraphstreamGraphParserTLP.class);
				put("tulip", GraphstreamGraphParserTLP.class);
				put("ncol", GraphstreamGraphParserNCOL.class);
				put("edge", GraphstreamGraphParserEdge.class);
				put("dgs", GraphstreamGraphParserDGS.class);

				// graphstream
				put("graphstream.pajek", GraphstreamGraphParserPajek.class);
				put("graphstream.net", GraphstreamGraphParserPajek.class);
				put("graphstream.lgl", GraphstreamGraphParserLGL.class);
				put("graphstream.dot", GraphstreamGraphParserDOT.class);
				put("graphstream.gexf", GraphstreamGraphParserGEXF.class);
				put("graphstream.graphml", GraphstreamGraphParserGraphML.class);
				put("graphstream.gml", GraphstreamGraphParserGraphML.class);
				put("graphstream.tlp", GraphstreamGraphParserTLP.class);
				put("graphstream.tulip", GraphstreamGraphParserTLP.class);
				put("graphstream.ncol", GraphstreamGraphParserNCOL.class);
				put("graphstream.edge", GraphstreamGraphParserEdge.class);
				put("graphstream.dgs", GraphstreamGraphParserDGS.class);

			}
		};

	/**
	 * contains the name of parsers for automatic detection. Should only contain
	 * each parser one time. Also, should be provided in the relevant order (more frequent formats first).
	 */
		private static final List<String> parsersForAutomaticDetection = new LinkedList<String>() {

			{
				add("pajek");
				add("graphml");
				add("gexf");
				add("edge");

				add("ncol");
				add("dot");
				add("dgs");

				add("lgl"); // too tolerant => end of the list

				add("tulip"); // creates problems? => end of the list

			}
		};

	/**
	 * Returns the list of all the names of loader declared.
	 * Typically required for interaction with users (like propose
	 * the list of all the possible loader, or search something passed
	 * by the user).
	 * @return
	 */
		public static Set<String> getAvailableLoaders() {
		return name2parser.keySet();
	}

	/**
	 * Returns a list of loaders declared, by ensuring that each loader is provided once
	 * only. So all the redondancies are removed.
	 * Typically used for automatic type detection.
	 * @return
	 */
	public static List<String> getLoadersForAutoDetection() {
		return parsersForAutomaticDetection;
	}

	private static Map<String, IGraphParser> name2singleton = new HashMap<String, IGraphParser>();

	public static IGraphParser getLoader(final String name) {
		IGraphParser res = name2singleton.get(name);

		if ( res == null ) {
			// no singleton created
			Class<? extends IGraphParser> classLayout = name2parser.get(name);
			if ( classLayout == null ) { throw GamaRuntimeException.error(
				"unknown parser name: " + name + "; please choose one of " + getAvailableLoaders().toString(),
				GAMA.getRuntimeScope()); }
			try {
				res = classLayout.newInstance();
			} catch (InstantiationException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			} catch (IllegalAccessException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			}
			name2singleton.put(name, res);
		}

		return res;
	}

}
