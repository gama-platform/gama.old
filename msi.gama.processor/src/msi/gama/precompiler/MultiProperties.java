/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
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
public class MultiProperties {

	Map<String, Set<String>> map;

	static final String NULL = "";

	public MultiProperties() {
		map = new HashMap();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Set<String> values() {
		Set<String> result = new HashSet();
		for ( Map.Entry<String, Set<String>> entry : map.entrySet() ) {
			result.addAll(entry.getValue());
		}
		return result;
	}

	public Set<String> get(final String key) {
		return map.get(key);
	}

	public String getFirst(final String key) {
		Set<String> result = get(key);
		if ( result == null ) { return null; }
		for ( String s : result ) {
			return s;
		}
		return null;
	}

	public void put(final String key, final String value) {
		if ( !map.containsKey(key) ) {
			map.put(key, new HashSet<String>());
		}
		map.get(key).add(value);
	}

	public void put(final String key, final Set<String> values) {
		if ( !map.containsKey(key) ) {
			map.put(key, values);
		} else {
			map.get(key).addAll(values);
		}
	}

	public void putAll(final MultiProperties m) {
		for ( String key : m.keySet() ) {
			put(key, m.get(key));
		}
	}

	public void store(final Writer writer) {
		Properties prop = new Properties();
		for ( String key : map.keySet() ) {
			prop.setProperty(key, toString(map.get(key)));
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
				sb.append(value).append(',');
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
		return NULL;

	}
	/**
	 * same than toString, without "set"
	 * @param strings
	 * @return toString
	 */
	public static String toStringWoSet(final Set<String> strings) {
		if ( !strings.isEmpty() ) {
			StringBuilder sb = new StringBuilder();
			for ( String value : strings ) {
				if (!value.trim().equals("set"))
					sb.append(value).append(',');
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
		return NULL;

	}

	public void load(final Reader reader) {
		Properties prop = new Properties();
		try {
			prop.load(reader);
		} catch (IOException e) {
			try {
				reader.close();
			} catch (IOException e1) {}
			return;
		}
		for ( String s : prop.stringPropertyNames() ) {
			String[] array = prop.getProperty(s, "").split(",");
			Set<String> values = new HashSet(Arrays.asList(array));
			put(s, values);
		}
		try {
			reader.close();
		} catch (IOException e) {}
	}

	public static MultiProperties loadFrom(final File file) {
		MultiProperties mp = new MultiProperties();
		if ( file == null ) { return mp; }
		try {
			mp.load(new FileReader(file));
		} catch (FileNotFoundException e) {}
		return mp;
	}

	public static MultiProperties loadFrom(final InputStream stream) {
		MultiProperties mp = new MultiProperties();
		if ( stream == null ) { return mp; }
		mp.load(new InputStreamReader(stream));
		return mp;

	}
}
