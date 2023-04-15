/*******************************************************************************************************
 *
 * DataTypeFactory.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.common;

/**
 * A factory for creating DataType objects.
 */
public final class DataTypeFactory {

	/**
	 * Gets the object meta data.
	 *
	 * @param val the val
	 * @return the object meta data
	 */
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

	/**
	 * Gets the object from text.
	 *
	 * @param val the val
	 * @param t the t
	 * @return the object from text
	 */
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
