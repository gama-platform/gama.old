// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

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
