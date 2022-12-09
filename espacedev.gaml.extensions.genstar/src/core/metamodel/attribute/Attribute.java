/*******************************************************************************************************
 *
 * Attribute.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.attribute;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.mapper.value.EncodedValueMapper;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSEnumDataType;

/**
 * The abstract expression of attribute's entity population schema. This schema is defined to be the same for all entity
 * in a given population, and can be described as:
 * <p>
 * <ul>
 * <li><b>Name</b> = a unique and characterized name, beware it will drive data case sensitive automatic reading, hence
 * it should have exactly the same form as in data file
 * <li><b>Values</b> = the set of all possible values, concrete entities can get for this attribute. Apart from possible
 * values, most attribute can have an empty value, refereed as {@code emptyValue}
 * <li><b>Data type</b> = a {@link GSEnumDataType} that gives information on value's content type
 * <li><b>Referent attribute</b> = an attribute this one referees to. That means values of the first are explicitly bind
 * to values of the second
 * </ul>
 * <p>
 * {@link Attribute} are defined at population level, where all entity should have a value for each.
 *
 * @author kevinchapuis
 * @author Duc an vo
 */

@JsonTypeName (Attribute.SELF)
public class Attribute<V extends IValue> implements IAttribute<V> {

	/** The Constant SELF. */
	public static final String SELF = "ATTRIBUTE";

	/** The value space. */
	private IValueSpace<V> valueSpace;
	
	/** The encoded values. */
	private EncodedValueMapper<V> encodedValues;

	/** The name. */
	private final String name;

	/** The description. */
	@JsonIgnore private String description = null;

	/**
	 * Instantiates a new attribute.
	 *
	 * @param name the name
	 */
	protected Attribute(final String name) {
		this.name = name;
	}

	// ----------------- IAttribute contract methods ----------------- //

	@Override
	public final String getAttributeName() { return name; }

	@Override
	public IValueSpace<V> getValueSpace() { return valueSpace; }

	@Override
	public void setValueSpace(final IValueSpace<V> valueSpace) { this.valueSpace = valueSpace; }

	@Override
	public EncodedValueMapper<V> getEncodedValueMapper() { return encodedValues; }

	@Override
	public void setEncodedValueMapper(final EncodedValueMapper<V> encodedMapper) { this.encodedValues = encodedMapper; }

	// --------------------------------------------------------------- //

	/**
	 * Retrieve the natural language description of this attribute
	 *
	 * @return
	 */
	@Override
	public String getDescription() { return description; }

	/**
	 * To give natural description for this attribute
	 *
	 * @param description
	 */
	public void setDescription(final String description) { this.description = description; }

	/**
	 * Add new encoded form for value
	 *
	 * @param value
	 * @param records
	 */
	public void addRecords(final String value, final String... records) {
		if (encodedValues == null) { this.setEncodedValueMapper(new EncodedValueMapper<>(valueSpace)); }
		this.encodedValues.putMapping(value, records);
	}

	/**
	 * The {@link Attribute} this attribute targets: should be itself, but could indicate disagregated
	 * {@link Attribute}, i.e. the same attribute but with other information about values
	 *
	 * @return
	 */
	@JsonIgnore
	public Attribute<? extends IValue> getReferentAttribute() { return this; }

	/**
	 * Tests if this attribute is linked to the one passed as argument. It returns true if one of these are true:
	 * <p>
	 * <ul>
	 * <li>{@code this.equals(attribute)}
	 * <li>{@code this.equals(attribute.getReferentAttribute())}
	 * <li>{@code this.getReferentAttribute().equals(attribute)}
	 * <li>{@code this.getReferentAttribute().equals(attribute.getReferentAttribute())}
	 * </ul>
	 * <p>
	 * 
	 * TODO: see if it can be removed
	 * 
	 * @param attribute
	 * @return
	 */
	public boolean isLinked(final Attribute<? extends IValue> attribute) {
		if (this.equals(attribute) || this.equals(attribute.getReferentAttribute())
				|| this.getReferentAttribute().equals(attribute)
				|| this.getReferentAttribute().equals(attribute.getReferentAttribute()))
			return true;
		return false;
	}

	/**
	 * Return the empty value
	 *
	 * @return
	 */
	@JsonIgnore
	public IValue getEmptyValue() { return valueSpace.getEmptyValue(); }

	/**
	 * Find any related value. Could be the value itself or any corresponding mapped values. When no mapping have been
	 * define there is 2 options (e.g. A and B are the two mapped attribute):
	 * <p>
	 * <ul>
	 * <li>the value exist for attribute A (B): then attribute B (A) empty value is return
	 * <li>the value does not exist in A nor B: a {@link NullPointerException} is raised
	 * </ul>
	 * <p>
	 *
	 * @param value
	 * @return
	 */
	public Collection<? extends IValue> findMappedAttributeValues(final IValue value) {
		if (this.getValueSpace().contains(value) || this.getValueSpace().isValidCandidate(value.getStringValue()))
			return Collections.singleton(value);
		if (encodedValues.getRecords().contains(value))
			return Collections.singleton(encodedValues.getRelatedValue(value));
		throw new NullPointerException();
	}

	/**
	 * Says if this attribute is an implementation of an {@link EmergentAttribute}
	 *
	 * @return true if it is an emergent attribute, false otherwise
	 */
	public boolean isEmergent() { return false; }

	////////////////////////////////////////////////////////////////
	// ------------------------- UTILITY ------------------------ //
	////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return name + " (" + this.getValueSpace().getType() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (description == null ? 0 : description.hashCode());
		return prime * result + this.getHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return this.isEqual(obj);
	}

}
