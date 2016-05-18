package ummisco.gama.serializer.factory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import msi.gama.runtime.IScope;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;
import ummisco.gama.serializer.gamaType.converters.GamaAgentConverter;
import ummisco.gama.serializer.gamaType.converters.GamaBasicTypeConverter;
import ummisco.gama.serializer.gamaType.converters.GamaFileConverter;
import ummisco.gama.serializer.gamaType.converters.GamaGraphConverter;
import ummisco.gama.serializer.gamaType.converters.GamaListConverter;
import ummisco.gama.serializer.gamaType.converters.GamaMapConverter;
import ummisco.gama.serializer.gamaType.converters.GamaMatrixConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPairConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPointConverter;
import ummisco.gama.serializer.gamaType.converters.GamaScopeConverter;
import ummisco.gama.serializer.gamaType.converters.GamaSimulationAgentConverter;
import ummisco.gama.serializer.gamaType.converters.LogConverter;
import ummisco.gama.serializer.gamaType.converters.SavedAgentConverter;

public abstract class Converters {

	private static Converter[] converters=null;
	
	private static void loadConverter(IScope scope)
	{
		ConverterScope cs = new ConverterScope(scope);
		converters= new Converter[10];
		converters[0]= new GamaBasicTypeConverter(cs);
		converters[1]=new GamaAgentConverter(cs);
		converters[2]=new GamaListConverter(cs);
		converters[3]=new GamaMapConverter(cs);
		//xstream.registerConverter(new SavedAgentConverter(cs));
		converters[4]=new GamaPairConverter();
		converters[5]=new GamaMatrixConverter(cs);
		converters[6]=new GamaGraphConverter(cs);		
		converters[7]=new GamaFileConverter(cs);
	}
	
	public static Converter[] converterFactory(IScope scope)
	{
		if(converters==null)
			loadConverter(scope);
		return converters;
	}
	
}
