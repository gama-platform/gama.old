/*******************************************************************************************************
 *
 * GraphExporters.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.writer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.dimacs.DIMACSExporter;
import org.jgrapht.nio.dimacs.DIMACSImporter;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.gexf.GEXFExporter;
import org.jgrapht.nio.gexf.SimpleGEXFImporter;
import org.jgrapht.nio.gml.GmlExporter;
import org.jgrapht.nio.gml.GmlImporter;
import org.jgrapht.nio.graph6.Graph6Sparse6Exporter;
import org.jgrapht.nio.graph6.Graph6Sparse6Importer;
import org.jgrapht.nio.graphml.GraphMLExporter;
import org.jgrapht.nio.graphml.GraphMLImporter;
import org.jgrapht.nio.tsplib.TSPLIBImporter;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Lists available graphs writers, independently of the underlying library.
 *
 * @author Patrick Taillandier
 *
 */
public class GraphExporters {

	/** The Constant name2writer. */
	private static final Map<String, Class<? extends GraphExporter>> name2writer =
			new HashMap<String, Class<? extends GraphExporter>>() {

				{
					put("dimacs", DIMACSExporter.class);
					put("dot", DOTExporter.class); 
					put("gexf", GEXFExporter.class);
					put("graphml", GraphMLExporter.class);
					put("graph6", Graph6Sparse6Exporter.class);
					put("gml", GmlExporter.class);
				}
			};

	/**
	 * Gets the available writers.
	 *
	 * @return the available writers
	 */
	public static Set<String> getAvailableWriters() {
		return name2writer.keySet();
	}

	/** The name 2 singleton. */
	private static Map<String, GraphExporter> name2singleton = new HashMap<>();

	/**
	 * Gets the graph writer.
	 *
	 * @param name the name
	 * @return the graph writer
	 */
	public static GraphExporter getGraphWriter(final String name) {
		GraphExporter res = name2singleton.get(name);
		
		if ( res == null ) {
			// no singleton created
			Class<? extends GraphExporter> clazz = name2writer.get(name);
			if ( clazz == null ) { throw GamaRuntimeException.error(
				"unknown exporter name: " + name + "; please choose one of " + getAvailableWriters().toString(),
				GAMA.getRuntimeScope()); }
			Constructor<?> ctor;
			try {
				ctor = clazz.getConstructor();
				res = (GraphExporter) ctor.newInstance();
				name2singleton.put(name, res);
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
