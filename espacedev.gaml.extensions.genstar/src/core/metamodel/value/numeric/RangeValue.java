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

	public static enum RangeBound{LOWER,UPPER}
	
	private Number bottomBound, topBound;

	private RangeSpace rs; 
	
	/**
	 * as the String value if long to construct and often used, 
	 * we cache it here
	 */
	public String stringValueCached = null;

	/**
	 * The actual value of a Range might be any object:
	 * integer, string, etc.
	 */
	private Object actualValue = null;
	
	/**
	 * a) When it's lower bound then max is used to setup other part of the range value
	 * <p>
	 * b) When it's upper bound then min is used to setup other part of the range value
	 * @param rs
	 * @param bound
	 * @param defaultBound
	 */
	protected RangeValue(RangeSpace rs, Number bound, RangeBound defaultBound){
		this.rs = rs;
		switch (defaultBound) {
		case UPPER: this.bottomBound = bound; this.topBound = rs.getMax();
			break;
		case LOWER: this.topBound = bound; this.bottomBound = rs.getMin();
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	protected RangeValue(RangeSpace rs, Number lowerBound, Number upperbound){
		this.bottomBound = lowerBound;
		this.topBound = upperbound;
		this.rs = rs;
	}
	
	// ----------------------------------------- //
	
	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Range;
	}

	protected String computeStringValue() {
		if(topBound.equals(this.rs.getMax()))
			return rs.getRangeTemplate().getTopTemplate(bottomBound);
		if(bottomBound.equals(this.rs.getMin()))
			return rs.getRangeTemplate().getBottomTemplate(topBound);
		return rs.getRangeTemplate().getMiddleTemplate(bottomBound, topBound);
	}
	
	@Override
	public final String getStringValue() {
		if (stringValueCached == null)
			stringValueCached = computeStringValue();
		return stringValueCached;
	}
	
	@Override
	public RangeSpace getValueSpace() {
		return rs;
	}

	/**
	 * The actual encapsulated value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@JsonIgnore
	@Override
	public Object getActualValue(){
		return this.actualValue;
	}
	
	/**
	 * Get the lower bound of the range value
	 * @return
	 */
	public Number getBottomBound() {
		return bottomBound;
	}

	/**
	 * Get the upper bound of the range value
	 * @return
	 */
	public Number getTopBound() {
		return topBound;
	}
	
	// ------------------------------------------------------ //

	@Override
	public int hashCode() {
		return this.getHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.isEquals(obj);
	}
	
	@Override
	public String toString() {
		return this.getStringValue();
	}

	@Override
	public <T> void setActualValue(T v) throws UnsupportedOperationException {
		this.actualValue = v;
	}
	
}
