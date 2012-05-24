/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.precompiler;

import java.io.*;
import java.util.*;

/**
 * Written by drogoul Modified on 27 juil. 2010
 * 
 * @todo Description
 * 
 */
public class GamlProperties {

	Map<String, LinkedHashSet<String>> map;

	public final static String SKILLS = "skills";
	public final static String OPERATORS = "operators";
	public final static String GAML = "gaml.properties";
	public final static String SYMBOLS = "symbols";
	public final static String FACTORIES = "factories";
	public final static String SPECIES = "species";
	public final static String SEPARATOR = "¤";

	static final String NULL = "";

	public GamlProperties() {
		map = new LinkedHashMap();
	}

	public GamlProperties(final Reader r) {
		this();
		load(r);
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public LinkedHashSet<String> get(final String key) {
		return map.get(key);
	}

	public String getFirst(final String key) {
		Set<String> result = get(key);
		if ( result == null ) { return null; }
		for ( Iterator<String> it = result.iterator(); it.hasNext(); ) {
			return it.next();
		}
		return null;
	}

	public void remove(final String key) {
		map.remove(key);
	}

	public void put(final String key, final String value) {
		if ( !map.containsKey(key) ) {
			map.put(key, new LinkedHashSet<String>());
		}
		map.get(key).add(value);
	}

	void put(final String key, final LinkedHashSet<String> values) {
		if ( !map.containsKey(key) ) {
			map.put(key, values);
		} else {
			map.get(key).addAll(values);
		}
	}

	public void putAll(final GamlProperties m) {
		for ( Iterator<Map.Entry<String, LinkedHashSet<String>>> it = m.map.entrySet().iterator(); it
			.hasNext(); ) {
			Map.Entry<String, LinkedHashSet<String>> entry = it.next();
			put(entry.getKey(), (LinkedHashSet<String>) entry.getValue().clone());
		}
	}

	public void store(final Writer writer) {
		Properties prop = new Properties();
		for ( Iterator<Map.Entry<String, LinkedHashSet<String>>> it = map.entrySet().iterator(); it
			.hasNext(); ) {
			Map.Entry<String, LinkedHashSet<String>> entry = it.next();
			prop.setProperty(entry.getKey(), toString(entry.getValue()));
		}
		try {
			prop.store(writer, NULL);
			writer.flush();
			writer.close();
		} catch (IOException e) {}

	}

	public static String toString(final Set<String> strings) {
		if ( !strings.isEmpty() ) {
			StringBuilder sb = new StringBuilder();
			for ( String value : strings ) {
				sb.append(value).append(SEPARATOR);
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
		return NULL;

	}

	public Map<String, String> filterFirst(final String c) {
		Map<String, String> gp = new LinkedHashMap();
		for ( String original : map.keySet() ) {
			if ( c.charAt(0) == original.charAt(0) ) {
				String key = original.substring(1);
				gp.put(key, getFirst(original));
			}
		}
		return gp;
	}

	public Map<String, Set<String>> filterAll(final String c) {
		Map<String, Set<String>> gp = new LinkedHashMap();
		for ( String original : map.keySet() ) {
			if ( c.charAt(0) == original.charAt(0) ) {
				String key = original.substring(1);
				gp.put(key, get(original));
			}
		}
		return gp;
	}

	public GamlProperties load(final Reader reader) {
		Properties prop = new Properties();
		try {
			prop.load(reader);
		} catch (IOException e) {
			try {
				reader.close();
			} catch (IOException e1) {}
			return null;
		}
		for ( String s : prop.stringPropertyNames() ) {
			String[] array = prop.getProperty(s, "").split(SEPARATOR);
			LinkedHashSet<String> values = new LinkedHashSet(Arrays.asList(array));
			put(s, values);
		}
		try {
			reader.close();
		} catch (IOException e) {}
		return this;
	}

}
