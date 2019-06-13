// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.util.file.osm;

/**
 * Defines the interface for tasks consuming OSM data types.
 *
 * @author Brett Henderson
 */
public interface Sink extends Task, Initializable {

	/**
	 * Process the entity.
	 *
	 * @param entityContainer
	 *            The entity to be processed.
	 */
	void process(EntityContainer entityContainer);
}
