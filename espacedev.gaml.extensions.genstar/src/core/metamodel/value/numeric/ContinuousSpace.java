/*******************************************************************************************************
 *
 * ContinuousSpace.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
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
 * Encapsulates a set of double value
 *
 * WARNING: inner structure is not time efficient, also for fast use of {@link ContinuousValue} try to use
 * {@link #getInstanceValue(String)} or {@link #proposeValue(String)}, that do not store {@link ContinuousValue} in a
 * collection
 *
 * @author kevinchapuis
 *
 */
public class ContinuousSpace implements IValueSpace<ContinuousValue> {

	/** The gsdp. */
	private static GSDataParser gsdp = new GSDataParser();

	/** The empty value. */
	private ContinuousValue emptyValue;

	/** The values. */
	private final TreeMap<Double, ContinuousValue> values;

	/** The excluded values. */
	private final Set<String> excludedValues;

	/** The max. */
	private double min;
	
	/** The max. */
	private double max;

	/** The attribute. */
	private final IAttribute<ContinuousValue> attribute;

	/**
	 * Instantiates a new continuous space.
	 *
	 * @param attribute
	 *            the attribute
	 */
	public ContinuousSpace(final IAttribute<ContinuousValue> attribute) {
		this(attribute, -Double.MAX_VALUE, Double.MAX_VALUE);
	}

	/**
	 * Instantiates a new continuous space.
	 *
	 * @param attribute
	 *            the attribute
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 */
	public ContinuousSpace(final IAttribute<ContinuousValue> attribute, final Double min, final Double max) {
		this.attribute = attribute;
		this.min = min;
		this.max = max;
		this.emptyValue = new ContinuousValue(this, Double.NaN);
		this.values = new TreeMap<>();
		this.excludedValues = new HashSet<>();
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Continue; }

	@Override
	public Class<ContinuousValue> getTypeClass() { return ContinuousValue.class; }

	@Override
	public ContinuousValue getInstanceValue(final String value) {
		double currentVal = gsdp.getDouble(value);
		if (currentVal < min || currentVal > max) throw new IllegalArgumentException(
				"Proposed value " + currentVal + " is " + (currentVal < min ? "below" : "beyond") + " given bound ("
						+ (currentVal < min ? min : max) + ")");
		return new ContinuousValue(this, currentVal);
	}

	@Override
	public ContinuousValue proposeValue(final String value) {
		return new ContinuousValue(this, gsdp.getDouble(value));
	}

	// -------------------- SETTERS & GETTER CAPACITIES -------------------- //

	@Override
	public ContinuousValue addValue(final String value) {
		if (excludedValues.contains(value)) return this.getEmptyValue();
		ContinuousValue iv = getValue(value);
		if (value == null) {
			iv = this.getInstanceValue(value);
			values.put(iv.getActualValue(), iv);
		}
		return iv;
	}

	@Override
	public ContinuousValue getValue(final String value) throws NullPointerException {
		return values.get(gsdp.getDouble(value));
	}

	@Override
	public Set<ContinuousValue> getValues() { return new HashSet<>(values.values()); }

	@Override
	public boolean contains(final IValue value) {
		if (!value.getClass().equals(ContinuousValue.class)) return false;
		return values.containsValue(value);
	}

	@Override
	public ContinuousValue getEmptyValue() { return emptyValue; }

	@Override
	public void setEmptyValue(final String value) {
		try {
			this.emptyValue = getValue(value);
		} catch (NullPointerException npe) {
			try {
				this.emptyValue = new ContinuousValue(this, gsdp.getDouble(value));
			} catch (Exception e) {
				// IF value == null or value is not a parsable double
				// just keep with default empty value
			}
		}
	}

	@Override
	public void addExceludedValue(final String value) {
		this.excludedValues.add(value);
	}

	@Override
	public boolean isValidCandidate(final String value) {
		return gsdp.getValueType(value).isNumericValue() && gsdp.getDouble(value) >= min
				&& gsdp.getDouble(value) <= max;
	}

	@Override
	public IAttribute<ContinuousValue> getAttribute() { return attribute; }

	// ----------------------------------------------------- //

	/**
	 * Gets the max.
	 *
	 * @return the max
	 */
	@JsonProperty ("max")
	public double getMax() { return max; }

	/**
	 * Sets the max.
	 *
	 * @param max
	 *            the new max
	 */
	public void setMax(final double max) { this.max = max; }

	/**
	 * Gets the min.
	 *
	 * @return the min
	 */
	@JsonProperty ("min")
	public double getMin() { return min; }

	/**
	 * Sets the min.
	 *
	 * @param min
	 *            the new min
	 */
	public void setMin(final double min) { this.min = min; }

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
	public IValueSpace<ContinuousValue> clone(final IAttribute<ContinuousValue> newReferent) {
		return new ContinuousSpace(newReferent, getMin(), getMax());
	}

}
