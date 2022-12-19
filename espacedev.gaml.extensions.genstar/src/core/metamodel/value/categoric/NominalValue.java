/*******************************************************************************************************
 *
 * NominalValue.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.value.categoric;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * Encapsulate nominal {@link String} value
 *
 * @author kevinchapuis
 *
 */
public class NominalValue implements IValue {

	/** The value. */
	private final String value;
	
	/** The actual value. */
	private Object actualValue;

	/** The vs. */
	@JsonManagedReference private final NominalSpace vs;

	/**
	 * Instantiates a new nominal value.
	 *
	 * @param vs the vs
	 * @param value the value
	 */
	protected NominalValue(final NominalSpace vs, final String value) {
		this.value = value;
		this.actualValue = value;
		this.vs = vs;
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Nominal; }

	@JsonIgnore
	@SuppressWarnings ("unchecked")
	@Override
	public <T> T getActualValue() { return (T) this.actualValue; }

	@Override
	public String getStringValue() { return value; }

	@Override
	public NominalSpace getValueSpace() { return vs; }

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
	public <T> void setActualValue(final T v) throws UnsupportedOperationException { this.actualValue = v; }

}
