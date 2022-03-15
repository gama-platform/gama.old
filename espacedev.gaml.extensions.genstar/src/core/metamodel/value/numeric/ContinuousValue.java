package core.metamodel.value.numeric;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * Encapsulates a double value
 * 
 * @author kevinchapuis
 *
 */
public class ContinuousValue implements IValue, Comparable<ContinuousValue> {

	private final Double value;

	@JsonManagedReference
	private ContinuousSpace cs;
		
	public ContinuousValue(ContinuousSpace cs, double value) {
		this.cs = cs;
		this.value = value;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Continue;
	}

	@Override
	public String getStringValue() {
		return String.valueOf(value);
	}

	@Override
	public int compareTo(ContinuousValue o) {
		return this.value.compareTo(o.getActualValue());
	}

	@Override
	public ContinuousSpace getValueSpace() {
		return cs;
	}

	/**
	 * The actual encapsulated value
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	@JsonIgnore
	public Double getActualValue(){
		return value;
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
		throw new UnsupportedOperationException("cannot change the actual value of a Continuous attribute, it will always be a Double");
	}

	
	
}
