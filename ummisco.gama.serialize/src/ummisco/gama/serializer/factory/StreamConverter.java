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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	/** The x stream map. */
	private static Map<Class<?>, XStream> REGULAR_STREAMS = new ConcurrentHashMap<>();

	/** The network streams. */
	private static Map<Class<?>, XStream> NETWORK_STREAMS = new ConcurrentHashMap<>();

	/**
	 * Gets the x stream instance.
	 *
	 * @param clazz
	 *            the clazz
	 * @param toJSON
	 *            the to JSON
	 * @param cnv
	 * @return the x stream instance
	 */
	private static XStream getXStreamInstance(final Map<Class<?>, XStream> streams, final Class<?> clazz,
			final boolean toJSON, final Converter[] cnv) {
		if (streams.containsKey(clazz)) return streams.get(clazz);
		XStream xStream = new XStream(toJSON ? new JettisonMappedXmlDriver() : new DomDriver());
		xStream.ignoreUnknownElements();
		xStream.processAnnotations(clazz);
		xStream.addPermission(AnyTypePermission.ANY);
		xStream.setClassLoader(GamaClassLoader.getInstance());
		for (final Converter c : cnv) { xStream.registerConverter(c); }
		streams.put(clazz, xStream);
		return xStream;
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
	private static XStream loadAndBuild(final IScope cs, final Object o, final boolean toJSON) {
		return getXStreamInstance(REGULAR_STREAMS, o.getClass(), toJSON, Converters.converterFactory(cs));
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
		return loadAndBuild(scope, o, false).toXML(o);
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
		return loadAndBuild(scope, String.class, false).fromXML(data);
	}

	/**
	 * Load and build network.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	private static XStream loadAndBuildNetwork(final IScope cs, final Object o) {
		return getXStreamInstance(NETWORK_STREAMS, o.getClass(), false, Converters.converterNetworkFactory(cs));
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

}