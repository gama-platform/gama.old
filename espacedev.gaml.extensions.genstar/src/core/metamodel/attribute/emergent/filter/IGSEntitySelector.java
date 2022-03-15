package core.metamodel.attribute.emergent.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.attribute.EmergentTransposerSerializer;
import core.metamodel.attribute.EmergentAttribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.comparator.ImplicitEntityComparator;
import core.metamodel.entity.matcher.IGSEntityMatcher;
import core.metamodel.entity.matcher.MatchType;
import core.metamodel.value.IValue;

/**
 * Filter a set of entity according to value matching
 * 
 * @author kevinchapuis
 *
 * @param <U> The output of the filter
 * @param <T> The predicate of filter process
 */
@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME,
	      include = JsonTypeInfo.As.EXISTING_PROPERTY,
	      property = IGSEntitySelector.TYPE
	      )
@JsonSubTypes({
    @JsonSubTypes.Type(value = GSNoFilter.class),
    @JsonSubTypes.Type(value = GSMatchSelection.class),
    @JsonSubTypes.Type(value = GSMatchFilter.class)
})
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
@JsonSerialize(using = EmergentTransposerSerializer.class)
public interface IGSEntitySelector<U, T> extends Function<IEntity<? extends IAttribute<? extends IValue>>, U> {
	

	public static final String TYPE = "TYPE";
	
	public static final String COMPARATOR = "COMPARATOR";
	public static final String MATCHERS = "MATCHERS";
	public static final String MATCH_TYPE = "MATCH TYPE";
	
	/**
	 * TODO : @doc
	 * Meant to validate a match with the given entity
	 * 
	 * @param type
	 * @param entity
	 * @return
	 */
	public boolean validate(MatchType type, IEntity<? extends IAttribute<? extends IValue>> entity);
	
	/**
	 * TODO : better @doc
	 * Should provide the combination of sub entities possible attribute/value match for a given value of emergent attribute
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public <V extends IValue> Map<IAttribute<? extends IValue>,IValue> reverse(EmergentAttribute<V,U,T> attribute, V value);
	
	/**
	 * The default supplier to collect retained entities
	 * @return
	 */
	@JsonIgnore
	default Supplier<Collection<IEntity<? extends IAttribute<? extends IValue>>>> getSupplier(){
		return new Supplier<Collection<IEntity<? extends IAttribute<? extends IValue>>>>() {
			@Override
			public Collection<IEntity<? extends IAttribute<? extends IValue>>> get() {
				return new HashSet<IEntity<? extends IAttribute<? extends IValue>>>();
			}
		};
	}
	
	@JsonProperty(COMPARATOR)
	public ImplicitEntityComparator getComparator();
	
	@JsonProperty(COMPARATOR)
	public void setComparator(ImplicitEntityComparator comparator);
	
	@JsonProperty(MATCHERS)
	public IGSEntityMatcher<T> getMatcher();
	
	@JsonProperty(MATCHERS)
	public void setMatcher(IGSEntityMatcher<T> matcher);

	@JsonProperty(MATCH_TYPE)
	public MatchType getMatchType();
	
	@JsonProperty(MATCH_TYPE)
	public void setMatchType(MatchType type);
	
}
