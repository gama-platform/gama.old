/*******************************************************************************************************
 *
 * KsonTypeMapper.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package org.nustaq.kson;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ummisco.gama.dev.utils.DEBUG;

/**
 * simple implementation of type mapper. * Maps Classes to short string names and vice versa. * allows to add
 * user-defined type conversions (e.g. Date, Collections)
 *
 * This default implementation supports Date<=>String and Collections<=>Array coercion.
 */
public class KsonTypeMapper {

	/** The Constant NULL_LITERAL. */
	public static final Object NULL_LITERAL = "NULL";

	/** The use simpl clz name. */
	protected boolean useSimplClzName = true;

	/** The type map. */
	protected HashMap<String, Class> typeMap = new HashMap<>(31);

	/** The reverse type map. */
	protected HashMap<Class, String> reverseTypeMap = new HashMap<>(31);

	/** The date time instance. */
	protected DateFormat dateTimeInstance = DateFormat.getDateTimeInstance();

	/**
	 * Instantiates a new kson type mapper.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 sept. 2023
	 */
	public KsonTypeMapper() {
		map("map", HashMap.class).map("list", HashMap.class).map("set", HashSet.class);
	}

	/** The none. */
	final Class NONE = Object.class;

	/**
	 * Gets the type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param type
	 *            the type
	 * @return the type
	 * @date 29 sept. 2023
	 */
	public Class getType(final String type) {
		Class res = typeMap.get(type);
		if (res == null) {
			try {
				res = Class.forName(type);
				if (res == null) {
					typeMap.put(type, NONE);
				} else {
					typeMap.put(type, res);
				}
			} catch (ClassNotFoundException e) {
				typeMap.put(type, NONE);
				return null;
			}
		}
		if (res == NONE) { res = null; }
		return res;
	}

	/**
	 * Map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @param c
	 *            the c
	 * @return the kson type mapper
	 * @date 29 sept. 2023
	 */
	public KsonTypeMapper map(final String name, final Class c) {
		typeMap.put(name, c);
		reverseTypeMap.put(c, name);
		return this;
	}

	/**
	 * Map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param stringAndClasses
	 *            the string and classes
	 * @return the kson type mapper
	 * @date 29 sept. 2023
	 */
	public KsonTypeMapper map(final Object... stringAndClasses) {
		for (int i = 0; i < stringAndClasses.length; i += 2) { map(stringAndClasses[i], stringAndClasses[i + 1]); }
		return this;
	}

	/**
	 * Map.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @return the kson type mapper
	 * @date 29 sept. 2023
	 */
	public KsonTypeMapper map(final Class... c) {
		for (Class aClass : c) { map(aClass.getSimpleName(), aClass); }
		return this;
	}

	/**
	 * Checks if is use simpl clz name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is use simpl clz name
	 * @date 29 sept. 2023
	 */
	public boolean isUseSimplClzName() { return useSimplClzName; }

	/**
	 * Sets the use simpl clz name.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param useSimplClzName
	 *            the new use simpl clz name
	 * @date 29 sept. 2023
	 */
	public void setUseSimplClzName(final boolean useSimplClzName) { this.useSimplClzName = useSimplClzName; }

	/**
	 * map given Object to a target type. (needs support in coerceWriting also) Note one could add a pluggable
	 * Serializer/Coercer pattern here if required. Skipped for now for simplicity.
	 *
	 * @param type
	 *            - of target field
	 * @param readObject
	 *            - object read from string
	 * @return
	 */
	public Object coerceReading(Class type, final Object readObject) {
		if (type == null) return readObject;
		// make hashmaps from arrays. warning: for optimal performance, use direct arrays[] only in your serialized
		// classes
		if (Map.class.isAssignableFrom(type) && readObject.getClass().isArray()) {
			try {
				Map c = (Map) type.newInstance();
				int len = Array.getLength(readObject);
				for (int i = 0; i < len; i += 2) { c.put(Array.get(readObject, i), Array.get(readObject, i + 1)); }
				return c;
			} catch (Exception e) {
				DEBUG.ERR("Exception thrown by newInstance", e);
			}
		} else // make collections from arrays. warning: for optimal performance, use direct arrays[] only in your
				// serialized classes
		if (Collection.class.isAssignableFrom(type) && readObject.getClass().isArray()) {
			try {
				if (type.isInterface()) {
					if (List.class.isAssignableFrom(type)) {
						type = ArrayList.class;
					} else if (Map.class.isAssignableFrom(type)) { type = HashMap.class; }
				}
				Collection c = (Collection) type.newInstance();
				int len = Array.getLength(readObject);
				for (int i = 0; i < len; i++) { c.add(Array.get(readObject, i)); }
				return c;
			} catch (Exception e) {
				DEBUG.ERR("Exception thrown by newInstance", e);
			}
		} else if (Date.class.isAssignableFrom(type) && readObject instanceof String) {
			try {
				return dateTimeInstance.parse((String) readObject);
			} catch (ParseException pe) {
				DEBUG.ERR("Failed to parse date", pe);
			}
		} else if ((type == char.class || Character.class.isAssignableFrom(type)) && readObject instanceof String)
			return ((String) readObject).charAt(0);
		return readObject;
	}

	/**
	 * Gets the date time instance.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the date time instance
	 * @date 29 sept. 2023
	 */
	public DateFormat getDateTimeInstance() { return dateTimeInstance; }

	/**
	 * Sets the date time instance.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param dateTimeInstance
	 *            the new date time instance
	 * @date 29 sept. 2023
	 */
	public void setDateTimeInstance(final DateFormat dateTimeInstance) { this.dateTimeInstance = dateTimeInstance; }

	/**
	 * Map literal.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param type
	 *            the type
	 * @return the object
	 * @date 29 sept. 2023
	 */
	public Object mapLiteral(final String type) {
		if ("null".equals(type)) return NULL_LITERAL;
		if ("true".equals(type) || "yes".equals(type) || "y".equals(type)) return Boolean.TRUE;
		if ("false".equals(type) || "no".equals(type) || "n".equals(type)) return Boolean.FALSE;
		return null;
	}

	/**
	 * Gets the string for type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param aClass
	 *            the a class
	 * @return the string for type
	 * @date 29 sept. 2023
	 */
	public String getStringForType(final Class<? extends Object> aClass) {
		String res = reverseTypeMap.get(aClass);
		if (res == null) { res = useSimplClzName ? aClass.getSimpleName() : aClass.getName(); }
		return res;
	}
}
