/*******************************************************************************************************
 *
 * msi.gama.util.graph.loader.AvailableGraphParsers.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.csv.CSVImporter;
import org.jgrapht.nio.dimacs.DIMACSImporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.gexf.SimpleGEXFImporter;
import org.jgrapht.nio.gml.GmlImporter;
import org.jgrapht.nio.graph6.Graph6Sparse6Importer;
import org.jgrapht.nio.graphml.GraphMLImporter;
import org.jgrapht.nio.json.JSONImporter;
import org.jgrapht.nio.tsplib.TSPLIBImporter;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Lists all the graph importer available.
 * If you add a parser, add it there.
 *
 * @author Patrick Taillandier
 *
 */
public class GraphImporters {

	private static final Map<String, Class<? extends GraphImporter>> name2parser =
		new HashMap<String, Class<? extends GraphImporter>>() {

			{
				// we store both the default version (ex. forcedirected is implemented by default by prefuse,
				// but also a prefixed version for disambiguation (like "prefuse.forcedirected")

				// default
				//put("csv", CSVImporter.class); 
				put("dimacs", DIMACSImporter.class);
				put("dot", DOTImporter.class); 
				put("gexf", SimpleGEXFImporter.class);
				put("graphml", GraphMLImporter.class);
				put("graph6", Graph6Sparse6Importer.class);
				put("gml", GmlImporter.class);
				//put("json", JSONImporter.class);
				put("tsplib", TSPLIBImporter.class);

			}
		};

	/**
	 * contains the name of parsers for automatic detection. Should only contain
	 * each parser one time. Also, should be provided in the relevant order (more frequent formats first).
	 */
		private static final List<String> parsersForAutomaticDetection = new LinkedList<String>() {

			{
				add("graphml");
				add("gexf");
				add("dimacs");

				add("graph6");
				add("gml");
				add("tsplib");
				
				add("json");

				add("csv");
				

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

	private static Map<String, GraphImporter> name2singleton = new HashMap<String, GraphImporter>();

	public static GraphImporter getGraphImporter(final String fileType) {
		GraphImporter res = name2singleton.get(fileType);

		if ( res == null ) {
			// no singleton created
			Class<? extends GraphImporter> clazz = name2parser.get(fileType);
			if ( clazz == null ) { throw GamaRuntimeException.error(
				"unknown parser name: " + fileType + "; please choose one of " + getAvailableLoaders().toString(),
				GAMA.getRuntimeScope()); }
			Constructor<?> ctor;
			try {
				ctor = clazz.getConstructor();
				res = (GraphImporter) ctor.newInstance();
				name2singleton.put(fileType, res);
				return res;
			} catch (NoSuchMethodException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			} catch (SecurityException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			} catch (IllegalArgumentException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			} catch (InvocationTargetException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			} catch (InstantiationException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			} catch (IllegalAccessException e) {
				throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
			}
		}
		return res;
	}

}
