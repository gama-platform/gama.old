// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

/**
 * Entity container implementation for relations.
 *
 * @author Brett Henderson
 */
public class RelationContainer extends EntityContainer {

	private final Relation relation;

	/**
	 * Creates a new instance.
	 *
	 * @param relation
	 *            The relation to wrap.
	 */
	public RelationContainer(final Relation relation) {
		this.relation = relation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		relation.store(sw, scr);
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
	public Relation getEntity() {
		return relation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RelationContainer getWriteableInstance() {
		if (relation.isReadOnly()) {
			return new RelationContainer(relation.getWriteableInstance());
		} else {
			return this;
		}
	}
}
