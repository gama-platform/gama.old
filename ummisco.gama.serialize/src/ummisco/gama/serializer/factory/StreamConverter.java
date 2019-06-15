/*********************************************************************************************
 *
 * 'StreamConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.serializer.factory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import msi.gama.runtime.IScope;
import msi.gaml.compilation.kernel.GamaClassLoader;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

public abstract class StreamConverter {

	public static void registerConverter(final XStream dataStreamer, final Converter c) {
		dataStreamer.registerConverter(c);
	}

	public static XStream loadAndBuild(final ConverterScope cs) {
		final XStream dataStreamer = new XStream(new DomDriver());
		dataStreamer.setClassLoader(GamaClassLoader.getInstance());

		final Converter[] cnv = Converters.converterFactory(cs);
		for (final Converter c : cnv) {
			StreamConverter.registerConverter(dataStreamer, c);
		}
		// dataStreamer.setMode(XStream.ID_REFERENCES);
		return dataStreamer;
	}

	public static synchronized String convertObjectToStream(final IScope scope, final Object o) {
		return loadAndBuild(new ConverterScope(scope)).toXML(o);
	}

	public static synchronized String convertObjectToStream(final ConverterScope scope, final Object o) {
		return loadAndBuild(scope).toXML(o);
	}

	public static Object convertStreamToObject(final IScope scope, final String data) {
		return loadAndBuild(new ConverterScope(scope)).fromXML(data);
	}

	public static Object convertStreamToObject(final ConverterScope scope, final String data) {
		return loadAndBuild(scope).fromXML(data);
	}

	// TODO To remove when possible
	public static XStream loadAndBuildNetwork(final ConverterScope cs) {
		final XStream dataStreamer = new XStream(new DomDriver());
		dataStreamer.setClassLoader(GamaClassLoader.getInstance());

		final Converter[] cnv = Converters.converterNetworkFactory(cs);
		for (final Converter c : cnv) {
			StreamConverter.registerConverter(dataStreamer, c);
		}
		return dataStreamer;
	}

	public static synchronized String convertNetworkObjectToStream(final ConverterScope scope, final Object o) {
		return loadAndBuildNetwork(scope).toXML(o);
	}

	public static synchronized String convertNetworkObjectToStream(final IScope scope, final Object o) {
		return loadAndBuildNetwork(new ConverterScope(scope)).toXML(o);
	}

	public static Object convertNetworkStreamToObject(final ConverterScope scope, final String data) {
		return loadAndBuildNetwork(scope).fromXML(data);
	}

	public static Object convertNetworkStreamToObject(final IScope scope, final String data) {
		return loadAndBuildNetwork(new ConverterScope(scope)).fromXML(data);
	}

	// END TODO
}
