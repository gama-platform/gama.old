/*******************************************************************************************************
 *
 * TagMatcher.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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
import core.util.exception.GenstarException;

/**
 * Vector of tags that can identify matches
 *
 * @author kevinchapuis
 *
 */
@JsonTypeName (TagMatcher.SELF)
public class TagMatcher implements IGSEntityMatcher<EntityTag> {

	/** The Constant SELF. */
	public static final String SELF = "TAG MATCHER";

	/** The tags. */
	Collection<EntityTag> tags;

	/**
	 * Instantiates a new tag matcher.
	 */
	public TagMatcher() {
		this.tags = new HashSet<>();
	}

	/**
	 * Instantiates a new tag matcher.
	 *
	 * @param matches the matches
	 */
	public TagMatcher(final EntityTag... matches) {
		this();
		this.addMatchToVector(matches);
	}

	@Override
	public boolean valueMatch(final EntityTag tag) {
		return tags.contains(tag);
	}

	@Override
	public boolean valuesMatch(final Collection<? extends EntityTag> values) {
		return tags.containsAll(values);
	}

	@Override
	public boolean entityMatch(final IEntity<? extends IAttribute<? extends IValue>> entity, final MatchType type) {
		return switch (type) {
			case ALL -> this.valuesMatch(entity.getTags());
			case ANY -> entity.getTags().stream().anyMatch(this::valueMatch);
			case NONE -> entity.getTags().stream().noneMatch(this::valueMatch);
			default -> throw new GenstarException();
		};
	}

	@Override
	public int getHammingDistance(final IEntity<? extends IAttribute<? extends IValue>> entity) {
		return (int) this.tags.stream().filter(tag -> entity.hasTags(tag)).count();
	}

	@Override
	public void addMatchToVector(final EntityTag... matches) {
		this.tags.addAll(Arrays.asList(matches));
	}

	@Override
	public String toString() {
		return GSDisplayUtil.prettyPrint(tags, ";");
	}

	@Override
	public void setVector(final Collection<EntityTag> vector) { this.tags = vector; }

	@Override
	public Collection<EntityTag> getVector() { return Collections.unmodifiableCollection(tags); }

}
