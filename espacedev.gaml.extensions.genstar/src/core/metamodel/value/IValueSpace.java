/*******************************************************************************************************
 *
 * IValueSpace.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.binary.BinarySpace;
import core.metamodel.value.categoric.NominalSpace;
import core.metamodel.value.categoric.OrderedSpace;
import core.metamodel.value.numeric.ContinuousSpace;
import core.metamodel.value.numeric.IntegerSpace;
import core.metamodel.value.numeric.RangeSpace;
import core.util.data.GSEnumDataType;

/**
 * Define a set of value both concretely and theoretically as it characterize a given attribute. Theoretical space is
 * define using the {@link #addValue(String)} method while concrete space is define using {@link #getValue(String)}.
 * First one is a prior requirement to retrieve value from concrete space
 *
 * @author kevinchapuis
 *
 * @param <V>
 */
@JsonSubTypes ({ @JsonSubTypes.Type (
		value = BinarySpace.class),
		@JsonSubTypes.Type (
				value = NominalSpace.class),
		@JsonSubTypes.Type (
				value = OrderedSpace.class),
		@JsonSubTypes.Type (
				value = ContinuousSpace.class),
		@JsonSubTypes.Type (
				value = IntegerSpace.class),
		@JsonSubTypes.Type (
				value = RangeSpace.class) })
@JsonPropertyOrder ({ IValueSpace.TYPE_LABEL, IValueSpace.VALUES_LABEL })
public interface IValueSpace<V extends IValue> {

	/** The ref att. */
	String REF_ATT = "REFERENCE ATTRIBUTE";

	/** The empty label. */
	String EMPTY_LABEL = "EMPTY VALUE";

	/** The type label. */
	String TYPE_LABEL = "TYPE";

	/** The values label. */
	String VALUES_LABEL = "VALUES";

	/**
	 * Create and return requested value without any constraint
	 *
	 * @param value
	 * @return
	 */
	V proposeValue(String value);

	/**
	 * Create and return requested value according to value space specification
	 *
	 * @param value
	 * @return
	 */
	V getInstanceValue(String value);

	/**
	 * Create, add and return requested value according to value space specification. Must be compliant with concrete
	 * value type (or throw an exception) - contract should be define by {@link #getInstanceValue(String)}.
	 * <p>
	 * If one asking to add excluded value, returns the empty value
	 *
	 * @param value
	 * @return
	 */
	V addValue(String value) throws IllegalArgumentException;

	/**
	 * Retrieve the value from the operational value space as it as been defined. It might correspond to either the
	 * textual representation of the value in the space, or to its actual value.
	 *
	 * the value have not been define in the theoretical space first using {@link #addValue(String)} then an exception
	 * is raised
	 *
	 * @param value
	 * @return
	 * @throws NullPointerException
	 */
	V getValue(String value) throws NullPointerException;

	/**
	 * Give the set of value in this value space
	 *
	 * @return
	 */
	@JsonProperty (VALUES_LABEL)
	Set<V> getValues();

	/**
	 * Test if this value space contains {@code value}. <br>
	 * WARNING: do not try to call contains() method with {@link #getValues()} because inner collection could rely on
	 * comparators and leads to a class cast exception
	 *
	 * @param value
	 * @return
	 */
	boolean contains(IValue value);

	/**
	 * Test if this value space contains value provided as a String
	 *
	 * @param valueStr
	 * @return
	 */
	boolean contains(String valueStr);

	/**
	 * Test if this value space contains all provided values as a collection of String
	 *
	 * @param valuesStr
	 * @return
	 */
	boolean containsAllLabels(Collection<String> valuesStr);

	/**
	 * Return the *empty value* for this value space
	 *
	 */
	@JsonProperty (EMPTY_LABEL)
	V getEmptyValue();

	/**
	 * Force the *empty value* to be made of {@link IValue} made from {@code value} parameter
	 *
	 * @param value
	 */
	void setEmptyValue(String value);

	/**
	 * Set explicit no data
	 *
	 * @param string
	 */
	void addExceludedValue(String string);

	// ------------------------------------------------------ //

	/**
	 * Return the type of value this space contains
	 */
	@JsonProperty (IValueSpace.TYPE_LABEL)
	GSEnumDataType getType();

	/**
	 * Return the type class of value
	 *
	 * @return
	 */
	@JsonIgnore
	Class<V> getTypeClass();

	/**
	 * States if passed value is a theoretical valid candidate to be part of this value space
	 *
	 * @param value
	 * @return
	 */
	boolean isValidCandidate(String value);

	/**
	 * The attribute this value space defines
	 *
	 * @return
	 */
	@JsonProperty (IValueSpace.REF_ATT)
	@JsonBackReference (
			value = IValueSpace.REF_ATT)
	IAttribute<V> getAttribute();

	/**
	 * Return a blank (no value store) clone of this value space
	 *
	 * @return
	 */
	IValueSpace<V> clone(IAttribute<V> newReferent);

	// -------------------------------------------------------- //

	/**
	 * To pretty string.
	 *
	 * @return the string
	 */
	default String toPrettyString() {
		return "[" + this.getType().toString() + "]";
	}

	/**
	 * Utility method to compute hash code
	 *
	 * @return
	 */
	@JsonIgnore
	default int getHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (getAttribute() == null ? 0 : getAttribute().getAttributeName().hashCode());
		return prime * result + getType().hashCode();
	}

	/**
	 * Utility method to estimate equality
	 *
	 * @param obj
	 * @return
	 */
	default boolean isEqual(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		IValueSpace<? extends IValue> other = (IValueSpace<?>) obj;
		if (getAttribute() == null) {
			if (other.getAttribute() != null) return false;
		} else if (!getAttribute().getAttributeName().equals(other.getAttribute().getAttributeName())) return false;
		return Objects.equals(getType(), other.getType());
	}

}
