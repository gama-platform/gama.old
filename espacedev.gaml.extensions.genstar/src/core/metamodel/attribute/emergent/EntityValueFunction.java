package core.metamodel.attribute.emergent;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * 
 * A function that will transpose any entity <E> to a value <V> on a given attribute
 * 
 * @author kevinchapuis
 *
 * @param <E>
 * @param <V>
 */
@JsonTypeName(EntityValueFunction.SELF)
public class EntityValueFunction<E extends IEntity<? extends IAttribute<? extends IValue>>, V extends IValue> implements IGSValueFunction<E, V> {

	public static final String SELF = "ENTITY ATTRIBUTE VALUE FUNCTION";
	
	private Map<V, V> mapping;
	
	private Attribute<V> superReferent;
	private Attribute<V> subReferent;

	/**
	 * 
	 * @param superReferent
	 * @param subReferent
	 * @param mapping
	 */
	public EntityValueFunction(Attribute<V> superReferent, Attribute<V> subReferent, Map<V, V> mapping) {
		this.superReferent = superReferent;
		this.subReferent = subReferent;
		this.mapping = this.checkMapping(mapping);
	}
	
	/**
	 * 
	 * @param referent
	 */
	public EntityValueFunction(Attribute<V> referent) {
		this.mapping = referent.getValueSpace().getValues().stream()
				.collect(Collectors.toMap(Function.identity(), Function.identity()));
		this.superReferent = referent;
		this.subReferent = referent;
	}
	
	// The general contract of the class
	
	@Override
	public V apply(E entity) {
		return mapping.get(entity.getValueForAttribute(this.subReferent.getAttributeName()));
	}

	@Override
	public Collection<Map<IAttribute<? extends IValue>, IValue>> reverse(
			Map<IAttribute<? extends IValue>, IValue> entities) {
		
		return null;
	}
	
	// Utilities and accessors

	@Override
	public Attribute<V> getReferent() {
		return this.superReferent;
	}
	
	@Override
	public void setReferent(Attribute<V> referent) {
		this.superReferent = referent;
	}

	@JsonProperty(IGSValueFunction.MAPPING)
	public Map<V, V> getMapping() {
		return Collections.unmodifiableMap(this.mapping);
	}
	
	@JsonProperty(IGSValueFunction.MAPPING)
	public void setMapping(Map<V, V> mapping) {
		this.mapping = this.checkMapping(mapping);
	}
	
	/*
	 * Private check according to super and sub referent attribute
	 */
	private Map<V,V> checkMapping(Map<V,V> mapping) {
		if(mapping.keySet().stream().anyMatch(v -> !subReferent.getValueSpace().contains(v))
				|| mapping.values().stream().anyMatch(v -> !superReferent.getValueSpace().contains(v)))
			throw new IllegalArgumentException("Trying to setup mapper with inconsistent mapping : {subEntity = "
				+mapping.keySet().stream().filter(v -> !subReferent.getValueSpace().contains(v))
				.map(IValue::getStringValue).collect(Collectors.joining(";"))+"} | {superEntity = "
				+mapping.values().stream().filter(v -> !superReferent.getValueSpace().contains(v))
				.map(IValue::getStringValue).collect(Collectors.joining(";"))+"}");
		return mapping;
	}

}
