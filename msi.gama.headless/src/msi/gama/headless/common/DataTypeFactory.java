/*********************************************************************************************
 *
 *
 * 'DataTypeFactory.java', in plugin 'msi.gama.headless', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.common;

public final class DataTypeFactory {

	public static DataType getObjectMetaData(final Object val) {
		DataType type;
		if (val instanceof Integer || val instanceof Long) {
			type = DataType.INT;
		} else if (val instanceof Float || val instanceof Double) {
			type = DataType.FLOAT;
		} else if (val instanceof Boolean) {
			type = DataType.BOOLEAN;
		} else if (val instanceof String) {
			type = DataType.STRING;
		} else {
			type = DataType.UNDEFINED;
		}
		return type;
	}

	public static Object getObjectFromText(final String val, final DataType t) {
		if (t.equals(DataType.INT)) return Integer.valueOf(val);
		if (t.equals(DataType.BOOLEAN)) return Boolean.valueOf(val);
		// See #3006
		if (t.equals(DataType.FLOAT)) return Double.valueOf(val);
		if (t.equals(DataType.STRING)) return val;
		return val;
	}

}
