package ummisco.gama.serializer.factory;

import com.thoughtworks.xstream.converters.Converter;

import ummisco.gama.serializer.gamaType.converters.GamaAgentConverter;
import ummisco.gama.serializer.gamaType.converters.GamaBasicTypeConverter;
import ummisco.gama.serializer.gamaType.converters.GamaMapConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPairConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPointConverter;
import ummisco.gama.serializer.gamaType.converters.GamaScopeConverter;
import ummisco.gama.serializer.gamaType.converters.GamaSimulationAgentConverter;

public abstract class Converters {

	private static Converter[] converters={
				new GamaMapConverter(),
				new GamaPairConverter(),
				new GamaPointConverter(),
				new GamaScopeConverter(),
				new GamaSimulationAgentConverter(),
				new GamaBasicTypeConverter(),
				new GamaAgentConverter()
		};
	
	public static Converter[] converterFactory()
	{
		return converters;
	}
	
}
