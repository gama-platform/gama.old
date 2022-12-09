/*******************************************************************************************************
 *
 * IGenstarDictionary.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.configuration.dictionary;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.record.RecordAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * Encapsulate description of attributes that will characterized synthetic population entities and help to parse input
 * data
 *
 *
 * To read such a dictionnary from file, refer to ReadINSEEDictionaryUtils in the gospl package.
 *
 * @author Kevin Chapuis
 * @author Samuel Thiriot
 *
 * @param <A>
 */
@JsonTypeInfo (
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes ({ @JsonSubTypes.Type (
		value = AttributeDictionary.class) })
public interface IGenstarDictionary<A extends IAttribute<? extends IValue>> {

	/** The self label. */
	String SELF_LABEL = "DICTIONARY";

	/** The attributes label. */
	String ATTRIBUTES_LABEL = "ATTRIBUTES";

	/** The records label. */
	String RECORDS_LABEL = "RECORDS";

	/** The size label. */
	String SIZE_LABEL = "SIZE ATTRIBUTE";

	/** The weight label. */
	String WEIGHT_LABEL = "WEIGHT ATTRIBUTE NAME";

	/** The serial label. */
	String SERIAL_LABEL = "SERIAL ATTRIBUTE NAME";

	/** The level label. */
	String LEVEL_LABEL = "LAYER LEVEL";

	/**
	 * Retrieves meaningful attributes describe by this dictionary
	 *
	 * @see #getRecords()
	 *
	 * @return
	 */
	@JsonProperty (IGenstarDictionary.ATTRIBUTES_LABEL)
	Collection<A> getAttributes();

	/**
	 * Gets the attribute and record.
	 *
	 * @return the attribute and record
	 */
	@JsonIgnore
	Collection<IAttribute<? extends IValue>> getAttributeAndRecord();

	/**
	 * Access to attribute using attribute name define as {@link IAttribute#getAttributeName()}
	 *
	 * @param string
	 * @return
	 */
	A getAttribute(String attribute);

	/**
	 * Access to value
	 *
	 * @param value
	 * @return
	 */
	IValue getValue(String value);

	/**
	 * true if this dictionary contains an attribute associated to the attribute name passed as argument; false
	 * otherwise
	 *
	 * @param name
	 * @return
	 */
	boolean containsAttribute(String name);

	/**
	 * true if this dictionary contains a record attribute associated to attribute name passed as argument; false
	 * otherwise
	 *
	 * @param name
	 * @return
	 */
	boolean containsRecord(String name);

	/**
	 * returns true if one of the attributes of the dictionnary has any space containing any value corresponding to this
	 * value
	 *
	 * @param s
	 * @return
	 */
	boolean containsValue(String valueStr);

	// ---------------------- ADD & SET

	/**
	 * Adds the attributes.
	 *
	 * @param attributes
	 *            the attributes
	 * @return the i genstar dictionary
	 */
	@SuppressWarnings ("unchecked")
	IGenstarDictionary<A> addAttributes(A... attributes);

	/**
	 * Adds the attributes.
	 *
	 * @param attributes
	 *            the attributes
	 * @return the i genstar dictionary
	 */
	IGenstarDictionary<A> addAttributes(Collection<A> attributes);

	// ------------------------ RECORDS

	/**
	 * Retrives record attributes contain in this dictionary. Record should only describe brut contingency
	 *
	 * @return
	 */
	@JsonProperty (IGenstarDictionary.RECORDS_LABEL)
	Collection<RecordAttribute<A, A>> getRecords();

	/**
	 * Add record attributes to this dictionary.
	 *
	 * @param records
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	IGenstarDictionary<A> addRecords(RecordAttribute<A, A>... records);

	// ------------------------ SPECIAL ATTRIBUTE FOR SAMPLE DICTIONARY

	/**
	 *
	 * @return string name of weight attribute
	 */
	String getWeightAttributeName();

	/**
	 *
	 * @param weightAttribute
	 */
	void setWeightAttributeName(String weigthAttribute);

	/**
	 *
	 * @return string name of
	 */
	String getIdentifierAttributeName();

	/**
	 *
	 * @param identifierAttribute
	 */
	void setIdentifierAttributeName(String identifierAttribute);

	// ------------------------ SIZE

	/**
	 * Gets the size attribute.
	 *
	 * @return the size attribute
	 */
	@JsonProperty (IGenstarDictionary.SIZE_LABEL)
	EmergentAttribute<? extends IValue, Collection<IEntity<? extends IAttribute<? extends IValue>>>, ?>
			getSizeAttribute();

	// ------------------------ LEVEL LAYER

	/**
	 * By default set to 0 value
	 *
	 * @return
	 */
	int getLevel();

	/**
	 * Set the layer of this dictionary
	 *
	 * @param level
	 */
	void setLevel(int level);

	// ----------- UTILITIES

	/**
	 * returns the count of attributes
	 *
	 * @return
	 */
	int size();

	/**
	 * add the dictionary passed as parameter to this one and returns a novel dictionary which contains all their
	 * attributes.
	 *
	 * @param dictionnary
	 * @return
	 */
	IGenstarDictionary<A> merge(IGenstarDictionary<A> dictionnary);

}
