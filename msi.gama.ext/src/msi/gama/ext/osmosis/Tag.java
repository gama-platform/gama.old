// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

/**
 * A data class representing a single OSM tag.
 *
 * @author Brett Henderson
 */
public class Tag implements Comparable<Tag>, Storeable {

	/**
	 * The key identifying the tag.
	 */
	private final String key;
	/**
	 * The value associated with the tag.
	 */
	private final String value;

	/**
	 * Creates a new instance.
	 *
	 * @param key
	 *            The key identifying the tag.
	 * @param value
	 *            The value associated with the tag.
	 */
	public Tag(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public Tag(final StoreReader sr, final StoreClassRegister scr) {
		this(sr.readString(), sr.readString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		sw.writeString(key);
		sw.writeString(value);
	}

	/**
	 * Compares this tag to the specified tag. The tag comparison is based on a comparison of key and value in that
	 * order.
	 *
	 * @param tag
	 *            The tag to compare to.
	 * @return 0 if equal, &lt; 0 if considered "smaller", and &gt; 0 if considered "bigger".
	 */
	@Override
	public int compareTo(final Tag tag) {
		int keyResult;

		keyResult = this.key.compareTo(tag.key);

		if (keyResult != 0) { return keyResult; }

		return this.value.compareTo(tag.value);
	}

	/**
	 * @return The key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return The value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * ${@inheritDoc}.
	 */
	@Override
	public String toString() {
		return "Tag('" + getKey() + "'='" + getValue() + "')";
	}

}
