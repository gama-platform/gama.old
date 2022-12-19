/*******************************************************************************************************
 *
 * BinarySpace.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.value.binary;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSEnumDataType;

/**
 * Value space of boolean value
 *
 * @author kevinchapuis
 *
 */
public class BinarySpace implements IValueSpace<BooleanValue> {

	/** The values. */
	private final Set<BooleanValue> values;
	
	/** The empty value. */
	private final BooleanValue emptyValue;

	/** The attribute. */
	private final IAttribute<BooleanValue> attribute;

	/** The value true. */
	@JsonIgnore public final BooleanValue valueTrue;
	
	/** The value false. */
	@JsonIgnore public final BooleanValue valueFalse;

	/**
	 * Constraint bianry space constructor define with values: {@link Boolean#TRUE}, {@link Boolean#FALSE} and a null
	 * {@link Boolean} as empty value
	 *
	 * @param attribute
	 */
	public BinarySpace(final IAttribute<BooleanValue> attribute) {
		this.attribute = attribute;
		valueTrue = new BooleanValue(this, true);
		valueFalse = new BooleanValue(this, false);
		this.values = Stream.of(valueTrue, valueFalse).collect(Collectors.toSet());
		this.emptyValue = new BooleanValue(this, null);
	}

	// ---------------------------------------------------------------------- //

	@Override
	public BooleanValue getInstanceValue(final String value) {
		return this.getValue(value);
	}

	@Override
	public BooleanValue proposeValue(final String value) {
		return this.getValue(value);
	}

	@Override
	public BooleanValue addValue(final String value) throws IllegalArgumentException {
		return this.getValue(value);
	}

	@Override
	public BooleanValue getValue(final String value) throws NullPointerException {
		if (!isValidCandidate(value)) throw new NullPointerException("The string value " + value
				+ " cannot be resolve to boolean as defined by " + this.getClass().getSimpleName());
		return values.stream().filter(val -> val.getStringValue().equalsIgnoreCase(value)).findFirst().get();
	}

	@Override
	public Set<BooleanValue> getValues() { return Collections.unmodifiableSet(values); }

	@Override
	public boolean contains(final IValue value) {
		return values.contains(value);
	}

	@Override
	public IAttribute<BooleanValue> getAttribute() { return attribute; }

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Boolean; }

	@Override
	public Class<BooleanValue> getTypeClass() { return BooleanValue.class; }

	@Override
	public BooleanValue getEmptyValue() { return emptyValue; }

	@Override
	public void setEmptyValue(final String value) {
		// JUST DONT DO THAT
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isValidCandidate(final String value) {
		if (!value.equalsIgnoreCase(Boolean.TRUE.toString()) || !value.equalsIgnoreCase(Boolean.FALSE.toString())
				|| emptyValue.getStringValue().equalsIgnoreCase(value))
			return true;
		return false;
	}

	// ---------------------------------------------- //

	@Override
	public int hashCode() {
		return this.getHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return this.isEqual(obj);
	}

	@Override
	public String toString() {
		return this.toPrettyString();
	}

	@Override
	public boolean contains(final String valueStr) {
		return false;
	}

	@Override
	public boolean containsAllLabels(final Collection<String> valuesStr) {
		return this.values.stream().allMatch(val -> valuesStr.contains(val.getStringValue()));
	}

	@Override
	public IValueSpace<BooleanValue> clone(final IAttribute<BooleanValue> newReferent) {
		return new BinarySpace(newReferent);
	}

	@Override
	public void addExceludedValue(final String string) {}

}
