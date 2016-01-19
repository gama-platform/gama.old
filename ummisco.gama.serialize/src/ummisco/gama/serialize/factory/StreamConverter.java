package ummisco.gama.serialize.factory;

import java.util.logging.Logger;

import org.geotools.util.ConverterFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import msi.gama.util.GamaMapFactory;
import msi.gaml.types.IType;

public abstract class StreamConverter {
	private static XStream dataStreamer;
	
	public static void registerConverter(Converter c)
	{
		dataStreamer.registerConverter(c);
		Logger.getLogger(StreamConverter.class.getName()).finer("Loaded Converter :"+c.getClass().getName());
	}
	
	
	
	public static void loadAndBuild()
	{
		dataStreamer = new XStream(new DomDriver());
		Converter[] cnv = Converters.converterFactory();
		for(Converter c:cnv)
		{
			StreamConverter.registerConverter(c);
		}
	}
	
	public static String convertObjectToStream(Object o)
	{
		if(dataStreamer==null)
			loadAndBuild();
		return dataStreamer.toXML(o);
	}
	
	public static Object convertStreamToObject(String data)
	{
		if(dataStreamer==null)
			loadAndBuild();
		return dataStreamer.fromXML(data);
	}
	
	
	public static void main(String[] arg)
	{
		
		
	}

}
