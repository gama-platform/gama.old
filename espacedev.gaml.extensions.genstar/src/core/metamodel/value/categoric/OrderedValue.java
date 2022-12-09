/*******************************************************************************************************
 *
 * OrderedValue.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
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
import core.metamodel.value.IValueSpace;
import core.util.data.GSEnumDataType;

/**
 * Encapsulate a {@link String} value that is inherently ordered
 * <p>
 * Two {@link OrderedValue} can be ordered if, and only if, they pertain to the same {@link IValueSpace}. In fact, two
 * ordered value cannot be compared outside of a specific {@link OrderedSpace} using
 * {@link OrderedSpace#compare(OrderedValue, OrderedValue)} method
 *
 * @author kevinchapuis
 *
 */
public class OrderedValue implements IValue {

	/** The value. */
	private final String value;

	/** The order. */
	private Number order;

	/** The sv. */
	@JsonManagedReference private final OrderedSpace sv;

	/**
	 * Instantiates a new ordered value.
	 *
	 * @param sv
	 *            the sv
	 * @param value
	 *            the value
	 * @param order
	 *            the order
	 */
	protected OrderedValue(final OrderedSpace sv, final String value, final Number order) {
		this.sv = sv;
		this.value = value;
		this.order = order;
	}

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Order; }

	@JsonIgnore
	@SuppressWarnings ("unchecked")
	@Override
	public <T> T getActualValue() { return (T) this.value; }

	@Override
	public String getStringValue() { return value; }

	@Override
	public OrderedSpace getValueSpace() { return sv; }

	/**
	 * Compare to.
	 *
	 * @param o
	 *            the o
	 * @return the int
	 */
	protected int compareTo(final OrderedValue o) {
		return Double.compare(order.doubleValue(), o.getOrder().doubleValue());
	}

	/**
	 * Sets the order.
	 *
	 * @param order
	 *            the new order
	 */
	protected void setOrder(final int order) { this.order = order; }

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public Number getOrder() { return order; }

	// ------------------------------------------------------ //

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: Does not take into account order
	 */
	@Override
	public int hashCode() {
		return this.getHashCode();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: Does not take into account order
	 */
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
		throw new UnsupportedOperationException("cannot change the actual value of a Ordered attribute");
	}

}
