package core.metamodel.attribute.emergent.filter;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.matcher.IGSEntityMatcher;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.value.IValue;

/**
 * Get one child entity from super entity based on filter and matcher
 * 
 * @author kevinchapuis
 *
 * @param <F>
 */
@JsonTypeName(GSMatchSelection.SELF)
public class GSMatchSelection<F> extends AGSEntitySelector<IEntity<? extends IAttribute<? extends IValue>>,F> {

	public static final String SELF = "SELECTION FILTER";
	
	public GSMatchSelection(IGSEntityMatcher<F> matcher, MatchType match) {
		super(matcher, match);
	}
	
	public GSMatchSelection(IGSEntityMatcher<F> matcher) {
		super(matcher);
	}
	
	@Override
	public IEntity<? extends IAttribute<? extends IValue>> apply(IEntity<? extends IAttribute<? extends IValue>> superEntity) {
		Optional<IEntity<? extends IAttribute<? extends IValue>>> output = superEntity.getChildren().stream()
				.filter(e -> this.validate(super.getMatchType(), e))
				.sorted(getComparator())
				.findFirst();
		if(output.isPresent()) {
			return output.get();
		}
		throw new NullPointerException("There is no sub entity associated to "+super.getMatcher().toString()+" in super entity: "+superEntity);
	}

	@Override
	public <V extends IValue> Map<IAttribute<? extends IValue>, IValue> reverse(
			EmergentAttribute<V, IEntity<? extends IAttribute<? extends IValue>>, F> attribute, V value) {
		
		return null;
	}
	
}
