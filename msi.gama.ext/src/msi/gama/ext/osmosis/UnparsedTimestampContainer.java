// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.Date;

/**
 * A timestamp container implementation that holds a timestamp in textual form.
 *
 * @author Brett Henderson
 */
public class UnparsedTimestampContainer implements TimestampContainer {

	private final TimestampFormat managedTimestampFormat;
	private final String timestampString;
	private Date timestamp;

	/**
	 * Creates a new instance.
	 *
	 * @param timestampFormat
	 *            The format to use for parsing the timestamp string.
	 * @param timestampString
	 *            The timestamp in unparsed string form.
	 */
	public UnparsedTimestampContainer(final TimestampFormat timestampFormat, final String timestampString) {
		this.managedTimestampFormat = timestampFormat;
		this.timestampString = timestampString;

		if (timestampString == null) {
			throw new OsmosisRuntimeException("The entity timestamp attribute is missing.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormattedTimestamp(final TimestampFormat timestampFormat) {
		if (timestampString != null && managedTimestampFormat.isEquivalent(timestampFormat)) { return timestampString; }

		// Ensure the timestamp has been parsed.
		getTimestamp();

		if (timestamp != null) {
			return timestampFormat.formatTimestamp(timestamp);
		} else {
			return "";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getTimestamp() {
		if (timestamp == null && timestampString != null && timestampString.length() > 0) {
			timestamp = managedTimestampFormat.parseTimestamp(timestampString);
		}

		return timestamp;
	}
}
