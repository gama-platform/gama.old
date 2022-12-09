/*******************************************************************************************************
 *
 * RangeValue.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value.numeric;

import com.fasterxml.jackson.annotation.JsonIgnore;

import core.metamodel.value.IValue;
import core.metamodel.value.numeric.template.GSRangeTemplate;
import core.util.data.GSEnumDataType;

/**
 * Encapsulate two bounded number, i.e. low and higher bound
 * <p>
 * Referent {@link RangeSpace} provide a {@link GSRangeTemplate} to transpose input string into {@link RangeValue}
 *
 * @author kevinchapuis
 *
 */
public class RangeValue implements IValue {

	/**
	 * The Enum RangeBound.
	 */
	public enum RangeBound {

		/** The lower. */
		LOWER,
		/** The upper. */
		UPPER
	}

	/** The top bound. */
	private Number bottomBound;

	/** The top bound. */
	private Number topBound;

	/** The rs. */
	private final RangeSpace rs;

	/**
	 * as the String value if long to construct and often used, we cache it here
	 */
	private String stringValueCached = null;

	/**
	 * The actual value of a Range might be any object: integer, string, etc.
	 */
	private Object actualValue = null;

	/**
	 * a) When it's lower bound then max is used to setup other part of the range value
	 * <p>
	 * b) When it's upper bound then min is used to setup other part of the range value
	 *
	 * @param rs
	 * @param bound
	 * @param defaultBound
	 */
	protected RangeValue(final RangeSpace rs, final Number bound, final RangeBound defaultBound) {
		this.rs = rs;
		switch (defaultBound) {
			case UPPER:
				this.bottomBound = bound;
				this.topBound = rs.getMax();
				break;
			case LOWER:
				this.topBound = bound;
				this.bottomBound = rs.getMin();
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Instantiates a new range value.
	 *
	 * @param rs
	 *            the rs
	 * @param lowerBound
	 *            the lower bound
	 * @param upperbound
	 *            the upperbound
	 */
	protected RangeValue(final RangeSpace rs, final Number lowerBound, final Number upperbound) {
		this.bottomBound = lowerBound;
		this.topBound = upperbound;
		this.rs = rs;
	}

	// ----------------------------------------- //

	@Override
	public GSEnumDataType getType() { return GSEnumDataType.Range; }

	/**
	 * Compute string value.
	 *
	 * @return the string
	 */
	protected String computeStringValue() {
		if (topBound.equals(this.rs.getMax())) return rs.getRangeTemplate().getTopTemplate(bottomBound);
		if (bottomBound.equals(this.rs.getMin())) return rs.getRangeTemplate().getBottomTemplate(topBound);
		return rs.getRangeTemplate().getMiddleTemplate(bottomBound, topBound);
	}

	@Override
	public final String getStringValue() {
		if (stringValueCached == null) { stringValueCached = computeStringValue(); }
		return stringValueCached;
	}

	@Override
	public RangeSpace getValueSpace() { return rs; }

	/**
	 * The actual encapsulated value
	 *
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	@JsonIgnore
	@Override
	public Object getActualValue() { return this.actualValue; }

	/**
	 * Get the lower bound of the range value
	 *
	 * @return
	 */
	public Number getBottomBound() { return bottomBound; }

	/**
	 * Get the upper bound of the range value
	 *
	 * @return
	 */
	public Number getTopBound() { return topBound; }

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

	/**
	 * Sets the bottom bound.
	 *
	 * @param bottomBound
	 *            the new bottom bound
	 */
	public void setBottomBound(final Number bottomBound) { this.bottomBound = bottomBound; }

	/**
	 * Sets the top bound.
	 *
	 * @param topBound
	 *            the new top bound
	 */
	public void setTopBound(final Number topBound) { this.topBound = topBound; }

}
