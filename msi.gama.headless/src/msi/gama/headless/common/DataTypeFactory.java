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
		
		switch (val.getClass().getSimpleName()) {
			case "Integer":
			case "Long":
				type = DataType.INT;
				break;
			case "Float":
			case "Double":
				type = DataType.FLOAT;
				break;
			case "Boolean":
				type = DataType.BOOLEAN;
				break;
			case "String":
				type = DataType.STRING;
				break;
	
			default:
				type = DataType.UNDEFINED;
				break;
		}

		return type;
	}

	public static Object getObjectFromText(final String val, final DataType t) {	
		Object result;
		switch (t) {
		case INT:
			result = Integer.valueOf(val);
			break;
		case BOOLEAN:
			result = Boolean.valueOf(val);
			break;
		case FLOAT:
			result = Double.valueOf(val);
			break;
		case STRING:
		default:
			result = val;
			break;
		}
		
		return result;
	}

}
