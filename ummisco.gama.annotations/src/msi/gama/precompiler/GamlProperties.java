/*******************************************************************************************************
 *
 * GamlProperties.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Written by drogoul Modified on 27 juil. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlProperties {

	/** The map. */
	Map<String, LinkedHashSet<String>> map;

	/** The Constant SKILLS. */
	public final static String SKILLS = "skills";
	
	/** The Constant ACTIONS. */
	public final static String ACTIONS = "actions";
	
	/** The Constant ATTRIBUTES. */
	public final static String ATTRIBUTES = "attributes";
	
	/** The Constant STATEMENTS. */
	public final static String STATEMENTS = "statements";
	
	/** The Constant OPERATORS. */
	public final static String OPERATORS = "operators";
	
	/** The Constant GAML. */
	public final static String GAML = "gaml.properties";
	
	/** The Constant SPECIES. */
	public final static String SPECIES = "species";
	
	/** The Constant CONSTANTS. */
	public final static String CONSTANTS = "constants";
	
	/** The Constant ARCHITECTURES. */
	public final static String ARCHITECTURES = "architectures";
	
	/** The Constant TYPES. */
	public final static String TYPES = "types";
	
	/** The Constant PLUGINS. */
	public final static String PLUGINS = "plugins";
	
	/** The Constant SEPARATOR. */
	public final static String SEPARATOR = "~";

	/** The Constant NULL. */
	static final String NULL = "";

	/**
	 * Instantiates a new gaml properties.
	 */
	public GamlProperties() {
		map = new LinkedHashMap();
	}

	/**
	 * Instantiates a new gaml properties.
	 *
	 * @param r the r
	 */
	public GamlProperties(final Reader r) {
		this();
		load(r);
	}

	/**
	 * Key set.
	 *
	 * @return the sets the
	 */
	public Set<String> keySet() {
		return map.keySet();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the linked hash set
	 */
	public LinkedHashSet<String> get(final String key) {
		return map.get(key);
	}

	/**
	 * Gets the first.
	 *
	 * @param key the key
	 * @return the first
	 */
	public String getFirst(final String key) {
		final Set<String> result = get(key);
		if (result == null) { return null; }
		for (final Iterator<String> it = result.iterator(); it.hasNext();) {
			return it.next();
		}
		return null;
	}

	/**
	 * Removes the.
	 *
	 * @param key the key
	 */
	public void remove(final String key) {
		map.remove(key);
	}

	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void put(final String key, final String value) {
		if (!map.containsKey(key)) {
			map.put(key, new LinkedHashSet<String>());
		}
		if (value != null) {
			map.get(key).add(value);
		}
	}

	/**
	 * Put.
	 *
	 * @param key the key
	 * @param values the values
	 */
	public void put(final String key, final Iterable<String> values) {
		if (!map.containsKey(key))
			map.put(key, new LinkedHashSet());
		for (final String s : values) {
			map.get(key).add(s);
		}
	}

	/**
	 * Put all.
	 *
	 * @param m the m
	 */
	public void putAll(final GamlProperties m) {
		for (final Iterator<Map.Entry<String, LinkedHashSet<String>>> it = m.map.entrySet().iterator(); it.hasNext();) {
			final Map.Entry<String, LinkedHashSet<String>> entry = it.next();
			put(entry.getKey(), (LinkedHashSet<String>) entry.getValue().clone());
		}
	}

	/**
	 * Store.
	 *
	 * @param writer the writer
	 */
	public void store(final Writer writer) {
		final Properties prop = new Properties();
		for (final Iterator<Map.Entry<String, LinkedHashSet<String>>> it = map.entrySet().iterator(); it.hasNext();) {
			final Map.Entry<String, LinkedHashSet<String>> entry = it.next();
			prop.setProperty(entry.getKey(), toString(entry.getValue()));
		}
		try {
			prop.store(writer, NULL);
			writer.flush();
			writer.close();
		} catch (final IOException e) {
			throw new java.io.IOError(e);
		}

	}

	/**
	 * To string.
	 *
	 * @param strings the strings
	 * @return the string
	 */
	public static String toString(final Set<String> strings) {
		if (!strings.isEmpty()) {
			final StringBuilder sb = new StringBuilder();
			for (final String value : strings) {
				sb.append(value).append(SEPARATOR);
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
		return NULL;

	}

	/**
	 * Filter first.
	 *
	 * @param c the c
	 * @return the map
	 */
	public Map<String, String> filterFirst(final String c) {
		final Map<String, String> gp = new LinkedHashMap();
		for (final String original : map.keySet()) {
			if (c.charAt(0) == original.charAt(0)) {
				final String key = original.substring(1);
				gp.put(key, getFirst(original));
			}
		}
		return gp;
	}

	/**
	 * Filter all.
	 *
	 * @param c the c
	 * @return the map
	 */
	public Map<String, Set<String>> filterAll(final String c) {
		final Map<String, Set<String>> gp = new LinkedHashMap();
		for (final String original : map.keySet()) {
			if (c.charAt(0) == original.charAt(0)) {
				final String key = original.substring(1);
				gp.put(key, get(original));
			}
		}
		return gp;
	}

	/**
	 * Load.
	 *
	 * @param reader the reader
	 * @return the gaml properties
	 */
	public GamlProperties load(final Reader reader) {
		final Properties prop = new Properties();
		try {
			prop.load(reader);
		} catch (final IOException e) {
			try {
				reader.close();
			} catch (final IOException e1) {}
			return null;
		}
		for (final String s : prop.stringPropertyNames()) {
			final String[] array = prop.getProperty(s, "").split(SEPARATOR);
			final LinkedHashSet<String> values = new LinkedHashSet(Arrays.asList(array));
			put(s, values);
		}
		try {
			reader.close();
		} catch (final IOException e) {}
		return this;
	}

	/**
	 *
	 */
	public void clear() {
		map.clear();
	}

}
