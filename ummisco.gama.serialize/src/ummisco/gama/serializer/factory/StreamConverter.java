package ummisco.gama.serializer.factory;

import java.util.logging.Logger;

import org.geotools.util.ConverterFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.GamaClassLoader;
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
	
	
	
	public static XStream loadAndBuild(IScope scope)
	{
		XStream dataStreamer = new XStream(new DomDriver());
		dataStreamer.setClassLoader(GamaClassLoader.getInstance());

//		Converter[] cnv = Converters.converterFactory(scope);
		Converter[] cnv = Converters.converterFactory(new ConverterScope(scope));
		for(Converter c:cnv)
		{
			StreamConverter.registerConverter(dataStreamer,c);
		}
		return dataStreamer;
	}
	
	public static synchronized String convertObjectToStream(IScope scope, Object o)
	{
		//if(dataStreamer==null|| currentScope != scope)
			//loadAndBuild(scope);
		return loadAndBuild(scope).toXML(o);
	}
	
	public static Object convertStreamToObject(IScope scope,String data)
	{
		//if(dataStreamer==null|| currentScope != scope)
			//loadAndBuild(scope);
		return loadAndBuild(scope).fromXML(data);
	}
	
	
	

}
