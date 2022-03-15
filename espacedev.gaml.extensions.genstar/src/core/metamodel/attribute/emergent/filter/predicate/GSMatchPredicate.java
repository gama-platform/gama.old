package core.metamodel.attribute.emergent.filter.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.emergent.filter.AGSEntitySelector;
import core.metamodel.attribute.emergent.filter.IGSEntitySelector;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.value.IValue;

public class GSMatchPredicate<U, T> extends AGSEntitySelector<U, T> {

	private Collection<Function<U, Boolean>> predicates;
	
	private IGSEntitySelector<U, T> transposer;
	
	public GSMatchPredicate(IGSEntitySelector<U,T> transposer) {
		super(transposer.getMatcher(), transposer.getMatchType());
		this.transposer = transposer;
	}
	
	@SafeVarargs
	public GSMatchPredicate(IGSEntitySelector<U,T> transposer,
			Function<U, Boolean>... predicates) {
		this(transposer);
		this.predicates = new ArrayList<>();
		this.predicates.addAll(Arrays.asList(predicates));
	}

	@Override
	public boolean validate(MatchType type, IEntity<? extends IAttribute<? extends IValue>> entity) {
		switch (type) {
		case ANY:
			return predicates.stream().anyMatch(predicate -> predicate.apply(this.apply(entity)));
		case NONE:
			return predicates.stream().noneMatch(predicate -> predicate.apply(this.apply(entity)));
		default:
			return predicates.stream().allMatch(predicate -> predicate.apply(this.apply(entity)));
		}
	}

	@Override
	public U apply(IEntity<? extends IAttribute<? extends IValue>> superEntity) {
		return transposer.apply(superEntity);
	}

	@Override
	public <V extends IValue> Map<IAttribute<? extends IValue>, IValue> reverse(EmergentAttribute<V,U,T> attribute, V value) {
		return transposer.reverse(attribute, value);
	}

}
