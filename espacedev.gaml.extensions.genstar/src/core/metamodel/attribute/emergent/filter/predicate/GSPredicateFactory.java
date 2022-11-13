package core.metamodel.attribute.emergent.filter.predicate;

import java.util.Collection;
import java.util.function.Function;

import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.emergent.CompositeValueFunction;
import core.metamodel.attribute.emergent.filter.GSMatchFilter;
import core.metamodel.attribute.emergent.filter.IGSEntitySelector;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.matcher.AttributeVectorMatcher;
import core.metamodel.entity.matcher.IGSEntityMatcher;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.value.IValue;

/**
 * Create predicate {@link GSMatchPredicate} to setup {@link CompositeValueFunction}
 * 
 * @author kevinchapuis
 *
 */
public class GSPredicateFactory {

	private static final GSPredicateFactory FACTORY = new GSPredicateFactory();
	private static Function<Collection<IEntity<? extends IAttribute<? extends IValue>>>, Boolean> EXIST_P;
	
	private GSPredicateFactory() {}
	
	public static GSPredicateFactory getFactory() {
		return FACTORY;
	}
	
	/**
	 * Default existence predicate: use an {@link IGSEntitySelector} to filter entities, will return
	 * true if any entity have been selected or false otherwise
	 * 
	 * @param matcher
	 * @param type
	 * @return
	 */
	public <T> GSMatchPredicate<Collection<IEntity<? extends IAttribute<? extends IValue>>>, T> 
			createExistPredicate(IGSEntityMatcher<T> matcher, MatchType type){
		return new GSMatchPredicate<>(new GSMatchFilter<>(matcher, type), this.getExistanceFunction());
	}
	
	/**
	 * Existence predicate for a set of individuals to match properties: a {@link GSMatchFilter} to select the individuals
	 * and test existance
	 * 
	 * @param selector
	 * @param type
	 * @param matches
	 * @return
	 */
	public <T> GSMatchPredicate<Collection<IEntity<? extends IAttribute<? extends IValue>>>, T> createExistPredicate(
			IGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>,T> selector, 
			MatchType type, IValue... matches){
		return new GSMatchPredicate<>(selector, this.getExistanceFunction(type, matches));
	}
	
	/**
	 * 
	 * @param selector
	 * @param matches
	 * @return
	 */
	public <T> GSMatchPredicate<IEntity<? extends IAttribute<? extends IValue>>, T> createExistPredicate(
			IGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>,T> selector, IValue... matches){
		return new GSMatchPredicate<>(selector, this.getExistanceFunction(matches));
	}


	// ------------------- CREATE FUNCTION

	
	private Function<IEntity<? extends IAttribute<? extends IValue>>, Boolean> 
			getExistanceFunction(IValue[] matches) {
		return new Function<IEntity<? extends IAttribute<? extends IValue>>, Boolean>() {
			private AttributeVectorMatcher avm = new AttributeVectorMatcher(matches);
			@Override public Boolean apply(IEntity<? extends IAttribute<? extends IValue>> entity) {
				return avm.entityMatch(entity, MatchType.ALL);
			}
		};
	}
	
	private Function<Collection<IEntity<? extends IAttribute<? extends IValue>>>, Boolean> 
			getExistanceFunction(MatchType type, IValue... matches) {
		switch (type) {
		case NONE:
			return new Function<Collection<IEntity<? extends IAttribute<? extends IValue>>>, Boolean>(){
				private AttributeVectorMatcher avm = new AttributeVectorMatcher(matches);
				@Override public Boolean apply(Collection<IEntity<? extends IAttribute<? extends IValue>>> entities) {
					return entities.stream().noneMatch(entity -> avm.entityMatch(entity, MatchType.ALL));
				}
				
			};
		case ANY:
			return new Function<Collection<IEntity<? extends IAttribute<? extends IValue>>>, Boolean>(){
				private AttributeVectorMatcher avm = new AttributeVectorMatcher(matches);
				@Override public Boolean apply(Collection<IEntity<? extends IAttribute<? extends IValue>>> entities) {
					return entities.stream().anyMatch(entity -> avm.entityMatch(entity, MatchType.ALL));
				}
				
			};
		default:
			return new Function<Collection<IEntity<? extends IAttribute<? extends IValue>>>, Boolean>(){
				private AttributeVectorMatcher avm = new AttributeVectorMatcher(matches);
				@Override public Boolean apply(Collection<IEntity<? extends IAttribute<? extends IValue>>> entities) {
					return entities.stream().allMatch(entity -> avm.entityMatch(entity, MatchType.ALL));
				}
				
			};
		}
	}

	private Function<Collection<IEntity<? extends IAttribute<? extends IValue>>>, Boolean> getExistanceFunction() {
		if(EXIST_P == null) {
			EXIST_P = new Function<Collection<IEntity<? extends IAttribute<? extends IValue>>>, Boolean>() {
				@Override
				public Boolean apply(Collection<IEntity<? extends IAttribute<? extends IValue>>> collection) {
					
					return !collection.isEmpty();
				}
			};
		}
		return EXIST_P;
	}
}
