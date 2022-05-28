/*******************************************************************************************************
 *
 * StreamConverter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import msi.gama.runtime.IScope;
import msi.gaml.compilation.kernel.GamaClassLoader;

/**
 * The Class StreamConverter.
 */
public abstract class StreamConverter {

	/** The streamer. */
	// static XStream streamer;
	//
	// static {
	// streamer = new XStream(new DomDriver());
	// streamer.addPermission(AnyTypePermission.ANY);
	// streamer.setClassLoader(GamaClassLoader.getInstance());
	// }
	private static Map<Class<?>, XStream> xStreamMap = Collections.synchronizedMap(new HashMap<Class<?>, XStream>());

	/**
	 * Gets the x stream instance.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the x stream instance
	 */
	private static XStream getXStreamInstance(final Class<?> clazz) {
		return getXStreamInstance(clazz, false);
	}

	/**
	 * Gets the x stream instance.
	 *
	 * @param clazz
	 *            the clazz
	 * @param toJSON
	 *            the to JSON
	 * @return the x stream instance
	 */
	private static XStream getXStreamInstance(final Class<?> clazz, final boolean toJSON) {
		if (xStreamMap.containsKey(clazz)) return xStreamMap.get(clazz);
		synchronized (clazz) {
			if (xStreamMap.containsKey(clazz)) return xStreamMap.get(clazz);
			XStream xStream;
			if (toJSON) {
				xStream = new XStream(new JettisonMappedXmlDriver());
			} else {
				xStream = new XStream(new DomDriver());
			}
			xStream.ignoreUnknownElements();
			xStream.processAnnotations(clazz);
			xStream.addPermission(AnyTypePermission.ANY);
			xStream.setClassLoader(GamaClassLoader.getInstance());
			xStreamMap.put(clazz, xStream);
			return xStream;
		}
	}

	/**
	 * From XML.
	 *
	 * @param xml
	 *            the xml
	 * @param type
	 *            the type
	 * @return the object
	 */
	public static Object fromXML(final String xml, final Class type) {
		return getXStreamInstance(type).fromXML(xml);

	}

	/**
	 * To xml.
	 *
	 * @param obj
	 *            the obj
	 * @return the string
	 */
	public static String toXml(final Object obj) {
		return getXStreamInstance(obj.getClass()).toXML(obj);

	}

	/**
	 * Register converter.
	 *
	 * @param dataStreamer
	 *            the data streamer
	 * @param c
	 *            the c
	 */
	public static void registerConverter(final XStream st, final Converter c) {
		st.registerConverter(c);
	}

	/**
	 * Convert object to stream in JSON.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 */
	public static synchronized String convertObjectToJSONStream(final IScope scope, final Object o) {
		return loadAndBuild(scope, o, true).toXML(o);
	}

	/**
	 * Load and build.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	public static XStream loadAndBuild(final IScope cs, final Object o) {
		return loadAndBuild(cs, o, false);
	}

	/**
	 * Load and build.
	 *
	 * @param cs
	 *            the cs
	 * @param o
	 *            the o
	 * @param toJSON
	 *            the to JSON
	 * @return the x stream
	 */
	public static XStream loadAndBuild(final IScope cs, final Object o, final boolean toJSON) {
		final Converter[] cnv = Converters.converterFactory(cs);
		XStream streamer = getXStreamInstance(o.getClass(), toJSON);
		for (final Converter c : cnv) { StreamConverter.registerConverter(streamer, c); }
		// dataStreamer.setMode(XStream.ID_REFERENCES);
		return streamer;
	}

	/**
	 * Convert object to stream.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 */
	public static synchronized String convertObjectToStream(final IScope scope, final Object o) {
		return loadAndBuild(scope, o).toXML(o);
	}

	/**
	 * Convert stream to object.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the object
	 */
	public static Object convertStreamToObject(final IScope scope, final String data) {
		return loadAndBuild(scope, String.class).fromXML(data);
	}

	/**
	 * Load and build network.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	// TODO To remove when possible
	public static XStream loadAndBuildNetwork(final IScope cs, final Object o) {

		XStream streamer = getXStreamInstance(o.getClass());
		final Converter[] cnv = Converters.converterNetworkFactory(cs);
		for (final Converter c : cnv) { StreamConverter.registerConverter(streamer, c); }
		return streamer;
	}

	/**
	 * Convert network object to stream.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 */
	public static synchronized String convertNetworkObjectToStream(final IScope scope, final Object o) {
		return loadAndBuildNetwork(scope, o).toXML(o);
	}

	/**
	 * Convert network stream to object.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the object
	 */
	public static Object convertNetworkStreamToObject(final IScope scope, final String data) {
		return loadAndBuildNetwork(scope, String.class).fromXML(data);
	}

	// END TODO
}
