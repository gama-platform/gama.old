/*********************************************************************************************
 *
 * 'TypeNode.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package msi.gama.util.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamaTreeNode<T> {

	static Double DEFAULT_WEIGHT = null;
	private T data;
	private final Double weight;
	private List<GamaTreeNode<T>> children;
	private GamaTreeNode<T> parent;

	public GamaTreeNode(final T data) {
		this(data, DEFAULT_WEIGHT);
	}

	public GamaTreeNode(final T data, final Double weight) {
		this.weight = weight;
		setData(data);
	}

	public GamaTreeNode<T> getParent() {
		return parent;
	}

	public List<GamaTreeNode<T>> getChildren() {
		if (children == null) { return Collections.EMPTY_LIST; }
		return children;
	}

	public int getNumberOfChildren() {
		return children.size();
	}

	public boolean hasChildren() {
		return getNumberOfChildren() > 0;
	}
	//
	// public void addChild(final GamaTreeNode<T> child) {
	// addChild(child, DEFAULT_WEIGHT);
	// }

	private void addChild(final GamaTreeNode<T> child) {
		child.parent = this;
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
	}

	public GamaTreeNode<T> addChild(final T child) {
		final GamaTreeNode<T> result = addChild(child, DEFAULT_WEIGHT);
		return result;
	}

	public GamaTreeNode<T> addChild(final T child, final Double weight) {
		final GamaTreeNode<T> result = new GamaTreeNode<>(child, weight);
		addChild(result);
		return result;
	}

	public T getData() {
		return this.data;
	}

	public void setData(final T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final GamaTreeNode<?> other = (GamaTreeNode<?>) obj;
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

	public void toString(final StringBuilder sb) {
		sb.append(getData());
		if (children != null) {
			sb.append('(');
			for (final GamaTreeNode<T> node : children) {
				node.toString(sb);
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
			sb.append(')');
		}
		if (weight != null) {
			sb.append("::").append(getWeight());
		}

	}

	public void dispose() {
		parent = null;
		if (children != null) {
			for (final GamaTreeNode<T> node : children) {
				node.dispose();
			}
			children.clear();
		}
	}

	public GamaTreeNode<T> copy() {
		final GamaTreeNode<T> result = new GamaTreeNode<>(getData(), weight);
		if (children != null) {
			for (final GamaTreeNode<T> node : children) {
				result.addChild(node.copy());
			}
		}
		return result;
	}

	public Double getWeight() {
		return weight;
	}
}
