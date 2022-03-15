package core.metamodel.attribute.emergent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.emergent.filter.predicate.GSMatchPredicate;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

public class CompositeValueFunction<V extends IValue> implements 
	IGSValueFunction<IEntity<? extends IAttribute<? extends IValue>>, V> {

	private Attribute<V> referent; 

	private Map<Collection<GSMatchPredicate<?, ?>>, V> predicates;
	
	public CompositeValueFunction(Attribute<V> referent) {
		this.referent = referent;
		this.predicates = new HashMap<>();
		referent.getValueSpace().getValues().stream().forEach(value -> this.predicates.put(new ArrayList<>(), value));
	}
	
	// Main (Java) Function (Class) contract
	
	@Override
	public V apply(IEntity<? extends IAttribute<? extends IValue>> superEntity) {
		return predicates.entrySet().stream()
				.filter(ps -> ps.getKey().stream()
						.allMatch(predicate -> predicate.validate(predicate.getMatchType(), superEntity)))
				.findFirst().get().getValue();
	}
	
	@Override
	public Collection<Map<IAttribute<? extends IValue>, IValue>> reverse(
			Map<IAttribute<? extends IValue>, IValue> entities) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Other part

	@Override
	public Attribute<V> getReferent() {
		return referent;
	}

	@Override
	public void setReferent(Attribute<V> referent) {
		this.referent = referent;
	}

	/**
	 * Add one predicate to be validated for a particular value to occur
	 * @param transposer
	 * @param relatedValue
	 */
	public void addPredicate(GSMatchPredicate<?, ?> transposer, V relatedValue) {
		Optional<Collection<GSMatchPredicate<?, ?>>> opt = predicates.entrySet().stream()
				.filter(entry -> entry.getValue().equals(relatedValue))
				.map(entry -> entry.getKey())
				.findFirst();
		if(opt.isPresent())
			opt.get().add(transposer);
		else
			this.predicates.put(Stream.of(transposer).collect(Collectors.toList()), relatedValue);
	}

}
