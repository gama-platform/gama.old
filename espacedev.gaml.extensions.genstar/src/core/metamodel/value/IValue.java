/*******************************************************************************************************
 *
 * IValue.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.attribute.value.ValueSerializer;
import core.metamodel.value.binary.BooleanValue;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.OrderedValue;
import core.metamodel.value.numeric.ContinuousValue;
import core.metamodel.value.numeric.IntegerValue;
import core.metamodel.value.numeric.RangeValue;
import core.util.data.GSEnumDataType;

/**
 * The values that characterise Genstar's attribute of entity
 *
 * @author kevinchapuis
 *
 */
@JsonTypeInfo (
		use = JsonTypeInfo.Id.NONE,
		include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes ({ @JsonSubTypes.Type (
		value = BooleanValue.class),
		@JsonSubTypes.Type (
				value = NominalValue.class),
		@JsonSubTypes.Type (
				value = OrderedValue.class),
		@JsonSubTypes.Type (
				value = ContinuousValue.class),
		@JsonSubTypes.Type (
				value = IntegerValue.class),
		@JsonSubTypes.Type (
				value = RangeValue.class) })
@JsonSerialize (
		using = ValueSerializer.class)
public interface IValue {

	/** The value label. */
	String VALUE_LABEL = "INPUT VALUE";

	/**
	 * The type of data this value encapsulate
	 *
	 * @return
	 */
	@JsonIgnore
	GSEnumDataType getType();

	/**
	 * Returns the actual, native value. For a Boolean, the cast to Boolean will be implicit; for a Nominal, will be an
	 * implicit cast to String, etc.
	 *
	 * @return
	 */
	<T> T getActualValue();

	/**
	 * Defines the actual value. Provides the possibility to defined an encoding for this value; for instance, a Range
	 * might be "less than 10" and have an actual value of 0 or 'A'.
	 *
	 * Throws an exception if it makes no sense for the given type of value; for instance an Integer value does not
	 * supports changing the actual value, as this actual value is obviously always the integer value.
	 *
	 * @throws UnsupportedOperationException
	 */
	<T> void setActualValue(T v) throws UnsupportedOperationException;

	/**
	 * The value represented as a String
	 *
	 * @return
	 */
	@JsonProperty (IValue.VALUE_LABEL)
	String getStringValue();

	/**
	 * The value space this value is part of
	 *
	 * @return
	 */
	@JsonBackReference ()
	IValueSpace<? extends IValue> getValueSpace();

	/**
	 * Default hash code computation
	 *
	 * @return
	 */
	default int getHashCode() { return Objects.hash(getStringValue(), getValueSpace()); }

	/**
	 * Default equal implementation
	 *
	 * @param obj
	 * @return
	 */
	default boolean isEquals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		IValue other = (IValue) obj;
		if (this.getStringValue() == null) {
			if (other.getStringValue() != null) return false;
		} else if (!this.getStringValue().equals(other.getStringValue())) return false;
		if (this.getValueSpace() == null) {
			if (other.getValueSpace() != null) return false;
		} else if (!this.getValueSpace().equals(other.getValueSpace())) return false;
		return true;
	}

}
