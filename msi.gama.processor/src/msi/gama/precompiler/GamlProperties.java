/*********************************************************************************************
 *
 *
 * 'GamlProperties.java', in plugin 'msi.gama.processor', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
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
	public final static String ACTIONS = "actions";
	public final static String ATTRIBUTES = "attributes";
	public final static String STATEMENTS = "statements";
	public final static String OPERATORS = "operators";
	public final static String GAML = "gaml.properties";
	public final static String FACTORIES = "factories";
	public final static String SPECIES = "species";
	public final static String CONSTANTS = "constants";
	public final static String ARCHITECTURES = "architectures";
	public final static String TYPES = "types";
	public final static String PLUGINS = "plugins";
	public final static String SEPARATOR = JavaWriter.DOC_SEP;

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
		if ( value != null ) {
			map.get(key).add(value);
		}
	}

	public void put(final String key, final Set<String> values) {
		if ( !map.containsKey(key) ) {
			map.put(key, new LinkedHashSet(values));
		} else {
			map.get(key).addAll(values);
		}
	}

	public void putAll(final GamlProperties m) {
		for ( Iterator<Map.Entry<String, LinkedHashSet<String>>> it = m.map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, LinkedHashSet<String>> entry = it.next();
			put(entry.getKey(), (LinkedHashSet<String>) entry.getValue().clone());
		}
	}

	public void store(final Writer writer) {
		Properties prop = new Properties();
		for ( Iterator<Map.Entry<String, LinkedHashSet<String>>> it = map.entrySet().iterator(); it.hasNext(); ) {
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

	/**
	 *
	 */
	public void clear() {
		map.clear();
	}

}
