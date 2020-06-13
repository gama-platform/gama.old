/*********************************************************************************************
 * 
 *
 * 'DataTypeFactory.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.common;

//gama.getexperiment.getOutputManager...

public final class DataTypeFactory {
	
	public static DataType getObjectMetaData(Object val)
	{
		DataType type;
		if(val==null)
		{
			return DataType.UNDEFINED;
		}
		if(val instanceof Integer||val instanceof Long)
			type=DataType.INT;
		else if(val instanceof Float||val instanceof Double)
			type=DataType.FLOAT;
		else if(val instanceof Boolean)
			type=DataType.BOOLEAN;
		else if(val instanceof String )
			type=DataType.STRING;
		/*else if(val instanceof pams.common.dataTypes.Combined )
			type=DataType.COMBINED;*/
		/*else if(val instanceof Display2D)
			type=DataType.DISPLAY2D;
			*/

		else type=DataType.UNDEFINED;
		return type;
	}
	
	public static Object getObjectFromText(String val, DataType t)
	{
		if(t.equals(DataType.INT))
			return Integer.valueOf(val);
		if(t.equals(DataType.BOOLEAN))
			return Boolean.valueOf(val);
		if(t.equals(DataType.FLOAT))
			return Float.valueOf(val);
		if(t.equals(DataType.STRING))
			return val;
		return val;
	}
	

}
