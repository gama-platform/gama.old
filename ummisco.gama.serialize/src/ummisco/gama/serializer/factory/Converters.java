/*******************************************************************************************************
 *
 * Converters.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.factory;

import com.thoughtworks.xstream.converters.Converter;

import ummisco.gama.serializer.gamaType.converters.ConverterScope;
import ummisco.gama.serializer.gamaType.converters.GamaAgentConverter;
import ummisco.gama.serializer.gamaType.converters.GamaAgentConverterNetwork;
import ummisco.gama.serializer.gamaType.converters.GamaBDIPlanConverter;
import ummisco.gama.serializer.gamaType.converters.GamaBasicTypeConverter;
import ummisco.gama.serializer.gamaType.converters.GamaFileConverter;
import ummisco.gama.serializer.gamaType.converters.GamaGraphConverter;
import ummisco.gama.serializer.gamaType.converters.GamaListConverter;
import ummisco.gama.serializer.gamaType.converters.GamaListConverterNetwork;
import ummisco.gama.serializer.gamaType.converters.GamaMapConverter;
import ummisco.gama.serializer.gamaType.converters.GamaMatrixConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPairConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPathConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPopulationConverter;
import ummisco.gama.serializer.gamaType.converters.GamaSpeciesConverter;
import ummisco.gama.serializer.gamaType.converters.LogConverter;
import ummisco.gama.serializer.gamaType.converters.ReferenceAgentConverter;
import ummisco.gama.serializer.gamaType.converters.SavedAgentConverter;

/**
 * The Class Converters.
 */
public abstract class Converters {

	/**
	 * Load converter.
	 *
	 * @param cs the cs
	 * @return the converter[]
	 */
	private static Converter[] loadConverter(ConverterScope cs)
	{
		Converter[] converters= new Converter[15];
		converters[0]= new GamaBasicTypeConverter(cs);
		converters[1]=new GamaAgentConverter(cs);		
		converters[2]=new GamaListConverter(cs);
		converters[3]=new GamaMapConverter(cs);
		converters[4]=new GamaPairConverter();
		converters[5]=new GamaMatrixConverter(cs);
		converters[6]=new GamaGraphConverter(cs);		
		converters[7]=new GamaFileConverter(cs);

		converters[8]=new LogConverter();
		converters[9]=new SavedAgentConverter(cs);
		
		converters[10]= new GamaPopulationConverter(cs);
		converters[11]= new GamaSpeciesConverter(cs);	
		converters[12]= new ReferenceAgentConverter(cs);		
		converters[13]= new GamaPathConverter(cs);	
		
		converters[14]= new GamaBDIPlanConverter(cs);
		
		//converters[12]= new ComplexMessageConverter(cs);		
		
		return converters;
	}
	
	
	/**
	 * Converter factory.
	 *
	 * @param cs the cs
	 * @return the converter[]
	 */
	public static Converter[] converterFactory(ConverterScope cs)
	{
		return loadConverter(cs);
	}

	
	
	/**
	 * Load converter network.
	 *
	 * @param cs the cs
	 * @return the converter[]
	 */
	// TODO Remove when possible
	private static Converter[] loadConverterNetwork(ConverterScope cs)
	{
		Converter[] converters= new Converter[14];
		converters[0]= new GamaBasicTypeConverter(cs);
		converters[1]=new GamaAgentConverterNetwork(cs);
		converters[2]=new GamaListConverterNetwork(cs);
		converters[3]=new GamaMapConverter(cs);
		converters[4]=new GamaPairConverter();
		converters[5]=new GamaMatrixConverter(cs);
		converters[6]=new GamaGraphConverter(cs);		
		converters[7]=new GamaFileConverter(cs);

		converters[8]=new LogConverter();
		converters[9]=new SavedAgentConverter(cs);
		
		converters[10]= new GamaPopulationConverter(cs);
		converters[11]= new GamaSpeciesConverter(cs);
		converters[12]= new GamaPathConverter(cs);	
		
		converters[13]= new GamaBDIPlanConverter(cs);
		
		
		//converters[12]= new ComplexMessageConverter(cs);		
		
		return converters;
	}
	
	/**
	 * Converter network factory.
	 *
	 * @param cs the cs
	 * @return the converter[]
	 */
	public static Converter[] converterNetworkFactory(ConverterScope cs)
	{
		return loadConverterNetwork(cs);
	}	
	
	// END TODO
}
