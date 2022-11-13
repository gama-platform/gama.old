package core.metamodel.entity.matcher;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.entity.EntityMatcherSerializer;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * Estimate the match between two entity considering a vector of predicates
 * 
 * @author kevinchapuis
 *
 * @param <M>
 */
@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME,
	      include = JsonTypeInfo.As.EXISTING_PROPERTY,
	      property = IGSEntityMatcher.TYPE_LABEL
	      )
@JsonSubTypes({
  @JsonSubTypes.Type(value = AttributeVectorMatcher.class),
  @JsonSubTypes.Type(value = TagMatcher.class)
})
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
@JsonSerialize(using = EntityMatcherSerializer.class)
public interface IGSEntityMatcher<M> {
	
	public static final String TYPE_LABEL = "ENTITY MATCH TYPE";
	public static final String VECTOR_LABEL = "MATCH VECTOR";

	public boolean valueMatch(M value);
	public boolean valuesMatch(Collection<? extends M> values);
	public boolean entityMatch(IEntity<? extends IAttribute<? extends IValue>> entity, MatchType type);
	public int getHammingDistance(IEntity<? extends IAttribute<? extends IValue>> entity);
	public void addMatchToVector(@SuppressWarnings("unchecked") M... matches);
	
	@JsonProperty(VECTOR_LABEL)
	public void setVector(Collection<M> vector);
	
	@JsonProperty(VECTOR_LABEL)
	public Collection<M> getVector();
	
}
