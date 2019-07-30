// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

/**
 * Entity container implementation for bound.
 *
 * @author knewman
 */
public class BoundContainer extends EntityContainer {

	private final Bound bound;

	/**
	 * Creates a new instance.
	 *
	 * @param bound
	 *            The bound to wrap.
	 */
	public BoundContainer(final Bound bound) {
		this.bound = bound;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		bound.store(sw, scr);
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
	public Bound getEntity() {
		return bound;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoundContainer getWriteableInstance() {
		if (bound.isReadOnly()) {
			return new BoundContainer(bound.getWriteableInstance());
		} else {
			return this;
		}
	}
}
