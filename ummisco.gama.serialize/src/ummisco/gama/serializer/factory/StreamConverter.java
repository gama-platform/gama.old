package ummisco.gama.serializer.factory;

import java.util.logging.Logger;

import org.geotools.util.ConverterFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gaml.types.IType;

public abstract class StreamConverter {
	private static XStream dataStreamer;
	
	public static void registerConverter(Converter c)
	{
		dataStreamer.registerConverter(c);
		Logger.getLogger(StreamConverter.class.getName()).finer("Loaded Converter :"+c.getClass().getName());
	}
	
	
	
	public static void loadAndBuild(IScope scope)
	{
		dataStreamer = new XStream(new DomDriver());
		Converter[] cnv = Converters.converterFactory(scope);
		for(Converter c:cnv)
		{
			StreamConverter.registerConverter(c);
		}
	}
	
	public static String convertObjectToStream(IScope scope, Object o)
	{
		if(dataStreamer==null)
			loadAndBuild(scope);
		return dataStreamer.toXML(o);
	}
	
	public static Object convertStreamToObject(IScope scope,String data)
	{
		if(dataStreamer==null)
			loadAndBuild(scope);
		return dataStreamer.fromXML(data);
	}
	
	
	public static void main(String[] arg)
	{
		
		
	}

}
