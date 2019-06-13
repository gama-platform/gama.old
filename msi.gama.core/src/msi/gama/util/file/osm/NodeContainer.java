// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.util.file.osm;

/**
 * Entity container implementation for nodes.
 *
 * @author Brett Henderson
 */
public class NodeContainer extends EntityContainer {

	private final Node node;

	/**
	 * Creates a new instance.
	 *
	 * @param node
	 *            The node to wrap.
	 */
	public NodeContainer(final Node node) {
		this.node = node;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param sr
	 *            The store to read state from.
	 * @param scr
	 *            Maintains the mapping between classes and their identifiers within the store.
	 */
	public NodeContainer(final StoreReader sr, final StoreClassRegister scr) {
		node = new Node(sr, scr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(final StoreWriter sw, final StoreClassRegister scr) {
		node.store(sw, scr);
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
	public Node getEntity() {
		return node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeContainer getWriteableInstance() {
		if (node.isReadOnly()) {
			return new NodeContainer(node.getWriteableInstance());
		} else {
			return this;
		}
	}
}
