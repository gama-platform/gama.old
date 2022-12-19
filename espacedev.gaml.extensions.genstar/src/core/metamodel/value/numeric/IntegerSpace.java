/*******************************************************************************************************
 *
 * IntegerSpace.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value.numeric;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;

/**
 * javadoc
 *
 * @author kevinchapuis
 *
 */
public class IntegerSpace implements IValueSpace<IntegerValue> {

	/** The gsdp. */
	private static GSDataParser gsdp = new GSDataParser();

	/** The empty value. */
	private IntegerValue emptyValue;

	/** The values. */
	private final TreeMap<Integer, IntegerValue> values;

	/** The excluded values. */
	private final Set<String> excludedValues;

	/** The max. */
	private int min;
	
	/** The max. */
	private int max;

	/** The attribute. */
	private final IAttribute<IntegerValue> attribute;

	/**
	 * Instantiates a new integer space.
	 *
	 * @param attribute
	 *            the attribute
	 */
	public IntegerSpace(final IAttribute<IntegerValue> attribute) {
		this(attribute, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Instantiates a new integer space.
	 *
	 * @param attribute
	 *            the attribute
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 */
	public IntegerSpace(final IAttribute<IntegerValue> attribute, final Integer min, final Integer max) {
		this.values = new TreeMap<>();
		this.excludedValues = new HashSet<>();
		this.emptyValue = new IntegerValue(this);
		this.attribute = attribute;
		this.min = min;
		this.max = max;
	}

	@Override
	public IntegerValue getInstanceValue(final String value) {
		int currentVal = gsdp.parseNumbers(value).intValue();
		if (currentVal < min || currentVal > max) throw new IllegalArgumentException("Proposed value " + value + " is "
				+ (currentVal < min ? "below" : "beyond") + " given bound (" + (currentVal < min ? min : max) + ")");
		return new IntegerValue(this, currentVal);
	}

	@Override
	public IntegerValue proposeValue(final String value) {
		return new IntegerValue(this, gsdp.parseNumbers(value).intValue());
	}

	// -------------------- SETTERS & GETTER CAPACITIES -------------------- //

	/**
	 * {@inheritDoc} \p By default integer value addition is not stored, make use of {@link #getInstanceValue(String)}
	 */
	@Override
	public IntegerValue addValue(final String value) {
		if (excludedValues.contains(value)) return this.getEmptyValue();
		IntegerValue iv = getValue(value);
		if (iv == null) {
			iv = this.getInstanceValue(value);
			values.put(iv.getActualValue(), iv);
		}
		return iv;
	}

	@Override
	public IntegerValue getValue(final String value) throws NullPointerException {
		return values.get(gsdp.parseNumbers(value).intValue());
	}

	@Override
	public Set<IntegerValue> getValues() { return new HashSet<>(values.values()); }

	@Override
	public boolean contains(final IValue value) {
		if (!value.getClass().equals(IntegerValue.class)) return false;
		return values.containsValue(value);
	}

	@Override
	public IntegerValue getEmptyValue() { return emptyValue; }

	@Override
	public void setEmptyValue(final String value) {
		try {
			this.emptyValue = getValue(value);
		} catch (NullPointerException npe) {
			try {
				this.emptyValue = new IntegerValue(this, gsdp.getDouble(value).intValue());
			} catch (Exception e) {
				// If value is not a parsable integer or null just leave the
				// default value as it is
			}
		}
	}

	@Override
	public void addExceludedValue(final String value) {
		this.excludedValues.add(value);
	}

	@Override
	public boolean isValidCandidate(final String value) {
		return gsdp.getValueType(value).isNumericValue() && gsdp.parseNumbers(value).intValue() >= min
				&& gsdp.parseNumbers(value).intValue() <= max;
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Integer; }

	@Override
	public Class<IntegerValue> getTypeClass() { return IntegerValue.class; }

	@Override
	public IAttribute<IntegerValue> getAttribute() { return attribute; }

	// ----------------------------------------------------- //

	/**
	 * Gets the max.
	 *
	 * @return the max
	 */
	@JsonProperty ("max")
	public int getMax() { return max; }

	/**
	 * Sets the max.
	 *
	 * @param max
	 *            the new max
	 */
	public void setMax(final int max) { this.max = max; }

	/**
	 * Gets the min.
	 *
	 * @return the min
	 */
	@JsonProperty ("min")
	public int getMin() { return min; }

	/**
	 * Sets the min.
	 *
	 * @param min
	 *            the new min
	 */
	public void setMin(final int min) { this.min = min; }

	// ----------------------------------------------- //

	@Override
	public int hashCode() {
		return this.getHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return isEqual(obj);
	}

	@Override
	public String toString() {
		return this.toPrettyString();
	}

	@Override
	public boolean contains(final String valueStr) {
		return values.values().stream().anyMatch(v -> v.getStringValue().equals(valueStr));
	}

	@Override
	public boolean containsAllLabels(final Collection<String> valuesStr) {
		return this.values.values().stream().allMatch(val -> valuesStr.contains(val.getStringValue()));
	}

	@Override
	public IValueSpace<IntegerValue> clone(final IAttribute<IntegerValue> newReferent) {
		return new IntegerSpace(newReferent, getMin(), getMax());
	}

}
