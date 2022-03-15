package core.metamodel.attribute.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.MappedAttribute;
import core.metamodel.attribute.mapper.value.EncodedValueMapper;
import core.metamodel.value.IValue;

/**
 * To be used when two attribute are equivalent while being encoded in two different forms, including aggregation: 
 * one key for several value. 
 * </p>
 * If there is only a different encoding for values of the same attribute, you should use {@link EncodedValueMapper}
 * which are present by default in any attribute {@link Attribute#addRecords(String, String...)}
 * 
 * @author kevinchapuis 
 *
 * @param <K>
 * @param <V>
 */
public class RecordMapper<K extends IValue, V extends IValue> implements IAttributeMapper<K, V> {

	@JsonProperty(IAttributeMapper.THE_MAP)
	private Map<K, V> record;
	
	private MappedAttribute<K,V> relatedAttribute;
	
	public RecordMapper() {
		this.record = new LinkedHashMap<>();
	}
	
	// -------------------- IAttributeMapper contract -------------------- //
	
	@Override
	public boolean add(K mapTo, V mapWith) {
		if(record.containsKey(mapTo))
			throw new IllegalStateException("Trying to erase existing record: ["
					+mapTo.getStringValue()+" => "+record.get(mapTo)+"]");
		record.put(mapTo, mapWith);
		return true;
	}

	@Override
	public Set<? extends IValue> getMappedValues(IValue value) {
		if(!record.containsKey(value) && !record.containsValue(value))
			throw new NullPointerException("The value "+value+" is not part of any known linked attribute ("
				+ this + " || "+relatedAttribute.getReferentAttribute()+ ")");
		return Collections.singleton(record.containsKey(value) ? 
				record.get(value) : 
					record.keySet().stream().filter(key -> record.get(key).equals(value)).findAny().get());
	}
	
	// -------------------- GETTER & SETTER -------------------- //
	
	@Override
	public void setRelatedAttribute(MappedAttribute<K, V> relatedAttribute) {
		this.relatedAttribute = relatedAttribute;
	}
	
	@Override
	public MappedAttribute<K, V> getRelatedAttribute(){
		return this.relatedAttribute;
	}
	
	@Override
	public Map<Collection<K>, Collection<V>> getRawMapper(){
		return record.keySet().stream().collect(Collectors
				.toMap(
						key -> Collections.singleton(key), 
						key -> Collections.singleton(record.get(key))));
	}
	
	// CLASS SPECIFIC
	
	public Map<K, V> getMapper(){
		return Collections.unmodifiableMap(record);
	}
	
	public void setMapper(Map<K, V> record) {
		this.record = record;
	}
	
	public K getKey(V value) {
		Optional<Entry<K,V>> output = this.record.entrySet().stream()
				.filter(entry -> entry.getValue().equals(value)).findFirst();
		if(output.isPresent())
			return output.get().getKey();
		throw new NullPointerException("Not any key for value record "+value);
	}
	
	public K getKey(String value) {
		Optional<V> optValue = this.record.values().stream()
				.filter(v -> v.getStringValue().equals(value)).findFirst();
		if(optValue.isPresent())
			return this.getKey(optValue.get());
		throw new NullPointerException("Not any key for value record "+value);
	}
	
	public V getValue(K key) {
		V output = this.record.getOrDefault(key, null);
		if(output != null)
			return output;
		throw new NullPointerException("Not any value for key record "+key);
	}
	
	public V getValue(String key) {
		Optional<K> optValue = this.record.keySet().stream()
				.filter(k -> k.getStringValue().equals(key)).findFirst();
		if(optValue.isPresent())
			return this.getValue(optValue.get());
		throw new NullPointerException("Not any value for key record "+key);
	}
	
	// ---------------------------------------------------------- //


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((record == null) ? 0 : record.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		RecordMapper other = (RecordMapper) obj;
		if (record == null) {
			if (other.record != null)
				return false;
		} else if (!record.equals(other.record))
			return false;
		return true;
	}

}
