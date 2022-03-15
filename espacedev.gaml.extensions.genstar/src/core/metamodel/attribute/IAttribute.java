package core.metamodel.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import core.configuration.jackson.attribute.AttributeDeserializer;
import core.metamodel.attribute.mapper.value.EncodedValueMapper;
import core.metamodel.attribute.record.RecordAttribute;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;

/**
 * Attribute (of for instance an individual or household)
 * 
 * @author gospl-team
 *
 */
@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME,
	      include = JsonTypeInfo.As.WRAPPER_OBJECT
	      )
@JsonSubTypes({
	        @JsonSubTypes.Type(value = Attribute.class),
	        @JsonSubTypes.Type(value = MappedAttribute.class),
	        @JsonSubTypes.Type(value = RecordAttribute.class),
	        @JsonSubTypes.Type(value = EmergentAttribute.class)
	    })
@JsonDeserialize(using = AttributeDeserializer.class)
@JsonPropertyOrder({ IAttribute.NAME, IAttribute.VALUE_SPACE })
public interface IAttribute<V extends IValue> {
	
	public static final String VALUE_SPACE = "VALUE SPACE";
	public static final String ENCODED_MAPPER = "CODE MAPPER";
	public static final String NAME = "NAME";

	/**
	 * The name of the attribute - work as attribute id, so it must be unique
	 * 
	 * @return the name - {@link String}
	 */
	@JsonProperty(NAME)
	public String getAttributeName();
	
	/**
	 * String description of the attribute
	 * @return
	 */
	public String getDescription();
	
// ------------------------- value related methods ------------------------- //


	/**
	 * The theoretical space of value that characterize this attribute
	 * 
	 * @return
	 */
	@JsonProperty(VALUE_SPACE)
	@JsonManagedReference(value = IValueSpace.REF_ATT)
	public IValueSpace<V> getValueSpace();
	
	/**
	 * Set the value space that define value compatibility and the actual value set
	 * 
	 * @param valueSpace
	 */
	public void setValueSpace(IValueSpace<V> valueSpace);
	
	/**
	 * Keep track of encoded form for values. In most census data, there is 
	 * simple code form and full form for values, e.g. 0 = unemployed, 1 = employed
	 * 2 = retired, 9 = NIU
	 * 
	 * @return
	 */
	@JsonProperty(EncodedValueMapper.SELF)
	public EncodedValueMapper<V> getEncodedValueMapper();
	
	/**
	 * Set the encoded value mapper
	 * 
	 * @param encodedMapper
	 */
	@JsonProperty(EncodedValueMapper.SELF)
	public void setEncodedValueMapper(EncodedValueMapper<V> encodedMapper);
	
// -------------------------
	
	/**
	 * Utility method to compute hash code
	 * @return
	 */
	@JsonIgnore
	default int getHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getAttributeName().hashCode();
		result = prime * result + getValueSpace().getHashCode();
		return result;
	}

	/**
	 * Utility method to estimate equality
	 * @param obj
	 * @return
	 */
	default boolean isEqual(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IAttribute<? extends IValue> other = (IAttribute<?>) obj;
		if (getValueSpace() == null) {
			if (other.getValueSpace() != null)
				return false;
		} else if (!getAttributeName().equals(other.getAttributeName()))
			return false;
		return true;
	}
	
}
