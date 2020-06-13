/*********************************************************************************************
 *
 * 'GamlProperties.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

	Map<String, LinkedHashSet<String>> map;

	public final static String SKILLS = "skills";
	public final static String ACTIONS = "actions";
	public final static String ATTRIBUTES = "attributes";
	public final static String STATEMENTS = "statements";
	public final static String OPERATORS = "operators";
	public final static String GAML = "gaml.properties";
	public final static String SPECIES = "species";
	public final static String CONSTANTS = "constants";
	public final static String ARCHITECTURES = "architectures";
	public final static String TYPES = "types";
	public final static String PLUGINS = "plugins";
	public final static String SEPARATOR = "~";

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
		final Set<String> result = get(key);
		if (result == null) { return null; }
		for (final Iterator<String> it = result.iterator(); it.hasNext();) {
			return it.next();
		}
		return null;
	}

	public void remove(final String key) {
		map.remove(key);
	}

	public void put(final String key, final String value) {
		if (!map.containsKey(key)) {
			map.put(key, new LinkedHashSet<String>());
		}
		if (value != null) {
			map.get(key).add(value);
		}
	}

	public void put(final String key, final Iterable<String> values) {
		if (!map.containsKey(key))
			map.put(key, new LinkedHashSet());
		for (final String s : values) {
			map.get(key).add(s);
		}
	}

	public void putAll(final GamlProperties m) {
		for (final Iterator<Map.Entry<String, LinkedHashSet<String>>> it = m.map.entrySet().iterator(); it.hasNext();) {
			final Map.Entry<String, LinkedHashSet<String>> entry = it.next();
			put(entry.getKey(), (LinkedHashSet<String>) entry.getValue().clone());
		}
	}

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
