/*******************************************************************************************************
 *
 * IntegerValue.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.value.numeric;

import com.fasterxml.jackson.annotation.JsonIgnore;

import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * A value that encapsulate an {@link Integer} and extend {@link IValue} contract
 *
 * @author kevinchapuis
 *
 */
public class IntegerValue implements IValue {

	/** The value. */
	private final Integer value;

	/** The is. */
	private final IntegerSpace is;

	/**
	 * Instantiates a new integer value.
	 *
	 * @param is the is
	 */
	protected IntegerValue(final IntegerSpace is) {
		this.is = is;
		this.value = null;
	}

	/**
	 * Instantiates a new integer value.
	 *
	 * @param is the is
	 * @param value the value
	 */
	protected IntegerValue(final IntegerSpace is, final int value) {
		this.is = is;
		this.value = value;
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Integer; }

	@Override
	public String getStringValue() { return String.valueOf(value); }

	@Override
	public IntegerSpace getValueSpace() { return is; }

	/**
	 * The actual encapsulated value
	 *
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	@JsonIgnore
	@Override
	public Integer getActualValue() { return value; }

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
				"cannot change the actual value of an Integer attribute, it will always be the corresponding Integer value");
	}

}
