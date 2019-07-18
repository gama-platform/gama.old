// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import org.xml.sax.Attributes;

/**
 * Provides an element processor implementation for a way node.
 *
 * @author Brett Henderson
 */
public class WayNodeElementProcessor extends BaseElementProcessor {
	private static final String ATTRIBUTE_NAME_ID = "ref";

	private final WayNodeListener wayNodeListener;
	private WayNode wayNode;

	/**
	 * Creates a new instance.
	 *
	 * @param parentProcessor
	 *            The parent element processor.
	 * @param wayNodeListener
	 *            The way node listener for receiving created tags.
	 */
	public WayNodeElementProcessor(final BaseElementProcessor parentProcessor, final WayNodeListener wayNodeListener) {
		super(parentProcessor, true);

		this.wayNodeListener = wayNodeListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void begin(final Attributes attributes) {
		long id;

		id = Long.parseLong(attributes.getValue(ATTRIBUTE_NAME_ID));

		wayNode = new WayNode(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void end() {
		wayNodeListener.processWayNode(wayNode);
		wayNode = null;
	}
}
