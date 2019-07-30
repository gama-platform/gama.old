// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.Date;

/**
 * A timestamp container implementation that holds a standard date object.
 *
 * @author Brett Henderson
 */
public class SimpleTimestampContainer implements TimestampContainer {

	private final Date timestamp;

	/**
	 * Creates a new instance.
	 *
	 * @param timestamp
	 *            The timestamp to be managed.
	 */
	public SimpleTimestampContainer(final Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormattedTimestamp(final TimestampFormat timestampFormat) {
		return timestampFormat.formatTimestamp(timestamp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getTimestamp() {
		return timestamp;
	}
}
