// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

/**
 * Entity container implementation for ways.
 *
 * @author Brett Henderson
 */
public class WayContainer extends EntityContainer {

	private final Way way;

	/**
	 * Creates a new instance.
	 *
	 * @param way
	 *            The way to wrap.
	 */
	public WayContainer(final Way way) {
		this.way = way;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public WayContainer(final StoreReader sr, final StoreClassRegister scr) {
		way = new Way(sr, scr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		way.store(sw, scr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(final EntityProcessor processor) {
		processor.process(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Way getEntity() {
		return way;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WayContainer getWriteableInstance() {
		if (way.isReadOnly()) {
			return new WayContainer(way.getWriteableInstance());
		} else {
			return this;
		}
	}
}
