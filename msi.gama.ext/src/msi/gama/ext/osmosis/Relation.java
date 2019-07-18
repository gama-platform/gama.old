// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A data class representing a single OSM relation.
 *
 * @author Brett Henderson
 */
public class Relation extends Entity implements Comparable<Relation> {
	private List<RelationMember> members;

	/**
	 * Creates a new instance.
	 *
	 * @param entityData
	 *            The common entity data.
	 */
	public Relation(final CommonEntityData entityData) {
		super(entityData);

		this.members = new ArrayList<>();
	}

	/**
	 * Creates a new instance.
	 *
	 * @param entityData
	 *            The common entity data.
	 * @param members
	 *            The members to apply to the object.
	 */
	public Relation(final CommonEntityData entityData, final List<RelationMember> members) {
		super(entityData);

		this.members = new ArrayList<>(members);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param originalRelation
	 *            The relation to clone from.
	 */
	private Relation(final Relation originalRelation) {
		super(originalRelation);

		this.members = new ArrayList<>(originalRelation.members);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public Relation(final StoreReader sr, final StoreClassRegister scr) {
		super(sr, scr);

		int featureCount;

		featureCount = sr.readInteger();

		members = new ArrayList<>();
		for (int i = 0; i < featureCount; i++) {
			members.add(new RelationMember(sr, scr));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		super.store(sw, scr);

		sw.writeInteger(members.size());
		for (final RelationMember relationMember : members) {
			relationMember.store(sw, scr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getType() {
		return EntityType.Relation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Relation) {
			return compareTo((Relation) o) == 0;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		/*
		 * As per the hashCode definition, this doesn't have to be unique it just has to return the same value for any
		 * two objects that compare equal. Using both id and version will provide a good distribution of values but is
		 * simple to calculate.
		 */
		return (int) getId() + getVersion();
	}

	/**
	 * Compares this member list to the specified member list. The bigger list is considered bigger, if that is equal
	 * then each relation member is compared.
	 *
	 * @param comparisonMemberList
	 *            The member list to compare to.
	 * @return 0 if equal, &lt; 0 if considered "smaller", and &gt; 0 if considered "bigger".
	 */
	protected int compareMemberList(final Collection<RelationMember> comparisonMemberList) {
		Iterator<RelationMember> i;
		Iterator<RelationMember> j;

		// The list with the most entities is considered bigger.
		if (members.size() != comparisonMemberList.size()) { return members.size() - comparisonMemberList.size(); }

		// Check the individual node references.
		i = members.iterator();
		j = comparisonMemberList.iterator();
		while (i.hasNext()) {
			final int result = i.next().compareTo(j.next());

			if (result != 0) { return result; }
		}

		// There are no differences.
		return 0;
	}

	/**
	 * Compares this relation to the specified relation. The relation comparison is based on a comparison of id,
	 * version, timestamp, and tags in that order.
	 *
	 * @param comparisonRelation
	 *            The relation to compare to.
	 * @return 0 if equal, &lt; 0 if considered "smaller", and &gt; 0 if considered "bigger".
	 */
	@Override
	public int compareTo(final Relation comparisonRelation) {
		int memberListResult;

		if (this.getId() < comparisonRelation.getId()) { return -1; }
		if (this.getId() > comparisonRelation.getId()) { return 1; }

		if (this.getVersion() < comparisonRelation.getVersion()) { return -1; }
		if (this.getVersion() > comparisonRelation.getVersion()) { return 1; }

		if (this.getTimestamp() == null && comparisonRelation.getTimestamp() != null) { return -1; }
		if (this.getTimestamp() != null && comparisonRelation.getTimestamp() == null) { return 1; }
		if (this.getTimestamp() != null && comparisonRelation.getTimestamp() != null) {
			int result;

			result = this.getTimestamp().compareTo(comparisonRelation.getTimestamp());

			if (result != 0) { return result; }
		}

		memberListResult = compareMemberList(comparisonRelation.members);

		if (memberListResult != 0) { return memberListResult; }

		return compareTags(comparisonRelation.getTags());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void makeReadOnly() {
		if (!isReadOnly()) {
			members = Collections.unmodifiableList(members);
		}

		super.makeReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Relation getWriteableInstance() {
		if (isReadOnly()) {
			return new Relation(this);
		} else {
			return this;
		}
	}

	/**
	 * Returns the attached list of relation members. The returned list is read-only.
	 *
	 * @return The member list.
	 */
	public List<RelationMember> getMembers() {
		return members;
	}

	/**
	 * ${@inheritDoc}.
	 */
	@Override
	public String toString() {
		String type = null;
		final Collection<Tag> tags = getTags();
		for (final Tag tag : tags) {
			if (tag.getKey() != null && tag.getKey().equalsIgnoreCase("type")) {
				type = tag.getValue();
				break;
			}
		}
		if (type != null) {
			return "Relation(id=" + getId() + ", #tags=" + getTags().size() + ", type='" + type + "')";
		}
		return "Relation(id=" + getId() + ", #tags=" + getTags().size() + ")";
	}
}
