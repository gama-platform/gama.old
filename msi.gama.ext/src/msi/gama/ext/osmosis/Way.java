// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A data class representing a single OSM way.
 *
 * @author Brett Henderson
 */
public class Way extends Entity implements Comparable<Way> {

	private List<WayNode> wayNodes;

	/**
	 * Creates a new instance.
	 *
	 * @param entityData
	 *            The common entity data.
	 */
	public Way(final CommonEntityData entityData) {
		super(entityData);

		this.wayNodes = new ArrayList<>();
	}

	/**
	 * Creates a new instance.
	 *
	 * @param entityData
	 *            The common entity data.
	 * @param wayNodes
	 *            The way nodes to apply to the object
	 */
	public Way(final CommonEntityData entityData, final List<WayNode> wayNodes) {
		super(entityData);

		this.wayNodes = new ArrayList<>(wayNodes);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param originalWay
	 *            The way to clone from.
	 */
	private Way(final Way originalWay) {
		super(originalWay);

		this.wayNodes = new ArrayList<>(originalWay.wayNodes);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public Way(final StoreReader sr, final StoreClassRegister scr) {
		super(sr, scr);

		int featureCount;

		featureCount = sr.readInteger();

		wayNodes = new ArrayList<>();
		for (int i = 0; i < featureCount; i++) {
			wayNodes.add(new WayNode(sr, scr));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		super.store(sw, scr);

		sw.writeInteger(wayNodes.size());
		for (final WayNode wayNode : wayNodes) {
			wayNode.store(sw, scr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getType() {
		return EntityType.Way;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Way) {
			return compareTo((Way) o) == 0;
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
	 * Compares this node list to the specified node list. The comparison is based on a direct comparison of the node
	 * ids.
	 *
	 * @param comparisonWayNodes
	 *            The node list to compare to.
	 * @return 0 if equal, &lt; 0 if considered "smaller", and &gt; 0 if considered "bigger".
	 */
	protected int compareWayNodes(final List<WayNode> comparisonWayNodes) {
		Iterator<WayNode> i;
		Iterator<WayNode> j;

		// The list with the most entities is considered bigger.
		if (wayNodes.size() != comparisonWayNodes.size()) { return wayNodes.size() - comparisonWayNodes.size(); }

		// Check the individual way nodes.
		i = wayNodes.iterator();
		j = comparisonWayNodes.iterator();
		while (i.hasNext()) {
			final int result = i.next().compareTo(j.next());

			if (result != 0) { return result; }
		}

		// There are no differences.
		return 0;
	}

	/**
	 * Compares this way to the specified way. The way comparison is based on a comparison of id, version, timestamp,
	 * wayNodeList and tags in that order.
	 *
	 * @param comparisonWay
	 *            The way to compare to.
	 * @return 0 if equal, &lt; 0 if considered "smaller", and &gt; 0 if considered "bigger".
	 */
	@Override
	public int compareTo(final Way comparisonWay) {
		int wayNodeListResult;

		if (this.getId() < comparisonWay.getId()) { return -1; }
		if (this.getId() > comparisonWay.getId()) { return 1; }

		if (this.getVersion() < comparisonWay.getVersion()) { return -1; }
		if (this.getVersion() > comparisonWay.getVersion()) { return 1; }

		if (this.getTimestamp() == null && comparisonWay.getTimestamp() != null) { return -1; }
		if (this.getTimestamp() != null && comparisonWay.getTimestamp() == null) { return 1; }
		if (this.getTimestamp() != null && comparisonWay.getTimestamp() != null) {
			int result;

			result = this.getTimestamp().compareTo(comparisonWay.getTimestamp());

			if (result != 0) { return result; }
		}

		wayNodeListResult = compareWayNodes(comparisonWay.getWayNodes());

		if (wayNodeListResult != 0) { return wayNodeListResult; }

		return compareTags(comparisonWay.getTags());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void makeReadOnly() {
		if (!isReadOnly()) {
			wayNodes = Collections.unmodifiableList(wayNodes);
		}

		super.makeReadOnly();
	}

	/**
	 * Returns the attached list of way nodes. The returned list is read-only.
	 *
	 * @return The wayNodeList.
	 */
	public List<WayNode> getWayNodes() {
		return wayNodes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Way getWriteableInstance() {
		if (isReadOnly()) {
			return new Way(this);
		} else {
			return this;
		}
	}

	/**
	 * Is this way closed? (A way is closed if the first node id equals the last node id.)
	 *
	 * @return True or false
	 */
	public boolean isClosed() {
		return wayNodes.get(0).getNodeId() == wayNodes.get(wayNodes.size() - 1).getNodeId();
	}

	/**
	 * ${@inheritDoc}.
	 */
	@Override
	public String toString() {
		String name = null;
		final Collection<Tag> tags = getTags();
		for (final Tag tag : tags) {
			if (tag.getKey() != null && tag.getKey().equalsIgnoreCase("name")) {
				name = tag.getValue();
				break;
			}
		}
		if (name != null) { return "Way(id=" + getId() + ", #tags=" + getTags().size() + ", name='" + name + "')"; }
		return "Way(id=" + getId() + ", #tags=" + getTags().size() + ")";
	}
}
