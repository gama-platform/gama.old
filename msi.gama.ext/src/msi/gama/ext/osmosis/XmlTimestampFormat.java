// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.Date;

/**
 * A timestamp format implementation for dates read and stored from osm xml files.
 *
 * @author Brett Henderson
 */
public class XmlTimestampFormat extends TimestampFormat {

	private final ThreadLocal<DateFormatter> dateFormatterStore;
	private final ThreadLocal<DateParser> dateParserStore;

	/**
	 * Creates a new instance.
	 */
	public XmlTimestampFormat() {
		dateFormatterStore = new ThreadLocal<>();
		dateParserStore = new ThreadLocal<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String formatTimestamp(final Date timestamp) {
		DateFormatter dateFormatter;

		dateFormatter = dateFormatterStore.get();
		if (dateFormatter == null) {
			dateFormatter = new DateFormatter();
			dateFormatterStore.set(dateFormatter);
		}

		return dateFormatter.format(timestamp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date parseTimestamp(final String timestamp) {
		DateParser dateParser;

		dateParser = dateParserStore.get();
		if (dateParser == null) {
			dateParser = new DateParser();
			dateParserStore.set(dateParser);
		}

		return dateParser.parse(timestamp);
	}
}
