package core.metamodel.value.numeric;

import com.fasterxml.jackson.annotation.JsonIgnore;

import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * A value that encapsulate an {@link Integer} and extend {@link IValue} contract
 * 
 * @author kevinchapuis
 *
 */
public class IntegerValue implements IValue {

	private final Integer value;
	
	private IntegerSpace is;
	
	protected IntegerValue(IntegerSpace is){
		this.is = is;
		this.value = null;
	}
	
	protected IntegerValue(IntegerSpace is, int value){
		this.is = is;
		this.value = value;
	}
	
	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Integer;
	}

	@Override
	public String getStringValue() {
		return String.valueOf(value);
	}

	@Override
	public IntegerSpace getValueSpace() {
		return is;
	}
	
	/**
	 * The actual encapsulated value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@JsonIgnore
	@Override
	public Integer getActualValue(){
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
		throw new UnsupportedOperationException("cannot change the actual value of an Integer attribute, it will always be the corresponding Integer value");
	}

	
	
}
