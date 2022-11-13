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
 * TODO: javadoc
 * 
 * @author kevinchapuis
 *
 * @param <K>
 * @param <V>
 */
public class UndirectedMapper<K extends IValue, V extends IValue> implements IAttributeMapper<K, V> {

	private Map<Collection<K>,Collection<V>> map;

	private MappedAttribute<K, V> relatedAttribute;

	public UndirectedMapper() {
		this.map = new LinkedHashMap<>();
	}

	// -------------------- IAttributeMapper contract -------------------- //

	@Override
	public boolean add(K mapTo, V mapWith) {
		if(map.keySet().stream().anyMatch(set -> set.contains(mapTo))) {
			return map.get(map.keySet().stream().filter(key -> key.contains(mapTo))
					.findFirst().get()).add(mapWith);
		} else if(map.values().stream().anyMatch(vSet -> vSet.contains(mapWith))) {
			return map.keySet().stream().filter(key -> map.get(key).contains(mapWith))
					.findFirst().get().add(mapTo);
		} else if(this.getRelatedAttribute().getValueSpace().contains(mapTo) 
				&& this.getRelatedAttribute().getReferentAttribute().getValueSpace()
				.getValues().contains(mapWith)) {
			map.put(new ArrayList<>(Arrays.asList(mapTo)), new ArrayList<>(Arrays.asList(mapWith)));
			return true;
		}
		return false;
	}

	@Override
	public Collection<? extends IValue> getMappedValues(IValue value) {
		// Modif Benoit ! && ! 
		if(!map.values().stream().flatMap(Collection::stream).anyMatch(val -> val.equals(value)) 
				&& !map.keySet().stream().flatMap(Collection::stream).anyMatch(val -> val.equals(value)))
			throw new NullPointerException("Value "+value+" is not linked to this mapped attribute ("
					+this.getRelatedAttribute()+")");

		// Modif Benoit
		// new return : pour tenir compte que les keys, contiennent des listes.
		return (map.entrySet().stream().filter(e -> e.getKey().contains(value)).count() != 0) ?
				map.entrySet().stream().filter(e -> e.getKey().contains(value)).flatMap(e -> e.getValue().stream()).toList()
				: map.entrySet().stream().filter(e -> e.getValue().contains(value)).flatMap(e -> e.getKey().stream()).toList();
		
//		return map.keySet().contains(value) ? 
//				Collections.unmodifiableCollection(map.get(value))
//				: map.entrySet().stream().filter(e -> e.getValue().contains(value))
//				.flatMap(e -> e.getKey().stream()).toList();
	}

	// -------------------- GETTER & SETTER -------------------- //

	@Override
	public void setRelatedAttribute(MappedAttribute<K, V> relatedAttribute) {
		this.relatedAttribute = relatedAttribute;
	}

	@Override
	public MappedAttribute<K, V> getRelatedAttribute() {
		return this.relatedAttribute;
	}

	@Override
	public Map<Collection<K>, Collection<V>> getRawMapper(){
		return Collections.unmodifiableMap(map);
	}

	// CLASS SPECIFIC

	public Map<Collection<K>, Collection<V>> getMapper(){
		return Collections.unmodifiableMap(map);
	}

	public void setMapper(Map<Collection<K>, Collection<V>> map) {
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
		UndirectedMapper other = (UndirectedMapper) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

}
