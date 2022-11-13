package core.metamodel.attribute.emergent.filter;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.value.IValue;

@JsonTypeName(GSNoFilter.SELF)
public class GSNoFilter extends AGSEntitySelector<Collection<IEntity<? extends IAttribute<? extends IValue>>>,Object> {
	
	public static final String SELF = "EMPTY FILTER";
	
	public GSNoFilter() {
		super(null, MatchType.getDefault());
	}
	
	@Override
	public Collection<IEntity<? extends IAttribute<? extends IValue>>> apply(IEntity<? extends IAttribute<? extends IValue>> superEntity) {
		return superEntity.getChildren();
	}
	
	@Override
	public boolean validate(MatchType type, IEntity<? extends IAttribute<? extends IValue>> entity) {
		return true;
	}

	@Override
	public <V extends IValue> Map<IAttribute<? extends IValue>, IValue> reverse(
			EmergentAttribute<V, Collection<IEntity<? extends IAttribute<? extends IValue>>>, Object> attribute,
			V value) {
		
		return null;
	}


}
