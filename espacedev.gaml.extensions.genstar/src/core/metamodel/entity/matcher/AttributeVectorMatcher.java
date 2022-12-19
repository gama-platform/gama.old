/*******************************************************************************************************
 *
 * AttributeVectorMatcher.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.metamodel.entity.matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;
import core.util.GSDisplayUtil;
import core.util.exception.GenstarException;

/**
 * A vector of attribute with binded set of values and several utility methods to asses comparison with other entities
 * made of attribute and value
 *
 * @author kevinchapuis
 *
 */
@JsonTypeName (AttributeVectorMatcher.SELF)
public class AttributeVectorMatcher implements IGSEntityMatcher<IValue> {

	/** The Constant SELF. */
	public static final String SELF = "VECTOR VALUE MATCHER";

	/** The Constant ATT_SEPRATOR. */
	public static final String ATT_SEPRATOR = " : ";
	
	/** The Constant VAL_SEPRATOR. */
	public static final CharSequence VAL_SEPRATOR = ",";

	/** The vector. */
	Map<IAttribute<? extends IValue>, Set<IValue>> vector;

	/**
	 * Instantiates a new attribute vector matcher.
	 */
	public AttributeVectorMatcher() {
		this.vector = new HashMap<>();
	}

	/**
	 * Instantiates a new attribute vector matcher.
	 *
	 * @param entity the entity
	 */
	public AttributeVectorMatcher(final IEntity<Attribute<? extends IValue>> entity) {
		this.vector = entity.getAttributeMap().entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> new HashSet<>(Arrays.asList(e.getValue()))));
	}

	/**
	 * Instantiates a new attribute vector matcher.
	 *
	 * @param vector the vector
	 */
	public AttributeVectorMatcher(final Map<IAttribute<? extends IValue>, Set<IValue>> vector) {
		this.vector = vector;
	}

	/**
	 * Instantiates a new attribute vector matcher.
	 *
	 * @param vector the vector
	 */
	public AttributeVectorMatcher(final IValue... vector) {
		this();
		this.addMatchToVector(vector);
	}

	// -------------------------------------------//

	/**
	 * If this attribute vector contains given value. Relies on {@link Collection#contains(Object)} implementation.
	 *
	 * @param value
	 * @return
	 */
	@Override
	public boolean valueMatch(final IValue value) {
		return this.getVector().contains(value);
	}

	/**
	 * If this attribute vector contains all values provided in the given collection. Relies on
	 * {@link Collection#containsAll(Collection)} implementation.
	 *
	 * @param values
	 * @return
	 */
	@Override
	public boolean valuesMatch(final Collection<? extends IValue> values) {
		return this.getVector().containsAll(values);
	}

	/**
	 * If the attribute vector contains each value that characterize entity vector value. Relies on
	 * {@link Collection#contains(Object)} implementation.
	 *
	 * @param entity
	 * @return
	 */
	@Override
	public boolean entityMatch(final IEntity<? extends IAttribute<? extends IValue>> entity, final MatchType type) {
		return switch (type) {
			case ALL -> this.valuesMatch(entity.getValues());
			case ANY -> entity.getValues().stream().anyMatch(this::valueMatch);
			case NONE -> entity.getValues().stream().noneMatch(this::valueMatch);
			default -> throw new GenstarException();
		};
	}

	/**
	 * Return the Hamming distance between this attribute vector and given entity. It relies on entity level value
	 * vector contract, meaning if the attribute as several value for one attribute, the Hamming distance will be 0 is
	 * none are present in the entity and 1 if at least one is actually in the entity value vector
	 *
	 * @param entity
	 * @return
	 */
	@Override
	public int getHammingDistance(final IEntity<? extends IAttribute<? extends IValue>> entity) {
		return (int) entity.getAttributes().stream().filter(a -> vector.containsKey(a)
				&& vector.get(a).contains(entity.getValueForAttribute(a.getAttributeName()))).count();
	}

	// SETTER

	/**
	 * Add new value match to this attribute vector
	 *
	 * @param matches
	 */
	@Override
	public void addMatchToVector(final IValue... matches) {
		vector.putAll(Stream.of(matches)
				.collect(Collectors.groupingBy(v -> v.getValueSpace().getAttribute(), Collectors.toSet())));
	}

	// -------------------------------------------//

	@Override
	public String toString() {
		return GSDisplayUtil.prettyPrint(vector.values(), ";");
	}

	@Override
	public void setVector(final Collection<IValue> vector) {
		this.addMatchToVector(vector.toArray(new IValue[vector.size()]));
	}

	@Override
	public Collection<IValue> getVector() {
		return vector.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
	}

	/**
	 * Gets the map vector.
	 *
	 * @return the map vector
	 */
	public Map<IAttribute<? extends IValue>, Set<IValue>> getMapVector() { return Collections.unmodifiableMap(vector); }

}
