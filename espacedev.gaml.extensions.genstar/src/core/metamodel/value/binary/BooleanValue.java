package core.metamodel.value.binary;

import com.fasterxml.jackson.annotation.JsonIgnore;

import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSEnumDataType;

/**
 * Boolean value: encapsulate {@link Boolean} which can be null
 * 
 * @author kevinchapuis
 *
 */
public class BooleanValue implements IValue {

	private Boolean value;

	private BinarySpace bs;
		
	protected BooleanValue(
			BinarySpace bs, Boolean value){
		this.bs = bs;
		this.value = value;
	}
	
	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Boolean;
	}

	@Override
	public String getStringValue() {
		return String.valueOf(value);
	}
	
	/**
	 * The actual encapsulated value
	 * @return
	 */
	@JsonIgnore
	@SuppressWarnings("unchecked")
	@Override
	public Boolean getActualValue(){
		return value;
	}
	
	@Override
	public IValueSpace<BooleanValue> getValueSpace() {
		return bs;
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
		throw new UnsupportedOperationException("cannot change the actual value of a Boolean attribute");
	}

}
