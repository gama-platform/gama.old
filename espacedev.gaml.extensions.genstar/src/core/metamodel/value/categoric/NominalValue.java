package core.metamodel.value.categoric;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * Encapsulate nominal {@link String} value
 * 
 * @author kevinchapuis
 *
 */
public class NominalValue implements IValue {
	
	private String value;
	private Object actualValue;
	
	@JsonManagedReference
	private NominalSpace vs;
	
	protected NominalValue(NominalSpace vs, String value){
		this.value = value;
		this.actualValue = value;
		this.vs = vs;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Nominal;
	}
	
	@JsonIgnore
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getActualValue() {
		return (T) this.actualValue;
	}
	
	@Override
	public String getStringValue() {
		return value;
	}
	
	@Override
	public NominalSpace getValueSpace() {
		return vs;
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
