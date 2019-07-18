// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.Collection;

/**
 * A data class representing a single OSM node.
 *
 * @author Brett Henderson
 */
public class Node extends Entity implements Comparable<Node> {

	private double latitude;
	private double longitude;

	/**
	 * Creates a new instance.
	 *
	 * @param entityData
	 *            The common entity data.
	 * @param latitude
	 *            The geographic latitude.
	 * @param longitude
	 *            The geographic longitude.
	 */
	public Node(final CommonEntityData entityData, final double latitude, final double longitude) {
		super(entityData);

		init(latitude, longitude);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param originalNode
	 *            The node to clone from.
	 */
	private Node(final Node originalNode) {
		super(originalNode);

		init(originalNode.latitude, originalNode.longitude);
	}

	/**
	 * Initializes non-collection attributes.
	 *
	 * @param newLatitude
	 *            The geographic latitude.
	 * @param newLongitude
	 *            The geographic longitude.
	 */
	private void init(final double newLatitude, final double newLongitude) {
		this.latitude = newLatitude;
		this.longitude = newLongitude;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public Node(final StoreReader sr, final StoreClassRegister scr) {
		super(sr, scr);

		this.latitude = FixedPrecisionCoordinateConvertor.convertToDouble(sr.readInteger());
		this.longitude = FixedPrecisionCoordinateConvertor.convertToDouble(sr.readInteger());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		super.store(sw, scr);

		sw.writeInteger(FixedPrecisionCoordinateConvertor.convertToFixed(latitude));
		sw.writeInteger(FixedPrecisionCoordinateConvertor.convertToFixed(longitude));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getType() {
		return EntityType.Node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Node) {
			return compareTo((Node) o) == 0;
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
	 * Compares this node to the specified node. The node comparison is based on a comparison of id, version, latitude,
	 * longitude, timestamp and tags in that order.
	 *
	 * @param comparisonNode
	 *            The node to compare to.
	 * @return 0 if equal, &lt; 0 if considered "smaller", and &gt; 0 if considered "bigger".
	 */
	@Override
	public int compareTo(final Node comparisonNode) {
		if (this.getId() < comparisonNode.getId()) { return -1; }

		if (this.getId() > comparisonNode.getId()) { return 1; }

		if (this.getVersion() < comparisonNode.getVersion()) { return -1; }

		if (this.getVersion() > comparisonNode.getVersion()) { return 1; }

		if (this.latitude < comparisonNode.latitude) { return -1; }

		if (this.latitude > comparisonNode.latitude) { return 1; }

		if (this.longitude < comparisonNode.longitude) { return -1; }

		if (this.longitude > comparisonNode.longitude) { return 1; }

		if (this.getTimestamp() == null && comparisonNode.getTimestamp() != null) { return -1; }
		if (this.getTimestamp() != null && comparisonNode.getTimestamp() == null) { return 1; }
		if (this.getTimestamp() != null && comparisonNode.getTimestamp() != null) {
			int result;

			result = this.getTimestamp().compareTo(comparisonNode.getTimestamp());

			if (result != 0) { return result; }
		}

		return compareTags(comparisonNode.getTags());
	}

	/**
	 * Gets the latitude.
	 *
	 * @return The latitude.
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude
	 *            The latitude.
	 */
	public void setLatitude(final double latitude) {
		assertWriteable();

		this.latitude = latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return The longitude.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude
	 *            The longitude.
	 */
	public void setLongitude(final double longitude) {
		assertWriteable();

		this.longitude = longitude;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getWriteableInstance() {
		if (isReadOnly()) {
			return new Node(this);
		} else {
			return this;
		}
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
		if (name != null) { return "Node(id=" + getId() + ", #tags=" + getTags().size() + ", name='" + name + "')"; }
		return "Node(id=" + getId() + ", #tags=" + getTags().size() + ")";
	}
}
