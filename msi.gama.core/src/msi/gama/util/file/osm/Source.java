// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.util.file.osm;

/**
 * Defines the interface for tasks producing OSM data types.
 *
 * @author Brett Henderson
 */
public interface Source extends Task {

	/**
	 * Sets the osm sink to send data to.
	 *
	 * @param sink
	 *            The sink for receiving all produced data.
	 */
	void setSink(Sink sink);
}
