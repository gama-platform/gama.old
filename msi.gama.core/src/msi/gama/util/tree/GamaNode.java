/*******************************************************************************************************
 *
 * GamaNode.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.util.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class GamaNode.
 *
 * @param <T> the generic type
 */
public class GamaNode<T> {

	/** The default weight. */
	static Integer DEFAULT_WEIGHT = null;
	
	/** The data. */
	private T data;
	
	/** The weight. */
	private Integer weight;
	
	/** The children. */
	private List<GamaNode<T>> children;
	
	/** The parent. */
	private GamaNode<T> parent;

	/**
	 * Instantiates a new gama node.
	 *
	 * @param data the data
	 */
	public GamaNode(final T data) {
		this(data, DEFAULT_WEIGHT);
	}

	/**
	 * Instantiates a new gama node.
	 *
	 * @param data the data
	 * @param weight the weight
	 */
	public GamaNode(final T data, final Integer weight) {
		this.weight = weight;
		setData(data);
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public GamaNode<T> getParent() {
		return parent;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<GamaNode<T>> getChildren() {
		if (children == null) { return Collections.EMPTY_LIST; }
		return children;
	}

	/**
	 * Checks for children.
	 *
	 * @return true, if successful
	 */
	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}

	/**
	 * Adds the child.
	 *
	 * @param child the child
	 * @return the gama node
	 */
	public GamaNode<T> addChild(final GamaNode<T> child) {
		child.parent = this;
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
		return child;
	}

	/**
	 * Adds the child.
	 *
	 * @param child the child
	 * @return the gama node
	 */
	public GamaNode<T> addChild(final T child) {
		return addChild(child, DEFAULT_WEIGHT);
	}

	/**
	 * Adds the child.
	 *
	 * @param child the child
	 * @param w the w
	 * @return the gama node
	 */
	public GamaNode<T> addChild(final T child, final Integer w) {
		final GamaNode<T> result = new GamaNode<>(child, w);
		return addChild(result);
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public T getData() {
		return this.data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(final T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb, 0);
		return sb.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final GamaNode<?> other = (GamaNode<?>) obj;
		if (data == null) {
			if (other.data != null) { return false; }
		} else if (!data.equals(other.data)) { return false; }
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * To string.
	 *
	 * @param sb the sb
	 * @param level the level
	 */
	private void toString(final StringBuilder sb, final int level) {
		// sb.append(Strings.LN);
		// for (int i = 0; i < level; i++) {
		// sb.append(Strings.TAB);
		// }
		sb.append(getData());
		if (children != null) {
			sb.append("([");
			for (final GamaNode<T> node : children) {
				node.toString(sb, level + 1);
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
			// sb.append(Strings.LN);
			// for (int i = 0; i < level; i++) {
			// sb.append(Strings.TAB);
			// }
			sb.append("])");
		}
		if (weight != null) {
			sb.append("::").append(getWeight());
		}

	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		parent = null;
		if (children != null) {
			for (final GamaNode<T> node : children) {
				node.dispose();
			}
			children.clear();
		}
	}

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public Integer getWeight() {
		return weight;
	}

	/**
	 * Sets the weight.
	 *
	 * @param w the new weight
	 */
	public void setWeight(final Integer w) {
		weight = w;
	}

	/**
	 * Attach to.
	 *
	 * @param node the node
	 */
	public void attachTo(final GamaNode<T> node) {
		if (parent != null) {
			parent.removeChild(this);
		}
		node.addChild(this);

	}

	/**
	 * Removes the child.
	 *
	 * @param gamaTreeNode the gama tree node
	 */
	private void removeChild(final GamaNode<T> gamaTreeNode) {
		children.remove(gamaTreeNode);
	}
}
