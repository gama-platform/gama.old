// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a collection of tags.
 *
 * @author Brett Henderson
 */
public class TagCollectionImpl extends ArrayList<Tag> implements TagCollection {

	/**
	 * Creates a new instance.
	 */
	public TagCollectionImpl() {
		super();
	}

	/**
	 * Creates a new instance.
	 *
	 * @param tags
	 *            The initial tags.
	 */
	public TagCollectionImpl(final Collection<? extends Tag> tags) {
		super(tags);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public TagCollectionImpl(final StoreReader sr, final StoreClassRegister scr) {
		super();
		int tagCount;
		tagCount = sr.readCharacter();
		for (int i = 0; i < tagCount; i++) {
			add(new Tag(sr, scr));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		sw.writeCharacter(IntAsChar.intToChar(size()));
		for (final Tag tag : this) {
			tag.store(sw, scr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> buildMap() {
		Map<String, String> tagMap;

		tagMap = new HashMap<>(size());
		for (final Tag tag : this) {
			tagMap.put(tag.getKey(), tag.getValue());
		}

		return tagMap;
	}

	@Override
	public TagCollection toReadOnly() {
		return new TagCollectionImpl(java.util.Collections.unmodifiableCollection(this));
	}
}
