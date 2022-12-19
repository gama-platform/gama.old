/*******************************************************************************************************
 *
 * ContinuousValue.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.value.numeric;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * Encapsulates a double value
 *
 * @author kevinchapuis
 *
 */
public class ContinuousValue implements IValue, Comparable<ContinuousValue> {

	/** The value. */
	private final Double value;

	/** The cs. */
	@JsonManagedReference private final ContinuousSpace cs;

	/**
	 * Instantiates a new continuous value.
	 *
	 * @param cs the cs
	 * @param value the value
	 */
	public ContinuousValue(final ContinuousSpace cs, final double value) {
		this.cs = cs;
		this.value = value;
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Continue; }

	@Override
	public String getStringValue() { return String.valueOf(value); }

	@Override
	public int compareTo(final ContinuousValue o) {
		return this.value.compareTo(o.getActualValue());
	}

	@Override
	public ContinuousSpace getValueSpace() { return cs; }

	/**
	 * The actual encapsulated value
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings ("unchecked")
	@JsonIgnore
	public Double getActualValue() { return value; }

	// ------------------------------------------------------ //

	@Override
	public int hashCode() {
		return this.getHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return this.isEquals(obj);
	}

	@Override
	public String toString() {
		return this.getStringValue();
	}

	@Override
	public <T> void setActualValue(final T v) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"cannot change the actual value of a Continuous attribute, it will always be a Double");
	}

}
