package core.metamodel.attribute.mapper;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.attribute.AttributeMapperSerializer;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.MappedAttribute;
import core.metamodel.value.IValue;

/**
 * Encapsulate mapper process: it binds one or several values of a related attribute to
 * one or several values of referent attribute. Relationship between attributes could be
 * of one-to-one value (OTO) also called Record, one-to-several values (OTS) also called
 * Aggregate and several-to-several (STS) also called undirected.
 * <p>
 * By default {@link #add(IValue, IValue)} method keep track of insertion order
 * 
 * TODO: could be more generic and directly be related to {@link IAttribute}
 * 
 * @author kevinchapuis
 *
 * @param <K> value type of the related attribute
 * @param <V> value type of the referent attribute
 */
@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME,
	      include = JsonTypeInfo.As.PROPERTY,
	      property = IAttributeMapper.TYPE
	      )
@JsonSubTypes({
    @JsonSubTypes.Type(value = RecordMapper.class, name = IAttributeMapper.REC),
    @JsonSubTypes.Type(value = AggregateMapper.class, name = IAttributeMapper.AGG),
    @JsonSubTypes.Type(value = UndirectedMapper.class, name = IAttributeMapper.UND)
})
@JsonSerialize(using = AttributeMapperSerializer.class)
public interface IAttributeMapper<K extends IValue, V extends IValue> {

	public static final String TYPE = "TYPE";
	
	public static final String REC = "REC";
	public static final String AGG = "AGG";
	public static final String UND = "UND";

	public static final String THE_MAP = "THE MAP";
	
	/**
	 * Add a pair of self value to referent value
	 * 
	 * @param mapTo
	 * @param mapWith
	 */
	public boolean add(K mapTo, V mapWith);

	/**
	 * Retrieve all values that are mapped to value passed as argument
	 * 
	 * @see Attribute#findMappedAttributeValues(IValue)
	 * 
	 * @param value
	 * @return
	 */
	public Collection<? extends IValue> getMappedValues(IValue value);
	
	// ----------------- GETTER & SETTER ----------------- //

	public void setRelatedAttribute(MappedAttribute<K, V> relatedAttribute);
	
	public MappedAttribute<K, V> getRelatedAttribute();

	public Map<Collection<K>, Collection<V>> getRawMapper();
	
}
