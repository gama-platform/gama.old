/*******************************************************************************************************
 *
 * NominalSpace.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value.categoric;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.metamodel.value.categoric.template.GSCategoricTemplate;
import core.util.data.GSEnumDataType;

/**
 * A set of value of nominal type. Nominal values represent Strings.
 *
 * @author kevinchapuis
 *
 */
public class NominalSpace implements IValueSpace<NominalValue> {

	/** The attribute. */
	private final IAttribute<NominalValue> attribute;

	/** The values. */
	protected Map<String, NominalValue> values;

	/** The empty value. */
	private NominalValue emptyValue;

	/** The excluded values. */
	private final Set<String> excludedValues;

	/** The ct. */
	private final GSCategoricTemplate ct;

	/**
	 * Instantiates a new nominal space.
	 *
	 * @param attribute
	 *            the attribute
	 * @param ct
	 *            the ct
	 */
	public NominalSpace(final IAttribute<NominalValue> attribute, final GSCategoricTemplate ct) {
		this.attribute = attribute;
		this.values = new HashMap<>();
		this.excludedValues = new HashSet<>();
		this.emptyValue = new NominalValue(this, null);
		this.ct = ct;
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Nominal; }

	@Override
	public Class<NominalValue> getTypeClass() { return NominalValue.class; }

	@Override
	public boolean isValidCandidate(final String value) {
		return true;
	}

	// -------------------- SETTERS & GETTER CAPACITIES -------------------- //

	@Override
	public NominalValue proposeValue(final String value) {
		return new NominalValue(this, value);
	}

	@Override
	public NominalValue getInstanceValue(final String value) {
		return new NominalValue(this, ct.getFormatedString(value));
	}

	@Override
	public NominalValue addValue(final String value) throws IllegalArgumentException {
		if (excludedValues.contains(value)) return this.emptyValue;

		String val = ct.getFormatedString(value);
		NominalValue nv = values.get(val);
		if (nv == null) {
			nv = new NominalValue(this, val);
			this.values.put(val, nv);
		}
		return nv;
	}

	@Override
	public Set<NominalValue> getValues() { return new HashSet<>(values.values()); }

	@Override
	public NominalValue getValue(final String value) throws NullPointerException {
		NominalValue val = values.get(value);
		if (val == null) { val = values.get(ct.getFormatedString(value)); }

		if (val == null) throw new NullPointerException(
				"The string value " + value + " is not comprise " + "in the value space " + this.toString());
		return val;
	}

	@Override
	public boolean contains(final IValue value) {
		if (value == null || !value.getClass().equals(NominalValue.class)) return false;
		return values.containsValue(value);
	}

	@Override
	public NominalValue getEmptyValue() { return emptyValue; }

	@Override
	public void setEmptyValue(final String value) {
		String val = ct.getFormatedString(value);
		NominalValue nv = values.get(val);
		if (nv == null) { nv = new NominalValue(this, val); }
		this.emptyValue = nv;
	}

	@Override
	public void addExceludedValue(final String string) {
		excludedValues.add(string);
	}

	@Override
	public IAttribute<NominalValue> getAttribute() { return attribute; }

	/**
	 * Gives the template used to elaborate proper formated value for this value space
	 *
	 * @return
	 */
	public GSCategoricTemplate getCategoricTemplate() { return ct; }

	@Override
	public IValueSpace<NominalValue> clone(final IAttribute<NominalValue> newReferent) {
		return new NominalSpace(newReferent, getCategoricTemplate());
	}

	// ---------------------------------------------- //

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.getHashCode();
		return prime * result + ct.hashCode();

	}

	@Override
	public boolean equals(final Object obj) {

		return obj instanceof NominalSpace ns && this.isEqual(ns) && Objects.equals(ct, ns.getCategoricTemplate());
	}

	@Override
	public String toString() {
		return this.toPrettyString();
	}

	@Override
	public boolean contains(final String valueStr) {
		return values.containsKey(valueStr);
	}

	@Override
	public boolean containsAllLabels(final Collection<String> valuesStr) {
		return this.values.values().stream().allMatch(val -> valuesStr.contains(val.getStringValue()));
	}

}
