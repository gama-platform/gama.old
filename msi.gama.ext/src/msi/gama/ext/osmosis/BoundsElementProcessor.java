// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import org.xml.sax.Attributes;

/**
 * Provides an element processor implementation for a node.
 *
 * @author Karl Newman
 * @author Igor Podolskiy
 */
public class BoundsElementProcessor extends SourceElementProcessor {

	private Bound bound;
	private final String defaultOrigin;

	/**
	 * Creates a new instance.
	 *
	 * @param parentProcessor
	 *            The parent of this element processor.
	 * @param sink
	 *            The sink for receiving processed data.
	 * @param enableDateParsing
	 *            If true, dates will be parsed from xml data, else the current date will be used thus saving parsing
	 *            time.
	 * @param defaultOrigin
	 *            The origin to set on the bound entity if there is no explicit origin defined.
	 */
	public BoundsElementProcessor(final BaseElementProcessor parentProcessor, final Sink sink,
			final boolean enableDateParsing, final String defaultOrigin) {
		super(parentProcessor, sink, enableDateParsing);
		this.defaultOrigin = defaultOrigin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void begin(final Attributes attributes) {
		final double bottom = getRequiredDoubleValue(attributes, XmlConstants.ATTRIBUTE_NAME_MINLAT);
		final double left = getRequiredDoubleValue(attributes, XmlConstants.ATTRIBUTE_NAME_MINLON);
		final double top = getRequiredDoubleValue(attributes, XmlConstants.ATTRIBUTE_NAME_MAXLAT);
		final double right = getRequiredDoubleValue(attributes, XmlConstants.ATTRIBUTE_NAME_MAXLON);

		String origin = attributes.getValue(XmlConstants.ATTRIBUTE_NAME_ORIGIN);
		if (origin == null) {
			origin = defaultOrigin;
		}
		bound = new Bound(right, left, top, bottom, origin);
	}

	private double getRequiredDoubleValue(final Attributes attributes, final String attributeName) {
		final String valueString = attributes.getValue(attributeName);

		if (valueString == null) {
			throw new OsmosisRuntimeException(
					String.format("Required attribute %s of the bounds element is missing", attributeName));
		}
		try {
			return Double.parseDouble(valueString);
		} catch (final NumberFormatException e) {
			throw new OsmosisRuntimeException(
					String.format("Cannot parse the %s attribute of the bounds element", attributeName), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void end() {
		getSink().process(new BoundContainer(bound));
		bound = null;
	}

}
