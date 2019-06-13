// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.util.file.osm;

import java.util.Collections;
import java.util.Map;

/**
 * Wraps a tag collection and prevents modifications from being made to it.
 *
 * @author Brett Henderson
 */
public class UnmodifiableTagCollection extends CollectionWrapper<Tag> implements TagCollection {

	private final TagCollection wrappedTags;

	/**
	 * Creates a new instance.
	 *
	 * @param wrappedTags
	 *            The tags to wrap.
	 */
	public UnmodifiableTagCollection(final TagCollection wrappedTags) {
		super(Collections.unmodifiableCollection(wrappedTags));

		this.wrappedTags = wrappedTags;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		wrappedTags.store(sw, scr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> buildMap() {
		return wrappedTags.buildMap();
	}
}
