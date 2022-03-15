package core.metamodel.attribute.emergent.aggregator;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSEnumDataType;

/**
 * Aggregator function aimed to get one return value based on several input value.
 * <p>
 * To extend {@link IAggregatorValueFunction} one must add the class into JsonSubTypes.Type() annotation. The new class
 * must also have a default empty constructor and getters/setters for any field variables
 * 
 * @author kevinchapuis
 *
 * @param <V>
 */
@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME,
	      include = JsonTypeInfo.As.PROPERTY,
	      property = IAggregatorValueFunction.TYPE
	      )
@JsonSubTypes({
	        @JsonSubTypes.Type(value = BooleanValueAggregator.class),
	        @JsonSubTypes.Type(value = IntegerValueAggregator.class),
	        @JsonSubTypes.Type(value = DoubleValueAggregator.class),
	        @JsonSubTypes.Type(value = NominalValueAggregator.class),
	        @JsonSubTypes.Type(value = OrderedValueAggregator.class),
	        @JsonSubTypes.Type(value = RangeValueAggregator.class)
	    })
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
public interface IAggregatorValueFunction<V extends IValue> {
	
	public static final String TYPE = "TYPE"; 
	
	public static final String DEFAULT_TAG = "DEFAULT - ";
	
	/**
	 * Turnes a collection of values into one value of the same type
	 * 
	 * @param values
	 * @param valueSpace
	 * @return
	 */
	public V aggregate(Collection<V> values, IValueSpace<V> valueSpace);
	
	@JsonProperty(TYPE)
	public String getType();
	
	/**
	 * Set the char sequence that join nominal and ordinal values
	 * @param charSeq
	 */
	default CharSequence getDefaultCharConcat() {
		return "-";
	}
	
	@SuppressWarnings("unchecked")
	static <A extends IValue> IAggregatorValueFunction<A> getDefaultAggregator(Class<A> clazz){
		switch (GSEnumDataType.getType(clazz)) {
		case Boolean:
			return (IAggregatorValueFunction<A>) BooleanValueAggregator.getInstance();
		case Integer:
			return (IAggregatorValueFunction<A>) IntegerValueAggregator.getInstance();
		case Continue:
			return (IAggregatorValueFunction<A>) DoubleValueAggregator.getInstance();
		case Nominal:
			return (IAggregatorValueFunction<A>) NominalValueAggregator.getInstance();
		case Order:
			return (IAggregatorValueFunction<A>) OrderedValueAggregator.getInstance();
		case Range:
			return (IAggregatorValueFunction<A>) RangeValueAggregator.getInstance();
		default:
			throw new IllegalArgumentException("The requested class "+clazz+" does not have any default aggregator");
		}
	}
	
}
