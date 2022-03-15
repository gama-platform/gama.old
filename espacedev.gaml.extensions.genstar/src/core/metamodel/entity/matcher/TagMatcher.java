package core.metamodel.entity.matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.entity.tag.EntityTag;
import core.metamodel.value.IValue;
import core.util.GSDisplayUtil;

/**
 * Vector of tags that can identify matches 
 * 
 * @author kevinchapuis
 *
 */
@JsonTypeName(TagMatcher.SELF)
public class TagMatcher implements IGSEntityMatcher<EntityTag> {

	public static final String SELF = "TAG MATCHER";
	
	Collection<EntityTag> tags;
	
	public TagMatcher() {
		this.tags = new HashSet<>();
	}
	
	public TagMatcher(EntityTag... matches) {
		this();
		this.addMatchToVector(matches);
	}
	
	@Override
	public boolean valueMatch(EntityTag tag) {
		return tags.contains(tag);
	}

	@Override
	public boolean valuesMatch(Collection<? extends EntityTag> values) {
		return tags.containsAll(values);
	}

	@Override
	public boolean entityMatch(IEntity<? extends IAttribute<? extends IValue>> entity, MatchType type) {
		switch (type) {
			case ALL: return this.valuesMatch(entity.getTags());
			case ANY: return entity.getTags().stream().anyMatch(tag -> valueMatch(tag));
			case NONE: return entity.getTags().stream().noneMatch(tag -> valueMatch(tag));
			default: throw new RuntimeException();
		}
	}

	@Override
	public int getHammingDistance(IEntity<? extends IAttribute<? extends IValue>> entity) {
		return (int) this.tags.stream().filter(tag -> entity.hasTags(tag)).count();
	}

	@Override
	public void addMatchToVector(EntityTag... matches) {
		this.tags.addAll(Arrays.asList(matches));
	}
	
	@Override
	public String toString() {
		return GSDisplayUtil.prettyPrint(tags, ";");
	}

	@Override
	public void setVector(Collection<EntityTag> vector) {
		this.tags = vector;
	}

	@Override
	public Collection<EntityTag> getVector() {
		return Collections.unmodifiableCollection(tags);
	}

}
