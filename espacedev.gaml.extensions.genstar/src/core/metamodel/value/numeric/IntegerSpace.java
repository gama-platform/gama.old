package core.metamodel.value.numeric;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;

/**
 * TODO: javadoc
 * 
 * @author kevinchapuis
 *
 */
public class IntegerSpace implements IValueSpace<IntegerValue> {

	private static GSDataParser gsdp = new GSDataParser();

	private IntegerValue emptyValue;
	private TreeMap<Integer, IntegerValue> values;
	private Set<String> excludedValues;

	private int min, max;

	private IAttribute<IntegerValue> attribute;

	public IntegerSpace(IAttribute<IntegerValue> attribute){
		this(attribute, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public IntegerSpace(IAttribute<IntegerValue> attribute, Integer min, Integer max) {
		this.values = new TreeMap<>();
		this.excludedValues = new HashSet<>();
		this.emptyValue = new IntegerValue(this);
		this.attribute = attribute;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public IntegerValue getInstanceValue(String value) {
		int currentVal = gsdp.parseNumbers(value).intValue();
		if(currentVal < min || currentVal > max)
			throw new IllegalArgumentException("Proposed value "+value+" is "
					+ (currentVal < min ? "below" : "beyond") + " given bound ("
					+ (currentVal < min ? min : max) + ")");
		return new IntegerValue(this, currentVal);
	}
	
	@Override
	public IntegerValue proposeValue(String value) {
		return new IntegerValue(this, gsdp.parseNumbers(value).intValue());
	}

	// -------------------- SETTERS & GETTER CAPACITIES -------------------- //

	/**
	 * {@inheritDoc}
	 * \p
	 * By default integer value addition is not stored, make use of {@link #getInstanceValue(String)}
	 */
	@Override
	public IntegerValue addValue(String value) {
		if(excludedValues.contains(value))
			return this.getEmptyValue();
		IntegerValue iv = getValue(value);
		if(iv == null) {
			iv = this.getInstanceValue(value);
			values.put(iv.getActualValue(), iv);
		}
		return iv;
	}

	@Override
	public IntegerValue getValue(String value) throws NullPointerException {
		return values.get(gsdp.parseNumbers(value).intValue());
	}

	@Override
	public Set<IntegerValue> getValues(){
		return new HashSet<>(values.values());
	}
	
	@Override
	public boolean contains(IValue value) {
		if(!value.getClass().equals(IntegerValue.class))
			return false;
		return values.containsValue(value);
	}

	@Override
	public IntegerValue getEmptyValue() {
		return emptyValue;
	}

	@Override
	public void setEmptyValue(String value) {
		try {
			this.emptyValue = getValue(value);
		} catch (NullPointerException npe) {
			try {
				this.emptyValue = new IntegerValue(this, gsdp.getDouble(value).intValue());
			} catch (Exception e) {
				// If value is not a parsable integer or null just leave the
				// default value as it is 
			}
		}
	}
	
	@Override
	public void addExceludedValue(String value) {
		this.excludedValues.add(value);
	}

	@Override
	public boolean isValidCandidate(String value){
		if(!gsdp.getValueType(value).isNumericValue() 
				|| gsdp.parseNumbers(value).intValue() < min 
				|| gsdp.parseNumbers(value).intValue() > max)
			return false;
		return true;
	}

	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Integer;
	}
	
	@Override
	public Class<IntegerValue> getTypeClass() {
		return IntegerValue.class;
	}

	@Override
	public IAttribute<IntegerValue> getAttribute() {
		return attribute;
	}

	// ----------------------------------------------------- //

	@JsonProperty("max")
	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@JsonProperty("min")
	public int getMin()	{
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	// ----------------------------------------------- //

	@Override
	public int hashCode() {
		return this.getHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return isEqual(obj);
	}
	
	@Override
	public String toString() {
		return this.toPrettyString();
	}
	
	@Override
	public boolean contains(String valueStr) {
		return values.values().stream().anyMatch(v -> v.getStringValue().equals(valueStr));
	}
	
	@Override
	public boolean containsAllLabels(Collection<String> valuesStr) {
		return this.values.values()
				.stream()
				.allMatch(val -> valuesStr.contains(val.getStringValue()));
	}

	@Override
	public IValueSpace<IntegerValue> clone(IAttribute<IntegerValue> newReferent) {
		return new IntegerSpace(newReferent, getMin(), getMax());
	}


}
