/*******************************************************************************************************
 *
 * GSEnumDataType.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.util.data;

import java.util.HashMap;
import java.util.Map;

import core.metamodel.value.IValue;
import core.metamodel.value.binary.BooleanValue;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.OrderedValue;
import core.metamodel.value.numeric.ContinuousValue;
import core.metamodel.value.numeric.IntegerValue;
import core.metamodel.value.numeric.RangeValue;

/**
 * Define all possible value type that can define attribute instance value:
 * <p>
 * <ul>
 * <li>{@link GSEnumDataType#Continue} : double value - {@link Double}
 * <li>{@link GSEnumDataType#Integer} : integer value - {@link Integer}
 * <li>{@link GSEnumDataType#Range} : couple of numeric values - {@link Number}
 * <li>{@link GSEnumDataType#Boolean} : boolean value - {@link Boolean}
 * <li>{@link GSEnumDataType#Order} : ordered nominal value - {@link String}
 * <li>{@link GSEnumDataType#Nominal} : nominal value - {@link String}
 * </ul>
 * <p>
 *
 * @author kevinchapuis
 * @author Vo Duc An
 *
 */
public enum GSEnumDataType {

	/** The Continue. */
	Continue(Double.class, ContinuousValue.class),
	
	/** The Integer. */
	Integer(Integer.class, IntegerValue.class),
	
	/** The Range. */
	Range(Number.class, RangeValue.class),
	
	/** The Boolean. */
	Boolean(Boolean.class, BooleanValue.class),
	
	/** The Order. */
	Order(String.class, OrderedValue.class),
	
	/** The Nominal. */
	Nominal(String.class, NominalValue.class);

	/** The wrapper class. */
	private Class<? extends IValue> wrapperClass;
	
	/** The concret class. */
	private Class<?> concretClass;

	/**
	 * Instantiates a new GS enum data type.
	 *
	 * @param concretClass the concret class
	 * @param wrapperClass the wrapper class
	 */
	GSEnumDataType(final Class<?> concretClass, final Class<? extends IValue> wrapperClass) {
		this.wrapperClass = wrapperClass;
		this.concretClass = concretClass;
	}

	/**
	 * Whether this {@link GSEnumDataType} is numerical or not
	 *
	 * @return
	 */
	public boolean isNumericValue() { return concretClass.getSuperclass().equals(Number.class); }

	/**
	 * Return the inner type this data type encapsulate
	 *
	 * @return
	 */
	public Class<?> getInnerType() { return concretClass; }

	/**
	 * Returns wrapper Gen* class
	 *
	 * @see IValue
	 *
	 * @return
	 */
	public Class<? extends IValue> getGenstarType() { return wrapperClass; }

	/** The wrapper class 2 enum. */
	private static Map<Class<?>, GSEnumDataType> wrapperClass2enum;

	/**
	 * Returns a datatype for a IValue type
	 *
	 * @param clazz
	 * @return
	 */
	public static GSEnumDataType getType(final Class<? extends IValue> clazz) {

		if (wrapperClass2enum == null) {
			wrapperClass2enum = new HashMap<>();
			for (GSEnumDataType dt : GSEnumDataType.values()) { wrapperClass2enum.put(dt.getGenstarType(), dt); }
		}

		GSEnumDataType res = wrapperClass2enum.get(clazz);
		if (res != null) return res;
		throw new IllegalArgumentException(
				clazz.getCanonicalName() + " is not linked to any " + GSEnumDataType.class.getCanonicalName());

	}

	/** The concret class 2 enum. */
	private static Map<Class<?>, GSEnumDataType> concretClass2enum;

	/**
	 * Gets the type for java type.
	 *
	 * @param clazz the clazz
	 * @return the type for java type
	 */
	public static GSEnumDataType getTypeForJavaType(final Class<?> clazz) {

		if (concretClass2enum == null) {
			concretClass2enum = new HashMap<>();
			for (GSEnumDataType dt : GSEnumDataType.values()) { concretClass2enum.put(dt.getInnerType(), dt); }
		}

		// hardcoded :-(
		// Long can be decoded as Int !
		if (clazz.equals(Long.class)) return GSEnumDataType.Integer;

		GSEnumDataType res = concretClass2enum.get(clazz);
		if (res != null) return res;
		throw new IllegalArgumentException(
				clazz.getCanonicalName() + " is not linked to any " + GSEnumDataType.class.getCanonicalName());

	}

}
