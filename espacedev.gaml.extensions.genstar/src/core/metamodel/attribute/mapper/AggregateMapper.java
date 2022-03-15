package core.metamodel.attribute.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import core.metamodel.attribute.MappedAttribute;
import core.metamodel.value.IValue;

/**
 * Mapper for aggregate relationship between two value set of the same type. We have a 1 to n relationship,
 * with n being an positive integer excluding null value. Will define {@link MappedAttribute} with referent
 * attribute being the values of this mapper, see {@link #getMappedValues(IValue)} for accessing referent attribute values,
 * and aggregated attribute being the key of this mapper.
 * 
 * @author kevinchapuis
 *
 * @see {@link MappedAttribute}, {@link IAttributeMapper}, {@link AttributeFactory}
 *
 * @param <V>
 */
public class AggregateMapper<V extends IValue> implements IAttributeMapper<V, V> {

	private Map<V, Collection<V>> map;
	
	private MappedAttribute<V, V> relatedAttribute;

	public AggregateMapper() {
		this.map = new LinkedHashMap<>();
	}
	
	// -------------------- IAttributeMapper contract -------------------- //
	
	@Override
	public boolean add(V mapTo, V mapWith) {
		if(map.containsKey(mapTo))
			return map.get(mapTo).add(mapWith);
		if(this.getRelatedAttribute().getValueSpace().contains(mapTo)
				&& this.getRelatedAttribute().getReferentAttribute().getValueSpace()
				.getValues().contains(mapWith)) {
			map.put(mapTo, new ArrayList<>(Arrays.asList(mapWith)));
			return true;
			}
		return false;
	}

	@Override
	public Collection<V> getMappedValues(IValue value) {
		if(map.containsKey(value))
			return map.get(value);
		if(map.values().stream().flatMap(Collection::stream).anyMatch(val -> val.equals(value)))
			return map.entrySet().stream().filter(e -> e.getValue().contains(value))
					.map(e -> e.getKey()).collect(Collectors.toSet());
		throw new NullPointerException("The value "+value+" is not part of any known link attribute ("
				+ this + " < "+this.getRelatedAttribute().getReferentAttribute()+ ")");
	}
	
	// -------------------- GETTER & SETTER -------------------- //

	@Override
	public void setRelatedAttribute(MappedAttribute<V, V> relatedAttribute) {
		this.relatedAttribute = relatedAttribute;
	}

	@Override
	public MappedAttribute<V, V> getRelatedAttribute() {
		return relatedAttribute;
	}
	
	@Override
	public Map<Collection<V>, Collection<V>> getRawMapper(){
		return map.keySet().stream().collect(Collectors
				.toMap(
						key -> Collections.singleton(key), 
						key -> Collections.unmodifiableCollection(map.get(key))));
	}
	
	// CLASS SPECIFIC
	
	public Map<V, Collection<V>> getMapper(){
		return Collections.unmodifiableMap(map);
	}
	
	public void setMapper(Map<V, Collection<V>> map) {
		this.map = map;
	}
	
	// ---------------------------------------------------------- //

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
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
		AggregateMapper other = (AggregateMapper) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

}
