// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

/**
 * Provides the definition of a class receiving way nodes.
 *
 * @author Brett Henderson
 */
public interface WayNodeListener {
	/**
	 * Processes the way node.
	 *
	 * @param wayNode
	 *            The way node.
	 */
	void processWayNode(WayNode wayNode);
}
