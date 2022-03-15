package core.metamodel.attribute;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.mapper.IAttributeMapper;
import core.metamodel.value.IValue;

/**
 * Mapped attribute contains:
 * <p>
 * 1 - must point to a referent attribute <br>
 * 2 - a mapping between self and referent values
 * <p>
 * The mapping is delegated to {@link IAttributeMapper}
 * 
 * @author kevinchapuis
 *
 * @param <K> value type associated with this attribute
 * @param <V> value type associated with referent attribute
 */
@JsonTypeName(MappedAttribute.SELF)
public class MappedAttribute<K extends IValue, V extends IValue> extends Attribute<K> {
	
	public static final String SELF = "MAPPED ATTRIBUTE";
	
	public static final String REF = "REFERENT ATTRIBUTE";
	public static final String MAP = "MAPPER";
	
	@JsonProperty(MappedAttribute.REF)
	private Attribute<V> referentAttribute;
	
	private IAttributeMapper<K, V> attributeMapper;

	protected MappedAttribute(String name, Attribute<V> referentAttribute,
			IAttributeMapper<K, V> attributeMapper) {
		super(name);
		this.referentAttribute = referentAttribute;
		this.attributeMapper = attributeMapper;
	}
	
	// ------------------------------------------------------------------ //
	
	@Override
	public boolean isLinked(Attribute<? extends IValue> attribute){
		return attribute.equals(referentAttribute);	
	}
	
	@Override
	public Attribute<V> getReferentAttribute(){
		return referentAttribute;
	}
	
	protected void setReferentAttribute(Attribute<V> referent) {
		this.referentAttribute = referent;
	}
	
	@Override
	public Collection<? extends IValue> findMappedAttributeValues(IValue value){
		try {
			return attributeMapper.getMappedValues(value);
		} catch (NullPointerException e) {
			if(value==null) {throw e;}
			if(getEncodedValueMapper()!=null && getEncodedValueMapper().hasValueOrRecord(value.getStringValue())) {
				IValue rec = getEncodedValueMapper().getRelatedValue(value.getStringValue());
				try { return attributeMapper.getMappedValues(rec);
				} catch (NullPointerException e2) { }
			}
			if(referentAttribute.getEncodedValueMapper()!=null &&
					referentAttribute.getEncodedValueMapper().hasValueOrRecord(value.getStringValue())) {
				IValue rec = referentAttribute.getEncodedValueMapper().transpose(value);
				try { return attributeMapper.getMappedValues(rec);
				} catch (NullPointerException e2) { }
			}
			if(getReferentAttribute().getValueSpace().contains(value))
				return Arrays.asList(this.getEmptyValue());
			if(this.getValueSpace().contains(value))
				return Arrays.asList(this.getReferentAttribute().getEmptyValue());
			if(this.getEmptyValue().equals(value) || 
					getReferentAttribute().getValueSpace().getEmptyValue().equals(value))
				return Arrays.asList(value);
			throw e;
		}
	}
	
	// ------------------------------------------------------------------- //
	
	@JsonProperty(MappedAttribute.MAP)
	public IAttributeMapper<K, V> getAttributeMapper(){
		return attributeMapper;
	}
	
	/**
	 * Add a pair of mapped key / value. Depending on inner {@link IAttributeMapper} implementation
	 * could lead to: 1) Add a new pair 2) Add a value to existing pairwised key / set of value in
	 * aggregated mapper 3) add a key or a value to an existing pair in undirected mapper
	 * 
	 * @param mapTo
	 * @param mapWith
	 * @return
	 */
	public boolean addMappedValue(K mapTo, V mapWith) {
		return this.attributeMapper.add(mapTo, mapWith);
	}
	
	/**
	 * Return associated keys from raw mapper
	 * @param value
	 * @return
	 */
	public Collection<K> getKey(V value){
		Optional<Collection<K>> opt = attributeMapper.getRawMapper().entrySet().stream()
				.filter(entry -> entry.getValue().contains(value))
				.map(entry -> entry.getKey())
				.findAny();
		return opt.isPresent() ? opt.get() : Collections.emptyList();
	}
	
	/**
	 * Return associated values from raw mapper
	 * @param key
	 * @return
	 */
	public Collection<V> getValue(K key){
		Optional<Collection<V>> opt = attributeMapper.getRawMapper().entrySet().stream()
				.filter(entry -> entry.getKey().contains(key))
				.map(entry -> entry.getValue())
				.findAny();
		return opt.isPresent() ? opt.get() : Collections.emptyList();
	}
	
	// ------------------------------------------------------------------- //
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((attributeMapper == null) ? 0 : attributeMapper.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		if(!this.isEqual(obj))
			return false;
		@SuppressWarnings("rawtypes")
		MappedAttribute other = (MappedAttribute) obj;
		if (attributeMapper == null) {
			if (other.attributeMapper != null)
				return false;
		} else if (!attributeMapper.equals(other.attributeMapper))
			return false;
		return true;
	}

}
