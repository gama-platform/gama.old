/*********************************************************************************************
 *
 * 'TypeNode.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package msi.gama.util.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GamaTreeNode<T> {

	private T data;
	private List<GamaTreeNode<T>> children;
	private GamaTreeNode<T> parent;

	public GamaTreeNode(final T data) {
		setData(data);
	}

	public GamaTreeNode<T> getParent() {
		return parent;
	}

	public List<GamaTreeNode<T>> getChildren() {
		if (children == null)
			return Collections.EMPTY_LIST;
		return this.children;
	}

	public int getNumberOfChildren() {
		return getChildren().size();
	}

	public boolean hasChildren() {
		return getNumberOfChildren() > 0;
	}

	public void addChild(final GamaTreeNode<T> child) {
		child.parent = this;
		if (children == null)
			children = new ArrayList<>();
		children.add(child);
	}

	public GamaTreeNode<T> addChild(final T child) {
		final GamaTreeNode<T> result = new GamaTreeNode<>(child);
		addChild(result);
		return result;
	}

	// public void addChildAt(final int index, final TypeNode<T> child) throws
	// IndexOutOfBoundsException {
	// child.parent = this;
	// children.add(index, child);
	// }

	public void removeChildAt(final int index) throws IndexOutOfBoundsException {
		if (children == null)
			return;
		children.remove(index);
	}

	public GamaTreeNode<T> getChildAt(final int index) throws IndexOutOfBoundsException {
		if (children == null)
			return null;
		return children.get(index);
	}

	public T getData() {
		return this.data;
	}

	public void setData(final T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return getData().toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GamaTreeNode<?> other = (GamaTreeNode<?>) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (data == null ? 0 : data.hashCode());
		return result;
	}

	public String toStringVerbose() {
		String stringRepresentation = getData().toString() + ":[";

		for (final GamaTreeNode<T> node : getChildren()) {
			stringRepresentation += node.getData().toString() + ", ";
		}

		// Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's
		// retarded.
		final Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(stringRepresentation);

		stringRepresentation = matcher.replaceFirst("");
		stringRepresentation += "]";

		return stringRepresentation;
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
		final GamaTreeNode<T> result = new GamaTreeNode<>(getData());
		if (children != null)
			for (final GamaTreeNode<T> node : children) {
				result.addChild(node.copy());
			}
		return result;
	}
}
