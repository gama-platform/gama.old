/*******************************************************************************************************
 *
 * Unknown.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.coders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruedi on 05/06/15.
 *
 * Can used by some Coders (namely Json) to represent objects of unknown classes. As binary codec's do not include
 * fieldnames in their outputstream, this can only be supported for fieldname containing (but slow) codec's.
 *
 */
public class Unknown implements Serializable {

	/** The fields. */
	Map<String, Object> fields;

	/** The items. */
	List items;

	/** The type. */
	String type;

	/**
	 * Instantiates a new unknown.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 sept. 2023
	 */
	public Unknown() {}

	/**
	 * Instantiates a new unknown.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param type
	 *            the type
	 * @date 30 sept. 2023
	 */
	public Unknown(final String type) {
		setType(type);
	}

	/**
	 * Sets the type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param type
	 *            the new type
	 * @date 30 sept. 2023
	 */
	public void setType(final String type) { this.type = type; }

	/**
	 * Sets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @return the unknown
	 * @date 30 sept. 2023
	 */
	public Unknown set(final String name, final Object value) {
		if (fields == null) { fields = new HashMap(); }
		fields.put(name, value);
		return this;
	}

	/**
	 * Adds the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param item
	 *            the item
	 * @return the unknown
	 * @date 30 sept. 2023
	 */
	public Unknown add(final Object item) {
		if (items == null) { items = new ArrayList(); }
		items.add(item);
		return this;
	}

	/**
	 * access nested data. unk.dot( 3, "id" );
	 *
	 * @param propPath
	 * @return
	 */
	public Object dot(final Object... propPath) {
		return dotImpl(0, propPath);
	}

	/**
	 * Ddot.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <T>
	 *            the generic type
	 * @param propPath
	 *            the prop path
	 * @return the t
	 * @date 30 sept. 2023
	 */
	public <T> T ddot(final Object... propPath) {
		List res = new ArrayList(propPath.length * 2);
		for (Object o : propPath) {
			if ((o instanceof String) && (((String) o).indexOf('.') >= 0)) {
				String split[] = ((String) o).split("\\.");
				for (String s : split) { res.add(s); }
			} else {
				res.add(o);
			}
		}
		return (T) dotImpl(0, res.toArray());
	}

	/**
	 * Dot unk.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param propPath
	 *            the prop path
	 * @return the unknown
	 * @date 30 sept. 2023
	 */
	public Unknown dotUnk(final Object... propPath) {
		return (Unknown) dotImpl(0, propPath);
	}

	/**
	 * Dot str.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param propPath
	 *            the prop path
	 * @return the string
	 * @date 30 sept. 2023
	 */
	public String dotStr(final Object... propPath) {
		return (String) dotImpl(0, propPath);
	}

	/**
	 * Dot int.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param propPath
	 *            the prop path
	 * @return the integer
	 * @date 30 sept. 2023
	 */
	public Integer dotInt(final Object... propPath) {
		return ((Number) dotImpl(0, propPath)).intValue();
	}

	/**
	 * Dot impl.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param index
	 *            the index
	 * @param propPath
	 *            the prop path
	 * @return the object
	 * @date 30 sept. 2023
	 */
	private Object dotImpl(final int index, final Object... propPath) {
		if (propPath[index] instanceof Number) {
			int idx = ((Number) propPath[index]).intValue();
			if (!isSequence() || idx < 0 || idx >= items.size()) return null;
			Object o = items.get(idx);
			if (index == propPath.length - 1) return o;
			return ((Unknown) o).dotImpl(index + 1, propPath);
		}
		String field = "" + propPath[index];
		if (isSequence()) return null;
		Object o = get(field);
		if (index == propPath.length - 1) return o;
		return ((Unknown) o).dotImpl(index + 1, propPath);
	}

	/**
	 * Gets the int.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the int
	 * @date 30 sept. 2023
	 */
	public int getInt(final String name) {
		Number o = (Number) get(name);
		if (o != null) return o.intValue();
		return 0;
	}

	/**
	 * Gets the double.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the double
	 * @date 30 sept. 2023
	 */
	public double getDouble(final String name) {
		Number o = (Number) get(name);
		if (o != null) return o.doubleValue();
		return 0;
	}

	/**
	 * Gets the string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the string
	 * @date 30 sept. 2023
	 */
	public String getString(final String name) {
		Object o = get(name);
		if (o != null) return o.toString();
		return null;
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the object
	 * @date 30 sept. 2023
	 */
	public Object get(final String name) {
		if (fields == null) return null;
		return fields.get(name);
	}

	/**
	 * Gets the arr.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the arr
	 * @date 30 sept. 2023
	 */
	public List getArr(final String name) {
		Object o = get(name);
		if (o instanceof Unknown) return ((Unknown) o).getItems();
		return null;
	}

	/**
	 * Gets the fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the fields
	 * @date 30 sept. 2023
	 */
	public Map<String, Object> getFields() { return fields; }

	/**
	 * Gets the type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the type
	 * @date 30 sept. 2023
	 */
	public String getType() { return type; }

	/**
	 * Gets the items.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the items
	 * @date 30 sept. 2023
	 */
	public List getItems() {
		if (items == null) { items = new ArrayList(); }
		return items;
	}

	/**
	 * Checks if is sequence.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is sequence
	 * @date 30 sept. 2023
	 */
	public boolean isSequence() { return items != null && (fields == null || fields.size() == 0); }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("<").append(type).append("> ");
		if (!isSequence()) {
			sb.append("{ ");
			for (Map.Entry<String, Object> stringObjectEntry : fields.entrySet()) {
				sb.append(stringObjectEntry.getKey()).append(" : ").append(stringObjectEntry.getValue());
				sb.append(", ");
			}
			sb.append(" }");
			return sb.toString();
		}
		sb.append("[ ");
		List items = getItems();
		for (int i = 0; i < items.size(); i++) {
			Object o = items.get(i);
			sb.append(o);
			if (i != items.size() - 1) { sb.append(", "); }
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Fields.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param fields
	 *            the fields
	 * @return the unknown
	 * @date 30 sept. 2023
	 */
	public Unknown fields(final Map<String, Object> fields) {
		this.fields = fields;
		return this;
	}

	/**
	 * Items.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param items
	 *            the items
	 * @return the unknown
	 * @date 30 sept. 2023
	 */
	public Unknown items(final List items) {
		this.items = items;
		return this;
	}

	/**
	 * Type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param type
	 *            the type
	 * @return the unknown
	 * @date 30 sept. 2023
	 */
	public Unknown type(final String type) {
		this.type = type;
		return this;
	}

	/**
	 * Put.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param field
	 *            the field
	 * @param vals
	 *            the vals
	 * @return the unknown
	 * @date 30 sept. 2023
	 */
	public Unknown put(final String field, final Object... vals) {
		Unknown unk = new Unknown();
		for (Object val : vals) { unk.add(val); }
		set(field, unk);
		return this;
	}
}
