package core.metamodel.attribute.emergent;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.emergent.aggregator.IAggregatorValueFunction;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * Function that will transpose a collection of {@link IEntity} to a value <V> taking into account a certain attribute.
 * 
 * @author kevinchapuis
 *
 * @param <V>
 */
@JsonTypeName(AggregateValueFunction.SELF)
public class AggregateValueFunction<V extends IValue> implements 
	IGSValueFunction<Collection<IEntity<? extends IAttribute<? extends IValue>>>, V> {

	public static final String SELF = "AGGREGATE VALUE FUNCTION";
	public static final String AGG = "AGGREGATOR";
	
	private IAggregatorValueFunction<V> aggregator;
	private Attribute<V> referent;
	
	public AggregateValueFunction(IAggregatorValueFunction<V> aggregator, Attribute<V> referent) {
		this.aggregator = aggregator;
		this.referent = referent;
	}
	
	// Main (Java) Function (Class) contract
	
	@Override
	public V apply(Collection<IEntity<? extends IAttribute<? extends IValue>>> entities) {
		return aggregator.aggregate(entities.stream()
				.map(e -> referent.getValueSpace().getValue(
						e.getValueForAttribute(referent.getAttributeName()).getStringValue()))
				.toList(), this.referent.getValueSpace());
	}

	@Override
	public Collection<Map<IAttribute<? extends IValue>, IValue>> reverse(
			Map<IAttribute<? extends IValue>, IValue> entities) {
		
		return null;
	}
	
	// Other part
	
	@Override
	public Attribute<V> getReferent() {
		return this.referent;
	}

	@Override
	public void setReferent(Attribute<V> referent) {
		this.referent = referent;
	}
	
	@JsonProperty(AggregateValueFunction.AGG)
	public IAggregatorValueFunction<V> getAggregator(){
		return this.aggregator;
	}
	
	@JsonProperty(AggregateValueFunction.AGG)
	public void getAggregator(IAggregatorValueFunction<V> aggregator){
		this.aggregator = aggregator;
	}
	
}
