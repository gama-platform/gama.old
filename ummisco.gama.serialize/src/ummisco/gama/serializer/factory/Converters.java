package ummisco.gama.serializer.factory;

import com.thoughtworks.xstream.converters.Converter;

import ummisco.gama.serializer.gamaType.converters.GamaMapConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPairConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPointConverter;

public abstract class Converters {

	private static Converter[] converters={
				new GamaMapConverter(),
				new GamaPairConverter(),
				new GamaPointConverter()
		};
	
	public static Converter[] converterFactory()
	{
		return converters;
	}
	
}
