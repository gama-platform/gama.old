/*******************************************************************************************************
 *
 * OrderedSpace.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value.categoric;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.metamodel.value.categoric.template.GSCategoricTemplate;
import core.util.data.GSEnumDataType;

/**
 * Define a space for ordered value: each value that pertain to this value space has an index that define its relative
 * position to all other values.
 * <p>
 * Hence, if values were added as it is, ordering is based on insertion order. This is also possible to add values with
 * a given index and manages indexes as list does.
 *
 * @author kevinchapuis
 *
 */
public class OrderedSpace implements IValueSpace<OrderedValue> {

	// Generic purpose comparator. Ordered value does not implement comparable because
	/** The comp. */
	// they only be compared within a given ordered space
	@JsonIgnore private static Comparator<OrderedValue> comp = OrderedValue::compareTo;

	/** The values. */
	private final TreeSet<OrderedValue> values;

	/** The empty value. */
	private OrderedValue emptyValue;

	/** The excluded values. */
	private final Set<String> excludedValues;

	/** The attribute. */
	private final IAttribute<OrderedValue> attribute;

	/** The template. */
	private final GSCategoricTemplate template;

	/**
	 * Contains a cache of the search for a value based on its string counterpart (performance)
	 */
	private final Map<String, OrderedValue> str2value = new HashMap<>();

	/** The instance index. */
	private int instanceIndex;

	/**
	 * Instantiates a new ordered space.
	 *
	 * @param attribute
	 *            the attribute
	 * @param template
	 *            the template
	 */
	public OrderedSpace(final IAttribute<OrderedValue> attribute, final GSCategoricTemplate template) {
		this.values = new TreeSet<>(comp);
		this.excludedValues = new HashSet<>();
		this.attribute = attribute;
		this.template = template;
		this.emptyValue = new OrderedValue(this, null, 0);
		this.instanceIndex = 0;
	}

	/**
	 * Compare.
	 *
	 * @param referent
	 *            the referent
	 * @param compareTo
	 *            the compare to
	 * @return the int
	 */
	public int compare(final OrderedValue referent, final OrderedValue compareTo) {
		return referent.compareTo(compareTo);
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Order; }

	@Override
	public Class<OrderedValue> getTypeClass() { return OrderedValue.class; }

	@Override
	public boolean isValidCandidate(final String value) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: order consistency is not guaranteed using this method
	 */
	@Override
	public OrderedValue getInstanceValue(final String value) {
		return new OrderedValue(this, value, ++instanceIndex);
	}

	/**
	 * {@inheritDoc} WARNING: completly break indexation rules
	 */
	@Override
	public OrderedValue proposeValue(final String value) {
		return new OrderedValue(this, value, 0);
	}

	// ------------------------ SETTERS & ADDER CAPACITIES ------------------------ //

	/**
	 * {@inheritDoc}
	 * <p>
	 * Whenever this method is called, value is added at the end of the ordered collection. If one wants to specify
	 * order (as int) the value should take use {@link #addValue(int, String)}
	 *
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	@Override
	public OrderedValue addValue(final String value) throws IllegalArgumentException {
		return addValue(values.size(), value);
	}

	/**
	 *
	 * @param order
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public OrderedValue addValue(final Number order, final String value) throws IllegalArgumentException {
		if (excludedValues.contains(value)) return this.getEmptyValue();
		OrderedValue ov = null;
		try {
			ov = this.getValue(value);
			if (ov.getOrder().equals(order)) throw new IllegalArgumentException(
					"Ordered value " + value + " already exists with order " + ov.getOrder());
		} catch (NullPointerException e) {
			ov = new OrderedValue(this, value, order);
			this.values.add(ov);
		}
		return ov;
	}

	@Override
	public Set<OrderedValue> getValues() { return Collections.unmodifiableSet(values); }

	@Override
	public OrderedValue getValue(final String value) throws NullPointerException {

		OrderedValue val = str2value.get(value);
		if (val != null) return val;

		Optional<OrderedValue> opOv =
				values.stream().filter(ov -> ov.getStringValue().equals(template.format(value))).findAny();
		if (opOv.isPresent()) {
			val = opOv.get();
			str2value.put(value, val);
			return val;
		}
		throw new NullPointerException(
				"The string value " + value + " is not comprise " + "in the value space " + this.toString());
	}

	@Override
	public boolean contains(final IValue value) {
		if (!value.getClass().equals(OrderedValue.class)) return false;
		return new HashSet<>(values).contains(value);
	}

	@Override
	public OrderedValue getEmptyValue() { return emptyValue; }

	@Override
	public void setEmptyValue(final String value) {
		try {
			this.emptyValue = getValue(value);
		} catch (NullPointerException e) {
			this.emptyValue = new OrderedValue(this, value, 0);
		}
	}

	@Override
	public void addExceludedValue(final String value) {
		this.excludedValues.add(value);
	}

	@Override
	public IAttribute<OrderedValue> getAttribute() { return attribute; }

	/**
	 * Gives the template used to elaborate proper formated value for this value space
	 *
	 * @return
	 */
	public GSCategoricTemplate getCategoricTemplate() { return template; }

	// ---------------------------------------------- //

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.getHashCode();
		return prime * result + template.hashCode();

	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof OrderedSpace os && this.isEqual(os) && this.template.equals(os.getCategoricTemplate());
	}

	@Override
	public String toString() {
		return this.toPrettyString();
	}

	@Override
	public boolean contains(final String valueStr) {
		return values.stream().anyMatch(v -> v.getStringValue().equals(valueStr));
	}

	@Override
	public boolean containsAllLabels(final Collection<String> valuesStr) {
		return this.values.stream().allMatch(val -> valuesStr.contains(val.getStringValue()));
	}

	@Override
	public IValueSpace<OrderedValue> clone(final IAttribute<OrderedValue> newReferent) {
		return new OrderedSpace(newReferent, getCategoricTemplate());
	}

}
