// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

/**
 * A data class representing a reference to an OSM node.
 *
 * @author Brett Henderson
 */
public class WayNode implements Comparable<WayNode>, Storeable {

	/**
	 * When sending way nodes with location information included through the Osmosis pipeline, a metadata key with this
	 * value should be set to true so that downstream tasks know that they are able to use it.
	 */
	public static final String METADATA_KEY_LOCATION_INCLUDED = "way_node.location_included";

	private final long nodeId;
	private double latitude;
	private double longitude;

	/**
	 * Creates a new instance.
	 *
	 * @param nodeId
	 *            The unique identifier of the node being referred to.
	 */
	public WayNode(final long nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param nodeId
	 *            The unique identifier of the node being referred to.
	 * @param latitude
	 *            The node's latitude
	 * @param longitude
	 *            The node's longitude
	 */
	public WayNode(final long nodeId, final double latitude, final double longitude) {
		this(nodeId);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public WayNode(final StoreReader sr, final StoreClassRegister scr) {
		this(sr.readLong(), sr.readDouble(), sr.readDouble());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		sw.writeLong(nodeId);
		sw.writeDouble(latitude);
		sw.writeDouble(longitude);
	}

	/**
	 * Compares this way node to the specified way node. The way node comparison is based on a comparison of nodeId.
	 *
	 * @param wayNode
	 *            The way node to compare to.
	 * @return 0 if equal, &lt; 0 if considered "smaller", and &gt; 0 if considered "bigger".
	 */
	@Override
	public int compareTo(final WayNode wayNode) {
		long result;

		result = this.nodeId - wayNode.nodeId;

		if (result > 0) {
			return 1;
		} else if (result < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * @return The latitude (if the PBF file was built with osmium command 'add-locations-to-ways')
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return The longitude (if the PBF file was built with osmium command 'add-locations-to-ways')
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return The nodeId.
	 */
	public long getNodeId() {
		return nodeId;
	}

	/**
	 * ${@inheritDoc}.
	 */
	@Override
	public String toString() {
		return "WayNode(nodeID=" + getNodeId() + ")";
	}
}
