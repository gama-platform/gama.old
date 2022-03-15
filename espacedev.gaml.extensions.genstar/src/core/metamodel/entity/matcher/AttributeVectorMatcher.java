package core.metamodel.entity.matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;
import core.util.GSDisplayUtil;

/**
 * A vector of attribute with binded set of values and several utility methods to asses comparison with other entities made
 * of attribute and value
 * 
 * @author kevinchapuis
 *
 */
@JsonTypeName(AttributeVectorMatcher.SELF)
public class AttributeVectorMatcher implements IGSEntityMatcher<IValue> {

	public static final String SELF = "VECTOR VALUE MATCHER";
	
	public static final String ATT_SEPRATOR = " : ";
	public static final CharSequence VAL_SEPRATOR = ",";
	
	Map<IAttribute<? extends IValue>, Set<IValue>> vector;
	
	public AttributeVectorMatcher() {
		this.vector = new HashMap<>();
	}
	
	public AttributeVectorMatcher(IEntity<Attribute<? extends IValue>> entity) {
		this.vector = entity.getAttributeMap().entrySet().stream()
				.collect(Collectors.toMap(
						e -> e.getKey(), 
						e -> new HashSet<IValue>(Arrays.asList(e.getValue()))
						));
	}
	
	public AttributeVectorMatcher(Map<IAttribute<? extends IValue>, Set<IValue>> vector) {
		this.vector = vector;
	}
	
	
	public AttributeVectorMatcher(IValue... vector) {
		this();
		this.addMatchToVector(vector);
	}
	
	//-------------------------------------------//
	
	/**
	 * If this attribute vector contains given value. Relies on {@link Collection#contains(Object)} implementation.
	 * @param value
	 * @return
	 */
	@Override
	public boolean valueMatch(IValue value) {
		return this.getVector().contains(value);
	}
	
	/**
	 * If this attribute vector contains all values provided in the given collection. Relies on {@link Collection#containsAll(Collection)} implementation.
	 * @param values
	 * @return
	 */
	@Override
	public boolean valuesMatch(Collection<? extends IValue> values) {
		return this.getVector().containsAll(values);
	}
	
	/**
	 * If the attribute vector contains each value that characterize entity vector value. Relies on {@link Collection#contains(Object)} implementation.
	 * @param entity
	 * @return
	 */
	@Override
	public boolean entityMatch(IEntity<? extends IAttribute<? extends IValue>> entity, MatchType type) {
		switch (type) {
		case ALL: return this.valuesMatch(entity.getValues());
		case ANY: return entity.getValues().stream().anyMatch(value -> this.valueMatch(value));
		case NONE: return entity.getValues().stream().noneMatch(value -> this.valueMatch(value));
		default: throw new RuntimeException();
		}
	}
	
	/**
	 * Return the Hamming distance between this attribute vector and given entity. It relies on entity level value vector contract, meaning
	 * if the attribute as several value for one attribute, the Hamming distance will be 0 is none are present in the entity and 1 if at least
	 * one is actually in the entity value vector
	 * 
	 * @param entity
	 * @return
	 */
	@Override
	public int getHammingDistance(IEntity<? extends IAttribute<? extends IValue>> entity) {
		return (int) entity.getAttributes().stream()
				.filter(a -> vector.keySet().contains(a) 
						&& vector.get(a).contains(entity.getValueForAttribute(a.getAttributeName())))
				.count();
	}
	
	// SETTER
	
	/**
	 * Add new value match to this attribute vector
	 * 
	 * @param matches
	 */
	@Override
	public void addMatchToVector(IValue... matches){
		vector.putAll(Stream.of(matches)
			.collect(Collectors.groupingBy(
					v -> v.getValueSpace().getAttribute(),
					Collectors.toSet())));
	}
	
	//-------------------------------------------//
	
	@Override
	public String toString() {
		return GSDisplayUtil.prettyPrint(vector.values(), ";");
	}
	
	@Override
	public void setVector(Collection<IValue> vector) {
		this.addMatchToVector(vector.toArray(new IValue[vector.size()]));
	}

	@Override
	public Collection<IValue> getVector() {
		return vector.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	public Map<IAttribute<? extends IValue>, Set<IValue>> getMapVector() {
		return Collections.unmodifiableMap(vector);
	}
	
}
