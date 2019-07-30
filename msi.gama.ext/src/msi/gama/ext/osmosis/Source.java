// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

/**
 * Defines the interface for tasks producing OSM data types.
 *
 * @author Brett Henderson
 */
public interface Source {

	/**
	 * Sets the osm sink to send data to.
	 *
	 * @param sink
	 *            The sink for receiving all produced data.
	 */
	void setSink(Sink sink);
}
