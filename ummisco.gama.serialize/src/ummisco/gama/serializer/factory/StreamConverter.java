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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import msi.gama.runtime.IScope;
import msi.gaml.compilation.kernel.GamaClassLoader;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

/**
 * The Class StreamConverter.
 */
public abstract class StreamConverter {

	/** The streamer. */
	static XStream streamer;

	static {
		streamer = new XStream(new DomDriver());
		streamer.addPermission(AnyTypePermission.ANY);
		streamer.setClassLoader(GamaClassLoader.getInstance());
	}

	/**
	 * Register converter.
	 *
	 * @param dataStreamer
	 *            the data streamer
	 * @param c
	 *            the c
	 */
	public static void registerConverter(final Converter c) {
		streamer.registerConverter(c);
	}

	/**
	 * Load and build.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	public static XStream loadAndBuild(final ConverterScope cs) {

		final Converter[] cnv = Converters.converterFactory(cs);
		for (final Converter c : cnv) { StreamConverter.registerConverter(c); }
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
		return loadAndBuild(new ConverterScope(scope)).toXML(o);
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
	public static synchronized String convertObjectToStream(final ConverterScope scope, final Object o) {
		return loadAndBuild(scope).toXML(o);
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
		return loadAndBuild(new ConverterScope(scope)).fromXML(data);
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
	public static Object convertStreamToObject(final ConverterScope scope, final String data) {
		return loadAndBuild(scope).fromXML(data);
	}

	/**
	 * Load and build network.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	// TODO To remove when possible
	public static XStream loadAndBuildNetwork(final ConverterScope cs) {

		final Converter[] cnv = Converters.converterNetworkFactory(cs);
		for (final Converter c : cnv) { StreamConverter.registerConverter(c); }
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
	public static synchronized String convertNetworkObjectToStream(final ConverterScope scope, final Object o) {
		return loadAndBuildNetwork(scope).toXML(o);
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
		return loadAndBuildNetwork(new ConverterScope(scope)).toXML(o);
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
	public static Object convertNetworkStreamToObject(final ConverterScope scope, final String data) {
		return loadAndBuildNetwork(scope).fromXML(data);
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
		return loadAndBuildNetwork(new ConverterScope(scope)).fromXML(data);
	}

	// END TODO
}
