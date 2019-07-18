// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.Map;

/**
 * Defines the interface for tasks consuming OSM data types.
 *
 * @author Brett Henderson
 */
public interface Sink {

	/**
	 * Process the entity.
	 *
	 * @param entityContainer
	 *            The entity to be processed.
	 */
	void process(EntityContainer entityContainer);

	void initialize(Map<String, Object> metaData);

	/**
	 * Ensures that all information is fully persisted. This includes database commits, file buffer flushes, etc.
	 * Implementations must call complete on any nested Completable objects. Where the releasable method of a Releasable
	 * class should be called within a finally block, this method should typically be the final statement within the try
	 * block.
	 */
	void complete();
}
