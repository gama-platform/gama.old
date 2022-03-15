package core.metamodel.value.binary;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSEnumDataType;

/**
 * Value space of boolean value
 * 
 * @author kevinchapuis
 *
 */
public class BinarySpace implements IValueSpace<BooleanValue> {
	
	private Set<BooleanValue> values;
	private BooleanValue emptyValue;

	private IAttribute<BooleanValue> attribute;
	
	@JsonIgnore
	public final BooleanValue valueTrue;
	@JsonIgnore
	public final BooleanValue valueFalse;
	
	/**
	 * Constraint bianry space constructor define with values: {@link Boolean#TRUE}, {@link Boolean#FALSE}
	 * and a null {@link Boolean} as empty value
	 * 
	 * @param attribute
	 */
	public BinarySpace(IAttribute<BooleanValue> attribute){
		this.attribute = attribute;
		valueTrue = new BooleanValue(this, true);
		valueFalse = new BooleanValue(this, false);
		this.values = Stream.of(valueTrue, valueFalse)
				.collect(Collectors.toSet());
		this.emptyValue = new BooleanValue(this, null);
	}
	
	// ---------------------------------------------------------------------- //

	@Override
	public BooleanValue getInstanceValue(String value) {
		return this.getValue(value);
	}
	
	@Override
	public BooleanValue proposeValue(String value) {
		return this.getValue(value);
	}
	
	@Override
	public BooleanValue addValue(String value) throws IllegalArgumentException {
		return this.getValue(value);
	}

	@Override
	public BooleanValue getValue(String value) throws NullPointerException {
		if(!isValidCandidate(value))
			throw new NullPointerException("The string value "+value
					+" cannot be resolve to boolean as defined by "+this.getClass().getSimpleName());
		return values.stream().filter(val -> val.getStringValue().equalsIgnoreCase(value)).findFirst().get();
	}
	
	@Override
	public Set<BooleanValue> getValues(){
		return Collections.unmodifiableSet(values);
	}
	
	@Override
	public boolean contains(IValue value) {
		return values.contains(value);
	}
	
	@Override
	public IAttribute<BooleanValue> getAttribute() {
		return attribute;
	}
	
	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Boolean;
	}
	
	@Override
	public Class<BooleanValue> getTypeClass() {
		return BooleanValue.class;
	}
	
	@Override
	public BooleanValue getEmptyValue() {
		return emptyValue;
	}
	
	@Override
	public void setEmptyValue(String value){
		// JUST DONT DO THAT
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isValidCandidate(String value) {
		if(!value.equalsIgnoreCase(Boolean.TRUE.toString()) 
				|| !value.equalsIgnoreCase(Boolean.FALSE.toString())
				|| emptyValue.getStringValue().equalsIgnoreCase(value))
			return true;
		return false;
	}
	
	// ---------------------------------------------- //
	
	@Override
	public int hashCode() {
		return this.getHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.isEqual(obj);
	}
	
	@Override
	public String toString() {
		return this.toPrettyString();
	}

	@Override
	public boolean contains(String valueStr) {
		return false;
	}

	@Override
	public boolean containsAllLabels(Collection<String> valuesStr) {
		return this.values
				.stream()
				.allMatch(val -> valuesStr.contains(val.getStringValue()));
	}

	@Override
	public IValueSpace<BooleanValue> clone(IAttribute<BooleanValue> newReferent) {
		return new BinarySpace(newReferent);
	}

	@Override
	public void addExceludedValue(String string) {
		// TODO Auto-generated method stub
		
	}

	
}
