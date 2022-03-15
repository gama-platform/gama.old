package core.metamodel.attribute.mapper.value;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.attribute.value.RecordValueSerializer;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.template.GSCategoricTemplate;

/**
 * Enable the use of several string form for any {@link IValue}, those are called record and are 
 * encoded value. They are linked to one value of parametric type K that extends {@link IValue}. On the
 * contrary, one value can have several encoded form. 
 * 
 * @author kevinchapuis
 *
 * @param <K>
 */
@JsonTypeName(EncodedValueMapper.SELF)
@JsonSerialize(using = RecordValueSerializer.class)
public class EncodedValueMapper<K extends IValue> {
	
	public static final String SELF = "ENCODED VALUES";
	public static final String MAPPING = "MAPPING";
	
	private static final String ATTRIBUTE_NAME = "ENCODE ATT";
	
	private IValueSpace<K> values;
	private Map<NominalValue,K> mapper;
	
	private Attribute<NominalValue> self;
	
	/**
	 * Default constructor
	 */
	public EncodedValueMapper(IValueSpace<K> values) {
		this.values = values;
		this.mapper = new HashMap<>();
		this.self = AttributeFactory.getFactory().createNominalAttribute(ATTRIBUTE_NAME, new GSCategoricTemplate());
	}
	
	/**
	 * Constructor that add mapped record
	 * @param mapper
	 */
	public EncodedValueMapper(IValueSpace<K> values, Map<String,K> mapper){
		this(values);
		for(Entry<String, K> entry : mapper.entrySet()) {
			this.putMapping(entry.getValue(), entry.getKey());
		}
	}
	
	/**
	 * Add new record(s) to encode K value given in parameter
	 * 
	 * @param value
	 * @param records
	 */
	public void putMapping(K value, String... records) {
		for(String record : records) {
			this.mapper.put(this.self.getValueSpace().addValue(record), value);
		}
	}
	
	/**
	 * Add new record(s) to encoded string value given in parameter. 
	 * 
	 * @param value
	 * @param records
	 * @return true if the records have been record, false otherwise
	 */
	public boolean putMapping(String value, String... records) {
		K val = null;
		try {
			val = values.getValue(value);
		} catch (NullPointerException e) {
			return false;
		}
		for(String record : records) {
			this.mapper.put(this.self.getValueSpace().addValue(record), val);
		}
		return true;
	}
	
	/**
	 * Get the related K value associated to a particular {@link IValue} record
	 * @param record
	 * @return
	 */
	public K getRelatedValue(IValue record) {
		return mapper.get(record);
	}
	
	/**
	 * Get the related K value associated to a particular String record
	 * @param record
	 * @return
	 */
	public K getRelatedValue(String record) {
		return mapper.get(this.self.getValueSpace().getValue(record));
	}
	
	/**
	 * Retrieve all possible String form of a {@link IValue} 
	 * @param value
	 * @return
	 */
	public Collection<NominalValue> getRecords(K value){
		return mapper.keySet().stream().filter(k -> mapper.get(k).equals(value))
				.collect(Collectors.toSet());
	}
	
	/**
	 * Retrieve all possible String encoded form of the String value passed in argument
	 * @param value
	 * @return
	 */
	public Collection<NominalValue> getRecords(String value){
		K val = values.getValue(value);
		return mapper.keySet().stream().filter(k -> mapper.get(k).equals(val))
				.collect(Collectors.toSet());
	}
	
	/**
	 * Returns all the encoded values, called records
	 * @return
	 */
	public Collection<NominalValue> getRecords(){
		return Collections.unmodifiableSet(mapper.keySet());
	} 
	
	/**
	 * Check if this mapper has a reference to any given value either as record (NominalValue) or value (K)
	 * @param value
	 * @return true if this mapper has a reference to this string value
	 */
	public boolean hasValueOrRecord(String value) {
		return mapper.entrySet().stream().anyMatch(entry -> 
				entry.getKey().getStringValue().equals(value) 
			|| entry.getValue().getStringValue().equals(value));
	}
	
	/**
	 * Simply map this value with corresponding record (if value of type K) or value (if record of type NominalValue)
	 * WARNING : if the argument is a value (of type K) and that this value has several encoded records, then the methods
	 * can return unconsistant results (choose one record at random)
	 * @param value
	 * @return
	 */
	public IValue transpose(IValue value) {
		if(!hasValueOrRecord(value.getStringValue())) {
			throw new NullPointerException("There is not any mapping for "+value.getStringValue());
		}
		return mapper.keySet().contains(value) ? 
				this.getRecords(value.getStringValue()).stream().findAny().get() :
					getRelatedValue(value.getStringValue());
	}
	
}
