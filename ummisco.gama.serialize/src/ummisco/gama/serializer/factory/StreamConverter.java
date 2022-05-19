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
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

/**
 * The Class StreamConverter.
 */
public abstract class StreamConverter {

	/** The streamer. */
//	static XStream streamer;
//
//	static {
//		streamer = new XStream(new DomDriver());
//		streamer.addPermission(AnyTypePermission.ANY);
//		streamer.setClassLoader(GamaClassLoader.getInstance());
//	}
	private static Map<Class<?>, XStream> xStreamMap = Collections.synchronizedMap(new HashMap<Class<?>, XStream>());

	private static XStream getXStreamInstance(Class<?> clazz) {
		return getXStreamInstance(clazz, false);
	}	
	
	private static XStream getXStreamInstance(Class<?> clazz, boolean toJSON) {
		if (xStreamMap.containsKey(clazz)) {
			return xStreamMap.get(clazz);
		}
		synchronized (clazz) {
			if (xStreamMap.containsKey(clazz)) {
				return xStreamMap.get(clazz);
			}
			XStream xStream;
			if(toJSON) {
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

	public static Object fromXML(String xml, final Class type) {
		return getXStreamInstance(type).fromXML(xml);

	}

	public static String toXml(Object obj) {
		return getXStreamInstance(obj.getClass()).toXML(obj);

	}

	/**
	 * Register converter.
	 *
	 * @param dataStreamer the data streamer
	 * @param c            the c
	 */
	public static void registerConverter(final XStream st,final Converter c) {
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
		return loadAndBuild(new ConverterScope(scope),o, true).toXML(o);
	}	
	
	/**
	 * Load and build.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	public static XStream loadAndBuild(final ConverterScope cs, final Object o) {
		return loadAndBuild(cs, o, false);
	}	
	
	public static XStream loadAndBuild(final ConverterScope cs, final Object o, final boolean toJSON) {

		final Converter[] cnv = Converters.converterFactory(cs);
		XStream streamer = getXStreamInstance(o.getClass(),toJSON);		
		for (final Converter c : cnv) { StreamConverter.registerConverter(streamer,c); }
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
		return loadAndBuild(new ConverterScope(scope),o).toXML(o);
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
		return loadAndBuild(scope,o).toXML(o);
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
		return loadAndBuild(new ConverterScope(scope),String.class).fromXML(data);
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
		return loadAndBuild(scope,String.class).fromXML(data);
	}

	/**
	 * Load and build network.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	// TODO To remove when possible
	public static XStream loadAndBuildNetwork(final ConverterScope cs, final Object o) {

		XStream streamer=getXStreamInstance(o.getClass());
		final Converter[] cnv = Converters.converterNetworkFactory(cs);
		for (final Converter c : cnv) { StreamConverter.registerConverter(streamer,c); }
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
		return loadAndBuildNetwork(scope,o).toXML(o);
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
		return loadAndBuildNetwork(new ConverterScope(scope),o).toXML(o);
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
		return loadAndBuildNetwork(scope,String.class).fromXML(data);
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
		return loadAndBuildNetwork(new ConverterScope(scope),String.class).fromXML(data);
	}
	
	
	
	/**
	 * Load and build MPI.
	 *
	 * @param cs
	 *            the cs
	 * @return the x stream
	 */
	// TODO To remove when possible
	public static XStream loadAndBuildMPI(final ConverterScope cs, final Object o) {

		XStream streamer=getXStreamInstance(o.getClass());
		final Converter[] cnv = Converters.converterMPIFactory(cs);
		for (final Converter c : cnv) { StreamConverter.registerConverter(streamer,c); }
		return streamer;
	}

	/**
	 * Convert MPI object to stream.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 */
	public static synchronized String convertMPIObjectToStream(final ConverterScope scope, final Object o) {
		return loadAndBuildMPI(scope,o).toXML(o);
	}

	/**
	 * Convert MPI object to stream.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 */
	public static synchronized String convertMPIObjectToStream(final IScope scope, final Object o) {
		return loadAndBuildMPI(new ConverterScope(scope),o).toXML(o);
	}

	/**
	 * Convert MPI stream to object.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the object
	 */
	public static synchronized Object convertMPIStreamToObject(final ConverterScope scope, final String data) {
		return loadAndBuildMPI(scope,String.class).fromXML(data);
	}

	/**
	 * Convert MPI stream to object.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the object
	 */
	public static synchronized Object convertMPIStreamToObject(final IScope scope, final String data) {
		return loadAndBuildMPI(new ConverterScope(scope),String.class).fromXML(data);
	}

	// END TODO
}
