package ummisco.gama.serialize.factory;

import com.thoughtworks.xstream.converters.Converter;

import ummisco.gama.serialize.gamaType.converters.GamaMapConverter;
import ummisco.gama.serialize.gamaType.converters.GamaPairConverter;
import ummisco.gama.serialize.gamaType.converters.GamaPointConverter;

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
