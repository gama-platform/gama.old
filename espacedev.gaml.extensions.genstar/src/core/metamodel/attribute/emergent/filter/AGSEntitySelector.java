package core.metamodel.attribute.emergent.filter;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.comparator.HammingEntityComparator;
import core.metamodel.entity.comparator.ImplicitEntityComparator;
import core.metamodel.entity.matcher.IGSEntityMatcher;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.value.IValue;

public abstract class AGSEntitySelector<U, T> implements IGSEntitySelector<U, T> {

	private ImplicitEntityComparator comparator;
	
	private MatchType match;
	private IGSEntityMatcher<T> matcher;

	public AGSEntitySelector(IGSEntityMatcher<T> matcher, MatchType match) {
		this.comparator = new HammingEntityComparator();
		this.match = match;
		this.matcher = matcher;
	}
	
	public AGSEntitySelector(IGSEntityMatcher<T> matcher) {
		this(matcher, MatchType.getDefault());
	}

	@Override
	public boolean validate(MatchType type, IEntity<? extends IAttribute<? extends IValue>> entity) {
		return this.matcher.entityMatch(entity, type);
	}
	
	@Override
	public ImplicitEntityComparator getComparator() {
		return this.comparator;
	}

	@Override
	public void setComparator(ImplicitEntityComparator comparator) {
		this.comparator = comparator;
	}

	@Override
	public IGSEntityMatcher<T> getMatcher() {
		return this.matcher;
	}

	@Override
	public void setMatcher(IGSEntityMatcher<T> matcher) {
		this.matcher = matcher;
	}
	
	@Override
	public MatchType getMatchType() {
		return this.match;
	}
	
	@Override
	public void setMatchType(MatchType type) {
		this.match = type;
	}
	
}
