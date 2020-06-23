/*******************************************************************************************************
 *
 * msi.gama.util.tree.GamaNode.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.util.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamaNode<T> {

	static Integer DEFAULT_WEIGHT = null;
	private T data;
	private Integer weight;
	private List<GamaNode<T>> children;
	private GamaNode<T> parent;

	public GamaNode(final T data) {
		this(data, DEFAULT_WEIGHT);
	}

	public GamaNode(final T data, final Integer weight) {
		this.weight = weight;
		setData(data);
	}

	public GamaNode<T> getParent() {
		return parent;
	}

	public List<GamaNode<T>> getChildren() {
		if (children == null) { return Collections.EMPTY_LIST; }
		return children;
	}

	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}

	public GamaNode<T> addChild(final GamaNode<T> child) {
		child.parent = this;
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
		return child;
	}

	public GamaNode<T> addChild(final T child) {
		return addChild(child, DEFAULT_WEIGHT);
	}

	public GamaNode<T> addChild(final T child, final Integer w) {
		final GamaNode<T> result = new GamaNode<>(child, w);
		return addChild(result);
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

	public void dispose() {
		parent = null;
		if (children != null) {
			for (final GamaNode<T> node : children) {
				node.dispose();
			}
			children.clear();
		}
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(final Integer w) {
		weight = w;
	}

	public void attachTo(final GamaNode<T> node) {
		if (parent != null) {
			parent.removeChild(this);
		}
		node.addChild(this);

	}

	private void removeChild(final GamaNode<T> gamaTreeNode) {
		children.remove(gamaTreeNode);
	}
}
