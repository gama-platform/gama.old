package ummisco.gama.serializer.factory;

import java.util.logging.Logger;

import org.geotools.util.ConverterFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.kernel.GamaClassLoader;
import msi.gaml.types.IType;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

public abstract class StreamConverter {
	//private static XStream dataStreamer;
//	private static IScope  currentScope;
	
	public static void closeXStream()
	{
		//dataStreamer= null;
		//currentScope = null;
	}
	
	public static void registerConverter( XStream dataStreamer,Converter c)
	{
		dataStreamer.registerConverter(c);
	}
	
	public static XStream loadAndBuild(ConverterScope cs)	
	{
		XStream dataStreamer = new XStream(new DomDriver());
		dataStreamer.setClassLoader(GamaClassLoader.getInstance());

		Converter[] cnv = Converters.converterFactory(cs);
		for(Converter c:cnv)
		{
			StreamConverter.registerConverter(dataStreamer,c);
		}
		return dataStreamer;
	}
	
	public static synchronized String convertObjectToStream(IScope scope, Object o)
	{
		return loadAndBuild(new ConverterScope(scope)).toXML(o);
	}
	
	public static synchronized String convertObjectToStream(ConverterScope scope, Object o)
	{
		return loadAndBuild(scope).toXML(o);
	}	
	
	public static Object convertStreamToObject(IScope scope,String data)
	{
		return loadAndBuild(new ConverterScope(scope)).fromXML(data);
	}
	
	public static Object convertStreamToObject(ConverterScope scope,String data)
	{
		return loadAndBuild(scope).fromXML(data);
	}
	
	

	
	// TODO To remove when possible
	public static XStream loadAndBuildNetwork(ConverterScope cs)	
	{
		XStream dataStreamer = new XStream(new DomDriver());
		dataStreamer.setClassLoader(GamaClassLoader.getInstance());

		Converter[] cnv = Converters.converterNetworkFactory(cs);
		for(Converter c:cnv)
		{
			StreamConverter.registerConverter(dataStreamer,c);
		}
		return dataStreamer;
	}	

	public static synchronized String convertNetworkObjectToStream(ConverterScope scope, Object o)
	{
		return loadAndBuildNetwork(scope).toXML(o);
	}	
	
	public static synchronized String convertNetworkObjectToStream(IScope scope, Object o)
	{
		return loadAndBuild(new ConverterScope(scope)).toXML(o);
	}
	
	public static Object convertNetworkStreamToObject(ConverterScope scope,String data)
	{
		return loadAndBuildNetwork(scope).fromXML(data);
	}
	
	public static Object convertNetworkStreamToObject(IScope scope,String data)
	{
		return loadAndBuild(new ConverterScope(scope)).fromXML(data);
	}	
	
	// END TODO
}
